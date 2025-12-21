package cloud.xcan.angus.core.gm.application.query.interfaces.impl;

import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRepo;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceStatus;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.infra.common.exception.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterfaceQueryImpl implements InterfaceQuery {
    private final InterfaceRepo interfaceRepo;

    @Override
    public Interface get(String id) {
        return interfaceRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFound("接口不存在"));
    }

    @Override
    public Page<Interface> find(InterfaceFindDto dto) {
        PageRequest pageRequest = PageRequest.of(
            dto.getPage() != null ? dto.getPage() : 0,
            dto.getSize() != null ? dto.getSize() : 20
        );
        return interfaceRepo.findAll(pageRequest);
    }

    @Override
    public long countTotal() {
        return interfaceRepo.count();
    }

    @Override
    public long countByStatus(InterfaceStatus status) {
        return interfaceRepo.countByStatus(status);
    }
}
