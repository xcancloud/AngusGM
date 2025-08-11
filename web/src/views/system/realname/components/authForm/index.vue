<script setup lang='ts'>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Form, FormItem } from 'ant-design-vue';
import { Card, Hints, Input, notification } from '@xcan-angus/vue-ui';
import { TenantType, regexpUtils } from '@xcan-angus/infra';

import UploadImage from './uploadImage.vue';
import AuditStatus from '@/components/AuditStatus/index.vue';
import ButtonGroup from './buttonGroup.vue';

import { tenant } from '@/api';

// Enterprise certification data interface
interface EnterpriseCertDataInfo {
  businessLicensePicUrl: string,
  name: string,
  creditCode: string
}

// Legal person certification data interface
interface LegalCertDataInfo {
  certBackPicUrl: string,
  certFrontPicUrl: string,
  certNo: string,
  name: string
}

// Organization certification data interface
interface OrgCertDataInfo {
  name: string,
  orgCode: string,
  orgCertPicUrl: string
}

// User certification data interface
interface UserCertDataInfo {
  certBackPicUrl: string,
  certFrontPicUrl: string,
  certNo: string,
  name: string
}

// New form data interface
interface NewForm {
  enterpriseCert?: EnterpriseCertDataInfo,
  enterpriseLegalPersonCert?: LegalCertDataInfo,
  governmentCert?: OrgCertDataInfo,
  type: string,
  personalCert?: UserCertDataInfo
}

// Form state interface
interface FormState {
  id: string,
  autoAudit: boolean,
  enterpriseCert: EnterpriseCertDataInfo,
  enterpriseLegalPersonCert: LegalCertDataInfo,
  governmentCert: OrgCertDataInfo,
  type: string,
  personalCert: UserCertDataInfo
}

interface Props {
  type: string
}

const route = useRoute();
const query = route.query;

const emit = defineEmits<{(e: 'cancel'): void, (e: 'confirmed'): void }>();

const props = withDefaults(defineProps<Props>(), {
  type: ''
});

const { t } = useI18n();

const confirmLoading = ref(false);

const formRef = ref();

// Audit in progress indicator
const unding = ref(false);

// Upload component error states
const errorUpload = ref<number[]>([]);

/* Authentication types: PERSONAL, ENTERPRISE, GOVERNMENT */

// Form data structure
const form = ref<FormState>({
  id: String(query.q),
  autoAudit: false,
  // Enterprise certification data
  enterpriseCert: {
    businessLicensePicUrl: '',
    name: '',
    creditCode: ''
  },
  // Enterprise legal person certification data
  enterpriseLegalPersonCert: {
    certBackPicUrl: '',
    certFrontPicUrl: '',
    certNo: '',
    name: ''
  },
  // Government organization certification data
  governmentCert: {
    name: '',
    orgCode: '',
    orgCertPicUrl: ''
  },
  type: props.type,
  // Personal certification data
  personalCert: {
    certBackPicUrl: '',
    certFrontPicUrl: '',
    certNo: '',
    name: ''
  }
});

/**
 * Validate ID card number based on authentication type
 */
const validateId = () => {
  if (props.type === TenantType.PERSONAL) {
    if (!form.value.personalCert.certNo.trim()) {
      return Promise.reject(new Error(t('realname.validation.idCardRequired')));
    }
    if (!regexpUtils.isId(form.value.personalCert.certNo)) {
      return Promise.reject(new Error(t('realname.messages.idCardFormatError')));
    }
    return Promise.resolve();
  }
  if (props.type === TenantType.ENTERPRISE) {
    if (!form.value.enterpriseLegalPersonCert.certNo.trim()) {
      return Promise.reject(new Error(t('realname.validation.legalIdCardRequired')));
    }
    if (!regexpUtils.isId(form.value.enterpriseLegalPersonCert.certNo)) {
      return Promise.reject(new Error(t('realname.messages.idCardFormatError')));
    }
    return Promise.resolve();
  }
  return Promise.resolve();
};

