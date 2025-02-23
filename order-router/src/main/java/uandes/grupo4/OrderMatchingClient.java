package uandes.grupo4;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import java.net.URI;

@ApplicationScoped
public class OrderMatchingClient {

    public void sendOrder(String baseUrl, Order order) {
        try {
            OrderServiceClient service = RestClientBuilder.newBuilder()
                    .baseUri(new URI(baseUrl))
                    .build(OrderServiceClient.class);

            service.sendOrder(order);
        } catch (Exception e) {
            throw new RuntimeException("Error calling Order Matching Service", e);
        }
    }
}