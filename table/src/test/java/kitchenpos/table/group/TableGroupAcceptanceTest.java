package kitchenpos.table.group;


import static kitchenpos.table.table.step.TableAcceptanceStep.테이블_저장되어_있음;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import kitchenpos.table.AcceptanceTest;
import kitchenpos.table.group.step.TableGroupAcceptanceStep;
import kitchenpos.table.group.ui.response.TableGroupResponse;
import kitchenpos.table.table.ui.response.OrderTableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("테이블 그룹 관련 기능")
class TableGroupAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("단체 지정을 등록할 수 있다.")
    void create() {
        //given
        OrderTableResponse firstOrderTable = 테이블_저장되어_있음(3, true);
        OrderTableResponse secondOrderTable = 테이블_저장되어_있음(5, true);
        List<Long> orderTableIds = Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId());

        //when
        ExtractableResponse<Response> response = TableGroupAcceptanceStep.단체_지정_요청(orderTableIds);

        //then
        TableGroupAcceptanceStep.단체_지정_됨(response, orderTableIds);
    }

    @Test
    @DisplayName("단체 지정을 해제할 수 있다.")
    void ungroup() {
        //given
        OrderTableResponse firstOrderTable = 테이블_저장되어_있음(3, true);
        OrderTableResponse secondOrderTable = 테이블_저장되어_있음(5, true);
        TableGroupResponse tableGroup = TableGroupAcceptanceStep.단체_지정_되어_있음(
            Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId()));

        //when
        ExtractableResponse<Response> response = TableGroupAcceptanceStep.단체_해제_요청(tableGroup);

        //then
        TableGroupAcceptanceStep.단체_해제_됨(response);
    }
}
