package uandes.grupo4;
// Evento para Disruptor
public class OrderEvent {
    private Order order;
    public void set(Order order) { this.order = order; }
    public Order get() { return order; }
}
