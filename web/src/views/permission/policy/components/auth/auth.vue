<script setup lang='ts'>
import { reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Form, FormItem, Radio, RadioGroup } from 'ant-design-vue';
import { Input, Modal, notification, SelectDept, SelectGroup, SelectUser } from '@xcan-angus/vue-ui';

import { auth } from '@/api';

interface FormType {
  id: string,
  name: string,
  appId: string,
  targetType: 'USER' | 'DEPT' | 'GROUP',
  targetId: string[]
}

interface Props {
  visible: boolean;
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
});

const emits = defineEmits<{(e: 'update:visible', value: boolean) }>();

// const route = useRoute();
// const router = useRouter();
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
  const [error, res] = await auth.getPolicyDetail(props.id as string);
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
const cancel = () => {
  emits('update:visible', false);
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
    cancel();
  });
};

const resetForm = () => {
  state.form.targetId = [];
  state.form.targetType = 'USER';
};

watch(() => props.visible, newValue => {
  if (newValue) {
    resetForm();
    load();
  }
}, {
  immediate: true
});

</script>

<template>
  <Modal
    :visible="props.visible"
    title="授权"
    @ok="save"
    @cancel="cancel">
    <Form
      ref="formRef"
      class="my-0 mx-auto"
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
        <RadioGroup v-model:value="state.form.targetType" class="mt-0.75">
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
    </Form>
  </Modal>
</template>
<style scoped>
:deep(.ant-form-item-label > label) {
  height: 28px;
}
</style>
