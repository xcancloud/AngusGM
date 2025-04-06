package cloud.xcan.angus.core.gm.application.cmd.setting;

import cloud.xcan.angus.api.commonlink.setting.quota.Quota;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import java.util.List;
import java.util.Map;

public interface SettingTenantQuotaCmd {

  void quotaReplace(String name, Long quota);

  void quotaReplaceBatch(Map<QuotaResource, Long> quotasMap);

  void quotaReplaceByOrder(Long orderId, Map<QuotaResource, Long> quotaMap, Long tenantId,
      String status, Boolean expired);

  Long newQuotaInit(String name);

  void add0(Quota quotaData, List<Long> tenantIds);

  void init(Long tenantId);

}
