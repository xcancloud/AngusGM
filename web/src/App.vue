<script setup lang="ts">
import { computed } from 'vue';
import { appContext } from '@xcan-angus/infra';
import { ConfigProvider, Denied, Header, NetworkError, NotFound } from '@xcan-angus/vue-ui';

import store from './utils/store';

const status = computed(() => {
  return store.state.statusCode;
});
</script>

<template>
  <ConfigProvider>
    <template v-if="status === 200">
      <RouterView />
    </template>
    <template v-else>
      <Header
        :menus="appContext.getAccessAppFuncTree() || []"
        :codeMap="appContext.getAccessAppFuncCodeMap()" />
      <div class="exception-container bg-theme-main">
        <template v-if="status === 403">
          <Denied />
        </template>
        <template v-else-if="status === 404">
          <NotFound />
        </template>
        <template v-else-if="status === 405">
          <Denied />
        </template>
        <template v-else-if="status === 500">
          <NetworkError />
        </template>
      </div>
    </template>
  </ConfigProvider>
</template>

<style>
:root {
  --font-family-zh: "PingFang SC", "Microsoft YaHei", "Segoe UI", Roboto, sans-serif;
  --font-family-en: -apple-system, "Inter", "SF Pro Display", "Segoe UI", Roboto, sans-serif;
  --letter-spacing-zh: 0;
  --letter-spacing-en: 0.1px;
  --line-height-zh: 1.5;
  --line-height-en: 1.6;
}

body,
html {
  min-width: 1280px;
  height: 100%;
  overflow: auto;
  font-size: 16px;
}

#app {
  position: relative;
  z-index: 1;
  min-width: 1200px;
  height: 100%;
  font-family: var(--font-family-zh);
  font-size: 14px;
  letter-spacing: var(--letter-spacing-zh);
  line-height: var(--line-height-zh);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

html[lang="en"] #app {
  font-family: var(--font-family-en);
  letter-spacing: var(--letter-spacing-en);
  line-height: var(--line-height-en);
  overflow-wrap: break-word;
  word-break: normal;
}

html[lang="en"] .btn {
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.exception-container {
  @apply flex justify-center items-center;
  height: calc(100% - 54px);
}
</style>
