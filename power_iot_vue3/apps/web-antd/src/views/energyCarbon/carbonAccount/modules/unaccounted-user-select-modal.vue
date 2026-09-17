<script lang="ts" setup>
import type { UnaccountedUserPageParam } from '#/api/energyCarbon/carbonAccount';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel, getDictOptions } from '@vben/hooks';

import { Button, Input, Pagination, Select, Table } from 'ant-design-vue';

import { getUnaccountedUserPage } from '#/api/energyCarbon/carbonAccount';
import LazyAreaCascader from '#/components/lazy-area-cascader/lazy-area-cascader.vue';

const props = defineProps<{
  accountingPeriodStart: string;
  accountingPeriodEnd: string;
  /** 是否多选模式，默认 true */
  multiple?: boolean;
}>();

const emit = defineEmits<{
  confirm: [users: any[]];
}>();

// 搜索条件
const searchUsername = ref('');
const searchIdCard = ref('');
const searchArea = ref<(number | string)[]>([]);
const searchReformType = ref<string | undefined>(undefined);

// 改造类型字典选项
const reformTypeOptions = computed(() =>
  getDictOptions(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE),
);

function getReformTypeLabel(value?: string) {
  if (!value) return '-';
  return getDictLabel(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, value) || value;
}

const userList = ref<any[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);

const isMultiple = computed(() => props.multiple !== false);

// 多选
const selectedRowKeys = ref<(number | string)[]>([]);
const selectedRows = ref<any[]>([]);
// 单选
const selectedRowKey = ref<number | string | null>(null);
const selectedRowData = ref<any>(null);

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
  { title: '用户名', dataIndex: 'username', width: 90, ellipsis: true },
  { title: '身份证号', dataIndex: 'idCard', width: 150, ellipsis: true },
  { title: '联系电话', dataIndex: 'phone', width: 110, ellipsis: true },
  { title: '行政区划', dataIndex: 'division', width: 160, ellipsis: true },
  { title: '地址', dataIndex: 'address', width: 130, ellipsis: true },
  {
    title: '改造类别',
    dataIndex: 'reformType',
    width: 90,
    ellipsis: true,
    customRender: ({ text }: any) => getReformTypeLabel(text),
  },
  {
    title: '清洁建筑取暖面积(㎡)',
    dataIndex: 'heatingArea',
    width: 140,
    ellipsis: true,
  },
  {
    title: '基准线排放强度',
    dataIndex: 'baselineIntensity',
    width: 120,
    ellipsis: true,
  },
  {
    title: '基准线排放量(kgCO2e)',
    dataIndex: 'baselineEmission',
    width: 150,
    ellipsis: true,
  },
];

// 行选择配置（根据 multiple 切换单选/多选）
const rowSelection = computed(() => {
  if (isMultiple.value) {
    return {
      type: 'checkbox' as const,
      selectedRowKeys: selectedRowKeys.value,
      preserveSelectedRowKeys: true,
      onChange: (keys: (number | string)[]) => {
        selectedRowKeys.value = keys;
      },
      onSelect: (record: any, selected: boolean) => {
        if (selected) {
          if (!selectedRows.value.some((r) => r.id === record.id)) {
            selectedRows.value.push(record);
          }
        } else {
          selectedRows.value = selectedRows.value.filter(
            (r) => r.id !== record.id,
          );
        }
      },
      onSelectAll: (selected: boolean, _changeRows: any[], allRows: any[]) => {
        if (selected) {
          for (const row of allRows) {
            if (!selectedRows.value.some((r) => r.id === row.id)) {
              selectedRows.value.push(row);
            }
          }
        } else {
          const allRowIds = new Set(allRows.map((r) => r.id));
          selectedRows.value = selectedRows.value.filter(
            (r) => !allRowIds.has(r.id),
          );
        }
      },
    };
  }
  // 单选模式
  return {
    type: 'radio' as const,
    selectedRowKeys: selectedRowKey.value != null ? [selectedRowKey.value] : [],
    onChange: (keys: (number | string)[], rows: any[]) => {
      selectedRowKey.value = keys[0] ?? null;
      selectedRowData.value = rows[0] ?? null;
    },
  };
});

// 当前选中数量（用于模板显示）
const selectedCount = computed(() =>
  isMultiple.value ? selectedRows.value.length : (selectedRowKey.value != null ? 1 : 0),
);

