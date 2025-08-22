<script setup lang='ts'>
import { onMounted, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button, Checkbox, Form, FormItem, Textarea, Tree } from 'ant-design-vue';
import { ButtonAuth, Card, Icon, Input, NoData, notification, Select } from '@xcan-angus/vue-ui';
import { appContext, GM } from '@xcan-angus/infra';
import { auth } from '@/api';
import { DataRecordType, PolicyFormState } from '../types';
import { findParentPath, handleAppFunctions, processFunctionDisplay } from '../utils';

const { t } = useI18n();

const emit = defineEmits<{(e: 'reload'): void }>();

// Collapsible state for the permission policy panel
const isCollapse = ref(true);
// Form reference for validation
const formRef = ref();

/**
 * Toggle the collapse state of the permission policy panel
 */
const changeCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

// Reactive state for the component
const state = reactive<PolicyFormState>({
  saving: false, // Save button loading state
  globalAppId: '', // Global management application ID
  globalAppName: '', // Global management application name
  form: { // Policy form data
    id: '',
    appId: undefined,
    appName: '',
    name: '',
    description: '',
    funcIds: [],
    code: undefined,
    default0: false
  },
  checkedNodes: [], // Currently checked tree nodes
  dataSource: [], // Function tree data source
  showFuncsObj: {} // Processed function display object
});

/**
 * Validate that at least one function is selected
 */
const validFunctions = () => {
  if (state.form.funcIds.length === 0) {
    return Promise.reject(new Error(t('permission.policy.add.funcRule')));
  }
  return Promise.resolve();
};

// Form validation rules
const rules = {
  name: [
    { required: true, message: t('permission.policy.add.nameRule'), trigger: 'blur' }
  ],
  appId: [
    { required: true, message: t('permission.policy.add.appRule'), trigger: 'change' }
  ],
  code: [
    { required: true, message: t('permission.policy.add.codeRule'), trigger: 'blur' }
  ],
  funcIds: [
    { required: true, validator: validFunctions, trigger: 'change' }
  ]
};

/**
 * Handle checkbox selection changes
 */
const onCheck = (_keys, info: { checkedNodes: DataRecordType[] }) => {
  state.checkedNodes = info.checkedNodes;

  // Handle parent nodes
  if (info.checkedNodes.length > 0) {
    const lastCheckedItem = info.checkedNodes[info.checkedNodes.length - 1];
    if (`${lastCheckedItem.pid}` !== '-1') {
      const list: DataRecordType[] = findParentPath(info.checkedNodes, lastCheckedItem.pid as string, state.dataSource);
      state.checkedNodes = [...list, ...info.checkedNodes];
    }
  }

  // Sync selected values to form
  state.form.funcIds = state.checkedNodes.map(item => item.id as string);
  formRef.value.validateFields(['funcIds']);
};

/**
 * Load resource functions by application ID
 */
const loadResourceByAppId = async () => {
  if (!state.form.appId) {
    return;
  }

  const userId = appContext.getUser()?.id;
  if (!userId) {
    return;
  }

  const [error, res] = await auth.getUserAppFunctionTree(userId, String(state.form.appId));
  if (error) {
    return;
  }
  state.dataSource = handleAppFunctions(res.data.appFuncs || []);
};

/**
 * Handle application selection change
 */
const selectedAppChange = (_value: any, option: any) => {
  if (option && option.appName && option.appId) {
    state.form.appId = option.appId;
    state.form.appName = option.appName;
    state.checkedNodes = [];
    state.form.funcIds = [];
    loadResourceByAppId();
  }
};

/**
 * Edit policy - load policy details and function tree for display
 */
const editPolicy = (item: { appId: string | number, policyId: string | number }) => {
  const reqList = [
    auth.getPolicyDetail(String(item.policyId)),
    auth.getPolicyFunctionsById(String(item.policyId))
  ];

  Promise.all(reqList).then(([[, detailRes], [, treeRes]]) => {
    const detailData = detailRes.data;
    const treeData = treeRes.data || [];

    // Update form with policy details
    state.form = {
      id: detailData?.id || '',
      appId: detailData?.appId || '',
      appName: detailData?.appName || '',
      name: detailData?.name || '',
      description: detailData?.description || '',
      code: detailData?.code || '',
      default0: detailData?.default0 || false,
      funcIds: []
    };

    loadResourceByAppId();
    state.checkedNodes = [];

    // Find and mark selected functions
    const findFunctions = (list: DataRecordType[]) => {
      list.forEach(v => {
        state.checkedNodes.push(v);
        if (v.children && Array.isArray(v.children)) {
          findFunctions(v.children);
        }
      });
    };

    findFunctions(treeData);
    state.form.funcIds = state.checkedNodes.map(i => i.id as string);

    // Scroll to top
    const dom = document.querySelector('#scrollMain');
    dom?.firstElementChild?.scrollTo(0, 0);
  }, (err: { message: string }) => {
    notification.error(err.message);
  });
};

