export type DetailType = {
  id: string | undefined,
  name: string | undefined,
  code: string | undefined,
  appId: string | undefined,
  appName: string | undefined,
  createdByName: string | undefined,
  createdDate: string | undefined,
  type: { value: 'OPEN_GRANT' | 'PRE_DEFINED' | 'USER_DEFINED' | undefined, message: string | undefined },
  enabled: boolean,
  description: string | undefined
}
