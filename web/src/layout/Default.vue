<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { VuexHelper, Breadcrumb, Header, Sidebar } from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/tools';

const { setLayoutCodeCode } = useMutations(['setLayoutCodeCode']);

const route = useRoute();
const logoDefaultImg = new URL('./assets/angus-gm.png', import.meta.url).href;
const sidebarMenus = ref<Array<unknown>>([]);

const { useMutations } = VuexHelper;

onMounted(() => {
  setLayoutCodeCode('gm');
});

const hasBreadcrumb = computed(() => {
  const { breadcrumb } = route.meta || {};
  return !!(breadcrumb as Array<unknown>)?.length;
});
</script>
<template>
  <Header
    v-model:subMenus="sidebarMenus"
    :menus="app.menuList"
    :codeMap="app.codeMap"
    :logoDefaultImg="logoDefaultImg" />
  <div class="main-container">
    <Sidebar
      v-show="sidebarMenus.length"
      class="flex-grow-0 flex-shrink-0"
      storageKey="gm"
      :dataSource="sidebarMenus" />
    <template v-if="!hasBreadcrumb">
      <div class="flex-1 h-full p-2 overflow-auto bg-theme-main" :class="route.meta.class || ''">
        <RouterView />
      </div>
    </template>
    <template v-else>
      <div class="flex-1 h-full">
        <Breadcrumb :route="route" />
        <div style="height: calc(100% - 43px);" class="p-2 overflow-auto bg-theme-main">
          <RouterView />
        </div>
      </div>
    </template>
  </div>
</template>
<style scoped>
.main-container {
  display: flex;
  flex-wrap: nowrap;
  height: calc(100% - 54px);
}
</style>
