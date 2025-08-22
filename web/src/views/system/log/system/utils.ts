import type {
  ServiceOption, InstanceOption, LogFileOption, BrowseOption, LogContentParams, FileExportConfig
} from './types';
import dayjs from 'dayjs';

/**
 * Create browse options configuration for log line selection
 */
export const createBrowseOptions = (t: (key: string, params: any) => string): BrowseOption[] => [
  {
    value: '50',
    label: t('log.system.messages.browseLines', { lines: '50' }),
    type: 0
  },
  {
    value: '500',
    label: t('log.system.messages.browseLines', { lines: '500' }),
    type: 0
  },
  {
    value: '1000',
    label: t('log.system.messages.browseLines', { lines: '1000' }),
    type: 0
  },
  {
    value: '10000',
    label: t('log.system.messages.browseLines', { lines: '10000' }),
    type: 0
  }
];

/**
 * Create service options from service names
 */
export const createServiceOptions = (services: string[]): ServiceOption[] => {
  if (!Array.isArray(services)) return [];

  return services.map((service: string) => ({
    label: service,
    value: service
  }));
};

/**
 * Create instance options from instance names
 */
export const createInstanceOptions = (instances: string[]): InstanceOption[] => {
  if (!Array.isArray(instances)) return [];

  return instances.map((instance: string) => ({
    label: instance,
    value: instance
  }));
};

/**
 * Create log file options from log file names
 */
export const createLogFileOptions = (logFiles: string[]): LogFileOption[] => {
  if (!Array.isArray(logFiles)) return [];

  return logFiles.map((logFile: string) => ({
    label: logFile,
    value: logFile
  }));
};

/**
 * Create log content request parameters
 */
export const createLogContentParams = (linesNum: string, browseType: number): LogContentParams => ({
  linesNum,
  tail: browseType === 0
});

/**
 * Get current service label from service options
 */
export const getCurrentServiceLabel = (
  serviceOptions: ServiceOption[],
  serviceId?: string
): string => {
  if (!serviceId) return '';

  const service = serviceOptions.find(item => item.value === serviceId);
  return service?.label || '';
};

/**
 * Get current browse option from browse options
 */
export const getCurrentBrowseOption = (
  browseOptions: BrowseOption[],
  browseValue: string
): BrowseOption | undefined => {
  return browseOptions.find(item => item.value === browseValue);
};

/**
 * Check if service selection is valid
 */
export const isValidServiceSelection = (serviceId?: string): boolean => {
  return !!serviceId && serviceId.trim().length > 0;
};

/**
 * Check if instance selection is valid
 */
export const isValidInstanceSelection = (instance?: string): boolean => {
  return !!instance && instance.trim().length > 0;
};

/**
 * Check if log file selection is valid
 */
export const isValidLogFileSelection = (logFile?: string): boolean => {
  return !!logFile && logFile.trim().length > 0;
};

/**
 * Check if log content is available
 */
export const hasLogContent = (logContent: string): boolean => {
  return !!logContent && logContent.trim().length > 0;
};

/**
 * Reset dependent selections when service changes
 */
export const resetServiceDependencies = (state: {
  instances?: string;
  logFile?: string;
  logContent: string;
}): void => {
  state.instances = undefined;
  state.logFile = undefined;
  state.logContent = '';
};

/**
 * Reset dependent selections when instance changes
 */
export const resetInstanceDependencies = (state: {
  logFile?: string;
  logContent: string;
}): void => {
  state.logFile = undefined;
  state.logContent = '';
};

/**
 * Reset log file selection and content
 */
export const resetLogFileSelection = (state: {
  logFile?: string;
  logContent: string;
}): void => {
  state.logFile = undefined;
  state.logContent = '';
};

/**
 * Create file export configuration for log download
 */
export const createFileExportConfig = (
  logContent: string,
  serviceLabel: string,
  browseValue: string
): FileExportConfig => {
  const timestamp = dayjs().format('YYYY/MM/DD HH/mm/ss');
  const filename = `${serviceLabel}(${browseValue})-${timestamp}.log`;

  const blob = new Blob([logContent], { type: 'text/plain;charset=utf-8' });
  const url = URL.createObjectURL(blob);

  return {
    timestamp,
    filename,
    blob,
    url
  };
};

/**
 * Download file using the provided configuration
 */
export const downloadFile = (config: FileExportConfig): void => {
  const link = document.createElement('a');
  link.download = config.filename;
  link.href = config.url;
  link.click();

  // Clean up the created URL object
  URL.revokeObjectURL(config.url);
};

/**
 * Export log content to a downloadable file
 */
export const exportLogToFile = (
  logContent: string,
  serviceLabel: string,
  browseValue: string
): void => {
  if (!hasLogContent(logContent)) return;

  const config = createFileExportConfig(logContent, serviceLabel, browseValue);
  downloadFile(config);
};

/**
 * Start auto-refresh timer with specified interval
 */
export const startAutoRefreshTimer = (
  callback: () => void,
  interval = 2000
): ReturnType<typeof setTimeout> => {
  return setTimeout(async () => {
    await callback();
    startAutoRefreshTimer(callback, interval); // Recursive call for continuous refresh
  }, interval);
};

/**
 * Stop auto-refresh timer
 */
export const stopAutoRefreshTimer = (timer: ReturnType<typeof setTimeout> | null): void => {
  if (timer) {
    clearTimeout(timer);
  }
};

/**
 * Validate API response data
 */
export const isValidApiResponse = (data: any): boolean => {
  return data && Array.isArray(data) && data.length > 0;
};

/**
 * Process API response data safely
 */
export const processApiResponseData = (data: any): any[] => {
  if (!isValidApiResponse(data)) {
    return [];
  }
  return data;
};

/**
 * Handle API errors gracefully
 */
export const handleApiError = (error: any, context: string): void => {
  console.error(`Failed to ${context}:`, error);
};

/**
 * Check if component is ready for log content loading
 */
export const isReadyForLogContent = (instance?: string, logFile?: string): boolean => {
  return isValidInstanceSelection(instance) && isValidLogFileSelection(logFile);
};

/**
 * Check if component is ready for instance loading
 */
export const isReadyForInstanceLoading = (serviceId?: string): boolean => {
  return isValidServiceSelection(serviceId);
};

/**
 * Check if component is ready for log file loading
 */
export const isReadyForLogFileLoading = (instance?: string): boolean => {
  return isValidInstanceSelection(instance);
};
