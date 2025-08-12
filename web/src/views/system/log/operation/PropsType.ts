// Operation log record type
export type OperationLogRecord = {
  id: string;
  fullName: string;
  avatar?: string;
  description: string;
  resource: {
    message: string;
  };
  resourceName: string;
  resourceId: string;
  optDate: string;
}
