package uandes.grupo4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// Clase que representa una orden
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
    String id;
    String type; // "buy" o "sell"
    String asset;
    int quantity;
}