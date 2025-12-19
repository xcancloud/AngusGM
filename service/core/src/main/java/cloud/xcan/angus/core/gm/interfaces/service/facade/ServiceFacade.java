package cloud.xcan.angus.core.gm.interfaces.service.facade;

import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceCreateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceStatsVo;
import org.springframework.data.domain.Page;

/**
 * 服务管理门面接口
 */
public interface ServiceFacade {

    /**
     * 创建服务
     */
    ServiceDetailVo create(ServiceCreateDto dto);

    /**
     * 更新服务
     */
    ServiceDetailVo update(ServiceUpdateDto dto);

    /**
     * 启用服务
     */
    ServiceDetailVo enable(String id);

    /**
     * 禁用服务
     */
    ServiceDetailVo disable(String id);

    /**
     * 删除服务
     */
    void delete(String id);

    /**
     * 查询服务详情
     */
    ServiceDetailVo get(String id);

    /**
     * 查询服务列表
     */
    Page<ServiceListVo> find(ServiceFindDto dto);

    /**
     * 查询服务统计
     */
    ServiceStatsVo stats();
}
