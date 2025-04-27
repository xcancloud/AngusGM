import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Email {
  constructor (prefix: string) {
    baseUrl = prefix + '/email';
  }

  getEmailList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`, params);
  }

  getEmailDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}`);
  }

  addServer<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/server`, params);
  }

  replaceServer<T> (params: T): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/server`, params);
  }

  updateServer<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/server/enabled`, params);
  }

  deleteServer<T> (params: T): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/server`, params);
  }

  testServerConfig<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/server/test`, params);
  }

  getServerCheck (params: { protocol: string }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/server/check`, params);
  }

  getServerDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/server/${id}`);
  }

  getServerList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/server`, params);
  }
}
