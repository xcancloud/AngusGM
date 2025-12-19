package cloud.xcan.angus.core.gm.application.query.country.impl;


import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.country.CountryQuery;
import cloud.xcan.angus.core.gm.domain.country.Country;
import cloud.xcan.angus.core.gm.domain.country.CountryRepo;
import cloud.xcan.angus.core.gm.domain.country.CountrySearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of country query operations.
 * </p>
 * <p>
 * Manages country retrieval, validation, and search capabilities. Provides comprehensive country
 * querying with full-text search support.
 * </p>
 * <p>
 * Supports country detail retrieval, paginated listing, and specification-based filtering for
 * country management.
 * </p>
 */
@org.springframework.stereotype.Service
public class CountryQueryImpl implements CountryQuery {

  @Resource
  private CountryRepo countryRepo;
  @Resource
  private CountrySearchRepo countrySearchRepo;

  /**
   * <p>
   * Retrieves detailed country information by ID.
   * </p>
   * <p>
   * Fetches complete country record with all associated information. Throws ResourceNotFound
   * exception if country does not exist.
   * </p>
   */
  @Override
  public Country detail(Long id) {
    return new BizTemplate<Country>() {

      @Override
      protected Country process() {
        return countryRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "Country"));
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves countries with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Returns paginated results for
   * comprehensive country management.
   * </p>
   */
  @Override
  public Page<Country> list(GenericSpecification<Country> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Country>>() {

      @Override
      protected Page<Country> process() {
        return fullTextSearch
            ? countrySearchRepo.find(spec.getCriteria(), pageable, Country.class, match)
            : countryRepo.findAll(spec, pageable);
      }
    }.execute();
  }

}
