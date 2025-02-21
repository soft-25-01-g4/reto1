package uandes.grupo4;
import com.lmax.disruptor.*;
import java.util.*;


class OrderEventHandler implements EventHandler<OrderEvent> {
    private final Queue<Order> buyOrders = new LinkedList<>();
    private final Queue<Order> sellOrders = new LinkedList<>();

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        Order order = event.get();
        
        if (order.type.equals("buy")) {
            matchOrder(order, sellOrders);
            if (order.quantity > 0) buyOrders.add(order); // Si queda pendiente, se almacena
        } else {
            matchOrder(order, buyOrders);
            if (order.quantity > 0) sellOrders.add(order);
        }
    }

    private void matchOrder(Order incomingOrder, Queue<Order> oppositeQueue) {
        Iterator<Order> iterator = oppositeQueue.iterator();
        while (iterator.hasNext() && incomingOrder.quantity > 0) {
            Order queuedOrder = iterator.next();
            int matchedQuantity = Math.min(incomingOrder.quantity, queuedOrder.quantity);
            
            System.out.println("Match! " + incomingOrder.id + " ↔ " + queuedOrder.id + " - Cantidad: " + matchedQuantity);
            
            incomingOrder.quantity -= matchedQuantity;
            queuedOrder.quantity -= matchedQuantity;
            
            if (queuedOrder.quantity == 0) iterator.remove(); // Elimina órdenes completamente procesadas
        }
    }
}