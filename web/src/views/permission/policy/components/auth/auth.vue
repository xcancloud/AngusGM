<script setup lang='ts'>
import { reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Form, FormItem, Radio, RadioGroup } from 'ant-design-vue';
import { Input, Modal, notification, SelectDept, SelectGroup, SelectUser } from '@xcan-angus/vue-ui';
import { auth } from '@/api';
import { AuthFormType } from '../PropsType';

/**
 * Component props interface
 * @interface Props
 * @property {boolean} visible - Controls modal visibility
 * @property {string} id - Policy ID for authorization operations
 */
interface Props {
  visible: boolean;
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false
});

/**
 * Component emits for parent communication
 * Notifies parent component of visibility changes
 */
const emits = defineEmits<{(e: 'update:visible', value: boolean) }>();

// const route = useRoute();
// const router = useRouter();
const { t } = useI18n();

/**
 * Reference to the form component
 * Used for form validation
 */
const formRef = ref();

/**
 * Component reactive state
 * Manages loading states, form data, and validation rules
 */
const state = reactive<{
  loading: boolean,
  saving: boolean,
  form: AuthFormType,
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
      { required: true, message: t('permission.policy.auth.rule'), trigger: 'change' }
    ]
  }
});

/**
 * Load policy details for authorization
 * Fetches policy information to populate the form
 */
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

/**
 * Handle target selection changes
 * Updates the form with selected target identifiers
 * @param {string[]} value - Array of selected target IDs
 */
const targetChange = (value: string[]) => {
  state.form.targetId = value;
};

/**
 * Cancel authorization operation
 * Closes the modal and resets form state
 */
const cancel = () => {
  emits('update:visible', false);
};

/**
 * Save authorization changes
 * Validates form and submits authorization request based on target type
 */
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

    notification.success(t('permission.policy.auth.success'));
    cancel();
  });
};

/**
 * Reset form to initial state
 * Clears target selection and resets target type to default
 */
const resetForm = () => {
  state.form.targetId = [];
  state.form.targetType = 'USER';
};

/**
 * Watch for modal visibility changes
 * Resets form and loads data when modal becomes visible
 */
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
  <!-- Authorization modal for policy targets -->
  <Modal
    :visible="props.visible"
    :title="t('permission.policy.auth.auth')"
    @ok="save"
    @cancel="cancel">
    <!-- Authorization form -->
    <Form
      ref="formRef"
      class="my-0 mx-auto"
      :model="state.form"
      :rules="state.rules"
      v-bind="{labelCol: {span: 5}, wrapperCol: {span: 18}}">
      <!-- Policy name display (read-only) -->
      <FormItem :label="t('permission.policy.auth.name')" name="name">
        <Input
          disabled
          :value="state.form.name"
          size="small" />
      </FormItem>

      <!-- Target type selection -->
      <FormItem :label="t('permission.policy.auth.targetType')" name="targetType">
        <RadioGroup v-model:value="state.form.targetType" class="mt-0.75">
          <Radio value="USER">{{ t('permission.policy.auth.user') }}</Radio>
          <Radio value="DEPT">{{ t('permission.policy.auth.dept') }}</Radio>
          <Radio value="GROUP">{{ t('permission.policy.auth.group') }}</Radio>
        </RadioGroup>
      </FormItem>

      <!-- User selection (conditional) -->
      <FormItem
        v-if="state.form.targetType === 'USER'"
        :label="t('permission.policy.auth.userLabel')"
        name="targetId">
        <SelectUser
          :placeholder="t('permission.policy.auth.userPlaceholder')"
          :allowClear="false"
          :internal="true"
          mode="multiple"
          size="small"
          @change="targetChange" />
      </FormItem>

      <!-- Department selection (conditional) -->
      <FormItem
        v-if="state.form.targetType === 'DEPT'"
        :label="t('permission.policy.auth.deptLabel')"
        name="targetId">
        <SelectDept
          :placeholder="t('permission.policy.auth.deptPlaceholder')"
          :allowClear="false"
          :internal="true"
          mode="multiple"
          size="small"
          @change="targetChange" />
      </FormItem>

      <!-- Group selection (conditional) -->
      <FormItem
        v-if="state.form.targetType === 'GROUP'"
        :label="t('permission.policy.auth.groupLabel')"
        name="targetId">
        <SelectGroup
          :placeholder="t('permission.policy.auth.groupPlaceholder')"
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
