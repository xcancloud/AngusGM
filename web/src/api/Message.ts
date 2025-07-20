import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Message {
  constructor (prefix: string) {
    baseUrl = prefix + '/message';
  }

  sendMessage<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}`, params);
  }

  getMessageDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}`);
  }

  getMessageStatusCount (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/status/count`);
  }

  searchMessageList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`, params);
  }

  deleteCurrentMessages (ids: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/current`, { ids });
  }

  markCurrentMessageRead (ids: string[]): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/read/current`, { ids }, { paramsType: true });
  }

  getCurrentMessageDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/current`);
  }

  getCurrentMessages (params: { pageNo: number, pageSize: number, read?: boolean }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/current`, params);
  }
}
