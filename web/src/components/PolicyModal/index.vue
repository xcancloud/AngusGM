<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/infra';
import { Checkbox, CheckboxGroup, Divider, Popover } from 'ant-design-vue';
import { Icon, Input, Modal, Scroll } from '@xcan-angus/vue-ui';

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

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void,
  (e: 'change', addIds: string[], addPolicies: { id: string, name: string }[]): void
}>();

const { t } = useI18n();

const params = ref<{
  filters: { key: 'name', op: 'MATCH_END', value: string | undefined }[],
  enabled: boolean,
  adminFullAssociated: boolean,
  tagId?: string,
  appId?: string
}>({ filters: [], enabled: true, adminFullAssociated: true, appId: undefined });
const notify = ref(0);
const dataList = ref<{
  id: string,
  name: string,
  code: string,
  description: string,
  appName: string,
  enabled: boolean
}[]>([]);
const checkedList = ref<string[]>([]);
const indeterminate = ref(false);
const loading = ref(false);

const onCheckAllChange = e => {
  if (e.target.checked) {
    checkedList.value = dataList.value.map(m => m.id);
    indeterminate.value = true;
  } else {
    indeterminate.value = false;
    checkedList.value = [];
  }
};

const handleInputChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (value) {
    params.value.filters[0] = { key: 'name', op: 'MATCH_END', value: value };
    return;
  }
  params.value.filters = [];
});

const handleOk = () => {
  const checkedPolicies = dataList.value.filter(item => checkedList.value.includes(item.id));
  emit('change', checkedList.value, checkedPolicies);
};

const handleCancel = () => {
  emit('update:visible', false);
};

const handleChange = (value) => {
  dataList.value = value;
};

watch(() => props.visible, newValue => {
  if (newValue) {
    params.value.appId = props.appId;
    notify.value++;
    checkedList.value = [];
  }
});

const action = computed(() => {
  if (!props.visible) {
    return undefined;
  }
  switch (props.type) {
    case 'User':
      if (!props.userId) {
        return undefined;
      }
      return `${GM}/auth/user/${props.userId}/unauth/policy`;
    case 'Group':
      if (!props.groupId) {
        return undefined;
      }
      return `${GM}/auth/group/${props.groupId}/unauth/policy`;
    case 'Dept':
      if (!props.deptId) {
        return undefined;
      }
      return `${GM}/auth/dept/${props.deptId}/unauth/policy`;
    default:
      return '';
  }
});
</script>
<template>
  <Modal
    :title="t('授权策略')"
    :visible="props.visible"
    :centered="true"
    :keyboard="true"
    :width="880"
    :confirmLoading="props.updateLoading"
    class="my-modal"
    @cancel="handleCancel"
    @ok="handleOk">
    <div class="-mt-3">
      <div class="mb-2 flex space-x-2">
        <Input
          placeholder="查询策略名称"
          size="small"
          class="w-1/2"
          allowClear
          @change="handleInputChange">
          <template #suffix>
            <Icon icon="icon-sousuo" class="text-3.5 leading-3.5" />
          </template>
        </Input>
      </div>
      <div class="flex text-theme-title text-3 rounded mb-3 bg-theme-form-head py-1">
        <div class="pl-6 w-46">
          ID
        </div>
        <div class="w-40 mr-2">
          {{ t('name') }}
        </div>
        <div class="w-60 mr-2">
          {{ t('code') }}
        </div>
        <div class="w-50">
          {{ t('所属应用') }}
        </div>
      </div>
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
            class="flex-1 items-center flex text-3 text-theme-content"
            :calss="{'mt-2':index>0}">
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
      <Divider class="my-2" />
      <Checkbox :indeterminate="indeterminate" @change="onCheckAllChange">
        全选
      </Checkbox>
    </div>
  </Modal>
</template>
