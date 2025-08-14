<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import clipboard from 'vue-clipboard3';
import { Skeleton, Tooltip } from 'ant-design-vue';
import { Card, Grid, Icon } from '@xcan-angus/vue-ui';
import { EditionType, appContext } from '@xcan-angus/infra';
import dayjs from 'dayjs';

import { edition } from '@/api';
import { InstallEdition, GridColumns } from './types';
import {
  getEditionTypeIcon,
  createCloudEditionColumns,
  createPrivateEditionColumns
} from './utils';
import grayImg from './images/gray1.png';

import UpdatableVersion from './updatable.vue';

const { t } = useI18n();

/**
 * Loading state for component initialization
 * Shows skeleton loading while fetching data
 */
const loading = ref<boolean>(true);
/**
 * Copy button text content
 * Changes based on copy operation result
 */
const copyContent = ref<string>('version.messages.copy');
/**
 * Currently installed edition information
 * Contains all details about the installed software
 */
const installEdition = ref<InstallEdition>();
/**
 * Current edition type
 * Determines which column configuration to use
 */
const editionType = ref<string>();

/**
 * Copy license number to clipboard
 * Copies the license number and updates copy button text
 */
const copy = async () => {
  const { toClipboard } = clipboard();

  try {
    if (!installEdition.value) {
      return;
    }

    await toClipboard(installEdition.value.licenseNo);
    copyContent.value = 'version.messages.copySuccess';
  } catch (error) {
    copyContent.value = 'version.messages.copyFailure';
  }
};

/**
 * Reset copy button text on mouse enter
 * Restores original copy button text when hovering
 */
const moveIn = () => {
  copyContent.value = 'version.messages.copy';
};

/**
 * Computed tooltip title
 * Dynamically displays copy button tooltip text
 */
const tooltipTitle = computed(() => {
  return t(copyContent.value);
});

/**
 * Computed columns configuration
 * Returns appropriate column configuration based on edition type
 */
const columns = computed((): GridColumns => {
  if (editionType.value === EditionType.CLOUD_SERVICE) {
    return createCloudEditionColumns(t);
  }
  return createPrivateEditionColumns(t);
});

/**
 * Initialize component
 * Sets up edition type and loads installed version data
 */
const init = async () => {
  editionType.value = appContext.getEditionType();
  await loadInstalledVersion();
};

/**
 * Load installed version information
 * Fetches current installation details from API
 */
const loadInstalledVersion = async (): Promise<void> => {
  try {
    const [error, { data }] = await edition.getEditionInstalled('AngusGM');
    if (error) {
      return;
    }

    installEdition.value = data;
  } finally {
    loading.value = false;
  }
};

/**
 * Initialize component on mount
 * Loads initial data when component is mounted
 */
onMounted(() => {
  init();
});
</script>

<template>
  <!-- System Version Information Card -->
  <Card
    :title="t('version.titles.systemVersion')"
    class="version-card shadow-lg hover:shadow-xl transition-all duration-300 bg-gradient-to-br from-white to-blue-50"
    bodyClass="flex px-10 py-8 items-center space-x-24 pr-32">
    <!-- Version Icon Image -->
    <div class="version-icon-container">
      <img :src="grayImg" class="version-icon" />
      <div class="icon-glow"></div>
    </div>

    <!-- Version Information Grid -->
    <Skeleton
      active
      :loading="loading"
      :title="false"
      :paragraph="{ rows: 9 }">
      <Grid
        :columns="columns"
        :dataSource="installEdition"
        class="version-grid">
        <!-- License Number with Copy Functionality -->
        <template #licenseNo="{text}">
          <div class="license-item">
            <span class="license-text">{{ text }}</span>
            <Tooltip :title="tooltipTitle">
              <Icon
                v-if="text"
                icon="icon-fuzhi"
                class="copy-icon"
                @mouseenter="moveIn"
                @click="copy" />
            </Tooltip>
          </div>
        </template>

        <!-- Edition Type with Icon -->
        <template #editionType="{text}">
          <div class="edition-type">
            <Icon :icon="getEditionTypeIcon(text?.value)" class="edition-icon" />
            <span class="edition-text">{{ text?.message }}</span>
          </div>
        </template>

        <!-- Goods Type Display -->
        <template #goodsType="{text}">
          <span class="goods-type">{{ text?.message }}</span>
        </template>

        <!-- End Date with Remaining Days -->
        <template #endDate="{text}">
          <div class="end-date">
            <span class="date-text">{{ text }}</span>
            <span class="remaining-days">
              {{ t('version.messages.remainingDays', { days: dayjs(text).diff(dayjs().format(),'day') }) }}
            </span>
          </div>
        </template>
      </Grid>
    </Skeleton>
  </Card>

  <!-- Updatable Version Component -->
  <UpdatableVersion
    class="mt-6"
    :currentVersion="installEdition?.goodsVersion"
    :installGoodsCode="installEdition?.goodsCode" />
</template>

<style scoped>
/* Version card styling */
.version-card {
  border-radius: 0.25rem;
  border: 1px solid #E5E7EB;
  overflow: hidden;
}

.version-card :deep(.ant-card-head) {
  background: linear-gradient(135deg, #3B82F6 0%, #1D4ED8 100%);
  border-bottom: none;
  padding: 0.25rem 2rem;
}

.version-card :deep(.ant-card-head-title) {
  color: white;
  font-size: 1.25rem;
  font-weight: 600;
}

/* Version icon container */
.version-icon-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.version-icon {
  width: 15rem;
  height: 15rem;
  border-radius: 1rem;
  box-shadow: 0 8px 25px 0 rgba(59, 130, 246, 0.2);
  transition: all 0.3s ease;
}

.version-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 35px 0 rgba(59, 130, 246, 0.3);
}

.icon-glow {
  position: absolute;
  inset: -1rem;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.1) 0%, transparent 70%);
  border-radius: 0.25rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.version-icon-container:hover .icon-glow {
  opacity: 1;
}

/* Version grid styling */
.version-grid {
  flex: 1;
}

.version-grid :deep(.ant-grid-row) {
  margin-bottom: 1rem;
}

.version-grid :deep(.ant-grid-col) {
  padding: 0.75rem 1rem;
  background: white;
  border-radius: 0.75rem;
  border: 1px solid #F3F4F6;
  transition: all 0.3s ease;
}

.version-grid :deep(.ant-grid-col:hover) {
  border-color: #3B82F6;
  box-shadow: 0 4px 12px 0 rgba(59, 130, 246, 0.1);
  transform: translateY(-1px);
}

.version-grid :deep(.ant-grid-col-label) {
  font-weight: 600;
  color: #374151;
  font-size: 0.9rem;
}

.version-grid :deep(.ant-grid-col-content) {
  color: #6B7280;
  font-weight: 500;
}

/* License item styling */
.license-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.license-text {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  background: #F3F4F6;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
}

.copy-icon {
  color: #6B7280;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0.25rem;
  border-radius: 0.375rem;
}

.copy-icon:hover {
  color: #3B82F6;
  background: rgba(59, 130, 246, 0.1);
  transform: scale(1.1);
}

/* Edition type styling */
.edition-type {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.edition-icon {
  font-size: 1.25rem;
  color: #3B82F6;
}

.edition-text {
  font-weight: 500;
  color: #374151;
}

/* Goods type styling */
.goods-type {
  font-weight: 500;
  color: #374151;
}

/* End date styling */
.end-date {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 0.5rem;
}

.date-text {
  font-weight: 300;
  color: #374151;
}

.remaining-days {
  font-size: 0.875rem;
  color: #6B7280;
  font-style: italic;
}
</style>
