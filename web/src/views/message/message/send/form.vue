<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { EnumMessage, enumUtils } from '@xcan-angus/infra';
import { MessageReceiveType, SentType } from '@/enums/enums';
import { DatePicker, Hints, Input } from '@xcan-angus/vue-ui';
import dayjs from 'dayjs';
import RichEditor from '@/components/RichEditor/index.vue';
import { Form, FormItem, Radio, RadioGroup } from 'ant-design-vue';

import { email } from '@/api';
import { getDisabledTimeOptions, isPastDate } from '../utils';

/**
 * Component props interface
 * Defines the properties passed from parent component
 */
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

/**
 * Component emits interface
 * Defines the events that this component can emit to parent
 */
const emit = defineEmits<{
  (e: 'update:propsTitle', value: string): void,
  (e: 'update:propsContent', value: string): void,
  (e: 'update:propsReceiveType', value: string): void,
  (e: 'update:propsSendType', value: string): void,
  (e: 'update:propsTimingDate', value: string): void,
}>();

const { t } = useI18n();

/**
 * Rich editor reference for content validation
 * Used to access editor methods for content extraction
 */
const richContentRef = ref();

// Form data fields
const title = ref('');
const content = ref('');
const receiveType = ref(MessageReceiveType.SITE);
const sendType = ref(SentType.SEND_NOW);
const timingDate = ref();

/**
 * Message receive type options
 * Available options for how messages can be received
 */
const messageReceiveType = ref<EnumMessage<MessageReceiveType>[]>([]);

/**
 * Load message receive type options
 * Populates the receive type dropdown with available options
 */
const loadMessageReceiveType = () => {
  messageReceiveType.value = (enumUtils.enumToMessages(MessageReceiveType));
};

/**
 * Send type options
 * Available options for when messages should be sent
 */
const sentTypeList = ref<EnumMessage<SentType>[]>([]);

/**
 * Load send type options
 * Populates the send type dropdown with available options
 */
const loadSentType = () => {
  sentTypeList.value = enumUtils.enumToMessages(SentType);
};

/**
 * Email server availability state
 * Tracks whether email server is available for sending
 */
const hasEmail = ref(false);

/**
 * Email option disabled state
 * Prevents email selection when server is unavailable
 */
const disabledEmail = ref(false);


// Form validation state
const titleRule = ref(false);

/**
 * Handle title input change
 * Validates title field and updates validation state
 */
const titleChange = (event: any) => {
  const value = event.target.value;
  titleRule.value = !value;
};

const contentRule = ref(false);
const contentRuleMsg = ref(t('messages.placeholder.content'));

const dateRule = ref(false);

/**
 * Handle date picker change
 * Validates date field and updates validation state
 */
const dateChange = (value: any) => {
  dateRule.value = !value;
};

/**
 * Disable past dates in date picker
 * Prevents scheduling messages in the past
 */
const disabledDate = isPastDate;

/**
 * Current time plus one minute
 * Used for time picker validation
 */
const currTime = dayjs().add(1, 'minute');

/**
 * Disable past time options
 * Prevents selecting past times for scheduled messages
 */
const disabledDateTime = () => getDisabledTimeOptions(currTime);

/**
 * File upload configuration
 * Defines business keys for message file uploads
 */
const uploadOptions = { bizKey: 'messageFiles', mediaBizKey: 'messageFiles' };


/**
 * Handle receive type change
 * Validates email server availability when email type is selected
 */
const receiveTypeChange = async (e: any) => {
  if (e.target.value === MessageReceiveType.EMAIL) {
    const params = {
      protocol: 'SMTP'
    };

    try {
      const [error] = await email.getServerCheck(params);

      if (error) {
        hasEmail.value = true;
        const timer = setTimeout(() => {
          hasEmail.value = false;
          disabledEmail.value = true;
          receiveType.value = MessageReceiveType.SITE;
          clearTimeout(timer);
        }, 5000);
        return;
      }
      hasEmail.value = false;
    } catch (err) {
      // Handle error silently
    }
  }
};

/**
 * Watch content changes for validation
 * Monitors content length and updates validation rules
 */
watch(() => content.value, (newValue) => {
  if (!newValue) {
    contentRule.value = true;
    contentRuleMsg.value = t('messages.placeholder.content');
    return;
  }

  if (newValue.length >= 8000) {
    contentRule.value = true;
    contentRuleMsg.value = t('messages.messages.inputContentLength');
  }
  contentRule.value = false;
});

/**
 * Watch parent validation state changes
 * Synchronizes validation state with parent component
 */
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

/**
 * Watch notification changes for form reset
 * Clears form data when parent component signals reset
 */
watch(() => props.notify, () => {
  title.value = '';
  content.value = '';
  sendType.value = SentType.SEND_NOW;
});

