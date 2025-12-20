package cloud.xcan.angus.core.gm.interfaces.service.facade.internal;

import cloud.xcan.angus.common.exception.ResourceNotFound;
import cloud.xcan.angus.core.gm.application.cmd.service.ServiceCmd;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.domain.service.Service;
import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceCreateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceStatsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * 服务管理门面实现
 */
@Component
@RequiredArgsConstructor
public class ServiceFacadeImpl implements ServiceFacade {

    private final ServiceCmd serviceCmd;
    private final ServiceQuery serviceQuery;
    private final ServiceAssembler serviceAssembler;

    @Override
    public ServiceDetailVo create(ServiceCreateDto dto) {
        Service service = serviceAssembler.toEntity(dto);
        service = serviceCmd.create(service);
        return serviceAssembler.toDetailVo(service);
    }

    @Override
    public ServiceDetailVo update(ServiceUpdateDto dto) {
        Service service = serviceAssembler.toEntity(dto);
        service = serviceCmd.update(service);
        return serviceAssembler.toDetailVo(service);
    }

    @Override
    public ServiceDetailVo enable(String id) {
        Service service = serviceCmd.enable(id);
        return serviceAssembler.toDetailVo(service);
    }

    @Override
    public ServiceDetailVo disable(String id) {
        Service service = serviceCmd.disable(id);
        return serviceAssembler.toDetailVo(service);
    }

    @Override
    public void delete(String id) {
        serviceCmd.delete(id);
    }

    @Override
    public ServiceDetailVo get(String id) {
        Service service = serviceQuery.findById(id)
                .orElseThrow(() -> new ResourceNotFound("服务不存在: " + id));
        return serviceAssembler.toDetailVo(service);
    }

    @Override
    public Page<ServiceListVo> find(ServiceFindDto dto) {
        Page<Service> page = serviceQuery.find(
                dto.getStatus(),
                dto.getProtocol(),
                dto.getApplicationId(),
                dto.getVersion(),
                PageRequest.of(dto.getPage(), dto.getSize())
        );
        return page.map(serviceAssembler::toListVo);
    }

    @Override
    public ServiceStatsVo stats() {
        return serviceAssembler.toStatsVo(serviceQuery.stats());
    }
}
