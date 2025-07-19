package cloud.xcan.angus.core.gm.interfaces.notice.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.dto.NoticeAddDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.dto.NoticeFindDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.vo.NoticeLatestVo;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.vo.NoticeVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.Set;


public class NoticeAssembler {

  public static Notice addToDomain(NoticeAddDto dto) {
    return new Notice()
        .setContent(dto.getContent())
        .setScope(dto.getScope())
        .setAppId(dto.getAppId())
        .setSendType(dto.getSendType())
        .setTimingDate(nullSafe(dto.getSendTimingDate(), LocalDateTime.now()))
        .setExpirationDate(dto.getExpirationDate());
  }

  public static NoticeVo toVo(Notice notice) {
    return isNull(notice) ? null : new NoticeVo()
        .setId(notice.getId())
        .setContent(notice.getContent())
        .setScope(notice.getScope())
        .setSendType(notice.getSendType())
        .setTimingDate(notice.getTimingDate())
        .setExpirationDate(notice.getExpirationDate())
        .setAppId(notice.getAppId())
        .setAppCode(notice.getAppCode())
        .setAppName(notice.getAppName())
        .setEditionType(notice.getEditionType())
        .setTenantId(notice.getTenantId())
        .setCreatedBy(notice.getCreatedBy())
        .setCreatedDate(notice.getCreatedDate());
  }

  public static NoticeLatestVo toLatestVo(Notice notice) {
    return isNull(notice) ? null : new NoticeLatestVo()
        .setId(notice.getId())
        .setContent(notice.getContent())
        .setScope(notice.getScope())
        .setSendType(notice.getSendType())
        .setTimingDate(notice.getTimingDate())
        .setExpirationDate(notice.getExpirationDate())
        .setAppId(notice.getAppId())
        .setAppCode(notice.getAppCode())
        .setAppName(notice.getAppName())
        .setEditionType(notice.getEditionType())
        .setTenantId(notice.getTenantId())
        .setCreatedBy(notice.getCreatedBy())
        .setCreatedDate(notice.getCreatedDate());
  }

  public static GenericSpecification<Notice> getSpecification(NoticeFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "timingDate")
        .orderByFields("id", "createdDate", "timingDate")
        .matchSearchFields("content")
        .build();
    return new GenericSpecification<>(filters);
  }

}
