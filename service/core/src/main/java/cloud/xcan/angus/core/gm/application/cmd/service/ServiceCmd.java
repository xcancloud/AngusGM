package cloud.xcan.angus.core.gm.application.cmd.service;

import cloud.xcan.angus.core.gm.domain.service.Service;

/**
 * 服务管理命令接口
 */
public interface ServiceCmd {

    /**
     * 创建服务
     */
    Service create(Service service);

    /**
     * 更新服务
     */
    Service update(Service service);

    /**
     * 启用服务
     */
    Service enable(String id);

    /**
     * 禁用服务
     */
    Service disable(String id);

    /**
     * 删除服务
     */
    void delete(String id);
}
