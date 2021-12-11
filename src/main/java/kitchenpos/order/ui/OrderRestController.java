package kitchenpos.order.ui;

import java.net.URI;
import java.util.List;
import kitchenpos.order.application.OrderService;
import kitchenpos.order.ui.request.OrderRequest;
import kitchenpos.order.ui.request.OrderStatusRequest;
import kitchenpos.order.ui.response.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request) {
        OrderResponse created = orderService.create(request);
        return ResponseEntity.created(uri(created))
            .body(created);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> list() {
        return ResponseEntity.ok()
            .body(orderService.list());
    }

    @PutMapping("/{orderId}/order-status")
    public ResponseEntity<OrderResponse> changeOrderStatus(
        @PathVariable long orderId,
        @RequestBody OrderStatusRequest request) {
        return ResponseEntity.ok(orderService.changeOrderStatus(orderId, request));
    }

    private URI uri(OrderResponse created) {
        return URI.create("/api/orders/" + created.getId());
    }
}
