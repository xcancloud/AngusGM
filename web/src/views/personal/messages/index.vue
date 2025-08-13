<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { Button, Checkbox, Pagination, RadioChangeEvent } from 'ant-design-vue';
import { Card, NoData, PureCard } from '@xcan-angus/vue-ui';
import RichEditor from '@/components/RichEditor/index.vue';
import { message } from '@/api';

// Message interface definition
interface Message {
  content: string,
  id: string,
  title: string,
  sendDate: string,
  read: boolean,
  fullName: string
}

const { t } = useI18n();

// Tab types for message filtering
type TabKey = 'ALL' | 'UNREAD' | 'READ'
const tabList: { key: TabKey, tab: string }[] = [
  { key: 'ALL', tab: t('message.tabs.all') },
  { key: 'UNREAD', tab: t('message.tabs.unread') },
  { key: 'READ', tab: t('message.tabs.read') }
];

// Message count state variables
const totalNum = ref(0);
const unreadNum = ref(0);
const readNum = ref(0);

// Load message count statistics from API
const loadCount = async () => {
  const [error, { data = [] }] = await message.getMessageStatusCount();
  if (error) {
    return;
  }
  data.every((item: { count: number, tab: { value: TabKey } }) => {
    switch (item.tab.value) {
      case 'ALL':
        totalNum.value = +item.count;
        break;
      case 'READ':
        readNum.value = +item.count;
        break;
      case 'UNREAD':
        unreadNum.value = +item.count;
        break;
    }
    return true;
  });
};

// Route and tab management
const route = useRoute();
const defaultId = route.params.id;
const initTab = defaultId ? 'UNREAD' : 'ALL';
const selectTab = ref<TabKey>(initTab);
const selectMessage = ref<Message>();

// Handle tab changes and reset pagination/selection state
const tabChange = (key: TabKey) => {
  selectTab.value = key;
  if (key === 'READ') {
    readPageNo.value = 1;
    readCheckAll.value = false;
    readCheckedList.value = [];
    readIndeterminate.value = false;
    selectMessage.value = undefined;
    loadMessages();
  } else if (key === 'UNREAD') {
    unreadPageNo.value = 1;
    unreadCheckAll.value = false;
    unreadCheckedList.value = [];
    unreadIndeterminate.value = false;
    selectMessage.value = undefined;
    loadMessages();
  } else {
    allPageNo.value = 1;
    checkAll.value = false;
    checkedList.value = [];
    indeterminate.value = false;
    selectMessage.value = undefined;
    loadMessages();
  }
};

// ALL messages tab state management
const checkAll = ref(false); // Whether all messages on current page are selected
const checkedList = ref<string[]>([]); // Selected message IDs on current page
const indeterminate = ref(false); // Indeterminate state for checkbox
const messageList = ref<Message[]>([]); // Message list for current page
const idList = computed(() => { // ID collection of messages on current page
  return messageList.value?.map(item => item.id);
});

// READ messages tab state management
const readCheckAll = ref(false); // Whether all read messages on current page are selected
const readCheckedList = ref<string[]>([]); // Selected read message IDs on current page
const readIndeterminate = ref(false); // Indeterminate state for read messages checkbox
const readMessageList = ref<Message[]>([]); // Read message list for current page
const readIdList = computed(() => { // ID collection of read messages on current page
  return readMessageList.value?.map(item => item.id);
});

// UNREAD messages tab state management
const unreadCheckAll = ref(false); // Whether all unread messages on current page are selected
const unreadCheckedList = ref<string[]>([]); // Selected unread message IDs on current page
const unreadIndeterminate = ref(false); // Indeterminate state for unread messages checkbox
const unreadMessageList = ref<Message[]>([]); // Unread message list for current page
const unreadIdList = computed(() => { // ID collection of unread messages on current page
  return unreadMessageList.value?.map(item => item.id);
});

