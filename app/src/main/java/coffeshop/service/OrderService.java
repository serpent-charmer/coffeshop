package coffeshop.service;

import coffeshop.order.Order;
import coffeshop.order.event.OrderEvent;
import coffeshop.order.exceptions.OrderIllegalStateException;

public interface OrderService {
    void publishEvent(OrderEvent event);
    Order findOrder(int id) throws OrderIllegalStateException;
}
