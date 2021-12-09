package kitchenpos.acceptance;

import static kitchenpos.acceptance.step.MenuAcceptanceStep.메뉴_등록_되어_있음;
import static kitchenpos.acceptance.step.MenuGroupAcceptanceStep.메뉴_그룹_등록_되어_있음;
import static kitchenpos.acceptance.step.OrderAcceptanceStep.주문_등록_되어_있음;
import static kitchenpos.acceptance.step.OrderAcceptanceStep.주문_등록_됨;
import static kitchenpos.acceptance.step.OrderAcceptanceStep.주문_등록_요청;
import static kitchenpos.acceptance.step.OrderAcceptanceStep.주문_목록_조회_됨;
import static kitchenpos.acceptance.step.OrderAcceptanceStep.주문_목록_조회_요청;
import static kitchenpos.acceptance.step.OrderAcceptanceStep.주문_상태_수정_됨;
import static kitchenpos.acceptance.step.OrderAcceptanceStep.주문_상태_수정_요청;
import static kitchenpos.acceptance.step.ProductAcceptanceStep.상품_등록_되어_있음;
import static kitchenpos.acceptance.step.TableAcceptanceStep.테이블_저장되어_있음;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("주문 관련 기능")
class OrderAcceptanceTest extends AcceptanceTest {


    private Menu 후라이드치킨세트;
    private OrderTable 주문테이블;

    @BeforeEach
    void setUp() {
        MenuGroup 두마리메뉴 = 메뉴_그룹_등록_되어_있음("두마리메뉴");
        Product 후라이드치킨 = 상품_등록_되어_있음("후라이드치킨", BigDecimal.TEN);
        후라이드치킨세트 = 메뉴_등록_되어_있음("후라이드치킨세트", BigDecimal.TEN,
            두마리메뉴.getId(), 후라이드치킨.getId(), 2);
        주문테이블 = 테이블_저장되어_있음(3, false);
    }

    @Test
    @DisplayName("주문을 등록할 수 있다.")
    void create() {
        //given
        int quantity = 2;

        //when
        ExtractableResponse<Response> response = 주문_등록_요청(주문테이블, 후라이드치킨세트, quantity);

        //then
        주문_등록_됨(response, quantity, 후라이드치킨세트);
    }

    @Test
    @DisplayName("주문들을 조회할 수 있다.")
    void list() {
        //given
        Order order = 주문_등록_되어_있음(주문테이블, 후라이드치킨세트, 2);

        //when
        ExtractableResponse<Response> response = 주문_목록_조회_요청();

        //then
        주문_목록_조회_됨(response, order);
    }

    @Test
    @DisplayName("주문 상태를 변경할 수 있다.")
    void changeOrderStatus() {
        //given
        Order order = 주문_등록_되어_있음(주문테이블, 후라이드치킨세트, 2);
        OrderStatus status = OrderStatus.MEAL;

        //when
        ExtractableResponse<Response> response = 주문_상태_수정_요청(order.getId(), status);

        //then
        주문_상태_수정_됨(response, status);
    }
}
