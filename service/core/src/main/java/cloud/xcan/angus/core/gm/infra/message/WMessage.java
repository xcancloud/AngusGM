package cloud.xcan.angus.core.gm.infra.message;

import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.spec.experimental.ValueObject;
import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class WMessage<T extends ValueObject<T>> extends ValueObjectSupport<T> {

  public String id;

  public String bizKey;

  public ReceiveObjectType receiveObjectType;

  public PushMediaType pushMediaType;

  public abstract T getContent();

  public WMessage() {
  }

  public WMessage(String id, String bizKey, ReceiveObjectType receiveObjectType,
      PushMediaType pushMediaType) {
    this.id = id;
    this.bizKey = bizKey;
    this.receiveObjectType = receiveObjectType;
    this.pushMediaType = pushMediaType;
  }

  @Override
  public T copy() {
    return null;
  }
}
