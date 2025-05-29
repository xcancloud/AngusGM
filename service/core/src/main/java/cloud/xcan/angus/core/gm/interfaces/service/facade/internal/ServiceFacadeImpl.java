package cloud.xcan.angus.core.gm.interfaces.service.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler.toServiceResourceApiVo;
import static cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler.toServiceResourceVo;
import static cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.service.ServiceCmd;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceSearch;
import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceAddDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceSearchDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ResourceApiVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceResourceVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class ServiceFacadeImpl implements ServiceFacade {

  @Resource
  private ServiceCmd serviceCmd;

  @Resource
  private ServiceQuery serviceQuery;

  @Resource
  private ServiceSearch serviceSearch;

  @Override
  public IdKey<Long, Object> add(ServiceAddDto dto) {
    return serviceCmd.add(addDtoToDomain(dto));
  }

  @Override
  public void update(ServiceUpdateDto dto) {
    serviceCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public IdKey<Long, Object> replace(ServiceReplaceDto dto) {
    return serviceCmd.replace(replaceDtoToDomain(dto));
  }

  @Override
  public void delete(HashSet<Long> ids) {
    serviceCmd.delete(ids);
  }

  @Override
  public void enabled(List<EnabledOrDisabledDto> dto) {
    List<Service> services = dto.stream().map(ServiceAssembler::enabledDtoToDomain)
        .collect(Collectors.toList());
    serviceCmd.enabled(services);
  }

  @NameJoin
  @Override
  public ServiceVo detail(Long id) {
    Service service = serviceQuery.detail(id);
    return ServiceAssembler.toServiceDetailVo(service);
  }

  @NameJoin
  @Override
  public PageResult<ServiceVo> list(ServiceFindDto findDto) {
    Page<Service> page = serviceQuery.find(getSpecification(findDto), findDto.tranPage());
    return buildVoPageResult(page, ServiceAssembler::toServiceDetailVo);
  }

  @NameJoin
  @Override
  public PageResult<ServiceVo> search(ServiceSearchDto dto) {
    Page<Service> page = serviceSearch.search(getSearchCriteria(dto), dto.tranPage(),
        Service.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, ServiceAssembler::toServiceDetailVo);
  }

  @Override
  public List<ServiceResourceVo> resourceList(String serviceCode, Boolean auth) {
    List<Service> resources = serviceQuery.resourceList(serviceCode, auth);
    return toServiceResourceVo(resources);
  }

  @Override
  public List<ResourceApiVo> resourceApiList(String serviceCode, String resourceName,
      Boolean auth) {
    Service resourceApis = serviceQuery.resourceApiList(serviceCode, resourceName, auth);
    return toServiceResourceApiVo(resourceApis);
  }
}
