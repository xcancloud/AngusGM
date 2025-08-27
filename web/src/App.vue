<script setup lang="ts">
import { computed } from 'vue';
import { appContext } from '@xcan-angus/infra';
import { ConfigProvider, Denied, Header, NetworkError, NotFound } from '@xcan-angus/vue-ui';

import store from './utils/store';

// Import global styles
import '@/assets/styles/global.css';

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

<style scoped>
/* Component-specific styles only */
/* Global styles are now imported from global.css */
</style>
