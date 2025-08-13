<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Grid, Icon, PureCard } from '@xcan-angus/vue-ui';
import { Image, Skeleton } from 'ant-design-vue';

import noPublic from './images/officialAccount.jpg';
import weChat from './images/commercialAffairs.jpg';
import Video from '../../components/vedio.vue';
import { Goods } from '../../PropsType';

const { t } = useI18n();

interface Props {
  loading: boolean;
  goods: Goods
}

const columns = [[
  {
    dataIndex: 'name',
    label: t('open2p.columns.name')
  },
  {
    dataIndex: 'editionType',
    label: t('open2p.columns.type')
  }
]];

const props = withDefaults(defineProps<Props>(), {
  loading: false
});

const top = ref(0);

const handlePull = () => {
  top.value -= 140;
};

const handlePush = () => {
  top.value += 140;
};

</script>
<template>
  <PureCard class="p-6 w-62.5">
    <Skeleton
      active
      :loading="loading"
      :title="false"
      :paragraph="{ rows: 2, width: ['100%', '100%'] }">
      <Grid
        :columns="columns"
        :dataSource="props.goods"
        fontSize="14px">
        <template #editionType="{text}">
          {{ text?.message }}
        </template>
      </Grid>
    </Skeleton>

    <div class="border-t border-b border-theme-divider py-10 mt-10 mx-auto w-42.5 video-aske text-center">
      <template v-if="props.goods.videos?.length">
        <div>{{ t('open2p.messages.videoTeaching') }}</div>
        <Icon
          class="text-4 cursor-pointer"
          :class="{'invisible': top >= 0}"
          icon="icon-shangla"
          @click="handlePush" />
        <div class="max-h-75  overflow-hidden">
          <div :style="{marginTop: top + 'px'}" class="transition-all">
            <div
              v-for="(item, index) in props.goods.videos"
              :key="item.url"
              class="w-42.5 mt-6 mx-auto"
              :class="{'mb-6': index + 1 === props.goods.videos?.length}">
              <Video
                :src="item.url"
                class="h-24"
                :name="item.name"
                :width="170" />
              {{ item.name }}
            </div>
          </div>
        </div>
        <Icon
          class="text-4 cursor-pointer"
          :class="{'invisible': (-top)/140 + 2 >= props.goods.videos?.length}"
          icon="icon-xiala"
          @click="handlePull" />
      </template>
      <Image :src="noPublic" class="w-full h-full" />
      {{ t('open2p.messages.noPublicTips') }}
      <Image :src="weChat" class="w-full h-full mt-5" />
      {{ t('open2p.messages.weChatTips') }}
    </div>
  </PureCard>
</template>
<style scoped>
:deep(.video-aske .ant-skeleton-avatar) {
  width: 170px;
  height: 90px;
}
</style>
<style>
.ant-image-preview-img {
  display: inline-block;
}
</style>
