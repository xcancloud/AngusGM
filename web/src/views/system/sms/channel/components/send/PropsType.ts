export const enum SendState {
  SENDSUCCESS = 'sentSuccessfully',
  SENDFAILED = 'sendFailed',
  SENDING = 'sending',
}

export interface Aisle {
  accessKeyId: string,
  accessKeySecret: string,
  endpoint: string,
  enabled: boolean,
  id: string,
  logo: string,
  name: string,
  loading: boolean,
  display: boolean,
  visible: boolean
}