/**
 * Save policy - create new or update existing
 */
const save = () => {
  formRef.value.validate().then(async () => {
    const params = {
      name: state.form.name,
      appId: String(state.form.appId),
      description: state.form.description,
      funcIds: state.form.funcIds,
      code: state.form.code,
      default0: state.form.default0,
      grantStage: 'MANUAL',
      type: 'USER_DEFINED',
      id: state.form.id || undefined
    };

    state.saving = true;
    let res: [Error | null, any];
    let successTip = '';

    if (state.form.id) {
      // Update existing policy
      successTip = t('common.messages.editSuccess');
      res = await auth.updatePolicy([params]);
    } else {
      // Create new policy
      successTip = t('common.messages.addSuccess');
      res = await auth.addPolicy([params]);
    }

    const [error] = res;
    state.saving = false;

    if (error) {
      return;
    }

    notification.success(successTip);
    emit('reload');

    // Reset form after successful save
    resetForm();
  });
};

/**
 * Reset form to initial state
 */
const resetForm = () => {
  state.form = {
    id: '',
    appId: '',
    appName: '',
    name: '',
    description: '',
    funcIds: [],
    code: undefined,
    default0: false
  };
  state.checkedNodes = [];
  state.dataSource = [];
};

// Watch checked nodes to process function display data
watch(() => state.checkedNodes, (val) => {
  state.showFuncsObj = processFunctionDisplay(val);
}, { deep: true });

onMounted(() => {
  // loadGlobalApp();
});

defineExpose({
  editPolicy
});
</script>

