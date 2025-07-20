import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class CloudStore {
  constructor (prefix: string) {
    baseUrl = prefix + '/store';
  }

  uninstallPlugin (params: { goodsId: string }): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/installation/plugin/offlineUninstall`, params);
  }

  offlineInstall (file: FormData, config: Record<string, any>): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/installation/plugin/offlineInstall`, file, config);
  }

  offlineUpgrade (file: FormData, config: Record<string, any>): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/installation/plugin/offlineUpgrade`, file, config);
  }

  getInstallationDetail (recordId: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/installation/${recordId}`);
  }

  // id <=> goodsId
  getCloudGoodsDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/cloud/goods/${id}`);
  }

  starGoods (params: { star: boolean, goodsId: string }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/star`, params);
  }

  starCloudGoodsInPriv (params: { star: boolean, goodsId: string }, _baseUrl = baseUrl): Promise<[Error | null, any]> {
    return http.post(`${_baseUrl}/openapi2p/v1/store/star`, params);
  }

  onlineUpgrade (params: { upgradeToGoodsId: string }): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/installation/plugin/onlineUpgrade`, {
      ...params
    });
  }

  onlineUpgradeInPriv (params: { upgradeToGoodsId: string }, _baseUrl = baseUrl): Promise<[Error | null, any]> {
    return http.put(`${_baseUrl}/openapi2p/v1/installation/plugin/onlineUpgrade`, {
      ...params
    });
  }

  onlineInstall (goodsId: string): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/installation/plugin/onlineInstall`, { goodsId }, {
      paramsType: true
    });
  }

  onlineInstallInPriv (goodsId: string, _baseUrl = baseUrl): Promise<[Error | null, any]> {
    return http.post(`${_baseUrl}/installation/plugin/onlineInstall`, { goodsId }, {
      paramsType: true
    });
  }

  onlineUninstall (goodsId: string): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/installation/plugin/onlineUninstall`, { goodsId });
  }
}
