package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler;


import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.channel.SmsChannelVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;


public class SmsChannelAssembler {

  public static SmsChannel updateDtoToDomain(SmsChannelUpdateDto dto) {
    return new SmsChannel().setId(dto.getId())
        .setEndpoint(dto.getEndpoint())
        .setAccessKeyId(dto.getAccessKeyId())
        .setAccessKeySecret(dto.getAccessKeySecret())
        .setThirdChannelNo(dto.getThirdChannelNo());
  }

  public static SmsChannelVo toVo(SmsChannel smsChannel) {
    return new SmsChannelVo()
        .setId(smsChannel.getId())
        .setName(smsChannel.getName())
        .setEnabled(smsChannel.getEnabled())
        .setLogo(smsChannel.getLogo())
        .setAccessKeyId(smsChannel.getAccessKeyId())
        .setAccessKeySecret(smsChannel.getAccessKeySecret())
        .setEndpoint(smsChannel.getEndpoint())
        .setThirdChannelNo(smsChannel.getThirdChannelNo());
  }

  public static Specification<SmsChannel> getSpecification(SmsChannelFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .build();
    Specification<SmsChannel> joinSp = (root, query, cb) -> cb.conjunction();
    GenericSpecification<SmsChannel> gp = new GenericSpecification<>(filters);
    return joinSp.and(gp);
  }

  public static SmsChannelVo getSmsChannelVo(SmsChannel smsChannel) {
    return new SmsChannelVo()
        .setName(smsChannel.getName())
        .setLogo(smsChannel.getLogo())
        .setEnabled(smsChannel.getEnabled())
        .setEndpoint(smsChannel.getEndpoint())
        .setAccessKeyId(smsChannel.getAccessKeyId())
        .setAccessKeySecret(smsChannel.getAccessKeySecret())
        .setThirdChannelNo(smsChannel.getThirdChannelNo());
  }
}
