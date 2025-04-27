export interface ReceiveObjectDataType {
  id: string,
  avatar: string,
  name: string,
  mobile: string
}

export interface ContentType {
  fullName?: string | undefined,
  id?: string | undefined,
  readNum?: string | undefined,
  receiveObjectData?: Array<ReceiveObjectDataType>,
  receiveObjectType?: Record<string, any>,
  receiveObjectDataName?: string,
  receiveObjectDataLength?: number,
  timingDate?: string,
  sentNum?: string,
  status?: string,
  title?: string,
  userId?: string,
  content?: string,
  sentType?: string,
  receiveType?: string,
}
