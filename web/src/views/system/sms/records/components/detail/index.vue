<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton } from 'ant-design-vue';
import { Card, Grid } from '@xcan-angus/vue-ui';
import { cookie } from '@xcan-angus/tools';

import { sms } from '@/api';
import { SmsRecord } from '../../PropsType';

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
      label: t('发送状态'),
      dataIndex: 'sendStatus'
    },
    {
      label: t('发送租户ID'),
      dataIndex: 'sendTenantId'
    },
    {
      label: t('发送用户ID'),
      dataIndex: 'sendId'
    },
    {
      label: t('模板编码'),
      dataIndex: 'templateCode'
    },
    {
      label: t('是否加急'),
      dataIndex: 'urgent',
      customRender: ({ text }): string => text ? '是' : '否'
    },
    {
      label: t('是否验证码'),
      dataIndex: 'verificationCode',
      customRender: ({ text }): string => text ? '是' : '否'
    },
    {
      label: t('失败原因'),
      dataIndex: 'failureReason'
    }
  ],
  [
    {
      label: t('发送时间'),
      dataIndex: 'actualSendDate'
    },
    {
      label: t('期望发送时间'),
      dataIndex: 'expectedSendDate'
    },
    {
      label: t('业务ID'),
      dataIndex: 'outId'
    },
    {
      label: t('业务Key'),
      dataIndex: 'bizKey'
    },
    {
      label: t('语言'),
      dataIndex: 'language',
      customRender: ({ text }): string => text?.message || '--'
    },
    {
      label: t('批量发送'),
      dataIndex: 'batch',
      customRender: ({ text }): string => text ? '是' : '否'
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
  <Card :title="t('基本信息')" bodyClass="px-8 py-5">
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
            :href="`${item.url}&access_token=${cookie.get('access_token')}`">{{ item.name }}</a>
        </template>
      </Grid>
    </Skeleton>
  </Card>
  <Card
    :title="t('发送参数')"
    class="my-2"
    bodyClass="px-8 py-5">
    <pre v-if="installRecordInfo?.inputParam">{{ JSON.stringify(installRecordInfo?.inputParam, null, 2) }}</pre>
  </Card>
  <Card
    :title="t('返回参数')"
    bodyClass="px-8 py-5">
    <pre v-if="installRecordInfo?.thirdOutputParam">{{
        JSON.stringify(JSON.parse(installRecordInfo?.thirdOutputParam?.replace(/\\/g, '')), null, 2)
    }}</pre>
  </Card>
</template>
