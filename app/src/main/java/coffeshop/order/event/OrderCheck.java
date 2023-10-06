package coffeshop.order.event;

import coffeshop.order.exceptions.OrderIllegalStateException;

public interface OrderCheck {
    public void checkRegister() throws OrderIllegalStateException;
    public void checkCanceledOrOut() throws OrderIllegalStateException;
}
