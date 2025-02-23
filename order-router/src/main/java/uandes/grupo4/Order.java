package uandes.grupo4;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL) // Evita problemas con valores nulos
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora propiedades desconocidas
public class Order {
    private String id;
    private String type;
    private String asset;
    private int quantity;

    public Order() { } // Constructor vacío requerido por Jackson

    public Order(@JsonProperty("id") String id,
                 @JsonProperty("type") String type,
                 @JsonProperty("asset") String asset,
                 @JsonProperty("quantity") int quantity) {
        this.id = id;
        this.type = type;
        this.asset = asset;
        this.quantity = quantity;
    }

     @Override
    public String toString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize Order\"}";
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getAsset() {
        return asset;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
