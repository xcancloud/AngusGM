import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class Dept {
  constructor (prefix: string) {
    baseUrl = prefix + '/dept';
  }

  addDept (params) {
    return http.post(`${baseUrl}`, params);
  }

  updateDept (params) {
    return http.patch(`${baseUrl}`, params);
  }

  deleteDept (params: { ids: string[] }) {
    const { ...param } = params;
    return http.del(`${baseUrl}`, param);
  }

  getDeptDetail (id: string) {
    return http.get(`${baseUrl}/${id}`, {});
  }

  getNavigationByDeptId (params: { id: string }) {
    return http.get(`${baseUrl}/${params.id}/navigation`, {}, { silence: false });
  }

  getDeptList (params: unknown) {
    return http.get(`${baseUrl}`, params);
  }

  getDeptCount (id: string) {
    return http.get(`${baseUrl}/${id}/count`, {});
  }

  createDeptUser (id: string, userIds: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${id}/user`, userIds);
  }

  deleteDeptUser (id: string, userIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}/user`, { ids: userIds });
  }

  getDeptUsers<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/user`, params);
  }

  addDeptTags (params: { id: string, ids: string[] }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${params.id}/tag`, params.ids);
  }

  deleteDeptTag (params: { id: string, ids: string[] }) {
    return http.del(`${baseUrl}/${params.id}/tag`, { tagIds: params.ids });
  }

  getDeptTags (id: string, params) {
    return http.get(`${baseUrl}/${id}/tag`, params);
  }
}
