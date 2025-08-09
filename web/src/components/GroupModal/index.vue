<script setup lang="ts">
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Checkbox, CheckboxGroup } from 'ant-design-vue';
import { Icon, Input, Modal, Scroll, Select } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/infra';

import { orgTag, user } from '@/api';

/**
 * Component props interface
 * Defines the properties passed to the group modal component
 */
interface Props {
  visible: boolean; // Modal visibility flag
  updateLoading?: boolean; // Loading state for update operations
  relevancyIds?: string[]; // Relevant IDs for filtering
  type?: 'User' | 'Dept' | 'Tag' // Type of association (User, Department, or Tag)
  tagId?: string; // Tag ID for tag associations
  deptId?: string; // Department ID for department associations
  userId?: string; // User ID for user associations
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  updateLoading: false,
  relevancyIds: () => [],
  type: undefined,
  tagId: undefined,
  deptId: undefined,
  userId: undefined
});

/**
 * Component emits definition
 * Defines events that this component can emit to parent
 */
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void, // Update modal visibility
  (e: 'change', ids: string[], groups: { id: string, name: string }[], delIds: string[]): void // Handle group changes
}>();

// Internationalization setup
const { t } = useI18n();

/**
 * Reactive state management for component
 */
const params = ref<{
  filters: { key: 'name', op: 'MATCH_END', value: string | undefined }[],
  enabled?: boolean,
  tagId?: string,
  fullTextSearch: boolean
}>({ filters: [], fullTextSearch: true }); // Search parameters
const dataList = ref<{ id: string, name: string, code: string, enabled: boolean }[]>([]); // All available groups
const checkedList = ref<string[]>([]); // Currently checked group IDs
const indeterminate = ref<boolean>(false); // Indeterminate state for select all checkbox
const oldCheckedList = ref<string[]>([]); // Previously associated groups from backend
const loading = ref(false); // Loading state for API calls

/**
 * Double-click a row to toggle selection
 */
const handleRowDblClick = (id: string): void => {
  const idx = checkedList.value.indexOf(id);
  if (idx >= 0) {
    checkedList.value = checkedList.value.filter(k => k !== id);
  } else {
    checkedList.value = [...checkedList.value, id];
  }
};

/**
 * Handle select all checkbox change
 * Selects or deselects all available groups
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
 * Notification counter for scroll refresh
 */
const notify = ref(0);

/**
 * Handle search input with debouncing
 * Filters groups by name with end matching
 * @param event - Input change event
 */
const handleInputChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (value) {
    params.value.filters[0] = { key: 'name', op: 'MATCH_END', value: value };
    return;
  }
  params.value.filters = [];
  notify.value++;
});

/**
 * Load tag group associations
 * Fetches existing group associations for a tag
 * @param newList - Array of new group objects
 */
const loadTagTargetList = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    tagId: props.tagId,
    targetType: 'GROUP',
    filters: [{ key: 'targetId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await orgTag.getTagTargets(listParams);
  loading.value = false;
  if (error) {
    return;
  }
  if (!data?.list?.length) {
    return;
  }

  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.targetId)])];
  oldCheckedList.value.push(...data.list.map(item => item.targetId));
};

/**
 * Load user group associations
 * Fetches existing group associations for a user
 * @param newList - Array of new group objects
 */
const loadUserGroup = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    filters: [{ key: 'groupId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await user.getUserGroup(props.userId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) {
    return;
  }
  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.groupId)])];
  oldCheckedList.value.push(...data.list.map(item => item.groupId));
};

/**
 * Handle modal confirmation
 * Calculates added and deleted group IDs and emits change event
 */
const handleOk = () => {
  // New group IDs to add
  const addGroupIds = checkedList.value.filter(item => !oldCheckedList.value.includes(item));
  // New group objects
  const groups = dataList.value.filter(f => addGroupIds.includes(f.id));
  // Group IDs to remove
  const deleteGroupIds = oldCheckedList.value.filter(item => !checkedList.value.includes(item));
  emit('change', checkedList.value, groups, deleteGroupIds);
};

/**
 * Handle modal cancellation
 * Emits update event to close modal
 */
