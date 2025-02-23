package uandes.grupo4;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/distribute")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class DistribuidorActivos {

    private final Map<String, String> assetUrlMap = new ConcurrentHashMap<>();

    private final KubernetesClient kubernetesClient;
    ;

    public DistribuidorActivos() {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            System.out.println("Connected to Kubernetes: " + client.getNamespace());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    public Uni<Response> distributeAsset(AssetRequest request) {
        if (assetUrlMap.containsKey(request.asset)) {
            return Uni.createFrom().item(Response.ok(assetUrlMap.get(request.asset)).build());
        }

        return Uni.createFrom().item(() -> {
            String serviceUrl = generateLMAX(request.asset);
            assetUrlMap.put(request.asset, serviceUrl);
            return Response.ok(serviceUrl).build();
        });
    }

    private String generateLMAX(String asset) {
        String podName = "lmax-" + asset.toLowerCase();
        Pod pod = new PodBuilder()
                .withNewMetadata()
                .withName(podName)
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName("lmax")
                .withImage("quay.io/jcepedav/lmax:latest")
                .addNewPort().withContainerPort(8080).endPort()
                .endContainer()
                .endSpec()
                .build();
        
        kubernetesClient.pods().inNamespace("default").createOrReplace(pod);
        
        return "http://" + podName + ".default.svc.cluster.local:8080";
    }

    public static class AssetRequest {
        public String id;
        public String type;
        public String asset;
        public int quantity;
    }
}
