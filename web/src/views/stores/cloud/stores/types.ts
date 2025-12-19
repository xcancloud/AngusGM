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
  type: { value: string, message: string };
  version: string;
  charge: boolean;
  creator: string;
  createdDate: string;
  price: {
    finalPrice: string;
    totalSpecPrice: string;
  },
  pricingUrl: string;
  onlineDate: string;
  star?: boolean;
  applyEditionTypes?: { value: string, message: string }[]
}

const goodsPriTypes = ['DATACENTER', 'ENTERPRISE', 'COMMUNITY'];
const goodsCloudTypes = ['CLOUD_SERVICE'];

export const isPriGoods = (goods: Goods): boolean => {
  return (goods.applyEditionTypes || []).some(i => goodsPriTypes.includes(i.value));
};
export const isCloudGoods = (goods: Goods): boolean => {
  return (goods.applyEditionTypes || []).some(i => goodsCloudTypes.includes(i.value));
};

export const multipleEditionTypes = (goods: Goods): boolean => {
  return (goods.applyEditionTypes || []).filter(i => goodsPriTypes.includes(i.value)).length > 1;
};

export const downloadEditionTypes = (goods: Goods) => {
  return (goods.applyEditionTypes || []).filter(i => goodsPriTypes.includes(i.value));
};

export const goodsTypeColor = {
  PLUGIN: 'text-orange-text',
  PLUGIN_APPLICATION: 'text-orange-text',
  APPLICATION: 'text-purple-text',
  RESOURCE_QUOTA: 'text-green-text'
};
