import { createApp } from 'vue';
import { createI18n } from 'vue-i18n';
import { app, i18n as i18n_, AppOrServiceRoute, EnumPlugin, enumUtils, cookieUtils, http } from '@xcan-angus/infra';
import { commonLocaleBundles, appLocaleBundles, storeLocaleBundles, signLocaleBundles } from '@/utils/locale';
import { enumNamespaceMap } from '@/enums/enums';

import router, { startupGuard } from '@/router';
import store from '@/utils/store';

// Styles
import '@xcan-angus/vue-ui/style.css';
import 'tailwindcss/base.css';
import 'tailwindcss/components.css';
import 'tailwindcss/utilities.css';

// Icons
import '../public/iconfont/iconfont.js';

interface BootstrapOptions {
  appComponent: 'main' | 'sign' | 'store';
  additionalMessages?: Record<string, any>;
  httpOptions?: { baseURL?: string };
  preInit?: () => Promise<void>;
}

// Constants
const SUPPORTED_LOCALES = ['en', 'zh_CN'] as const;
const DEFAULT_LOCALE = 'en';
const ROUTE_PATTERNS = {
  SIGN: /^\/(signin|password\/init|signup|password\/reset)/,
  STORE: /^\/(stores\/cloud\/open2p)/
} as const;

/**
 * Create i18n instance with proper configuration
 * @param locale - Current locale
 * @param messages - Locale messages
 * @returns Configured i18n instance
 */
const createI18nInstance = (locale: string, messages: Record<string, any>) => {
  return createI18n({
    locale: SUPPORTED_LOCALES.includes(locale as any) ? locale : DEFAULT_LOCALE,
    legacy: false,
    fallbackLocale: DEFAULT_LOCALE,
    messages,
    missingWarn: false, // Disable missing key warnings in production
    fallbackWarn: false
  });
};

/**
 * Create enum plugin options
 * @param i18n - I18n instance
 * @returns Enum plugin configuration
 */
const createEnumPluginOptions = (i18n: any) => ({
  i18n,
  enumUtils,
  appEnums: enumNamespaceMap
});

/**
 * Create and mount Vue application
 * @param appComponent - Component to mount
 * @param i18n - I18n instance
 * @param enumPluginOptions - Enum plugin options
 */
const createAndMountApp = async (
  appComponent: 'main' | 'sign' | 'store',
  i18n: any,
  enumPluginOptions: any
) => {
  try {
    let App: any;

    // Use explicit imports instead of dynamic imports for better Vite compatibility
    switch (appComponent) {
      case 'main':
        App = (await import('@/App.vue')).default;
        break;
      case 'sign':
        App = (await import('@/SignApp.vue')).default;
        break;
      case 'store':
        App = (await import('@/StoreApp.vue')).default;
        break;
      default:
        throw new Error(`Unknown app component: ${appComponent}`);
    }

    const vueApp = createApp(App);

    vueApp
      .use(router)
      .use(store)
      .use(EnumPlugin, enumPluginOptions)
      .use(i18n);

    vueApp.mount('#app');

    return vueApp;
  } catch (error) {
    console.error('Failed to create and mount app:', error);
    throw error;
  }
};

/**
 * Bootstrap application with common configuration
 * @param options - Bootstrap options
 */
const bootstrapApp = async (options: BootstrapOptions): Promise<void> => {
  try {
    // Initialize environment
    await app.initEnvironment();

    // Create HTTP client
    if (options.httpOptions) {
      await http.create(options.httpOptions);
    } else {
      await http.create();
    }

    // Pre-initialization tasks
    if (options.preInit) {
      await options.preInit();
    }

    // Get current locale
    const locale = i18n_.getI18nLanguage();

    // Create messages object
    const messages = {
      en: {
        ...commonLocaleBundles.en,
        ...options.additionalMessages?.en
      },
      zh_CN: {
        ...commonLocaleBundles.zh_CN,
        ...options.additionalMessages?.zh_CN
      }
    };

    // Create i18n instance
    const i18n = createI18nInstance(locale, messages);

    // Create enum plugin options
    const enumPluginOptions = createEnumPluginOptions(i18n);

    // Create and mount app
    await createAndMountApp(options.appComponent, i18n, enumPluginOptions);
  } catch (error) {
    console.error('Bootstrap failed:', error);
    // Show user-friendly error message
    showBootstrapError();
    throw error;
  }
};

/**
 * Bootstrap main application
 */
const bootstrapMainApp = async (): Promise<void> => {
  await bootstrapApp({
    appComponent: 'main',
    additionalMessages: appLocaleBundles,
    preInit: async () => {
      await app.initAfterAuthentication({ code: AppOrServiceRoute.gm });
      await app.initializeDefaultThemeStyle();
      startupGuard();
    }
  });
};

/**
 * Bootstrap sign-in application
 */
const bootstrapSignApp = async (): Promise<void> => {
  await bootstrapApp({
    appComponent: 'sign',
    additionalMessages: signLocaleBundles,
    preInit: async () => {
      cookieUtils.deleteTokenInfo();
    }
  });
};

/**
 * Bootstrap store application
 */
const bootstrapStoreApp = async (): Promise<void> => {
  const url = new URL(location.href);
  const origin = url.searchParams.get('or') || '';

  // Validate origin parameter to prevent malicious URLs
  const isValidOrigin = origin && /^https?:\/\/[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(:[0-9]+)?$/.test(origin);

  await bootstrapApp({
    appComponent: 'store',
    additionalMessages: storeLocaleBundles,
    httpOptions: isValidOrigin ? { baseURL: origin } : undefined
  });
};

/**
 * Show user-friendly bootstrap error
 */
const showBootstrapError = (): void => {
  const appElement = document.getElementById('app');
  if (appElement) {
    appElement.innerHTML = `
      <div style="
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100vh;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        text-align: center;
        padding: 2rem;
      ">
        <h1 style="color: #ef4444; margin-bottom: 1rem;">Application startup failed</h1>
        <p style="color: #6b7280; margin-bottom: 1rem;">
          Sorry, there was a problem launching the app. Please refresh the page and try again, or contact technical support.
        </p>
      </div>
    `;
  }
};

/**
 * Determine which app to bootstrap based on current route
 */
const determineAppType = (): 'sign' | 'store' | 'main' => {
  const pathname = location.pathname;

  if (ROUTE_PATTERNS.SIGN.test(pathname)) {
    return 'sign';
  }

  if (ROUTE_PATTERNS.STORE.test(pathname)) {
    return 'store';
  }

  return 'main';
};

/**
 * Main application entry point
 */
const main = async (): Promise<void> => {
  try {
    const appType = determineAppType();

    switch (appType) {
      case 'sign':
        await bootstrapSignApp();
        break;
      case 'store':
        await bootstrapStoreApp();
        break;
      case 'main':
      default:
        await bootstrapMainApp();
        break;
    }
  } catch (error) {
    console.error('Application startup failed:', error);
    showBootstrapError();
  }
};

// Error handling for unhandled promise rejections
window.addEventListener('unhandledrejection', (event) => {
  console.error('Unhandled promise rejection:', event.reason);
  event.preventDefault();
});

// Error handling for global errors
window.addEventListener('error', (error) => {
  console.error('Global error:', error);
});

// Start application
main().catch((error) => {
  console.error('Critical error during startup:', error);
  showBootstrapError();
});
