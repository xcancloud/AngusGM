
// Locale files - English
import enCommon from '@/locales/en/common.json';
import enPersonal from '@/locales/en/personal.json';
import enOrganization from '@/locales/en/organization.json';
import enPermission from '@/locales/en/permission.json';
import enStatistics from '@/locales/en/statistics.json';
import enMessages from '@/locales/en/messages.json';
import enSystem from '@/locales/en/system.json';
import enSign from '@/locales/en/sign.json';
import enStore from '@/locales/en/store.json';

// Locale files - Chinese
import zhCommon from '@/locales/zh_CN/common.json';
import zhPersonal from '@/locales/zh_CN/personal.json';
import zhOrganization from '@/locales/zh_CN/organization.json';
import zhPermission from '@/locales/zh_CN/permission.json';
import zhStatistics from '@/locales/zh_CN/statistics.json';
import zhMessages from '@/locales/zh_CN/messages.json';
import zhSystem from '@/locales/zh_CN/system.json';
import zhSign from '@/locales/zh_CN/sign.json';
import zhStore from '@/locales/zh_CN/store.json';

// Enum locales
import zhEnumLocale from '@/enums/locale/zh_CN.json';
import enEnumLocale from '@/enums/locale/en.json';

// Locale message bundles
export const commonLocaleBundles = {
  en: {
    ...enCommon,
    ...enEnumLocale
  },
  zh_CN: {
    ...zhCommon,
    ...zhEnumLocale
  }
} as const;

export const appLocaleBundles = {
  en: {
    ...enPersonal,
    ...enOrganization,
    ...enPermission,
    ...enStatistics,
    ...enMessages,
    ...enSystem,
    ...enSign,
    ...enStore
  },
  zh_CN: {
    ...zhPersonal,
    ...zhOrganization,
    ...zhPermission,
    ...zhStatistics,
    ...zhMessages,
    ...zhSystem,
    ...zhSign,
    ...zhStore
  }
} as const;

export const signLocaleBundles = {
  en: { ...enSign },
  zh_CN: { ...zhSign }
} as const;

export const storeLocaleBundles = {
  en: { ...enStore },
  zh_CN: { ...zhStore }
} as const;
