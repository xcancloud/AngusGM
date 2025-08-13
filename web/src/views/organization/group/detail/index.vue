<script setup lang='ts'>
import { defineAsyncComponent, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { modal, notification } from '@xcan-angus/vue-ui';

import { Detail } from '../types';
import { group } from '@/api';

const DetailCard = defineAsyncComponent(() => import('./detailCard.vue'));
const Associations = defineAsyncComponent(() => import('../association/index.vue'));

const { t } = useI18n();
const groupDetail = ref<Detail>();
const route = useRoute();
const loading = ref(false);

const groupId = route.params.id as string;

const loadGroupDetail = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data }] = await group.getGroupDetail(groupId);
  loading.value = false;
  if (error) {
    return;
  }
  groupDetail.value = data;
};

const updateStatusConfirm = () => {
  modal.confirm({
    centered: true,
    title: groupDetail.value?.enabled
      ? t('common.actions.disable')
      : t('common.actions.enable'),
    content: groupDetail.value?.enabled
      ? t('common.messages.confirmDisable', { name: groupDetail.value?.name })
      : t('common.messages.confirmEnable', { name: groupDetail.value?.name }),
    async onOk () {
      await updateStatus();
    }
  });
};

const updateStatus = async () => {
  if (!groupDetail.value) {
    return;
  }
  const params = [{ id: groupDetail.value.id, enabled: !groupDetail.value.enabled }];
  const [error] = await group.toggleGroupEnabled(params);
  if (error) {
    return;
  }
  notification.success(
    groupDetail.value.enabled
      ? t('common.messages.disableSuccess')
      : t('common.messages.enableSuccess')
  );
  await loadGroupDetail();
};

const editSuccess = () => {
  loadGroupDetail();
};

onMounted(() => {
  loadGroupDetail();
});
</script>

<template>
  <div class="flex space-x-2 min-h-full">
    <DetailCard
      :dataSource="groupDetail"
      @update="updateStatusConfirm"
      @success="editSuccess" />
    <Associations :groupId="groupId" :enabled="groupDetail?.enabled" />
  </div>
</template>
