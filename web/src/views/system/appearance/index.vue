<script setup lang="ts">
import { defineAsyncComponent, inject, onMounted, ref, Ref, watch } from 'vue';
import { Button, Divider, Skeleton } from 'ant-design-vue';
import { AsyncComponent, Card, Grid, Icon, Image, Input, notification, Cropper } from '@xcan-angus/vue-ui';
import { appContext } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { app } from '@/api';
import { AppInfo } from './PropsType';

const ExpandHead = defineAsyncComponent(() => import('./components/expandHead.vue'));

// TODO 替换上下文
const tenantInfo: Ref = inject('tenantInfo', ref());

const { t } = useI18n();
const editionType = ref<string>();
const loading = ref(false);
const firstLoad = ref(true);

const init = async () => {
  editionType.value = appContext.getEditionType();
};

const appList = ref<AppInfo[]>([]);
const oldAppList = ref<AppInfo[]>([]);
const getAuthAppList = async function () {
  loading.value = true;
  const [error, { data = [] }] = await app.getUserAuthApp(tenantInfo.value.id);
  loading.value = false;
  if (error) {
    return;
  }
  appList.value = data.filter(item => item?.tags?.map(m => m.name).includes('DISPLAY_ON_NAVIGATOR'))?.map(item => ({
    ...item,
    showInfo: true,
    isEditName: false,
    isEditUrl: false,
    isUpload: false,
    loading: false
  }));
  oldAppList.value = JSON.parse(JSON.stringify(appList.value));
};

watch(() => tenantInfo.value, (newValue) => {
  if (newValue) {
    getAuthAppList();
  }
}, {
  immediate: true
});

const params = { bizKey: 'applicationIcon' };
const options = {
  autoCrop: true, // 是否默认生成截图框
  autoCropWidth: 240, // 默认生成截图框的宽度
  autoCropHeight: 80, // 默认生成截图框的长度
  fixedBox: false, // 是否固定截图框的大小 不允许改变
  info: true, // 裁剪框的大小信息
  outputSize: 1, // 裁剪生成图片的质量 [1至0.1]
  outputType: 'png', // 裁剪生成图片的格式
  canScale: true, // 图片是否允许滚轮缩放
  fixed: false, // 是否开启截图框宽高固定比例
  fixedNumber: [7, 5], // 截图框的宽高比例
  full: true, // 是否输出原图比例的截图
  canMoveBox: true, // 截图框能否拖动
  original: false, // 上传图片按照原始比例渲染
  centerBox: false, // 截图框是否被限制在图片里面
  infoTrue: false // true 为展示真实输出图片宽高 false 展示看到的截图框宽高
};

const uploadAppIndex = ref(0);
const checkedApp = ref<AppInfo>();
const visible = ref<boolean>(false);
const clickUpload = (item: AppInfo, index: number) => {
  uploadAppIndex.value = index;
  checkedApp.value = item;
  visible.value = !visible.value;
};
const upLoadLoading = ref(false);
const handleUploadSuccess = (value) => {
  if (!checkedApp.value) {
    return;
  }

  updateAppSite({ id: checkedApp.value.id, icon: value.data[0].url }, uploadAppIndex);
};

const showNameChange = (event: any, index) => {
  appList.value[index].showName = event.target.value;
};
const cancelEditTitle = (index) => {
  const _app = appList.value[index];
  _app.showName = oldAppList.value[index].showName;
  _app.isEditName = false;
};

const updateAppName = async (index: number) => {
  const _app = appList.value[index];
  if (!_app.showName) {
    appList.value[index].showName = oldAppList.value[index].showName;
    _app.isEditName = false;
    return;
  }
  updateAppSite({ id: _app.id, showName: _app.showName }, index);
  _app.isEditName = false;
};

const appUrlChange = (event: any, index) => {
  appList.value[index].url = event.target.value;
};

const updateAppUrl = async (index: number) => {
  const _app = appList.value[index];
  if (!_app.url) {
    appList.value[index].url = oldAppList.value[index].url;
    _app.isEditUrl = false;
    return;
  }
  updateAppSite({ id: _app.id, url: _app.url }, index);
  _app.isEditUrl = false;
};

const cancelEditUrl = (index) => {
  const _app = appList.value[index];
  _app.url = oldAppList.value[index].url;
  _app.isEditUrl = false;
};

