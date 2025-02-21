package uandes.grupo4;
import com.lmax.disruptor.*;


// Manejador de eventos para procesar órdenes
public class OrderEventHandler implements EventHandler<OrderEvent> {
    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) {
        Order order = event.get();
        // Lógica de emparejamiento FIFO (se puede mejorar con estructuras más eficientes)
        System.out.println("Procesando orden: " + order.id + " - Tipo: " + order.type);
    }
}

