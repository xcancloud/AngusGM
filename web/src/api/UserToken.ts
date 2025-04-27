import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class UserToken {
  constructor (prefix: string) {
    baseUrl = prefix + '/user';
  }

  getToken (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/token`);
  }

  addToken (params: Record<string, unknown>): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/token`, params);
  }

  deleteToken (params: Record<string, unknown>): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/token`, params);
  }

  getTokenValue (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/token/${id}/value`);
  }
}
