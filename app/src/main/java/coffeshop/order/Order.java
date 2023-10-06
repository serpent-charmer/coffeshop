package coffeshop.order;

import coffeshop.order.event.EventType;
import coffeshop.service.EventResult;

import java.util.ArrayList;

public class Order {

    EventType status;
    ArrayList<EventResult> events;

    public Order(EventType status, ArrayList<EventResult> events) {
        this.status = status;
        this.events = events;
    }

    public EventType getStatus() {
        return status;
    }

    public ArrayList<EventResult> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "Order{" +
                "status=" + status +
                ", events=" + events +
                '}';
    }
}
