package uandes.grupo4;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.AppsV1Api.APIlistNamespacedDeploymentRequest;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.custom.IntOrString;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/distribute")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DistribuidorActivos {

    private final Map<String, String> assetToUrlMap = new HashMap<>();
    private final AppsV1Api appsApi;
    private final CoreV1Api coreApi;
    private final String namespace = "juank1400-dev";

    public DistribuidorActivos() throws Exception {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        this.appsApi = new AppsV1Api(client);
        this.coreApi = new CoreV1Api(client);
    }

    @POST
    public Response distributeAsset(AssetRequest request) {
        String asset = request.asset();
        String type = request.type();
        if(type.equals("sell")) {
            if (assetToUrlMap.containsKey(asset)) {
                return Response.ok(Map.of("url", assetToUrlMap.get(asset))).build();
            }
    
            // No existe, generar un nuevo LMAX
            String serviceUrl = generarLMAX(asset);
            if (serviceUrl != null) {
                assetToUrlMap.put(asset, serviceUrl);
                return Response.ok(Map.of("url", serviceUrl)).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Map.of("error", "Failed to deploy LMAX instance"))
                        .build();
            }
        } else {
            return Response.ok("Deploy don't required").build();
        }
    }

    private String generarLMAX(String asset) {
        try {
            String deploymentName = "lmax-" + asset.toLowerCase().replace(" ", "-").replace("_", "-");
            String image = "quay.io/jcepedav/lmax:latest";
            String sidecarImage = "busybox"; // Sidecar para logs
    
            // Volumen compartido entre contenedores
            V1Volume sharedVolume = new V1Volume()
                .name("shared-data")
                .emptyDir(new V1EmptyDirVolumeSource());
    
            // Contenedor principal
            V1Container lmaxContainer = new V1Container()
                .name("lmax-container")
                .image(image)
                .ports(java.util.List.of(new V1ContainerPort().containerPort(8080)))
                .volumeMounts(java.util.List.of(new V1VolumeMount().name("shared-data").mountPath("/data")));
    
            // Sidecar para monitoreo de logs
            V1Container sidecarContainer = new V1Container()
                .name("log-sidecar")
                .image(sidecarImage)
                .command(java.util.List.of("sh", "-c", "tail -F /data/matches.log"))
                .volumeMounts(java.util.List.of(new V1VolumeMount().name("shared-data").mountPath("/data")));
    
            // Labels para OpenShift
            Map<String, String> labels = Map.of(
                "app", deploymentName,
                "app.kubernetes.io/name", deploymentName,
                "app.kubernetes.io/part-of", "lmax-group"
            );
    
            // Crear Deployment con etiquetas para OpenShift
            V1Deployment deployment = new V1Deployment()
                .metadata(new V1ObjectMeta().name(deploymentName).namespace(namespace).labels(labels))
                .spec(new V1DeploymentSpec()
                    .replicas(1)
                    .selector(new V1LabelSelector().matchLabels(Map.of("app", deploymentName)))
                    .template(new V1PodTemplateSpec()
                        .metadata(new V1ObjectMeta().labels(labels))
                        .spec(new V1PodSpec()
                            .volumes(java.util.List.of(sharedVolume)) // Agregar volumen compartido
                            .containers(java.util.List.of(lmaxContainer, sidecarContainer)) // Agregar contenedores
                        )
                    )
                );
    
            appsApi.createNamespacedDeployment(namespace, deployment).execute();
    
            // Crear Service con las mismas etiquetas
            V1Service service = new V1Service()
                .metadata(new V1ObjectMeta().name(deploymentName).namespace(namespace).labels(labels))
                .spec(new V1ServiceSpec()
                    .selector(Map.of("app", deploymentName))
                    .ports(java.util.List.of(new V1ServicePort().port(8080).targetPort(new IntOrString(8080))))
                    .type("ClusterIP")
                );
    
            coreApi.createNamespacedService(namespace, service).execute();
    
            // Construir la URL del servicio
            return "http://" + deploymentName + "." + namespace + ".svc.cluster.local:8080";
    
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
        
    public void ListDeployments() {
        try {
            V1DeploymentList deployments = appsApi.listNamespacedDeployment(namespace).execute();

            // Imprimir información de los Deployments
            for (V1Deployment deployment : deployments.getItems()) {
                System.out.println("Nombre: " + deployment.getMetadata().getName());
                System.out.println("Réplicas deseadas: " + deployment.getSpec().getReplicas());
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    public record AssetRequest(String id, String type, String asset, int quantity) {}
}