// Handle select all checkbox changes
const onCheckAllChange = (e: RadioChangeEvent) => {
  if (e.target.checked) {
    switch (selectTab.value) {
      case 'READ':
        readCheckedList.value = readIdList.value;
        readCheckAll.value = true;
        readIndeterminate.value = false;
        break;
      case 'UNREAD':
        unreadCheckedList.value = unreadIdList.value;
        unreadCheckAll.value = true;
        unreadIndeterminate.value = false;
        break;
      default:
        checkedList.value = idList.value;
        checkAll.value = true;
        indeterminate.value = false;
        break;
    }
    return;
  }

  // Clear all selections
  switch (selectTab.value) {
    case 'READ':
      readCheckedList.value = [];
      readCheckAll.value = false;
      readIndeterminate.value = false;
      break;
    case 'UNREAD':
      unreadCheckedList.value = [];
      unreadCheckAll.value = false;
      unreadIndeterminate.value = false;
      break;
    default:
      checkedList.value = [];
      checkAll.value = false;
      indeterminate.value = false;
      break;
  }
};

// Pagination state for different tabs
const allTotal = ref(0); // Total count for ALL messages tab
const allPageNo = ref(1); // Current page number for ALL messages
const allPageSize = ref(10); // Page size for ALL messages

const readTotal = ref(0); // Total count for READ messages tab
const readPageNo = ref(1); // Current page number for READ messages
const readPageSize = ref(10); // Page size for READ messages

const unreadTotal = ref(0); // Total count for UNREAD messages tab
const unreadPageNo = ref(1); // Current page number for UNREAD messages
const unreadPageSize = ref(10); // Page size for UNREAD messages

// Get API parameters based on current tab
const getParams = () => {
  const params: {
    pageNo: number,
    pageSize: number,
    read?: boolean,
  } = {
    pageNo: 1,
    pageSize: 10
  };
  switch (selectTab.value) {
    case 'READ':
      params.pageNo = readPageNo.value;
      params.pageSize = readPageSize.value;
      params.read = true;
      break;
    case 'UNREAD':
      params.pageNo = unreadPageNo.value;
      params.pageSize = unreadPageSize.value;
      params.read = false;
      break;
    default:
      params.pageNo = allPageNo.value;
      params.pageSize = allPageSize.value;
      break;
  }
  return params;
};

// Load messages based on current tab and pagination
const loadMessages = async () => {
  const params = getParams();
  const [error, { data = { list: [], total: 0 } }] = await message.getCurrentMessages(params);
  if (error) {
    // Handle error by clearing data
    const read = params.read;
    if (typeof read === 'boolean') {
      if (read) {
        readTotal.value = 0;
        readMessageList.value = [];
      } else {
        unreadTotal.value = 0;
        unreadMessageList.value = [];
      }
    } else {
      allTotal.value = 0;
      messageList.value = [];
    }
    return;
  }

  // Auto-select first message if no message is selected
  if (!selectMessage.value?.id) {
    let currentMessage: Message;
    if (defaultId) {
      // Find message by default ID from route
      currentMessage = data.list.find(item => item.id === defaultId);
      if (currentMessage) {
        await loadMessageById(currentMessage?.id);
        // Mark as read if unread
        if (!currentMessage?.read) {
          const flag = await toMark([currentMessage.id]);
          if (flag) {
            currentMessage.read = true;
          }
        }
      }
    } else {
      // Select first message from list
      currentMessage = data.list[0];
      if (currentMessage) {
        selectMessage.value = currentMessage;
        await selectHandle(currentMessage);
      }
    }
  }

  // Update state based on current tab
  const read = params.read;
  if (typeof read === 'boolean') {
    if (read) {
      readTotal.value = +data.total;
      readMessageList.value = data.list;
    } else {
      unreadTotal.value = +data.total;
      unreadMessageList.value = data.list;
    }
  } else {
    allTotal.value = +data.total;
    messageList.value = data.list;
  }
};

