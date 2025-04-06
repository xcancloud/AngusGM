package cloud.xcan.angus.api.commonlink.service;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonServiceRepo")
public interface ServiceRepo extends BaseRepository<Service, Long> {

  List<Service> findAllByIdIn(Collection<Long> ids);

  Optional<Service> findByCode(String code);

  List<Service> findAllByIdInAndEnabled(Collection<Long> ids, boolean b);

  List<Service> findByCodeIn(Collection<String> serviceCodes);

  @Modifying
  @Query(value = "DELETE FROM service WHERE id IN ?1", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

}
