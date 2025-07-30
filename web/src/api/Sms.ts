import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class Sms {
  constructor (prefix: string) {
    baseUrl = prefix + '/sms';
  }

  getSmsList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`, params);
  }

  getSmsDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}`);
  }

  updateChannelConfig<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/channel`, params);
  }

  toggleChannelEnabled (params: { id: string, enabled: boolean }): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/channel/enabled`, params);
  }

  loadSendTest (params: { channelId: string, mobiles: string[] }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/channel/test`, params);
  }

  getChannels (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/channel`);
  }

  updateTemplate<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/template/${id}`, params);
  }

  getTemplates (params: { channelId: string | undefined, language?: string }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/template`, params);
  }
}
