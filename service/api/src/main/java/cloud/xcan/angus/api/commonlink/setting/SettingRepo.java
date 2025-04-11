package cloud.xcan.angus.api.commonlink.setting;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@DoInFuture("Add cache")
@Repository("commonSettingRepo")
public interface SettingRepo extends BaseRepository<Setting, Long> {

  Optional<Setting> findByKey(SettingKey key);

  @Modifying
  @Query(value = "UPDATE c_setting SET value = ?2 WHERE `key` =?1", nativeQuery = true)
  int updateValueByKey(String key, String value);

}
