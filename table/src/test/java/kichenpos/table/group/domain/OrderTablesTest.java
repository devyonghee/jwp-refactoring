package kichenpos.table.group.domain;

import static kichenpos.table.table.sample.OrderTableSample.빈_두명_테이블;
import static kichenpos.table.table.sample.OrderTableSample.채워진_다섯명_테이블;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Arrays;
import java.util.Collections;
import kichenpos.table.table.domain.Headcount;
import kichenpos.table.table.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("주문 테이블들")
class OrderTablesTest {

    @Test
    @DisplayName("생성")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> OrderTables.from(
                Collections.singletonList(OrderTable.empty(Headcount.from(1)))
            ));
    }

    @Test
    @DisplayName("주문 테이블 리스트 필수")
    void instance_nullOrderTables_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> OrderTables.from(null))
            .withMessage("주문 테이블 리스트는 필수입니다.");
    }

    @Test
    @DisplayName("주문 테이블 리스트에 null이 포함될 수 없음")
    void instance_containNull_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> OrderTables.from(Collections.singletonList(null)))
            .withMessageEndingWith("null이 포함될 수 없습니다.");
    }

    @Test
    @DisplayName("그룹이 없고 빈 상태")
    void notHaveGroupAndEmpty() {
        //given
        OrderTables orderTables = OrderTables.from(Collections.singletonList(빈_두명_테이블()));

        //when, then
        assertThat(orderTables.notHaveGroupAndEmpty()).isTrue();
    }

    @Test
    @DisplayName("하나라도 채워진 상태라면 그룹이 없고 빈 상태가 아님")
    void notHaveGroupAndEmpty_oneFull() {
        //given
        OrderTables orderTables = OrderTables.from(Arrays.asList(빈_두명_테이블(), 채워진_다섯명_테이블()));

        //when, then
        assertThat(orderTables.notHaveGroupAndEmpty()).isFalse();
    }

}
