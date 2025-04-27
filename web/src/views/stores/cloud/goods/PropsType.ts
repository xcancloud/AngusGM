export type Enum = {
  value: string;
  message: string;
}

export interface Goods {
  id: string;
  tenantId: string;
  tenantName: string;
  goodsEditionType: Enum;
  goodsType: Enum;
  goodsId: string;
  goodsCode: string;
  goodsName: string;
  goodsVersion: string;
  goodsIconUrl: string;
  goodsIntroduction: string;
  charge: boolean;
  orderId: string;
  orderNo: string;
  expired: boolean;
  expiredDate: string;
  purchaseBy: string;
  purchaseByName: string;
  purchaseDate: string;
  applyEditionTypes: { value: string, message: string }[];
}

const goodsPriTypes = ['DATACENTER', 'ENTERPRISE', 'COMMUNITY'];
const goodsCloudTypes = ['CLOUD_SERVICE'];

// 是否为私有化商品
export const isPriGoods = (applyEditionTypes: { value: string, message: string }[] = []): boolean => {
  return (applyEditionTypes || []).some(i => goodsPriTypes.includes(i.value));
};

export const isCloudGoods = (applyEditionTypes: { value: string, message: string }[] = []): boolean => {
  return (applyEditionTypes || []).some(i => goodsCloudTypes.includes(i.value));
};

export const multipleEditionTypes = (goods: Goods): boolean => {
  return (goods.applyEditionTypes || []).filter(i => goodsPriTypes.includes(i.value)).length > 1;
};

export const downloadEditionTypes = (goods: Goods) => {
  return (goods.applyEditionTypes || []).filter(i => goodsPriTypes.includes(i.value));
};
