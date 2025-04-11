package cloud.xcan.angus.core.gm.infra.remote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
public class WeChatRobotRequest {

  private String msgtype = "text";

  private Text text;

  private MarkDown markdown;

  public void setText(Text text) {
    this.text = text;
    this.msgtype = text.getMsgType();
  }

  public void setMarkdown(MarkDown markdown) {
    this.markdown = markdown;
    this.msgtype = markdown.getMsgType();
  }

  @Setter
  @Getter
  @AllArgsConstructor
  public static class Text {

    private String content;

    public String getMsgType() {
      return "text";
    }

  }

  @Setter
  @Getter
  @AllArgsConstructor
  public static class MarkDown {

    private String content;

    public String getMsgType() {
      return "markdown";
    }
  }
}
