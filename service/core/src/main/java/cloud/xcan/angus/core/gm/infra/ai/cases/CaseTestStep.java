package cloud.xcan.angus.core.gm.infra.ai.cases;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class CaseTestStep {

  @Schema(description = "Case testing step")
  private String step;

  @Schema(description = "Case expected testing result")
  private String expectedResult;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CaseTestStep)) {
      return false;
    }
    CaseTestStep that = (CaseTestStep) o;
    return Objects.equals(step, that.step) &&
        Objects.equals(expectedResult, that.expectedResult);
  }

  @Override
  public int hashCode() {
    return Objects.hash(step, expectedResult);
  }
}
