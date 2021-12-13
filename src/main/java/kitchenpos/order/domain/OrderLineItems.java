package kitchenpos.order.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import org.springframework.util.Assert;

@Embeddable
class OrderLineItems {

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST})
    private List<OrderLineItem> items;

    protected OrderLineItems() {
    }

    private OrderLineItems(List<OrderLineItem> items) {
        Assert.notNull(items, "주문 항목 리스트는 필수입니다.");
        Assert.noNullElements(items,
            () -> String.format("주문 항목 리스트(%s)에 null이 포함될 수 없습니다.", items));
        this.items = items;
    }

    static OrderLineItems from(List<OrderLineItem> items) {
        return new OrderLineItems(items);
    }

    List<OrderLineItem> list() {
        return Collections.unmodifiableList(items);
    }

    boolean isNotEmpty() {
        return !items.isEmpty();
    }

    void changeOrder(Order order) {
        Assert.notNull(order, "변경하려는 주문은 필수입니다.");
        items.forEach(item -> item.changeOrder(order));
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderLineItems that = (OrderLineItems) o;
        return Objects.equals(items, that.items);
    }

    @Override
    public String toString() {
        return "OrderLineItems{" +
            "items=" + items +
            '}';
    }
}