// Form validation rules
const rules = {
  // Enterprise certification rules
  enterpriseCert: {
    businessLicensePicUrl: [{
      required: true,
      message: t('realname.validation.businessLicenseRequired'),
      trigger: 'change',
      validator: (rules: any) => errorRules(rules, 3)
    }],
    name: [{ required: true, messge: t('realname.validation.companyNameRequired'), trigger: 'change' }],
    creditCode: [{ required: true, message: t('realname.validation.enterpriseNameRequired'), trigger: 'change' }]
  },
  // Enterprise legal person certification rules
  enterpriseLegalPersonCert: {
    certFrontPicUrl: [{
      required: true,
      message: t('realname.messages.uploadLegalIdCard'),
      trigger: 'change',
      validator: (rules: any) => errorRules(rules, 1)
    }],
    certNo: [{ required: true, validator: validateId, trigger: 'blur' }],
    name: [{ required: true, message: t('realname.placeholder.inputLegalName'), trigger: 'change' }]
  },
  // Government organization certification rules
  governmentCert: {
    name: [{ required: true, message: t('realname.placeholder.inputOrgName'), trigger: 'change' }],
    orgCode: [{ required: true, message: t('realname.validation.orgCodeRequired'), trigger: 'change' }],
    orgCertPicUrl: [{
      required: true,
      message: t('realname.validation.orgCertificateRequired'),
      trigger: 'change',
      validator: (rules: any) => errorRules(rules, 3)
    }]
  },
  // Personal certification rules
  personalCert: {
    certFrontPicUrl: [{
      required: true,
      message: t('realname.messages.uploadIdCard'),
      trigger: 'change',
      validator: (rules: any) => errorRules(rules, 1)
    }],
    certNo: [{ required: true, validator: validateId, trigger: 'blur' }],
    name: [{ required: true, message: t('realname.placeholder.inputRealName'), trigger: 'change' }]
  }
};

/**
 * Remove error code from upload error array
 * @param code - Error code to remove
 */
const deleteCode = function (code: number) {
  errorUpload.value = errorUpload.value.filter(item => item !== code);
};

/**
 * Custom validation rules for upload components
 * @param rules - Validation rules object
 * @param errCode - Error code for specific validation
 */
async function errorRules (rules: { message: string }, errCode: number) {
  // Add delay for validation
  await new Promise<void>((resolve) => {
    setTimeout(() => {
      resolve();
    }, 1000);
  });

  if (errCode === 3) {
    // Business license or organization certificate validation
    const rules3 = form.value.enterpriseCert.businessLicensePicUrl === '' &&
      form.value.governmentCert.orgCertPicUrl === '';
    if (rules3) {
      errorUpload.value.push(errCode);
      return Promise.reject(new Error(rules.message));
    }
    deleteCode(errCode);
    return Promise.resolve();
  } else {
    // ID card front and back validation
    const rules1 = form.value.personalCert.certFrontPicUrl === '' &&
      form.value.enterpriseLegalPersonCert.certFrontPicUrl === '';
    const rules2 = form.value.personalCert.certBackPicUrl === '' &&
      form.value.enterpriseLegalPersonCert.certBackPicUrl === '';

    if (rules1 && rules2) {
      errorUpload.value.push(1);
      errorUpload.value.push(2);
      return Promise.reject(new Error(rules.message));
    } else if (rules1) {
      errorUpload.value.push(1);
      deleteCode(2);
      return Promise.reject(new Error(rules.message));
    } else if (rules2) {
      errorUpload.value.push(2);
      deleteCode(1);
      return Promise.reject(new Error(rules.message));
    } else {
      deleteCode(1);
      deleteCode(2);
      return Promise.resolve();
    }
  }
}

/**
 * Handle cancel event
 */
const cancel = function () {
  emit('cancel');
};

/**
 * Handle form confirmation and submission
 */
