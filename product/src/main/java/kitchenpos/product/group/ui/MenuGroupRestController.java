package kitchenpos.product.group.ui;

import java.net.URI;
import java.util.List;
import kitchenpos.product.group.application.MenuGroupService;
import kitchenpos.product.group.ui.request.MenuGroupRequest;
import kitchenpos.product.group.ui.response.MenuGroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu-groups")
public class MenuGroupRestController {

    private final MenuGroupService menuGroupService;

    public MenuGroupRestController(MenuGroupService menuGroupService) {
        this.menuGroupService = menuGroupService;
    }

    @PostMapping
    public ResponseEntity<MenuGroupResponse> create(@RequestBody MenuGroupRequest request) {
        MenuGroupResponse created = menuGroupService.create(request);
        return ResponseEntity.created(uri(created))
            .body(created);
    }

    @GetMapping
    public ResponseEntity<List<MenuGroupResponse>> list() {
        return ResponseEntity.ok()
            .body(menuGroupService.list());
    }

    private URI uri(MenuGroupResponse created) {
        return URI.create("/api/menu-groups/" + created.getId());
    }
}
