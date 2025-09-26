import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RestaurantQueueApp extends JFrame {
    private OrderQueueManager queueManager;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JLabel queueSizeLabel;

    public RestaurantQueueApp() {
        // Initialize database first
        DatabaseUtil.initializeDatabase();

        queueManager = new OrderQueueManager();
        initializeUI();
        setTitle("Restaurant Queue Management System with Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        // Refresh display after UI is initialized
        refreshQueueDisplay();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("DineEase Restaurant Queue Management System (Database)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        queueSizeLabel = new JLabel("Orders in Queue: 0");
        queueSizeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        queueSizeLabel.setForeground(Color.YELLOW);

        panel.add(titleLabel);
        panel.add(Box.createHorizontalStrut(50));
        panel.add(queueSizeLabel);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Order ID", "Customer Name", "Order Details", "Priority", "Position", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ordersTable.setRowHeight(25);
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Orders Queue"));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));

        JButton addRegularBtn = createStyledButton("Add Regular Order", new Color(34, 139, 34));
        JButton addImmediateBtn = createStyledButton("Add Immediate Order", new Color(220, 20, 60));
        JButton processBtn = createStyledButton("Process Next Order", new Color(30, 144, 255));
        JButton refreshBtn = createStyledButton("Refresh Queue", new Color(255, 140, 0));
        JButton historyBtn = createStyledButton("View History", new Color(138, 43, 226));
        JButton clearBtn = createStyledButton("Clear All", new Color(128, 128, 128));

        addRegularBtn.addActionListener(e -> showAddOrderDialog(false));
        addImmediateBtn.addActionListener(e -> showAddOrderDialog(true));
        processBtn.addActionListener(e -> processNextOrder());
        refreshBtn.addActionListener(e -> refreshQueueDisplay());
        historyBtn.addActionListener(e -> showOrderHistory());
        clearBtn.addActionListener(e -> clearAllOrders());

        panel.add(addRegularBtn);
        panel.add(addImmediateBtn);
        panel.add(processBtn);
        panel.add(refreshBtn);
        panel.add(historyBtn);
        panel.add(clearBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void showAddOrderDialog(boolean immediate) {
        JDialog dialog = new JDialog(this, "Add " + (immediate ? "Immediate " : "") + "Order", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField();
        JTextField detailsField = new JTextField();

        dialog.add(new JLabel("Customer Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Order Details:"));
        dialog.add(detailsField);

        JButton addBtn = new JButton("Add Order");
        JButton cancelBtn = new JButton("Cancel");

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String details = detailsField.getText().trim();

            if (name.isEmpty() || details.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
                return;
            }

            if (immediate) {
                queueManager.addImmediateOrder(name, details);
            } else {
                queueManager.addOrder(name, details);
            }

            refreshQueueDisplay();
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(new JLabel());
        dialog.add(buttonPanel);

        dialog.setVisible(true);
    }

    private void processNextOrder() {
        Order order = queueManager.processNextOrder();
        if (order != null) {
            JOptionPane.showMessageDialog(this,
                    "Processing: " + order.toString(),
                    "Order Processed",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshQueueDisplay();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No orders in queue!",
                    "Queue Empty",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshQueueDisplay() {
        tableModel.setRowCount(0);

        java.util.List<Order> orders = queueManager.getAllOrders();

        int position = 1;
        for (Order order : orders) {
            tableModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getOrderDetails(),
                    order.isImmediate() ? "IMMEDIATE" : "Regular",
                    position++,
                    order.getTimestamp()
            });
        }

        queueSizeLabel.setText("Orders in Queue: " + queueManager.getQueueSize());
    }

    private void showOrderHistory() {
        java.util.List<Order> history = queueManager.getOrderHistory();

        String[] columns = {"Order ID", "Customer Name", "Order Details", "Priority", "Timestamp", "Status"};
        DefaultTableModel historyModel = new DefaultTableModel(columns, 0);

        for (Order order : history) {
            historyModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getOrderDetails(),
                    order.isImmediate() ? "IMMEDIATE" : "Regular",
                    order.getTimestamp(),
                    order.getStatus()
            });
        }

        JTable historyTable = new JTable(historyModel);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        historyTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Order History (Last 50 Processed Orders)", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearAllOrders() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all pending orders?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            queueManager.clearAllOrders();
            refreshQueueDisplay();
            JOptionPane.showMessageDialog(this, "All pending orders cleared!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            RestaurantQueueApp app = new RestaurantQueueApp();
            app.setVisible(true);
        });
    }
}