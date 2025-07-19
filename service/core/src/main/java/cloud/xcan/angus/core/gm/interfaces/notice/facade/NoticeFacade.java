package cloud.xcan.angus.core.gm.interfaces.notice.facade;


import cloud.xcan.angus.core.gm.interfaces.notice.facade.dto.NoticeAddDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.dto.NoticeFindDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.vo.NoticeLatestVo;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.vo.NoticeVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;


public interface NoticeFacade {

  IdKey<Long, Object> add(NoticeAddDto dto);

  void delete(List<Long> ids);

  NoticeVo detail(Long id);

  NoticeLatestVo globalLatest();

  NoticeLatestVo appLatest(Long appId);

  PageResult<NoticeVo> list(NoticeFindDto dto);

}
