package cloud.xcan.angus.api.commonlink.setting.apisample;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class ApiSample extends ValueObjectSupport<ApiSample> {

  private String icon;

  private String name;

  private String angusUrl;

  private String swaggerUrl;

  /**
   * Rewrite business equal. {@link ValueObjectSupport#equals(Object)}
   */
  @Override
  public boolean sameValueAs(ApiSample other) {
    return isNotEmpty(name) && nonNull(other) && name.equalsIgnoreCase(other.getName());
  }
}
