package cloud.xcan.angus.core.gm.interfaces.notice.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.notice.facade.internal.assembler.NoticeAssembler.addToDomain;
import static cloud.xcan.angus.core.gm.interfaces.notice.facade.internal.assembler.NoticeAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.notice.facade.internal.assembler.NoticeAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.notice.facade.internal.assembler.NoticeAssembler.toLatestVo;
import static cloud.xcan.angus.core.gm.interfaces.notice.facade.internal.assembler.NoticeAssembler.toVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.notice.NoticeCmd;
import cloud.xcan.angus.core.gm.application.query.notice.NoticeQuery;
import cloud.xcan.angus.core.gm.application.query.notice.NoticeSearch;
import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.NoticeFacade;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.dto.NoticeAddDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.dto.NoticeFindDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.internal.assembler.NoticeAssembler;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.vo.NoticeLatestVo;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.vo.NoticeVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class NoticeFacadeImpl implements NoticeFacade {

  @Resource
  private NoticeCmd noticeCmd;

  @Resource
  private NoticeQuery noticeQuery;

  @Resource
  private NoticeSearch noticeSearch;

  @Override
  public IdKey<Long, Object> add(NoticeAddDto dto) {
    Notice notice = addToDomain(dto);
    return noticeCmd.add(notice);
  }

  @Override
  public void delete(List<Long> ids) {
    noticeCmd.delete(ids);
  }

  @NameJoin
  @Override
  public NoticeVo detail(Long id) {
    Notice notice = noticeQuery.detail(id);
    return toVo(notice);
  }

  @NameJoin
  @Override
  public PageResult<NoticeVo> list(NoticeFindDto dto) {
    Page<Notice> page = noticeQuery.find(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, NoticeAssembler::toVo);
  }

  @NameJoin
  @Override
  public PageResult<NoticeVo> search(NoticeFindDto dto) {
    Page<Notice> page = noticeSearch.search(getSearchCriteria(dto), dto.tranPage(),
        Notice.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, NoticeAssembler::toVo);
  }

  @Override
  public NoticeLatestVo globalLatest() {
    return toLatestVo(noticeQuery.globalLatest());
  }

  @Override
  public NoticeLatestVo appLatest(Long appId) {
    return toLatestVo(noticeQuery.appLatest(appId));
  }
}
