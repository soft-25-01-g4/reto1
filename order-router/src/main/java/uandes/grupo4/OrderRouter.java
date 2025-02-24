package uandes.grupo4;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Path("/orders")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderRouter {

    private final Map<String, String> assetToEndpoint = new HashMap<>(Map.of());

    @Inject
    @RestClient
    OrderServiceClient orderServiceClient;

    @Inject
    RedisService redisService;
    @POST
    public Response routeOrder(Order order) {
        String endpoint = assetToEndpoint.get(order.getAsset());

        long timestamp = System.currentTimeMillis();

        redisService.saveOrderTiming(order.getId(), String.valueOf(timestamp));

        if (endpoint == null) {
            return Response.status(Response.Status.OK)
                    .entity("Este activo no tiene ofertas disponibles: " + order.getAsset())
                    .build();
        }

        // Enviar la orden al motor de emparejamiento correspondiente
        OrderServiceClient dynamicClient = RestClientBuilder.newBuilder()
                .baseUri(URI.create(endpoint))
                .build(OrderServiceClient.class);

        Response externalResponse = dynamicClient.forwardOrder(order);

        return Response.status(externalResponse.getStatus())
        .entity(externalResponse.readEntity(String.class))
        .build();

        /*
        ------------ ESTE EN CASO DE QUE QUIERAS VER EL RESPONSE DE LOS ENDPOINTS---------------

        Response externalResponse = dynamicClient.forwardOrder(order);

        return Response.status(externalResponse.getStatus())
                .entity(externalResponse.readEntity(String.class))
                .build();

                return Response.ok("Orden enviada al motor correspondiente").build();

         */
    }

    @POST
    @Path("/add-endpoint")
    public Response addEndpoint(@QueryParam("asset") String asset, @QueryParam("endpoint") String endpoint) {
        if (asset == null || endpoint == null || asset.isBlank() || endpoint.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Asset and endpoint must not be empty")
                    .build();
        }

        assetToEndpoint.put(asset, endpoint);
        return Response.ok("Endpoint added successfully for asset: " + asset).build();
    }

    /*
    ESTE LO AGREGO PARA QUE PUEDAS VER LOS ENDPOINTS QUE SE VAN AGREGANDO
     */
    @GET
    @Path("/endpoints")
    public Response getAllEndpoints() {
        return Response.ok(assetToEndpoint).build();
    }
}
