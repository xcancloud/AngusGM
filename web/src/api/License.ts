import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class License {
  constructor (prefix: string) {
    baseUrl = prefix + '/license';
  }

  getLicenseInPriv<T> (params: T, configs?: Record<string, any>): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`, params, configs);
  }

  getLicenseInCloud<T> (params: T, configs?: Record<string, any>): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/release`, params, configs);
  }
}
