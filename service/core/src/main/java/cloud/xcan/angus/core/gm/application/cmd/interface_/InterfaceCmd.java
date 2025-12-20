package cloud.xcan.angus.core.gm.application.cmd.interface_;

import cloud.xcan.angus.core.gm.domain.interface_.Interface;

/**
 * 接口管理命令服务接口
 */
public interface InterfaceCmd {
    Interface create(Interface interface_);
    Interface update(Interface interface_);
    void enable(String id);
    void disable(String id);
    void delete(String id);
}
