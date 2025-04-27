<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useMutations } from '@xcan/vuex';
import { Breadcrumb, Header } from '@xcan/design';

import { personalCenterMenus, getTopRightMenu } from './fixed-top-menu';

const route = useRoute();
const { t } = useI18n();
const { setLayoutCodeCode } = useMutations(['setLayoutCodeCode']);

const codeList = ref<{
  code: string;
  hasAuth: boolean;
  showName: string;
  url?: string;
}[]>([]);

onMounted(async () => {
  setLayoutCodeCode('pl');
  codeList.value = await getTopRightMenu();
});

const menuList = computed(() => {
  return personalCenterMenus.map(item => {
    return {
      ...item,
      showName: t(item.showName)
    };
  });
});

const codeMap = computed(() => {
  return codeList.value.reduce((prev, curv) => {
    prev.set(curv.code, {
      ...curv,
      showName: t(curv.showName)
    });
    return prev;
  }, new Map());
});

const hasBreadcrumb = computed(() => {
  return !!(route.meta?.breadcrumb as Array<unknown>)?.length;
});

const style = computed(() => {
  return hasBreadcrumb.value ? 'height: calc(100% - 43px);' : 'height: 100%;';
});
</script>
<template>
  <Header
    :hideCodes="['logo']"
    :menus="menuList"
    :codeMap="codeMap" />
  <div style="height: calc(100% - 54px)" class="p-5 text-3 overflow-auto bg-theme-main">
    <Breadcrumb :route="route" />
    <div :style="style">
      <RouterView />
    </div>
  </div>
</template>
<style>
.flex-col-class {
  @apply flex flex-col w-full space-y-2;
}

.flex-row-class {
  @apply flex space-x-2;
}
</style>