const confirm = function () {
  confirmLoading.value = true;
  formRef.value.validate()
    .then(async () => {
      const newForm: NewForm = {
        type: form.value.type
      };

      /* Build form data based on authentication type */
      if (props.type === TenantType.PERSONAL) {
        newForm.personalCert = form.value.personalCert;
      } else if (props.type === TenantType.ENTERPRISE) {
        newForm.enterpriseCert = form.value.enterpriseCert;
        newForm.enterpriseLegalPersonCert = form.value.enterpriseLegalPersonCert;
      } else {
        newForm.governmentCert = form.value.governmentCert;
      }

      const [error] = await tenant.submitCertAudit(newForm);
      confirmLoading.value = false;

      if (error) {
        return;
      }

      notification.success(t('realname.messages.successSubmit'));
      emit('confirmed');
    }).catch(() => {
      confirmLoading.value = false;
    });
};

/**
 * Handle file upload change events
 * @param label - Upload component identifier
 * @param value - Uploaded file ID or URL
 */
const loadChange = function (label: string, value: any) {
  /* Process uploaded image ID or URL */
  /* Handle different authentication types: PERSONAL, ENTERPRISE, GOVERNMENT */
  switch (label) {
    case '0':
      /* ID card front side */
      if (props.type === TenantType.PERSONAL) {
        form.value.personalCert.certFrontPicUrl = value;
      } else {
        form.value.enterpriseLegalPersonCert.certFrontPicUrl = value;
      }
      break;
    case '1':
      /* ID card back side */
      if (props.type === TenantType.PERSONAL) {
        form.value.personalCert.certBackPicUrl = value;
      } else {
        form.value.enterpriseLegalPersonCert.certBackPicUrl = value;
      }
      break;
    default:
      /* Other certificates */
      if (props.type === TenantType.ENTERPRISE) {
        form.value.enterpriseCert.businessLicensePicUrl = value;
      } else {
        form.value.governmentCert.orgCertPicUrl = value;
      }
      break;
  }
};

const trimValue = (type, key) => {
  form.value[type][key] = form.value[type][key].trim();
};

