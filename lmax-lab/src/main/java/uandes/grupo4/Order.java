package uandes.grupo4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

// Clase que representa una orden
@RegisterForReflection
public class Order {
    
    @JsonCreator
    public Order(@JsonProperty("id") String id, 
                 @JsonProperty("asset") String asset, 
                 @JsonProperty("quantity") int quantity, 
                 @JsonProperty("type") String type) {
                    this.id = id;
                    this.asset = asset;
                    this.quantity = quantity;
                    this.type = type;
                }

    @JsonProperty("id")
    public String id;

    @JsonProperty("asset")
    public String asset;

    @JsonProperty("quantity")
    public int quantity;

    @JsonProperty("type")
    public String type;

    // 🔹 Constructor vacío requerido por Jackson
    public Order() {}
}