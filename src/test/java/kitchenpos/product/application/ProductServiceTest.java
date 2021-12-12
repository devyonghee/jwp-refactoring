package kitchenpos.product.application;

import static kitchenpos.product.application.sample.ProductSample.후라이드치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import kitchenpos.product.ui.request.ProductRequest;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품을 등록할 수 있다.")
    void create() {
        //given
        ProductRequest request = new ProductRequest("후라이드치킨", BigDecimal.ONE);
        when(productRepository.save(any())).thenReturn(후라이드치킨());

        //when
        productService.create(request);

        //then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, only()).save(productCaptor.capture());
        assertThat(productCaptor.getValue())
            .extracting(Product::getName, Product::getPrice)
            .containsExactly(request.getName(), request.getPrice());
    }

    @Test
    @DisplayName("등록하려는 상품의 가격은 반드시 존재해야 한다.")
    void create_nullPrice_thrownException() {
        //given
        ProductRequest request = new ProductRequest("후라이드치킨", null);

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
        ProductRequest request = new ProductRequest("후라이드치킨", BigDecimal.valueOf(-1));

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
        verify(productRepository, only()).findAll();
    }

}
