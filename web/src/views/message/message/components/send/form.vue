<script setup lang="ts">
import {ref, onMounted, watch} from 'vue';
import {useI18n} from 'vue-i18n';
import {lazyEnum} from '@xcan/enum';
import {Grid, DatePicker, Input, Hints} from '@xcan/design';
import dayjs from 'dayjs';
import RichEditor from '@/components/RichEditor/index.vue';
import {RadioGroup, Radio} from 'ant-design-vue';

import {email} from '@/api';

interface Props {
  propsTitle: string;
  propsContent: string;
  propsReceiveType: string;
  propsSendType: string;
  propsTimingDate: string;
  propsTitleRule: boolean;
  propsContentRule: boolean;
  propsContentRuleMsg: string;
  propsDateRule: boolean;
  notify: number;
}

const props = withDefaults(defineProps<Props>(), {
  propsTitle: undefined,
  propsContent: undefined,
  propsReceiveType: undefined,
  propsSendType: undefined,
  propsTimingDate: undefined,
  propsTitleRule: false,
  propsContentRule: false,
  propsContentRuleMsg: '',
  propsDateRule: false,
  notify: 0
});

const emit = defineEmits<{
  (e: 'update:propsTitle', value: string): void,
  (e: 'update:propsContent', value: string): void,
  (e: 'update:propsReceiveType', value: string): void,
  (e: 'update:propsSendType', value: string): void,
  (e: 'update:propsTimingDate', value: string): void,
}>();

const {t} = useI18n();

const title = ref('');
const content = ref('');
const receiveType = ref('SITE');
const sendType = ref('SEND_NOW');
const timingDate = ref();

const init = () => {
  loadMessageReceiveType();
  loadSentType();
};

const messageReceiveType = ref<{ message: string, value: string }[]>([]);
const loadMessageReceiveType = async () => {
  messageReceiveType.value = (await lazyEnum.load('MessageReceiveType'))[1];
};

const sentTypeList = ref<{ message: string, value: string }[]>([]);
const loadSentType = async () => {
  const [_error, data] = await lazyEnum.load('SentType');
  sentTypeList.value = data;
};

onMounted(() => {
  init();
});

const columns = ref([
  [
    {
      label: t('messageTitle'),
      dataIndex: 'title',
      required: true
    },
    {
      label: t('messageContent'),
      dataIndex: 'content',
      required: true
    },
    {
      label: t('receivingMethod'),
      dataIndex: 'receiveType'
    },
    {
      label: t('sendMethod'),
      dataIndex: 'sendType'
    }
  ]
]);

const sendTypeChange = (e): void => {
  if (e.target.value !== 'TIMING_SEND') {
    columns.value[0].length = 4;
    return;
  }
  columns.value[0].push({
    label: t('sendDate'),
    dataIndex: 'date',
    required: true
  });
};

const hasEmail = ref(false);
const disabledEmail = ref(false);
const receiveTypeChange = async (e) => {
  if (e.target.value === 'EMAIL') {
    const params = {
      protocol: 'SMTP'
    };
    const [error] = await email.getServerCheck(params);
    if (error) {
      hasEmail.value = true;
      const timer = setTimeout(() => {
        hasEmail.value = false;
        disabledEmail.value = true;
        receiveType.value = 'SITE';
        clearTimeout(timer);
      }, 5000);
      return;
    }
    hasEmail.value = false;
  }
};

const titleRule = ref(false);
const titleChange = (event: any) => {
  const value = event.target.value;
  titleRule.value = !value;
};

const contentRule = ref(false);
const contentRuleMsg = ref(t('请输入消息内容'));
watch(() => content.value, (newValue) => {
  if (!newValue) {
    contentRule.value = true;
    contentRuleMsg.value = t('请输入消息内容');
    return;
  }

  if (newValue.length >= 8000) {
    contentRule.value = true;
    contentRuleMsg.value = t('内容太长,无法发送');
  }
  contentRule.value = false;
});

