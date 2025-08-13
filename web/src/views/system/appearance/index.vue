<script setup lang="ts">
import { defineAsyncComponent, onMounted, ref, watch } from 'vue';
import { Button, Divider, Skeleton } from 'ant-design-vue';
import { AsyncComponent, Card, Cropper, Grid, Icon, Image, Input, notification } from '@xcan-angus/vue-ui';
import { appContext } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { app } from '@/api';
import { AppInfo } from './types';

// Lazy load the ExpandHead component for better performance
const ExpandHead = defineAsyncComponent(() => import('./components/expandHead.vue'));

const { t } = useI18n();

// Reactive state variables
const editionType = ref<string>();
const loading = ref(false);
const firstLoad = ref(true);

// Initialize edition type from app context
const init = async () => {
  editionType.value = appContext.getEditionType();
};

// Application data management
const appList = ref<AppInfo[]>([]);
const oldAppList = ref<AppInfo[]>([]);

/**
 * Fetch and process authorized application list
 * Filters apps that should be displayed in navigation
 */
const getAuthAppList = async function () {
  loading.value = true;

  // Fix linter error: ensure user ID exists before making API call
  const userId = appContext.getUser()?.id;
  if (!userId) {
    loading.value = false;
    return;
  }

  const [error, { data = [] }] = await app.getUserAuthApp(userId);
  loading.value = false;

  if (error) {
    return;
  }

  // Filter apps with DISPLAY_ON_NAVIGATOR tag and add UI state properties
  appList.value = data
    .filter(item => item?.tags?.some(tag => tag.name === 'DISPLAY_ON_NAVIGATOR'))
    .map(item => ({
      ...item,
      showInfo: true,
      isEditName: false,
      isEditUrl: false,
      isUpload: false,
      loading: false
    }));

  // Create deep copy for rollback functionality
  oldAppList.value = JSON.parse(JSON.stringify(appList.value));
};

// Watch for user changes and fetch app list when user is available
watch(() => appContext.getUser(), (newValue) => {
  if (newValue) {
    getAuthAppList();
  }
}, {
  immediate: true
});

// Cropper configuration parameters
const params = { bizKey: 'applicationIcon' };

// Image cropper options for logo upload
const options = {
  autoCrop: true, // Enable default crop frame generation
  autoCropWidth: 240, // Default crop frame width
  autoCropHeight: 80, // Default crop frame height
  fixedBox: false, // Allow crop frame size changes
  info: true, // Show crop frame size information
  outputSize: 1, // Output image quality (1 to 0.1)
  outputType: 'png', // Output image format
  canScale: true, // Allow mouse wheel scaling
  fixed: false, // Enable fixed aspect ratio for crop frame
  fixedNumber: [7, 5], // Crop frame aspect ratio (width:height)
  full: true, // Output full-size cropped image
  canMoveBox: true, // Allow crop frame dragging
  original: false, // Render uploaded image at original scale
  centerBox: false, // Restrict crop frame within image bounds
  infoTrue: false // Show actual output dimensions vs visible crop frame dimensions
};

// Upload state management
const uploadAppIndex = ref(0);
const checkedApp = ref<AppInfo>();
const visible = ref<boolean>(false);
const upLoadLoading = ref(false);

/**
 * Handle upload button click
 * Sets up upload state and shows cropper modal
 */
const clickUpload = (item: AppInfo, index: number) => {
  uploadAppIndex.value = index;
  checkedApp.value = item;
  visible.value = !visible.value;
};

/**
 * Handle successful image upload
 * Updates app icon and triggers site update
 */
const handleUploadSuccess = (value: any) => {
  if (!checkedApp.value) {
    return;
  }

  updateAppSite({ id: checkedApp.value.id, icon: value.data[0].url }, uploadAppIndex.value);
};

// Name editing functionality
const showNameChange = (event: Event, index: number) => {
  const target = event.target as HTMLInputElement;
  appList.value[index].showName = target.value;
};

const cancelEditTitle = (index: number) => {
  const _app = appList.value[index];
  _app.showName = oldAppList.value[index].showName;
  _app.isEditName = false;
};

/**
 * Update application display name
 * Validates input and calls update API
 */
const updateAppName = async (index: number) => {
  const _app = appList.value[index];
  if (!_app.showName?.trim()) {
    // Rollback to original value if input is empty
    appList.value[index].showName = oldAppList.value[index].showName;
    _app.isEditName = false;
    return;
  }
  await updateAppSite({ id: _app.id, showName: _app.showName }, index);
  _app.isEditName = false;
};

// URL editing functionality
const appUrlChange = (event: Event, index: number) => {
  const target = event.target as HTMLInputElement;
  appList.value[index].url = target.value;
};

/**
 * Update application URL
 * Validates input and calls update API
 */
const updateAppUrl = async (index: number) => {
  const _app = appList.value[index];
  if (!_app.url?.trim()) {
    // Rollback to original value if input is empty
    appList.value[index].url = oldAppList.value[index].url;
    _app.isEditUrl = false;
    return;
  }
  await updateAppSite({ id: _app.id, url: _app.url }, index);
  _app.isEditUrl = false;
};

const cancelEditUrl = (index: number) => {
  const _app = appList.value[index];
  _app.url = oldAppList.value[index].url;
  _app.isEditUrl = false;
};

/**
 * Update application site information
 * Handles icon, name, and URL updates with error handling and rollback
 */
