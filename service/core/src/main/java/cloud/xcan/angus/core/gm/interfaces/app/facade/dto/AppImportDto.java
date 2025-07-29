package cloud.xcan.angus.core.gm.interfaces.app.facade.dto;

import cloud.xcan.angus.api.enums.ExportFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppImportDto {

  @NotNull
  @Schema(description = "Import file format type", requiredMode = RequiredMode.REQUIRED)
  private ExportFileType importType;

}
