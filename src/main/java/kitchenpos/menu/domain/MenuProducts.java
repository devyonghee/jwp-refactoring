package kitchenpos.menu.domain;

import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import org.springframework.util.Assert;

@Embeddable
public class MenuProducts {

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "menu")
    private List<MenuProduct> products;

    protected MenuProducts() {
    }

    private MenuProducts(List<MenuProduct> products) {
        Assert.notNull(products, "메뉴 상품 리스트는 필수입니다.");
        Assert.noNullElements(products,
            () -> String.format("메뉴 상품 리스트(%s)에 null이 포함될 수 없습니다.", products));
        this.products = products;
    }

    public static MenuProducts from(List<MenuProduct> products) {
        return new MenuProducts(products);
    }

    public static MenuProducts singleton(MenuProduct product) {
        return new MenuProducts(Collections.singletonList(product));
    }

    public List<MenuProduct> list() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public String toString() {
        return "MenuProducts{" +
            "products=" + products +
            '}';
    }
}
