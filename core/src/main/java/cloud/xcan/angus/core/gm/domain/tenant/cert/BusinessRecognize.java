package cloud.xcan.angus.core.gm.domain.tenant.cert;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class BusinessRecognize {

  private String creditCode;
  private String companyName;
  private String companyType;
  private String businessAddress;
  private String legalPerson;
  private String registeredCapital;
  private String RegistrationDate; // Ignore warning, Three party interface parameters
  private String validPeriod;
  private String validFromDate;
  private String validToDate;

}
