package uandes.grupo4;
// Clase que representa una orden
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Order {
    private String id;
    private String type;
    private String asset;
    private int quantity;

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Sobrescribimos toString() para que devuelva JSON
    @Override
    public String toString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize Order\"}";
        }
    }
}
