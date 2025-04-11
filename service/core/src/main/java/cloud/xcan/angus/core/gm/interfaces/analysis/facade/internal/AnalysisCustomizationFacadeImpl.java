package cloud.xcan.angus.core.gm.interfaces.analysis.facade.internal;

import static cloud.xcan.angus.api.manager.converter.AnalysisConverter.toQueryBuilder;
import static cloud.xcan.angus.api.manager.converter.AnalysisConverter.toQueryDefinition;

import cloud.xcan.angus.api.gm.analysis.dto.CustomizationSummaryDto;
import cloud.xcan.angus.api.gm.analysis.vo.SummaryQueryDefinitionVo;
import cloud.xcan.angus.api.manager.SimpleSummaryManager;
import cloud.xcan.angus.api.manager.converter.AnalysisConverter;
import cloud.xcan.angus.core.gm.interfaces.analysis.facade.AnalysisCustomizationFacade;
import cloud.xcan.angus.core.jpa.repository.SimpleSummaryRepository;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

// @ConditionalOnMissingClass -> Main application
@Component
public class AnalysisCustomizationFacadeImpl implements AnalysisCustomizationFacade {

  @Resource
  private SimpleSummaryManager simpleSummaryManager;

  @Override
  public SummaryQueryDefinitionVo definitions() {
    return toQueryDefinition(SimpleSummaryRepository.REGISTER);
  }

  @Override
  public Object summary(CustomizationSummaryDto dto) {
    return simpleSummaryManager.getSummary(toQueryBuilder(dto));
  }

  @Override
  public Map<String, Object> summary(List<CustomizationSummaryDto> dtos) {
    return simpleSummaryManager.getSummary(dtos.stream().map(AnalysisConverter::toQueryBuilder)
        .collect(Collectors.toList()));
  }
}
