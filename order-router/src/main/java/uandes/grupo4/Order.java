package uandes.grupo4;
// Clase que representa una orden
public class Order {
    
    public Order(String id, String type, String asset, int quantity) {
        this.id = id;
        this.type = type;
        this.asset = asset;
        this.quantity = quantity;
    }

    String id;
    String type; // "buy" o "sell"
    String asset;
    int quantity;
}
