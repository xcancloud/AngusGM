import { createApp } from 'vue';
import { createI18n } from 'vue-i18n';
import { app, i18n as i18n_, AppOrServiceRoute, EnumPlugin, enumUtils, cookieUtils, http } from '@xcan-angus/infra';

import router, { startupGuard } from '@/router';
import store from '@/store';

import '@xcan-angus/vue-ui/style.css';
import 'tailwindcss/base.css';
import 'tailwindcss/components.css';
import 'tailwindcss/utilities.css';

import enCommon from './locales/en/common.json';
import enPersonal from './locales/en/personal.json';
import enOrganization from './locales/en/organization.json';
import enPermission from './locales/en/permission.json';
import enStatistics from './locales/en/statistics.json';
import enMessages from './locales/en/messages.json';
import enSystem from './locales/en/system.json';
import enSign from './locales/en/sign.json';

import zhCommon from './locales/zh_CN/common.json';
import zhPersonal from './locales/zh_CN/personal.json';
import zhOrganization from './locales/zh_CN/organization.json';
import zhPermission from './locales/zh_CN/permission.json';
import zhStatistics from './locales/zh_CN/statistics.json';
import zhMessages from './locales/zh_CN/messages.json';
import zhSystem from './locales/zh_CN/system.json';
import zhSign from './locales/zh_CN/sign.json';

import zhEnumLocale from '@/enums/locale/zh_CN.json';
import enEnumLocale from '@/enums/locale/en.json';
import { enumNamespaceMap } from '@/enums/enums';

import '../public/iconfont/iconfont.js';

const bootstrap = async () => {
  await app.initEnvironment();
  await http.create();
  app.initAfterAuthentication({ code: AppOrServiceRoute.gm }).then(async () => {
    await app.initializeDefaultThemeStyle();
    startupGuard();

    const messages = {
      en: {
        ...enCommon,
        ...enPersonal,
        ...enOrganization,
        ...enPermission,
        ...enStatistics,
        ...enEnumLocale,
        ...enMessages,
        ...enSystem,
        ...enSign
      },
      zh_CN: {
        ...zhCommon,
        ...zhPersonal,
        ...zhOrganization,
        ...zhPermission,
        ...zhStatistics,
        ...zhEnumLocale,
        ...zhMessages,
        ...zhSystem,
        ...zhSign
      }
    };
    const locale = i18n_.getI18nLanguage();
    const i18n = createI18n({
      locale,
      legacy: false,
      fallbackLocale: 'en',
      messages
    });

    const enumPluginOptions = {
      i18n: i18n,
      enumUtils: enumUtils,
      appEnums: enumNamespaceMap
    };

    const App = (await import('@/App.vue')).default;
    createApp(App)
      .use(router)
      .use(store)
      .use(EnumPlugin, enumPluginOptions)
      .use(i18n)
      .mount('#app');
  });
};

const bootstrapSign = async () => {
  await app.initEnvironment();
  await http.create();

  cookieUtils.deleteTokenInfo();

  const messages = {
    en: {
      ...enCommon,
      ...enSign
    },
    zh_CN: {
      ...zhCommon,
      ...zhSign
    }
  };
  const locale = i18n_.getI18nLanguage();
  const i18n = createI18n({
    locale,
    legacy: false,
    fallbackLocale: 'en',
    messages
  });

  const enumPluginOptions = {
    i18n: i18n,
    enumUtils: enumUtils,
    appEnums: enumNamespaceMap
  };

  const App = (await import('@/SignApp.vue')).default;
  createApp(App)
    .use(router)
    .use(store)
    .use(EnumPlugin, enumPluginOptions)
    .use(i18n)
    .mount('#app');
};

const bootstrapPrivateStore = async () => {
  await app.initEnvironment();
  const url = new URL(location.href);
  const origin = url.searchParams.get('or') || '';

  // Validate origin parameter to prevent malicious URLs
  if (origin && !origin.match(/^https?:\/\/[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(:[0-9]+)?$/)) {
    await http.create();
  } else {
    await http.create({ baseURL: origin });
  }
  const locale = i18n_.getI18nLanguage();
  const messages = (await import(`./locales/${locale}/index.js`)).default;
  const i18n = createI18n({
    locale,
    legacy: false,
    messages: {
      [locale]: messages
    }
  });

  const App = (await import('@/PrivStoreApp.vue')).default;
  createApp(App)
    .use(router)
    .use(store)
    .use(i18n)
    .mount('#app');
};

const main = () => {
  if (/^\/(signin|password\/init|signup|password\/reset)/.test(location.pathname)) {
    bootstrapSign().then();
  } else if (/^\/(stores\/cloud\/open2p)/.test(location.pathname)) {
    bootstrapPrivateStore().then();
  } else {
    bootstrap().then();
  }
};

main();
