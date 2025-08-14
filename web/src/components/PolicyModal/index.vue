<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/infra';
import { Checkbox, CheckboxGroup, Divider, Popover } from 'ant-design-vue';
import { Icon, Input, Modal, Scroll } from '@xcan-angus/vue-ui';
import Auth from '@/api/Auth';

/**
 * Auth API instance for policy operations
 */
const authApi = new Auth(GM);

/**
 * Component props interface for PolicyModal
 */
interface Props {
  visible: boolean;
  updateLoading?: boolean;
  type?: 'Group' | 'Dept' | 'User'
  userId?: string;
  deptId?: string;
  groupId?: string;
  appId?: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  updateLoading: false,
  userId: undefined,
  deptId: undefined,
  type: undefined,
  groupId: undefined,
  appId: undefined
});

/**
 * Component emits for updating visibility and policy changes
 */
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void,
  (e: 'change', addIds: string[], addPolicies: { id: string, name: string }[]): void
}>();

const { t } = useI18n();

/**
 * Search parameters for filtering policies
 */
const params = ref<{
  filters: { key: 'name', op: 'MATCH_END', value: string | undefined }[],
  enabled: boolean,
  adminFullAssociated: boolean,
  tagId?: string,
  appId?: string
}>({ filters: [], enabled: true, adminFullAssociated: true, appId: undefined });

/**
 * Notification counter to trigger data refresh
 */
const notify = ref(0);

/**
 * List of available policies data
 */
const dataList = ref<{
  id: string,
  name: string,
  code: string,
  description: string,
  appName: string,
  enabled: boolean
}[]>([]);

/**
 * Currently selected policy IDs
 */
const checkedList = ref<string[]>([]);

/**
 * Indeterminate state for select all checkbox
 */
const indeterminate = ref(false);

/**
 * Loading state for data fetching
 */
const loading = ref(false);

/**
 * Handle select all checkbox change
 * @param e - Checkbox change event
 */
const onCheckAllChange = e => {
  if (e.target.checked) {
    checkedList.value = dataList.value.map(m => m.id);
    indeterminate.value = true;
  } else {
    indeterminate.value = false;
    checkedList.value = [];
  }
};

/**
 * Handle double-click on policy item to toggle selection
 * @param itemId - Policy ID to toggle
 */
const handleDoubleClick = (itemId: string) => {
  const index = checkedList.value.indexOf(itemId);
  if (index > -1) {
    // Remove from selection
    checkedList.value.splice(index, 1);
  } else {
    // Add to selection
    checkedList.value.push(itemId);
  }
};

/**
 * Update indeterminate state based on current selection
 */
const updateIndeterminateState = () => {
  if (checkedList.value.length === 0) {
    indeterminate.value = false;
  } else if (checkedList.value.length === dataList.value.length) {
    indeterminate.value = false;
  } else {
    indeterminate.value = true;
  }
};

/**
 * Handle search input change with debounce
 * @param event - Input change event
 */
const handleInputChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (value) {
    params.value.filters[0] = { key: 'name', op: 'MATCH_END', value: value };
    return;
  }
  params.value.filters = [];
});

/**
 * Handle modal OK button click
 */
const handleOk = () => {
  const checkedPolicies = dataList.value.filter(item => checkedList.value.includes(item.id));
  emit('change', checkedList.value, checkedPolicies);
};

/**
 * Handle modal cancel button click
 */
const handleCancel = () => {
  emit('update:visible', false);
};

/**
 * Handle data change from Scroll component
 * @param value - New data list
 */
const handleChange = (value) => {
  dataList.value = value;
  // Update indeterminate state when data changes
  updateIndeterminateState();
};

/**
 * Watch for visible prop changes to reset state
 */
watch(() => props.visible, newValue => {
  if (newValue) {
    params.value.appId = props.appId;
    notify.value++;
    checkedList.value = [];
    indeterminate.value = false;
  }
});

/**
 * Computed API endpoint based on component type and IDs
 */
const action = computed(() => {
  if (!props.visible) {
    return undefined;
  }

  switch (props.type) {
    case 'User':
      if (!props.userId) {
        return undefined;
      }
      return authApi.getUnauthPolicyUrl('User', props.userId);
    case 'Group':
      if (!props.groupId) {
        return undefined;
      }
      return authApi.getUnauthPolicyUrl('Group', props.groupId);
    case 'Dept':
      if (!props.deptId) {
        return undefined;
      }
      return authApi.getUnauthPolicyUrl('Dept', props.deptId);
    default:
      return '';
  }
});
</script>
<template>
  <Modal
    :title="t('permission.name')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :width="880"
    :confirmLoading="props.updateLoading"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="-mt-3">
      <!-- Search input section -->
      <div class="mb-2 flex space-x-2">
        <Input
          :placeholder="t('permission.placeholder.policyName')"
          size="small"
          class="w-1/2"
          allowClear
          @change="handleInputChange">
          <template #suffix>
            <Icon icon="icon-sousuo" class="text-3.5 leading-3.5" />
          </template>
        </Input>
      </div>

      <!-- Table header section -->
      <div class="flex text-theme-title text-3 rounded mb-3 bg-theme-form-head py-1">
        <div class="pl-6 w-46">
          ID
        </div>
        <div class="w-40 mr-2">
          {{ t('permission.columns.name') }}
        </div>
        <div class="w-60 mr-2">
          {{ t('permission.columns.code') }}
        </div>
        <div class="w-50">
          {{ t('permission.columns.appName') }}
        </div>
      </div>

      <!-- Scrollable data list section -->
      <Scroll
        v-model="loading"
        style="height: 284px;"
        :lineHeight="29"
        :action="action"
        :params="params"
        :notify="notify"
        @change="handleChange">
        <CheckboxGroup
          v-model:value="checkedList"
          style="width: 100%;"
          class="space-y-2">
          <div
            v-for="item,index in dataList"
            :key="item.id"
            class="flex-1 items-center flex text-3 text-theme-content cursor-pointer hover:bg-gray-50"
            :calss="{'mt-2':index>0}"
            @dblclick="handleDoubleClick(item.id)">
            <Checkbox
              :value="item.id">
            </Checkbox>
            <div class="truncate w-40 ml-2 mt-0.5">{{ item.id }}</div>
            <div class="truncate w-40 mr-2 mt-0.5" :title="item.name">
              <Popover
                placement="bottomLeft"
                :overlayStyle="{maxWidth:'600px',fontSize:'12px'}">
                <template #title>
                  {{ item.name }}
                </template>
                <template v-if="item.description" #content>
                  {{ item.description }}
                </template>
                {{ item.name }}
              </Popover>
            </div>
            <div class="truncate w-60 mr-2 mt-0.5" :title="item.code">{{ item.code }}</div>
            <div class="truncate w-50 mt-0.5">{{ item.appName }}</div>
          </div>
        </CheckboxGroup>
      </Scroll>

      <!-- Footer section with select all checkbox -->
      <Divider class="my-2" />
      <Checkbox :indeterminate="indeterminate" @change="onCheckAllChange">
        {{ t('common.form.selectAll') }}
      </Checkbox>
    </div>
  </Modal>
</template>
