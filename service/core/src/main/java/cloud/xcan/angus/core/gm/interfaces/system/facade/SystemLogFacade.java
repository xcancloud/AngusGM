package cloud.xcan.angus.core.gm.interfaces.system.facade;

import java.util.List;

public interface SystemLogFacade {

  List<String> fileList(String instanceId);

  String fileDetail(String instanceId, String fileName, Integer linesNum, Boolean tail);

  void fileClear(String instanceId, String fileName);
}
