package kitchenpos.product.menu.application;

import static kitchenpos.product.group.sample.MenuGroupSample.두마리메뉴;
import static kitchenpos.product.menu.sample.MenuSample.이십원_후라이드치킨_두마리세트;
import static kitchenpos.product.product.sample.ProductSample.십원치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kichenpos.common.domain.Quantity;
import kitchenpos.product.group.domain.MenuGroup;
import kitchenpos.product.group.domain.MenuGroupQueryService;
import kitchenpos.product.menu.domain.Menu;
import kitchenpos.product.menu.domain.MenuCommandService;
import kitchenpos.product.menu.domain.MenuProduct;
import kitchenpos.product.menu.domain.MenuQueryService;
import kitchenpos.product.menu.ui.request.MenuProductRequest;
import kitchenpos.product.menu.ui.request.MenuRequest;
import kitchenpos.product.product.domain.Product;
import kitchenpos.product.product.domain.ProductQueryService;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴 서비스")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class MenuServiceTest {

    @Mock
    private MenuCommandService menuCommandService;
    @Mock
    private MenuQueryService menuQueryService;
    @Mock
    private MenuGroupQueryService menuGroupQueryService;
    @Mock
    private ProductQueryService productQueryService;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("메뉴를 등록할 수 있다.")
    void create() {
        //given
        MenuProductRequest menuProductRequest = new MenuProductRequest(1L, 1);
        MenuRequest menuRequest = new MenuRequest("이십원_후라이드치킨_두마리세트", BigDecimal.ONE, 1L,
            Collections.singletonList(menuProductRequest));

        MenuGroup 두마리메뉴 = 두마리메뉴();
        when(menuGroupQueryService.menuGroup(menuRequest.getMenuGroupId())).thenReturn(두마리메뉴);

        Product 십원치킨 = 십원치킨();
        when(productQueryService.product(menuProductRequest.getProductId()))
            .thenReturn(십원치킨);

        Menu 이십원_후라이드치킨_두마리세트 = 이십원_후라이드치킨_두마리세트();
        when(menuCommandService.save(any())).thenReturn(이십원_후라이드치킨_두마리세트);

        //when
        menuService.create(menuRequest);

        //then
        requestedMenuSave(menuRequest);
    }

    @Test
    @DisplayName("등록하려는 메뉴의 가격은 반드시 존재해야 한다.")
    void create_nullPrice_thrownException() {
        //given
        MenuProductRequest menuProductRequest = new MenuProductRequest(1L, 1);
        MenuRequest menuRequest = new MenuRequest("이십원_후라이드치킨_두마리세트", null, 1L,
            Collections.singletonList(menuProductRequest));

        //when
        ThrowingCallable createCallable = () -> menuService.create(menuRequest);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(createCallable);
    }

    @Test
    @DisplayName("등록하려는 메뉴의 가격은 0원 이상이어야 한다.")
    void create_priceLessThanZero_thrownException() {
        //given
        MenuProductRequest menuProductRequest = new MenuProductRequest(1L, 1);
        MenuRequest menuRequest = new MenuRequest("이십원_후라이드치킨_두마리세트", BigDecimal.valueOf(-1), 1L,
            Collections.singletonList(menuProductRequest));

        //when
        ThrowingCallable createCallable = () -> menuService.create(menuRequest);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(createCallable);
    }

    @Test
    @DisplayName("등록하려는 메뉴의 가격은 상품들의 수량 * 가격을 모두 합친 금액보다 작거나 같아야 한다.")
    void create_priceLessThanMenuProductPrice_thrownException() {
        //given
        MenuProductRequest menuProductRequest = new MenuProductRequest(1L, 1);
        MenuRequest menuRequest = new MenuRequest(
            "이십원_후라이드치킨_두마리세트",
            BigDecimal.valueOf(20),
            1L,
            Collections.singletonList(menuProductRequest));

        MenuGroup 두마리메뉴 = 두마리메뉴();
        when(menuGroupQueryService.menuGroup(menuRequest.getMenuGroupId())).thenReturn(두마리메뉴);

        Product 십원치킨 = 십원치킨();
        when(productQueryService.product(menuProductRequest.getProductId()))
            .thenReturn(십원치킨);

        //when
        ThrowingCallable createCallable = () -> menuService.create(menuRequest);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(createCallable);
    }

    @Test
    @DisplayName("메뉴들을 조회할 수 있다.")
    void list() {
        //when
        menuService.list();

        //then
        verify(menuQueryService, only()).findAll();
    }

    @Test
    @DisplayName("ids 목록으로 메뉴들을 조회할 수 있다.")
    void listByIds() {
        //given
        List<Long> ids = Arrays.asList(1L, 2L);

        //when
        menuService.listByIds(ids);

        //then
        verify(menuQueryService, only()).findAllById(ids);
    }

    private void requestedMenuSave(MenuRequest menuRequest) {
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuCommandService, only()).save(menuCaptor.capture());
        Menu savedMenu = menuCaptor.getValue();

        assertAll(
            () -> assertThat(savedMenu)
                .extracting(Menu::name, Menu::price)
                .containsExactly(menuRequest.name(), menuRequest.price()),
            () -> assertThat(savedMenu.menuProducts())
                .extracting(MenuProduct::quantity)
                .containsExactly(
                    menuRequest.getMenuProducts()
                        .stream()
                        .map(MenuProductRequest::quantity)
                        .toArray(Quantity[]::new)
                )
        );
    }
}
