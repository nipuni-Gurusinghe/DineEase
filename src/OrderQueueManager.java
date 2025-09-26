import javax.swing.*;
import java.sql.*;
import java.util.*;

public class OrderQueueManager {
    private LinkedList<Order> orderQueue;
    private int nextOrderId;

    public OrderQueueManager() {
        this.orderQueue = new LinkedList<>();
        this.nextOrderId = getNextOrderIdFromDB();
        loadOrdersFromDatabase();
    }

    private int getNextOrderIdFromDB() {
        String sql = "SELECT COALESCE(MAX(order_id), 0) + 1 FROM orders";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting next order ID: " + e.getMessage());
        }
        return 1;
    }

    private void loadOrdersFromDatabase() {
        String sql = "SELECT * FROM orders WHERE status = 'PENDING' ORDER BY is_immediate DESC, timestamp ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("order_details"),
                        rs.getBoolean("is_immediate"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("status")
                );
                orderQueue.add(order);
            }
            System.out.println("Loaded " + orderQueue.size() + " orders from database.");
        } catch (SQLException e) {
            System.err.println("Error loading orders from database: " + e.getMessage());
        }
    }

    public void addOrder(String customerName, String orderDetails) {
        String sql = "INSERT INTO orders (customer_name, order_details, is_immediate, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customerName);
            pstmt.setString(2, orderDetails);
            pstmt.setBoolean(3, false);
            pstmt.setString(4, "PENDING");

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        Order order = new Order(orderId, customerName, orderDetails, false);
                        orderQueue.addLast(order);
                        nextOrderId = Math.max(nextOrderId, orderId + 1);
                        System.out.println("Added: " + order);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding order to database: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saving order to database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addImmediateOrder(String customerName, String orderDetails) {
        String sql = "INSERT INTO orders (customer_name, order_details, is_immediate, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customerName);
            pstmt.setString(2, orderDetails);
            pstmt.setBoolean(3, true);
            pstmt.setString(4, "PENDING");

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        Order order = new Order(orderId, customerName, orderDetails, true);

                        // Insert in correct position in queue
                        int insertPosition = 0;
                        for (Order o : orderQueue) {
                            if (o.isImmediate()) {
                                insertPosition++;
                            } else {
                                break;
                            }
                        }
                        orderQueue.add(insertPosition, order);
                        nextOrderId = Math.max(nextOrderId, orderId + 1);
                        System.out.println("Added Immediate: " + order);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding immediate order to database: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error saving immediate order to database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Order processNextOrder() {
        if (orderQueue.isEmpty()) {
            return null;
        }

        Order order = orderQueue.removeFirst();
        markOrderAsProcessed(order.getOrderId());
        return order;
    }

    private void markOrderAsProcessed(int orderId) {
        String sql = "UPDATE orders SET status = 'PROCESSED' WHERE order_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
            System.out.println("Marked order #" + orderId + " as PROCESSED");
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orderQueue);
    }

    public int getQueueSize() {
        return orderQueue.size();
    }

    public boolean removeOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ? AND status = 'PENDING'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                boolean removed = orderQueue.removeIf(order -> order.getOrderId() == orderId);
                if (removed) {
                    System.out.println("Removed order #" + orderId + " from database and queue");
                }
                return removed;
            }
        } catch (SQLException e) {
            System.err.println("Error removing order from database: " + e.getMessage());
        }
        return false;
    }

    public void clearAllOrders() {
        String sql = "DELETE FROM orders WHERE status = 'PENDING'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.executeUpdate();
            orderQueue.clear();
            nextOrderId = getNextOrderIdFromDB();
            System.out.println("Cleared all pending orders from database and queue");
        } catch (SQLException e) {
            System.err.println("Error clearing orders from database: " + e.getMessage());
        }
    }

    // Method to get order history
    public List<Order> getOrderHistory() {
        List<Order> history = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = 'PROCESSED' ORDER BY timestamp DESC LIMIT 50";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("order_details"),
                        rs.getBoolean("is_immediate"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("status")
                );
                history.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error loading order history: " + e.getMessage());
        }
        return history;
    }
}