const handleCancel = () => {
  emit('update:visible', false);
};

/**
 * Handle scroll data change from scroll component
 * Loads existing associations when new groups are loaded
 * @param value - New group list
 * @param newValue - New group list (same as value)
 */
const handleChange = (value, newValue) => {
  dataList.value = value;
  dataList.value = value;
  switch (props.type) {
    case 'Tag':
      loadTagTargetList(newValue); // Load tag group associations
      break;
    case 'User':
      loadUserGroup(newValue); // Load user group associations
      break;

    default:
      break;
  }
};

/**
 * Handle tag selection change
 * Updates search parameters when tag filter changes
 * @param value - Selected tag ID
 */
const tagChange = (value) => {
  if (value) {
    params.value.tagId = value;
  } else {
    if (params.value.tagId) {
      delete params.value.tagId;
    }
  }
  notify.value++;
};

/**
 * Watch tag ID changes
 * Resets checked list when tag filter changes
 */
watch(() => props.tagId, () => {
  oldCheckedList.value = [];
  checkedList.value = [];
  notify.value++;
});

</script>
<template>
  <!-- Group association modal -->
  <Modal
    :title="t('group.assocTitle')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :width="800"
    :confirmLoading="props.updateLoading"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="-mt-3">
      <!-- Search and filter controls -->
      <div class="mb-2 flex space-x-2">
        <!-- Group name search input -->
        <Input
          :placeholder="t('group.placeholder.name')"
          class="w-1/2"
          size="small"
          allowClear
          @change="handleInputChange">
          <template #suffix>
            <Icon icon="icon-sousuo" class="text-3.5 leading-3.5" />
          </template>
        </Input>

        <!-- Tag filter dropdown -->
        <Select
          :placeholder="t('group.placeholder.tag')"
          :action="`${GM}/org/tag`"
          :fieldNames="{ label: 'name', value: 'id' }"
          :params="{fullTextSearch: true}"
          showSearcht
          allowClear
          class="w-1/2"
          @change="tagChange" />
      </div>

      <!-- Table header -->
      <div class="flex items-center h-8 leading-5 px-2 space-x-2 mb-1 rounded bg-theme-form-head text-theme-title">
        <Checkbox
          class="w-4"
          :indeterminate="indeterminate"
          @change="onCheckAllChange" />
        <div class="flex-1 min-w-60">{{ t('common.columns.name') }}</div>
        <div class="w-60">{{ t('common.columns.code') }}</div>
        <div class="w-25">{{ t('common.status.validStatus') }}</div>
      </div>

      <!-- Scrollable group list with checkboxes -->
      <Scroll
        :action="`${GM}/group`"
        style="height: 292px;"
        class="py-1"
        :lineHeight="30"
        :params="params"
        :notify="notify"
        @change="handleChange">
        <CheckboxGroup
          v-model:value="checkedList"
          style="width: 100%;"
          class="space-y-1 px-2">
          <div
            v-for="item in dataList"
            :key="item.id"
            class="flex-1 items-center flex leading-5 h-7 space-x-2 text-theme-content"
            @dblclick.stop="handleRowDblClick(item.id)">
            <!-- Group checkbox with icon -->
            <Checkbox :value="item.id">
              <Icon icon="icon-zu1" class="-mt-0.75 text-4" />
            </Checkbox>

            <!-- Group ID -->
            <!-- <div class="flex-shrink-0 w-50 ">
              <div class="pt-0.5">{{ item.id }}</div>
            </div>-->

            <!-- Group name -->
            <div class="truncate flex-1 min-w-60 ">
              <div class="pt-0.5">{{ item.name }}</div>
            </div>

            <!-- Group code -->
            <div class="truncate flex-shrink-0 w-60 ">
              <div class="pt-0.5">{{ item.code }}</div>
            </div>

            <!-- Group code -->
            <div class="flex-shrink-0 w-25">
              <Badge
                :status="item.enabled ? 'success' : 'error'"
                :text="item.enabled ? t('common.status.enabled') : t('common.status.disabled')" />
            </div>
          </div>
        </CheckboxGroup>
      </Scroll>


    </div>
  </Modal>
</template>