// Handle individual checkbox changes
const checkboxChange = (e: RadioChangeEvent, _message: Message) => {
  const id = _message.id;
  if (e.target.checked) {
    // Add message to selected list
    switch (selectTab.value) {
      case 'READ':
        readCheckedList.value.push(id);
        readCheckAll.value = !!(readCheckedList.value.length && readCheckedList.value.length === readIdList.value.length);
        readIndeterminate.value = !!readCheckedList.value.length && readCheckedList.value.length < readMessageList.value.length;
        break;
      case 'UNREAD':
        unreadCheckedList.value.push(id);
        unreadCheckAll.value = !!(unreadCheckedList.value.length && unreadCheckedList.value.length === unreadIdList.value.length);
        unreadIndeterminate.value = !!unreadCheckedList.value.length && unreadCheckedList.value.length < unreadMessageList.value.length;
        break;
      default:
        checkedList.value.push(id);
        checkAll.value = !!(checkedList.value.length && checkedList.value.length === idList.value.length);
        indeterminate.value = !!checkedList.value.length && checkedList.value.length < messageList.value.length;
        break;
    }
  } else {
    // Remove message from selected list
    switch (selectTab.value) {
      case 'READ':
        readCheckedList.value = readCheckedList.value.filter(item => item !== id);
        readCheckAll.value = false;
        readIndeterminate.value = !!readCheckedList.value.length && readCheckedList.value.length < readMessageList.value.length;
        break;
      case 'UNREAD':
        unreadCheckedList.value = unreadCheckedList.value.filter(item => item !== id);
        unreadCheckAll.value = false;
        unreadIndeterminate.value = !!unreadCheckedList.value.length && unreadCheckedList.value.length < unreadMessageList.value.length;
        break;
      default:
        checkedList.value = checkedList.value.filter(item => item !== id);
        checkAll.value = false;
        indeterminate.value = !!checkedList.value.length && checkedList.value.length < messageList.value.length;
        break;
    }
  }
};

// Load message detail by ID
const loadMessageById = async (id: string) => {
  const [error, { data }] = await message.getCurrentMessageDetail(id);
  if (error) {
    return;
  }
  selectMessage.value = data;
};

// Handle message selection and mark as read if necessary
const selectHandle = async (_message: Message) => {
  selectMessage.value = _message;
  await loadMessageById(_message.id);
  if (!_message || _message.read) {
    return;
  }

  // Mark unread message as read
  const flag = await toMark([_message.id]);
  if (flag) {
    refresh();
  }
};

// Mark messages as read
const toMark = async (_ids: string[]) => {
  if (!_ids?.length) {
    return;
  }

  const [error] = await message.markCurrentMessageRead(_ids);
  return !error;
};

// Refresh message list and count
const refresh = () => {
  loadMessages();
  loadCount();
};

// Mark selected messages as read
const markHandle = () => {
  const _ids = showCheckedList.value.filter(item => {
    return showUnreadIdList.value.includes(item);
  });
  toMark(_ids);
};

// Delete selected messages
const toDel = async () => {
  const [error] = await message.deleteCurrentMessages(showCheckedList.value);
  if (error) {
    return;
  }

  // Clear right panel content if selected message is deleted
  const has = showCheckedList.value.findIndex(item => item === selectMessage.value?.id) > -1;
  if (has) {
    selectMessage.value = undefined;
  }

  refresh();

  // Reset selection state after deletion
  switch (selectTab.value) {
    case 'READ':
      readCheckAll.value = false;
      readCheckedList.value = [];
      readIndeterminate.value = false;
      break;
    case 'UNREAD':
      unreadCheckAll.value = false;
      unreadCheckedList.value = [];
      unreadIndeterminate.value = false;
      break;
    default:
      checkAll.value = false;
      checkedList.value = [];
      indeterminate.value = false;
      break;
  }
};

// Computed pagination object based on current tab
const pagination = computed(() => {
  switch (selectTab.value) {
    case 'READ':
      return {
        pageNo: readPageNo.value,
        pageSize: readPageSize.value,
        total: readTotal.value
      };
    case 'UNREAD':
      return {
        pageNo: unreadPageNo.value,
        pageSize: unreadPageSize.value,
        total: unreadTotal.value
      };
    default:
      return {
        pageNo: allPageNo.value,
        pageSize: allPageSize.value,
        total: allTotal.value
      };
  }
});

