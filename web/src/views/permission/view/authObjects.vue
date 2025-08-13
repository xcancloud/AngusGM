<script setup lang='ts'>
import { computed, defineAsyncComponent, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { AsyncComponent, ButtonAuth, Icon, IconRefresh, Image, Input, Scroll } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/infra';

/**
 * Async component import for better performance
 * Lazy loading component to improve initial page load time
 */
const AddMembers = defineAsyncComponent(() => import('./addMembers.vue'));

/**
 * Component Props Interface
 *
 * Defines the required and optional properties passed to this component,
 * including the entity type, application ID, and pre-selected target
 */
interface Props {
  type: 'USER' | 'DEPT' | 'GROUP',
  appId: string,
  selectedTargetId?: string
}

/**
 * Data Type Interface
 *
 * Defines the structure for target data items, supporting different
 * entity types with their specific properties
 */
interface DataType {
  id: string;
  userId?: string;
  avatar?: string;
  createdByName?: string;
  userName?: string;
  deptName?: string;
  groupName?: string;
  deptId?: string;
  groupId?: string;
  fullName?: string;
  name?: string;
}

/**
 * Authorization Configuration Mapping
 *
 * Maps entity types to their corresponding authorization action codes,
 * enabling proper permission checking for different operations
 */
const addAuthConfig = {
  USER: 'AuthorizeUserAdd',
  DEPT: 'AuthorizeDeptAdd',
  GROUP: 'AuthorizeGroupAdd'
};

/**
 * Modal visibility state for adding new members
 * Controls when the add members modal is displayed
 */
const addVisible = ref(false);

/**
 * Open Add Members Modal
 *
 * Shows the modal for adding new members to the current authorization target
 */
const add = () => {
  addVisible.value = true;
};

const { t } = useI18n();
const props = withDefaults(defineProps<Props>(), {});

/**
 * Compute Entity Type Name
 *
 * Generates the localized name for the current entity type
 * to be used in search placeholders and other UI elements
 */
const typeName = props.type === 'DEPT'
  ? t('permission.check.dept')
  : props.type === 'GROUP'
    ? t('permission.check.group')
    : t('permission.check.user');

const emit = defineEmits<{(e: 'change', targetId: string): void }>();

/**
 * Refresh counter for triggering data reloads
 * Incremented to force component refresh when needed
 */
const refresh = ref(0);

/**
 * Component State Management
 *
 * Centralized reactive state for managing component behavior,
 * data loading, and user interactions
 */
const state = reactive<{
  loading: boolean,
  dataSource: DataType[],
  selectedTargetId: string | undefined,
  searchValue: string | undefined
}>({
  loading: false, // Data loading state
  dataSource: [], // List data source
  selectedTargetId: undefined, // Currently selected target ID
  searchValue: undefined // Search input value
});

/**
 * Handle Target Selection Change
 *
 * Processes user selection of different targets and emits
 * the change event to parent components
 */
const targetChange = (item: DataType) => {
  const id = props.type === 'USER'
    ? item.id
    : props.type === 'GROUP'
      ? item.id
      : item.id;
  state.selectedTargetId = id;
  emit('change', id);
};

/**
 * Compute API Action URL
 *
 * Generates the appropriate API endpoint for fetching target data
 * based on the current application ID and entity type
 */
const getAction = computed(() => {
  if (!props.appId) {
    return undefined;
  }
  return `${GM}/app/${props.appId}/auth/${props.type.toLocaleLowerCase()}`;
});

/**
 * Load Data from API Response
 *
 * Processes the API response data and activates the first item
 * for better user experience
 */
const loadData = (data: DataType[]) => {
  state.dataSource = data;
  activeFirstItem();
};

/**
 * Activate First Item
 *
 * Automatically selects the first item in the data list
 * to ensure a target is always selected when data loads
 */
const activeFirstItem = () => {
  if (state.dataSource.length) {
    targetChange(state.dataSource[0]);
  }
};

/**
 * Watch Application ID Changes
 *
 * Monitors changes in the application ID to trigger data refresh
 * when switching between different applications
 */
watch(() => props.appId, newValue => {
  if (newValue) {
    refresh.value += 1;
  }
});

/**
 * Watch Selected Target ID Changes
 *
 * Synchronizes the internal selected target ID with
 * the prop value from parent components
 */
watch(() => props.selectedTargetId, newValue => {
  state.selectedTargetId = newValue;
});

/**
 * Compute Search Parameters
 *
 * Builds the search filter parameters based on the current
 * search value and entity type for API calls
 */
const params = computed(() => {
  return {
    filters: [state.searchValue && {
      key: props.type === 'USER' ? 'fullName' : 'name',
      value: state.searchValue,
      op: 'MATCH_END'
    }].filter(Boolean)
  };
});

/**
 * Handle Search Input Changes
 *
 * Debounced function for handling search input changes
 * to avoid excessive API calls during typing
 */
const changeSearchValue = debounce(duration.search, (e: Event) => {
  const target = e.target as HTMLInputElement;
  state.searchValue = target.value;
});

/**
 * Update List and Close Modal
 *
 * Handles the completion of add member operations,
 * refreshing the list if successful and closing the modal
 */
const updateList = (value: boolean) => {
  if (value) {
    refresh.value++;
  }
  addVisible.value = false;
};

/**
 * Expose methods for parent component access
 * Allows parent components to call internal methods
 */
defineExpose({ activeFirstItem });
</script>

<template>
  <!-- Search and Action Bar -->
  <div class="w-full p-2 flex space-x-2 items-center">
    <Input
      :value="state.searchValue"
      :allowClear="true"
      :placeholder="t('permission.check.searchPlaceholder', { name: typeName })"
      class="flex-1"
      size="small"
      @change="changeSearchValue">
      <template #suffix>
        <Icon
          icon="icon-sousuo"
          class="text-3 leading-3 text-theme-content cursor-pointer" />
      </template>
    </Input>
    <ButtonAuth
      :code="addAuthConfig[props.type]"
      type="primary"
      icon="icon-tianjia"
      :disabled="!props.appId"
      @click="add" />
    <IconRefresh
      class="text-3.5"
      @click="updateList(true)" />
  </div>

  <!-- Target List with Virtual Scrolling -->
  <Scroll
    :action="getAction"
    :params="params"
    :notify="refresh"
    :lineHeight="32"
    class="flex-1"
    @change="loadData">
    <template #default>
      <div
        v-for="item in state.dataSource"
        :key="item.id"
        class="w-full px-3 flex items-center h-8 cursor-pointer"
        :class="{
          'bg-theme-tabs-selected': state.selectedTargetId === item.id
        }"
        @click="targetChange(item)">
        <!-- User Avatar Display -->
        <div v-if="type === 'USER'" class="w-6 h-6 rounded-full">
          <Image
            class="w-full h-full rounded-full"
            type="avatar"
            :src="item.avatar" />
        </div>

        <!-- Department Icon Display -->
        <div
          v-if="type === 'DEPT'"
          class="w-6 h-6 rounded-full bg-blue-tips flex items-center justify-center">
          <Icon class="inline w-full h-full text-theme-content text-4" icon="icon-bumen" />
        </div>

        <!-- Group Icon Display -->
        <div
          v-if="type === 'GROUP'"
          class="w-6 h-6 rounded-full bg-blue-tips flex items-center justify-center">
          <Icon class="inline w-full h-full text-theme-content text-4" icon="icon-zu" />
        </div>

        <!-- Target Name Display -->
        <div
          class="name-width text-3 leading-3 text-theme-content pl-5 overflow-hidden overflow-ellipsis whitespace-nowrap"
          :title="type === 'USER' ? item.fullName : item.name">
          {{ type === 'USER' ? item.fullName : item.name }}
        </div>
      </div>
    </template>
  </Scroll>

  <!-- Add Members Modal -->
  <AsyncComponent :visible="addVisible">
    <AddMembers
      v-if="addVisible"
      :visible="addVisible"
      :appId="props.appId"
      :type="props.type"
      @update="updateList" />
  </AsyncComponent>
</template>

<style scoped>
/**
 * Custom styling for name width
 * Ensures proper text display within the target list items
 */
.name-width {
  width: calc(100% - 40px);
}
</style>
