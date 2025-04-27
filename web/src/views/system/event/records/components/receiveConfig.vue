<script setup lang="ts">
import { watch, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Popover, Tag } from 'ant-design-vue';
import { Modal, Grid, NoData } from '@xcan/design';

import { event } from '@/api';

interface Props {
  visible: boolean,
  eventCode: string,
  eKey: string
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  eventCode: '',
  eKey: ''
});

const emit = defineEmits<{(e: 'update:visible', value: boolean): void }>();

const dataSource = reactive<Record<string, any>>({});
const columns = ref<Record<string, any>[][]>([[]]);

const init = async () => {
  const [error, res] = await event.getEventChannel(props.eventCode, props.eKey);
  if (error) {
    return;
  }

  if (!res.data && res.data?.length === 0) {
    return;
  }
  res.data.forEach(channel => {
    dataSource[channel.channelType.value] = channel.channels;
    columns.value[0].push({
      dataIndex: channel.channelType.value,
      label: channel.channelType.message
    });
  });
};

const { t } = useI18n();

const handleOk = async () => {
  emit('update:visible', false);
};
const handleCancel = () => {
  emit('update:visible', false);
};

watch(() => props.visible, newValue => {
  if (newValue) {
    init();
  }
}, {
  immediate: true
});

</script>

<template>
  <Modal
    :visible="visible"
    :title="t('systemEventLog.table-2')"
    :centered="true"
    width="600px"
    @ok="handleOk"
    @cancel="handleCancel">
    <Grid
      :columns="columns"
      :dataSource="dataSource"
      class="max-h-100 overflow-auto">
      <template #DINGTALK="{text}">
        <Popover v-for="channel in text" :key="channel.id">
          <Tag class="mb-1">
            {{ channel.name }}
          </Tag>
          <template #content>
            <div class="max-w-100 break-words">{{ channel.address }}</div>
          </template>
        </Popover>
      </template>
      <template #WEBHOOK="{text}">
        <Popover v-for="channel in text" :key="channel.id">
          <Tag class="mb-1">
            {{ channel.name }}
          </Tag>
          <template #content>
            <div class="max-w-100 break-words">{{ channel.address }}</div>
          </template>
        </Popover>
      </template>
      <template #EMAIL="{text}">
        <Popover v-for="channel in text" :key="channel.id">
          <Tag class="mb-1">
            {{ channel.name }}
          </Tag>
          <template #content>
            <div class="max-w-100 break-words">{{ channel.address }}</div>
          </template>
        </Popover>
      </template>
      <template #WECHAT="{text}">
        <Popover v-for="channel in text" :key="channel.id">
          <Tag class="mb-1">
            {{ channel.name }}
          </Tag>
          <template #content>
            <div class="max-w-100 break-words">{{ channel.address }}</div>
          </template>
        </Popover>
      </template>
    </Grid>
    <NoData v-if="!columns[0].length" />
  </Modal>
</template>
