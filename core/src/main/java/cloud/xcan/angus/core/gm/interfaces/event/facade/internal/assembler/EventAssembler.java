package cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler;

import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.event.source.EventContent;
import cloud.xcan.angus.core.gm.domain.EventCoreMessage;
import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.EventFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.EventSearchDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelTestDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventDetailVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.utils.GsonUtils;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.locale.MessageHolder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;


@Slf4j
public class EventAssembler {

  public static final String ROBOT_BR = "\n";
  //public static final String WEB_BR = "<br/>";

  public static List<Event> toAddDomain(List<EventContent> eventContents) {
    return eventContents.stream().map(eventContent -> {
      Event event = new Event();
      event.setUserId(nullSafe(eventContent.getUserId(), -1L));
      event.setFullName(eventContent.getFullName());
      event.setTenantId(nullSafe(eventContent.getTenantId(), OWNER_TENANT_ID));
      event.setTenantName(eventContent.getTenantName());
      event.setDescription(eventContent.getDescription());
      event.setType(eventContent.getType());
      event.setSourceData(eventContent);
      event.setCode(eventContent.getCode());
      event.setEKey(stringSafe(eventContent.getEKey()));
      event.setAppCode(eventContent.getAppCode());
      event.setServiceCode(eventContent.getServiceCode());
      event.setTargetType(eventContent.getTargetType());
      event.setTargetId(nullSafe(eventContent.getTargetId(), "-1"));
      event.setTargetName(eventContent.getTargetName());
      return event;
    }).collect(Collectors.toList());
  }

  public static EventVo toVo(Event event, String eventAddress) {
    return new EventVo().setId(event.getId())
        .setUserId(event.getUserId())
        .setFullName(event.getFullName())
        .setTenantId(event.getTenantId())
        .setTenantName(event.getTenantName())
        .setPushStatus(event.getPushStatus())
        .setCode(event.getCode())
        .setName(event.getName())
        .setDescription(event.getDescription())
        .setEKey(event.getEKey())
        .setType(event.getType())
        .setEventViewUrl(buildMarkDownContent(event,
            eventAddress + "/pubview/v1/event/" + event.getId(), ROBOT_BR)
        )
        .setTargetType(event.getTargetType())
        .setTargetId(event.getTargetId())
        .setTargetName(event.getTargetName())
        .setAppCode(event.getAppCode())
        .setServiceCode(event.getServiceCode())
        .setCreatedDate(event.getCreatedDate())
        .setPushMsg(event.getPushMsg());
  }

  public static EventDetailVo toDetailVo(Event event, String eventAddress) {
    return new EventDetailVo().setId(event.getId())
        .setUserId(event.getUserId()).setFullName(event.getFullName())
        .setTenantId(event.getTenantId()).setTenantName(event.getTenantName())
        .setPushStatus(event.getPushStatus())
        .setName(event.getName())
        .setCode(event.getCode())
        .setDescription(event.getDescription())
        .setEKey(event.getEKey())
        .setType(event.getType())
        .setEventViewUrl(buildMarkDownContent(event,
            eventAddress + "/pubview/v1/event/" + event.getId(), ROBOT_BR)
        )
        .setSource(event.getSourceData())
        .setTargetType(event.getTargetType())
        .setTargetId(event.getTargetId())
        .setTargetName(event.getTargetName())
        .setAppCode(event.getAppCode())
        .setServiceCode(event.getServiceCode())
        .setCreatedDate(event.getCreatedDate())
        .setPushMsg(event.getPushMsg());
  }

  public static EventPush testEventPush(EventChannelTestDto dto) {
    return new EventPush().setChannelType(dto.getChannelType())
        .setContent(MessageHolder.message(EventCoreMessage.CHANNEL_TEST_MESSAGE))
        .setAddress(dto.getAddress()).setName(dto.getName());
  }

  @Deprecated
  public static String getDingTalkSign(Long nowMillis, String secret) {
    String sign;
    try {
      String stringToSign = nowMillis + "\n" + secret;
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
      String base64Str = Base64.getEncoder().encodeToString(signData);
      sign = URLEncoder.encode(base64Str, StandardCharsets.UTF_8);
      log.debug("DingTalk sign: {}", sign);
    } catch (Exception e) {
      log.error("DingTalk sign Exception: ", e);
      throw BizException.of("DingTalk sign Exception: " + e.getMessage());
    }
    return sign;
  }

