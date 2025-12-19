package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsDetailVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsListVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsStatsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SmsFacadeImpl implements SmsFacade {
    private final SmsCmd smsCmd;
    private final SmsQuery smsQuery;
    private final SmsAssembler smsAssembler;

    @Override
    public SmsDetailVo create(SmsCreateDto dto) {
        Sms sms = smsAssembler.toEntity(dto);
        Sms created = smsCmd.create(sms);
        return smsAssembler.toDetailVo(created);
    }

    @Override
    public SmsDetailVo update(SmsUpdateDto dto) {
        Sms sms = smsAssembler.toEntity(dto);
        Sms updated = smsCmd.update(sms);
        return smsAssembler.toDetailVo(updated);
    }

    @Override
    public SmsDetailVo send(SmsCreateDto dto) {
        Sms sms = smsAssembler.toEntity(dto);
        Sms created = smsCmd.create(sms);
        Sms sent = smsCmd.send(created);
        return smsAssembler.toDetailVo(sent);
    }

    @Override
    public void retry(Long id) {
        smsCmd.retry(id);
    }

    @Override
    public void cancel(Long id) {
        smsCmd.cancel(id);
    }

    @Override
    public void delete(Long id) {
        smsCmd.delete(id);
    }

    @Override
    public SmsDetailVo findById(Long id) {
        Sms sms = smsQuery.findById(id);
        return smsAssembler.toDetailVo(sms);
    }

    @Override
    public Page<SmsListVo> findAll(SmsFindDto dto, Pageable pageable) {
        return smsQuery.findAll(dto.getStatus(), dto.getType(), dto.getPhone(), dto.getTemplateCode(), pageable)
                .map(smsAssembler::toListVo);
    }

    @Override
    public SmsStatsVo getStats() {
        SmsStatsVo stats = new SmsStatsVo();
        stats.setTotal(smsQuery.countTotal());
        
        Map<SmsStatus, Long> statusDist = new HashMap<>();
        for (SmsStatus status : SmsStatus.values()) {
            statusDist.put(status, smsQuery.countByStatus(status));
        }
        stats.setStatusDistribution(statusDist);
        
        Map<SmsType, Long> typeDist = new HashMap<>();
        for (SmsType type : SmsType.values()) {
            typeDist.put(type, smsQuery.countByType(type));
        }
        stats.setTypeDistribution(typeDist);
        
        // Calculate success rate
        long total = stats.getTotal();
        if (total > 0) {
            long successful = smsQuery.countByStatus(SmsStatus.DELIVERED);
            stats.setSuccessRate((double) successful / total * 100);
        } else {
            stats.setSuccessRate(0.0);
        }
        
        return stats;
    }
}
