package uandes.grupo4;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class RedisService {

    private final HashCommands<String, String, String> hashCommands;

    @Inject
    public RedisService(RedisDataSource redisDataSource) {
        this.hashCommands = redisDataSource.hash(String.class, String.class, String.class);
    }

    public void saveOrderTiming(String orderId, String horaInicio) {
        hashCommands.hset(orderId, Map.of("horaInicio", horaInicio));
    }


}