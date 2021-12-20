package kitchenpos.product.product.application;

import static kitchenpos.product.product.sample.ProductSample.십원치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import kichenpos.common.domain.Name;
import kichenpos.common.domain.Price;
import kitchenpos.product.product.domain.Product;
import kitchenpos.product.product.domain.ProductCommandService;
import kitchenpos.product.product.domain.ProductQueryService;
import kitchenpos.product.product.ui.request.ProductRequest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("상품 서비스")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class ProductServiceTest {

    @Mock
    private ProductCommandService commandService;
    @Mock
    private ProductQueryService queryService;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품을 등록할 수 있다.")
    void create() {
        //given
        ProductRequest request = new ProductRequest("치킨", BigDecimal.ONE);

        Product 십원치킨 = 십원치킨();
        when(commandService.save(any())).thenReturn(십원치킨);

        //when
        productService.create(request);

        //then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(commandService, only()).save(productCaptor.capture());
        assertThat(productCaptor.getValue())
            .extracting(Product::name, Product::price)
            .containsExactly(Name.from(request.getName()), Price.from(request.getPrice()));
    }

    @Test
    @DisplayName("등록하려는 상품의 가격은 반드시 존재해야 한다.")
    void create_nullPrice_thrownException() {
        //given
        ProductRequest request = new ProductRequest("치킨", null);

        //when
        ThrowingCallable createCallable = () -> productService.create(request);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(createCallable);
    }

    @Test
    @DisplayName("등록하려는 상품의 가격은 0원 이상이어야 한다.")
    void create_priceLessThanZero_thrownException() {
        //given
        ProductRequest request = new ProductRequest("치킨", BigDecimal.valueOf(-1));

        //when
        ThrowingCallable createCallable = () -> productService.create(request);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(createCallable);
    }

    @Test
    @DisplayName("상품들을 조회할 수 있다.")
    void list() {
        //when
        productService.list();

        //then
        verify(queryService, only()).findAll();
    }
}
