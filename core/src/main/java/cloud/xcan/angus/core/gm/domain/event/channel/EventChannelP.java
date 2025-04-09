package cloud.xcan.angus.core.gm.domain.event.channel;

public interface EventChannelP {

  Long getId();

  String getType();

  String getName();

  String getAddress();

  Long getTemplateId();

}
