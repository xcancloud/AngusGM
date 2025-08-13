<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import clipboard from 'vue-clipboard3';
import { Skeleton, Tooltip } from 'ant-design-vue';
import { Card, Grid, Icon } from '@xcan-angus/vue-ui';
import { EditionType, appContext } from '@xcan-angus/infra';
import dayjs from 'dayjs';

import { edition } from '@/api';
import { InstallEdition } from './types';
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
 * Grid columns configuration for cloud service edition
 * Defines the layout and fields for cloud service version display
 */
const cloudEditionColumns = [
  [
    {
      label: t('version.columns.editionType'),
      dataIndex: 'editionType'
    },
    {
      label: t('version.columns.goodsCode'),
      dataIndex: 'goodsCode'
    },
    {
      label: t('version.columns.goodsVersion'),
      dataIndex: 'goodsVersion'
    },
    {
      label: t('version.columns.provider'),
      dataIndex: 'provider'
    },
    {
      label: t('version.columns.publisher'),
      dataIndex: 'issuer'
    },
    {
      label: t('version.columns.holder'),
      dataIndex: 'holder'
    },
    {
      label: t('version.columns.beginDate'),
      dataIndex: 'beginDate'
    },
    {
      label: t('version.columns.endDate'),
      dataIndex: 'endDate'
    }
  ]
];

/**
 * Grid columns configuration for private edition
 * Defines the layout and fields for private version display
 * Includes additional fields like license number and MD5 signature
 */
const privateEditionColumns = [
  [
    {
      label: t('version.columns.editionType'),
      dataIndex: 'editionType'
    },
    {
      label: t('version.columns.goodsCode'),
      dataIndex: 'goodsCode'
    },
    {
      label: t('version.columns.goodsVersion'),
      dataIndex: 'goodsVersion'
    },
    {
      label: t('version.columns.provider'),
      dataIndex: 'provider'
    },
    {
      label: t('version.columns.publisher'),
      dataIndex: 'issuer'
    },
    {
      label: t('version.columns.holder'),
      dataIndex: 'holder'
    },
    {
      label: t('version.columns.licenseNo'),
      dataIndex: 'licenseNo'
    },
    {
      label: t('version.columns.beginDate'),
      dataIndex: 'beginDate'
    },
    {
      label: t('version.columns.endDate'),
      dataIndex: 'endDate'
    },
    {
      label: t('version.columns.signature'),
      dataIndex: 'signature'
    }
  ]
];

/**
 * Get edition type icon based on edition type key
 * Returns appropriate icon class for different edition types
 */
const getEditionTypeIcon = (key: string) => {
  switch (key) {
    case EditionType.DATACENTER:
      return 'icon-shujuzhongxin';
    case EditionType.CLOUD_SERVICE:
      return 'icon-yunfuwu';
    case EditionType.ENTERPRISE:
      return 'icon-qiye';
    case EditionType.COMMUNITY:
      return 'icon-shequ';
  }
};

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
const columns = computed(() => {
  if (editionType.value === EditionType.CLOUD_SERVICE) {
    return cloudEditionColumns;
  }
  return privateEditionColumns;
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
  <Card :title="t('version.titles.systemVersion')" bodyClass="flex px-8 py-5 items-center space-x-20 pr-30">
    <!-- Version Icon Image -->
    <img :src="grayImg" class="w-60 h-60" />

    <!-- Version Information Grid -->
    <Skeleton
      active
      :loading="loading"
      :title="false"
      :paragraph="{ rows: 9 }">
      <Grid :columns="columns" :dataSource="installEdition">
        <!-- License Number with Copy Functionality -->
        <template #licenseNo="{text}">
          {{ text }}
          <Tooltip :title="tooltipTitle">
            <Icon
              v-if="text"
              icon="icon-fuzhi"
              class="ml-1.5 mr-0.5  mb-0.5 text-3 leading-3 cursor-pointer content-primary-text-hover"
              @mouseenter="moveIn"
              @click="copy" />
          </Tooltip>
        </template>

        <!-- Edition Type with Icon -->
        <template #editionType="{text}">
          <Icon :icon="getEditionTypeIcon(text?.value)" class="text-4 -mt-0.5" />
          {{ text?.message }}
        </template>

        <!-- Goods Type Display -->
        <template #goodsType="{text}">
          {{ text?.message }}
        </template>

        <!-- End Date with Remaining Days -->
        <template #endDate="{text}">
          {{ text }} ( {{ t('version.messages.remainingDays', { days: dayjs(text).diff(dayjs().format(),'day') }) }} )
        </template>
      </Grid>
    </Skeleton>
  </Card>

  <!-- Updatable Version Component -->
  <UpdatableVersion
    class="mt-2"
    :currentVersion="installEdition?.goodsVersion"
    :installGoodsCode="installEdition?.goodsCode" />
</template>
