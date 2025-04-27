import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Setting {
  constructor (prefix: string) {
    baseUrl = prefix + '/setting';
  }

  setSetting<T> (params: T): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}`, params);
  }

  getUserPreference (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/user/preference`);
  }

  updateUserPreference<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/user/preference`, params);
  }

  getSettingDetail (key: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${key}`);
  }

  updateTenantQuota (name: string, newQuota: number): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/tenant/quota/${name}/${newQuota}`);
  }

  getTenantQuotaApp (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/tenant/quota/app`);
  }

  searchTenantQuota<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/tenant/quota/search`, params);
  }

  updateTenantLocale<T> (params: T): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/tenant/locale`, params);
  }

  getTenantLocale (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/tenant/locale`);
  }

  getTenantSafetyConfig (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/tenant/security`);
  }

  updateTenantSafetyConfig<T> (params: T): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/tenant/security`, params);
  }

  getSignupInvitationCode (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/tenant/signup/invitationCode`);
  }
}
