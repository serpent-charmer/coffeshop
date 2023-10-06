package coffeshop.service;

import coffeshop.order.event.EventType;

import java.sql.Timestamp;

public class EventResult {
    EventType eventType;
    Timestamp eventDate;

    public EventResult(EventType eventType, Timestamp eventDate) {
        this.eventType = eventType;
        this.eventDate = eventDate;
    }

    @Override
    public String toString() {
        return "EventResult{" +
                "eventType=" + eventType +
                ", eventDate=" + eventDate +
                '}';
    }
}
