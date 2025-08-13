<script setup lang="ts">
import { onMounted, reactive, ref, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { modal, Table, PureCard, SearchPanel, notification, Hints, IconCount, IconRefresh, ButtonAuth } from '@xcan-angus/vue-ui';
import { app, GM } from '@xcan-angus/infra';
import { SentType } from '@/enums/enums';
import { Tooltip } from 'ant-design-vue';

import { notice } from '@/api';
import type { NoticeDataType, PaginationType, SearchParamsType } from './types';
import { getQueryParams, getSearchOptions, getTableColumns } from './utils';

const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const router = useRouter();
const { t } = useI18n();

const pagination = reactive<PaginationType>({
  pageSize: 10,
  current: 1,
  total: 0
});

const showCount = ref(true);
const searchParams = ref<SearchParamsType[]>([]);
const loading = ref(false);
const disabled = ref(false);
const noticeData = ref<NoticeDataType[]>([]);

const getNoticeList = async () => {
  if (loading.value) {
    return;
  }
  const params = getQueryParams(pagination, searchParams.value);
  loading.value = true;
  const [error, res] = await notice.getNoticeList(params);
  loading.value = false;
  if (error) {
    return;
  }

  pagination.total = Number(res.data.total || 0);
  noticeData.value = res.data?.list || [];
};

const changePage = async (page) => {
  const { pageSize, current } = page;
  pagination.pageSize = pageSize;
  pagination.current = current;
  disabled.value = true;
  await getNoticeList();
  disabled.value = false;
};

const changeSearchPanel = async (value) => {
  searchParams.value = value;
  pagination.current = 1;
  disabled.value = true;
  await getNoticeList();
  disabled.value = false;
};

const toSendNotice = () => {
  router.push('/messages/notification/send');
};

const deleteNotice = (item: NoticeDataType) => {
  modal.confirm({
    centered: true,
    title: t('common.messages.delete'),
    content: t('notification.messages.confirmDelete'),
    async onOk () {
      const [error] = await notice.deleteNotice([item.id as string]);
      if (error) {
        notification.error(error.message);
      }
      notification.success('common.messages.deleteSuccess');
      if (pagination.current > 1 && noticeData.value.length === 1) {
        pagination.current = pagination.current - 1;
      }
      disabled.value = true;
      await getNoticeList();
      disabled.value = false;
    }
  });
};

const searchOptions = getSearchOptions(t);

const columns = getTableColumns(t);

onMounted(() => {
  getNoticeList();
});

</script>
<template>
  <div class="flex flex-col min-h-full">
    <Hints :text="t('notification.globalTip')" class="mb-1" />
    <PureCard class="p-3.5 flex-1">
      <Statistics
        resource="Notice"
        :barTitle="t('statistics.metrics.newNotification')"
        :router="GM"
        dateType="YEAR"
        :visible="showCount" />
      <div class="flex items-start my-2 justify-between">
        <SearchPanel
          :options="searchOptions"
          class="flex-1"
          @change="changeSearchPanel" />
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
        :dataSource="noticeData"
        :pagination="pagination"
        rowKey="id"
        size="small"
        @change="changePage">
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
              @click="deleteNotice(record)" />
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
              :to="`/messages/notification/${record.id}`"
              class="text-theme-special text-theme-text-hover whitespace-nowrap">
              {{ record.id }}
            </RouterLink>
          </template>
          <template v-if="column.key==='timingDate'">
            {{ record.sendType?.value === SentType.TIMING_SEND ? record.timingDate : '--' }}
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