/**
 * Expose validation method to parent component
 * Provides form validation functionality for external use
 */
defineExpose({
  validate: () => {
    let result = true;

    // Validate title
    if (!title.value) {
      titleRule.value = true;
      result = false;
    }

    // Validate content from rich editor
    const contentData = richContentRef.value.getData();
    if (!contentData?.length) {
      result = false;
      contentRule.value = true;
      contentRuleMsg.value = t('messages.messages.inputContent');
    }

    // Validate content length
    if (contentData && contentData.length > 8000) {
      result = false;
      contentRule.value = true;
      contentRuleMsg.value = t('messages.messages.inputContentLength');
    }

    return result;
  }
});


/**
 * Initialize component data
 * Loads enum data for message receive types and send types
 */
const init = () => {
  loadMessageReceiveType();
  loadSentType();
};

/**
 * Set up watchers for form data synchronization
 * Ensures parent component receives updated form values
 */
onMounted(() => {
  init();

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

</script>

<template>
  <Form
    :labelCol="{style: {width: '130px'}}"
    class="flex-1 pt-1"
    :colon="false">
    <!-- Message Title Field -->
    <FormItem :label="t('messages.columns.title')" required>
      <Input
        v-model:value="title"
        :maxlength="100"
        size="small"
        class="-mt-1"
        :error="titleRule"
        :placeholder="t('messages.placeholder.inputTitle')"
        @change="titleChange" />
    </FormItem>

    <!-- Message Content Field -->
    <FormItem :label="t('messages.columns.content')" required>
      <RichEditor
        ref="richContentRef"
        v-model:value="content"
        :uploadOptions="uploadOptions"
        :height="360"
        :class="{'rich-editor-rule':contentRule}" />
      <div class="text-rule h-3.5">
        <template v-if="contentRule">
          {{ contentRuleMsg }}
        </template>
      </div>
    </FormItem>

    <!-- Receive Type Selection -->
    <FormItem :label="t('messages.columns.receiveType')">
      <RadioGroup
        v-model:value="receiveType"
        size="small"
        class="flex items-center"
        @change="receiveTypeChange">
        <Radio
          v-for="item in messageReceiveType "
          :key="item.value"
          :value="item.value"
          :disabled="disabledEmail && item.value === MessageReceiveType.EMAIL">
          {{ item.message }}
        </Radio>
        <Hints :text="t('messages.messages.sendEmailTip')" />
      </RadioGroup>
      <div v-if="hasEmail" class="text-rule h-3.5">
        {{ t('messages.messages.sendEmailCheckTip') }}
      </div>
    </FormItem>

    <!-- Send Type Selection -->
    <FormItem :label="t('messages.columns.sendType')">
      <RadioGroup
        v-model:value="sendType"
        size="small">
        <Radio
          v-for="item in sentTypeList"
          :key="item.value"
          :value="item.value">
          {{ item.message }}
        </Radio>
      </RadioGroup>
    </FormItem>

    <!-- Timing Date Selection (Conditional) -->
    <FormItem v-if="sendType === SentType.TIMING_SEND" :label="t('messages.columns.timingDate')">
      <DatePicker
        v-model:value="timingDate"
        size="small"
        class="w-60"
        :class="{'date-rule-error':dateRule}"
        :disabledDate="disabledDate"
        :showTime="{hideDisabledOptions: true, defaultValue: dayjs('00:00:00', 'HH:mm:ss') }"
        :disabledTime="disabledDateTime"
        type="date"
        @change="dateChange" />
    </FormItem>
  </Form>
</template>

<style scoped>
/**
 * Custom styling for form validation errors
 * Provides visual feedback for invalid input fields
 */
:deep(.audit-rule-error .ant-input:not([disabled])) {
  border-color: rgba(255, 77, 79, 100%) !important;
}

:deep(.ant-picker.ant-picker-small.date-rule-error:not([disabled])) {
  border-color: rgba(255, 77, 79, 100%) !important;
}

/**
 * Hide word count in rich editor
 * Removes default word count display
 */
:deep(.tox-statusbar__wordcount) {
  display: none;
}

/**
 * Set minimum height for rich editor
 * Ensures consistent editor sizing
 */
:deep(.tox.tox-tinymce) {
  min-height: 400px;
}

/**
 * Style rich editor validation errors
 * Provides visual feedback for content validation
 */
:deep(.rich-editor-rule + .tox.tox-tinymce) {
  border: 1px solid rgba(255, 77, 79, 100%) !important;
}

/**
 * Make form labels bold
 * Enhances label visibility while maintaining font size
 */
:deep(.ant-form-item-label > label) {
  font-weight: 700;
}
</style>
