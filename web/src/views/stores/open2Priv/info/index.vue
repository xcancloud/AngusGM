<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { store } from '@/api';
import { cookie, http } from '@xcan/utils';
import { Carousel, StoreComment, Image, PureCard, Icon, Colon } from '@xcan/design';
import { Tag } from 'ant-design-vue';
import { API, VERSION } from '@xcan/sdk';

import VideoLink from '@/views/stores/open2Priv/info/videoLink/index.vue';
import ContentInfo from '@/views/stores/open2Priv/info/contentInfo/index.vue';
import ShowButton from '../components/showButton.vue';

import type { Goods } from '../PropsType';
import { goodsTypeColor, getEnumMessages } from '../PropsType';

const route = useRoute();

const id = route.params.id;
const userId = cookie.get('uid');

const loading = ref(true);
const goods = ref<Goods>({
  id: '',
  name: '',
  hot: false,
  introduction: '',
  tags: [],
  editionType: { value: '', message: '' },
  iconUrl: '',
  starNum: 0,
  goodsId: '',
  videos: [],
  features: [],
  bannerUrls: [],
  type: { value: '', message: '' },
  version: '',
  charge: true,
  createdByName: '',
  createdDate: '',
  price: {
    finalPrice: '0',
    totalSpecPrice: '0'
  },
  pricingUrl: '',
  onlineDate: ''
});

const loadDetail = async () => {
  loading.value = true;
  const [error, res] = await http.get(`${window.location.ancestorOrigins[0]}/${API}/${VERSION}/store/priv/goods/${id}`);
  loading.value = false;
  if (error) {
    return;
  }

  goods.value = res.data;
};

const starGoods = async (goods: Goods) => {
  const [error] = await store.starCloudGoodsInPriv({
    goodsId: goods.goodsId,
    star: !goods.star
  }, window.location.ancestorOrigins[0]);
  if (error) {
    return;
  }
  goods.star = !goods.star;
  if (goods.star) {
    goods.starNum = 1 + +goods.starNum;
  } else {
    goods.starNum = goods.starNum - 1;
  }
};

onMounted(() => {
  loadDetail();
});
</script>
<template>
  <div class="h-full pr-2 bg-theme-main overflow-auto">
    <PureCard
      :key="goods.id"
      class="px-8 py-7 border text-3 bg-white">
      <div class="flex">
        <Image
          type="image"
          :src="goods.iconUrl"
          class="w-28 h-28" />
        <div class="ml-6 flex-1 self-center">
          <div>
            <span class="font-medium text-4 mr-4 text-blue-1">{{ goods.name }}</span>
            {{ goods.version }}
            <span class="inline-flex items-center ml-4 text-3.5 cursor-pointer" @click="starGoods(goods)"><Icon
              icon="icon-a-tuijiandianzan"
              class="text-3 mr-2"
              :class="{'text-blue-text': goods.star}" />{{ goods.starNum }}</span>
          </div>
          <Tag
            v-for="tag in goods.tags"
            :key="tag"
            class="h-5 leading-5 px-1 mt-4">
            {{ tag }}
          </Tag>
          <div class="flex items-center mt-5 space-x-8">
            <div>
              <label class="mr-1 text-black-label">适用版本
                <Colon class="mr-1" />
              </label><span>{{ getEnumMessages(goods.applyEditionTypes) }}</span>
            </div>
            <div>
              <label class="mr-1 text-black-label">类型
                <Colon class="mr-1" />
              </label><span :class="goodsTypeColor[goods.type?.value]">{{ goods.type.message }}</span>
            </div>
            <div>
              <label class="mr-1 text-black-label">发布时间
                <Colon class="mr-1" />
              </label><span>{{ goods.onlineDate }}</span>
            </div>
            <div class="">
              <label class="mr-1 text-black-label">价格
                <Colon class="mr-1" />
              </label>
              <template v-if="goods.charge">
                <template v-if="goods.price">
                  <span class="mr-1">¥</span><span>{{ goods.price?.finalPrice }}</span><span
                    v-show="goods.price?.totalSpecPrice !== goods.price?.finalPrice"
                    class="line-through border rounded-full ml-2 px-2 border-orange-text  text-orange-text">{{ "¥" + goods.price?.totalSpecPrice }}</span>
                </template>
                <template v-else>--</template>
              </template>
              <template v-else>免费</template>
            </div>
          </div>
        </div>
        <div class="self-center">
          <ShowButton :goods="goods" />
        </div>
      </div>
    </PureCard>
    <PureCard v-if="goods?.bannerUrls?.length" class="mt-3">
      <Carousel :dataSource="goods?.bannerUrls" />
    </PureCard>
    <div class="flex mt-3">
      <div class="flex-1">
        <ContentInfo :goods="goods" :loading="loading" />
        <StoreComment
          v-if="goods.goodsId && goods.allowComment"
          :goodsId="goods.goodsId"
          :tenantId="userId"
          class="mt-4" />
      </div>
      <VideoLink
        class="ml-3"
        :goods="goods"
        :loading="loading" />
    </div>
  </div>
</template>
