<script lang="ts" setup>
import type { ContactPageParam } from '#/api/energyCarbon/contact';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, Input, Pagination, Table } from 'ant-design-vue';

import { getContactPage } from '#/api/energyCarbon/contact';

const emit = defineEmits<{
  confirm: [contact: any];
}>();

const searchName = ref('');
const searchPhone = ref('');
const searchCompany = ref('');

const contactList = ref<any[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);

const selectedRowKeys = ref<(number | string)[]>([]);
const selectedRow = ref<any>(null);

const pagination = computed(() => ({
  current: currentPage.value,
  pageSize: pageSize.value,
  total: total.value,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50'],
  showTotal: (t: number) => `共 ${t} 条`,
  size: 'small' as const,
}));

const columns = [
  { title: '姓名', dataIndex: 'name', width: 80, ellipsis: true },
  { title: '联系电话', dataIndex: 'phone', width: 110, ellipsis: true },
  { title: '邮箱', dataIndex: 'email', width: 150, ellipsis: true },
  { title: '所属公司', dataIndex: 'company', width: 120, ellipsis: true },
  { title: '职位', dataIndex: 'position', width: 100, ellipsis: true },
];

const rowSelection = computed(() => ({
  type: 'radio' as const,
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: (number | string)[], rows: any[]) => {
    selectedRowKeys.value = keys;
    selectedRow.value = rows[0] || null;
  },
}));

async function loadData(page = 1, size = pageSize.value) {
  loading.value = true;
  try {
    const params: ContactPageParam = {
      pageNo: page,
      pageSize: size,
      name: searchName.value || undefined,
      phone: searchPhone.value || undefined,
      company: searchCompany.value || undefined,
    };
    const result = await getContactPage(params);
    contactList.value = result?.list || [];
    total.value = result?.total || 0;
    currentPage.value = page;
    pageSize.value = size;
    selectedRowKeys.value = [];
    selectedRow.value = null;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  loadData(1);
}

function handleTableChange(p: any) {
  loadData(p.current, p.pageSize);
}

const [Modal, modalApi] = useVbenModal({
  class: 'w-1/2',
  title: '',
  centered: true,
  destroyOnClose: false,
  fullscreenButton: false,
  header: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      searchName.value = '';
      searchPhone.value = '';
      searchCompany.value = '';
      selectedRowKeys.value = [];
      selectedRow.value = null;
      return;
    }
    loadData();
  },
});

function handleConfirm() {
  if (!selectedRow.value) {
    return;
  }
  emit('confirm', selectedRow.value);
  modalApi.close();
}

function open() {
  modalApi.open();
}

defineExpose({
  open,
});
</script>

<template>
  <Modal class="w-1/2">
    <div class="contact-select-body">
      <!-- 搜索 -->
      <div class="search-row">
        <Input
          v-model:value="searchName"
          placeholder="姓名"
          style="flex-shrink: 0; width: 120px"
          @press-enter="handleSearch"
        />
        <Input
          v-model:value="searchPhone"
          placeholder="联系电话"
          style="flex-shrink: 0; width: 120px"
          @press-enter="handleSearch"
        />
        <Input
          v-model:value="searchCompany"
          placeholder="所属公司"
          style="flex-shrink: 0; width: 120px"
          @press-enter="handleSearch"
        />
        <Button type="primary" size="small" @click="handleSearch">查询</Button>
      </div>

      <!-- 表格 -->
      <Table
        :columns="columns"
        :data-source="contactList"
        :row-selection="rowSelection"
        row-key="id"
        :pagination="false"
        :loading="loading"
        size="small"
        :scroll="{ x: 600 }"
      />

      <!-- 分页 -->
      <div class="pagination-row">
        <Pagination v-bind="pagination" @change="handleTableChange" />
      </div>
    </div>

    <template #footer>
      <Button size="small" @click="modalApi.close()">取消</Button>
      <Button
        type="primary"
        size="small"
        :disabled="!selectedRow"
        @click="handleConfirm"
      >
        确定
      </Button>
    </template>
  </Modal>
</template>

<style scoped>
.contact-select-body {
  padding: 8px 10px;
}

.search-row {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 8px;
}

.search-row :deep(.ant-input) {
  font-size: 12px;
}

:deep(.ant-table) {
  font-size: 12px;
}

:deep(.ant-table-thead > tr > th) {
  padding: 5px 8px !important;
  font-weight: 500;
  background-color: #f8f9fa;
}

:deep(.ant-table-tbody > tr > td) {
  padding: 4px 8px !important;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background-color: #f0f7ff;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 6px;
}
</style>
