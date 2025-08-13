<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Grid, Icon, Input, PureCard } from '@xcan-angus/vue-ui';
import { Tag } from 'ant-design-vue';
import { appContext, regexpUtils } from '@xcan-angus/infra';
import { user } from '@/api';

// User information state - initialize with current user data
const userInfo = ref(appContext.getUser() || {});

const { t } = useI18n();

// Name editing state variables
const nameInput = ref(false);
const firstName = ref<string>('');
const prevFirstName = ref<string>('');
const lastName = ref<string>('');
const prevLastName = ref<string>('');
const fullName = ref<string>('');

// Validation error states for name fields
const firstNameError = ref(false);
const lastNameError = ref(false);
const fullNameError = ref(false);

// Other editable fields state
const titleInput = ref(false);
const title = ref<string>('');

const landlineInput = ref(false);
const landline = ref<string>('');

const addressInput = ref(false);
const address = ref<string>('');

/**
 * Table columns configuration for detailed information display
 * Organized in two rows for better layout and readability
 */
const columns = [
  [
    { dataIndex: 'name', label: t('information.columns.name') },
    { dataIndex: 'mobile', label: t('information.columns.mobile') },
    { dataIndex: 'email', label: t('information.columns.email') },
    { dataIndex: 'title', label: t('information.columns.title') },
    { dataIndex: 'landline', label: t('information.columns.landline') },
    { dataIndex: 'address', label: t('information.columns.address'), divide: true }
  ],
  [
    { dataIndex: 'createdDate', label: t('information.columns.registerTime') },
    { dataIndex: 'sysAdmin', label: t('information.columns.identity') },
    { dataIndex: 'group', label: t('information.columns.group') },
    { dataIndex: 'depts', label: t('information.columns.ownDept') },
    { dataIndex: 'tags', label: t('information.columns.userTags') },
    { dataIndex: 'otherAccount', label: t('information.columns.otherAccount'), offset: true }
  ]
];

/**
 * Initiates name editing mode
 * Sets up initial values and clears validation errors
 */
const editName = () => {
  if (!userInfo.value) return;

  firstName.value = userInfo.value.firstName || '';
  prevFirstName.value = firstName.value;
  lastName.value = userInfo.value.lastName || '';
  prevLastName.value = lastName.value;
  fullName.value = userInfo.value.fullName || '';
  nameInput.value = true;
  firstNameError.value = false;
  lastNameError.value = false;
  fullNameError.value = false;
};

/**
 * Handles first name input blur event
 * Validates input and triggers name change if modified
 */
const firstNameBlur = () => {
  firstNameError.value = !firstName.value;
  if (fullName.value && prevFirstName.value === firstName.value) {
    return;
  }

  prevFirstName.value = firstName.value;
  nameChange();
};

/**
 * Handles last name input blur event
 * Validates input and triggers name change if modified
 */
const lastNameBlur = () => {
  lastNameError.value = !lastName.value;
  if (fullName.value && prevLastName.value === lastName.value) {
    return;
  }

  prevLastName.value = lastName.value;
  nameChange();
};

/**
 * Handles full name input blur event
 * Validates full name input
 */
const fullNameBlur = () => {
  fullNameError.value = !fullName.value;
};

const nameChange = () => {
  if (!firstName.value || !lastName.value) {
    return;
  }

  if (regexpUtils.hasZh(firstName.value || '') || regexpUtils.hasZh(lastName.value || '')) {
    fullName.value = (firstName.value || '') + (lastName.value || '');
    return;
  }

  fullName.value = (lastName.value || '') + (firstName.value || '');
};

const nameSaving = ref(false);
const saveName = async () => {
  if (nameSaving.value) {
    return;
  }

  if (!firstName.value || !lastName.value || !fullName.value) {
    firstNameError.value = !firstName.value;
    lastNameError.value = !lastName.value;
    fullNameError.value = !fullName.value;
    return;
  }

  const params = {
    firstName: firstName.value,
    lastName: lastName.value,
    fullName: fullName.value
  };
  nameSaving.value = true;
  const [error] = await user.updateCurrentUser(params);
  nameSaving.value = false;
  if (error) {
    return;
  }

  userInfo.value.firstName = params.firstName;
  userInfo.value.lastName = params.lastName;
  userInfo.value.fullName = params.fullName;
  nameInput.value = false;
};

const cancelNameEdit = () => {
  nameInput.value = false;
  firstName.value = '';
  lastName.value = '';
  fullName.value = '';
};

const editTitle = () => {
  title.value = userInfo.value.title || '';
  titleInput.value = true;
};

const titleSaving = ref(false);
const saveTitle = async () => {
  if (titleSaving.value) {
    return;
  }

  const params = { title: title.value };
  titleSaving.value = true;
  const [error] = await user.updateCurrentUser(params);
  titleSaving.value = false;
  if (error) {
    return;
  }

  userInfo.value.title = params.title;
  titleInput.value = false;
};

