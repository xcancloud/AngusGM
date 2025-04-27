<script setup lang='ts'>
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { Button, Form, FormItem, RadioGroup, Radio } from 'ant-design-vue';
import { notification, SelectUser, SelectDept, SelectGroup, PureCard, Input } from '@xcan-angus/vue-ui';

import { auth } from '@/api';

interface FormType {
  id: string,
  name: string,
  appId: string,
  targetType: 'USER' | 'DEPT' | 'GROUP',
  targetId: string[]
}

const route = useRoute();
const router = useRouter();
const { t } = useI18n();

const formRef = ref();

const state = reactive<{
  loading: boolean,
  saving: boolean,
  form: FormType,
  rules: Record<string, Array<any>>
}>({
  loading: false,
  saving: false,
  form: {
    id: '',
    name: '',
    appId: '',
    targetType: 'USER',
    targetId: []
  },
  rules: {
    targetId: [
      { required: true, message: t('permissionsStrategy.auth.rule'), trigger: 'change' }
    ]
  }
});

// 查询策略详情
const load = async () => {
  state.loading = true;
  const [error, res] = await auth.getPolicyDetail(route.params.id as string);
  state.loading = false;
  if (error) {
    return;
  }

  state.form.id = res.data.id;
  state.form.name = res.data.name;
  state.form.appId = res.data.appId;
};

// 授权对象发生变化
const targetChange = (value: string[]) => {
  state.form.targetId = value;
};

// 取消
const linkToList = () => {
  router.push('/permissions/policy');
};

// 保存
const save = () => {
  formRef.value.validate().then(async () => {
    let res: [Error | null, any];
    const ids = state.form.targetId;
    state.saving = true;
    if (state.form.targetType === 'USER') {
      res = await auth.addPolicyUser(state.form.id, ids);
    } else if (state.form.targetType === 'DEPT') {
      res = await auth.addPolicyDept(state.form.id, ids);
    } else {
      res = await auth.addPolicyGroup(state.form.id, ids);
    }

    const [error] = res;
    state.saving = false;
    if (error) {
      return;
    }

    notification.success(t('permissionsStrategy.auth.success'));
    linkToList();
  });
};

onMounted(() => {
  load();
});
</script>

<template>
  <PureCard class="flex-1 py-10">
    <Form
      ref="formRef"
      class="w-1/2 my-0 mx-auto"
      :model="state.form"
      :rules="state.rules"
      v-bind="{labelCol: {span: 5}, wrapperCol: {span: 18}}">
      <FormItem :label="t('permissionsStrategy.auth.name')" name="name">
        <Input
          disabled
          :value="state.form.name"
          size="small" />
      </FormItem>
      <FormItem :label="t('permissionsStrategy.auth.targetType')" name="targetType">
        <RadioGroup v-model:value="state.form.targetType">
          <Radio value="USER">{{ t('permissionsStrategy.auth.user') }}</Radio>
          <Radio value="DEPT">{{ t('permissionsStrategy.auth.dept') }}</Radio>
          <Radio value="GROUP">{{ t('permissionsStrategy.auth.group') }}</Radio>
        </RadioGroup>
      </FormItem>
      <FormItem
        v-if="state.form.targetType === 'USER'"
        :label="t('permissionsStrategy.auth.userLabel')"
        name="targetId">
        <SelectUser
          :placeholder="t('permissionsStrategy.auth.userPlaceholder')"
          :allowClear="false"
          :internal="true"
          mode="multiple"
          size="small"
          @change="targetChange" />
      </FormItem>
      <FormItem
        v-if="state.form.targetType === 'DEPT'"
        :label="t('permissionsStrategy.auth.deptLabel')"
        name="targetId">
        <SelectDept
          :placeholder="t('permissionsStrategy.auth.deptPlaceholder')"
          :allowClear="false"
          :internal="true"
          mode="multiple"
          size="small"
          @change="targetChange" />
      </FormItem>
      <FormItem
        v-if="state.form.targetType === 'GROUP'"
        :label="t('permissionsStrategy.auth.groupLabel')"
        name="targetId">
        <SelectGroup
          :placeholder="t('permissionsStrategy.auth.groupPlaceholder')"
          :allowClear="false"
          :internal="true"
          mode="multiple"
          size="small"
          @change="targetChange" />
      </FormItem>
      <FormItem label=" " :colon="false">
        <Button
          size="small"
          type="primary"
          :loading="state.saving"
          @click="save">
          {{ t('permissionsStrategy.auth.auth') }}
        </Button>
        <Button
          size="small"
          class="ml-5"
          @click="linkToList">
          {{ t('permissionsStrategy.auth.cancel') }}
        </Button>
      </FormItem>
    </Form>
  </PureCard>
</template>
