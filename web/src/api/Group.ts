import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Group {
  constructor (prefix: string) {
    baseUrl = prefix + '/group';
  }

  addGroup<T> (params: T): Promise<[Error | null, any]> {
    return http.post(baseUrl, params);
  }

  replaceGroup<T> (params: T): Promise<[Error | null, any]> {
    return http.put(baseUrl, params);
  }

  updateGroup<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(baseUrl, params);
  }

  toggleGroupEnabled (params: { id: string, enabled: boolean }[]): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/enabled`, params);
  }

  deleteGroup (ids: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}`, { ids });
  }

  searchGroups<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/search`, params);
  }

  getGroupDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}`, {});
  }

  addGroupUser (id: string, userIds: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${id}/user`, userIds);
  }

  deleteGroupUser (id: string, userIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}/user`, { ids: userIds });
  }

  getGroupUser<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/user`, params);
  }

  getGroupTag<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/tag`, params);
  }
}
