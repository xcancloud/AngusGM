package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.core.utils.CoreUtils.getMatchSearchFields;

import cloud.xcan.angus.core.gm.application.cmd.interfaces.InterfaceCmd;
import cloud.xcan.angus.core.gm.application.query.apimonitoring.ApiMonitoringQuery;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.service.Service;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.InterfaceFacade;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceCallStatsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceDeprecateDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceSyncDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler.InterfacesAssembler;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceCallStatsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDeprecateVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceListVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceServiceVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceSyncVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceTagVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * Interface management facade implementation
 */
@Component
public class InterfaceFacadeImpl implements InterfaceFacade {

  @Resource
  private InterfaceQuery interfaceQuery;

  @Resource
  private InterfaceCmd interfaceCmd;

  @Resource
  private ServiceQuery serviceQuery;

  @Resource
  private ApiMonitoringQuery apiMonitoringQuery;

  @Override
  public InterfaceSyncVo sync(InterfaceSyncDto dto) {
    // Sync interfaces from service
    // Sync interfaces from service
    interfaceCmd.syncFromService(dto.getServiceName());
    
    InterfaceSyncVo vo = new InterfaceSyncVo();
    vo.setServiceName(dto.getServiceName());
    vo.setSyncTime(LocalDateTime.now());
    
    // TODO: Calculate actual sync statistics
    // For now, return placeholder values
    vo.setTotalInterfaces(0);
    vo.setNewInterfaces(0);
    vo.setUpdatedInterfaces(0);
    vo.setDeprecatedInterfaces(0);
    
    return vo;
  }

  @Override
  public InterfaceSyncVo syncAll() {
    // Sync all services - query all services
    Page<Service> servicesPage = serviceQuery.find(null, null, null, null, 
        org.springframework.data.domain.PageRequest.of(0, 1000));
    List<Service> services = servicesPage.getContent();
    
    InterfaceSyncVo vo = new InterfaceSyncVo();
    vo.setSyncTime(LocalDateTime.now());
    vo.setTotalInterfaces(0);
    vo.setNewInterfaces(0);
    vo.setUpdatedInterfaces(0);
    vo.setDeprecatedInterfaces(0);
    
    List<InterfaceSyncVo.ServiceSyncInfo> serviceInfos = new ArrayList<>();
    for (Service service : services) {
      // Sync each service
      interfaceCmd.syncFromService(service.getCode());
      
      InterfaceSyncVo.ServiceSyncInfo info = new InterfaceSyncVo.ServiceSyncInfo();
      info.setServiceName(service.getCode());
      info.setInterfaceCount((int) interfaceQuery.countByServiceId(service.getId()));
      serviceInfos.add(info);
    }
    
    vo.setServices(serviceInfos);
    return vo;
  }

  @Override
  public InterfaceDeprecateVo deprecate(Long id, InterfaceDeprecateDto dto) {
    Interface inter = interfaceCmd.deprecate(id, 
        dto.getDeprecated() != null ? dto.getDeprecated() : false, 
        dto.getDeprecationNote());
    
    InterfaceDeprecateVo vo = new InterfaceDeprecateVo();
    vo.setId(inter.getId());
    vo.setDeprecated(inter.getDeprecated());
    vo.setDeprecationNote(inter.getDeprecationNote());
    vo.setModifiedDate(inter.getModifiedDate() != null ? inter.getModifiedDate() : LocalDateTime.now());
    
    return vo;
  }

  @Override
  public InterfaceDetailVo getDetail(Long id) {
    Interface inter = interfaceQuery.findAndCheck(id);
    return InterfacesAssembler.toDetailVo(inter);
  }

