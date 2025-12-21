package cloud.xcan.angus.core.gm.application.query.interfaces;

import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceFindDto;
import org.springframework.data.domain.Page;

public interface InterfaceQuery {
    Interface get(String id);
    Page<Interface> find(InterfaceFindDto dto);
    long countTotal();
    long countByStatus(cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceStatus status);
}
