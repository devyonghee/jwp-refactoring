package kichenpos.order.table.infrastructure;

import kichenpos.order.table.infrastructure.dto.OrderTableDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "table", url = "${kichenpos.table.url}", path = "/api/tables")
public interface OrderTableClient {

    @GetMapping("/{id}")
    OrderTableDto getTable(@PathVariable("id") long id);

    @PostMapping("/{id}/order")
    void changeOrdered(@PathVariable("id") long id);

    @PostMapping("/{id}/finish")
    void changeFinish(@PathVariable("id") long id);
}
