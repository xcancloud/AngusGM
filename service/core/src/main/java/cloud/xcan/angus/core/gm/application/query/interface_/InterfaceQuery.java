package cloud.xcan.angus.core.gm.application.query.interface_;

import cloud.xcan.angus.core.gm.domain.interface_.Interface;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceFindDto;
import org.springframework.data.domain.Page;

public interface InterfaceQuery {
    Interface get(String id);
    Page<Interface> find(InterfaceFindDto dto);
    long countTotal();
    long countByStatus(cloud.xcan.angus.core.gm.domain.interface_.enums.InterfaceStatus status);
}
