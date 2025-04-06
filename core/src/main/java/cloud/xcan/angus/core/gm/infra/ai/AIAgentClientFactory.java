package cloud.xcan.angus.core.gm.infra.ai;

import cloud.xcan.angus.core.gm.infra.ai.supplier.AIAgentClient;
import cloud.xcan.angus.core.gm.infra.ai.supplier.ProviderType;
import cloud.xcan.angus.core.gm.infra.ai.supplier.TudouAIAgentClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AIAgentClientFactory {

  private AIAgentClientFactory() {
  }

  public static AIAgentClient create(ProviderType providerType) {
    switch (providerType) {
      case Tudou:
        return new TudouAIAgentClient();
    }
    return new TudouAIAgentClient();
  }

  public static void main(String[] args) {
    String sessionId = "333ca176-0784-4a06-af9d-8ecab5a85091";
    String aiAgentId = "2137107453692846363";
    AIAgentClient client = create(ProviderType.Tudou);

    String cmd1 = "写10个Wiki系统的产品Backlog";
    client.chat(AIChatType.WRITE_BACKLOG, cmd1, sessionId, aiAgentId);

    String cmd2 = "将用户登录功能拆分成多个子任务";
    client.chat(AIChatType.SPLIT_SUB_TASK, cmd2, sessionId, aiAgentId);

    String cmd3 = "编写用户验证码登录功能测试用例";
    client.chat(AIChatType.WRITE_FUNC_CASE, cmd3, sessionId, aiAgentId);

    String cmd4 = "写一个查询用户性能测试脚本";
    client.chat(AIChatType.WRITE_ANGUS_SCRIPT, cmd4, sessionId, aiAgentId);
  }

}

