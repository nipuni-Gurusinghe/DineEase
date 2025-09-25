// RestaurantQueueApp.java
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
        queueManager = new OrderQueueManager();
        initializeUI();
        setTitle("Restaurant Queue Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        // Main panel with border layout
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center panel with orders table
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Control panel with buttons
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("🍽️ Restaurant Queue Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
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

        // Table model
        String[] columns = {"Order ID", "Customer Name", "Order Details", "Priority", "Position"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        ordersTable.setRowHeight(25);
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Orders Queue"));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));

        // Buttons
        JButton addRegularBtn = createStyledButton("Add Regular Order", new Color(34, 139, 34));
        JButton addImmediateBtn = createStyledButton("Add Immediate Order", new Color(220, 20, 60));
        JButton processBtn = createStyledButton("Process Next Order", new Color(30, 144, 255));
        JButton refreshBtn = createStyledButton("Refresh Queue", new Color(255, 140, 0));
        JButton clearBtn = createStyledButton("Clear All", new Color(128, 128, 128));

        // Button actions
        addRegularBtn.addActionListener(e -> showAddOrderDialog(false));
        addImmediateBtn.addActionListener(e -> showAddOrderDialog(true));

        panel.add(addRegularBtn);
        panel.add(addImmediateBtn);
        panel.add(processBtn);
        panel.add(refreshBtn);
        panel.add(clearBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(new JLabel());
        dialog.add(buttonPanel);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            RestaurantQueueApp app = new RestaurantQueueApp();
            app.setVisible(true);
        });
    }
}