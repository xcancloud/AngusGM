<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { cookieUtils, utils } from '@xcan-angus/infra';
import { Upload } from 'ant-design-vue';
import { Hints, Icon, Modal } from '@xcan-angus/vue-ui';

import { store } from '@/api';

interface FileObj {
  id: string,
  name: string,
  state: boolean,
  schedule: number,
  total: string
}

interface UploadConfig {
  onUploadProgress: (progressEvent: { loaded: number; total: number }) => void
}

interface Props {
  visible: boolean,
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
});

const emit = defineEmits<{(e: 'update:visible', value: boolean): void, (e: 'refresh', value: boolean): void }>();

const token = cookieUtils.get('access_token');
const { t } = useI18n();
const headers = {
  Authorization: `Bearer ${token}`
};

const fileList = ref<FileObj[]>([]);

/**
 * <p>Close modal with OK action.</p>
 */
const handleOk = () => {
  emit('update:visible', false);
};

/**
 * <p>Close modal with Cancel action.</p>
 */
const handleCancel = () => {
  emit('update:visible', false);
};

/**
 * <p>Intercept Upload change to perform manual upload via API.</p>
 * <p>Reason: We use custom backend and need fine-grained progress control.</p>
 */
const uploadChange = async ({ file }) => {
  if (file) {
    const fileObj = {
      id: '',
      name: file.name,
      state: false,
      schedule: 0,
      total: ''
    };

    fileList.value.push(fileObj);

    const formData = new FormData();
    formData.append('file', file.originFileObj);

    const config: UploadConfig = {
      onUploadProgress: (progressEvent: { loaded: number, total: number }) => {
        // Avoid division by zero when total is not available
        const total = progressEvent.total || 0;
        const loaded = progressEvent.loaded || 0;
        const percentCompleted = total > 0 ? Math.round((loaded * 100) / total) : 0;
        fileList.value[fileList.value.length - 1].schedule = percentCompleted;
        fileList.value[fileList.value.length - 1].total = utils.formatBytes(total);
      }
    };

    const [error] = await store.offlineInstall(formData, config);
    if (error) {
      fileList.value[fileList.value.length - 1].state = true;
      return;
    }
    // Notify parent to refresh the list after successful upload
    emit('refresh', true);
  }
};

const delUploadItem = (index: number) => {
  fileList.value.splice(index, 1);
};
</script>
<template>
  <Modal
    :title="t('cloud.messages.uploadPackage')"
    :visible="props.visible"
    :centered="true"
    :destroyOnClose="true"
    width="800px"
    @ok="handleOk"
    @cancel="handleCancel">
    <div class="flex">
      <Upload
        :showUploadList="false"
        :multiple="false"
        accept=".zip"
        :headers="headers"
        :customRequest="() =>{}"
        @change="uploadChange">
        <span class="text-theme-special text-theme-text-hover cursor-pointer">
          <Icon icon="icon-xuanze" class="mr-1 mb-0.5 text-3 leading-3" />
          {{ t('cloud.messages.selectTheFile') }}
        </span>
      </Upload>
      <Hints :text="t('cloud.messages.supportedFormats')+': zip'" class="ml-4" />
    </div>
    <div class="border border-theme-divider px-4 pt-4 mt-4 text-3 leading-3 text-theme-content customize">
      <div
        v-for="(item, index) in fileList"
        :key="index"
        class="flex justify-between mb-4 space-x-6">
        <div class="flex items-center flex-1">
          <Icon icon="icon-wenjianbao" />
          <span
            class="ml-2 whitespace-nowrap w-60 overflow-ellipsis overflow-hidden">{{ item.name }}</span>
          <span class="ml-4 whitespace-nowrap">{{ item.total }}</span>
          <div
            v-if="!item.state"
            class="h-2.5 ml-4 border border-theme-divider w-30 items-center flex-1">
            <div class="proging flex-1" :style="{ width: item.schedule + '%' }"></div>
          </div>
          <div v-if="!item.state">{{ item.schedule }}%</div>
          <div v-if="item.state" class="flex items-center ml-4">
            <Icon :icon="item.state?'icon-cuowu':'icon-right'" :class="item.state?'text-danger':'text-success'" />
            <div class="ml-1.5 whitespace-nowrap">
              {{ item.state ? t('cloud.messages.uploadCompleted1') : t('cloud.messages.uploadCompleted') }}
            </div>
          </div>
        </div>
        <Icon
          icon="icon-lajitong"
          class="text-theme-text-hover cursor-pointer"
          @click="delUploadItem(index)" />
      </div>
    </div>
  </Modal>
</template>
<style scoped>
.customize {
  min-height: 10rem;
}

.proging {
  width: 0%;
  height: 100%;
  background-color: #ff8100;
}
</style>
