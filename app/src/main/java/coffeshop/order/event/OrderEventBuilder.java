package coffeshop.order.event;

import coffeshop.EventType;

import java.sql.Timestamp;

public class OrderEventBuilder {

    protected OrderEvent event;

    private OrderEventBuilder(OrderEvent event) {
        this.event = event;
    }
    public static OrderEventBuilder get(int orderId, int employeeId, Timestamp eventDate) {
        return new OrderEventBuilder(new OrderEvent(orderId, employeeId, eventDate));
    }

    public OrderEvent register(int clientId, Timestamp deliveryDate, int merchId, double merchPrice) {
        event = new OrderRegistered(event, clientId, deliveryDate, merchId, merchPrice);
        event.eventType = EventType.Registered;
        return event;
    }

    public OrderEvent process() {
        event.eventType = EventType.Processing;
        return event;
    }

    public OrderEvent done() {
        event.eventType = EventType.Done;
        return event;
    }

    public OrderEvent cancel(String reason) {
        event = new OrderCanceled(event, reason);
        event.eventType = EventType.Canceled;
        return event;
    }


    public OrderEvent out() {
        event.eventType = EventType.Out;
        return event;
    }


}