const updateAppSite = async (params, index) => {
  const _app = appList.value[index];
  _app.loading = true;
  const [error] = await app.updateAppSite(params);
  _app.loading = false;
  if (error) {
    if (params.icon) {
      _app.icon = oldAppList.value[index].icon;
      return;
    }
    if (params.showName) {
      _app.showName = oldAppList.value[index].showName;
      return;
    }
    if (params.url) {
      _app.url = oldAppList.value[index].url;
      return;
    }
    return;
  }

  if (params.icon) {
    notification.success('上传Logo成功');
    return;
  }

  if (params.showName) {
    notification.success('修改名称成功');
    return;
  }

  if (params.url) {
    notification.success('修改域名成功');
  }
};

onMounted(() => {
  init();
});

const gridColumns = [
  [
    {
      label: '',
      dataIndex: 'icon',
      offset: true
    }, {
      label: t('名称'),
      dataIndex: 'showName',
      offset: true
    }, {
      label: t('域名'),
      dataIndex: 'url',
      offset: true
    }
  ]
];

</script>
<template>
  <Card
    title="站点信息"
    bodyClass="px-8 py-5"
    class="mb-2">
    <Skeleton
      active
      :loading="firstLoad"
      :title="false"
      :paragraph="{ rows: 8 }">
      <div
        v-for="item,index in appList"
        :key="item.id"
        class="mb-8 last:mb-0">
        <ExpandHead v-model:visible="item.showInfo" :title=" item?.name" />
        <div
          :class="item.showInfo ? 'open-record' : 'stop-record'"
          class="transition-height duration-500 overflow-hidden mt-3.5 ml-4.5">
          <Grid
            :columns="gridColumns"
            :dataSource="item"
            :marginBottom="14"
            :colon="false"
            labelSpacing="40px">
            <template #icon="{ text }">
              <div class="flex space-x-10 justify-between w-100 items-center" style="min-height: 80px;">
                <div class="h-20 w-70">
                  <Image class="max-h-full max-w-full" :src="text" />
                </div>
                <template v-if="editionType !== 'CLOUD_SERVICE'">
                  <Button
                    size="small"
                    :loading="item.loading"
                    @click="clickUpload(item,index)">
                    <Icon icon="icon-shangchuan" class="mr-1 mb-0.5" />
                    <span class="whitespace-nowrap">{{
                      t('上传')
                    }}</span>
                  </Button>
                </template>
              </div>
            </template>
            <template #showName="{ text }">
              <div class="relative w-100">
                <Input
                  :value="text"
                  :maxlength="50"
                  size="small"
                  class="absolute"
                  :disabled="!item.isEditName"
                  @change="showNameChange($event,index)">
                  <template v-if="item.isEditName" #suffix>
                    <a class="text-3 leading-3" @click="updateAppName(index)">{{ t('sure') }}</a>
                    <Divider type="vertical" />
                    <a
                      class="text-3 leading-3"
                      @click="cancelEditTitle(index)">{{ t('cancel') }}</a>
                  </template>
                </Input>
                <template v-if="!item.isEditName && editionType !== 'CLOUD_SERVICE'">
                  <Icon
                    icon="icon-shuxie"
                    class="mt-2 text-3 leading-3 text-theme-special text-theme-text-hover  cursor-pointer absolute -right-4"
                    @click="item.isEditName = !item.isEditName" />
                </template>
              </div>
            </template>
            <template #url="{ text }">
              <div class="relative w-100 h-8">
                <Input
                  :value="text"
                  :maxlength="200"
                  size="small"
                  class="absolute"
                  :disabled="!item.isEditUrl"
                  @change="appUrlChange($event,index)">
                  <template v-if="item.isEditUrl" #suffix>
                    <a class="text-3 leading-3" @click="updateAppUrl(index)">{{ t('sure') }}</a>
                    <Divider type="vertical" />
                    <a
                      class="text-3 leading-3"
                      @click="cancelEditUrl(index)">{{ t('cancel') }}</a>
                  </template>
                </Input>
                <template v-if="!item.isEditUrl && editionType !== 'CLOUD_SERVICE'">
                  <Icon
                    icon="icon-shuxie"
                    class="mt-2 text-3 leading-3 text-theme-special text-theme-text-hover  cursor-pointer absolute -right-4"
                    @click="item.isEditUrl = !item.isEditUrl" />
                </template>
              </div>
            </template>
          </Grid>
        </div>
      </div>
    </Skeleton>
  </Card>
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
.open-record {
  max-height: 500px;
}

.stop-record {
  max-height: 0;
}

</style>
