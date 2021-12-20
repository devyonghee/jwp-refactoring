package kitchenpos.product.group.domain;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MenuGroupCommandService {

    private final MenuGroupRepository menuGroupRepository;

    public MenuGroupCommandService(MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    public MenuGroup save(MenuGroup menuGroup) {
        return menuGroupRepository.save(menuGroup);
    }
}
