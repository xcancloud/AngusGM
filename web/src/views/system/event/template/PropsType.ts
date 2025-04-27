export interface SaveSetting {
  pkey: { value: string, message: string },
  receiveIds: string[]
}

export interface EventConfigList {
  id: string;
  ekey: string;
  bizKey: string;
  bigBizKey: string;
  eventCode: string;
  eventName: string;
  eventType: undefined | string;
  allowedChannelTypes: string[] | { value: string, message: string }[];
  pushSetting: SaveSetting[];
}
