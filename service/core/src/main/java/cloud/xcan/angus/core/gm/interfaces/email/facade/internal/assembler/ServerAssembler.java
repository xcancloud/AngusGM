package cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.core.gm.domain.email.server.AuthAccount;
import cloud.xcan.angus.core.gm.domain.email.server.EmailServer;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerAddDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.to.AuthAccountTo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.server.ServerDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

public class ServerAssembler {

  public static EmailServer addDtoToDomain(ServerAddDto dto) {
    return new EmailServer().setName(dto.getName())
        .setProtocol(dto.getProtocol())
        .setRemark(stringSafe(dto.getRemark()))
        .setHost(dto.getHost())
        .setPort(dto.getPort())
        .setStartTlsEnabled(nullSafe(dto.getStartTlsEnabled(), true))
        .setSslEnabled(nullSafe(dto.getSslEnabled(), true))
        .setAuthEnabled(nullSafe(dto.getAuthEnabled(), true))
        .setAuthAccountData(Objects.isNull(dto.getAuthAccount()) ? null
            : new AuthAccount()
                .setAccount(dto.getAuthAccount().getAccount())
                .setPassword(dto.getAuthAccount().getPassword())
        ).setSubjectPrefix(dto.getSubjectPrefix());
  }

  public static EmailServer replaceDtoToDomain(ServerReplaceDto dto) {
    return new EmailServer().setId(dto.getId())
        .setName(dto.getName())
        .setProtocol(dto.getProtocol())
        .setRemark(stringSafe(dto.getRemark()))
        .setHost(dto.getHost())
        .setPort(dto.getPort())
        .setStartTlsEnabled(nullSafe(dto.getStartTlsEnabled(), false))
        .setSslEnabled(nullSafe(dto.getSslEnabled(), true))
        .setAuthEnabled(nullSafe(dto.getAuthEnabled(), true))
        .setAuthAccountData(Objects.isNull(dto.getAuthAccount()) ? null : new AuthAccount()
            .setAccount(dto.getAuthAccount().getAccount())
            .setPassword(dto.getAuthAccount().getPassword()))
        .setSubjectPrefix(dto.getSubjectPrefix());
  }

  public static EmailServer updateDtoToDomain(ServerUpdateDto dto) {
    return new EmailServer().setId(dto.getId())
        .setName(dto.getName())
        .setProtocol(dto.getProtocol())
        .setRemark(dto.getRemark())
        .setHost(dto.getHost())
        .setPort(dto.getPort())
        .setStartTlsEnabled(dto.getStartTlsEnabled())
        .setSslEnabled(dto.getSslEnabled())
        .setAuthEnabled(dto.getAuthEnabled())
        .setAuthAccountData(Objects.isNull(dto.getAuthAccount()) ? null : new AuthAccount()
            .setAccount(dto.getAuthAccount().getAccount())
            .setPassword(dto.getAuthAccount().getPassword())
        )
        .setSubjectPrefix(dto.getSubjectPrefix());
  }

  public static ServerDetailVo toDetail(EmailServer emailServer) {
    return new ServerDetailVo()
        .setId(emailServer.getId())
        .setName(emailServer.getName())
        .setProtocol(emailServer.getProtocol())
        .setEnabled(emailServer.getEnabled())
        .setRemark(emailServer.getRemark())
        .setHost(emailServer.getHost())
        .setPort(emailServer.getPort())
        .setStartTlsEnabled(emailServer.getStartTlsEnabled())
        .setSslEnabled(emailServer.getSslEnabled())
        .setAuthEnabled(emailServer.getAuthEnabled())
        .setAuthAccount(Objects.isNull(emailServer.getAuthAccountData()) ? null :
            new AuthAccountTo().setAccount(emailServer.getAuthAccountData().getAccount())
                .setPassword(emailServer.getAuthAccountData().getPassword()))
        .setSubjectPrefix(emailServer.getSubjectPrefix());
  }

  public static Specification<EmailServer> getSpecification(ServerFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "name")
        .orderByFields("id", "name")
        .build();
    return new GenericSpecification<>(filters);
  }

}
