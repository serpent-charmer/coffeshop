package coffeshop;

import coffeshop.order.Order;
import coffeshop.order.event.EventType;
import coffeshop.order.event.OrderEvent;
import coffeshop.order.event.OrderEventBuilder;
import coffeshop.order.exceptions.OrderIllegalStateException;
import coffeshop.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private OrderServiceImpl ORDER_SERVICE = new OrderServiceImpl();
    private int ORDER_ID_1 = 22;
    private int ORDER_ID_2 = 23;

    @BeforeEach
    void setDbUrl() throws SQLException {
        DbUtil.DB_URL = "jdbc:sqlite:coffeshop_test.db";
        try(Connection conn = DbUtil.getConnection()) {
            conn.createStatement().executeUpdate("DROP TABLE ORDER_BASE;");
            conn.createStatement().executeUpdate("DROP TABLE EVENT;");
            conn.createStatement().executeUpdate("DROP TABLE EVENT_REGISTERED;");
            conn.createStatement().executeUpdate("DROP TABLE EVENT_CANCELED;");
        }
        DbUtil.prepare();
    }

    @Test
    void orderingTwiceFails() {
        OrderEvent register = OrderEventBuilder.get(ORDER_ID_1,
                        1,
                        Timestamp.valueOf(LocalDateTime.now()))
                .register(2,
                        Timestamp.valueOf(LocalDateTime.now().plusHours(8)),
                        22, 15.99);
        Exception exc = assertThrows(OrderIllegalStateException.class, () -> {
            register.publish();
            register.publish();
        });
        assertEquals("Order already registered", exc.getMessage());
    }

    @Test
    void publishAfterCancelFails() {
        OrderEvent register = OrderEventBuilder.get(ORDER_ID_1,
                        1,
                        Timestamp.valueOf(LocalDateTime.now()))
                .register(2,
                        Timestamp.valueOf(LocalDateTime.now().plusHours(8)),
                        22, 15.99);
        OrderEvent cancel = OrderEventBuilder.get(ORDER_ID_1,
                        1,
                        Timestamp.valueOf(LocalDateTime.now()))
                .cancel("No money");
        Exception exc = assertThrows(OrderIllegalStateException.class, () -> {
            register.publish();
            cancel.publish();
            cancel.publish();
        });
        assertEquals("Order already canceled or out", exc.getMessage());
    }

    @Test
    void publishBeforeRegisterFails() {
        OrderEvent cancel = OrderEventBuilder.get(ORDER_ID_1,
                        1,
                        Timestamp.valueOf(LocalDateTime.now()))
                .cancel("No money");
        Exception exc = assertThrows(OrderIllegalStateException.class, cancel::publish);
        assertEquals("No register event previously recorded", exc.getMessage());
    }

    @Test
    void getInfo() {
        OrderEvent register = OrderEventBuilder.get(ORDER_ID_2,
                        0,
                        Timestamp.valueOf(LocalDateTime.now()))
                .register(0, Timestamp.valueOf(LocalDateTime.now().plusHours(8)),
                        0, 0);
        OrderEvent process = OrderEventBuilder.get(ORDER_ID_2,
                        0,
                        Timestamp.valueOf(LocalDateTime.now()))
                .process();
        OrderEvent done = OrderEventBuilder.get(ORDER_ID_2,
                        0,
                        Timestamp.valueOf(LocalDateTime.now()))
                .done();
        OrderEvent out = OrderEventBuilder.get(ORDER_ID_2,
                        0,
                        Timestamp.valueOf(LocalDateTime.now()))
                .out();
        ORDER_SERVICE.publishEvent(register);
        ORDER_SERVICE.publishEvent(process);
        Order order = ORDER_SERVICE.findOrder(ORDER_ID_2);
        assertEquals(EventType.Processing, order.getStatus());
        ORDER_SERVICE.publishEvent(done);
        ORDER_SERVICE.publishEvent(out);
        order = ORDER_SERVICE.findOrder(ORDER_ID_2);
        assertEquals(EventType.Out, order.getStatus());
    }

}