<template>
  <Card :title="t('permission.policy.add.title')" :bodyClass="isCollapse ? 'px-8 py-5' : 'p-0'">
    <template #rightExtra>
      <a
        href="javascript:;"
        class="text-3 leading-3 text-theme-special text-theme-text-hover"
        @click="changeCollapse">
        <span class="inline-block text-3 leading-3 transition-all">
          {{ isCollapse ? t('common.form.collapse') : t('common.form.expand') }}
        </span>
        <Icon
          icon="icon-xiala"
          :class="!isCollapse ? '' : 'collapse-rotate'"
          class="inline-block ml-1.5 text-3 leading-3 transition-all transform" />
      </a>
    </template>

    <template v-if="isCollapse">
      <div class="transition-all transform duration-1000">
        <Form
          ref="formRef"
          size="small"
          :colon="false"
          :model="state.form"
          :rules="rules"
          v-bind="{ labelCol: { style: { width: '120px' } } }">
          <!-- Policy Name Field -->
          <FormItem
            :label="t('permission.policy.add.nameLabel')"
            name="name"
            class="w-1/2">
            <Input
              v-model:value="state.form.name"
              :maxlength="100"
              :placeholder="t('permission.policy.add.namePlaceholder')"
              size="small" />
          </FormItem>

          <!-- Policy Code Field -->
          <FormItem
            :label="t('permission.policy.add.codeLabel')"
            name="code"
            class="w-1/2">
            <Input
              v-model:value="state.form.code"
              :disabled="!!state.form.id"
              :maxlength="80"
              :placeholder="t('permission.policy.add.codePlaceholder')"
              size="small"
              dataType="mixin-en"
              includes=":_-." />
          </FormItem>

          <!-- Application Selection Field -->
          <FormItem
            :label="t('permission.policy.add.appLabel')"
            name="appId"
            class="w-1/2">
            <Select
              :value="state.form.appId"
              showSearch
              :action="`${GM}/appopen/list?tenantId=${appContext.getUser()?.tenantId}&clientId=xcan_tp`"
              :disabled="!!state.form.id"
              :placeholder="t('permission.policy.add.appPlaceholder')"
              :lazy="false"
              :fieldNames="{label: 'appName', value: 'appId'}"
              internal
              size="small"
              @change="selectedAppChange" />
          </FormItem>

          <!-- Function Permissions Field -->
          <FormItem
            v-if="state.form.appId"
            :label="t('permission.policy.add.funcLabel')"
            name="funcIds">
            <div class="w-full h-82.5 border border-solid border-theme-divider rounded">
              <!-- Function selection header -->
              <div class="flex w-full h-8 bg-theme-container border-b border-solid border-theme-divider">
                <div class="flex items-center justify-center w-75 h-full text-3 leading-3 text-theme-title">
                  {{ t('permission.policy.add.funcLeft') }}
                </div>
                <div
                  class="flex items-center justify-center funcs-right-width h-full text-3 leading-3 text-theme-title">
                  {{ t('permission.policy.add.funcRight') }}
                </div>
              </div>

              <!-- Function selection content -->
              <div class="flex w-full h-72.5 py-6">
                <!-- Menu tree panel -->
                <div class="flex w-100 h-full px-3 overflow-auto border-r border-solid border-theme-divider">
                  <Tree
                    v-if="state.dataSource.length"
                    class="w-full text-3 leading-3"
                    :treeData="state.dataSource"
                    :selectable="false"
                    :checkable="true"
                    :checkStrictly="true"
                    :defaultExpandAll="true"
                    :checkedKeys="(state.checkedNodes.map((v) => v.id))"
                    :fieldNames="{key: 'id', title: 'name', children: 'children'}"
                    size="small"
                    @check="onCheck">
                    <template #title="item">
                      <div class="whitespace-nowrap" :class="{'text-gray-text': item.disabled}">
                        {{ item.name }}
                        <span class="text-gray-text">
                          （{{ `${item.type?.message}${item.disabled ? '、' + t('permission.notControlled') : '' }` }}）
                        </span>
                      </div>
                    </template>
                  </Tree>
                  <NoData
                    v-else
                    class="h-full w-full" />
                </div>

                <!-- Function list panel -->
                <div class="funcs-right-width h-full px-8 overflow-x-hidden overflow-y-auto break-all">
                  <div
                    v-for="(api, resourceName ) in state.showFuncsObj"
                    :key="resourceName"
                    class="w-full pb-2.5 flex">
                    <div
                      class="w-25 pr-1 pt-1.25 align-top text-right text-3 leading-3 text-theme-title truncate"
                      :title="resourceName">
                      {{ resourceName }}
                    </div>
                    <div class="flex-1 tags-wrap-width">
                      <span
                        v-for="(apiItem, apiIndex) in api"
                        :key="apiIndex"
                        class="inline-block px-2 py-1.5 mr-1 mb-1 border border-solid border-theme-divider text-3 leading-3 text-theme-content">
                        {{ apiItem?.name }}({{ apiItem.code }})
                      </span>
                      <span v-if="api.length === 0">{{ t('common.table.noData') }}</span>
                    </div>
                  </div>
                  <NoData v-if="Object.keys(state.showFuncsObj).length === 0" class="h-full w-full" />
                </div>
              </div>
            </div>
          </FormItem>

          <!-- Default Policy Checkbox -->
          <FormItem
            :label="t('permission.policy.add.defaultPolicy')"
            name="default0"
            class="w-1/2">
            <Checkbox v-model:checked="state.form.default0">
              {{ t('permission.policy.add.defaultPolicyTip') }}
            </Checkbox>
          </FormItem>

          <!-- Description Field -->
          <FormItem
            :label="t('permission.policy.add.descLabel')"
            name="description"
            class="w-1/2">
            <Textarea
              v-model:value="state.form.description"
              :maxlength="200"
              :placeholder="t('permission.policy.add.descPlaceholder')"
              showCount
              size="small"
              class="h-15" />
          </FormItem>

          <!-- Action Buttons -->
          <FormItem label=" " :colon="false">
            <ButtonAuth
              v-if="!state.form.id"
              code="PolicyAdd"
              type="primary"
              icon="icon-tianjia"
              @click="save" />
            <template v-else>
              <ButtonAuth
                code="PolicyModify"
                type="primary"
                icon="icon-shuxie"
                :loading="state.saving"
                @click="save" />
              <Button
                size="small"
                class="ml-2"
                @click="resetForm">
                <Icon icon="icon-quxiao" class="mr-1 text-3" />
                {{ t('common.actions.cancel') }}
              </Button>
            </template>
          </FormItem>
        </Form>
      </div>
    </template>
  </Card>
</template>

<style scoped>
.collapse-rotate {
  @apply -rotate-180;
}

.funcs-right-width {
  width: calc(100% - 300px);
}

.tags-wrap-width {
  width: calc(100% - 92px);
}

:deep(.ant-tree li .ant-tree-node-content-wrapper) {
  @apply relative pr-6 overflow-ellipsis overflow-hidden;
}
</style>
