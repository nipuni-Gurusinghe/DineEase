
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


}