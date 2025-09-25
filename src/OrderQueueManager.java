
import java.util.*;

public class OrderQueueManager {
    private LinkedList<Order> orderQueue;
    private int nextOrderId;

    public OrderQueueManager() {
        this.orderQueue = new LinkedList<>();
        this.nextOrderId = 1;
    }


    public void addOrder(String customerName, String orderDetails) {
        Order order = new Order(nextOrderId++, customerName, orderDetails, false);
        orderQueue.addLast(order);
        System.out.println("Added: " + order);
    }


    public void addImmediateOrder(String customerName, String orderDetails) {
        Order order = new Order(nextOrderId++, customerName, orderDetails, true);


        int insertPosition = 0;
        for (Order o : orderQueue) {
            if (o.isImmediate()) {
                insertPosition++;
            } else {
                break;
            }
        }

        orderQueue.add(insertPosition, order);
        System.out.println("Added Immediate: " + order);
    }
    public Order processNextOrder() {
        if (orderQueue.isEmpty()) {
            return null;
        }
        return orderQueue.removeFirst();
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orderQueue);
    }

    public int getQueueSize() {
        return orderQueue.size();
    }

    public boolean removeOrder(int orderId) {
        return orderQueue.removeIf(order -> order.getOrderId() == orderId);
    }

    public void clearAllOrders() {
        orderQueue.clear();
        nextOrderId = 1;
    }
}