</script>
<template>
  <Card v-if="unding" class="mx-auto overflow-hidden">
    <AuditStatus backUrl="/" />
  </Card>
  <Card v-else class="flex-1">
    <div class="w-139 mx-auto mt-10">
      <Form
        ref="formRef"
        :model="form"
        :rules="rules"
        :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 17 }">
        <template v-if="props.type === TenantType.PERSONAL">
          <FormItem
            :label="t('realname.columns.realName')"
            :name="['personalCert','name']"
            :colon="false">
            <Input
              v-model:value="form.personalCert.name"
              :maxlength="80"
              :placeholder="t('realname.placeholder.inputRealName')"
              @blur="trimValue('personalCert', 'name')" />
          </FormItem>
          <FormItem
            :label="t('realname.columns.idCard')"
            :name="['personalCert','certNo']"
            :colon="false">
            <Input
              v-model:value="form.personalCert.certNo"
              :maxlenth="50"
              :placeholder="t('realname.validation.idCardRequired')"
              @blur="trimValue('personalCert', 'certNo')" />
          </FormItem>
          <FormItem
            :label="t('realname.columns.certFront')"
            :name="['enterpriseLegalPersonCert','certFrontPicUrl']"
            :colon="false">
            <UploadImage
              type="1"
              :mess="t('realname.placeholder.uploadIdCardFront')"
              class="mr-8 inline-block"
              :error="errorUpload.includes(1)"
              @change="(value)=> loadChange('0', value)" />
            <UploadImage
              type="1"
              class="inline-block"
              :mess="t('realname.placeholder.uploadIdCardBack')"
              :error="errorUpload.includes(2)"
              @change="(value)=> loadChange('1', value)" />
          </FormItem>
        </template>
        <template v-else-if="props.type === TenantType.ENTERPRISE">
          <FormItem
            :label="t('realname.columns.enterpriseName')"
            :name="['enterpriseCert','name']"
            :colon="false">
            <Input
              v-model:value="form.enterpriseCert.name"
              :maxlength="30"
              :placeholder="t('realname.placeholder.inputEnterpriseName')"
              @blur="trimValue('enterpriseCert', 'name')" />
          </FormItem>
          <FormItem
            :label="t('realname.columns.creditCode')"
            :name="['enterpriseCert','creditCode']"
            :colon="false">
            <Input
              v-model:value="form.enterpriseCert.creditCode"
              :maxlength="50"
              :placeholder="t('realname.placeholder.inputCreditCode')" />
          </FormItem>
          <FormItem
            :label="t('realname.columns.legalName')"
            :name="['enterpriseLegalPersonCert','name']"
            :colon="false">
            <Input
              v-model:value="form.enterpriseLegalPersonCert.name"
              :placeholder="t('realname.placeholder.inputLegalName')"
              :maxlenth="50"
              @blur="trimValue('enterpriseLegalPersonCert', 'name')" />
          </FormItem>
          <FormItem
            :label="t('realname.columns.legalIdCard')"
            :name="['enterpriseLegalPersonCert','certNo']"
            :colon="false">
            <Input
              v-model:value="form.enterpriseLegalPersonCert.certNo"
              :placeholder="t('realname.validation.legalIdCardRequired')"
              :maxlenth="50"
              @blur="trimValue('enterpriseLegalPersonCert', 'certNo')" />
          </FormItem>
          <FormItem
            :label="t('realname.columns.certFront')"
            :name="['enterpriseLegalPersonCert','certFrontPicUrl']"
            :colon="false">
            <div class="flex">
              <UploadImage
                type="1"
                :mess="t('realname.placeholder.uploadLegalIdCardFront')"
                class="mr-5"
                :error="errorUpload.includes(1)"
                @change="(value)=> loadChange('0', value)" />
              <UploadImage
                type="1"
                :mess="t('realname.placeholder.uploadLegalIdCardBack')"
                :error="errorUpload.includes(2)"
                @change="(value)=> loadChange('1', value)" />
            </div>
          </FormItem>
          <FormItem
            :label="t('realname.columns.businessLicense')"
            :name="['enterpriseCert','businessLicensePicUrl']"
            :colon="false">
            <UploadImage
              type="2"
              :mess="t('realname.placeholder.uploadBusinessLicense')"
              :error="errorUpload.includes(3)"
              class="mr-5"
              @change="(value)=> loadChange('2', value)" />
          </FormItem>
        </template>
        <template v-else>
          <FormItem
            :label="t('realname.columns.orgName')"
            :maxlength="30"
            :name="['governmentCert','name']"
            :colon="false">
            <Input
              v-model:value="form.governmentCert.name"
              :placeholder="t('realname.placeholder.inputOrgName')"
              :maxlenth="80"
              @blur="trimValue('governmentCert', 'name')" />
          </FormItem>
          <FormItem
            :label="t('realname.columns.orgCode')"
            :name="['governmentCert','orgCode']"
            :colon="false">
            <Input
              v-model:value="form.governmentCert.orgCode"
              :maxlength="50"
              :placeholder="t('realname.placeholder.inputOrgCode')"
              @blur="trimValue('governmentCert', 'orgCode')" />
          </FormItem>
          <FormItem
            :label="t('realname.columns.orgCertificate')"
            :name="['governmentCert','orgCertPicUrl']"
            :colon="false">
            <UploadImage
              type="2"
              :mess="t('realname.placeholder.uploadOrgCertificate')"
              class="mr-5"
              @change="(value)=> loadChange('2', value)" />
          </FormItem>
        </template>
      </Form>
      <div class="w-250 h-0.25 bg-gray-border -ml-60 mt-9"></div>
      <Hints :text="t('realname.messages.registerAccountDefaultAdmin')" class="ml-40 mt-10" />
    </div>
    <ButtonGroup
      class="my-7.5"
      :confirmLoading="confirmLoading"
      @cancel="cancel"
      @confirm="confirm" />
  </Card>
</template>
