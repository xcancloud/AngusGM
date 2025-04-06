package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static io.swagger.v3.oas.models.extension.ExtensionKey.RESOURCE_NAME_KEY;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.spec.http.HttpMethod;
import cloud.xcan.angus.spec.http.HttpSender.Response;
import cloud.xcan.angus.spec.http.HttpUrlConnectionSender;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.springframework.cloud.client.ServiceInstance;

public class ApiConverter {

  public static void parseSwaggerDocs(List<Api> apis, Service serviceDb,
      ServiceInstance serviceInstance, HttpUrlConnectionSender httpSender, String swaggerApiUrl,
      ApiType api) throws Throwable {
    String syncApiUrl = serviceInstance.getUri() + swaggerApiUrl;
    Response apiResponses = httpSender.get(syncApiUrl).send();
    if (nonNull(apiResponses) && apiResponses.isSuccessful()) {
      parseApi(apis, apiResponses.body(), serviceDb, api);
    }
  }

  /**
   * Parse api in swagger
   */
  public static void parseApi(List<Api> apis, String apiContent, Service serviceDb,
      ApiType apiType) {
    OpenAPI openAPI = new OpenAPIParser().readContents(
        apiContent, null, null).getOpenAPI();
    if (Objects.isNull(openAPI)) {
      throw new IllegalStateException("Parse OpenAPI is null");
    }
    if (openAPI.getPaths().isEmpty()) {
      return;
    }
    for (Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
      String key = entry.getKey();
      PathItem pathItem = entry.getValue();
      // Process Get request
      Operation getOperation = pathItem.getGet();
      if (nonNull(getOperation)) {
        apis.add(assembleParsedApi(serviceDb, apiType, key, getOperation,
            stringSafe(getOperation.getSummary()), HttpMethod.GET));
      }

      // Process Post request
      Operation postOperation = pathItem.getPost();
      if (nonNull(postOperation)) {
        apis.add(assembleParsedApi(serviceDb, apiType, key, postOperation,
            stringSafe(postOperation.getSummary()), HttpMethod.POST));
      }

      // Process Put request
      Operation putOperation = pathItem.getPut();
      if (nonNull(putOperation)) {
        apis.add(assembleParsedApi(serviceDb, apiType, key, putOperation,
            stringSafe(putOperation.getSummary()), HttpMethod.PUT));
      }

      // Process Patch request
      Operation patchOperation = pathItem.getPatch();
      if (nonNull(patchOperation)) {
        apis.add(assembleParsedApi(serviceDb, apiType, key, patchOperation,
            stringSafe(patchOperation.getSummary()), HttpMethod.PATCH));
      }

      // Process Delete request
      Operation deleteOperation = pathItem.getDelete();
      if (nonNull(deleteOperation)) {
        apis.add(assembleParsedApi(serviceDb, apiType, key, deleteOperation,
            stringSafe(deleteOperation.getSummary()), HttpMethod.DELETE));
      }

      // Process Head request
      Operation headOperation = pathItem.getHead();
      if (nonNull(headOperation)) {
        apis.add(assembleParsedApi(serviceDb, apiType, key, headOperation,
            headOperation.getDescription(), HttpMethod.HEAD));
      }

      // Process Trace requests
      Operation traceOperation = pathItem.getTrace();
      if (nonNull(traceOperation)) {
        apis.add(assembleParsedApi(serviceDb, apiType, key, traceOperation,
            traceOperation.getDescription(), HttpMethod.TRACE));
      }
    }
  }

  public static Api assembleParsedApi(Service serviceDb, ApiType apiType, String key,
      Operation operation, String description, HttpMethod method) {
    return getBaseApi(serviceDb, key, apiType, safeResourceName(operation, operation.getTags()))
        .setCode(safeServiceCode(operation.getOperationId()))
        .setScopes(safeScope(operation.getSecurity()))
        .setName(safeServiceName(operation.getSummary()))
        .setDescription(description)
        .setMethod(method);
  }

  public static Api getBaseApi(Service serviceDb, String uri, ApiType apiType,
      String resourceName) {
    return new Api().setResourceName(resourceName)
        .setUri(uri)/*.setEnabled(true)*/
        .setServiceId(serviceDb.getId())
        .setServiceCode(serviceDb.getCode())
        .setServiceName(serviceDb.getName())
        .setType(apiType).setSync(true).setSwaggerDeleted(false);
  }

  public static String safeServiceCode(String operationId) {
    return isNull(operationId) ? ""
        : operationId.length() > MAX_CODE_LENGTH_X2 ? operationId.substring(0,
            MAX_CODE_LENGTH_X2) : operationId;
  }

  public static Set<String> safeScope(List<SecurityRequirement> securities) {
    if (isEmpty(securities)) {
      return null;
    }
    Set<String> scopes = new HashSet<>();
    for (SecurityRequirement security : securities) {
      if (isNotEmpty(security)) {
        for (List<String> value : security.values()) {
          scopes.addAll(value);
        }
        return scopes;
      }
    }
    return null;
  }

  public static String safeServiceName(String summary) {
    return isNull(summary) ? "" : summary.length() > MAX_NAME_LENGTH_X2
        ? summary.substring(0, MAX_NAME_LENGTH_X2) : summary;
  }

  public static String safeResourceName(Operation operation, List<String> tags) {
    if (isNotEmpty(operation.getExtensions())
        && operation.getExtensions().containsKey(RESOURCE_NAME_KEY)) {
      return operation.getExtensions().get(RESOURCE_NAME_KEY).toString();
    }
    return isEmpty(tags) ? "" : tags.stream().findFirst().orElse("");
  }

}
