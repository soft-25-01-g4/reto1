package uandes.grupo4;
// Clase que representa una orden
public class Order {
    public Order(String pid, String ptype, String passet, int pquantity) {
        id = pid;
        type = ptype.toString();
        asset = passet;
        quantity = pquantity;

    }
    String id;
    String type; // "buy" o "sell"
    String asset;
    int quantity;
}