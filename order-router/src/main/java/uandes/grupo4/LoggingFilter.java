package uandes.grupo4;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class LoggingFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        System.out.println("📤 Enviando request a: " + requestContext.getUri());
        System.out.println("📝 Método: " + requestContext.getMethod());
        System.out.println("🔤 Headers: " + requestContext.getHeaders());

        if (requestContext.hasEntity()) {
            System.out.println("📦 Cuerpo: " + requestContext.getEntity());
        }
    }
}
