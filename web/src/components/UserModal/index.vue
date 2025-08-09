<script setup lang="ts">
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Badge, Checkbox, CheckboxGroup } from 'ant-design-vue';
import { Icon, Image, Input, Modal, Scroll, Select } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/infra';

import { dept, group, orgTag } from '@/api';

/**
 * Props accepted by the UserModal component.
 * - visible: Whether the modal is shown.
 * - updateLoading: Loading state for confirming changes.
 * - relevancyIds: Initially related user ids (echo state when opening modal).
 * - type: Context of association (Group/Dept/Tag/Operation) to determine echo strategy.
 * - tagId/deptId/groupId: Optional ids used for echo queries.
 */
interface Props {
  visible: boolean;
  updateLoading?: boolean;
  relevancyIds?: string[];
  type?: 'Group' | 'Dept' | 'Tag' | 'Operation';
  tagId?: string;
  deptId?: string;
  groupId?: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  updateLoading: false,
  relevancyIds: () => [],
  tagId: undefined,
  deptId: undefined,
  type: undefined,
  groupId: undefined
});

/**
 * Emits
 * - update:visible: Close modal from inside
 * - change: Confirm selected users, with details of adds and removals
 */
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (
    e: 'change',
    ids: string[],
    users: { id: string; fullName: string }[],
    delIds: string[],
    checkedUsers: { id: string; fullName: string }[]
  ): void;
}>();

const { t } = useI18n();

/**
 * Basic types for API/scrolling list data and filters.
 */
interface UserListItem {
  id: string;
  fullName: string;
  avatar?: string;
  enabled: boolean;
}

interface NameFilter {
  key: 'fullName';
  op: 'MATCH_END';
  value: string | undefined;
}

/**
 * Query params bound to the Scroll component.
 * - filters: name filter (debounced input)
 * - tagId: optional filter when searching by tag
 * - fullTextSearch: full text flag
 */
const params = ref<{
  filters: NameFilter[];
  enabled?: boolean;
  tagId?: string;
  fullTextSearch: boolean;
}>({ filters: [], fullTextSearch: true });

/**
 * notify: Trigger flag for Scroll to re-fetch data.
 */
const notify = ref(0);

/**
 * dataList: Fetched user list displayed in the body.
 */
const dataList = ref<UserListItem[]>([]);

/**
 * checkedList: Current checkbox selections in the list.
 */
const checkedList = ref<string[]>([]);

/**
 * oldCheckedList: Users already related in backend (echo state).
 * Used to compute add/remove deltas when confirming.
 */
const oldCheckedList = ref<string[]>([]);

/**
 * indeterminate: Indeterminate state for the "select all" checkbox.
 */
const indeterminate = ref(false);

/**
 * loading: Loading indicator bound to Scroll via v-model.
 */
const loading = ref(false);

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
 * Handle select-all checkbox change.
 * When checked, select all ids from current dataList; otherwise clear.
 */
const onCheckAllChange = (e: { target: { checked: boolean } }): void => {
  if (e.target.checked) {
    checkedList.value = dataList.value.map(m => m.id);
    indeterminate.value = true;
  } else {
    indeterminate.value = false;
    checkedList.value = [];
  }
};

/**
 * Debounced name input handler to update the name filter.
 */
const handleInputChange = debounce(duration.search, (event: any) => {
  const value = event?.target?.value as string | undefined;
  if (value) {
    params.value.filters[0] = { key: 'fullName', op: 'MATCH_END', value };
    return;
  }
  params.value.filters = [];
});

/**
 * Echo selected users for Tag context (pre-check users related to current tag).
 */
const loadTagTargetList = async (newList: UserListItem[]): Promise<void> => {
  if (loading.value) return;
  const listParams = {
    tagId: props.tagId,
    targetType: 'USER',
    filters: [{ key: 'targetId', value: newList.map(m => m.id), op: 'IN' }]
  } as const;
  loading.value = true;
  const [error, { data }] = await orgTag.getTagTargets(listParams);
  loading.value = false;
  if (error) return;
  if (!data?.list?.length) return;

  checkedList.value = [
    ...new Set([...checkedList.value, ...data.list.map(item => item.targetId)])
  ];
  oldCheckedList.value.push(...data.list.map(item => item.targetId));
};

/**
 * Echo selected users for Group context (pre-check users related to current group).
 */
const loadGroupUserList = async (newList: UserListItem[]): Promise<void> => {
  if (loading.value) return;
  const listParams = {
    filters: [{ key: 'userId', value: newList.map(m => m.id), op: 'IN' }]
  } as const;
  loading.value = true;
  const [error, { data }] = await group.getGroupUser(props.groupId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) return;

  checkedList.value = [
    ...new Set([...checkedList.value, ...data.list.map(item => item.userId)])
  ];
  oldCheckedList.value.push(...data.list.map(item => item.userId));
};

/**
 * Echo selected users for Dept context (pre-check users related to current department).
 */
