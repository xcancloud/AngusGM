<script setup lang='ts'>
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Skeleton } from 'ant-design-vue';

import { auth } from '@/api';
import type { DetailType } from './PropTypes';
import BaseInfo from './baseInfo.vue';
import TargetPanel from './targetPanel.vue';
import MenuAuth from './menuAuth.vue';
import { Card, Hints } from '@xcan-angus/vue-ui';

const { t } = useI18n();
const route = useRoute();
const TAB_LIST = [{ key: '1', tab: t('permissionsStrategy.detail.tab1') }, { key: '3', tab: t('菜单权限') }, {
  key: '2',
  tab: t('permissionsStrategy.detail.tab2')
}];
const activeKey = ref('1');
const state = reactive<{
  tab: '1' | '2',
  loading: boolean,
  detail: DetailType
}>({
  tab: '1',
  loading: false,
  detail: {
    id: undefined,
    name: undefined,
    code: undefined,
    appId: undefined,
    appName: undefined,
    createdByName: undefined,
    createdDate: undefined,
    type: { value: undefined, message: undefined },
    enabled: false,
    description: undefined
  }
});

const firstLoad = ref(true);
// 查询策略详情
const load = async () => {
  state.loading = true;
  const [error, res] = await auth.getPolicyDetail(route.params.id);
  state.loading = false;
  firstLoad.value = false;
  if (error) {
    return;
  }

  state.detail = res.data || {};
};

const defaultPolicies = ref<string[]>([]);

const loadDefaults = async () => {
  const [error, res] = await auth.getTenantDefaultPolicy();
  if (error) {
    return;
  }
  const handlerData = res.data || [];
  handlerData.forEach((item) => {
    const checkedItem = item.defaultPolicies.find(v => v.currentDefault);
    if (checkedItem) {
      defaultPolicies.value.push(checkedItem.id);
    }
  });
};

onMounted(() => {
  loadDefaults();
  load();
});
</script>

<template>
  <Card
    v-model:value="activeKey"
    :tabList="TAB_LIST"
    class="min-h-full flex flex-col text-3"
    bodyClass="flex p-3.5  flex-col flex-1">
    <template v-if="activeKey ==='1'">
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 6 }">
        <BaseInfo :detail="state.detail" class="ml-3 mr-3 mt-2" />
      </Skeleton>
    </template>
    <template v-if="activeKey==='3'">
      <div class="flex flex-1">
        <MenuAuth :detail="state.detail" />
      </div>
    </template>
    <template v-if="activeKey ==='2'">
      <template v-if="defaultPolicies.includes(route.params.id)">
        <Hints
          class="mb-1"
          text="当前策略已经被设置为用户应用初始“默认权限策略”，无需重复授权；如果再次重复授权，取消授权时需要同时取消“应用默认授权”设置和当前重复关联授权；只取消“应用默认授权”设置时不会去掉当前重复授权。" />
      </template>
      <div class="flex flex-1">
        <TargetPanel
          key="USER"
          type="USER"
          :policyId="state.detail.id"
          :appId="state.detail.appId" />
        <TargetPanel
          key="DEPT"
          class="ml-3"
          type="DEPT"
          :policyId="state.detail.id" />
        <TargetPanel
          key="GROUP"
          class="ml-3"
          type="GROUP"
          :policyId="state.detail.id" />
      </div>
    </template>
  </Card>
</template>
