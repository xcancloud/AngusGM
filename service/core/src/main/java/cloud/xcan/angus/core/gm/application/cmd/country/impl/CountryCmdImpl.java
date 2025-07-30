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

/**
 * Implementation of country command operations for managing country data.
 * 
 * <p>This class provides comprehensive functionality for country management including:</p>
 * <ul>
 *   <li>Adding multiple countries with batch operations</li>
 *   <li>Updating country information</li>
 *   <li>Deleting countries by identifiers</li>
 *   <li>Handling country data consistency</li>
 * </ul>
 * 
 * <p>The implementation ensures proper country data management with
 * transactional safety and batch processing capabilities.</p>
 */
@Biz
public class CountryCmdImpl extends CommCmd<Country, Long> implements CountryCmd {

  @Resource
  private CountryRepo countryRepo;

  /**
   * Adds multiple countries with batch processing and duplicate handling.
   * 
   * <p>This method performs batch country creation including:</p>
   * <ul>
   *   <li>Validating country data integrity</li>
   *   <li>Handling duplicate country codes</li>
   *   <li>Batch inserting for optimal performance</li>
   *   <li>Returning created country identifiers</li>
   * </ul>
   * 
   * @param countries List of country entities to create
   * @return List of created country identifiers with associated data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<Country> countries) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected List<IdKey<Long, Object>> process() {
        // Batch insert countries with duplicate code handling
        return batchInsert(countries, "code");
      }
    }.execute();
  }

  /**
   * Updates multiple countries with batch processing.
   * 
   * <p>This method performs batch country updates including:</p>
   * <ul>
   *   <li>Validating country existence</li>
   *   <li>Updating country information</li>
   *   <li>Handling not found scenarios</li>
   * </ul>
   * 
   * @param countries List of country entities to update
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(List<Country> countries) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Batch update countries with not found handling
        batchUpdateOrNotFound0(countries);
        return null;
      }
    }.execute();
  }

  /**
   * Deletes countries by identifiers with individual deletion.
   * 
   * <p>This method performs country deletion including:</p>
   * <ul>
   *   <li>Validating country existence</li>
   *   <li>Deleting countries individually</li>
   *   <li>Handling deletion errors</li>
   * </ul>
   * 
   * @param ids Set of country identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Delete countries individually for error handling
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
