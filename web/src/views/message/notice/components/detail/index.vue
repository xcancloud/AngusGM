<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { modal, Card, Grid, notification } from '@xcan/design';
import { security } from '@xcan/security';
import { useRoute, useRouter } from 'vue-router';
import { Button, Skeleton } from 'ant-design-vue';

import { notice } from '@/api';

const router = useRouter();
const route = useRoute();
const { t } = useI18n();
const columns = [
  [
    {
      dataIndex: 'id',
      label: 'ID'
    },
    {
      dataIndex: 'sendType',
      label: t('sendType')
    },
    {
      dataIndex: 'createdByName',
      label: t('sender')
    },
    {
      dataIndex: 'createdDate',
      label: t('createdDate')
    }
  ],
  [
    {
      dataIndex: 'timingDate',
      label: t('timingDate')
    },
    {
      dataIndex: 'expirationDate',
      label: t('expirationDate')
    },
    {
      dataIndex: 'scope',
      label: t('noticeScope')
    }
  ]
];

const dataSource = ref();
const id = ref('');

const firstLoad = ref(true);

const getDetaiData = async () => {
  const [error, { data = {} }] = await notice.getNoticeDetail(id.value);
  firstLoad.value = false;
  if (error) {
    return;
  }
  dataSource.value = data || {};
};

const del = () => {
  modal.confirm({
    centered: true,
    title: t('deleteNotice'),
    content: t('delNoticeTip'),
    async onOk () {
      const [error] = await notice.deleteNotice([id.value]);
      if (error) {
        notification.error(error.message);
      }
      notification.success('删除成功');
      router.replace({ path: '/messages/notice' });
    }
  });
};

onMounted(() => {
  id.value = route.params.id as string || '';
  getDetaiData();
});
</script>
<template>
  <div class="flex flex-col min-h-full">
    <Card
      class="text-3 mb-2"
      bodyClass="px-8 py-5"
      :title="t('basicInformation')">
      <template v-if="!firstLoad" #rightExtra>
        <div>
          <Button
            :disabled="!security.has('NoticeDelete')"
            size="small"
            @click="del">
            {{ security.getName('NoticeDelete') }}
          </Button>
        </div>
      </template>
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
      :title="t('noticeContent')">
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
