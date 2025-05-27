package cloud.xcan.angus.core.gm.infra.remote.push;

import cloud.xcan.angus.remote.client.DynamicFeignClient;
import cloud.xcan.angus.security.FeignInnerApiAuthInterceptor;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class FeignRemoteFactory {

  private final Client client;
  private final Encoder encoder;
  private final Decoder decoder;
  private final Contract contract;
  private final FeignInnerApiAuthInterceptor feignInnerApiAuthInterceptor;

  private final Map<String, DynamicFeignClient> clients = new ConcurrentHashMap<>();

  public FeignRemoteFactory(Client client, Encoder encoder, Decoder decoder, Contract contract,
      FeignInnerApiAuthInterceptor feignInnerApiAuthInterceptor) {
    this.client = client;
    this.encoder = encoder;
    this.decoder = decoder;
    this.contract = contract;
    this.feignInnerApiAuthInterceptor = feignInnerApiAuthInterceptor;
  }

  public DynamicFeignClient dynamicClient(String url) {
    if (clients.containsKey(url)) {
      return clients.get(url);
    }
    DynamicFeignClient dynamicFeignClient = Feign.builder().client(client)
        .encoder(encoder).decoder(decoder).contract(contract)
        .requestInterceptor(feignInnerApiAuthInterceptor)
        .target(DynamicFeignClient.class, url);
    clients.put(url, dynamicFeignClient);
    return dynamicFeignClient;
  }
}
