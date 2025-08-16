<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton, Tag } from 'ant-design-vue';
import { Card, Grid, Image } from '@xcan-angus/vue-ui';
import RichEditor from '@/components/RichEditor/index.vue';

import { message } from '@/api';
import { ReceiveObjectData, ReceiveObjectDataType } from '../types';
import { createDetailGridColumns, getStatusText } from '../utils';

const { t } = useI18n();

const route = useRoute();

/**
 * Message content data structure
 * Stores all message details including metadata and recipient information
 */
const content: ReceiveObjectData = reactive({});
/**
 * Flag to track if this is the first data load
 * Used to show skeleton loading state only on initial load
 */
const firstLoad = ref(true);

/**
 * Fetch message detail from API
 * Retrieves complete message information including recipients and content
 */
const getMessageDetail = async () => {
  const id = route.params.id as string;

  try {
    const [error, res] = await message.getMessageDetail(id);
    if (error) {
      return;
    }

    // Update content with message details
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

    // Extract recipient names for display
    const arr: Array<string> = [];
    (res.data.receiveObjects || []).forEach((ele: ReceiveObjectDataType) => {
      arr.push(ele.name);
    });
    content.receiveObjectDataName = res.data.receiveTenantName || t('messages.allUsers');
  } finally {
    firstLoad.value = false;
  }
};

/**
 * Grid column configuration for basic message information
 * Organized in two rows for better layout and readability
 */
const gridColumns = computed(() => createDetailGridColumns());

/**
 * Initialize component on mount
 * Loads message detail data when component is mounted
 */
onMounted(() => {
  getMessageDetail();
});
</script>

<template>
  <div class="flex flex-col min-h-full space-y-2">
    <!-- Basic Message Information Card -->
    <Card bodyClass="px-8 py-5" :title="t('messages.basicInfo')">
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 4 }">
        <Grid :columns="gridColumns" :dataSource="content">
          <!-- Status Badge Template -->
          <template #status="{text}">
            <Badge :status="getStatusText(text?.value) as any" :text="text?.message" />
          </template>
          <!-- Sent Number Template -->
          <template #sentNum="{text}">
            {{ +text > -1 ? text : '--' }}
          </template>
        </Grid>
      </Skeleton>
    </Card>

    <!-- Message Content Card -->
    <Card
      class="flex-1"
      bodyClass="px-5 py-5"
      :title="t('messages.columns.content')">
      <div class="text-center text-4 leading-4 text-theme-title">{{ content.title }}</div>
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 12 }">
        <RichEditor :value="content?.content" mode="view" />
      </Skeleton>
    </Card>

    <!-- Recipients Information Card -->
    <Card class="flex-1 text-3" bodyClass="p-3 overflow-auto body-h">
      <template #title>
        <div>
          <span class="mr-1">{{ t('messages.columns.recipient') }}:</span>
          <span>{{ content.receiveObjectType?.message }}</span>
        </div>
      </template>
      <template #default>
        <Skeleton
          active
          :loading="firstLoad"
          :title="false"
          :paragraph="{ rows: 20 }">
          <!-- User Recipients Display -->
          <template v-if="content.receiveObjectType?.value === 'USER'">
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
          <!-- Non-User Recipients Display -->
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
/**
 * Custom styling for card body height
 * Ensures proper layout and scrolling for recipient information
 */
:deep(.body-h) {
  height: calc(100% - 48px);
}

/**
 * Custom styling for receiver tags
 * Provides consistent padding and spacing for tag elements
 */
.receiver :deep(.ant-tag) {
  @apply px-4.5 py-3;
}
</style>
