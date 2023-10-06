package coffeshop.order.event;

import coffeshop.DbUtil;
import coffeshop.EventType;
import coffeshop.order.exceptions.OrderIllegalStateException;

import java.sql.*;

public class OrderEvent implements OrderCheck {
    final int orderId;
    final int employeeId;
    final Timestamp eventDate;
    EventType eventType;

    public OrderEvent(int orderId, int employeeId, Timestamp eventDate) {
        this.orderId = orderId;
        this.employeeId = employeeId;
        this.eventDate = eventDate;
    }

    public OrderEvent(OrderEvent event) {
        this(event.orderId, event.employeeId, event.eventDate);
    }

    public int publish() throws OrderIllegalStateException, SQLException {

        checkRegister();
        checkCanceledOrOut();

        int event_id = -1;
        try (Connection conn = DbUtil.getConnection()) {
            String sql = "INSERT INTO event " +
                    "(event_type, employee_id, event_date) " +
                    "VALUES(?, ?, ?) RETURNING event.id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, eventType.getType());
            stmt.setInt(2, employeeId);
            stmt.setTimestamp(3, eventDate);
            ResultSet rs = stmt.executeQuery();
            event_id = rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        }

        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO order_base VALUES(?, ?)");
            stmt.setInt(1, orderId);
            stmt.setInt(2, event_id);
            stmt.execute();
        } catch (SQLException e) {
            throw e;
        }

        return event_id;

    }

    public boolean check(String sql) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void checkRegister() throws OrderIllegalStateException {
        if(!check("select event_id from order_base " +
                "join event on order_base.event_id = event.id " +
                "where event_type = 'R' and order_base.id = ?")) {
            throw new OrderIllegalStateException("No register event previously recorded");
        }
    }

    @Override
    public void checkCanceledOrOut() throws OrderIllegalStateException {
        if(check("select event_id from order_base join " +
                "event on order_base.event_id = event.id " +
                "where (event_type = 'C' or event_type = 'O') " +
                "and order_base.id = ?")) {
            throw new OrderIllegalStateException("Order already canceled or out");
        }
    }
}
