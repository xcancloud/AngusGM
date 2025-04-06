package cloud.xcan.angus.api.gm.analysis.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SummaryQueryVo implements Serializable {

  private String name;

  private String table;

  private boolean hasSysAdmin = false;

  private String[] hasAnyAuthority;

  private String[] hasAuthority;

  private String topAuthority;

  private String[] groupByColumns = new String[]{"createdDate"};

  private String[] aggregateColumns = new String[]{"id"};

}
