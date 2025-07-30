import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class Auth {
  constructor (prefix: string) {
    baseUrl = prefix + '/auth';
  }

  updateUserPassword<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/user/password`, params);
  }

  updateCurrentPassword<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/user/password/current`, params);
  }

  getUserAppFunctionTree (userId: string, appIdOrCode: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/user/${userId}/app/${appIdOrCode}/func/tree`, { joinApi: true });
  }

  getTenantDefaultPolicy (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/tenant/app/default`);
  }

  patchAppDefaultPolicy (appId: string, policyId: string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/tenant/app/${appId}/policy/default/${policyId}`);
  }

  addPolicy<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/policy`, params);
  }

  updatePolicy<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/policy`, params);
  }

  togglePolicyEnabled (params: { enabled: boolean, id: string }[]): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/policy/enabled`, params);
  }

  getPolicyDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/policy/${id}`);
  }

  deletePolicy (ids: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/policy`, { ids });
  }

  getPolicyList (params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/policy`, params);
  }

  getPolicyFunctionsById (policyId: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/policy/${policyId}/func/tree`);
  }

  addUserPolicy (userId: string, policyIds: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/user/${userId}/policy`, policyIds);
  }

  deleteUserPolicy (userId: string, policyIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/user/${userId}/policy`, { policyIds: policyIds });
  }

  getUserPolicy<T> (userId: string, params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/user/${userId}/policy/associated`, params);
  }

  getUserUnAuthPolicy (userId: string, params = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/user/${userId}/unauth/policy`, params);
  }

  getGroupPolicy<T> (groupId: string, params?: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/group/${groupId}/policy`, params);
  }

  addGroupPolicy (groupId: string, policyIds: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/group/${groupId}/policy`, policyIds);
  }

  deleteGroupPolicy (groupId: string, policyIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/group/${groupId}/policy`, { policyIds: policyIds });
  }

  getPolicyGroup<T> (groupId: string, params?: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/group/${groupId}/policy/associated`, params);
  }

  getGroupUnAuthPolicy (groupId: string, params = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/group/${groupId}/unauth/policy`, params);
  }

  getDeptPolicy (deptId: string, params, config = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/dept/${deptId}/policy`, params, config);
  }

  getDeptUnAuthPolicy (deptId: string, params = {}): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/dept/${deptId}/unauth/policy`, params);
  }

  addPolicyByDept (deptId: string, policyIds: string[], config = {}): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/dept/${deptId}/policy`, policyIds, config);
  }

  deletePolicyByDept (deptId: string, policyIds: string[], config = {}): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/dept/${deptId}/policy`, { policyIds }, config);
  }

  deleteAppDefaultPolicy (appId: string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/tenant/app/${appId}/policy/default`);
  }

  addPolicyUser (policyId: string, userIds: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/policy/${policyId}/user`, userIds);
  }

  getPolicyUsers (policyId: string, params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/policy/${policyId}/user`, params);
  }

  deletePolicyUsers (policyId: string, userIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/policy/${policyId}/user`, { userIds });
  }

  addPolicyGroup (policyId: string, groupIds: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/policy/${policyId}/group`, groupIds);
  }

  getPolicyGroups (policyId: string, params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/policy/${policyId}/group`, params);
  }

  deletePolicyGroups (policyId: string, groupIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/policy/${policyId}/group`, { groupIds });
  }

  addPolicyDept (policyId: string, deptIds: string[]): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/policy/${policyId}/dept`, deptIds);
  }

  getPolicyDept (policyId: string, params): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/policy/${policyId}/dept`, params);
  }

  deletePolicyDept (policyId: string, deptIds: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/policy/${policyId}/dept`, { deptIds });
  }

  updateUserInitPassword (params: { newPassword: string|undefined }) : Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/user/password/init/current`, params);
  }
}
