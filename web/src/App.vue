<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ConfigProvider, Denied, Header, NetworkError, NotFound, VuexHelper } from '@xcan-angus/vue-ui';
import { sessionStore } from '@xcan-angus/infra';
import { getTopRightMenu } from '@/utils/menus';

const { t } = useI18n();

const { useState, useMutations } = VuexHelper;
const { statusCode, layoutCode } = useState(['statusCode', 'layoutCode']);

const topRightMenus = ref<{
  code: string;
  hasAuth: boolean;
  showName: string;
  url?: string;
}[]>([]);

onMounted(async () => {
  if (!layoutCode.value) {
    let tempLayoutCode = sessionStore.get('__LC__');
    if (!['gm', 'pl'].includes(tempLayoutCode)) {
      tempLayoutCode = 'gm';
    }
    setLayoutCodeCode(tempLayoutCode);
  }
  topRightMenus.value = await getTopRightMenu();
});

const codeMap = computed(() => {
  return topRightMenus.value.reduce((prev, curv) => {
    prev.set(curv.code, {
      ...curv,
      showName: t(curv.showName)
    });
    return prev;
  }, new Map());
});

const { setLayoutCodeCode } = useMutations(['setLayoutCodeCode']);
</script>

<template>
  <ConfigProvider>
    <template v-if="statusCode === 200">
      <RouterView />
    </template>
    <template v-else>
      <template id="layoutCode === 'pl'">
        <Header :codeMap="codeMap" />
      </template>
      <div class="exception-container bg-theme-main">
        <template v-if="statusCode === 403">
          <Denied />
        </template>
        <template v-else-if="statusCode === 404">
          <NotFound />
        </template>
        <template v-else-if="statusCode === 405">
          <Denied />
        </template>
        <template v-else-if="statusCode === 500">
          <NetworkError />
        </template>
      </div>
    </template>
  </ConfigProvider>
</template>

<style>
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
  font-family: Inter,
  "Apple System",
  "SF Pro SC",
  "SF Pro Display",
  "Helvetica Neue",
  Arial,
  "PingFang SC",
  "Hiragino Sans GB",
  STHeiti,
  "Microsoft YaHei",
  "Microsoft JhengHei",
  "Source Han Sans SC",
  "Noto Sans CJK SC",
  "Source Han Sans CN",
  sans-serif;
  font-size: 14px;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.exception-container {
  @apply flex justify-center items-center;

  height: calc(100% - 54px);
}
</style>
