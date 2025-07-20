<script setup lang="ts">
import { onMounted, reactive, ref, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import {
  modal,
  Table,
  PureCard,
  SearchPanel,
  notification,
  Hints,
  IconCount,
  IconRefresh,
  ButtonAuth
} from '@xcan-angus/vue-ui';
import { app, GM } from '@xcan-angus/tools';
import { Tooltip } from 'ant-design-vue';

import { notice } from '@/api';
import type { FormDataType } from './interface';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const router = useRouter();
const { t } = useI18n();

const columns = [
  {
    key: 'id',
    dataIndex: 'id',
    title: 'ID',
    width: '13%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'content',
    dataIndex: 'content',
    title: t('noticeContent'),
    ellipsis: true
  },
  {
    key: 'scope',
    dataIndex: 'scope',
    title: t('noticeScope'),
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'sendType',
    dataIndex: 'sendType',
    title: t('sendType'),
    width: '8%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'timingDate',
    dataIndex: 'timingDate',
    title: t('timingDate'),
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'createdByName',
    dataIndex: 'createdByName',
    title: t('sender'),
    width: '14%'
  },
  {
    key: 'createdDate',
    dataIndex: 'createdDate',
    title: t('createdDate'),
    width: '12%',
    customCell: () => {
      return { style: 'white-space:nowrap;' };
    }
  },
  {
    key: 'action',
    dataIndex: 'action',
    title: t('operation'),
    width: '5%',
    align: 'center'
  }
];

const searchOpt = [
  {
    type: 'input',
    allowClear: true,
    valueKey: 'content',
    placeholder: t('noticeContentPlaceholder')
  },
  {
    type: 'select-enum',
    allowClear: true,
    valueKey: 'scope',
    enumKey: 'NoticeScope',
    placeholder: t('noticeScope')
  },
  {
    type: 'select-enum',
    allowClear: true,
    valueKey: 'sendType',
    enumKey: 'SentType',
    placeholder: t('sendType')
  }
];

const showCount = ref(true);
const searchParams = ref([]);

const pagination = reactive({
  pageSize: 10,
  current: 1,
  total: 0
});

const dataSource = ref<FormDataType[]>([]);

const getParams = () => {
  const { pageSize, current } = pagination;
  return {
    pageSize,
    pageNo: current,
    filters: searchParams.value,
    fullTextSearch: true
  };
};

const loading = ref(false);
const disabled = ref(false);

const getNoticeList = async () => {
  if (loading.value) {
    return;
  }
  const params = getParams();
  loading.value = true;
  const [error, res] = await notice.getNoticeList(params);
  loading.value = false;
  if (error) {
    return;
  }
  dataSource.value = res.data?.list || [];
  pagination.total = res.data.total;
};

const pageChange = async (page) => {
  const { pageSize, current } = page;
  pagination.pageSize = pageSize;
  pagination.current = current;
  disabled.value = true;
  await getNoticeList();
  disabled.value = false;
};

const changePanel = async (value) => {
  searchParams.value = value;
  pagination.current = 1;
  disabled.value = true;
  await getNoticeList();
  disabled.value = false;
};

const toSendNotice = () => {
  router.push('/messages/notice/send');
};

const delNotice = (item: FormDataType) => {
  modal.confirm({
    centered: true,
    title: t('deleteNotice'),
    content: t('delNoticeTip'),
    async onOk () {
      const [error] = await notice.deleteNotice([item.id as string]);
      if (error) {
        notification.error(error.message);
      }
      notification.success('删除成功');
      if (pagination.current > 1 && dataSource.value.length === 1) {
        pagination.current = pagination.current - 1;
      }
      disabled.value = true;
      await getNoticeList();
      disabled.value = false;
    }
  });
};

onMounted(() => {
  getNoticeList();
});

</script>
<template>
  <div class="flex flex-col min-h-full">
    <Hints :text="t('globalNoticeTip')" class="mb-1" />
    <PureCard class="p-3.5 flex-1">
      <Statistics
        :visible="showCount"
        resource="Notice"
        :barTitle="t('notice')"
        :geteway="GM" />
      <div class="flex items-start my-2 justify-between">
        <SearchPanel
          :options="searchOpt"
          class="flex-1"
          @change="changePanel" />
        <div class="flex items-center space-x-2">
          <ButtonAuth
            code="NoticePublish"
            type="primary"
            icon="icon-fabu"
            @click="toSendNotice" />
          <IconCount v-model:value="showCount" />
          <IconRefresh
            :loading="loading"
            :disabled="disabled"
            @click="getNoticeList" />
        </div>
      </div>
      <Table
        :columns="columns"
        :loading="loading"
        :dataSource="dataSource"
        :pagination="pagination"
        @change="pageChange">
        <template #bodyCell="{record, column, text}">
          <template v-if="column.dataIndex === 'content'">
            <Tooltip :title="text" placement="topLeft">
              <div class="truncate">{{ text }}</div>
            </Tooltip>
          </template>
          <template v-if="column.key === 'action'">
            <ButtonAuth
              code="NoticeDelete"
              type="text"
              icon="icon-lajitong"
              @click="delNotice(record)" />
          </template>
          <template v-if="column.key === 'sendType'">
            {{ record.sendType.message }}
          </template>
          <template v-if="column.key === 'scope'">
            {{ record.scope.message }}
          </template>
          <template v-if="column.key === 'id'">
            <RouterLink
              v-if="app.has('NoticeDetail')"
              :to="`/messages/notice/${record.id}`"
              class="text-theme-special text-theme-text-hover whitespace-nowrap">
              {{ record.id }}
            </RouterLink>
          </template>
          <!-- 定时发送 -->
          <template v-if="column.key==='timingDate'">
            {{ record.sendType?.value === 'TIMING_SEND' ? record.timingDate : '--' }}
          </template>
        </template>
      </Table>
    </PureCard>
  </div>
</template>
<style scoped>
.ant-form-horizontal :deep(.ant-form-item-control) {
  flex: 1 1 50%;
  max-width: 600px;
}
</style>
