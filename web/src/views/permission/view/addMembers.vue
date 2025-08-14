<script setup lang="ts">
import { computed, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { appContext } from '@xcan-angus/infra';
import { Grid, Modal, Select, SelectUser } from '@xcan-angus/vue-ui';

import { app, auth } from '@/api';
import { AddAuthParams, AddMembersProps, AddMembersState } from './types';

/**
 * Component Props Interface
 *
 * Defines the required properties for the modal functionality,
 * including visibility state, application context, and entity type
 */
type Props = AddMembersProps

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  appId: undefined,
  type: 'USER'
});

const emit = defineEmits<{(e: 'update', refresh: boolean): void }>();

const { t } = useI18n();

/**
 * Reactive State Management
 */
const state = reactive<AddMembersState>({
  selectedUserIds: undefined,
  userError: false,
  selectedPolicyIds: [],
  policyError: false
});

/**
 * Handle User Selection Changes
 *
 * Updates error state based on whether users are selected
 * to provide real-time validation feedback
 */
const userChange = (value: any) => {
  state.userError = !value || value.length === 0;
};

/**
 * Handle Policy Selection Changes
 *
 * Updates error state based on whether policies are selected
 * to provide real-time validation feedback
 */
const policyChange = (value: any) => {
  state.policyError = !value || value.length === 0;
};

/**
 * Handle Form Submission
 *
 * Validates form inputs and routes to appropriate API calls
 * based on the selected entity type
 */
const handleOk = () => {
  if (!state.selectedUserIds || state.selectedUserIds.length === 0) {
    state.userError = true;
    return;
  }
  if (!state.selectedPolicyIds.length) {
    state.policyError = true;
    return;
  }

  const params: AddAuthParams = {
    orgIds: state.selectedUserIds,
    policyIds: state.selectedPolicyIds
  };

  // Route to appropriate API based on entity type
  if (props.type === 'USER') {
    addUserAuth(params);
  } else if (props.type === 'DEPT') {
    addDeptAuth(params);
  } else {
    addGroupAuth(params);
  }
};

/**
 * Add Department Authorization
 *
 * Calls the API to add department authorization for the selected
 * departments and policies
 */
const addDeptAuth = async (params: AddAuthParams): Promise<void> => {
  try {
    const [err] = await app.addDeptAuth(props.appId, params);
    if (err) {
      emit('update', false);
      return;
    }

    emit('update', true);
  } catch (error) {
    console.error('Failed to add department authorization:', error);
    emit('update', false);
  }
};

/**
 * Add User Authorization
 *
 * Calls the API to add user authorization for the selected
 * users and policies
 */
const addUserAuth = async (params: AddAuthParams): Promise<void> => {
  try {
    const [err] = await app.addUserAuth(props.appId, params);
    if (err) {
      emit('update', false);
      return;
    }

    emit('update', true);
  } catch (error) {
    console.error('Failed to add user authorization:', error);
    emit('update', false);
  }
};

/**
 * Add Group Authorization
 *
 * Calls the API to add group authorization for the selected
 * groups and policies
 */
const addGroupAuth = async (params: AddAuthParams): Promise<void> => {
  try {
    const [err] = await app.addGroupAuth(props.appId, params);
    if (err) {
      emit('update', false);
      return;
    }

    emit('update', true);
  } catch (error) {
    console.error('Failed to add group authorization:', error);
    emit('update', false);
  }
};

/**
 * Handle Modal Cancellation
 *
 * Emits update event to close modal without refreshing
 * the parent component data
 */
const handleCancel = () => {
  emit('update', false);
};

/**
 * Compute Modal Title
 *
 * Generates the appropriate title for the modal based on
 * the selected entity type
 */
const title = computed(() => {
  if (props.type === 'USER') {
    return t('permission.view.userTitle');
  }
  if (props.type === 'DEPT') {
    return t('permission.view.deptTitle');
  }
  return t('permission.view.groupTitle');
});

/**
 * Compute Selection Placeholder
 *
 * Generates the appropriate placeholder text for the member
 * selection component based on entity type
 */
const placeholder = computed(() => {
  if (props.type === 'USER') {
    return t('permission.view.userPlaceholder');
  }
  if (props.type === 'DEPT') {
    return t('permission.view.deptPlaceholder');
  }
  return t('permission.view.groupPlaceholder');
});

/**
 * Compute Unauthorized Organization API Action
 *
 * Generates the appropriate API endpoint for fetching unauthorized
 * organizations based on entity type
 */
const selectAppUnAuthOrgAction = computed(() => {
  return app.getUnauthPolicyUrl(props.type, props.appId);
});

/**
 * Compute User Authorization Policy API Action
 *
 * Generates the API endpoint for fetching user authorization policies
 */
const selectUserAuthPolicyAction = computed(() => {
  return auth.getUserAuthPolicyUrl(appContext.getUser()?.id || 0, props.appId);
});

/**
 * Compute Field Names for Selection Components
 *
 * Determines the appropriate field names for label and value
 * based on the entity type for proper data binding
 */
const fieldNames = computed(() => {
  switch (props.type) {
    case 'USER' :
      return { label: 'fullName', value: 'id' };
    case 'DEPT' :
      return { label: 'name', value: 'id' };
    case 'GROUP':
      return { label: 'name', value: 'id' };
    default:
      return { label: 'fullName', value: 'id' };
  }
});

/**
 * Compute Selection Label
 *
 * Generates the appropriate label for the member selection
 * component based on entity type
 */
const selectLabel = computed(() => {
  if (props.type === 'USER') {
    return t('permission.view.userPlaceholder');
  }
  if (props.type === 'DEPT') {
    return t('permission.view.deptPlaceholder');
  }
  return t('permission.view.groupPlaceholder');
});

/**
 * Grid Column Configuration
 *
 * Defines the layout structure for the form grid,
 * including labels and data binding for each section
 */
const gridColumns = [
  [
    {
      label: selectLabel.value,
      dataIndex: 'users',
      offset: true
    },
    {
      label: t('permission.view.selectPolicy'),
      dataIndex: 'polices',
      offset: true
    }
  ]
];

/**
 * Policy selection parameters for filtering available policies
 * Ensures only enabled and admin-accessible policies are shown
 */
const policyParams = { enabled: true, adminFullAssociated: true };
</script>

<template>
  <Modal
    :title="title"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :width="600"
    @cancel="handleCancel"
    @ok="handleOk">
    <!-- Form Grid Layout -->
    <Grid :columns="gridColumns" :colon="false">
      <!-- Member Selection Section -->
      <template #users>
        <SelectUser
          v-model:value="state.selectedUserIds"
          :placeholder="placeholder"
          :action="selectAppUnAuthOrgAction"
          :fieldNames="fieldNames"
          :error="state.userError"
          allowClear
          showSearch
          mode="multiple"
          class="w-full"
          optionLabelProp="label"
          @change="userChange" />
      </template>

      <!-- Policy Selection Section -->
      <template #polices>
        <Select
          v-model:value="state.selectedPolicyIds"
          placeholder="permission.view.selectPolicy"
          :params="policyParams"
          class="w-full"
          mode="multiple"
          :error="state.policyError"
          :action="selectUserAuthPolicyAction"
          :fieldNames="{ label: 'name', value: 'id' }"
          allowClear
          showSearch
          @change="policyChange" />
      </template>
    </Grid>
  </Modal>
</template>

<style scoped>
/**
 * Custom styling for tab borders
 * Uses CSS variable for consistent theming
 */
.my-tabs-bordr {
  border-color: var(--content-special-text);
}
</style>
