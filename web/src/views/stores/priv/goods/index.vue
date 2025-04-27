<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { notification, PureCard, Scroll, Icon, SearchPanel, NoData, Colon, IconRefresh } from '@xcan/design';
import { Button, Dropdown, Menu, MenuItem } from 'ant-design-vue';
import { STORE } from '@xcan/sdk';

import { store } from '@/api';
import UploadPlugin from '@/views/stores/priv/goods/components/uploadPlugin/index.vue';
import { PrivateGoods } from './PropsType';

const { t } = useI18n();
const loading = ref<boolean>(false);
const visible = ref<boolean>(false);
const spinng = ref<boolean>(false);
const notify = ref(0);
const params: { filters: Record<string, string>[], orderBy: string, orderSort: 'DESC' | 'ASC' } = reactive({
  filters: [],
  orderBy: 'purchaseDate',
  orderSort: 'DESC',
  installStatus: 'MORMAL',
  installType: 'INSTALLED',
  uninstall: false
});

const handleOrder = (value: string) => {
  params.orderBy = value;
  params.orderSort = params.orderSort === 'DESC' ? 'ASC' : 'DESC';
};

const handleParamChange = (filters) => {
  params.filters = filters;
  notify.value++;
};

const uninstall = async (id) => {
  loading.value = true;
  const [error] = await store.uninstallPlugin({ goodsId: id });
  loading.value = false;
  if (error) {
    return;
  }
  notification.success(t('stores.uninstallSuccessfully'));
  notify.value++;
};

const openUpload = () => {
  visible.value = true;
};

const refresh = (isRefresh: boolean) => {
  if (isRefresh) {
    notify.value++;
  }
};

const goodsList = ref<PrivateGoods[]>([]);

const handleChange = (value) => {
  goodsList.value = value;
};

const getProductTypeColor = (value) => {
  if (value === 'APPLICATION') {
    return 'color: rgb(255, 163, 0);';
  } else {
    return 'color: rgb(108, 149, 0);';
  }
};
</script>
<template>
  <PureCard class="p-3.5 flex items-center mb-2">
    <div class="flex-1">
      <SearchPanel
        :options="[{
          type: 'input',
          valueKey: 'name',
          allowClear: true,
          placeholder: t('查询商品名称、描述')
        }]"
        @change="handleParamChange" />
    </div>
    <div class="flex items-center space-x-2.5">
      <Button
        size="small"
        type="primary"
        @click="openUpload">
        上传并安装
      </Button>
      <Dropdown overlayClassName="table-oper-menu ant-dropdown-sm" placement="bottom">
        <Icon icon="icon-shunxu" class="cursor-pointer outline-none" />
        <template #overlay>
          <Menu class="p-2 text-3 leading-3 font-normal bg-theme-container">
            <MenuItem @click="handleOrder('purchaseDate')">
              {{ t('按购买时间') }}
            </MenuItem>
          </Menu>
        </template>
      </Dropdown>
      <IconRefresh :loading="spinng" @click="refresh(true)" />
    </div>
  </PureCard>
  <Scroll
    v-model:spinning="spinng"
    style="height: calc(100% - 76px);"
    class="-mr-2"
    :action="`${STORE}/store/installation/search`"
    :params="params"
    :notify="notify"
    @change="handleChange">
    <template v-if="goodsList?.length">
      <PureCard
        v-for="item in goodsList"
        :key="item.id"
        class="p-3.5 mb-2 mr-2">
        <div class="flex justify-between items-center">
          <div class="flex items-center flex-1 mr-40">
            <div class="w-14 h-14 flex flex-col justify-center">
              <img class="max-h-full w-h-full rounded mx-auto" :src="item.iconText" />
            </div>
            <div class="flex flex-col justify-between flex-1 ml-5 h-12 text-theme-content">
              <div>
                <RouterLink
                  class="text-theme-special text-theme-text-hover font-medium text-3.5"
                  :to="`/storespriv/goods/${item.id}`">
                  {{ item.name }}
                </RouterLink>
                <span class="text-3 ml-2">V{{ item.version }}</span>
                <span
                  v-if="item.expired"
                  class="ml-2 inline-block px-2 rounded-full text-3 bg-danger-bg text-danger">已过期</span>
                <span
                  v-if="item.uninstallable === false"
                  class="ml-2 inline-block px-2 rounded-full text-3 bg-orange-bg1 text-orange-text"> 不可卸载</span>
              </div>
              <div class="flex text-3 flex-wrap text-theme-sub-content">
                <div>
                  <span>{{ t('版本') }}<Colon class="mr-1" /></span>
                  <span class="mr-8 font-medium text-theme-content">{{ item.editionType?.message || '--' }}</span>
                </div>
                <div>
                  <span>{{ t('类型') }}<Colon class="mr-1" /></span>
                  <span class="mr-8 font-medium text-theme-content" :style="getProductTypeColor(item.type?.value)">{{ item.type?.message || '--' }}</span>
                </div>
                <div>
                  <span>{{ t('安装时间') }}<Colon class="mr-1" /></span>
                  <span class="mr-8 font-medium text-theme-content">{{ item.createdDate || '--' }}</span>
                </div>
                <!-- <div>
                  <span>{{ t('是否过期') }}<Colon class="mr-1" /></span>
                  <span class="mr-8 font-medium text-theme-content">{{ '--' }}</span>
                </div> -->
                <div>
                  <span>{{ t('过期时间') }}<Colon class="mr-1" /></span>
                  <span class="mr-8 font-medium text-theme-content">{{ item.expiredDate || '--' }}</span>
                </div>
              </div>
            </div>
          </div>
          <Button
            v-if="item.uninstallable"
            size="small"
            @click="uninstall(item.goodsId)">
            {{ t('stores.uninstall') }}
          </Button>
        </div>
        <div class="border-t my-2 border-theme-divider"></div>
        <p class="text-3 bg-gray-text-bg-1 p-2 text-black-label mt-3">
          {{ item.introduction }}
        </p>
      </PureCard>
    </template>
    <NoData v-else />
  </Scroll>
  <UploadPlugin
    v-model:visible="visible"
    @refresh="refresh" />
</template>
