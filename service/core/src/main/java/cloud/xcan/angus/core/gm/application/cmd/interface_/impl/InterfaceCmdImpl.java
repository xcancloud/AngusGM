package cloud.xcan.angus.core.gm.application.cmd.interface_.impl;

import cloud.xcan.angus.core.gm.application.cmd.interface_.InterfaceCmd;
import cloud.xcan.angus.core.gm.domain.interface_.Interface;
import cloud.xcan.angus.core.gm.domain.interface_.InterfaceRepo;
import cloud.xcan.angus.core.gm.domain.interface_.enums.InterfaceStatus;
import cloud.xcan.angus.infra.common.exception.ResourceExisted;
import cloud.xcan.angus.infra.common.exception.ResourceNotFound;
import cloud.xcan.angus.kernel.template.BizTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterfaceCmdImpl implements InterfaceCmd {
    private final InterfaceRepo interfaceRepo;
    private final BizTemplate bizTemplate;

    @Override
    @Transactional
    public Interface create(Interface interface_) {
        return bizTemplate.execute(() -> {
            if (interfaceRepo.existsByPath(interface_.getPath())) {
                throw new ResourceExisted("接口路径已存在");
            }
            interface_.setStatus(InterfaceStatus.ENABLED);
            return interfaceRepo.save(interface_);
        });
    }

    @Override
    @Transactional
    public Interface update(Interface interface_) {
        return bizTemplate.execute(() -> {
            Interface existing = interfaceRepo.findById(interface_.getId())
                .orElseThrow(() -> new ResourceNotFound("接口不存在"));
            existing.setName(interface_.getName());
            existing.setPath(interface_.getPath());
            existing.setMethod(interface_.getMethod());
            existing.setDescription(interface_.getDescription());
            existing.setAuthRequired(interface_.getAuthRequired());
            existing.setServiceId(interface_.getServiceId());
            return interfaceRepo.save(existing);
        });
    }

    @Override
    @Transactional
    public void enable(String id) {
        bizTemplate.execute(() -> {
            Interface interface_ = interfaceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFound("接口不存在"));
            interface_.setStatus(InterfaceStatus.ENABLED);
            interfaceRepo.save(interface_);
            return null;
        });
    }

    @Override
    @Transactional
    public void disable(String id) {
        bizTemplate.execute(() -> {
            Interface interface_ = interfaceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFound("接口不存在"));
            interface_.setStatus(InterfaceStatus.DISABLED);
            interfaceRepo.save(interface_);
            return null;
        });
    }

    @Override
    @Transactional
    public void delete(String id) {
        bizTemplate.execute(() -> {
            if (!interfaceRepo.existsById(id)) {
                throw new ResourceNotFound("接口不存在");
            }
            interfaceRepo.deleteById(id);
            return null;
        });
    }
}
