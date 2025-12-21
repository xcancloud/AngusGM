package cloud.xcan.angus.core.gm.interfaces.quota.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.quota.QuotaCmd;
import cloud.xcan.angus.core.gm.application.query.quota.QuotaQuery;
import cloud.xcan.angus.core.gm.domain.quota.Quota;
import cloud.xcan.angus.core.gm.domain.quota.QuotaStatus;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.QuotaFacade;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.internal.assembler.QuotaAssembler;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuotaFacadeImpl implements QuotaFacade {
    
    @Resource
    private QuotaCmd quotaCmd;
    
    @Resource
    private QuotaQuery quotaQuery;
    
    @Resource
    private QuotaAssembler assembler;
    
    @Override
    public QuotaDetailVo create(QuotaCreateDto dto) {
        Quota quota = assembler.toEntity(dto);
        Quota created = quotaCmd.create(quota);
        return assembler.toDetailVo(created);
    }
    
    @Override
    public QuotaDetailVo update(QuotaUpdateDto dto) {
        Quota quota = assembler.toEntity(dto);
        Quota updated = quotaCmd.update(quota);
        return assembler.toDetailVo(updated);
    }
    
    @Override
    public void delete(Long id) {
        quotaCmd.delete(id);
    }
    
    @Override
    public QuotaDetailVo findById(Long id) {
        Quota quota = quotaQuery.findById(id).orElseThrow();
        return assembler.toDetailVo(quota);
    }
    
    @Override
    public List<QuotaListVo> findAll(QuotaFindDto dto) {
        return quotaQuery.findAll().stream()
                .map(assembler::toListVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public QuotaStatsVo getStats() {
        List<Quota> all = quotaQuery.findAll();
        QuotaStatsVo stats = new QuotaStatsVo();
        stats.setTotalCount((long) all.size());
        stats.setExceededCount(all.stream().filter(q -> q.getStatus() == QuotaStatus.EXCEEDED).count());
        stats.setWarningCount(all.stream().filter(q -> q.getStatus() == QuotaStatus.WARNING).count());
        return stats;
    }
}
