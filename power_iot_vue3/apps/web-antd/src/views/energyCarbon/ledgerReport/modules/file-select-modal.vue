<script lang="ts" setup>
import type { LedgerFilePageParam } from '#/api/energyCarbon/ledgerFile';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import { Button, Input, Pagination, Table } from 'ant-design-vue';

import { getLedgerFilePage } from '#/api/energyCarbon/ledgerFile';

const emit = defineEmits<{
  confirm: [files: any[]];
}>();

const searchFileName = ref('');
const fileList = ref<any[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const reportType = ref<string>();
const districtCode = ref<string>();

const selectedRowKeys = ref<(number | string)[]>([]);
const selectedRows = ref<any[]>([]);

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
  { title: '上传类型', dataIndex: 'uploadType', width: 120, ellipsis: true },
  { title: '文件名称', dataIndex: 'fileName', width: 200, ellipsis: true },
  { title: '数据量', dataIndex: 'dataCount', width: 100 },
  {
    title: '上传状态',
    dataIndex: 'uploadStatus',
    width: 100,
    customRender: ({ text }: any) =>
      getDictLabel(DICT_TYPE.ENERGY_CBON_SUCCESS_STATUS, text),
  },
  {
    title: '上传时间',
    dataIndex: 'uploadTime',
    width: 160,
    ellipsis: true,
  },
];

const rowSelection = computed(() => ({
  type: 'checkbox' as const,
  selectedRowKeys: selectedRowKeys.value,
  preserveSelectedRowKeys: true,
  onChange: (keys: (number | string)[], rows: any[]) => {
    selectedRowKeys.value = keys;
    selectedRows.value = rows;
  },
}));

async function loadData(page = 1, size = pageSize.value) {
  loading.value = true;
  try {
    const params: LedgerFilePageParam = {
      pageNo: page,
      pageSize: size,
      districtCode: districtCode.value,
      uploadType: reportType.value,
      uploadStatus: '2',
      fileName: searchFileName.value || undefined,
    };
    const result = await getLedgerFilePage(params);
    fileList.value = result?.list || [];
    total.value = result?.total || 0;
    currentPage.value = page;
    pageSize.value = size;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  loadData(1);
}

function handleTableChange(page: number, size: number) {
  loadData(page, size);
}

const [Modal, modalApi] = useVbenModal({
  class: 'w-2/3',
  title: '选择台账文件',
  centered: true,
  destroyOnClose: true,
  fullscreenButton: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      searchFileName.value = '';
      selectedRowKeys.value = [];
      selectedRows.value = [];
      return;
    }
    loadData();
  },
});

function handleConfirm() {
  if (selectedRows.value.length === 0) {
    return;
  }
  emit(
    'confirm',
    selectedRows.value.map((item) => ({
      fileId: item.id,
      fileName: item.fileName,
      fileUrl: item.fileUrl,
      uploadType: item.uploadType,
      dataCount: item.dataCount,
    })),
  );
  modalApi.close();
}

function open(params?: { districtCode?: string; reportType?: string }) {
  reportType.value = params?.reportType;
  districtCode.value = params?.districtCode;
  modalApi.open();
}

defineExpose({ open });
</script>

<template>
  <Modal>
    <div class="file-select-body">
      <div class="search-row">
        <Input
          v-model:value="searchFileName"
          placeholder="文件名称"
          style="width: 200px"
          @press-enter="handleSearch"
        />
        <Button type="primary" size="small" @click="handleSearch">查询</Button>
      </div>

      <Table
        :columns="columns"
        :data-source="fileList"
        :row-selection="rowSelection"
        row-key="id"
        :pagination="false"
        :loading="loading"
        size="small"
        :scroll="{ x: 700 }"
      />

      <div class="pagination-row">
        <Pagination v-bind="pagination" @change="handleTableChange" />
      </div>
    </div>

    <template #footer>
      <Button size="small" @click="modalApi.close()">取消</Button>
      <Button
        type="primary"
        size="small"
        :disabled="selectedRows.length === 0"
        @click="handleConfirm"
      >
        确定
      </Button>
    </template>
  </Modal>
</template>

<style scoped>
.file-select-body {
  padding: 8px 10px;
}

.search-row {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 8px;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>
