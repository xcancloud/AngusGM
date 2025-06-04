import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class UserLog {
  constructor (prefix: string) {
    baseUrl = prefix + '/log';
  }

  searchOperationLog (params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/operation/search`, params);
  }

  getApiLog (params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/api`, params);
  }

  getApiLogDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/api/${id}`);
  }
}
