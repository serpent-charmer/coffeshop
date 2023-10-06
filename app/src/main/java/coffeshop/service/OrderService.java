package coffeshop.service;

import coffeshop.order.Order;
import coffeshop.order.event.OrderEvent;

public interface OrderService {
    void publishEvent(OrderEvent event);
    Order findOrder(int id);
}
