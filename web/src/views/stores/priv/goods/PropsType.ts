type EnumType = {
  value: string;
  message: string;
}

type Servers = {
  ipAddress: string;
  macAddress: string;
  cpuSerialNumber: string;
  mainBoardSerial: string;
  machineCode: string;
}

export interface PrivateGoods {
  id: string;
  goodsId: string;
  editionType: EnumType;
  type: EnumType;
  name: string;
  code: string;
  version: string;
  iconText: string;
  tags: string[];
  introduction: string;
  charge: false,
  orderNo: string;
  purchaseByName: string;
  purchaseDate: string;
  issuer: string;
  upgradeFromGoodsId: string;
  upgradeFromCode: string;
  upgradeFromVersion: string;
  servers: Servers
  installType: EnumType;
  installStatus: EnumType;
  installMessage: string;
  online: boolean;
  tenantId: string;
  tenantName: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  uninstallable: boolean;
}
