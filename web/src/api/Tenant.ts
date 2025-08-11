import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class Tenant {
  constructor (prefix: string) {
    baseUrl = prefix + '/tenant';
  }

  getTenantDetail (tenantId: number): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${tenantId}`);
  }

  submitCertAudit<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/cert/audit/submit`, params);
  }

  getCertAudit (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/cert/audit`);
  }

  getSecurity (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/security`);
  }

  updateLocale<T> (params: T): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/setting/locale`, params);
  }

  getLocale (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/setting/locale`);
  }

  sendSignCancelSms (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/sign/cancel/sms/send`, {});
  }

  confirmSignCancelSms (code: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/sign/cancel/sms/confirm`, { verificationCode: code });
  }

  revokeSignCancel (): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/sign/cancel/revoke`);
  }
}
