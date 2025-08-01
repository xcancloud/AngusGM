<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { appContext, GM } from '@xcan-angus/infra';
import { Grid, Modal, Select, SelectUser } from '@xcan-angus/vue-ui';

import { app } from '@/api';

interface Props {
  visible: boolean;
  appId: string;
  type: 'USER' | 'DEPT' | 'GROUP';
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  appId: undefined,
  type: 'USER'
});

const emit = defineEmits<{(e: 'update', refresh: boolean): void }>();

const { t } = useI18n();

const selectedUserIds = ref();
const userError = ref(false);
const userChange = (value) => {
  userError.value = !value;
};

const selectedPolicyIds = ref([]);
const policyError = ref(false);
const policyChange = (value) => {
  policyError.value = !value.length;
};

const handleOk = () => {
  if (!selectedUserIds.value) {
    userError.value = true;
    return;
  }
  if (!selectedPolicyIds.value.length) {
    policyError.value = true;
    return;
  }
  const params = { orgIds: selectedUserIds.value, policyIds: selectedPolicyIds.value };
  if (props.type === 'USER') {
    addUserAuth(params);
  } else if (props.type === 'DEPT') {
    addDeptAuth(params);
  } else {
    addGroupAuth(params);
  }
};

const addDeptAuth = async (params: { orgIds: string[], policyIds: string[] }): Promise<void> => {
  const [err] = await app.addDeptAuth(props.appId, params);
  if (err) {
    emit('update', false);
    return;
  }

  emit('update', true);
};

const addUserAuth = async (params: { orgIds: string[], policyIds: string[] }): Promise<void> => {
  const [err] = await app.addUserAuth(props.appId, params);
  if (err) {
    emit('update', false);
    return;
  }

  emit('update', true);
};

const addGroupAuth = async (params: { orgIds: string[], policyIds: string[] }): Promise<void> => {
  const [err] = await app.addGroupAuth(props.appId, params);
  if (err) {
    emit('update', false);
    return;
  }

  emit('update', true);
};

const handleCancel = () => {
  emit('update', false);
};

const title = computed(() => {
  if (props.type === 'USER') {
    return t('授权用户');
  }
  if (props.type === 'DEPT') {
    return t('授权部门');
  }
  return t('授权组');
});

const placeholder = computed(() => {
  if (props.type === 'USER') {
    return t('请选择用户');
  }
  if (props.type === 'DEPT') {
    return t('请选择部门');
  }
  return t('请选择组');
});

const selectAppUnAuthOrgAction = computed(() => {
  switch (props.type) {
    // TODO 路径信息迁移到 api 路由
    case 'USER' :
      return `${GM}/app/${props.appId}/unauth/user`;
    case 'DEPT' :
      return `${GM}/app/${props.appId}/unauth/dept`;
    case 'GROUP':
      return `${GM}/app/${props.appId}/unauth/group`;
    default:
      return `${GM}/app/${props.appId}/unauth/user`;
  }
});

// TODO 路径信息迁移到 api 路由
const selectUserAuthPolicyAction = computed(() => {
  return `${GM}/auth/user/${appContext.getUser()?.id}/policy/associated?appId=${props.appId}&adminFullAssociated=true`;
});

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

const selectLabel = computed(() => {
  if (props.type === 'USER') {
    return t('选择用户');
  }
  if (props.type === 'DEPT') {
    return t('选择部门');
  }
  return t('选择组');
});

const gridColumns = [
  [
    {
      label: selectLabel.value,
      dataIndex: 'users',
      offset: true
    },
    {
      label: t('选择策略'),
      dataIndex: 'polices',
      offset: true
    }
  ]
];

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
    <Grid :columns="gridColumns" :colon="false">
      <template #users>
        <SelectUser
          v-model:value="selectedUserIds"
          :placeholder="placeholder"
          :action="selectAppUnAuthOrgAction"
          :fieldNames="fieldNames"
          :error="userError"
          allowClear
          showSearch
          mode="multiple"
          class="w-full"
          optionLabelProp="label"
          @change="userChange" />
      </template>
      <template #polices>
        <Select
          v-model:value="selectedPolicyIds"
          placeholder="选择策略"
          :params="policyParams"
          class="w-full"
          mode="multiple"
          :error="policyError"
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
.my-tabs-bordr {
  border-color: var(--content-special-text);
}
</style>
