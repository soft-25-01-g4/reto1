package uandes.grupo4;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;

@ApplicationScoped
public class OrderMatchingClient {

    public void sendOrder(String baseUrl, Order order) {
        try {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonOrder = objectMapper.writeValueAsString(order);
                System.out.println("Orden serializada: " + jsonOrder);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            OrderServiceClient service = RestClientBuilder.newBuilder()
                    .baseUri(new URI(baseUrl))
//                    .register(LoggingFilter.class) // Agregar filtro de logs
                    .build(OrderServiceClient.class);

            service.sendOrder(order);
        } catch (ClientWebApplicationException e) {
            System.err.println("❌ Error en REST Client: " + e.getResponse().getStatus());
            System.err.println("📥 Respuesta: " + e.getResponse().readEntity(String.class));
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error calling Order Matching Service", e);
        }
    }
}