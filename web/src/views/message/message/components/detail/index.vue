<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Tag, Skeleton, Badge } from 'ant-design-vue';
import { Image, Card, Grid } from '@xcan-angus/vue-ui';
import RichEditor from '@/components/RichEditor/index.vue';

import { message } from '@/api';
import { ContentType, ReceiveObjectDataType } from './PropsType';

// import RichBrowser from '@xcan/browser';

const { t } = useI18n();

const route = useRoute();
const content: ContentType = reactive({});

const firstLoad = ref(true);

const getMessageDetail = async () => {
  const id = route.params.id as string;
  const [error, res] = await message.getMessageDetail(id);
  firstLoad.value = false;

  if (error) {
    return;
  }

  content.readNum = res.data.readNum;
  content.sentNum = res.data.sentNum;
  content.status = res.data.status;
  content.timingDate = res.data.timingDate;
  content.fullName = res.data.createdByName;
  content.title = res.data.title;
  content.receiveObjectType = res.data.receiveObjectType;
  content.receiveObjectData = res.data.receiveObjects;
  content.content = res.data.content;
  content.receiveObjectDataLength = res.data.sentNum;
  content.sentType = res.data.sentType.message;
  content.receiveType = res.data.receiveType.message;
  const arr: Array<string> = [];
  (res.data.receiveObjects || []).forEach((ele: ReceiveObjectDataType) => {
    arr.push(ele.name);
  });
  content.receiveObjectDataName = res.data.receiveTenantName || t('allUser');
};

const gridColumns = [
  [
    {
      label: t('sender'),
      dataIndex: 'fullName'
    },
    {
      label: t('sendMethod'),
      dataIndex: 'sentType'
    },
    {
      label: t('receivingMethod'),
      dataIndex: 'receiveType'
    },
    {
      label: t('sendDate'),
      dataIndex: 'timingDate'
    }
  ],
  [
    {
      label: t('receiveNum'),
      dataIndex: 'sentNum'
    },
    {
      label: t('receiveRead'),
      dataIndex: 'readNum'
    },
    {
      label: t('status'),
      dataIndex: 'status'
    }
  ]
];

const obj: {
  [key: string]: string
} = {
  PENDING: 'warning',
  SENT: 'success',
  FAILURE: 'error'
};

const getStatusText = (key: string): string => {
  return obj[key];
};

onMounted(() => {
  getMessageDetail();
});
</script>
<template>
  <div class="flex flex-col min-h-full space-y-2">
    <Card bodyClass="px-8 py-5" :title="t('basicInformation')">
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 4 }">
        <Grid :columns="gridColumns" :dataSource="content">
          <template #status="{text}">
            <Badge :status="getStatusText(text?.value)" :text="text?.message" />
          </template>
          <template #sentNum="{text}">
            {{ +text > -1 ? text : '--' }}
          </template>
        </Grid>
      </Skeleton>
    </Card>
    <Card
      class="flex-1"
      bodyClass="px-5 py-5"
      :title="t('messageContent')">
      <div class="text-center text-4 leading-4 text-theme-title">{{ content.title }}</div>
      <div class="mt-2 text-center text-theme-sub-content mb-4 text-3">
        <Skeleton
          active
          :loading="firstLoad"
          :title="false"
          :paragraph="{ rows: 2 }">
          <span class="mr-2">{{ content.fullName }}</span>
          <span>{{ content.timingDate }}</span>
        </Skeleton>
      </div>
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 12 }">
        <RichEditor :value="content?.content" mode="view" />
      </Skeleton>
    </Card>
    <Card class="flex-1 text-3" bodyClass="p-3 overflow-auto body-h">
      <template #title>
        <div>
          <span class="mr-1">{{ t('recipient') }}:</span>
          <span>{{ content.receiveObjectType?.message }}</span>
        </div>
      </template>
      <template #default>
        <Skeleton
          active
          :loading="firstLoad"
          :title="false"
          :paragraph="{ rows: 20 }">
          <template v-if="content.receiveObjectType?.value == 'USER'">
            <div
              v-for="item in content.receiveObjectData"
              :key="item.id"
              class="flex items-center text-3.5 pt-2 pb-2 first:pt-0 last:pb-0">
              <Image
                class="w-5 h-5 rounded-3xl"
                type="avatar"
                :src="item.avatar" />
              <div class="text-theme-content flex-1 ml-3 truncate text-3">{{ item.name }}</div>
              <div class="text-theme-content">{{ item.mobile }}</div>
            </div>
          </template>
          <template v-else>
            <Tag
              v-for="(item) in content.receiveObjectData"
              :key="item.id"
              class="mr-4 mb-4">
              <div class="max-w-30 truncate" :title="item.name">{{ item.name }}</div>
            </Tag>
          </template>
        </Skeleton>
      </template>
    </Card>
  </div>
</template>

<style scoped>
:deep(.body-h) {
  height: calc(100% - 48px);
}

.receiver :deep(.ant-tag) {
  @apply px-4.5 py-3;
}

</style>
