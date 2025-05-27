package cloud.xcan.angus.core.gm.infra.remote.push;

import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import feign.RequestLine;
import java.net.URI;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "thirdPushClient")
public interface ThirdPushClient {

  @RequestLine("POST")
  Map<?, ?> dingTalk(URI uri, @RequestBody DingTalkRobotRequest request);

  @RequestLine("POST")
  Map<?, ?> wechat(URI uri, @RequestBody WeChatRobotRequest request);

  @RequestLine("POST")
  void webhook(URI uri, @RequestBody EventPush dto);

}
