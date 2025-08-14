<script setup lang='ts'>
import { defineAsyncComponent, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';

import { Detail } from '../types';
import {
  loadGroupDetail as loadGroupDetailUtil, updateStatusConfirm as updateStatusConfirmUtil
} from '../utils';

// Lazy load child components for better performance
const DetailCard = defineAsyncComponent(() => import('./detailCard.vue'));
const Associations = defineAsyncComponent(() => import('../association/index.vue'));

const { t } = useI18n();

/**
 * Reactive data for group detail information
 */
const groupDetail = ref<Detail>();

/**
 * Current route instance for accessing route parameters
 */
const route = useRoute();

/**
 * Loading state to prevent multiple simultaneous API calls
 */
const loading = ref(false);

/**
 * Extract group ID from route parameters
 */
const groupId = route.params.id as string;

/**
 * Load group detail data from API
 * Includes loading state management to prevent duplicate requests
 */
const loadGroupDetail = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const data = await loadGroupDetailUtil(groupId);
  loading.value = false;
  if (data) {
    groupDetail.value = data;
  }
};

/**
 * Handle group status update confirmation
 * Triggers the status update utility function with current group data
 */
const updateStatusConfirm = () => {
  if (groupDetail.value) {
    updateStatusConfirmUtil(groupDetail.value.id, groupDetail.value.name, groupDetail.value.enabled, t,
      undefined, undefined, undefined, undefined, loadGroupDetail);
  }
};

/**
 * Callback function called after successful edit operation
 * Reloads group detail to reflect changes
 */
const editSuccess = () => {
  loadGroupDetail();
};

/**
 * Initialize component data on mount
 * Load group detail information when component is created
 */
onMounted(() => {
  loadGroupDetail();
});
</script>

<template>
  <!-- Main container with flexbox layout for detail card and associations -->
  <div class="flex space-x-2 min-h-full">
    <!-- Group detail information card -->
    <DetailCard
      :dataSource="groupDetail"
      @update="updateStatusConfirm"
      @success="editSuccess" />
    <!-- Group associations component showing related users/tags -->
    <Associations :groupId="groupId" :enabled="groupDetail?.enabled" />
  </div>
</template>