const cancelTitleEdit = () => {
  titleInput.value = false;
  title.value = '';
};

/**
 * Initiates landline editing mode
 * Sets up initial value for landline input
 */
const editLandline = () => {
  if (!userInfo.value) return;
  landline.value = userInfo.value.landline || '';
  landlineInput.value = true;
};

// Loading state for landline save operation
const landlineSaving = ref(false);

/**
 * Saves landline changes to backend
 * Updates local state on successful save
 */
const saveLandline = async () => {
  if (landlineSaving.value) {
    return;
  }

  const params = { landline: landline.value };
  landlineSaving.value = true;
  const [error] = await user.updateCurrentUser(params);
  landlineSaving.value = false;
  if (error) {
    return;
  }

  if (userInfo.value) {
    userInfo.value.landline = params.landline;
  }
  landlineInput.value = false;
};

/**
 * Cancels landline editing and resets input state
 */
const cancelLandlineEdit = () => {
  landlineInput.value = false;
  landline.value = '';
};

/**
 * Initiates address editing mode
 * Sets up initial value for address input
 */
const editAddress = () => {
  if (!userInfo.value) return;
  address.value = userInfo.value.address || '';
  addressInput.value = true;
};

const addressSaving = ref(false);
const saveAddress = async () => {
  if (addressSaving.value) {
    return;
  }

  const params = { address: address.value };
  addressSaving.value = true;
  const [error] = await user.updateCurrentUser(params);
  addressSaving.value = false;
  if (error) {
    return;
  }

  userInfo.value.address = params.address;
  addressInput.value = false;
};

const cancelAddressEdit = () => {
  addressInput.value = false;
  address.value = '';
};

/**
 * Computed property for user departments
 * Returns empty array if no departments assigned
 */
const depts = computed(() => {
  return userInfo.value?.depts || [];
});

/**
 * Computed property for user groups
 * Returns empty array if no groups assigned
 */
const groups = computed(() => {
  return userInfo.value?.groups || [];
});

/**
 * Computed property for formatted phone number
 * Combines country code and mobile number
 */
const phone = computed(() => {
  if (!userInfo.value?.itc || !userInfo.value?.mobile) return '';

  const temp = (+userInfo.value.itc + ' ' + userInfo.value.mobile).replace(/\+/, '');
  return temp ? ('+' + temp) : '';
});

/**
 * Watches for changes in user information
 * Synchronizes local state with updated user data
 * Resets validation errors when user data changes
 */
