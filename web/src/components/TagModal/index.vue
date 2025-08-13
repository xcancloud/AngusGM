<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/infra';
import { Checkbox, CheckboxGroup, Divider } from 'ant-design-vue';
import { Icon, Input, Modal, Scroll } from '@xcan-angus/vue-ui';

import { dept, user } from '@/api';

/**
 * Component props interface
 * Defines the properties passed to the tag modal component
 */
interface Props {
  visible: boolean; // Modal visibility flag
  updateLoading?: boolean; // Loading state for update operations
  relevancyIds?: string[]; // Relevant IDs for filtering
  type?: 'Group' | 'Dept' | 'User' // Type of association (Group, Department, or User)
  userId?: string; // User ID for user associations
  deptId?: string; // Department ID for department associations
  groupId?: string; // Group ID for group associations
  max?: number; // Maximum number of tags that can be selected
  data: string[]; // Initial selected tag IDs
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  updateLoading: false,
  relevancyIds: () => [],
  userId: undefined,
  deptId: undefined,
  type: undefined,
  groupId: undefined,
  max: Infinity,
  data: () => []
});

/**
 * Component emits definition
 * Defines events that this component can emit to parent
 */
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void, // Update modal visibility
  (e: 'change', ids: string[], tags: { id: string, name: string }[], delIds: string[]): void // Handle tag changes
}>();

// Internationalization setup
const { t } = useI18n();

/**
 * Reactive state management for component
 */
const params = ref<{ filters: { key: 'name', op: 'MATCH_END', value: string | undefined }[], fullTextSearch: boolean }>({ filters: [], fullTextSearch: true }); // Search parameters
const notify = ref(0); // Notification counter for scroll refresh
const dataList = ref<{ id: string, name: string }[]>([]); // All available tags
const checkedList = ref<string[]>([]); // Currently checked tag IDs
const oldCheckedList = ref<string[]>([]); // Previously associated tags from backend
const indeterminate = ref(false); // Indeterminate state for select all checkbox
const loading = ref(false); // Loading state for API calls

/**
 * Handle select all checkbox change
 * Selects or deselects all available tags
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
 * Handle search input with debouncing
 * Filters tags by name with end matching
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
 * Load user tag associations
 * Fetches existing tag associations for a user
 * @param newList - Array of new tag objects
 */
const loadUserTagList = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    filters: [{ key: 'tagId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await user.getUserTag(props.userId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) {
    return;
  }

  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.tagId)])];
  oldCheckedList.value.push(...data.list.map(item => item.tagId));
};

/**
 * Load department tag associations
 * Fetches existing tag associations for a department
 * @param newList - Array of new tag objects
 */
const loadDeptTagList = async (newList): Promise<void> => {
  if (loading.value) {
    return;
  }
  const listParams = {
    filters: [{ key: 'tagId', value: newList.map(m => m.id), op: 'IN' }]
  };
  loading.value = true;
  const [error, { data }] = await dept.getDeptTags(props.deptId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) {
    return;
  }

  checkedList.value = [...new Set([...checkedList.value, ...data.list.map(item => item.tagId)])];
  oldCheckedList.value.push(...data.list.map(item => item.tagId));
};

/**
 * Handle modal confirmation
 * Calculates added and deleted tag IDs and emits change event
 */
const handleOk = () => {
  // New tag IDs to add
  const addTagIds = checkedList.value.filter(item => !oldCheckedList.value.includes(item));
  // New tag objects
  const tags = dataList.value.filter(f => addTagIds.includes(f.id));
  // Tag IDs to remove
  const deleteTagIds = oldCheckedList.value.filter(item => !checkedList.value.includes(item));
  emit('change', addTagIds, tags, deleteTagIds);
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
 * Loads existing associations when new tags are loaded
 * @param value - New tag list
 @param newValue - New tag list (same as value)
 */
const handleChange = (value, newValue) => {
  dataList.value = value;
  switch (props.type) {
    case 'User':
      loadUserTagList(newValue); // Load user tag associations
      break;
    case 'Dept':
      loadDeptTagList(newValue); // Load department tag associations
      break;
    default:
      break;
  }
};

/**
 * Watch modal visibility changes
 * Resets checked list when modal opens
 */
watch(() => props.visible, (newValue) => {
  if (newValue) {
    notify.value++;
    checkedList.value = props.data;
  }
}, {
  immediate: true
});

/**
 * Check if tag should be disabled
 * Disables tags when maximum selection count is reached
 * @param id - Tag ID to check
 * @returns Whether the tag should be disabled
 */
const getDisabled = (id) => {
  return !checkedList.value.includes(id) && checkedList.value.length >= props.max;
};

/**
 * Computed API action for tag loading
 */
const action = computed(() => {
  return `${GM}/org/tag`;
});

</script>
<template>
  <!-- Tag association modal -->
  <Modal
    :title="t('tag.assocTitle')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :width="800"
    :confirmLoading="props.updateLoading"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="-mt-3">
      <!-- Search input for tag filtering -->
      <Input
        :placeholder="t('tag.placeholder.name')"
        class="mb-2 w-1/2"
        size="small"
        @change="handleInputChange">
        <template #suffix>
          <Icon icon="icon-sousuo" class="text-3.5 leading-3.5" />
        </template>
      </Input>

      <!-- Table header -->
      <div class="flex py-1 bg-theme-form-head text-theme-title text-3 font-normal">
        <div class="w-1/3 pl-8 mr-2">
          ID
        </div>
        <div class="w-2/3 pl-4">
          {{ t('tag.columns.userTag.name') }}
        </div>
      </div>

      <!-- Scrollable tag list with checkboxes -->
      <Scroll
        v-model="loading"
        :action="action"
        style="height: 308px;"
        class="py-1"
        :lineHeight="30"
        :params="params"
        :notify="notify"
        @change="handleChange">
        <CheckboxGroup
          v-model:value="checkedList"
          style="width: 100%;"
          class="space-y-2">
          <!-- Tag list items -->
          <div
            v-for="item,index in dataList"
            :key="item.id"
            class="flex flex-1 items-center text-3 text-theme-content"
            :calss="{'mt-1':index>0}">
            <!-- Tag checkbox -->
            <Checkbox
              :value="item.id"
              :disabled="getDisabled(item.id)"
              class="mr-3.5 -mb-0.5">
            </Checkbox>

            <!-- Tag ID -->
            <div class="w-1/3 truncate mr-2 pt-1" :title="item.id">
              {{ item.id }}
            </div>

            <!-- Tag name -->
            <div class="w-2/3 truncate pt-1" :title="item.name">
              {{ item.name }}
            </div>
          </div>
        </CheckboxGroup>
      </Scroll>

      <!-- Select all checkbox -->
      <Divider class="my-2" />
      <Checkbox :indeterminate="indeterminate" @change="onCheckAllChange">
        {{ t('common.form.selectAll') }}
      </Checkbox>
    </div>
  </Modal>
</template>
