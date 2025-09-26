import java.sql.Timestamp;

public class Order {
    private int orderId;
    private String customerName;
    private String orderDetails;
    private boolean isImmediate;
    private Timestamp timestamp;
    private String status;

    public Order(int orderId, String customerName, String orderDetails, boolean isImmediate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDetails = orderDetails;
        this.isImmediate = isImmediate;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.status = "PENDING";
    }

    // Database constructor
    public Order(int orderId, String customerName, String orderDetails, boolean isImmediate, Timestamp timestamp, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDetails = orderDetails;
        this.isImmediate = isImmediate;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getOrderDetails() { return orderDetails; }
    public boolean isImmediate() { return isImmediate; }
    public Timestamp getTimestamp() { return timestamp; }
    public String getStatus() { return status; }

    public void setImmediate(boolean immediate) { isImmediate = immediate; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Order #%d - %s: %s %s",
                orderId, customerName, orderDetails,
                isImmediate ? "(IMMEDIATE)" : "");
    }
}