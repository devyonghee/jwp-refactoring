package kitchenpos.product.group.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import kichenpos.common.domain.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("메뉴 그룹")
class MenuGroupTest {

    @Test
    @DisplayName("생성")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> MenuGroup.from(Name.from("두마리메뉴")));
    }

    @Test
    @DisplayName("이름은 필수")
    void instance_nullName_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> MenuGroup.from(null))
            .withMessage("이름은 필수입니다.");
    }
}
