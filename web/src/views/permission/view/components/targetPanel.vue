<script setup lang='ts'>
import { reactive, watch, computed, ref, defineAsyncComponent } from 'vue';
import { useI18n } from 'vue-i18n';
import { Image, Input, Icon, Scroll, AsyncComponent, IconRefresh, ButtonAuth } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration, GM } from '@xcan-angus/infra';

const AddMembers = defineAsyncComponent(() => import('./addMembers.vue'));

interface Props {
  type: 'USER' | 'DEPT' | 'GROUP',
  appId: string,
  selectedTargetId?: string
}

interface DataType {
  id: string;
  userId?: string;
  avatar?: string;
  createdByName?: string;
  userName?: string;
  deptName?: string;
  groupName?: string;
  deptId?: string;
  groupId?: string;
  fullName?: string;
  name?: string;
}

const addAuthConfig = {
  USER: 'AuthorizeUserAdd',
  DEPT: 'AuthorizeDeptAdd',
  GROUP: 'AuthorizeGroupAdd'
};

const addVisible = ref(false);

const add = () => {
  addVisible.value = true;
};

const { t } = useI18n();
const props = withDefaults(defineProps<Props>(), {});
const typeName = props.type === 'DEPT' ? t('permissionsCheck.dept') : props.type === 'GROUP' ? t('permissionsCheck.group') : t('permissionsCheck.user');

const emit = defineEmits<{(e: 'change', targetId: string): void }>();
const refresh = ref(0);
const state = reactive<{
  loading: boolean,
  dataSource: DataType[],
  selectedTargetId: string | undefined,
  searchValue: string | undefined
}>({
  loading: false, // 查询状态
  dataSource: [], // 列表数据
  selectedTargetId: undefined, // 当前选中的对象ID
  searchValue: undefined // 检索内容
});

// 切换当前选中的对象
const targetChange = (item: DataType) => {
  const id = props.type === 'USER' ? item.id : props.type === 'GROUP' ? item.id : item.id;
  state.selectedTargetId = id;
  emit('change', id);
};

const getAction = computed(() => {
  if (!props.appId) {
    return undefined;
  }
  return `${GM}/app/${props.appId}/auth/${props.type.toLocaleLowerCase()}`;
});

const loadData = (data) => {
  state.dataSource = data;
  activeFirstItem();
};

// 激活首项
const activeFirstItem = () => {
  if (state.dataSource.length) {
    targetChange(state.dataSource[0]);
  }
};

watch(() => props.appId, newValue => {
  if (newValue) {
    refresh.value += 1;
  }
});

watch(() => props.selectedTargetId, newValue => {
  state.selectedTargetId = newValue;
});

const params = computed(() => {
  return {
    filters: [state.searchValue && {
      key: props.type === 'USER' ? 'fullName' : 'name',
      value: state.searchValue,
      op: 'MATCH_END'
    }].filter(Boolean)
  };
});

const changeSearchValue = debounce(duration.search, (e) => {
  state.searchValue = e.target.value;
});

const updateList = (value: boolean) => {
  if (value) {
    refresh.value++;
  }
  addVisible.value = false;
};

defineExpose({ activeFirstItem });
</script>

<template>
  <!-- 查询条件 -->
  <div class="w-full p-2 flex space-x-2 items-center">
    <Input
      :value="state.searchValue"
      :allowClear="true"
      :placeholder="t('permissionsCheck.searchPlaceholder', { name: typeName })"
      class="flex-1"
      size="small"
      @change="changeSearchValue">
      <template #suffix>
        <Icon
          icon="icon-sousuo"
          class="text-3 leading-3 text-theme-content cursor-pointer" />
      </template>
    </Input>
    <ButtonAuth
      :code="addAuthConfig[props.type]"
      type="primary"
      icon="icon-tianjia"
      :disabled="!props.appId"
      @click="add" />
    <IconRefresh
      class="text-3.5"
      @click="updateList(true)" />
  </div>
  <Scroll
    :action="getAction"
    :params="params"
    :notify="refresh"
    :lineHeight="32"
    class="flex-1"
    @change="loadData">
    <template #default>
      <div
        v-for="item in state.dataSource"
        :key="item.id"
        class="w-full px-3 flex items-center h-8 cursor-pointer"
        :class="{
          'bg-theme-tabs-selected': state.selectedTargetId === item.id
        }"
        @click="targetChange(item)">
        <div v-if="type === 'USER'" class="w-6 h-6 rounded-full">
          <Image
            class="w-full h-full rounded-full"
            type="avatar"
            :src="item.avatar" />
        </div>
        <div
          v-if="type === 'DEPT'"
          class="w-6 h-6 rounded-full bg-blue-tips flex items-center justify-center">
          <Icon class="inline w-full h-full text-theme-content text-4" icon="icon-bumen" />
        </div>
        <div
          v-if="type === 'GROUP'"
          class="w-6 h-6 rounded-full bg-blue-tips flex items-center justify-center">
          <Icon class="inline w-full h-full text-theme-content text-4" icon="icon-zu" />
        </div>
        <div
          class="name-width text-3 leading-3 text-theme-content pl-5 overflow-hidden overflow-ellipsis whitespace-nowrap"
          :title="type === 'USER' ? item.fullName : item.name">
          {{ type === 'USER' ? item.fullName : item.name }}
        </div>
      </div>
    </template>
  </Scroll>
  <AsyncComponent :visible="addVisible">
    <AddMembers
      v-if="addVisible"
      :visible="addVisible"
      :appId="props.appId"
      :type="props.type"
      @update="updateList" />
  </AsyncComponent>
</template>

<style scoped>
.name-width {
  width: calc(100% - 40px);
}

</style>
