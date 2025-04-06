package cloud.xcan.angus.core.gm.infra.ai;

public enum AIChatType {
  WRITE_BACKLOG,
  SPLIT_SUB_TASK,
  WRITE_FUNC_CASE,
  WRITE_ANGUS_SCRIPT;

  public boolean isYamlFormat() {
    return this.equals(WRITE_ANGUS_SCRIPT);
  }
}
