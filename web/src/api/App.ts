import { GM, http } from '@xcan-angus/infra';

let baseUrl: string;
export default class App {
  constructor (prefix: string) {
    baseUrl = prefix + '/app';
  }

  updateAppSite (params: { id: string, icon?: string, showName?: string, url?: string }): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/site`, params);
  }

  addDeptAuth (appId: string, params: { orgIds: string[], policyIds: string[] }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${appId}/dept/policy/auth`, params);
  }

  addUserAuth (appId: string, params: { orgIds: string[], policyIds: string[] }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${appId}/user/policy/auth`, params);
  }

  addGroupAuth (appId: string, params: { orgIds: string[], policyIds: string[] }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/${appId}/group/policy/auth`, params);
  }

  getUserAuthApp (userId: string): Promise<[Error | null, any]> {
    return http.get(`${GM}/org/User/${userId}/auth/app?joinPolicy=false`);
  }

  getAppByCode (appCode: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${appCode}`);
  }
}
