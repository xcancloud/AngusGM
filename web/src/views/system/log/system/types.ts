/**
 * Service option interface for dropdown selection
 */
export interface ServiceOption {
  /** Display label for the service */
  label: string;
  /** Service identifier value */
  value: string;
}

/**
 * Instance option interface for dropdown selection
 */
export interface InstanceOption {
  /** Display label for the instance */
  label: string;
  /** Instance identifier value */
  value: string;
}

/**
 * Log file option interface for dropdown selection
 */
export interface LogFileOption {
  /** Display label for the log file */
  label: string;
  /** Log file identifier value */
  value: string;
}

/**
 * Browse option interface for log line selection
 */
export interface BrowseOption {
  /** Number of lines to browse */
  value: string;
  /** Display label for the browse option */
  label: string;
  /** Type indicator (0 for tail, 1 for head) */
  type: number;
}

/**
 * Log content request parameters interface
 */
export interface LogContentParams {
  /** Number of lines to retrieve */
  linesNum: string;
  /** Whether to get lines from tail (true) or head (false) */
  tail: boolean;
}

/**
 * Component state interface for main system log component
 */
export interface SystemLogState {
  /** Available service options */
  serviceOptions: ServiceOption[];
  /** Available instance options */
  instancesOptions: InstanceOption[];
  /** Available log file options */
  logOptions: LogFileOption[];
  /** Selected service ID */
  serviceId?: string;
  /** Selected instance */
  instances?: string;
  /** Selected log file */
  logFile?: string;
  /** Number of lines to browse */
  browse: string;
  /** Log content to display */
  logContent: string;
  /** Whether auto-refresh is enabled */
  autoRefresh: boolean;
  /** Loading state for auto-refresh */
  autoRefreshLoading: boolean;
  /** Whether fullscreen mode is active */
  fullScreen: boolean;
}

/**
 * File export configuration interface
 */
export interface FileExportConfig {
  /** Timestamp for filename */
  timestamp: string;
  /** Generated filename */
  filename: string;
  /** Blob object for download */
  blob: Blob;
  /** Download URL */
  url: string;
}

/**
 * Service change parameters interface
 */
export interface ServiceChangeParams {
  /** New service value */
  value: any;
}

/**
 * Instance change parameters interface
 */
export interface InstanceChangeParams {
  /** New instance value */
  value: any;
}

/**
 * Log file change parameters interface */
export interface LogFileChangeParams {
  /** New log file value */
  value: any;
}

/**
 * Auto refresh change parameters interface
 */
export interface AutoRefreshChangeParams {
  /** New auto-refresh state */
  value: boolean;
}

/**
 * Fullscreen toggle parameters interface
 */
export interface FullscreenToggleParams {
  /** New fullscreen state */
  value: boolean;
}
