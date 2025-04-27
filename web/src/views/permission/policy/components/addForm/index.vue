<script setup lang='ts'>
import { ref, reactive, onMounted, watch, inject } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button, Checkbox, Form, FormItem, Textarea, Tree } from 'ant-design-vue';
import { notification, Select, Input, Card, Icon, NoData, ButtonAuth } from '@xcan/design';
import { GM } from '@xcan/sdk';

import { auth } from '@/api';

interface FormType {
  id: string,
  appId: string,
  appName: string,
  name: string,
  description: string,
  funcIds: string[],
  code?: string,
  default0: boolean
}

interface ApisType {
  id: string,
  code: string,
  name: string,
  resourceName: string
}

interface DataRecordType {
  id: string,
  showName: string,
  pid: string,
  type: { value: 'BUTTON' | 'MENU' | 'PANEL', message: string },
  apis: ApisType[],
  children: DataRecordType[]
}

const { t } = useI18n();
const endReg = /.*(Door|pub)$/g;

const tenantInfo = inject('tenantInfo', ref());
const emit = defineEmits<{(e: 'reload'): void }>();

// 添加权限策略面板折叠状态
const isCollapse = ref(true);

// 改变权限策略面板折叠状态
const changeCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

// 添加权限策略表单数据
const state = reactive<{
  saving: boolean,
  globalAppId: string,
  globalAppName: string,
  form: FormType,
  checkedNodes: DataRecordType[],
  dataSource: DataRecordType[],
  showFuncsObj: { [key: string]: Array<ApisType> }
}>({
  saving: false, // 保存按钮状态
  globalAppId: '', // 全局管理应用的Id
  globalAppName: '', // 全局管理应用的Name
  form: { // 添加策略表单
    id: '',
    appId: undefined,
    appName: '',
    name: '',
    description: '',
    funcIds: [],
    code: undefined,
    default0: false
  },
  checkedNodes: [], // 当前复选框选中的节点
  dataSource: [], // 功能树数据
  showFuncsObj: {}
});

// 添加权限策略表单实例
const formRef = ref();

// 策略功能校验
const validFuncs = () => {
  if (state.form.funcIds.length === 0) {
    return Promise.reject(new Error(t('permissionsStrategy.add.funcsRule')));
  }
  return Promise.resolve();
};

// 表单校验规则
const rules = {
  name: [
    { required: true, message: t('permissionsStrategy.add.nameRule'), trigger: 'blur' }
  ],
  appId: [
    { required: true, message: t('permissionsStrategy.add.appRule'), trigger: 'change' }
  ],
  code: [
    { required: true, message: t('请输入编码'), trigger: 'blur' }
  ],
  funcIds: [
    { required: true, validator: validFuncs, trigger: 'change' }
  ]
};

// 查询父级
const findParentPath = (oldList: DataRecordType[], pid: string) => {
  const parentPaths: DataRecordType[] = [];
  // 转为扁平数组
  const fn = (list: DataRecordType[]) => {
    const res: DataRecordType[] = [];
    list.forEach(item => {
      res.push(item);
      item.children && res.push(...fn(item.children));
    });
    return res;
  };
  const tempList = fn(state.dataSource);
  // 查询父级id
  const pFn = (parentId: string) => {
    const item = tempList.find(v => v.id === parentId);
    if (item) {
      // 已存在或者是自己时, 不添加
      if (!oldList.find(v => v.id === item.id) && item.pid !== pid) {
        parentPaths.unshift(item);
      }
      if (`${item.pid}` !== '-1') {
        pFn(item.pid);
      }
    }
  };
  pFn(pid);
  return parentPaths;
};

// 复选框勾选变化
const onCheck = (_keys, info: { checkedNodes: DataRecordType[] }) => {
  state.checkedNodes = info.checkedNodes;
  // 处理父级
  if (info.checkedNodes.length > 0) {
    const lastCheckedItem = info.checkedNodes[info.checkedNodes.length - 1];
    if (`${lastCheckedItem.pid}` !== '-1') {
      const list: DataRecordType[] = findParentPath(info.checkedNodes, lastCheckedItem.pid);
      state.checkedNodes = [...list, ...info.checkedNodes];
    }
  }

  // 把勾选的值同步到表单中
  state.form.funcIds = state.checkedNodes.map(item => item.id);
  formRef.value.validateFields(['funcIds']);
};

// 根据应用Id查询应用下的功能资源
const loadResourceByAppId = async () => {
  if (!state.form.appId) {
    return;
  }

  const [error, res] = await auth.getUserAppFunctionTree(tenantInfo.value.id, state.form.appId);
  if (error) {
    return;
  }
  state.dataSource = handleAppFuncs(res.data.appFuncs || []);
};

const handleAppFuncs = (funcs = []) => {
  if (!funcs.length) {
    return [];
  }

  function travelTree (data) {
    return data.map(item => {
      return {
        ...item,
        disabled: !item.authCtrl,
        children: travelTree(item.children || [])
      };
    });
  }

  return travelTree(funcs);
};

