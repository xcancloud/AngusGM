import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class SystemToken {
  constructor (prefix: string) {
    baseUrl = prefix + '/system';
  }

  addToken<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/token`, params);
  }

  deleteToken<T> (params: T): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/token`, params);
  }

  getTokens (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/token`);
  }

  getTokenAuth (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/token/${id}/auth`);
  }

  getTokenValue (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/token/${id}/value`);
  }
}
