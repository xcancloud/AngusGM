package cloud.xcan.angus.core.gm.application.cmd.country.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.country.CountryCmd;
import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.core.gm.domain.country.CountryRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class CountryCmdImpl extends CommCmd<Country, Long> implements CountryCmd {

  @Resource
  private CountryRepo countryRepo;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<Country> countries) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected List<IdKey<Long, Object>> process() {
        return batchInsert(countries, "code");
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<Country> countries) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        batchUpdateOrNotFound0(countries);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        for (Long id : ids) {
          countryRepo.deleteById(id);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Country, Long> getRepository() {
    return countryRepo;
  }
}
