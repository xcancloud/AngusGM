package cloud.xcan.angus.core.gm.interfaces.service;

import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceCreateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceStatsVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 服务管理REST接口
 */
@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceRest {

    private final ServiceFacade serviceFacade;

    /**
     * 创建服务
     */
    @PostMapping
    public ServiceDetailVo create(@Valid @RequestBody ServiceCreateDto dto) {
        return serviceFacade.create(dto);
    }

    /**
     * 更新服务
     */
    @PatchMapping("/{id}")
    public ServiceDetailVo update(@PathVariable String id, @Valid @RequestBody ServiceUpdateDto dto) {
        dto.setId(id);
        return serviceFacade.update(dto);
    }

    /**
     * 启用服务
     */
    @PostMapping("/{id}/enable")
    public ServiceDetailVo enable(@PathVariable String id) {
        return serviceFacade.enable(id);
    }

    /**
     * 禁用服务
     */
    @PostMapping("/{id}/disable")
    public ServiceDetailVo disable(@PathVariable String id) {
        return serviceFacade.disable(id);
    }

    /**
     * 删除服务
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        serviceFacade.delete(id);
    }

    /**
     * 查询服务详情
     */
    @GetMapping("/{id}")
    public ServiceDetailVo get(@PathVariable String id) {
        return serviceFacade.get(id);
    }

    /**
     * 查询服务列表
     */
    @GetMapping
    public Page<ServiceListVo> find(ServiceFindDto dto) {
        return serviceFacade.find(dto);
    }

    /**
     * 查询服务统计
     */
    @GetMapping("/stats")
    public ServiceStatsVo stats() {
        return serviceFacade.stats();
    }
}
