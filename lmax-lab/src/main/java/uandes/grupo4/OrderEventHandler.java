package uandes.grupo4;
import com.lmax.disruptor.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class OrderEventHandler implements EventHandler<OrderEvent> {
    private final Queue<Order> buyOrders = new LinkedList<>();
    private final Queue<Order> sellOrders = new LinkedList<>();

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        Order order = event.get();
        
        if (order.type.equals("buy")) {
            matchOrder(order, sellOrders, true);
            if (order.quantity > 0) buyOrders.add(order); // Si queda pendiente, se almacena
        } else {
            matchOrder(order, buyOrders, false);
            if (order.quantity > 0) sellOrders.add(order);
        }
    }

    private void matchOrder(Order incomingOrder, Queue<Order> oppositeQueue, boolean isBuyOrder) {
        Iterator<Order> iterator = oppositeQueue.iterator();
        while (iterator.hasNext() && incomingOrder.quantity > 0) {
            Order queuedOrder = iterator.next();
            int matchedQuantity = Math.min(incomingOrder.quantity, queuedOrder.quantity);
            
            System.out.println("Match! " + incomingOrder.id + " ↔ " + queuedOrder.id + " - Cantidad: " + matchedQuantity);

            if(isBuyOrder)
            {
                long timestamp = System.currentTimeMillis();
                System.out.println("Timestamp: " + timestamp);
                saveMatch(incomingOrder.id, queuedOrder.id, queuedOrder.asset, matchedQuantity, timestamp);
                //redis.saveOrderFinish(incomingOrder.id, String.valueOf(timestamp));
            }

            incomingOrder.quantity -= matchedQuantity;
            queuedOrder.quantity -= matchedQuantity;
            
            if (queuedOrder.quantity == 0) iterator.remove(); // Elimina órdenes completamente procesadas
        }
    }


    public void saveMatch(String orderId, String matchId, String asset, int quantity, long timestamp) {
        String logEntry = orderId + "," + matchId + "," + asset + "," + quantity + "," + timestamp + "\n";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/data/matches.log", true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}