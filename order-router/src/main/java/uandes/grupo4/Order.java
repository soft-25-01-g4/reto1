package uandes.grupo4;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public Order(@JsonProperty("id") String id,
                 @JsonProperty("type") String type,
                 @JsonProperty("asset") String asset,
                 @JsonProperty("quantity") int quantity) {
        this.id = id;
        this.type = type;
        this.asset = asset;
        this.quantity = quantity;
    }
 
}