const dateRule = ref(false);
const dateChange = (value) => {
  dateRule.value = !value;
};
onMounted(() => {
  watch(() => title.value, (newValue) => {
    emit('update:propsTitle', newValue);
  }, {
    immediate: true
  });
  watch(() => content.value, (newValue) => {
    emit('update:propsContent', newValue);
  }, {
    immediate: true
  });
  watch(() => receiveType.value, (newValue) => {
    emit('update:propsReceiveType', newValue);
  }, {
    immediate: true
  });
  watch(() => sendType.value, (newValue) => {
    emit('update:propsSendType', newValue);
  }, {
    immediate: true
  });
  watch(() => timingDate.value, (newValue) => {
    emit('update:propsTimingDate', newValue);
  }, {
    immediate: true
  });
});

watch(() => props.propsTitleRule, (newValue) => {
  titleRule.value = newValue;
});

watch(() => props.propsContentRule, (newValue) => {
  contentRule.value = newValue;
});

watch(() => props.propsContentRuleMsg, (newValue) => {
  contentRuleMsg.value = newValue;
});

watch(() => props.propsDateRule, (newValue) => {
  dateRule.value = newValue;
});

const disabledDate = current => {
  return current && current < dayjs();
};

const range = (start: number, end: number) => {
  const result: number[] = [];

  for (let i = start; i < end; i++) {
    result.push(i);
  }

  return result;
};

const currTime = dayjs().add(1, 'minute');
const disabledDateTime = () => {
  return {
    disabledHours: () => range(0, currTime.hour()),
    disabledMinutes: () => range(0, currTime.minute())
  };
};

const richEditorOption = ref({
  menubar: false,
  height: 'calc(100vh - 360px)'
});

const uploadOptions = {bizKey: 'messageFiles', mediaBizKey: 'messageFiles'};

watch(() => props.notify, () => {
  title.value = '';
  content.value = '';
  sendType.value = 'SEND_NOW';
});
</script>
<template>
  <Grid
    :columns="columns"
    nowrap
    class="flex-1 pt-1">
    <template #title>
      <Input
        v-model:value="title"
        :maxlength="100"
        size="small"
        class="-mt-1"
        :error="titleRule"
        :placeholder="t('pubPlaceholder',{name:t('title'),num:100})"
        @change="titleChange"/>
    </template>
    <template #content>
      <RichEditor
        v-model:value="content"
        :options="richEditorOption"
        :uploadOptions="uploadOptions"
        :class="{'rich-editor-rule':contentRule}"/>
      <div class="text-rule h-3.5">
        <template v-if="contentRule">
          {{ contentRuleMsg }}
        </template>
      </div>
    </template>
    <template #receiveType>
      <RadioGroup
        v-model:value="receiveType"
        size="small"
        class="flex items-center"
        @change="receiveTypeChange">
        <Radio
          v-for="item in messageReceiveType "
          :key="item.value"
          :value="item.value"
          :disabled="disabledEmail && item.value ==='EMAIL'">
          {{ item.message }}
        </Radio>
        <Hints :text="t('sendTips1')"/>
      </RadioGroup>
      <div v-if="hasEmail" class="text-rule h-3.5">
        {{ t('sendTips2') }}
      </div>
    </template>
    <template #sendType>
      <RadioGroup
        v-model:value="sendType"
        size="small"
        @change="sendTypeChange">
        <Radio
          v-for="item in sentTypeList"
          :key="item.value"
          :value="item.value">
          {{ item.message }}
        </Radio>
      </RadioGroup>
    </template>
    <template #date>
      <DatePicker
        v-model:value="timingDate"
        size="small"
        class="w-60"
        :disabled="sendType === 'SEND_NOW'"
        :class="{'date-rule-error':dateRule}"
        :disabledDate="disabledDate"
        :showTime="{hideDisabledOptions: true, defaultValue: dayjs('00:00:00', 'HH:mm:ss') }"
        :disabledTime="disabledDateTime"
        type="date"
        @change="dateChange"/>
    </template>
  </Grid>
</template>
<style scoped>
:deep(.audit-rule-error .ant-input:not([disabled])) {
  border-color: rgba(255, 77, 79, 100%) !important;
}

:deep(.ant-picker.ant-picker-small.date-rule-error:not([disabled])) {
  border-color: rgba(255, 77, 79, 100%) !important;
}

:deep(.tox-statusbar__wordcount) {
  display: none;
}

:deep(.tox.tox-tinymce) {
  min-height: 400px;
}

:deep(.rich-editor-rule + .tox.tox-tinymce) {
  border: 1px solid rgba(255, 77, 79, 100%) !important;
}
</style>