  public static String buildMarkDownContent(Event event, String url, String br) {
    EventContent source = event.getSourceData();
    StringBuilder sb = new StringBuilder();
    sb.append("### ").append(source.getType().getValue()).append(br).append("=============")
        .append(br);
    if (Objects.nonNull(event.getName())) {
      sb.append("> Event Name: **").append(event.getName()).append("**").append(br);
    }
    if (Objects.nonNull(event.getDescription())) {
      sb.append("> Event Desc: ").append(event.getDescription()).append(br);
    }
    if (Objects.nonNull(event.getCreatedDate())) {
      sb.append("> Event Date: ").append(event.getCreatedDate()).append(br);
    }
    if (Objects.nonNull(event.getId())) {
      sb.append("> Event ID: ").append(event.getId()).append(br);
    }
    if (Objects.nonNull(event.getCode())) {
      sb.append("> Event Code: ").append(event.getCode()).append(br);
    }
    if (Objects.nonNull(event.getType())) {
      sb.append("> Event Type: ").append(event.getType().getValue()).append(br);
    }
    if (Objects.nonNull(url)) {
      sb.append("> Event Url: [Please see detail](").append(url).append(")").append(br);
    }
    sb.append("### SOURCE").append(br);
    if (Objects.nonNull(source.getClientId())) {
      sb.append("> Client Id: ").append(source.getClientId()).append(br);
    }
    if (Objects.nonNull(source.getServiceCode())) {
      sb.append("> Service Code: ").append(source.getServiceCode()).append(br);
    }
    if (Objects.nonNull(source.getServiceName())) {
      sb.append("> Service Name: ").append(source.getServiceName()).append(br);
    }
    if (Objects.nonNull(source.getInstanceId())) {
      sb.append("> Instance Name: ").append(source.getInstanceId()).append(br);
    }
    if (Objects.nonNull(source.getRequestId())) {
      sb.append("> Request Id: ").append(source.getRequestId()).append(br);
    }
    if (Objects.nonNull(source.getMethod())) {
      sb.append("> Request method: ").append(source.getMethod()).append(br);
    }
    if (Objects.nonNull(source.getUri())) {
      sb.append("> Request uri: ").append(source.getUri()).append(br);
    }
    if (Objects.nonNull(source.getTenantId())) {
      sb.append("### PRINCIPAL").append(br);
      sb.append("> Tenant Id: ").append(source.getTenantId()).append(br);
      sb.append("> Tenant Name: ").append(source.getTenantName()).append(br);
      if (Objects.nonNull(source.getUserId())) {
        sb.append("> User Id: ").append(source.getUserId()).append(br);
        sb.append("> User Fullname: ").append(source.getFullName()).append(br);
      }
    }
    if (event.getType().exceptional) {
      sb.append("### EXCEPTION").append(br);
      if (Objects.nonNull(source.getCode())) {
        sb.append("> Exception Code: ").append(source.getCode()).append(br);
      }
      if (Objects.nonNull(source.getEKey())) {
        sb.append("> Exception Key(eKey): ").append(source.getEKey()).append(br);
      }
      if (Objects.nonNull(source.getLevel())) {
        sb.append("> Exception Level: `").append(source.getLevel()).append("`").append(br);
      }
      if (Objects.nonNull(source.getExt())) {
        sb.append("> Exception Ext: ").append(GsonUtils.toJson(source.getExt())).append(br);
      }
      if (Objects.nonNull(source.getExt())) {
        sb.append("> Exception cause: `").append(source.getCause()).append("`").append(br);
      }
    } else {
      if (Objects.nonNull(event.getTargetType())) {
        sb.append("### BUSINESS").append(br);
        sb.append("> Resource Type: ").append(event.getType()).append(br);
        sb.append("> Resource Id: ").append(event.getTargetId()).append(br);
        sb.append("> Resource Name: ").append(event.getTargetName()).append(br);
      }
    }
    return sb.toString();
  }

  public static Specification<Event> getSpecification(EventFindDto dto) {
    if (Objects.nonNull(dto.getNonTenantEvent())) {
      if (dto.getNonTenantEvent()) {
        dto.setTenantId(-1L);
      }
      // Non-persistent field
      dto.setNonTenantEvent(null);
    }
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("description", "code", "eKey")
        .orderByFields("id", "createdDate", "tenantId", "userId")
        .rangeSearchFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(EventSearchDto dto) {
    if (Objects.nonNull(dto.getNonTenantEvent())) {
      if (dto.getNonTenantEvent()) {
        dto.setTenantId(-1L);
      }
      // Non-persistent field
      dto.setNonTenantEvent(null);
    }
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("description", "code", "eKey")
        .orderByFields("id", "createdDate", "tenantId", "userId")
        .rangeSearchFields("id", "createdDate")
        .build();
  }
}
