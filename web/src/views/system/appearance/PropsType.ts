export type AppInfo = {
  id: string;
  code: string;
  name: string;
  showName: string;
  icon: string;
  type: {
    value: string;
    message: string;
  },
  authCtrl: true,
  enabled: true,
  url: string;
  sequence: string;
  version: string;
  openStage: {
    value: string;
    message: string;
  },
  createdBy: string;
  createdByName: string;
  createdDate: string;
  showInfo?: boolean;
  isEditName?: boolean;
  isEditUrl?: boolean;
  isUpload?: boolean;
  loading?: boolean;
}
