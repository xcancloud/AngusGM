<script setup lang="ts">
import { defineAsyncComponent, ref, watch } from 'vue';
import { AsyncComponent, Card, Grid, Icon } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan-angus/infra';
import { Button, Checkbox, CheckboxGroup, Switch, Tag } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';

import { Alarm, Operation } from '../PropsType';

const { t } = useI18n();

// Async component for user selection modal
const UserModal = defineAsyncComponent(() => import('@/components/UserModal/index.vue'));

interface Props {
  alarm: Alarm;
  earlySwitchLoading: boolean;
  safetyCheckBoxLoading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  alarm: undefined,
  earlySwitchLoading: false,
  safetyCheckBoxLoading: false
});

const emit = defineEmits<{
  (e: 'change', value: string | boolean | ('SMS' | 'EMAIL') [] | {
    id: string,
    fullName: string
  }[], type: string, operation?: Operation): void
}>();

// Early warning switch state
const currEnabled = ref(false);

/**
 * Handle early warning switch change with debounce
 * @param value - Boolean value indicating if early warning is enabled
 */
const enabledChange = debounce(duration.search, (value) => {
  emit('change', value, 'enabled', 'earlySwitch');
});

// Selected alarm method IDs
const checkedMethodIds = ref<('SMS' | 'EMAIL') []>([]);

// User selection modal visibility
const userVisible = ref(false);

/**
 * Open user selection modal
 */
const handleSelectUser = () => {
  userVisible.value = true;
};

// Selected users for receiving alarms
const receiveUser = ref<{ id: string, fullName: string }[]>([]);

/**
 * Handle alarm method checkbox changes
 */
const checkBoxChange = () => {
  emit('change', checkedMethodIds.value, 'alarmWay', 'safetyCheckBox');
};

/**
 * Save selected users for alarm reception
 * @param _userIds - Array of user IDs
 * @param _users - Array of user objects with id and fullName
 */
const userSave = async (_userIds: string[], _users: { id: string, fullName: string }[]) => {
  userVisible.value = false;
  receiveUser.value = _users;
  emit('change', _users, 'receiveUser');
};

/**
 * Remove user from alarm reception list
 * @param userId - ID of user to remove
 */
const delUser = (userId: string) => {
  receiveUser.value = receiveUser.value.filter(item => item.id !== userId);
  emit('change', receiveUser.value, 'receiveUser');
};

// Watch for alarm prop changes and update local state
watch(() => props.alarm, (newValue) => {
  if (newValue) {
    currEnabled.value = newValue?.enabled;
    checkedMethodIds.value = newValue?.alarmWay?.map(item => item.value) || [];
    receiveUser.value = newValue?.receiveUser || [];
  }
}, {
  immediate: true
});

// Grid column configuration for layout
const gridColumns = [
  [
    {
      label: t('security.columns.alarmMethod'),
      dataIndex: 'type',
      colon: false
    },
    {
      label: t('security.columns.alarmRecipients'),
      dataIndex: 'user',
      colon: false
    }
  ]
];

</script>
<template>
  <Card bodyClass="px-8 py-5 flex items-center space-x-5">
    <template #title>
      <div class="flex items-center">
        <span>{{ t('security.titles.securityAlerts') }}</span>
        <Switch
          v-model:checked="currEnabled"
          :loading="props.earlySwitchLoading"
          size="small"
          class="ml-6 mt-0.5"
          @change="enabledChange" />
      </div>
    </template>
    <template #default>
      <Grid :columns="gridColumns">
        <template #type>
          <CheckboxGroup
            v-model:value="checkedMethodIds"
            :disabled="props.safetyCheckBoxLoading"
            class="space-x-6"
            @change="checkBoxChange">
            <Checkbox
              value="SMS"
              :disabled="!currEnabled"
              class="text-3 leading-3">
              {{ t('security.labels.sms') }}
            </Checkbox>
            <Checkbox
              value="EMAIL"
              :disabled="!currEnabled"
              class="text-3 leading-3">
              {{ t('security.labels.email') }}
            </Checkbox>
          </CheckboxGroup>
        </template>
        <template #user>
          <Tag
            v-for="(item, index) in receiveUser"
            :key="index"
            :visible="true"
            class="mb-2">
            {{ item?.fullName }}
            <Icon
              icon="icon-shanchuguanbi"
              class="text-3 leading-3 ml-1 mb-0.5 text-theme-text-hover"
              @click="delUser(item.id)" />
          </Tag>
          <Button
            :disabled="!currEnabled"
            type="link"
            size="small"
            @click="handleSelectUser">
            <Icon icon="icon-xuanze" class="mr-1 text-3" />
            {{ t('security.buttons.selectUsers') }}
          </Button>
        </template>
      </Grid>
      <AsyncComponent :visible="userVisible">
        <UserModal
          v-if="userVisible"
          v-model:visible="userVisible"
          :relevancyIds="receiveUser.map(item=>item.id)"
          @change="userSave" />
      </AsyncComponent>
    </template>
  </Card>
</template>
