export interface DataRecordType {
  id: string,
  apiName: string,
  apiId: string,
  method: string,
  uri: string
}

export interface DataInfoType {
  id: string,
  apiId: string,
  method: string,
  protocol: string,
  queryParam: string,
  remote: string,
  requestBody: string,
  requestDate: string,
  requestHeaders: Record<string, any>,
  responseBody: string,
  responseDate: string,
  responseHeaders: Record<string, any>,
  responseSize: string,
  status: string,
  tenantId: string,
  uri: string,
  url: string
}
