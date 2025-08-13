<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card, Grid } from '@xcan-angus/vue-ui';
import { useRoute } from 'vue-router';
import { Skeleton } from 'ant-design-vue';

import { notice } from '@/api';

const route = useRoute();
const { t } = useI18n();

const dataSource = ref();
const id = ref('');
const firstLoad = ref(true);

const getDetailData = async () => {
  const [error, { data = {} }] = await notice.getNoticeDetail(id.value);
  firstLoad.value = false;
  if (error) {
    return;
  }
  dataSource.value = data || {};
};

const columns = [
  [
    {
      dataIndex: 'id',
      label: 'ID'
    },
    {
      dataIndex: 'sendType',
      label: t('notification.columns.sendType')
    },
    {
      dataIndex: 'createdByName',
      label: t('common.columns.createdByName')
    },
    {
      dataIndex: 'createdDate',
      label: t('common.columns.createdDate')
    }
  ],
  [
    {
      dataIndex: 'timingDate',
      label: t('notification.columns.timingDate')
    },
    {
      dataIndex: 'expirationDate',
      label: t('notification.columns.expiredDate')
    },
    {
      dataIndex: 'scope',
      label: t('notification.columns.scope')
    }
  ]
];

onMounted(() => {
  id.value = route.params.id as string || '';
  getDetailData();
});
</script>
<template>
  <div class="flex flex-col min-h-full">
    <Card
      class="text-3 mb-2"
      bodyClass="px-8 py-5"
      :title="t('notification.basicInfo')">
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 4 }">
        <Grid :columns="columns" :dataSource="dataSource">
          <template #sendType="{text}">{{ text?.message }}</template>
          <template #scope="{text}">{{ text?.message }}</template>
          <template #timingDate="{text}">
            {{ dataSource?.sendType?.value === 'TIMING_SEND' ? text : '--' }}
          </template>
        </Grid>
      </Skeleton>
    </Card>
    <Card
      class="text-3 flex-1"
      bodyClass="px-8 py-5"
      :title="t('notification.columns.content')">
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 12 }">
        <div>{{ dataSource?.content }}</div>
      </Skeleton>
    </Card>
  </div>
</template>
