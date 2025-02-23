package uandes.grupo4;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.util.HashMap;
import java.util.Map;
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderRouter {

    // Simula una tabla en memoria que asocia activos con URLs de procesamiento
    private static final Map<String, String> assetRoutes = new HashMap<>();

    static {
        assetRoutes.put("USD", "https://lmax-juank1400-dev.apps.rm3.7wse.p1.openshiftapps.com");
        assetRoutes.put("BTC", "http://localhost:8082/match");
    }

    OrderMatchingClient  orderMatchingClient = new OrderMatchingClient();

    @POST
    public Response routeOrder(Order order) {
        String endpoint = assetRoutes.get(order.asset);

        if (endpoint == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Asset type not supported"))
                    .build();
        }

        // Enviar orden al servicio de emparejamiento correspondiente
        orderMatchingClient.sendOrder(endpoint, order);
        return Response.ok(Map.of("message", "Order forwarded to " + endpoint)).build();
    }
}
