package coffeshop.order;

import coffeshop.EventType;
import coffeshop.service.EventResult;

import java.util.ArrayList;

public class Order {
    EventType current;
    ArrayList<EventResult> events;

    public Order(EventType current, ArrayList<EventResult> events) {
        this.current = current;
        this.events = events;
    }
}
