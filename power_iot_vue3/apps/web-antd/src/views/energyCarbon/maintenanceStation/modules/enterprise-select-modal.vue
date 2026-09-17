<script lang="ts" setup>
import type { MaintenanceEnterprisePageParam } from '#/api/energyCarbon/maintenanceEnterprise';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import { Button, Input, Pagination, Table } from 'ant-design-vue';

import { getMaintenanceEnterprisePage } from '#/api/energyCarbon/maintenanceEnterprise';

const emit = defineEmits<{
  confirm: [enterprise: any];
}>();

const searchEnterpriseName = ref('');

const enterpriseList = ref<any[]>([]);
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
  {
    title: '企业名称',
    dataIndex: 'enterpriseName',
    width: 160,
    ellipsis: true,
  },
  {
    title: '统一社会信用代码',
    dataIndex: 'unifiedSocialCreditCode',
    width: 180,
    ellipsis: true,
  },
  {
    title: '服务类型',
    dataIndex: 'serviceType',
    width: 90,
    ellipsis: true,
    customRender: ({ text }: { text: string }) => getDictLabel(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, text) || '-',
  },
  {
    title: '区域负责人',
    dataIndex: 'regionalManagerName',
    width: 100,
    ellipsis: true,
  },
  {
    title: '负责人电话',
    dataIndex: 'regionalManagerPhone',
    width: 120,
    ellipsis: true,
  },
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
    const params: MaintenanceEnterprisePageParam = {
      pageNo: page,
      pageSize: size,
      enterpriseName: searchEnterpriseName.value || undefined,
    };
    const result = await getMaintenanceEnterprisePage(params);
    enterpriseList.value = result?.list || [];
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
  destroyOnClose: true,
  fullscreenButton: false,
  header: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      searchEnterpriseName.value = '';
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
    <div class="enterprise-select-body">
      <!-- 搜索 -->
      <div class="search-row">
        <Input
          v-model:value="searchEnterpriseName"
          placeholder="企业名称"
          style="flex-shrink: 0; width: 200px"
          @press-enter="handleSearch"
        />
        <Button type="primary" size="small" @click="handleSearch">
          查询
        </Button>
      </div>

      <!-- 表格 -->
      <Table
        :columns="columns"
        :data-source="enterpriseList"
        :row-selection="rowSelection"
        row-key="id"
        :pagination="false"
        :loading="loading"
        size="small"
        :scroll="{ x: 650 }"
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
.enterprise-select-body {
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