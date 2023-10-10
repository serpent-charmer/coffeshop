package coffeshop.service;

import coffeshop.DbUtil;
import coffeshop.order.Order;
import coffeshop.order.event.EventType;
import coffeshop.order.event.OrderEvent;
import coffeshop.order.exceptions.OrderIllegalStateException;

import java.sql.*;
import java.util.ArrayList;

public class OrderServiceImpl implements OrderService {

    @Override
    public void publishEvent(OrderEvent event) {
        try {
            event.publish();
        } catch (OrderIllegalStateException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order findOrder(int id) throws OrderIllegalStateException {
        String sql = "select event_id, event_type, employee_id, event_date from order_base " +
                "join event on order_base.event_id = event.id " +
                "where order_base.id = ? order by event_date";
        ArrayList<EventResult> events = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                EventType eventType = EventType.get(rs.getString("event_type"));
                Timestamp eventDate = rs.getTimestamp("event_date");
                events.add(new EventResult(eventType, eventDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(events.size() > 0) {
            return new Order(events.get(events.size() - 1).eventType, events);
        }
        throw new OrderIllegalStateException("Try registering an order first");
    }
}
