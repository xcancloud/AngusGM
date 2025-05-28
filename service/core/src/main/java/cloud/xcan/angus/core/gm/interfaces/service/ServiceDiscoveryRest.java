package cloud.xcan.angus.core.gm.interfaces.service;

import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ServiceDiscovery", description = "Provides a unified entry for querying AngusDiscovery (Service Registration Center) services and instances information.")
@RestController
@RequestMapping("/api/v1/service")
public class ServiceDiscoveryRest {

  @Resource
  private DiscoveryClient discoveryClient;

  @Operation(summary = "Query the services of AngusDiscovery.", operationId = "discovery:service:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/discovery")
  public ApiLocaleResult<List<String>> services() {
    return ApiLocaleResult.success(discoveryClient.getServices());
  }

  @Operation(summary = "Query the instances of AngusDiscovery service.", operationId = "discovery:instances:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{serviceCode}/discovery/instances")
  public ApiLocaleResult<List<String>> instances(@PathVariable String serviceCode) {
    return ApiLocaleResult.success(
        discoveryClient.getInstances(serviceCode).stream().map(ServiceInstance::getInstanceId)
            .collect(Collectors.toList()));
  }
}
