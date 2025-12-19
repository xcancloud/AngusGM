<script setup lang='ts'>
import { defineAsyncComponent, onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Skeleton } from 'ant-design-vue';

import { auth } from '@/api';
import type { PolicyDetailType } from '../types';
import { Card, Hints } from '@xcan-angus/vue-ui';

/**
 * Async component imports for better performance
 * Lazy loading components to improve initial page load time
 */
const BaseInfo = defineAsyncComponent(() => import('./baseInfo.vue'));
const AuthObjects = defineAsyncComponent(() => import('./authObjects.vue'));
const MenuAuth = defineAsyncComponent(() => import('./menuAuth.vue'));

const { t } = useI18n();
const route = useRoute();

/**
 * Currently active tab key
 * Controls which detail section is displayed
 */
const activeKey = ref('1');
/**
 * Flag to track if this is the first data load
 * Used to show skeleton loading state only on initial load
 */
const firstLoad = ref(true);
/**
 * List of default policy IDs
 * Tracks which policies are set as tenant default policies
 */
const defaultPolicies = ref<string[]>([]);
/**
 * Component reactive state
 * Manages tab state, loading status, and policy detail data
 */
const state = reactive<{
  tab: '1' | '2',
  loading: boolean,
  detail: PolicyDetailType
}>({
  tab: '1',
  loading: false,
  detail: {
    id: undefined,
    name: undefined,
    code: undefined,
    appId: undefined,
    appName: undefined,
    creator: undefined,
    createdDate: undefined,
    type: { value: undefined, message: undefined },
    enabled: false,
    description: undefined,
    global: false,
    default0: false,
    grantStage: undefined
  }
});

/**
 * Load policy detail information
 * Fetches complete policy data based on route parameter ID
 */
const load = async () => {
  state.loading = true;
  const [error, res] = await auth.getPolicyDetail(route.params.id as string);
  state.loading = false;
  firstLoad.value = false;
  if (error) {
    return;
  }

  state.detail = res.data || {};
};

/**
 * Load tenant default policies
 * Fetches and processes default policy configuration for the current tenant
 */
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

/**
 * Tab configuration for policy detail views
 * Defines the three main sections: basic info, target authorization, and menu permissions
 */
const TAB_LIST = [
  { key: '1', tab: t('permission.policy.detail.basicInfo') },
  { key: '3', tab: t('permission.policy.detail.authFunctions') },
  { key: '2', tab: t('permission.policy.detail.authObjects') }
];

/**
 * Initialize component data on mount
 * Loads both default policies and current policy details
 */
onMounted(() => {
  loadDefaults();
  load();
});
</script>

<template>
  <!-- Policy detail management interface with tabbed navigation -->
  <Card
    v-model:value="activeKey"
    :tabList="TAB_LIST"
    class="min-h-full flex flex-col text-3"
    bodyClass="flex p-3.5  flex-col flex-1">
    <!-- Tab 1: Basic policy information -->
    <template v-if="activeKey ==='1'">
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 6 }">
        <BaseInfo :detail="state.detail" class="ml-3 mr-3 mt-2" />
      </Skeleton>
    </template>

    <!-- Tab 3: Menu permissions management -->
    <template v-if="activeKey==='3'">
      <div class="flex flex-1">
        <MenuAuth :detail="state.detail" />
      </div>
    </template>

    <!-- Tab 2: Target authorization management -->
    <template v-if="activeKey ==='2'">
      <!-- Warning hint for default policy strategies -->
      <template v-if="defaultPolicies.includes(route.params.id as string)">
        <Hints
          class="mb-1"
          :text="t('permission.policy.detail.defaultPolicyHint')" />
      </template>

      <!-- Target authorization panels for users, departments, and groups -->
      <div class="flex flex-1">
        <AuthObjects
          key="USER"
          type="USER"
          :policyId="state.detail.id"
          :appId="state.detail.appId" />
        <AuthObjects
          key="DEPT"
          class="ml-3"
          type="DEPT"
          :policyId="state.detail.id" />
        <AuthObjects
          key="GROUP"
          class="ml-3"
          type="GROUP"
          :policyId="state.detail.id" />
      </div>
    </template>
  </Card>
</template>
