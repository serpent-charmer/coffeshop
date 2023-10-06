package coffeshop.order.event;

import coffeshop.DbUtil;
import coffeshop.order.exceptions.OrderIllegalStateException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class OrderRegistered extends OrderEvent {
    final int clientId;
    final Timestamp deliveryDate;
    final int merchId;
    final double merchPrice;

    protected OrderRegistered(OrderEvent event, int clientId, Timestamp deliveryDate, int merchId, double merchPrice) {
        super(event);
        this.clientId = clientId;
        this.deliveryDate = deliveryDate;
        this.merchId = merchId;
        this.merchPrice = merchPrice;
    }

    @Override
    public int publish() throws OrderIllegalStateException, SQLException {
        int eventId = super.publish();
        try (Connection conn = DbUtil.getConnection()) {
            String sql = "INSERT INTO event_registered " +
                    "(event_id, client_id, delivery_date, " +
                    "merch_id, merch_price) " +
                    "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, eventId);
            stmt.setInt(2, clientId);
            stmt.setTimestamp(3, deliveryDate);
            stmt.setInt(4, merchId);
            stmt.setDouble(5, merchPrice);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
        return eventId;
    }

    @Override
    public void checkRegister() throws OrderIllegalStateException {
        boolean shouldException = false;
        try {
            super.checkRegister();
            shouldException = true;
        } catch (OrderIllegalStateException ignored) {
        }
        if (shouldException)
            throw new OrderIllegalStateException("Order already registered");
    }

}
