package cloud.xcan.angus.core.gm.application.query.service;

import cloud.xcan.angus.core.gm.domain.service.Service;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

/**
 * 服务管理查询接口
 */
public interface ServiceQuery {

    /**
     * 根据ID查询服务
     */
    Optional<Service> findById(String id);

    /**
     * 查询服务列表
     */
    Page<Service> find(ServiceStatus status, ServiceProtocol protocol, 
                       String applicationId, String version, Pageable pageable);

    /**
     * 查询服务统计
     */
    Map<String, Object> stats();
}
