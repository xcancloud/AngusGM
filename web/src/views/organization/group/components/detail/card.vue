<script setup lang='ts'>
import { computed, ref, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { PureCard, Grid, Icon, Input, notification, ButtonAuth } from '@xcan/design';
import { useRouter } from 'vue-router';
import { Badge, Spin, Tooltip, Tag } from 'ant-design-vue';
import { security } from '@xcan/security';

import { _gidColumns } from './PropsType';
import { Detail } from '../../PropsType';

import {group} from '@/api';

interface Props {
  dataSource: Detail
}

const props = withDefaults(defineProps<Props>(), {
  dataSource: undefined
});

const emit = defineEmits<{(e: 'update'): void, (e: 'success'): void }>();

const router = useRouter();
const { t } = useI18n();

const gidColumns = computed(() => {
  return _gidColumns.map(i => {
    return i.map(j => {
      return {
        ...j,
        label: t(j.label)
      };
    });
  });
});

const updateStatus = () => {
  emit('update');
};

const isEditName = ref(false);
const nameRule = ref(false);
const editNameLoading = ref(false);
const nameChange = (event: any) => {
  nameRule.value = !event.target.value;
};

const inputRef = ref();
const openEditName = () => {
  isEditName.value = true;
  nextTick(() => {
    inputRef.value?.focus();
  });
};

const editName = async (event) => {
  isEditName.value = false;
  if (event.target.value === props.dataSource?.name || !event.target.value) {
    return;
  }

  editNameLoading.value = true;
  const [error] = await group.updateGroup([{ id: props.dataSource.id, name: event.target.value }]);
  editNameLoading.value = false;
  if (error) {
    return;
  }
  notification.success('修改成功');
  emit('success');
};

const hanleEdit = () => {
  router.push(`/organization/group/edit/${props.dataSource.id}?source=detail`);
};
</script>
<template>
  <PureCard class="w-100 px-5 py-5">
    <div class="flex items-center justify-between">
      <template v-if="isEditName && security.has('GroupModify')">
        <Input
          ref="inputRef"
          :value="props.dataSource?.name "
          :maxlength="100"
          :error="nameRule"
          class="mr-3.5 w-50"
          size="small"
          :placeholder="t('pubPlaceholder',{name:t('name'),num:100})"
          @change="nameChange"
          @blur="editName" />
      </template>
      <template v-else>
        <div class="flex items-center">
          <Tooltip
            :title="props.dataSource?.name"
            placement="bottomLeft">
            <div
              style="max-width: 200px;"
              class="truncate text-theme-title text-3.5 leading-3.5 font-medium cursor-pointer">
              {{
                props.dataSource?.name }}
            </div>
          </Tooltip>
          <template v-if="editNameLoading">
            <Spin class="-mt-2 ml-2.5" />
          </template>
          <template v-else>
            <Icon
              icon="icon-shuxie"
              class="text-3 leading-3 text-theme-special text-theme-text-hover cursor-pointer ml-2"
              @click="openEditName" />
          </template>
        </div>
      </template>
      <div>
        <ButtonAuth
          code="GroupModify"
          type="primary"
          icon="icon-shuxie"
          class="mr-2"
          @click="hanleEdit" />
        <ButtonAuth
          code="GroupEnable"
          type="primary"
          :icon="props.dataSource?.enabled?'icon-jinyong1':'icon-qiyong'"
          :showTextIndex="props.dataSource?.enabled?1:0"
          @click="updateStatus" />
      </div>
    </div>
    <Grid
      :columns="gidColumns"
      :dataSource="props.dataSource"
      class="mt-3">
      <template #source="{ text }">
        {{ text?.message }}
      </template>
      <template #tags="{ text }">
        <template v-if="text?.length > 0">
          <Tag
            v-for="(tag, tagIndex) in text"
            :key="tagIndex"
            :title="tag.name"
            class="mb-2 -mt-1 truncate"
            style="max-width: 346px;">
            <template v-if="tag.name.length<=15">
              {{ tag.name }}
            </template>
            <template v-else>
              <Tooltip
                :title="tag.name"
                placement="bottomLeft">
                {{ tag.name.slice(0,15) }}...
              </Tooltip>
            </template>
          </Tag>
        </template>
        <span v-else>--</span>
      </template>
      <template #enabled="{ text }">
        <Badge
          v-if="text"
          status="success"
          :text="t('enable')" />
        <Badge
          v-else
          status="error"
          :text="t('disable')" />
      </template>
      <template #createdByName="{ text }">
        {{ text || '--' }}
      </template>
    </Grid>
  </PureCard>
</template>
