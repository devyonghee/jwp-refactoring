package kitchenpos.table.table.application;

import java.util.List;
import kitchenpos.table.table.domain.TableCommandService;
import kitchenpos.table.table.domain.TableQueryService;
import kitchenpos.table.table.ui.request.EmptyRequest;
import kitchenpos.table.table.ui.request.OrderTableRequest;
import kitchenpos.table.table.ui.request.TableGuestsCountRequest;
import kitchenpos.table.table.ui.response.OrderTableResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TableService {

    private final TableCommandService commandService;
    private final TableQueryService queryService;

    public TableService(TableCommandService commandService,
        TableQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Transactional
    public OrderTableResponse create(OrderTableRequest request) {
        return OrderTableResponse.from(commandService.save(request.toEntity()));
    }

    public List<OrderTableResponse> list() {
        return OrderTableResponse.listFrom(queryService.findAll());
    }

    public OrderTableResponse findById(long id) {
        return OrderTableResponse.from(queryService.table(id));
    }

    @Transactional
    public OrderTableResponse changeEmpty(long id, EmptyRequest request) {
        return OrderTableResponse.from(commandService.changeEmpty(id, request.isEmpty()));
    }

    @Transactional
    public OrderTableResponse changeNumberOfGuests(long id, TableGuestsCountRequest request) {
        return OrderTableResponse.from(
            commandService.changeNumberOfGuests(id, request.numberOfGuests()));
    }

    @Transactional
    public void changeOrdered(long orderTableId) {
        commandService.changeOrdered(orderTableId);
    }

    @Transactional
    public void changeFinish(long orderTableId) {
        commandService.changeFinish(orderTableId);
    }
}
