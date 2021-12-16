package kitchenpos.table.application;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.common.exception.NotFoundException;
import kitchenpos.table.ui.request.TableGroupRequest;
import kitchenpos.table.ui.request.TableGroupRequest.OrderTableIdRequest;
import kitchenpos.table.ui.response.TableGroupResponse;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.domain.OrderTableRepository;
import kitchenpos.table.domain.TableGroup;
import kitchenpos.table.domain.TableGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TableGroupService {

    private final TableGroupRepository tableGroupRepository;
    private final OrderTableRepository tableRepository;

    public TableGroupService(TableGroupRepository tableGroupRepository,
        OrderTableRepository tableRepository) {
        this.tableGroupRepository = tableGroupRepository;
        this.tableRepository = tableRepository;
    }

    @Transactional
    public TableGroupResponse create(TableGroupRequest request) {
        return TableGroupResponse.from(tableGroupRepository.save(newTableGroup(request)));
    }

    @Transactional
    public void ungroup(long id) {
        tableGroup(id).ungroup();
    }

    private TableGroup tableGroup(long id) {
        return tableGroupRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(
                String.format("단체 지정된 그룹 id(%d)를 찾을 수 없습니다.", id)));
    }

    private TableGroup newTableGroup(TableGroupRequest request) {
        return TableGroup.from(orderTables(request.getOrderTables()));
    }

    private List<OrderTable> orderTables(List<OrderTableIdRequest> requests) {
        return requests.stream()
            .map(request -> tableRepository.table(request.getId()))
            .collect(Collectors.toList());
    }
}
