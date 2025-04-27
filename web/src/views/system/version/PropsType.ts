export type InstallVesion = {
  goodsId: string;
  goodsType: {
    value: string;
    message: string;
  },
  goodsCode: string;
  goodsName: string;
  editionType: {
    value: string;
    message: string;
  },
  provider: string;
  issuer: string;
  holderId: string;
  holder: string;
  licenseNo: string;
  info: string;
  signature: string;
  issuedDate: string;
  beginDate: string;
  endDate: string;
  goodsVersion: string;
}

export type UpgradeableVersion = {
  goodsId: string;
  name: string;
  code: string;
  version: string;
  iconUrl: string;
  introduction: string;
  information: string;
  features: string[];
  releaseDate: string;
  upgradeable: boolean | null;
}