// 所选择的应用发生变化
const selectedAppChange = (_value: string, option: { appName: string, appId: string }) => {
  state.form.appId = option.appId;
  state.form.appName = option.appName;
  state.checkedNodes = [];
  state.form.funcIds = [];
  loadResourceByAppId();
};

// 编辑(查询策略详情、功能树进行回显)
const editPolicy = (item: { appId: string, policyId: string }) => {
  const reqList = [
    auth.getPolicyDetail(item.policyId),
    auth.getPolicyFunctionsById(item.policyId)
  ];
  Promise.all(reqList).then(([[, detailRes], [, treeRes]]) => {
    const detailData = detailRes.data;
    const treeData = treeRes.data || [];
    state.form = {
      id: detailData?.id,
      appId: detailData?.appId,
      appName: detailData?.appName,
      name: detailData?.name,
      description: detailData?.description,
      code: detailData?.code,
      default0: detailData?.default0,
      funcIds: []
    };
    loadResourceByAppId();
    state.checkedNodes = [];
    const findFunctions = (list: DataRecordType[]) => {
      list.forEach(v => {
        state.checkedNodes.push(v);
        if (v.children && Array.isArray(v.children)) {
          findFunctions(v.children);
        }
      });
    };
    findFunctions(treeData);
    state.form.funcIds = state.checkedNodes.map(i => i.id);

    // 把滚动条滚动到顶部
    const dom = document.querySelector('#scrollMain');
    dom?.firstElementChild?.scrollTo(0, 0);
  }, (err: { message: string }) => {
    notification.error(err.message);
  });
};

// 保存
const save = () => {
  formRef.value.validate().then(async () => {
    const params = {
      name: state.form.name,
      appId: state.form.appId,
      description: state.form.description,
      funcIds: state.form.funcIds,
      code: state.form.code,
      default: state.form.default0,
      grantStage: 'MANUAL',
      type: 'USER_DEFINED',
      id: state.form.id || undefined
    };
    state.saving = true;
    let res: [Error | null, any];
    let successTip = '';
    if (state.form.id) {
      successTip = t('permissionsStrategy.add.editSuccess');
      res = await auth.updatePolicy([params]);
    } else {
      successTip = '添加策略成功';
      res = await auth.addPolicy([params]);
    }
    const [error] = res;
    state.saving = false;
    if (error) {
      return;
    }
    notification.success(successTip);
    emit('reload');
    // 成功后初始化表单、功能树等参数
    resetForm();
  });
};

const resetForm = () => {
  state.form = {
    id: '',
    appId: '',
    appName: '',
    name: '',
    description: '',
    funcIds: [],
    code: undefined,
    default0: false
  };
  state.checkedNodes = [];
  state.dataSource = [];
};

// 这里用于处理功能的展示的数据格式
watch(() => state.checkedNodes, (val) => {
  const resultMap: { [key: string]: Array<ApisType> } = {};
  val.forEach((item: DataRecordType) => {
    (item.apis || []).forEach(childItem => {
      // 按照api的id去重
      if (resultMap[childItem.resourceName]) {
        if (!resultMap[childItem.resourceName].find(v => v.id === childItem.id)) {
          resultMap[childItem.resourceName].push(childItem);
        }
      } else if (!childItem.resourceName.match(endReg)) {
        resultMap[childItem.resourceName] = [childItem];
      }
    });
  });
  state.showFuncsObj = resultMap;
}, { deep: true });

onMounted(() => {
  // loadGlobalApp();
});

defineExpose({
  editPolicy
});
</script>

