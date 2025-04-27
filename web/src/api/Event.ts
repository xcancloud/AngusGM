import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Event {
  constructor (prefix: string) {
    baseUrl = prefix + '/event';
  }

  searchEventList (params: {
    pageNo: number,
    pageSize: number;
    orderSort?: string;
    orderBy?: string;
    filters?: Record<string, string>[]
  }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/search`, params);
  }

  addTemplate<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/template`, params);
  }

  replaceTemplate<T> (params: T): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/template`, params);
  }

  deleteTemple (id: string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/template/${id}`);
  }

  getTemplateDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/template/${id}`);
  }

  getCurrentTemplates (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/template/${id}/current`);
  }

  searchTemplate (params: {
    pageNo: number,
    pageSize: number,
    filters: Record<string, string>[]
  }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/template/search`, params);
  }

  addChannel (params: { address: string; name: string; channelType: string }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/channel`, params);
  }

  replaceChannel (params: { address: string, id: string, name: string }): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/channel`, params);
  }

  replaceTemplateChannel (params: { id: string, channelIds: string[] }): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/template/${params.id}/channel`, params);
  }

  testChannelConfig (params: { address: string, channelType: string, name: string }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/channel/test`, params);
  }

  deleteChannel (id: string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/channel/${id}`);
  }

  getEventChannel (eventCode: string, eKey: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${eventCode}/${eKey}/channel`);
  }

  getTypeChannel (channelType: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/channel/type/${channelType}`);
  }
}