const updateAppSite = async (params: { id: number; icon?: string; showName?: string; url?: string }, index: number) => {
  const _app = appList.value[index];
  _app.loading = true;

  try {
    const [error] = await app.updateAppSite(params);

    if (error) {
      // Rollback changes on error
      if (params.icon) {
        _app.icon = oldAppList.value[index].icon;
      }
      if (params.showName) {
        _app.showName = oldAppList.value[index].showName;
      }
      if (params.url) {
        _app.url = oldAppList.value[index].url;
      }
      return;
    }

    // Show success notifications
    if (params.icon) {
      notification.success(t('appearance.messages.uploadLogoSuccess'));
    } else if (params.showName) {
      notification.success(t('appearance.messages.modifyNameSuccess'));
    } else if (params.url) {
      notification.success(t('appearance.messages.modifyDomainSuccess'));
    }
  } finally {
    _app.loading = false;
  }
};

// Initialize component on mount
onMounted(() => {
  init();
});

// Grid column configuration for the application list display
const gridColumns = [
  [
    {
      label: '',
      dataIndex: 'icon',
      offset: true
    }, {
      label: t('appearance.columns.name'),
      dataIndex: 'showName',
      offset: true
    }, {
      label: t('appearance.columns.domain'),
      dataIndex: 'url',
      offset: true
    }
  ]
];

</script>
<template>
  <Card
    :title="t('appearance.titles.siteInfo')"
    bodyClass="px-8 py-5"
    class="mb-2">
    <Skeleton
      active
      :loading="firstLoad"
      :title="false"
      :paragraph="{ rows: 8 }">
      <div
        v-for="(item, index) in appList"
        :key="item.id"
        class="mb-8 last:mb-0">
        <ExpandHead v-model:visible="item.showInfo" :title="item?.name" />
        <div
          :class="item.showInfo ? 'open-record' : 'stop-record'"
          class="transition-height duration-500 overflow-hidden mt-3.5 ml-4.5">
          <Grid
            :columns="gridColumns"
            :dataSource="item"
            :marginBottom="14"
            :colon="false"
            labelSpacing="40px">
            <!-- Icon display and upload section -->
            <template #icon="{ text }">
              <div class="flex space-x-10 justify-between w-100 items-center" style="min-height: 80px;">
                <div class="h-20 w-70">
                  <Image class="max-h-full max-w-full" :src="text" />
                </div>
                <template v-if="editionType !== 'CLOUD_SERVICE'">
                  <Button
                    size="small"
                    :loading="item.loading"
                    @click="clickUpload(item, index)">
                    <Icon icon="icon-shangchuan" class="mr-1 mb-0.5" />
                    <span class="whitespace-nowrap">{{
                      t('appearance.messages.upload')
                    }}</span>
                  </Button>
                </template>
              </div>
            </template>

            <!-- Application name editing section -->
            <template #showName="{ text }">
              <div class="relative w-100">
                <Input
                  :value="text"
                  :maxlength="50"
                  size="small"
                  class="absolute"
                  :disabled="!item.isEditName"
                  @change="showNameChange($event, index)">
                  <template v-if="item.isEditName" #suffix>
                    <a class="text-3 leading-3" @click="updateAppName(index)">{{ t('appearance.messages.sure') }}</a>
                    <Divider type="vertical" />
                    <a
                      class="text-3 leading-3"
                      @click="cancelEditTitle(index)">{{ t('appearance.messages.cancel') }}</a>
                  </template>
                </Input>
                <template v-if="!item.isEditName && editionType !== 'CLOUD_SERVICE'">
                  <Icon
                    icon="icon-shuxie"
                    class="mt-2 text-3 leading-3 text-theme-special text-theme-text-hover cursor-pointer absolute -right-4"
                    @click="item.isEditName = !item.isEditName" />
                </template>
              </div>
            </template>

            <!-- Application URL editing section -->
            <template #url="{ text }">
              <div class="relative w-100 h-8">
                <Input
                  :value="text"
                  :maxlength="200"
                  size="small"
                  class="absolute"
                  :disabled="!item.isEditUrl"
                  @change="appUrlChange($event, index)">
                  <template v-if="item.isEditUrl" #suffix>
                    <a class="text-3 leading-3" @click="updateAppUrl(index)">{{ t('appearance.messages.sure') }}</a>
                    <Divider type="vertical" />
                    <a
                      class="text-3 leading-3"
                      @click="cancelEditUrl(index)">{{ t('appearance.messages.cancel') }}</a>
                  </template>
                </Input>
                <template v-if="!item.isEditUrl && editionType !== 'CLOUD_SERVICE'">
                  <Icon
                    icon="icon-shuxie"
                    class="mt-2 text-3 leading-3 text-theme-special text-theme-text-hover cursor-pointer absolute -right-4"
                    @click="item.isEditUrl = !item.isEditUrl" />
                </template>
              </div>
            </template>
          </Grid>
        </div>
      </div>
    </Skeleton>
  </Card>

  <!-- Image cropper modal for logo upload -->
  <AsyncComponent :visible="visible">
    <Cropper
      v-model:visible="visible"
      v-model:loading="upLoadLoading"
      :params="params"
      :options="options"
      bizKey="applicationIcon"
      @success="handleUploadSuccess" />
  </AsyncComponent>
</template>

<style scoped>
/* Animation classes for expandable sections */
.open-record {
  max-height: 500px;
}

.stop-record {
  max-height: 0;
}
</style>
