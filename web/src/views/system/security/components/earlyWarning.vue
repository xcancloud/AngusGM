<script setup lang="ts">
import { ref, watch, defineAsyncComponent } from 'vue';
import { Card, Icon, AsyncComponent, Grid } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan-angus/tools';
import { CheckboxGroup, Checkbox, Switch, Tag, Button } from 'ant-design-vue';

import { Alarm, Operation } from '../PropsType';

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

// 获取注册码
// const getCode = async function () {
//   const [error, res] = await tenant.getInvitationCode();
// };

const currEnabled = ref(false);
const enabledChange = debounce(duration.search, (value) => {
  emit('change', value, 'enabled', 'earlySwitch');
});

const checkedMethodIds = ref<('SMS' | 'EMAIL') []>([]);

const userVisible = ref(false);
const handleSelectUser = () => {
  userVisible.value = true;
};

const receiveUser = ref<{ id: string, fullName: string }[]>([]);
const checkBoxChange = () => {
  emit('change', checkedMethodIds.value, 'alarmWay', 'safetyCheckBox');
};

const userSave = async (_userIds: string[], _users: { id: string, fullName: string }[]) => {
  userVisible.value = false;
  receiveUser.value = _users;
  emit('change', _users, 'receiveUser');
};

const delUser = (userId: string) => {
  receiveUser.value = receiveUser.value.filter(item => item.id !== userId);
  emit('change', receiveUser.value, 'receiveUser');
};

watch(() => props.alarm, (newValue) => {
  if (newValue) {
    currEnabled.value = newValue?.enabled;
    checkedMethodIds.value = newValue?.alarmWay?.map(item => item.value) || [];
    receiveUser.value = newValue?.receiveUser || [];
  }
}, {
  immediate: true
});

const gridColumns = [
  [
    {
      label: '告警方式',
      dataIndex: 'type',
      colon: false
    },
    {
      label: '告警接收用户',
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
        <span>安全告警</span>
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
              短信
            </Checkbox>
            <Checkbox
              value="EMAIL"
              :disabled="!currEnabled"
              class="text-3 leading-3">
              邮箱
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
            选择用户
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
