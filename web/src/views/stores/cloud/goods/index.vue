<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  ButtonAuth,
  Colon,
  DropdownSort,
  Icon,
  IconRefresh,
  PureCard,
  Scroll,
  SearchPanel,
  Spin
} from '@xcan-angus/vue-ui';
import { app, ESS, GoodsType } from '@xcan-angus/infra';

import UploadPlugin from './components/UploadPlugin/index.vue';
import ShowButton from './components/ShowButton.vue';
import { Goods } from './PropsType';
import { goodsTypeColor } from '@/views/stores/cloud/stores/PropsType';

const { t } = useI18n();
const visible = ref<boolean>(false);
const notify = ref(0);
const params: {
  filters: Record<string, string>[],
  expired: boolean,
  orderBy: string,
  orderSort: 'DESC' | 'ASC'
} = reactive({
  filters: [],
  orderBy: 'purchaseDate',
  orderSort: 'DESC',
  expired: false
});
const loading = ref(false);
const downLoading = ref(false);

watch(() => downLoading.value, newValue => {
  loading.value = newValue;
});

const handleOrder = ({ orderBy, orderSort }) => {
  params.orderBy = orderBy;
  params.orderSort = orderSort;
};

const handleParamChange = (filters) => {
  params.filters = filters;
  notify.value++;
};

const refresh = (isRefresh: boolean) => {
  if (isRefresh) {
    notify.value++;
  }
};

const goodsList = ref<Goods[]>([]);

const handleChange = (value) => {
  goodsList.value = value;
};

const sortMenus = [
  {
    name: '按购买时间',
    key: 'purchaseDate',
    orderSort: 'DESC'
  }
];

const options = [
  {
    type: 'input',
    valueKey: 'goodsName',
    allowClear: true,
    placeholder: t('查询商品名称、描述')
  },
  {
    type: 'select-enum',
    valueKey: 'goodsType',
    enumKey: GoodsType,
    placeholder: t('选择商品类型'),
    allowClear: true,
    excludes: ['RESOURCE_QUOTA']
  }
];
</script>
<template>
  <Spin
    :spinning="downLoading"
    class="h-full">
    <PureCard class="px-3.5 py-4 flex items-center justify-between mb-2">
      <SearchPanel :options="options" @change="handleParamChange" />
      <div class="flex items-center space-x-2.5">
        <DropdownSort :menuItems="sortMenus" @click="handleOrder">
          <Icon icon="icon-shunxu" class="cursor-pointer outline-none" />
        </DropdownSort>
        <IconRefresh
          :loading="loading"
          class="ml-2"
          @click="refresh(true)" />
      </div>
    </PureCard>
    <Scroll
      v-model:spinning="loading"
      style="height: calc(100% - 76px);"
      class="-mr-2"
      :action="`${ESS}/store/purchase`"
      :params="params"
      :notify="notify"
      :lineHeight="162"
      @change="handleChange">
      <PureCard
        v-for="item in goodsList"
        :key="item.id"
        class="p-3.5 mb-2 mr-2">
        <div class="flex items-center">
          <div class="w-14 h-14 flex flex-col justify-center">
            <img class="max-h-full w-h-full rounded mx-auto" :src="item.goodsIconUrl" />
          </div>
          <div class="flex flex-col justify-between flex-1 ml-5 text-theme-content">
            <div>
              <ButtonAuth
                code="MyGoodsDetail"
                type="link"
                class="text-theme-special text-theme-text-hover font-medium text-3.5"
                :href="`/stores/cloud/${item.goodsId}`"
                :text="item.goodsName">
              </ButtonAuth>
              <span
                v-if="!app.show('MyGoodsDetail')"
                class="text-theme-special text-theme-text-hover font-medium text-3.5">{{ item.goodsName }}</span>
              <!-- <RouterLink
                class="text-theme-special text-theme-text-hover font-medium text-3.5"
                :to="`/stores/cloud/${item.goodsId}`">
                {{ item.goodsName }}
              </RouterLink> -->
              <span class="text-3 ml-2">V{{ item.goodsVersion }}</span>
            </div>
            <div class="flex text-3 flex-wrap text-theme-sub-content mt-2 leading-5">
              <div>
                <span>{{ t('版本') }}<Colon class="mr-2" /></span>
                <span class="mr-8 font-medium text-theme-content">{{ item.goodsEditionType.message }}</span>
              </div>
              <div v-if="item.applyEditionTypes">
                <span>{{ t('适用版本') }}<Colon class="mr-2" /></span>
                <span class="mr-8 font-medium text-theme-content">{{
                  (item.applyEditionTypes || []).map(i => i.message).join('、')
                }}</span>
              </div>
              <div>
                <span>{{ t('类型') }}<Colon class="mr-2" /></span>
                <span class="mr-8 font-medium" :class="goodsTypeColor[item.goodsType.value]">{{
                  item.goodsType.message
                }}</span>
              </div>
              <div>
                <span>{{ t('获取时间') }}<Colon class="mr-2" /></span>
                <span class="mr-8 font-medium text-theme-content">{{ item?.purchaseDate || '--' }}</span>
              </div>
              <div>
                <span>{{ t('获取人') }}<Colon class="mr-2" /></span>
                <span class="mr-8 font-medium text-theme-content">{{ item?.purchaseByName || '--' }}</span>
              </div>
              <div>
                <span>{{ t('订单号') }}<Colon class="mr-2" /></span>
                <a class="mr-8 font-medium text-theme-content">{{ item.orderNo || '--' }}</a>
              </div>
              <div>
                <span>{{ t('过期时间') }}<Colon class="mr-2" /></span>
                <span class="mr-8 font-medium text-theme-content">{{ item.expiredDate || '--' }}</span>
              </div>
            </div>
          </div>
          <ShowButton v-model:downLoading="downLoading" :goods="item" />
        </div>
        <div class="border-t my-2 border-theme-divider"></div>
        <p class="text-3 bg-gray-text-bg-1 p-2 text-black-label mt-3">
          {{ item.goodsIntroduction }}
        </p>
      </PureCard>
    </Scroll>
    <UploadPlugin v-model:visible="visible" @refresh="refresh" />
  </Spin>
</template>
