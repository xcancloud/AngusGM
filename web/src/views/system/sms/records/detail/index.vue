<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton } from 'ant-design-vue';
import { Card, Grid } from '@xcan-angus/vue-ui';
import { cookieUtils } from '@xcan-angus/infra';

import { sms } from '@/api';
import { SmsRecord } from '../types';

const { t } = useI18n();
const route = useRoute();
const firstLoad = ref<boolean>(true);
const id = route.params.id as string;

const installRecordInfo = ref<SmsRecord>();

const init = () => {
  getSmsRecordInfo();
};

const getSmsRecordInfo = async (): Promise<void> => {
  const [error, { data }] = await sms.getSmsDetail(id);
  firstLoad.value = false;
  if (error) {
    return;
  }
  installRecordInfo.value = data;
};

onMounted(() => {
  init();
});

const gridColumns = [
  [
    {
      label: 'ID',
      dataIndex: 'id'
    },
    {
      label: t('sms.columns.sendStatus'),
      dataIndex: 'sendStatus'
    },
    {
      label: t('sms.columns.sendTenantId'),
      dataIndex: 'sendTenantId'
    },
    {
      label: t('sms.columns.sendUserId'),
      dataIndex: 'sendId'
    },
    {
      label: t('sms.columns.templateCode'),
      dataIndex: 'templateCode'
    },
    {
      label: t('sms.columns.urgent'),
      dataIndex: 'urgent',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('sms.columns.verificationCode'),
      dataIndex: 'verificationCode',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('sms.columns.failureReason'),
      dataIndex: 'failureReason'
    }
  ],
  [
    {
      label: t('sms.columns.actualSendDate'),
      dataIndex: 'actualSendDate'
    },
    {
      label: t('sms.columns.expectedSendDate'),
      dataIndex: 'expectedSendDate'
    },
    {
      label: t('sms.columns.outId'),
      dataIndex: 'outId'
    },
    {
      label: t('sms.columns.bizKey'),
      dataIndex: 'bizKey'
    },
    {
      label: t('sms.columns.language'),
      dataIndex: 'language',
      customRender: ({ text }): string => text?.message || '--'
    },
    {
      label: t('sms.columns.batch'),
      dataIndex: 'batch',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    }
  ]
];

const getSendStatusColor = (value: 'SUCCESS' | 'PENDING' | 'FAILURE') => {
  switch (value) {
    case 'SUCCESS': // 成功
      return 'rgba(82,196,26,1)';
    case 'PENDING': // 待处理
      return 'rgba(255,165,43,1)';
    case 'FAILURE': // 失败
      return 'rgba(245,34,45,1)';
  }
};
</script>
<template>
  <Card :title="t('common.labels.basicInformation')" bodyClass="px-8 py-5">
    <Skeleton
      :loading="firstLoad"
      :title="false"
      :paragraph="{ rows: 7 }">
      <Grid
        :columns="gridColumns"
        :dataSource="installRecordInfo">
        <template #sendStatus="{text}">
          <Badge :color="getSendStatusColor(text?.value)" :text="text?.message" />
        </template>
        <template #attachments="{text}">
          <a
            v-for="(item,index) in text"
            :key="index"
            class="mr-4 text-theme-special"
            :href="`${item.url}&access_token=${cookieUtils.get('access_token')}`">{{ item.name }}</a>
        </template>
      </Grid>
    </Skeleton>
  </Card>
  <Card
    :title="t('sms.titles.sendParams')"
    class="my-2"
    bodyClass="px-8 py-5">
    <pre v-if="installRecordInfo?.inputParam">{{ JSON.stringify(installRecordInfo?.inputParam, null, 2) }}</pre>
  </Card>
  <Card
    :title="t('sms.titles.returnParams')"
    bodyClass="px-8 py-5">
    <pre v-if="installRecordInfo?.thirdOutputParam">{{
        JSON.stringify(JSON.parse(installRecordInfo?.thirdOutputParam?.replace(/\\/g, '')), null, 2)
    }}</pre>
  </Card>
</template>
