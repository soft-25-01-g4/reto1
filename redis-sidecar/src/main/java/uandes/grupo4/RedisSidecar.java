package uandes.grupo4;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import redis.clients.jedis.Jedis;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@QuarkusMain
public class RedisSidecar {
    public static void main(String... args) {
        Quarkus.run(SidecarService.class, args);
    }
}

class SidecarService implements QuarkusApplication {

    @ConfigProperty(name = "redis.host")
    String redisHost;

    @ConfigProperty(name = "redis.port")
    int redisPort;

    @ConfigProperty(name = "file.path")
    String filePath;

    @ConfigProperty(name = "redis.password")
    String redisPassword;

    private Jedis jedis;
    private WatchService watchService;

    @Override
    public int run(String... args) {
        startWatching();
        Quarkus.waitForExit();
        return 0;
    }

    private void startWatching() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(filePath).getParent();
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            new Thread(this::processFileChanges).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFileChanges() {
        try {
            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        processLogFile();
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processLogFile() {
        Map<String, String> hfinal = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                System.out.println("Line: " + line);
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String orderId = parts[0].trim();
                    String horaFinal = parts[4].trim();
                    hfinal = new HashMap<>();
                    hfinal.put("horaFinal", horaFinal);
                    jedis = new Jedis(redisHost, redisPort);
                    if (redisPassword != null && !redisPassword.isEmpty()) {
                        jedis.auth(redisPassword);
                    }
                    jedis.hset(orderId, hfinal);
                    jedis.close();
                    System.out.println(orderId + "-" + horaFinal);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    void cleanup() {
        try {
            if (watchService != null) {
                watchService.close();
            }
            if (jedis != null) {
                jedis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