// Computed disabled state for action buttons
const disabled = computed(() => {
  switch (selectTab.value) {
    case 'READ':
      return !readCheckedList.value.length;
    case 'UNREAD':
      return !unreadCheckedList.value.length;
    default:
      return !checkedList.value.length;
  }
});

// Computed selected count for current tab
const showCheckedNum = computed(() => {
  switch (selectTab.value) {
    case 'READ':
      return readCheckedList.value.length;
    case 'UNREAD':
      return unreadCheckedList.value.length;
    default:
      return checkedList.value.length;
  }
});

// Computed select all state for current tab
const showCheckAll = computed(() => {
  switch (selectTab.value) {
    case 'READ':
      return readCheckAll.value;
    case 'UNREAD':
      return unreadCheckAll.value;
    default:
      return checkAll.value;
  }
});

// Computed indeterminate state for current tab
const showIndeterminate = computed(() => {
  switch (selectTab.value) {
    case 'READ':
      return readIndeterminate.value;
    case 'UNREAD':
      return unreadIndeterminate.value;
    default:
      return indeterminate.value;
  }
});

// Computed disabled state for mark as read button
const markDisabled = computed(() => {
  return !showCheckedList.value.find(item => showUnreadIdList.value.includes(item));
});

// Computed unread message IDs from current message list
const showUnreadIdList = computed(() => {
  return showMessageList.value.filter(item => !item.read).map(item => item.id);
});

// Computed message list for current tab
const showMessageList = computed(() => {
  switch (selectTab.value) {
    case 'READ':
      return readMessageList.value;
    case 'UNREAD':
      return unreadMessageList.value;
    default:
      return messageList.value;
  }
});

// Computed checked list for current tab
const showCheckedList = computed(() => {
  switch (selectTab.value) {
    case 'READ':
      return readCheckedList.value;
    case 'UNREAD':
      return unreadCheckedList.value;
    default:
      return checkedList.value;
  }
});

// Handle pagination changes
const paginationChange = (current: number, size: number) => {
  switch (selectTab.value) {
    case 'READ':
      readPageNo.value = size !== readPageSize.value ? 1 : current;
      readPageSize.value = size;
      readCheckedList.value = [];
      readCheckAll.value = false;
      readIndeterminate.value = false;
      break;
    case 'UNREAD':
      unreadPageNo.value = size !== unreadPageSize.value ? 1 : current;
      unreadPageSize.value = size;
      unreadCheckedList.value = [];
      unreadCheckAll.value = false;
      unreadIndeterminate.value = false;
      break;
    default:
      allPageNo.value = size !== allPageSize.value ? 1 : current;
      allPageSize.value = size;
      checkedList.value = [];
      checkAll.value = false;
      indeterminate.value = false;
      break;
  }
  loadMessages();
};

