/**
 * Application information interface for appearance management
 * Contains core app data and UI state management properties
 */
export interface AppInfo {
  // Core application properties
  id: number;
  code: string;
  name: string;
  showName: string;
  icon: string;
  url: string;
  sequence: string;
  version: string;

  // Application metadata
  type: {
    value: string;
    message: string;
  };
  authCtrl: boolean;
  enabled: boolean;
  openStage: {
    value: string;
    message: string;
  };

  // Creation information
  createdBy: string;
  createdByName: string;
  createdDate: string;

  // UI state management properties
  showInfo?: boolean; // Controls whether app info is expanded
  isEditName?: boolean; // Controls name editing mode
  isEditUrl?: boolean; // Controls URL editing mode
  isUpload?: boolean; // Controls upload state
  loading?: boolean; // Controls loading state during operations
}
