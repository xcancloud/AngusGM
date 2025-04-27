<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton } from 'ant-design-vue';
import { Card, Grid } from '@xcan/design';
import { cookie } from '@xcan/utils';

import { email } from '@/api';
import { EmailRecord } from '../../PropsType';

const { t } = useI18n();
const route = useRoute();
const firstLoad = ref<boolean>(true);
const id = route.params.id as string;

const installRecordInfo = ref<EmailRecord>();

const init = () => {
  getEmailRecordInfo();
};

const getEmailRecordInfo = async (): Promise<void> => {
  const [error, { data }] = await email.getEmailDetail(id);
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
      label: t('邮件类型'),
      dataIndex: 'emailType'
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
      label: t('批量发送'),
      dataIndex: 'batch',
      customRender: ({ text }): string => text ? '是' : '否'
    },
    {
      label: t('主题'),
      dataIndex: 'subject'
    },
    {
      label: t('发送内容'),
      dataIndex: 'content'
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
      label: t('发件人地址'),
      dataIndex: 'fromAddr'
    },
    {
      label: t('收件人地址'),
      dataIndex: 'toAddress',
      customRender: ({ text }): string => text?.join(',') || '--'
    },
    {
      label: t('是否HTML'),
      dataIndex: 'html',
      customRender: ({ text }) => text ? '是' : '否'
    },
    {
      label: t('失败原因'),
      dataIndex: 'failureReason'
    },
    {
      label: t('附件'),
      dataIndex: 'attachments'
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
  <Card :title="t('邮件信息')" bodyClass="px-8 py-5">
    <Skeleton
      :loading="firstLoad"
      :title="false"
      :paragraph="{ rows: 11 }">
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
    bodyClass="px-8 py-5"
    class=" flex-1">
    <Skeleton
      :loading="firstLoad"
      :title="false"
      :paragraph="{ rows: 5 }">
      <pre v-if="installRecordInfo?.templateParams">{{
          JSON.stringify(installRecordInfo?.templateParams, null, 2)
      }}</pre>
    </Skeleton>
  </Card>
</template>