// Initialize component on mount
onMounted(() => {
  refresh();
  // Update browser history to remove route parameters
  history.pushState(null, '', location.origin + '/personal/messages');
});
</script>
<template>
  <div class="msg-outer-container flex space-x-2 w-11/12 mx-auto text-3">
    <Card
      v-model:value="selectTab"
      class="w-125 h-full flex-shrink-0 flex-grow-0"
      bodyClass="px-0 pt-3 leading-4 card-body"
      justify="between"
      :tabList="tabList"
      @change="tabChange">
      <template #ALL="{ tab }">
        {{ tab }}<em class="ml-1 not-italic">({{ totalNum }})</em>
      </template>
      <template #UNREAD="{ tab }">
        {{ tab }}<em class="ml-1 not-italic">({{ unreadNum }})</em>
      </template>
      <template #READ="{ tab }">
        {{ tab }}<em class="ml-1 not-italic">({{ readNum }})</em>
      </template>
      <template #default>
        <template v-if="!showMessageList.length">
          <NoData class="mb-12" />
        </template>
        <template v-else>
          <div class="flex items-center justify-between mx-7.5 mb-2 text-theme-content select-none">
            <div class="flex items-center">
              <Checkbox
                :checked="showCheckAll"
                :indeterminate="showIndeterminate"
                style="top: -2px;"
                class="relative"
                @change="onCheckAllChange">
                {{ $t('message.actions.selectAll') }}
              </Checkbox>
              <span class="ml-2">{{ $t('message.actions.selected') }}<em class="mx-1 not-italic">{{
                showCheckedNum
              }}</em>{{ $t('common.table.items') }}</span>
            </div>
            <div class="flex items-center">
              <Button
                v-if="selectTab !== 'READ'"
                size="small"
                :disabled="markDisabled"
                type="link"
                class="mr-4"
                @click="markHandle">
                {{ $t('message.actions.markRead') }}
              </Button>
              <Button
                size="small"
                :disabled="disabled"
                type="link"
                @click="toDel">
                {{ $t('common.actions.delete') }}
              </Button>
            </div>
          </div>
          <div class="row-divider"></div>
          <div class="list-main mt-0.5 px-5 flex-1 overflow-x-hidden overflow-y-auto text-theme-content">
            <div
              v-for="(item, index) in showMessageList"
              :key="item.id"
              class="item-container"
              :class="{ 'active': selectMessage?.id === item.id,'show-border-b':index < showMessageList.length - 1 }">
              <Checkbox
                class="relative flex-shrink-0"
                :checked="showCheckedList.includes(item.id)"
                @change="checkboxChange($event, item)" />
              <div class="row-container flex-1 pl-2 cursor-pointer" @click="selectHandle(item)">
                <div class="flex items-start justify-between">
                  <div class="flex-1 font-medium text-theme-title">
                    {{ item.title }}
                  </div>
                  <img
                    v-if="item.read"
                    class="w-3"
                    src="./assets/read.png">
                  <img
                    v-else
                    class="w-3"
                    src="./assets/unread.png">
                </div>
                <div class="flex items-center mt-2.5">
                  <div class="flex-1 truncate mr-3">
                    <span class="mr-1">{{ $t('message.columns.sender') }}:</span> {{
                      item.fullName
                    }}
                  </div>
                  <div class="flex-shrink-0">{{ item.sendDate }}</div>
                </div>
              </div>
            </div>
          </div>
          <div class="flex items-center h-12 pl-8 pr-6.5 border-t border-solid border-theme-divider">
            <div class="flex-1 truncate">
              {{ $t('common.table.total') }}<em class="mx-1 not-italic">{{
                pagination.total
              }}</em>{{ $t('common.table.items') }}
            </div>
            <Pagination
              size="small"
              :current="pagination.pageNo"
              :pageSize="pagination.pageSize"
              :total="pagination.total"
              @change="paginationChange" />
          </div>
        </template>
      </template>
    </Card>
    <PureCard class="flex-1 h-full px-5 pt-4 pb-3 leading-4">
      <div class="px-3.5">
        <div class="font-medium text-theme-title">{{ selectMessage?.title }}</div>
        <div class="mt-3 text-theme-content">
          <span class="mr-3">{{ selectMessage?.fullName }}</span>
          <span>{{ selectMessage?.sendDate }}</span>
        </div>
      </div>
      <div class="w-full mt-3 border-t border-solid border-theme-divider"></div>
      <!-- <RichBrowser class="rich-editor" :value="selectMessage?.content" /> -->
      <RichEditor :value="selectMessage?.content" mode="view" />
    </PureCard>
  </div>
</template>
<style scoped>
.msg-outer-container {
  height: 100%;
}

.item-container {
  @apply flex items-start rounded;

  padding-right: 10px;
  padding-left: 10px;
}

.show-border-b {
  border-bottom: 1px solid rgb(241, 243, 248);
}

.row-container {
  padding-top: 10px;
  padding-bottom: 10px;
}

.ant-checkbox-wrapper {
  top: 6px;
}

.item-container.active {
  background-color: rgb(241, 243, 248);
}

.item-container:not(.active):hover {
  background-color: rgba(241, 243, 248, 70%);
}

:deep(.card-body) {
  height: calc(100% - 37px);
}

.list-main {
  height: calc(100% - 87px);
  overflow: auto;
}

.rich-editor {
  height: calc(100% - 57px);
  overflow: auto;
}

</style>