watch(() => userInfo.value, (newValue: any) => {
  if (!newValue?.id) {
    return;
  }

  title.value = newValue.title || '';
  landline.value = newValue.landline || '';
  firstName.value = newValue.firstName || '';
  lastName.value = newValue.lastName || '';
  fullName.value = newValue.fullName || '';

  firstNameError.value = false;
  lastNameError.value = false;
  fullNameError.value = false;
}, {
  immediate: true
});
</script>
<template>
  <!-- Detailed information card with user data grid -->
  <PureCard class="py-7.5 px-25 w-11/12 2xl:px-6 mx-auto">
    <Grid :columns="columns" :dataSource="userInfo">
      <template #name>
        <div class="relative flex justify-between">
          <template v-if="!nameInput">
            {{ userInfo.fullName }}
            <a class="mr-20 text-theme-special" @click="editName">
              <Icon icon="icon-shuxie" class="text-3.5" />
            </a>
          </template>
          <template v-else>
            <div class="absolute -top-1.5 flex flex-nowrap items-center whitespace-nowrap">
              <Input
                v-model:value="lastName"
                :error="lastNameError"
                placeholder="t('information.placeholder.lastName')"
                title="t('information.placeholder.lastName')"
                class="mr-2"
                @blur="lastNameBlur" />
              <Input
                v-model:value="firstName"
                :error="firstNameError"
                placeholder="t('information.placeholder.firstName')"
                title="t('information.placeholder.firstName')"
                class="mr-2"
                @blur="firstNameBlur" />
              <Input
                v-model:value="fullName"
                :error="fullNameError"
                placeholder="t('information.placeholder.fullName')"
                title="t('information.placeholder.fullName')"
                @blur="fullNameBlur" />
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 content-primary-text"
                @click.prevent="saveName">{{ t('information.messages.save') }}</a>
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 text-theme-content hover:text-theme-content"
                @click.prevent="cancelNameEdit">{{ t('information.messages.cancel') }}</a>
            </div>
          </template>
        </div>
      </template>
      <template #mobile>{{ phone }}</template>
      <template #title>
        <div class="relative flex justify-between">
          <template v-if="!titleInput">
            {{ userInfo.title }}
            <a class="mr-20 text-theme-special" @click="editTitle">
              <Icon icon="icon-shuxie" class="text-3.5" />
            </a>
          </template>
          <template v-else>
            <div
              style="width: calc(100% - 80px);"
              class="absolute -top-1.5 flex flex-nowrap items-center whitespace-nowrap">
              <Input
                v-model:value="title"
                title="t('information.placeholder.title')"
                placeholder="t('information.placeholder.title')"
                allowClear />
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 content-primary-text"
                @click.prevent="saveTitle">{{ t('information.messages.save') }}</a>
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 text-theme-content hover:text-theme-content"
                @click.prevent="cancelTitleEdit">{{ t('information.messages.cancel') }}</a>
            </div>
          </template>
        </div>
      </template>
      <template #landline>
        <div class="relative flex justify-between">
          <template v-if="!landlineInput">
            {{ userInfo.landline }}
            <a class="mr-20 text-theme-special" @click="editLandline">
              <Icon icon="icon-shuxie" class="text-3.5" />
            </a>
          </template>
          <template v-else>
            <div
              style="width: calc(100% - 80px);"
              class="absolute -top-1.5 flex flex-nowrap items-center whitespace-nowrap">
              <Input
                v-model:value="landline"
                title="t('information.placeholder.landline')"
                placeholder="t('information.placeholder.landline')"
                allowClear
                dataType="number"
                includes="-" />
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 content-primary-text"
                @click.prevent="saveLandline">{{ t('information.messages.save') }}</a>
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 text-theme-content hover:text-theme-content"
                @click.prevent="cancelLandlineEdit">{{ t('information.messages.cancel') }}</a>
            </div>
          </template>
        </div>
      </template>
      <template #address>
        <div class="relative flex justify-between">
          <template v-if="!addressInput">
            {{ userInfo.address }}
            <a class="mr-20 text-theme-special" @click="editAddress">
              <Icon icon="icon-shuxie" class="text-3.5" />
            </a>
          </template>
          <template v-else>
            <div
              style="width: calc(100% - 80px);"
              class="absolute -top-1.5 flex flex-nowrap items-center whitespace-nowrap">
              <Input
                v-model:value="address"
                title="t('information.placeholder.address')"
                placeholder="t('information.placeholder.address')"
                allowClear />
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 content-primary-text"
                @click.prevent="saveAddress">{{ t('information.messages.save') }}</a>
              <a
                class="flex flex-nowrap whitespace-nowrap ml-3 text-3 text-theme-content hover:text-theme-content"
                @click.prevent="cancelAddressEdit">{{ t('information.messages.cancel') }}</a>
            </div>
          </template>
        </div>
      </template>
      <template #group>
        <template v-if="groups?.length">
          <div
            v-for="item in groups"
            :key="item.id"
            class="inline-flex space-x-1 mr-2">
            <div class="w-5 h-5 rounded-full bg-blue-tips flex items-center justify-center">
              <Icon class="inline h-full text-theme-content text-4" icon="icon-zu" />
            </div>
            <span>{{ item.name }}</span>
          </div>
        </template>
      </template>
      <template #sysAdmin>
        {{ userInfo.sysAdmin ? t('information.status.systemAdmin') :
          t('information.status.generalUser') }}
      </template>
      <template #deptHead>
        {{ userInfo.deptHead ? t('information.status.principal') :
          t('information.status.generalUser') }}
      </template>
      <template #depts>
        <template v-if="depts?.length">
          <div
            v-for="item in depts"
            :key="item.id"
            class="inline-flex  space-x-1 mr-2">
            <div class="w-5 h-5 rounded-full bg-blue-tips flex items-center justify-center">
              <Icon class="inline h-full text-theme-content text-4" icon="icon-bumen" />
            </div>
            <span>{{ item.name }}</span>
          </div>
        </template>
      </template>
      <template #tags>
        <div v-if="userInfo.tags">
          <Tag v-for="item in userInfo.tags" :key="item.id">{{ item.name }}</Tag>
        </div>
      </template>
      <template #otherAccount>
        <div class="flex justify-between">
          <div>
            <Icon
              class="mr-4 text-6 leading-6 text-gray-placeholder"
              :class="{ 'active-wechat': userInfo.wechatUserId }"
              icon="icon-weixin" />
            <Icon
              class="mr-4 text-6 leading-6 text-gray-placeholder"
              :class="{ 'active-google': userInfo.googleUserId }"
              icon="icon-google" />
            <Icon
              class="text-6 leading-6 text-gray-placeholder"
              :class="{ 'active-github': userInfo.githubUserId }"
              icon="icon-Github" />
          </div>
        </div>
      </template>
      <template #tenantName="{text}">
        {{ text }}&nbsp;&nbsp;(ID:&nbsp;{{ userInfo?.tenantId }})
      </template>
    </Grid>
  </PureCard>
</template>
<style scoped>
/* Social media account status indicators */
.active-wechat {
  @apply text-green-wechat;
}

.active-google {
  @apply text-blue-google;
}

.active-github {
  @apply text-black-title;
}
</style>