const loadDepUserList = async (newList: UserListItem[]): Promise<void> => {
  if (loading.value) return;
  const listParams = {
    filters: [{ key: 'userId', value: newList.map(m => m.id), op: 'IN' }]
  } as const;
  loading.value = true;
  const [error, { data }] = await dept.getDeptUsers(props.deptId as string, listParams);
  loading.value = false;
  if (error || !data?.list?.length) return;

  checkedList.value = [
    ...new Set([...checkedList.value, ...data.list.map(item => item.userId)])
  ];
  oldCheckedList.value.push(...data.list.map(item => item.userId));
};

/**
 * Confirm handler: calculate deltas and emit details.
 * - addUserIds: ids newly added (checked but not in oldCheckedList)
 * - users: new user objects for UI feedback
 * - deleteUserIds: ids removed from previous relations
 * - checkedUsers: all currently checked user objects
 */
const handleOk = (): void => {
  const addUserIds = checkedList.value.filter(id => !oldCheckedList.value.includes(id));
  const users = dataList.value.filter(f => addUserIds.includes(f.id));
  const deleteUserIds = oldCheckedList.value.filter(id => !checkedList.value.includes(id));
  const checkedUsers = dataList.value.filter(item => checkedList.value.includes(item.id));
  emit('change', addUserIds, users, deleteUserIds, checkedUsers);
};

/**
 * Cancel handler: close modal.
 */
const handleCancel = (): void => {
  emit('update:visible', false);
};

/**
 * Scroll change callback: receive the latest list data and new page-delta data.
 * Depending on association type, perform echo operations to pre-check relevant users.
 */
const handleChange = (value: UserListItem[], newValue: UserListItem[]): void => {
  dataList.value = value;
  switch (props.type) {
    case 'Tag':
      loadTagTargetList(newValue);
      break;
    case 'Group':
      loadGroupUserList(newValue);
      break;
    case 'Dept':
      loadDepUserList(newValue);
      break;
    default:
      // Operation or unspecified: fallback to initial relevancyIds
      checkedList.value = props.relevancyIds;
      break;
  }
};

/**
 * Tag selector change: update params.tagId and notify Scroll to reload.
 */
const tagChange = (value: any): void => {
  if (value !== undefined && value !== null && value !== '') {
    params.value.tagId = String(value);
  } else if (params.value.tagId) {
    delete params.value.tagId;
  }
  notify.value++;
};

/**
 * When the bound tagId changes from parent, reset echo caches and notify reload.
 */
watch(
  () => props.tagId,
  () => {
    oldCheckedList.value = [];
    checkedList.value = [];
    notify.value++;
  }
);

/**
 * Initialize checkedList from props.relevancyIds and keep it in sync.
 */
watch(
  () => props.relevancyIds,
  () => {
    checkedList.value = props.relevancyIds;
  },
  { immediate: true }
);
</script>

<template>
  <Modal
    :title="t('user.actions.assocUsers')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :confirmLoading="props.updateLoading"
    :width="680"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <!-- Search bar: by name (debounced) and by tag -->
    <div class="mb-3 flex space-x-2">
      <Input
        :placeholder="t('user.placeholder.name')"
        size="small"
        class="w-1/2"
        @change="handleInputChange">
        <template #suffix>
          <Icon icon="icon-sousuo" class="text-3.5" />
        </template>
      </Input>
      <Select
        :placeholder="t('user.placeholder.selectTag')"
        :action="`${GM}/org/tag?fullTextSearch=true`"
        :fieldNames="{ label: 'name', value: 'id' }"
        showSearcht
        allowClear
        class="w-1/2"
        @change="tagChange" />
    </div>

    <!-- Header row for the scrolling list -->
    <div class="flex items-center h-8 leading-5 px-2 space-x-2 mb-2 rounded bg-theme-form-head text-theme-title">
      <Checkbox
        class="w-4"
        :indeterminate="indeterminate"
        @change="onCheckAllChange" />
      <div class="w-50">ID</div>
      <div class="flex-1 min-w-60">{{ t('common.columns.name') }}</div>
      <div class="w-25">{{ t('common.status.validStatus') }}</div>
    </div>

    <!-- Scroll list: virtualized, auto-fetching with params and notify triggers -->
    <Scroll
      v-model="loading"
      :action="`${GM}/user`"
      :params="params"
      :notify="notify"
      :lineHeight="28"
      style="height: 312px;"
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
          <Checkbox :value="item.id" />

          <div class="flex-shrink-0 w-50 ">
            <div class="pt-0.5">{{ item.id }}</div>
          </div>

          <div class="truncate flex items-center space-x-2 flex-1 min-w-60" :title="item.fullName">
            <Image
              :src="item?.avatar"
              class="flex-shrink-0 w-5 h-5 rounded-full mr-2"
              type="avatar" />
            {{ item.fullName }}
          </div>

          <div class="flex-shrink-0 w-25">
            <Badge
              :status="item.enabled ? 'success' : 'error'"
              :text="item.enabled ? t('common.status.enabled') : t('common.status.disabled')" />
          </div>
        </div>
      </CheckboxGroup>
    </Scroll>
  </Modal>
</template>
