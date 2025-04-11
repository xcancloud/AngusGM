package cloud.xcan.angus.core.gm.interfaces.setting.facade;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.SettingValueReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.SettingValueVo;

public interface SettingFacade {

  void replace(SettingValueReplaceDto dto);

  SettingValueVo detail(SettingKey key);

}
