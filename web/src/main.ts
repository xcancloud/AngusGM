import { createApp } from 'vue';
import { createI18n } from 'vue-i18n';
import { app, preference, cookie, http } from '@xcan-angus/tools';

import router, { startupGuard } from '@/router';
import store from '@/store';

import '@xcan-angus/vue-ui/style.css';
import 'tailwindcss/base.css';
import 'tailwindcss/components.css';
import 'tailwindcss/utilities.css';

import '../public/iconfont/iconfont.js';

const getPreferredLocale = (): string => {
  // Try to get locale from localStorage, URL parameter, or browser language
  const savedLocale = localStorage.getItem('preferred_locale');
  const urlParams = new URLSearchParams(location.search);
  const urlLocale = urlParams.get('locale');
  const browserLocale = navigator.language.replace('-', '_');
  
  const supportedLocales = ['zh_CN', 'en_US'];
  const preferredLocale = urlLocale || savedLocale || browserLocale || 'zh_CN';
  
  return supportedLocales.includes(preferredLocale) ? preferredLocale : 'zh_CN';
};

const bootstrap = async () => {
  await app.check();
  await http.create();
  app.initialize({ code: 'gm' }).then((res) => {
    preference.initialize(res.preference).then(async () => {
      startupGuard();

      const locale = getPreferredLocale();
      const messages = (await import(`./locales/${locale}/index.js`)).default;
      const i18n = createI18n({
        locale,
        legacy: false,
        messages: {
          [locale]: messages
        }
      });

      const App = (await import('@/App.vue')).default;
      createApp(App)
        .use(router)
        .use(store)
        .use(i18n)
        .mount('#app');
    });
  });
};

const bootstrapSign = async () => {
  await app.check();
  await http.create();
  cookie.remove('access_token');
  cookie.remove('refresh_token');
  const locale = getPreferredLocale();
  const messages = (await import(`./locales/${locale}/sign.js`)).default;
  const i18n = createI18n({
    locale,
    legacy: false,
    messages: {
      [locale]: messages
    }
  });

  const App = (await import('@/SignApp.vue')).default;
  preference.initialize({ themeCode: 'gray' }).then(() => {
    createApp(App)
      .use(router)
      .use(store)
      .use(i18n)
      .mount('#app');
  });
};

const bootstrapPrivStore = async () => {
  await app.check();
  const url = new URL(location.href);
  const origin = url.searchParams.get('or') || '';
  
  // Validate origin parameter to prevent malicious URLs
  if (origin && !origin.match(/^https?:\/\/[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(:[0-9]+)?$/)) {
    console.warn('Invalid origin parameter detected:', origin);
    await http.create();
  } else {
    await http.create({ baseURL: origin });
  }
  const locale = getPreferredLocale();
  const messages = (await import(`./locales/${locale}/index.js`)).default;
  const i18n = createI18n({
    locale,
    legacy: false,
    messages: {
      [locale]: messages
    }
  });

  const App = (await import('@/PrivStoreApp.vue')).default;
  preference.initialize({ themeCode: 'gray' }).then(() => {
    createApp(App)
      .use(router)
      .use(store)
      .use(i18n)
      .mount('#app');
  });
};

if (/^\/(signin|password\/init|signup|password\/reset)/.test(location.pathname)) {
  bootstrapSign();
} else if (/^\/(stores\/cloud\/open2p)/.test(location.pathname)) {
  bootstrapPrivStore();
} else {
  bootstrap();
}
