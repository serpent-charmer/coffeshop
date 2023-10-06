package coffeshop.order.event;

import coffeshop.DbUtil;
import coffeshop.order.exceptions.OrderIllegalStateException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderCanceled extends OrderEvent {
    final String reason;

    protected OrderCanceled(OrderEvent event, String reason) {
        super(event);
        this.reason = reason;
    }

    @Override
    public int publish() throws OrderIllegalStateException, SQLException {
        int eventId = super.publish();
        try (Connection conn = DbUtil.getConnection()) {
            String sql = "INSERT INTO event_canceled " +
                    "(event_id, reason) " +
                    "VALUES(?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, eventId);
            stmt.setString(2, reason);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
        return eventId;
    }
}
