import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class User {
  constructor (prefix: string) {
    baseUrl = prefix + '/user';
  }

  getUserList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`, params);
  }

  addUser<T> (params: T): Promise<[Error | null, any]> {
    return http.post(baseUrl, params);
  }

  updateUser<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(baseUrl, params);
  }

  toggleUserEnabled (params: { id: string, enabled: boolean }[]): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/enabled`, params);
  }

  deleteUser (ids: string[]): Promise<[Error | null, any]> {
    return http.del(baseUrl, { ids });
  }

  updateUserSysAdmin (params: { id: string, sysAdmin: boolean }): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/sysadmin`, params);
  }

  getUserDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}`, {});
  }

  getUserDept<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/dept`, params);
  }

  addUserDept<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${id}/dept`, params);
  }

  deleteUserDept<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}/dept`, params);
  }

  getUserGroup<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/group`, params);
  }

  addUserGroup<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${id}/group`, params);
  }

  deleteUserGroup (id: string, groupIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}/group`, { groupIds: groupIds });
  }

  getUserTag<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}/tag`, params);
  }

  addUserTag<T> (id: string, params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${id}/tag`, params);
  }

  deleteUserTag (id: string, tagIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}/tag`, { tagIds: tagIds });
  }

  toggleUserLocked<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/locked`, params);
  }

  getCurrentUser (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/current`);
  }

  updateCurrentUser<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/current`, params);
  }

  updateCurrentMobile<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/current/mobile`, params);
  }

  updateCurrentEmail<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/current/email`, params);
  }

  senEmailCode<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/current/email/send`, params);
  }

  sendSmsCode<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/current/sms/send`, params);
  }

  unbind (params: { type: 'WECHAT' | 'GITHUB' | 'GOOGLE' }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/social/unbind`, params);
  }
}
