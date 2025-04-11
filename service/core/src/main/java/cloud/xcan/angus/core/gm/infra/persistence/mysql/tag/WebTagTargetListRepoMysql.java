package cloud.xcan.angus.core.gm.infra.persistence.mysql.tag;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValueAndRemove;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.remote.search.SearchOperation.EQUAL;
import static cloud.xcan.angus.remote.search.SearchOperation.IN;
import static cloud.xcan.angus.remote.search.SearchOperation.MATCH_END;
import static cloud.xcan.angus.spec.utils.ObjectUtils.safeStringInValue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.core.gm.domain.tag.WebTagTargetListRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class WebTagTargetListRepoMysql extends AbstractSearchRepository<WebTagTarget> implements
    WebTagTargetListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<WebTagTarget> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "web_tag_target", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<WebTagTarget> mainClz, String tableName, String... matches) {
    String mainAlis = "a";
    // Assemble mainClz table
    StringBuilder sql = new StringBuilder("SELECT DISTINCT %s FROM " + tableName + " " + mainAlis);

    // Assemble non mainClz tagIds in Conditions
    sql.append(assembleTagJoinCondition(criteria))
        .append(" WHERE 1=1 ")
        // Assemble mainClz Conditions: targetType, createdBy, createdDate
        .append(getCriteriaAliasCondition(criteria, mainClz, mainAlis, mode, false, matches));
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    String targetType = findFirstValue(criteria, "targetType");
    if (WebTagTargetType.APP.getValue().equals(targetType)) {
      return "a.id, a.tenant_id, a.created_by, a.created_date, t.id tagId, t.name tagName, org.created_by targetCreatedBy, org.created_date targetCreatedDate, org.id targetId, org.name targetName, 'APP' targetType";
    }
    return "a.id, a.tenant_id, a.created_by, a.created_date, t.id tagId, t.name tagName, org.created_by targetCreatedBy, org.created_date targetCreatedDate, org.id targetId, org.name targetName, org.type targetType";
  }

  public static StringBuilder assembleTagJoinCondition(Set<SearchCriteria> criteria) {
    String targetType = findFirstValue(criteria, "targetType");

    StringBuilder sql = new StringBuilder();
    String tagIdInValue = findFirstValueAndRemove(criteria, "tagId", IN);
    String tagIdEqualValue = findFirstValueAndRemove(criteria, "tagId", EQUAL);
    String tagNameEqualValue = findFirstValueAndRemove(criteria, "tagName", EQUAL);
    String tagNameMatchValue = findFirstValueAndRemove(criteria, "tagName", MATCH_END);
    String targetIdInValue = findFirstValueAndRemove(criteria, "targetId", IN);
    String targetIdEqualValue = findFirstValueAndRemove(criteria, "targetId", EQUAL);
    String targetNameEqualValue = findFirstValueAndRemove(criteria, "targetName", EQUAL);
    String targetNameMatchValue = findFirstValueAndRemove(criteria, "targetName", MATCH_END);
    Long tenantId = getOptTenantId();

    sql.append(" INNER JOIN web_tag t ON a.tag_id = t.id ")
        //.append(" AND a.target_type = '").append(targetType).append("'")
        .append(" AND t.tenant_id = ").append(tenantId);
    if (isNotBlank(tagIdInValue)) {
      sql.append(" AND t.id in (").append(tagIdInValue).append(")");
    }
    if (isNotBlank(tagIdEqualValue)) {
      sql.append(" AND t.id = ").append(tagIdEqualValue);
    }
    if (isNotBlank(tagNameEqualValue)) {
      sql.append(" AND t.name = '").append(tagNameEqualValue).append("'");
    }
    if (isNotBlank(tagNameMatchValue)) {
      sql.append(" AND t.name like '").append(tagNameMatchValue).append("%'");
    }
    if (WebTagTargetType.APP.getValue().equals(targetType)) {
      sql.append(" INNER JOIN app org ON a.target_id = org.id ");
    } else {
      sql.append(" INNER JOIN app_func org ON a.target_id = org.id ");
    }
    sql.append(" AND org.tenant_id = ").append(tenantId);
    if (isNotBlank(targetIdInValue)) {
      targetIdInValue = safeStringInValue(targetIdInValue);
      sql.append(" AND org.id in (").append(targetIdInValue).append(")");
    }
    if (isNotBlank(targetIdEqualValue)) {
      sql.append(" AND org.id = ").append(targetIdEqualValue);
    }
    if (isNotBlank(targetNameEqualValue)) {
      sql.append(" AND org.name = '").append(targetNameEqualValue).append("'");
    }
    if (isNotBlank(targetNameMatchValue)) {
      sql.append(" AND org.name like '").append(targetNameMatchValue).append("%'");
    }
    return sql;
  }
}
