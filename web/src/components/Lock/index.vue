<script setup lang='ts'>
import { reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { RuleObject } from 'ant-design-vue/lib/form/interface';
import { Form, FormItem, Radio, RadioGroup } from 'ant-design-vue';
import { http, utils } from '@xcan-angus/infra';
import { Colon, DatePicker, Icon, Modal, notification } from '@xcan-angus/vue-ui';

interface FormType {
  type: 1 | 2, // 1: Time-based Lock, 2: Irreversible Lock
  times: string[], // Lock time
}

interface Props {
  width: string,
  visible: boolean,
  id: string | undefined,
  action: string,
  title: string,
  tip: string
}

const { t } = useI18n();

const props = withDefaults(defineProps<Props>(), {
  visible: false
});

const emit = defineEmits<{(e: 'cancel'): void, (e: 'save'): void }>();

const confirmLoading = ref(false);

const formRef = ref();

const state = reactive<{ form: FormType }>({
  form: {
    type: 1,
    times: []
  }
});

const validTimes = async (_rule: RuleObject, value: string[]) => {
  if (state.form.type === 1 && value[0] && value[1]) {
    return Promise.resolve();
  }
  return Promise.reject(new Error(t('validTime')));
};

const rules = {
  times: [
    { validator: validTimes, trigger: 'blur' }
  ]
};

const handleFuncs = {
  save: () => {
    formRef.value.validate().then(() => {
      const params: {
        id: string | undefined,
        locked: boolean,
        lockStartDate?: string,
        lockEndDate?: string
      } = {
        id: props.id,
        locked: true
      };
      if (state.form.type === 1) {
        params.lockStartDate = state.form.times[0];
        params.lockEndDate = state.form.times[1];
      }
      confirmLoading.value = true;
      http.patch(props.action, params).then(() => {
        notification.success(t('lockSuccess'));
        emit('save');
      }, (err: { message: string }) => {
        notification.error(err.message);
      }).finally(() => {
        confirmLoading.value = false;
      });
    }, () => {
    });
  },
  close: () => {
    emit('cancel');
  },
  changeTime: (dateString: string[]) => {
    if (utils._typeof(dateString) === 'array') {
      state.form.times = dateString;
    } else {
      state.form.times = [];
    }
    formRef.value.validateFields(['times']);
  }
};
</script>
<template>
  <Modal
    :width="width"
    :title="title"
    :centered="true"
    :maskClosable="false"
    :keyboard="false"
    :confirmLoading="confirmLoading"
    :visible="visible"
    destroyOnClose
    @ok="handleFuncs.save"
    @cancel="handleFuncs.close">
    <Form
      ref="formRef"
      :model="state.form"
      :rules="rules"
      v-bind="{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }">
      <p class="mb-6 text-3 leading-3.5 text-warn">
        <Icon icon="icon-tishi1" class="inline text-3.5" />
        <span class="ml-2">{{ tip }}</span>
      </p>
      <div class="px-6 py-3.25 bg-gray-light">
        <span class="text-3 mr-2">{{ t('user.ockType') }}<Colon /></span>
        <RadioGroup v-model:value="state.form.type">
          <Radio :value="1">{{ t('enum.SentType.SEND_NOW') }}</Radio>
          <Radio :value="2">{{ t('enum.SentType.TIMING_SEND') }}</Radio>
        </RadioGroup>
      </div>
      <FormItem
        v-if="state.form.type === 1"
        :label="t('times')"
        name="times"
        class="mt-6">
        <DatePicker
          :showTime="true"
          type="date-range"
          class="-mt-1"
          @change="handleFuncs.changeTime">
          <template #suffixIcon>
            <Icon icon="icon-shijianriqi" class="inline" />
          </template>
        </DatePicker>
      </FormItem>
    </Form>
  </Modal>
</template>
