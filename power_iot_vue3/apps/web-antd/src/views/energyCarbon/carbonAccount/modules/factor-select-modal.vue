<script lang="ts" setup>
import type { FactorLibPageParam } from '#/api/energyCarbon/factorLib';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { Button, Input, Pagination, Select, Table } from 'ant-design-vue';

import { getEmissionSourceList } from '#/api/energyCarbon/emissionSource';
import { getFactorLibPage } from '#/api/energyCarbon/factorLib';

const emit = defineEmits<{
  confirm: [factor: any];
}>();

const searchFactorName = ref('');
const searchEmissionSource = ref('');

const factorList = ref<any[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);

const selectedRowKeys = ref<(number | string)[]>([]);
const selectedRow = ref<any>(null);

// 排放源选项
const emissionSourceOptions = ref<any[]>([]);

// 计量单位字典
const unitOptions = getDictOptions(DICT_TYPE.ENERGY_CBON_UNIT_MEASURE, 'string');

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
  { title: '排放因子编码', dataIndex: 'factorCode', width: 130, ellipsis: true },
  { title: '排放因子名称', dataIndex: 'factorName', width: 160, ellipsis: true },
  { title: '排放源', dataIndex: 'emissionSource', width: 120, ellipsis: true },
  { title: '因子值', dataIndex: 'factorValue', width: 100 },
  { title: '计量单位', dataIndex: 'unit', width: 100 },
];

const rowSelection = computed(() => ({
  type: 'radio' as const,
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: (number | string)[], rows: any[]) => {
    selectedRowKeys.value = keys;
    selectedRow.value = rows[0] || null;
  },
}));

// 加载排放源选项
async function loadEmissionSourceOptions() {
  try {
    const result = await getEmissionSourceList();
    emissionSourceOptions.value = (result || []).map((item: any) => ({
      label: item.sourceName,
      value: item.sourceCode,
    }));
  } catch {
    emissionSourceOptions.value = [];
  }
}

// 获取单位显示文本
function getUnitLabel(unit: string) {
  const option = unitOptions.find((o: any) => o.value === unit);
  return option?.label || unit || '-';
}

// 获取排放源显示文本
function getEmissionSourceLabel(source: string) {
  const option = emissionSourceOptions.value.find((o: any) => o.value === source);
  return option?.label || source || '-';
}

async function loadData(page = 1, size = pageSize.value) {
  loading.value = true;
  try {
    const params: FactorLibPageParam = {
      pageNo: page,
      pageSize: size,
      factorName: searchFactorName.value || undefined,
      emissionSource: searchEmissionSource.value || undefined,
    };
    const result = await getFactorLibPage(params);
    factorList.value = result?.list || [];
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

function handleTableChange(page: number, size: number) {
  loadData(page, size);
}

const [Modal, modalApi] = useVbenModal({
  class: 'w-3/5',
  title: '',
  centered: true,
  destroyOnClose: true,
  fullscreenButton: false,
  header: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      searchFactorName.value = '';
      searchEmissionSource.value = '';
      selectedRowKeys.value = [];
      selectedRow.value = null;
      return;
    }
    // 加载排放源选项
    await loadEmissionSourceOptions();
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
  <Modal class="w-3/5">
    <div class="factor-select-body">
      <!-- 搜索 -->
      <div class="search-row">
        <Input
          v-model:value="searchFactorName"
          placeholder="排放因子名称"
          style="flex-shrink: 0; width: 160px"
          @press-enter="handleSearch"
        />
        <Select
          v-model:value="searchEmissionSource"
          :options="emissionSourceOptions"
          placeholder="排放源"
          allow-clear
          style="flex-shrink: 0; width: 140px"
        />
        <Button type="primary" size="small" @click="handleSearch">查询</Button>
      </div>

      <!-- 表格 -->
      <Table
        :columns="columns"
        :data-source="factorList"
        :row-selection="rowSelection"
        row-key="id"
        :pagination="false"
        :loading="loading"
        size="small"
        :scroll="{ x: 700 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'unit'">
            {{ getUnitLabel(record.unit) }}
          </template>
          <template v-if="column.dataIndex === 'emissionSource'">
            {{ getEmissionSourceLabel(record.emissionSource) }}
          </template>
        </template>
      </Table>

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
.factor-select-body {
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