  @Override
  public PageResult<InterfaceListVo> list(InterfaceFindDto dto) {
    GenericSpecification<Interface> spec = InterfacesAssembler.getSpecification(dto);
    Page<Interface> page = interfaceQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, InterfacesAssembler::toListVo);
  }

  @Override
  public PageResult<InterfaceListVo> listByService(String serviceName, InterfaceFindDto dto) {
    // Convert DTO to query specification in Facade layer
    GenericSpecification<Interface> spec = InterfacesAssembler.getSpecification(dto);
    
    // Find service by name/code to get serviceId
    Page<Service> servicesPage = serviceQuery.find(null, null, null, null, 
        org.springframework.data.domain.PageRequest.of(0, 1000));
    Service service = servicesPage.getContent().stream()
        .filter(s -> serviceName.equals(s.getCode()) || serviceName.equals(s.getName()))
        .findFirst()
        .orElse(null);
    
    if (service == null) {
      return buildVoPageResult(
          org.springframework.data.domain.Page.empty(dto.tranPage()),
          InterfacesAssembler::toListVo);
    }
    
    // Call application layer with domain objects and query conditions
    Page<Interface> page = interfaceQuery.findByServiceId(
        service.getId(),
        spec,
        dto.tranPage(),
        dto.fullTextSearch,
        dto.fullTextSearch ? getMatchSearchFields(dto.getClass()) : null);
    
    return buildVoPageResult(page, InterfacesAssembler::toListVo);
  }

  @Override
  public PageResult<InterfaceListVo> listByTag(String tag, InterfaceFindDto dto) {
    // Convert DTO to query specification in Facade layer
    GenericSpecification<Interface> spec = InterfacesAssembler.getSpecification(dto);
    
    // Call application layer with domain objects and query conditions
    Page<Interface> page = interfaceQuery.findByTag(
        tag,
        spec,
        dto.tranPage(),
        dto.fullTextSearch,
        dto.fullTextSearch ? getMatchSearchFields(dto.getClass()) : null);
    
    return buildVoPageResult(page, InterfacesAssembler::toListVo);
  }

  @Override
  public List<InterfaceServiceVo> getServices() {
    // Query all services
    Page<Service> servicesPage = serviceQuery.find(null, null, null, null, 
        org.springframework.data.domain.PageRequest.of(0, 1000));
    List<Service> services = servicesPage.getContent();
    List<InterfaceServiceVo> vos = new ArrayList<>();
    
    for (Service service : services) {
      InterfaceServiceVo vo = new InterfaceServiceVo();
      vo.setServiceName(service.getCode());
      vo.setDisplayName(service.getName());
      vo.setVersion(service.getVersion());
      vo.setBaseUrl(service.getBaseUrl());
      vo.setSyncTime(service.getLastModifiedDate());
      
      // Count interfaces for this service
      long interfaceCount = interfaceQuery.countByServiceId(service.getId());
      vo.setInterfaceCount((int) interfaceCount);
      
      vos.add(vo);
    }
    
    return vos;
  }

  @Override
  public List<InterfaceTagVo> getTags() {
    List<String> tags = interfaceQuery.findAllTags();
    List<InterfaceTagVo> vos = new ArrayList<>();
    
    for (String tag : tags) {
      InterfaceTagVo vo = new InterfaceTagVo();
      vo.setName(tag);
      
      // Count interfaces with this tag
      // TODO: Implement efficient tag counting
      vo.setInterfaceCount(0);
      
      vos.add(vo);
    }
    
    return vos;
  }

  @Override
  public InterfaceCallStatsVo getCallStats(Long id, InterfaceCallStatsDto dto) {
    Interface inter = interfaceQuery.findAndCheck(id);
    
    InterfaceCallStatsVo vo = new InterfaceCallStatsVo();
    vo.setInterfaceId(inter.getId());
    vo.setPath(inter.getPath());
    vo.setMethod(inter.getMethod() != null ? inter.getMethod().name() : null);
    
    // Set period
    InterfaceCallStatsVo.Period period = new InterfaceCallStatsVo.Period();
    period.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now().minusDays(7));
    period.setEndDate(dto.getEndDate() != null ? dto.getEndDate() : LocalDate.now());
    vo.setPeriod(period);
    
    // TODO: Get actual call statistics from ApiMonitoringQuery
    // For now, return placeholder values
    vo.setTotalRequests(0L);
    vo.setSuccessRequests(0L);
    vo.setFailedRequests(0L);
    vo.setAvgResponseTime(0);
    vo.setMaxResponseTime(0);
    vo.setMinResponseTime(0);
    vo.setRequestsPerDay(new ArrayList<>());
    vo.setErrorCodes(new ArrayList<>());
    
    return vo;
  }
}
