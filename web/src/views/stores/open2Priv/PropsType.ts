export interface Goods {
  id: string;
  name: string;
  hot: boolean;
  introduction: string;
  tags?: string[];
  editionType: { value: string, message: string },
  iconUrl: string;
  starNum: number;
  goodsId: string;
  videos: { name: string, url: string }[];
  features?: string[];
  bannerUrls?: string[];
  productType: { value: string, message: string };
  version: string;
  charge: boolean;
  createdByName: string;
  createdDate: string;
  price: {
    finalPrice: string;
    totalSpecPrice: string;
  },
  pricingUrl: string;
  onlineDate: string;
  star?: boolean;
  applyEditionTypes: { value: string, message: string }[];
  allowComment: boolean;
}

const goodsPriTypes = ['DATACENTER', 'ENTERPRISE', 'COMMUNITY'];
// const goodsCloudTypes = ['CLOUD_SERVICE', 'GENERIC'];

// 是否为私有化商品
export const isPriGoods = (goods: Goods): boolean => {
  return (goods.applyEditionTypes || []).some(i => goodsPriTypes.includes(i.value));
};

export const goodsTypeColor = {
  PLUGIN: 'text-orange-text',
  PLUGIN_APPLICATION: 'text-orange-text',
  APPLICATION: 'text-purple-text',
  RESOURCE_QUOTA: 'text-green-text'
};

export const getEnumMessages = (enums: { value: string, message: string }[] = []): string => {
  return (enums || []).map(i => i.message).join('、');
};
