package kitchenpos.order.order.sample;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

import java.util.Collections;
import kitchenpos.order.order.domain.Order;

public class OrderSample {

    public static Order 조리중인_후라이트치킨세트_두개_주문() {
        Order order = spy(Order.of(1L,
            Collections.singletonList(OrderLineItemSample.이십원_후라이트치킨_두마리세트_한개_주문_항목())));

        lenient().when(order.id())
            .thenReturn(1L);
        return order;
    }
}
