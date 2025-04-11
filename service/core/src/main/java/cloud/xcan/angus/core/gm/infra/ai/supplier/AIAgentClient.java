package cloud.xcan.angus.core.gm.infra.ai.supplier;

import cloud.xcan.angus.core.gm.infra.ai.AIChatType;
import java.util.Map;

public interface AIAgentClient {

  String WRITE_BACKLOG_PROMPT = "\\n要求输出结果用Json格式输出，且输出Json内容中不能包含注释，JSON格式中内容和下面例子保持一致：\\n[\\\"第一个Backlog名称\\\",\\\"第二个Backlog名称\\\"]";
  String SPLIT_SUB_TASK_PROMPT = "\\n要求输出结果用Json格式输出，且输出Json内容中不能包含注释，JSON格式中内容和下面例子保持一致：\\n[\\\"第一个子任务名称\\\",\\\"第二个子任务名称\\\"]";
  String WRITE_FUNC_CAE_PROMPT = "\\n要求输出结果用Json格式输出，且输出Json内容中不能包含注释，JSON格式中内容和下面例子保持一致：\\n[{\\\"name\\\":\\\"用例描述\\\",\\\"precondition\\\":\\\"用例前置条件\\\",\\\"steps\\\":[{\\\"step\\\":\\\"测试步骤\\\",\\\"expectedResult\\\":\\\"每个步骤对应期望结果，该字段是非必须的\\\"}],\\\"description\\\":\\\"用例详细描述\\\"}]";
  String WRITE_ANGUS_SCRIPT_PROMPT = "\\n要求输出结果用YAML格式输出，且必须符合AngusTester中脚本测试规范";

  String createSession(String aiAgentId) throws Exception;

  Map<String, Object> chat(AIChatType type, String cmd, String sessionId,
      String aiAgentId);

}
