export interface SaveSetting {
  pkey: { value: string, message: string },
  receiveIds: string[]
}

export interface EventRecord {
  description: string,
  ekey: string,
  errMsg: string,
  eventCode: string,
  eventContent: string,
  execNo: string,
  id: string,
  pushStatus: { value: string, message: string }
  tenantId: string,
  tenantName: string,
  triggerTime: string,
  type: { value: string, message: string }
}
