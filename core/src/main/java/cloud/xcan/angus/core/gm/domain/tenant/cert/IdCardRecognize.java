package cloud.xcan.angus.core.gm.domain.tenant.cert;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class IdCardRecognize {

  /**
   * Face of ID card
   */
  private String name;
  private String sex;
  private String ethnicity;
  private String birthDate;
  private String address;
  private String idNumber;

  /**
   * Back of ID card
   */
  private String issueAuthority;
  private String validPeriod;

}
