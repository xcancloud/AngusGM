<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Breadcrumb, Header, VuexHelper } from '@xcan-angus/vue-ui';
import { getTopRightMenu } from '@/utils/menus';

const route = useRoute();
const { t } = useI18n();

const { useMutations } = VuexHelper;
const { setLayoutCodeCode } = useMutations(['setLayoutCodeCode']);

// Personal center menu configuration
const personalCenterMenus = [
  {
    code: 'personal-info',
    hasAuth: true,
    tags: [{ id: '1', name: 'DYNAMIC_POSITION' }],
    authCtrl: false,
    type: 'MENU',
    id: '1',
    showName: t('fixedTopMenu.personalCenter.baseInfo'),
    url: '/personal/information',
    show: true,
    icon: '',
    name: 'personal-info',
    visible: true,
    width: undefined
  },
  {
    code: 'personal-security',
    hasAuth: true,
    tags: [{ id: '2', name: 'DYNAMIC_POSITION' }],
    authCtrl: false,
    type: 'MENU',
    id: '2',
    showName: t('fixedTopMenu.personalCenter.securitySetting'),
    url: '/personal/security',
    show: true,
    icon: '',
    name: 'personal-security',
    visible: true,
    width: undefined
  },
  {
    code: 'personal-token',
    hasAuth: true,
    tags: [{ id: '4', name: 'DYNAMIC_POSITION' }],
    authCtrl: false,
    type: 'MENU',
    id: '4',
    showName: t('fixedTopMenu.personalCenter.accessToken'),
    url: '/personal/token',
    show: true,
    icon: '',
    name: 'personal-token',
    visible: true,
    width: undefined
  },
  {
    code: 'personal-messages',
    hasAuth: true,
    tags: [{ id: '5', name: 'DYNAMIC_POSITION' }],
    authCtrl: false,
    type: 'MENU',
    id: '5',
    showName: t('fixedTopMenu.personalCenter.messages'),
    url: '/personal/messages',
    show: true,
    icon: '',
    name: 'personal-messages',
    visible: true,
    width: undefined
  }
];

const topRightMenus = ref<{
  code: string;
  hasAuth: boolean;
  showName: string;
  url?: string;
}[]>([]);

onMounted(async () => {
  setLayoutCodeCode('pl');
  topRightMenus.value = await getTopRightMenu();
});

const topRightMenusMap = computed(() => {
  return topRightMenus.value.reduce((prev, curv) => {
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
    :menus="personalCenterMenus"
    :codeMap="topRightMenusMap" />
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
