package cloud.xcan.angus.api.gm.setting.dto.quota;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class QuotaCheckDto implements Serializable {

  /**
   * It needs to be consistent with the name value in SummaryQueryRegister.
   *
   * @see SummaryQueryRegister
   */
  @NotNull
  @Schema(description = "Quota resource name.", requiredMode = RequiredMode.REQUIRED)
  private QuotaResource name;

  @NotNull
  @Schema(description = "Quota value, storage disk must be converted to bytes.", requiredMode = RequiredMode.REQUIRED)
  private Long quota;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuotaCheckDto that = (QuotaCheckDto) o;
    return name == that.name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
