<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { cookie } from '@xcan/utils';
import { Upload } from 'ant-design-vue';
import { Hints, Icon, Modal } from '@xcan/design';

import { store } from '@/api';
import { utils } from '@xcan-angus/tools';

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

const token = cookie.get('access_token');
const { t } = useI18n();
const headers = {
  Authorization: `Bearer ${token}`
};

const fileList = ref<FileObj[]>([]);

const handleOk = () => {
  emit('update:visible', false);
};

const handleCancel = () => {
  emit('update:visible', false);
};

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
        const total = progressEvent.total;
        const percentCompleted = Math.round((progressEvent.loaded * 100) / total);
        fileList.value[fileList.value.length - 1].schedule = percentCompleted;
        fileList.value[fileList.value.length - 1].total = utils.formatBytes(total);
      }
    };

    const [error] = await store.offlineInstall(formData, config);
    if (error) {
      fileList.value[fileList.value.length - 1].state = true;
      return;
    }
    emit('refresh', true);
  }
};

const delUploadItem = (index: number) => {
  fileList.value.splice(index, 1);
};
</script>
<template>
  <Modal
    :title="t('stores.uploadPackage')"
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
          {{ t('stores.selectTheFile') }}
        </span>
      </Upload>
      <Hints :text="t('stores.supportedFormats')+': zip'" class="ml-4" />
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
              {{ item.state ? t('stores.uploadCompleted1') : t('stores.uploadCompleted') }}
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