<template>
  <Card :title="t('permissionsStrategy.add.title')" :bodyClass="isCollapse ? 'px-8 py-5' : 'p-0'">
    <template #rightExtra>
      <a
        href="javascript:;"
        class="text-3 leading-3 text-theme-special text-theme-text-hover"
        @click="changeCollapse">
        <span class="inline-block text-3 leading-3 transition-all">{{ isCollapse ? t('permissionsStrategy.add.collapse') : t('permissionsStrategy.add.expand') }}</span>
        <Icon
          icon="icon-xiala"
          :class="!isCollapse ? '' : 'collapse-rotate'"
          class="inline-block ml-1.5 text-3 leading-3 transition-all transform" />
      </a>
    </template>
    <template v-if="isCollapse">
      <div class="transition-all transform duration-1000">
        <Form
          ref="formRef"
          size="small"
          :colon="false"
          :model="state.form"
          :rules="rules"
          v-bind="{ labelCol: { style: { width: '120px' } } }">
          <FormItem
            :label="t('permissionsStrategy.add.nameLabel')"
            name="name"
            class="w-1/2">
            <Input
              v-model:value="state.form.name"
              :maxlength="100"
              :placeholder="t('permissionsStrategy.add.namePlaceholder')"
              size="small" />
          </FormItem>
          <FormItem
            :label="t('code')"
            name="code"
            class="w-1/2">
            <Input
              v-model:value="state.form.code"
              :disabled="!!state.form.id"
              :maxlength="80"
              :placeholder="t('支持输入数字字母:_-.,最大80个字符')"
              size="small"
              dataType="mixin-en"
              includes=":_-." />
          </FormItem>
          <FormItem
            :label="t('permissionsStrategy.add.appLabel')"
            name="appId"
            class="w-1/2">
            <Select
              :value="state.form.appId"
              showSearch
              :action="`${GM}/appopen/list?tenantId=${tenantInfo.tenantId}&clientId=xcan_tp`"
              :disabled="!!state.form.id"
              :placeholder="t('permissionsStrategy.add.appPlaceholder')"
              :lazy="false"
              :fieldNames="{label: 'appName', value: 'appId'}"
              internal
              size="small"
              @change="selectedAppChange" />
          </FormItem>
          <FormItem
            v-if="state.form.appId"
            :label="t('permissionsStrategy.add.funcsLabel')"
            name="funcIds">
            <div class="w-full h-82.5 border border-solid border-theme-divider rounded">
              <div class="flex w-full h-8 bg-theme-container border-b border-solid border-theme-divider">
                <div class="flex items-center justify-center w-75 h-full text-3 leading-3 text-theme-title">
                  {{
                    t('permissionsStrategy.add.funcsLeft') }}
                </div>
                <div
                  class="flex items-center justify-center funcs-right-width h-full text-3 leading-3 text-theme-title">
                  {{
                    t('permissionsStrategy.add.funcsRight') }}
                </div>
              </div>
              <div class="flex w-full h-72.5 py-6">
                <!-- 菜单树 -->
                <div class="flex w-100 h-full px-3 overflow-auto border-r border-solid border-theme-divider">
                  <Tree
                    v-if="state.dataSource.length"
                    class="w-full text-3 leading-3"
                    :treeData="state.dataSource"
                    :selectable="false"
                    :checkable="true"
                    :checkStrictly="true"
                    :defaultExpandAll="true"
                    :checkedKeys="(state.checkedNodes.map((v) => v.id))"
                    :fieldNames="{key: 'id', title: 'name', children: 'children'}"
                    size="small"
                    @check="onCheck">
                    <template #title="item">
                      <div class="whitespace-nowrap" :class="{'text-gray-text': item.disabled}">
                        {{ item.name }} <span
                          class="text-gray-text">（{{ `${item.type?.message}${item.disabled ? '、不受权限控制' : '' }` }}）</span>
                      </div>
                    </template>
                  </Tree>
                  <NoData
                    v-else
                    class="h-full w-full" />
                </div>
                <!-- 功能列表 -->
                <div class="funcs-right-width h-full px-8 overflow-x-hidden overflow-y-auto break-all">
                  <div
                    v-for="(api, resourceName ) in state.showFuncsObj"
                    :key="resourceName"
                    class="w-full pb-2.5 flex">
                    <div
                      class="w-25 pr-1 pt-1.25 align-top text-right text-3 leading-3 text-theme-title truncate"
                      :title="resourceName">
                      {{ resourceName }}
                    </div>
                    <div class="flex-1 tags-wrap-width">
                      <span
                        v-for="(apiItem, apiIndex) in api"
                        :key="apiIndex"
                        class="inline-block px-2 py-1.5 mr-1 mb-1 border border-solid border-theme-divider text-3 leading-3 text-theme-content">
                        {{ apiItem?.name }}({{ apiItem.code }})
                      </span>
                      <span v-if="api.length === 0">--</span>
                    </div>
                  </div>
                  <NoData v-if="Object.keys(state.showFuncsObj).length === 0" class="h-full w-full" />
                </div>
              </div>
            </div>
          </FormItem>
          <FormItem
            :label="t('默认策略')"
            name="default0"
            class="w-1/2">
            <Checkbox v-model:checked="state.form.default0">
              设置后该策略可用于“默认权限策略”选项。
            </Checkbox>
          </FormItem>
          <FormItem
            :label="t('permissionsStrategy.add.descLabel')"
            name="description"
            class="w-1/2">
            <Textarea
              v-model:value="state.form.description"
              :maxlength="200"
              :placeholder="t('permissionsStrategy.add.descPlaceholder')"
              showCount
              size="small"
              class="h-15" />
          </FormItem>
          <FormItem label=" " :colon="false">
            <ButtonAuth
              v-if="!state.form.id"
              code="PolicyAdd"
              type="primary"
              icon="icon-tianjia"
              @click="save" />
            <template v-else>
              <ButtonAuth
                code="PolicyModify"
                type="primary"
                icon="icon-shuxie"
                :loading="state.saving"
                @click="save" />
              <Button
                size="small"
                class="ml-2"
                @click="resetForm">
                <Icon icon="icon-quxiao" class="mr-1 text-3" />
                {{ t('cancel') }}
              </Button>
            </template>
          </FormItem>
        </Form>
      </div>
    </template>
  </Card>
</template>

<style scoped>
.collapse-rotate {
  @apply -rotate-180;
}

.funcs-right-width {
  width: calc(100% - 300px);
}

.tags-wrap-width {
  width: calc(100% - 92px);
}

:deep(.ant-tree li .ant-tree-node-content-wrapper) {
  @apply relative pr-6 overflow-ellipsis overflow-hidden;
}
</style>
