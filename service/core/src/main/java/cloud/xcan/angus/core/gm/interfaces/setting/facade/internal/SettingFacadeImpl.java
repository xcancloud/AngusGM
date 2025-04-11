package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal;


import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingAssembler.toSettingValueVo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingAssembler.updateToSetting;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingCmd;
import cloud.xcan.angus.core.gm.application.query.setting.SettingQuery;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.SettingValueReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.SettingValueVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SettingFacadeImpl implements SettingFacade {

  @Resource
  private SettingQuery settingQuery;

  @Resource
  private SettingCmd settingCmd;

  @Override
  public void replace(SettingValueReplaceDto dto) {
    settingCmd.update(dto.getKey(), updateToSetting(dto));
  }

  @Override
  public SettingValueVo detail(SettingKey key) {
    return toSettingValueVo(settingQuery.detail(key));
  }

}
