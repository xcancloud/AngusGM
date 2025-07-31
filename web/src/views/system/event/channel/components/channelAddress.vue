<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Divider, Form, FormItem } from 'ant-design-vue';
import { modal, notification, Input, Select, ButtonAuth } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { regexpUtils } from '@xcan-angus/infra';

import { event } from '@/api';

interface Props {
  max: number,
  channelType: string;
  placeholder: string;
}

const props = withDefaults(defineProps<Props>(), {
  max: 5,
  channelType: 'WEBHOOK',
  placeholder: ''
});

const { t } = useI18n();
const formRefs: any[] = [];
const loading = ref(true);
const dataSource = ref<{
  address: string | string[],
  id: string,
  name: string,
  channelType: string,
  isEdit?: boolean
}[]>([{
  id: '',
  address: props.channelType === 'EMAIL' ? [] : '',
  name: '',
  channelType: props.channelType,
  isEdit: true
}]);

const validateEmial = (_val, value) => {
  if (!value.trim()) {
    return Promise.reject(new Error(t('address-required')));
  }
  if (value) {
    const values = value.split(',');
    if (values.every(email => regexpUtils.isEmail(email))) {
      return Promise.resolve();
    } else {
      Promise.reject(new Error('请输入正确邮箱地址'));
    }
  }
  return Promise.reject(new Error('请输入正确邮箱地址'));
};

const validateAddress = (_val, value) => {
  if (!value.trim()) {
    return Promise.reject(new Error(t('address-required')));
  }
  if (value && regexpUtils.isUrl(value)) {
    return Promise.resolve();
  }
  return Promise.reject(new Error('请输入正确地址'));
};

const addressRule = ref([
  { required: true, validator: props.channelType === 'EMAIL' ? validateEmial : validateAddress }
]);

// 查询列表详情
const getReceiveSettingDetail = async () => {
  const [error, res] = await event.getTypeChannel(props.channelType);
  if (error && !res.data) {
    return;
  }

  dataSource.value = res.data.map(m => {
    return {
      ...m,
      isEdit: false,
      address: props.channelType === 'EMAIL' ? m.address.split(',') : m.address
    };
  });

  if (dataSource.value.length < props.max) {
    dataSource.value[dataSource.value.length] = {
      id: '',
      address: props.channelType === 'EMAIL' ? [] : '',
      name: '',
      channelType: props.channelType,
      isEdit: true
    };
  }
};

// 新增保存
const addHttpItem = (index: number, item) => {
  formRefs[index].validate().then(async () => {
    loading.value = true;
    const addresses = typeof item.address === 'string' ? item.address : item.address.join(',');
    const [error] = await event.addChannel({ name: item.name, channelType: item.channelType, address: addresses });
    loading.value = false;
    if (error) {
      return;
    }

    notification.success(t('renew.t5'));
    getReceiveSettingDetail();
  }).catch();
};

// 编辑保存
const editHttpItemConfirm = (index: number, item) => {
  formRefs[index].validate().then(async () => {
    loading.value = true;
    const addresses = typeof item.address === 'string' ? item.address : item.address.join(',');
    const [error] = await event.replaceChannel({ id: item.id, address: addresses, name: item.name });
    loading.value = false;
    if (error) {
      return;
    }
    notification.success(t('renew.t7'));
    getReceiveSettingDetail();
  }).catch();
};

// 删除确认
const deleteConfirm = (id: string) => {
  modal.confirm({
    centered: true,
    title: t('delete'),
    content: t('renew.t3'),
    onOk () {
      delHttpConfig(id);
    }
  });
};

// 删除
const delHttpConfig = async (id: string): Promise<void> => {
  loading.value = true;
  const [error] = await event.deleteChannel(id);
  loading.value = false;
  if (error) {
    return;
  }

  notification.success(t('renew.t4'));
  getReceiveSettingDetail();
};

// 测试
const testConfig = async (index, item) => {
  formRefs[index].validate().then(async () => {
    loading.value = true;
    const addresses = typeof item.address === 'string' ? item.address : item.address.join(',');
    const [error] = await event.testChannelConfig({
      name: item.name,
      address: addresses,
      channelType: item.channelType.value || item.channelType
    });
    loading.value = false;
    if (error) {
      return;
    }

    notification.success(t('renew.t6'));
  }).catch();
};

const editAddress = (item) => {
  item.oldName = item.name;
  item.oldAddress = item.address;
  item.isEdit = true;
};

const cancelEdit = (item, index) => {
  item.name = item.oldName;
  item.address = item.oldAddress;
  item.isEdit = false;
  formRefs[index].clearValidate();
};

onMounted(() => {
  getReceiveSettingDetail();
});
</script>
<template>
  <div class="p-3.5 max-w-300">
    <Form
      v-for="(item, index) in dataSource"
      :key="index"
      :ref="dom => formRefs[index] = dom"
      :model="item"
      size="small"
      layout="vertical"
      class="flex space-x-5 mb-2">
      <div class="flex-1/3 break-all">
        <FormItem
          :label="index === 0 && t('name')"
          :colon="false"
          name="name"
          :rules="[{ required: true, message: t('name-required') }]"
          class="text-theme-content">
          <Input
            v-model:value="item.name"
            :placeholder="t('name-placeholder')"
            :maxlength="80"
            :disabled="!item.isEdit" />
        </FormItem>
      </div>
      <div class="flex-2/3 break-all">
        <FormItem
          :label="index === 0 && t('address')"
          :colon="false"
          name="address"
          :rules="addressRule"
          class="text-theme-content flex-1">
          <div class="flex items-center">
            <Select
              v-if="props.channelType === 'EMAIL'"
              v-model:value="item.address"
              mode="tags"
              class="flex-1"
              :maxlength="100"
              :disabled="!item.isEdit"
              :placeholder="props.placeholder">
            </Select>
            <Input
              v-else
              v-model:value="item.address"
              :maxlength="1000"
              class="flex-1"
              :disabled="!item.isEdit"
              :placeholder="props.placeholder">
            </Input>
            <div v-if="item.isEdit && item.id" class="pl-4">
              <a class="text-theme-special text-theme-text-hover text-3" @click="editHttpItemConfirm(index,item)">
                {{ t('sure') }}
              </a>
              <Divider type="vertical" />
              <a class="text-theme-special text-theme-text-hover text-3" @click="cancelEdit(item, index)">{{ t('cancel')
              }}</a>
            </div>
          </div>
        </FormItem>
      </div>
      <div class="w-80 pt-1 space-x-2.5" :class="{'mt-6': index === 0}">
        <ButtonAuth
          code="ChannelTest"
          type="link"
          icon="icon-zhihangceshi"
          @click="testConfig(index,item)" />
        <ButtonAuth
          v-if="item.id"
          code="ChannelDelete"
          type="link"
          icon="icon-lajitong"
          @click="deleteConfirm(item.id)" />
        <template v-if="!item.id">
          <ButtonAuth
            code="ChannelAdd"
            type="link"
            icon="icon-tianjia"
            @click="addHttpItem(index,item)" />
        </template>
        <template v-if="item.id">
          <ButtonAuth
            code="ChannelEdit"
            type="link"
            icon="icon-shuxie"
            @click="editAddress(item)" />
        </template>
      </div>
    </Form>
  </div>
</template>
