package cloud.xcan.angus.core.gm.application.cmd.service.impl;

import cloud.xcan.angus.common.exception.ResourceExisted;
import cloud.xcan.angus.common.exception.ResourceNotFound;
import cloud.xcan.angus.core.gm.application.cmd.service.ServiceCmd;
import cloud.xcan.angus.core.gm.domain.service.Service;
import cloud.xcan.angus.core.gm.domain.service.ServiceRepo;
import cloud.xcan.angus.core.gm.domain.service.enums.ServiceStatus;
import cloud.xcan.angus.infra.shared.biz.BizTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务管理命令实现
 */
@Component
@RequiredArgsConstructor
public class ServiceCmdImpl implements ServiceCmd {

    private final ServiceRepo serviceRepo;
    private final BizTemplate bizTemplate;

    @Override
    @Transactional
    public Service create(Service service) {
        return bizTemplate.execute(() -> {
            // 验证服务代码唯一性
            if (serviceRepo.findByCode(service.getCode()).isPresent()) {
                throw new ResourceExisted("服务代码已存在: " + service.getCode());
            }

            // 设置默认状态
            service.setStatus(ServiceStatus.ENABLED);
            service.setInterfaceCount(0);

            return serviceRepo.save(service);
        });
    }

    @Override
    @Transactional
    public Service update(Service service) {
        return bizTemplate.execute(() -> {
            Service existing = serviceRepo.findById(service.getId())
                    .orElseThrow(() -> new ResourceNotFound("服务不存在: " + service.getId()));

            // 如果修改了代码，验证唯一性
            if (!existing.getCode().equals(service.getCode())) {
                if (serviceRepo.findByCode(service.getCode()).isPresent()) {
                    throw new ResourceExisted("服务代码已存在: " + service.getCode());
                }
            }

            // 更新字段
            existing.setName(service.getName());
            existing.setCode(service.getCode());
            existing.setDescription(service.getDescription());
            existing.setProtocol(service.getProtocol());
            existing.setVersion(service.getVersion());
            existing.setBaseUrl(service.getBaseUrl());
            existing.setApplicationId(service.getApplicationId());

            return serviceRepo.save(existing);
        });
    }

    @Override
    @Transactional
    public Service enable(String id) {
        return bizTemplate.execute(() -> {
            Service service = serviceRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("服务不存在: " + id));

            service.setStatus(ServiceStatus.ENABLED);
            return serviceRepo.save(service);
        });
    }

    @Override
    @Transactional
    public Service disable(String id) {
        return bizTemplate.execute(() -> {
            Service service = serviceRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("服务不存在: " + id));

            service.setStatus(ServiceStatus.DISABLED);
            return serviceRepo.save(service);
        });
    }

    @Override
    @Transactional
    public void delete(String id) {
        bizTemplate.execute(() -> {
            if (!serviceRepo.existsById(id)) {
                throw new ResourceNotFound("服务不存在: " + id);
            }

            serviceRepo.deleteById(id);
            return null;
        });
    }
}