async function loadData(page = 1, size = pageSize.value) {
  loading.value = true;
  try {
    const params: UnaccountedUserPageParam = {
      pageNo: page,
      pageSize: size,
      username: searchUsername.value || undefined,
      idCard: searchIdCard.value || undefined,
      reformType: searchReformType.value || undefined,
      provinceCode: searchArea.value?.[0] ? String(searchArea.value[0]) : undefined,
      cityCode: searchArea.value?.[1] ? String(searchArea.value[1]) : undefined,
      districtCode: searchArea.value?.[2] ? String(searchArea.value[2]) : undefined,
      accountingPeriodStart: props.accountingPeriodStart,
      accountingPeriodEnd: props.accountingPeriodEnd,
    };
    const result = await getUnaccountedUserPage(params);
    userList.value = result?.list || [];
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

function handleReset() {
  searchUsername.value = '';
  searchIdCard.value = '';
  searchArea.value = [];
  searchReformType.value = undefined;
  loadData(1);
}

function handlePageChange(page: number, pageSize: number) {
  loadData(page, pageSize);
}

const [Modal, modalApi] = useVbenModal({
  class: 'w-[1100px]',
  title: '选择未核算用户',
  centered: true,
  destroyOnClose: true,
  fullscreenButton: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      searchUsername.value = '';
      searchIdCard.value = '';
      searchArea.value = [];
      searchReformType.value = undefined;
      selectedRowKeys.value = [];
      selectedRows.value = [];
      selectedRowKey.value = null;
      selectedRowData.value = null;
      return;
    }
    loadData();
  },
});

function handleConfirm() {
  if (isMultiple.value) {
    if (selectedRows.value.length === 0) return;
    emit('confirm', [...selectedRows.value]);
  } else {
    if (!selectedRowData.value) return;
    emit('confirm', [selectedRowData.value]);
  }
  modalApi.close();
}

function open() {
  selectedRowKeys.value = [];
  selectedRows.value = [];
  selectedRowKey.value = null;
  selectedRowData.value = null;
  modalApi.open();
}

defineExpose({
  open,
});
</script>

<template>
  <Modal class="w-[1100px]">
    <div class="unaccounted-select-body">
      <!-- 搜索 -->
      <div class="search-row">
        <Input
          v-model:value="searchUsername"
          placeholder="用户名"
          style="flex-shrink: 0; width: 120px"
          @press-enter="handleSearch"
        />
        <Input
          v-model:value="searchIdCard"
          placeholder="身份证号"
          style="flex-shrink: 0; width: 150px"
          @press-enter="handleSearch"
        />
        <LazyAreaCascader
          v-model="searchArea"
          placeholder="行政区划"
          style="flex-shrink: 0; width: 200px"
        />
        <Select
          v-model:value="searchReformType"
          :options="reformTypeOptions as any"
          placeholder="改造类别"
          style="flex-shrink: 0; width: 120px"
          allow-clear
        />
        <Button type="primary" size="small" @click="handleSearch">查询</Button>
        <Button size="small" @click="handleReset">重置</Button>
      </div>

      <!-- 核算周期提示 -->
      <div class="period-tip">
        核算周期：{{ props.accountingPeriodStart }} ~ {{ props.accountingPeriodEnd }}
      </div>

      <!-- 已选提示 -->
      <div v-if="selectedCount > 0" class="selected-tip">
        已选择 <span class="count">{{ selectedCount }}</span> 个用户
      </div>

      <!-- 表格 -->
      <Table
        :columns="columns"
        :data-source="userList"
        :row-selection="rowSelection"
        row-key="id"
        :pagination="false"
        :loading="loading"
        size="small"
        :scroll="{ x: 1200, y: 300 }"
      />

      <!-- 分页 -->
      <div class="pagination-row">
        <Pagination
          v-bind="pagination"
          @change="handlePageChange"
          @showSizeChange="handlePageChange"
        />
      </div>
    </div>

    <template #footer>
      <Button size="small" @click="modalApi.close()">取消</Button>
      <Button
        type="primary"
        size="small"
        :disabled="selectedCount === 0"
        @click="handleConfirm"
      >
        确定（{{ selectedCount }}）
      </Button>
    </template>
  </Modal>
</template>

<style scoped>
.unaccounted-select-body {
  padding: 8px 10px;
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  margin-bottom: 8px;
}

.search-row :deep(.ant-input) {
  font-size: 12px;
}

.period-tip {
  margin-bottom: 8px;
  font-size: 12px;
  color: #1890ff;
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
  padding: 4px 10px;
}

.selected-tip {
  padding: 6px 12px;
  margin-bottom: 8px;
  font-size: 12px;
  color: #1890ff;
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 4px;
}

.selected-tip .count {
  font-weight: 600;
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