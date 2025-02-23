package uandes.grupo4;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {
    @JsonProperty("id")
    public String id;

    @JsonProperty("type")
    public String type;

    @JsonProperty("asset")
    public String asset;

    @JsonProperty("quantity")
    public int quantity;

    // Constructor vacío requerido por Jackson
    public Order() {}

    public Order(String id, String type, String asset, int quantity) {
        this.id = id;
        this.type = type;
        this.asset = asset;
        this.quantity = quantity;
    }
 
}
