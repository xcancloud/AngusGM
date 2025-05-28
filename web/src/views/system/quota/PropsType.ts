export type Quota = {
  id: string;
  appCode: string;
  serviceCode: string;
  name: {
    value: string;
    message: string;
  },
  allowChange: string;
  calcRemaining: string;
  quota: string;
  default0: string;
  min: string;
  max: string;
  tenantId: string;
  tenantName: string;
}
