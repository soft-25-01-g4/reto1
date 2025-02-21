package uandes.grupo4;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@Path("/orders")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderRouter {

    private final Map<String, String> assetToEndpoint = Map.of(
        "BTC", "http://localhost:8081/orders",
        "ETH", "http://localhost:8081/orders",
        "USDT", "http://localhost:8081/orders"
    );

    @Inject
    @RestClient
    OrderServiceClient orderServiceClient;

    @POST
    public Response routeOrder(Order order) {
        String endpoint = assetToEndpoint.get(order.asset);

        if (endpoint == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Asset no soportado: " + order.asset)
                           .build();
        }

        // Enviar la orden al motor de emparejamiento correspondiente
        orderServiceClient.forwardOrder(endpoint, order);
        return Response.ok("Orden enviada al motor correspondiente").build();
    }
}
