package cloud.xcan.angus.core.gm.infra.ai.cases;

import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class FuncCaseVo {

  private String name;

  private String precondition;

  private LinkedHashSet<CaseTestStep> steps;

  private String description;

}
