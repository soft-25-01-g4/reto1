package uandes.grupo4;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class MatchingEngine {
    public static void main(String[] args) {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        EventFactory<OrderEvent> eventFactory = OrderEvent::new;
        int bufferSize = 1024;

        // Crear Disruptor con nuevo constructor
        Disruptor<OrderEvent> disruptor = new Disruptor<>(eventFactory, bufferSize, threadFactory,
                ProducerType.SINGLE, new SleepingWaitStrategy());

        // Configurar el manejador de eventos
        disruptor.handleEventsWith(new OrderEventHandler());
        disruptor.start();

        // Publicar eventos (órdenes de prueba)
        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
        for (int i = 0; i < 10; i++) {
            long sequence = ringBuffer.next();
            try {
                OrderEvent event = ringBuffer.get(sequence);
                event.set(new Order("order-" + i, i % 2 == 0 ? "buy" : "sell", "USD", 100));
            } finally {
                ringBuffer.publish(sequence);
            }
        }
    }
}