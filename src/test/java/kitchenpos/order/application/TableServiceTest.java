package kitchenpos.order.application;

import static kitchenpos.order.sample.OrderLineItemSample.이십원_후라이트치킨_두마리세트_한개_주문_항목;
import static kitchenpos.order.sample.OrderTableSample.빈_두명_테이블;
import static kitchenpos.order.sample.OrderTableSample.빈_세명_테이블;
import static kitchenpos.order.sample.OrderTableSample.채워진_다섯명_테이블;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import kitchenpos.common.exception.InvalidStatusException;
import kitchenpos.common.exception.NotFoundException;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.Headcount;
import kitchenpos.order.domain.OrderTable;
import kitchenpos.order.domain.OrderTableRepository;
import kitchenpos.order.domain.TableGroup;
import kitchenpos.order.ui.request.OrderTableRequest;
import kitchenpos.order.ui.request.TableGuestsCountRequest;
import kitchenpos.order.ui.request.TableStatusRequest;
import kitchenpos.order.ui.response.OrderTableResponse;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("테이블 서비스")
@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @InjectMocks
    private TableService tableService;

    @Test
    @DisplayName("테이블을 등록할 수 있다.")
    void create() {
        //given
        OrderTableRequest request = new OrderTableRequest(3, true);
        OrderTable 빈_세명_테이블 = 빈_세명_테이블();
        when(orderTableRepository.save(any())).thenReturn(빈_세명_테이블);

        //when
        tableService.create(request);

        //then
        ArgumentCaptor<OrderTable> orderTableCaptor = ArgumentCaptor.forClass(OrderTable.class);
        verify(orderTableRepository, only()).save(orderTableCaptor.capture());
        assertThat(orderTableCaptor.getValue())
            .extracting(OrderTable::hasTableGroup, OrderTable::isEmpty, OrderTable::numberOfGuests)
            .containsExactly(false, request.isEmpty(), Headcount.from(request.getNumberOfGuests()));
    }

    @Test
    @DisplayName("테이블들을 조회할 수 있다.")
    void list() {
        //when
        tableService.list();

        //then
        verify(orderTableRepository, only()).findAll();
    }

    @Test
    @DisplayName("테이블의 빈 상태를 변경할 수 있다.")
    void changeEmpty() {
        //given
        TableStatusRequest request = new TableStatusRequest(true);

        OrderTable 채워진_다섯명_테이블 = 채워진_다섯명_테이블();
        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.of(채워진_다섯명_테이블));

        //when
        OrderTableResponse response = tableService.changeEmpty(1L, request);

        //then
        assertThat(response)
            .extracting(OrderTableResponse::isEmpty)
            .isEqualTo(request.isEmpty());
    }

    @Test
    @DisplayName("빈테이블 여부를 변경하려면 변경하려는 테이블의 정보가 반드시 저장되어 있어야 한다.")
    void changeEmpty_notExistOrderTable_thrownException() {
        //given
        TableStatusRequest request = new TableStatusRequest(false);
        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        ThrowingCallable changeCallable = () -> tableService.changeEmpty(1L, request);

        //then
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(changeCallable)
            .withMessageEndingWith("찾을 수 없습니다.");
    }

    @Test
    @DisplayName("빈테이블 여부를 변경하려면 그룹이 지정되어 있지 않아야 한다.")
    void changeEmpty_existTableGroupId_thrownException() {
        //given
        TableStatusRequest request = new TableStatusRequest(false);

        OrderTable 빈_두명_테이블 = 빈_두명_테이블();
        OrderTable 빈_세명_테이블 = 빈_세명_테이블();
        TableGroup.from(Arrays.asList(빈_두명_테이블, 빈_세명_테이블));
        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.of(빈_두명_테이블));

        //when
        ThrowingCallable changeCallable = () -> tableService.changeEmpty(1L, request);

        //then
        assertThatExceptionOfType(InvalidStatusException.class)
            .isThrownBy(changeCallable)
            .withMessageEndingWith("그룹이 지정되어 있어서 상태를 변경할 수 없습니다.");
    }

    @Test
    @DisplayName("빈테이블 여부를 변경하려면 주문 상태가 조리 또는 식사 중이라면 변경이 불가능하다.")
    void changeEmpty_cookOrMealStatus_thrownException() {
        //given
        TableStatusRequest request = new TableStatusRequest(false);

        OrderTable 채워진_다섯명_테이블 = 채워진_다섯명_테이블();
        Order.of(채워진_다섯명_테이블, Collections.singletonList(이십원_후라이트치킨_두마리세트_한개_주문_항목()));
        when(orderTableRepository.findById(anyLong()))
            .thenReturn(Optional.of(채워진_다섯명_테이블));

        //when
        ThrowingCallable changeCallable = () -> tableService.changeEmpty(1L, request);

        //then
        assertThatExceptionOfType(InvalidStatusException.class)
            .isThrownBy(changeCallable)
            .withMessageEndingWith("상태를 변경할 수 없습니다.");
    }

    @Test
    @DisplayName("방문한 손님 수를 변경할 수 있다.")
    void changeNumberOfGuests() {
        //given
        int numberOfGuests = 3;
        TableGuestsCountRequest request = new TableGuestsCountRequest(numberOfGuests);

        OrderTable 채워진_다섯명_테이블 = 채워진_다섯명_테이블();
        when(orderTableRepository.findById(anyLong()))
            .thenReturn(Optional.of(채워진_다섯명_테이블));

        //when
        OrderTableResponse response = tableService.changeNumberOfGuests(1L, request);

        //then
        assertThat(response)
            .extracting(OrderTableResponse::getNumberOfGuests)
            .isEqualTo(numberOfGuests);
    }

    @Test
    @DisplayName("변경하려는 손님 수는 0이상 이어야 한다.")
    void changeNumberOfGuests_lessThanZero_thrownException() {
        //given
        TableGuestsCountRequest request = new TableGuestsCountRequest(-1);

        OrderTable 채워진_다섯명_테이블 = 채워진_다섯명_테이블();
        when(orderTableRepository.findById(anyLong()))
            .thenReturn(Optional.of(채워진_다섯명_테이블));

        //when
        ThrowingCallable changeCallable = () -> tableService.changeNumberOfGuests(1L, request);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(changeCallable)
            .withMessageEndingWith("이상 이어야 합니다.");
    }

    @Test
    @DisplayName("변경하려는 테이블의 정보가 반드시 저장되어 있어야 한다.")
    void changeNumberOfGuests_notExistOrderTable_thrownException() {
        //given
        TableGuestsCountRequest request = new TableGuestsCountRequest(3);

        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        ThrowingCallable changeCallable = () -> tableService.changeNumberOfGuests(1L, request);

        //then
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(changeCallable)
            .withMessageEndingWith("찾을 수 없습니다.");
    }

    @Test
    @DisplayName("방문한 손님 수를 변경하려는 테이블은 비어있지 않아야 한다.")
    void changeNumberOfGuests_empty_thrownException() {
        //given
        TableGuestsCountRequest request = new TableGuestsCountRequest(3);

        OrderTable 빈_두명_테이블 = 빈_두명_테이블();
        when(orderTableRepository.findById(anyLong())).thenReturn(Optional.of(빈_두명_테이블));

        //when
        ThrowingCallable changeCallable = () -> tableService.changeNumberOfGuests(1L, request);

        //then
        assertThatExceptionOfType(InvalidStatusException.class)
            .isThrownBy(changeCallable)
            .withMessageEndingWith("방문한 손님 수를 변경할 수 없습니다.");
    }
}
