public class Order {
    private int orderId;
    private String customerName;
    private String orderDetails;
    private boolean isImmediate;
    private long timestamp;

    public Order(int orderId, String customerName, String orderDetails, boolean isImmediate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDetails = orderDetails;
        this.isImmediate = isImmediate;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getOrderDetails() { return orderDetails; }
    public boolean isImmediate() { return isImmediate; }
    public long getTimestamp() { return timestamp; }

    public void setImmediate(boolean immediate) { isImmediate = immediate; }

    @Override
    public String toString() {
        return String.format("Order #%d - %s: %s %s",
                orderId, customerName, orderDetails,
                isImmediate ? "(IMMEDIATE)" : "");
    }
}