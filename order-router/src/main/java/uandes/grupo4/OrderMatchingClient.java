package uandes.grupo4;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.util.HashMap;
import java.util.Map;

@Path("/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderRouter {

    // Simula una tabla en memoria que asocia activos con URLs de procesamiento
    private static final Map<String, String> assetRoutes = new HashMap<>();

    static {
        assetRoutes.put("USD", "http://localhost:8081/match");
        assetRoutes.put("BTC", "http://localhost:8082/match");
    }

    @RestClient
    OrderMatchingClient orderMatchingClient;

    @POST
    public Response routeOrder(Order order) {
        String endpoint = assetRoutes.get(order.asset);

        if (endpoint == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Asset type not supported"))
                    .build();
        }

        // Enviar orden al servicio de emparejamiento correspondiente
        orderMatchingClient.sendOrder(order, endpoint);
        return Response.ok(Map.of("message", "Order forwarded to " + endpoint)).build();
    }
}

@RegisterRestClient(baseUri = "")
public interface OrderMatchingClient {
    @POST
    @Path("/match")
    @Consumes(MediaType.APPLICATION_JSON)
    void sendOrder(Order order, @HeaderParam("Endpoint") String endpoint);
}
