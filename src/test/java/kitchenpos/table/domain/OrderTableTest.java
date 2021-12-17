package kitchenpos.table.domain;

import static kitchenpos.table.sample.OrderTableSample.빈_두명_테이블;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Arrays;
import kitchenpos.common.exception.InvalidStatusException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("주문 테이블")
class OrderTableTest {

    @Test
    @DisplayName("생성")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> OrderTable.empty(Headcount.from(1)));
    }

    @Test
    @DisplayName("손님 수는 필수")
    void instance_nullHeadcount_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> OrderTable.empty(null))
            .withMessageEndingWith("필수입니다.");
    }

    @ParameterizedTest(name = "[{index}] 그룹과 주문이 없으면 {0} 상태로 변경 가능")
    @DisplayName("테이블 자리 빈 상태 변경")
    @CsvSource({"true,true", "false,false"})
    void changeEmpty(boolean empty, boolean expected) {
        //given
        OrderTable orderTable = OrderTable.empty(Headcount.from(1));

        //when
        orderTable.changeEmpty(empty);

        //then
        assertThat(orderTable.isEmpty()).isEqualTo(expected);
    }

    @Test
    @DisplayName("그룹이 있으면 테이블 빈 상태 변경 불가능")
    void changeEmpty_hasGroup_thrownInvalidStatusException() {
        //given
        OrderTable orderTable = OrderTable.empty(Headcount.from(1));
        TableGroup.from(Arrays.asList(orderTable, 빈_두명_테이블()));

        //when
        ThrowingCallable changeEmptyCallable = () -> orderTable.changeEmpty(false);

        //then
        assertThatExceptionOfType(InvalidStatusException.class)
            .isThrownBy(changeEmptyCallable)
            .withMessageEndingWith("그룹이 지정되어 있어서 상태를 변경할 수 없습니다.");
    }

    @Test
    @DisplayName("주문된 상태라면 테이블 빈 상태 변경 불가능")
    void changeEmpty_cooking_thrownInvalidStatusException() {
        //given
        OrderTable orderTable = OrderTable.seated(Headcount.from(1));
        orderTable.ordered();

        //when
        ThrowingCallable changeEmptyCallable = () -> orderTable.changeEmpty(true);

        //then
        assertThatExceptionOfType(InvalidStatusException.class)
            .isThrownBy(changeEmptyCallable)
            .withMessageEndingWith("상태를 변경할 수 없습니다.");
    }

    @Test
    @DisplayName("주문된 상태 변경")
    void ordered() {
        //given
        OrderTable orderTable = OrderTable.seated(Headcount.from(1));

        //when
        orderTable.ordered();

        //then
        assertThat(orderTable.isOrdered()).isTrue();
    }

    @Test
    @DisplayName("비어있는 테이블에서 주문 받은 상태로 변경 불가능")
    void ordered_emptyTable_thrownInvalidStatusException() {
        //given
        OrderTable orderTable = OrderTable.empty(Headcount.from(1));

        //when
        ThrowingCallable orderedCallable = orderTable::ordered;

        //then
        assertThatExceptionOfType(InvalidStatusException.class)
            .isThrownBy(orderedCallable)
            .withMessageEndingWith("주문을 받을 수 없습니다.");
    }

    @Test
    @DisplayName("테이블 완료 상태로 변경")
    void finish() {
        //given
        OrderTable orderTable = OrderTable.seated(Headcount.from(1));
        orderTable.ordered();

        //when, then
        assertThatNoException()
            .isThrownBy(orderTable::finish);
    }

    @Test
    @DisplayName("주문 받지 않은 테이블을 완료 상태로 변경 불가능")
    void finish_notOrdered_thrownInvalidStatusException() {
        //given
        OrderTable orderTable = OrderTable.seated(Headcount.from(1));

        //when
        ThrowingCallable finishCallable = orderTable::finish;

        //then
        assertThatExceptionOfType(InvalidStatusException.class)
            .isThrownBy(finishCallable)
            .withMessageEndingWith("상태를 완료로 변경할 수 없습니다.");
    }

    @Test
    @DisplayName("방문한 손님 수 변경")
    void changeNumberOfGuests() {
        //given
        OrderTable orderTable = OrderTable.seated(Headcount.from(1));

        //when
        orderTable.changeNumberOfGuests(Headcount.from(10));

        //then
        assertThat(orderTable.numberOfGuests()).isEqualTo(Headcount.from(10));
    }

    @Test
    @DisplayName("빈 테이블의 방문한 손님 수 변경 불가능")
    void changeNumberOfGuests_empty_thrownInvalidStatusException() {
        //given
        OrderTable orderTable = OrderTable.empty(Headcount.from(1));

        //when
        ThrowingCallable changeCallable = () -> orderTable.changeNumberOfGuests(Headcount.from(10));

        //then
        assertThatExceptionOfType(InvalidStatusException.class)
            .isThrownBy(changeCallable)
            .withMessageEndingWith("방문한 손님 수를 변경할 수 없습니다.");
    }
}
