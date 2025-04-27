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
  defaults: string;
  min: string;
  max: string;
  tenantId: string;
  tenantName: string;
}
