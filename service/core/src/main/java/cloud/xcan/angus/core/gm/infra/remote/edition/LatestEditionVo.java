package cloud.xcan.angus.core.gm.infra.remote.edition;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class LatestEditionVo implements Serializable {

  private Long goodsId;

  private String name;

  private String code;

  private String version;

  private String iconUrl;

  private String introduction;

  private String information;

  private LinkedHashSet<String> features;

  private LocalDateTime releaseDate;

  private Boolean upgradeable;

}
