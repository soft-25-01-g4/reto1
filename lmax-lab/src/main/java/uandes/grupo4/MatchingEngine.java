package uandes.grupo4;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@QuarkusMain
public class MatchingEngine {
    public static void main(String[] args) {
        Quarkus.run(args);
    }
}

@Path("/orders")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OrderResource {
    private final Disruptor<OrderEvent> disruptor;
    private final RingBuffer<OrderEvent> ringBuffer;

    @Inject
    public OrderResource() {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        EventFactory<OrderEvent> eventFactory = OrderEvent::new;
        int bufferSize = 1024;

        disruptor = new Disruptor<>(eventFactory, bufferSize, threadFactory,
                ProducerType.SINGLE, new SleepingWaitStrategy());
        disruptor.handleEventsWith(new OrderEventHandler());
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
    }

    @POST
    public String submitOrder(Order order) {
        long sequence = ringBuffer.next();
        try {
            OrderEvent event = ringBuffer.get(sequence);
            event.set(order);
        } finally {
            ringBuffer.publish(sequence);
        }
        return "Orden recibida: " + order.id;
    }
}
