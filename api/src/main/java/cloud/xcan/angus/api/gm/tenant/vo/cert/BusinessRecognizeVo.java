package cloud.xcan.angus.api.gm.tenant.vo.cert;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class BusinessRecognizeVo {

  private String creditCode;
  private String companyName;
  private String companyType;
  private String businessAddress;
  private String legalPerson;
  private String registeredCapital;
  private String registrationDate;
  private String validPeriod;
  private String validFromDate;
  private String validToDate;

}
