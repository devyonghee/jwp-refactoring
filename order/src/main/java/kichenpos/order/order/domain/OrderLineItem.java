package kichenpos.order.order.domain;

import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import kichenpos.common.domain.Quantity;
import org.springframework.util.Assert;

@Entity
@Table(indexes = @Index(name = "order_line_item_menu_id_index", columnList = "menuId"))
public class OrderLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;

    @Embedded
    private Quantity quantity;

    @Embedded
    private OrderLineItemMenu menu;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false,
        foreignKey = @ForeignKey(name = "fk_order_line_item_orders"))
    private Order order;

    protected OrderLineItem() {
    }

    private OrderLineItem(Quantity quantity, OrderLineItemMenu menu) {
        Assert.notNull(quantity, "수량은 필수입니다.");
        Assert.notNull(menu, "메뉴는 필수입니다.");
        this.quantity = quantity;
        this.menu = menu;
    }

    public static OrderLineItem of(Quantity quantity, OrderLineItemMenu menu) {
        return new OrderLineItem(quantity, menu);
    }

    public long seq() {
        return seq;
    }

    public OrderLineItemMenu menu() {
        return menu;
    }

    public Quantity quantity() {
        return quantity;
    }

    void changeOrder(Order order) {
        Assert.notNull(order, "변경하려는 주문은 필수입니다.");
        this.order = order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seq);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderLineItem that = (OrderLineItem) o;
        return seq == that.seq;
    }

    @Override
    public String toString() {
        return "OrderLineItem{" +
            "seq=" + seq +
            ", quantity=" + quantity +
            ", menu=" + menu +
            '}';
    }
}
