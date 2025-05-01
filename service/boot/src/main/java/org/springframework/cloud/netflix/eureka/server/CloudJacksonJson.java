package org.springframework.cloud.netflix.eureka.server;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.LeaseInfo;
import com.netflix.discovery.converters.EurekaJacksonCodec;
import com.netflix.discovery.converters.wrappers.CodecWrappers;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Supplier;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class CloudJacksonJson extends CodecWrappers.LegacyJacksonJson {

  protected final CloudJacksonCodec codec = new CloudJacksonCodec();

  public CloudJacksonJson() {
  }

  public CloudJacksonCodec getCodec() {
    return this.codec;
  }

  public String codecName() {
    return CodecWrappers.getCodecName(CodecWrappers.LegacyJacksonJson.class);
  }

  public <T> String encode(T object) {
    return this.codec.writeToString(object);
  }

  public <T> void encode(T object, OutputStream outputStream) throws IOException {
    this.codec.writeTo(object, outputStream);
  }

  public <T> T decode(String textValue, Class<T> type) throws IOException {
    return this.codec.readValue(type, textValue);
  }

  public <T> T decode(InputStream inputStream, Class<T> type) throws IOException {
    return this.codec.readValue(type, inputStream);
  }

  static InstanceInfo updateIfNeeded(final InstanceInfo info) {
    if (info.getInstanceId() == null && info.getMetadata() != null) {
      String instanceId = (String) info.getMetadata().get("instanceId");
      if (StringUtils.hasText(instanceId)) {
        if (StringUtils.hasText(info.getHostName()) && !instanceId.startsWith(info.getHostName())) {
          String var10000 = info.getHostName();
          instanceId = var10000 + ":" + instanceId;
        }

        return (new InstanceInfo.Builder(info)).setInstanceId(instanceId).build();
      }
    }

    return info;
  }

  static class CloudJacksonCodec extends EurekaJacksonCodec {

    private static final Version VERSION = new Version(1, 1, 0, (String) null, (String) null,
        (String) null);

    CloudJacksonCodec() {
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(Include.NON_NULL);
      SimpleModule module = new SimpleModule("eureka1.x", VERSION);
      module.addSerializer(DataCenterInfo.class, new DataCenterInfoSerializer());
      module.addSerializer(InstanceInfo.class, new CloudInstanceInfoSerializer());
      module.addSerializer(Application.class, new ApplicationSerializer());
      module.addSerializer(Applications.class,
          new ApplicationsSerializer(this.getVersionDeltaKey(),
              this.getAppHashCodeKey()));
      module.addSerializer(InstanceRegistry.class, new InstanceRegistrySerializer());

      module.addDeserializer(LeaseInfo.class, new LeaseInfoDeserializer());
      module.addDeserializer(InstanceInfo.class, new CloudInstanceInfoDeserializer(mapper));
      module.addDeserializer(Application.class,
          new ApplicationDeserializer(mapper));
      module.addDeserializer(Applications.class,
          new ApplicationsDeserializer(mapper, this.getVersionDeltaKey(),
              this.getAppHashCodeKey()));
      mapper.registerModule(module);
      HashMap<Class<?>, Supplier<ObjectReader>> readers = new HashMap();
      readers.put(InstanceInfo.class, () -> {
        return mapper.reader().withType(InstanceInfo.class).withRootName("instance");
      });
      readers.put(Application.class, () -> {
        return mapper.reader().withType(Application.class).withRootName("application");
      });
      readers.put(Applications.class, () -> {
        return mapper.reader().withType(Applications.class).withRootName("applications");
      });
      this.setField("objectReaderByClass", readers);
      HashMap<Class<?>, ObjectWriter> writers = new HashMap();
      writers.put(InstanceInfo.class,
          mapper.writer().withType(InstanceInfo.class).withRootName("instance"));
      writers.put(Application.class,
          mapper.writer().withType(Application.class).withRootName("application"));
      writers.put(Applications.class,
          mapper.writer().withType(Applications.class).withRootName("applications"));
      this.setField("objectWriterByClass", writers);
      this.setField("mapper", mapper);
    }

    void setField(String name, Object value) {
      Field field = ReflectionUtils.findField(EurekaJacksonCodec.class, name);
      ReflectionUtils.makeAccessible(field);
      ReflectionUtils.setField(field, this, value);
    }
  }

  static class CloudInstanceInfoDeserializer extends EurekaJacksonCodec.InstanceInfoDeserializer {

    protected CloudInstanceInfoDeserializer(ObjectMapper mapper) {
      super(mapper);
    }

    public InstanceInfo deserialize(JsonParser jp, DeserializationContext context)
        throws IOException {
      InstanceInfo info = super.deserialize(jp, context);
      InstanceInfo updated = CloudJacksonJson.updateIfNeeded(info);
      return updated;
    }
  }

  static class CloudInstanceInfoSerializer extends EurekaJacksonCodec.InstanceInfoSerializer {

    CloudInstanceInfoSerializer() {
    }

    public void serialize(final InstanceInfo info, JsonGenerator jgen, SerializerProvider provider)
        throws IOException {
      InstanceInfo updated = CloudJacksonJson.updateIfNeeded(info);
      super.serialize(updated, jgen, provider);
    }
  }

  // Extension Class
  public static class InstanceRegistrySerializer extends JsonSerializer<InstanceRegistry> {

    public InstanceRegistrySerializer() {
    }

    @Override
    public void serialize(InstanceRegistry value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException {
      jgen.writeStartObject();
      jgen.writeStringField("leaseExpirationEnabled",
          String.valueOf(value.isLeaseExpirationEnabled()));
      jgen.writeObjectField("numOfRenewsPerMinThreshold", value.getNumOfRenewsPerMinThreshold());
      jgen.writeObjectField("numOfRenewsInLastMin", value.getNumOfRenewsInLastMin());
      jgen.writeObjectField("selfPreservationModeEnabled", value.isSelfPreservationModeEnabled());
      jgen.writeEndObject();
    }

  }
}
