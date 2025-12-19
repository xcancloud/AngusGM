<script setup lang="ts">

import { reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { NoData } from '@xcan-angus/vue-ui';
import { Tree } from 'ant-design-vue';

import type { ApisType, DataRecordType, PolicyDetailType } from '../types';
import { auth } from '@/api';

const { t } = useI18n();

/**
 * Props interface for MenuAuth component
 * @interface Props
 * @property {PolicyDetailType} detail - Policy detail information
 */
interface Props {
  detail: PolicyDetailType
}

/**
 * Component props with default values
 * Provides fallback values for all detail properties to prevent rendering errors
 */
const props = withDefaults(defineProps<Props>(), {
  detail: () => ({
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
  })
});

/**
 * Component reactive state
 * Manages checked nodes, data source, and function display objects
 */
const state = reactive<{
  checkedNodes: DataRecordType[],
  dataSource: DataRecordType[],
  showFuncsObj: { [key: string]: Array<ApisType> }
}>({
  checkedNodes: [], // Currently checked checkbox nodes
  dataSource: [], // Function tree data
  showFuncsObj: {}
});

/**
 * Load authorized function tree for the policy
 * Fetches function permissions based on policy ID and application ID
 */
const load = async () => {
  if (!props.detail.appId) {
    return;
  }

  const [error, res] = await auth.getPolicyFunctionsById(props.detail.id as string);
  if (error) {
    return;
  }

  const data = res.data;
  handlerDataSource(data);
  state.dataSource = data;
};

/**
 * Selected API identifiers
 * Tracks which APIs are currently selected in the tree
 */
const selectApis = ref<string[]>([]);

/**
 * Handle tree node selection
 * Updates the selected APIs when a tree node is selected
 */
const handleSelect = (id: string[], funcs) => {
  if (id.length) {
    selectApis.value = (funcs.node.apis || []).map(api => api.id);
  } else {
    selectApis.value = [];
  }
};

/**
 * Process data source to disable checkbox operations
 * Recursively processes the tree data to set appropriate disabled states
 */
const handlerDataSource = (data: DataRecordType[]) => {
  const fn = (list: DataRecordType[]) => {
    list.forEach((item) => {
      state.checkedNodes.push(item);
      if (item.children && Array.isArray(item.children)) {
        fn(item.children);
      }
    });
  };
  fn(data);
};

/**
 * Watch for changes in application ID
 * Reloads function tree when the application changes
 */
watch(() => props.detail.appId, (val: string | number | undefined) => {
  if (val) {
    load();
  }
}, {
  immediate: true
});
</script>

<template>
  <!-- Menu authorization management interface -->
  <div class="w-full border border-solid border-theme-divider rounded flex flex-col">
    <!-- Header section with column titles -->
    <div class="flex w-full h-10 bg-theme-container border-b border-solid border-theme-divider">
      <div class="flex items-center justify-center w-75 h-full text-3 leading-3 text-theme-title">
        {{ t('permission.policy.add.funcLeft') }}
      </div>
      <div class="flex items-center justify-center funcs-right-width h-full text-3 leading-3 text-theme-title">
        {{ t('permission.policy.add.funcRight') }}
      </div>
    </div>

    <!-- Main content area with menu tree and function list -->
    <div class="flex-1 flex">
      <!-- Menu tree section -->
      <div class="flex w-75 h-full p-3 overflow-x-hidden overflow-y-auto border-r border-solid border-theme-divider">
        <Tree
          v-if="state.dataSource.length"
          class="w-full text-3"
          :treeData="state.dataSource"
          :selectable="true"
          :checkable="false"
          :defaultExpandAll="true"
          size="small"
          :fieldNames="{key: 'id', title: 'showName', children: 'children'}"
          @select="handleSelect">
          <template #title="item">
            <div class="whitespace-nowrap">
              {{ item.name }} <span
                class="text-gray-text">（{{ item.type?.message }}）</span>
            </div>
          </template>
        </Tree>
        <NoData
          v-else
          class="h-full w-full" />
      </div>

      <!-- Function list section -->
      <div class="funcs-right-width h-full px-8 overflow-x-hidden overflow-y-auto break-all flex-1">
        <template v-if="state.checkedNodes.length">
          <div
            v-for="(api, resourceName) in state.showFuncsObj"
            :key="resourceName"
            class="w-full pb-2.5 flex">
            <div
              class="w-25 pr-1 pt-1.25 align-top text-right text-3 leading-3 text-theme-title truncate"
              :title="resourceName">
              {{ resourceName }}
            </div>
            <div class="flex-1 flex flex-wrap">
              <span
                v-for="(apiItem, apiIndex) in api"
                :key="apiIndex"
                :class="{'bg-orange-bg1': selectApis.includes(apiItem.id)}"
                class="inline-block px-2 py-1.5 mr-1 mb-1 border border-solid border-theme-divider text-3 leading-3 text-theme-content">
                {{ apiItem.name }}({{ apiItem.code }})
              </span>
              <span v-if="api.length === 0">--</span>
            </div>
          </div>
        </template>
        <NoData
          v-else
          class="h-full flex-1" />
      </div>
    </div>
  </div>
</template>
