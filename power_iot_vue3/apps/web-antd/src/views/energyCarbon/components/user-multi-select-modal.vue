<script lang="ts" setup>
import type { UserInfoListParam } from '#/api/energyCarbon/basicData';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel, getDictOptions } from '@vben/hooks';

import { Button, DatePicker, Input, Pagination, Select, Table } from 'ant-design-vue';

import LazyAreaCascader from '#/components/lazy-area-cascader/lazy-area-cascader.vue';

const props = defineProps<{
  /** 获取用户列表的 API 函数 */
  api: (params: UserInfoListParam) => Promise<any>;
  /** 默认行政区过滤（市级编码） */
  cityCode?: number | string;
  /** 默认行政区过滤（区级编码） */
  districtCode?: number | string;
  /** 是否锁定行政区选择，禁止手动修改 */
  lockArea?: boolean;
  /** 默认行政区过滤（省级编码） */
  provinceCode?: number | string;
}>();

const emit = defineEmits<{
  confirm: [users: any[]];
}>();

// 搜索条件
const searchUsername = ref('');
const searchIdCard = ref('');
const searchArea = ref<(number | string)[]>([]);
const searchReformType = ref<string | undefined>(undefined);
const searchAccountingYear = ref<string | undefined>(undefined);

// 改造类型字典选项
const reformTypeOptions = computed(() =>
  getDictOptions(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE),
);

// 获取改造类型字典标签
function getReformTypeLabel(value?: string) {
  if (!value) return '-';
  return getDictLabel(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, value) || value;
}

const userList = ref<any[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);

// 多选相关
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
  { title: '用户名', dataIndex: 'username', width: 80, ellipsis: true },
  { title: '身份证号', dataIndex: 'idCard', width: 150, ellipsis: true },
  { title: '联系电话', dataIndex: 'phone', width: 110, ellipsis: true },
  { title: '行政区划', dataIndex: 'division', width: 150, ellipsis: true },
  { title: '地址', dataIndex: 'address', width: 120, ellipsis: true },
  { title: '改造类别', dataIndex: 'reformType', width: 100, ellipsis: true, customRender: ({ text }: any) => getReformTypeLabel(text) },
  {
    title: '核算周期',
    dataIndex: 'accountingPeriodStart',
    width: 180,
    ellipsis: true,
    customRender: ({ record }: any) =>
      record.accountingPeriodStart
        ? `${record.accountingPeriodStart} ~ ${record.accountingPeriodEnd || ''}`
        : '-',
  },
  {
    title: '基准线排放量',
    dataIndex: 'baselineEmission',
    width: 110,
    ellipsis: true,
  },
  { title: '实际排放量', dataIndex: 'actualEmission', width: 110, ellipsis: true },
  { title: '减排量', dataIndex: 'reduction', width: 100, ellipsis: true },
  { title: '电力表', dataIndex: 'electricityId', width: 100, ellipsis: true },
  { title: '燃气表', dataIndex: 'gasId', width: 100, ellipsis: true },
];

// 多选配置
const rowSelection = computed(() => ({
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
}));

async function loadData(page = 1, size = pageSize.value) {
  loading.value = true;
  try {
    const params: UserInfoListParam = {
      pageNo: page,
      pageSize: size,
      username: searchUsername.value || undefined,
      idCard: searchIdCard.value || undefined,
      reformType: searchReformType.value || undefined,
      provinceCode: searchArea.value?.[0] || undefined,
      cityCode: searchArea.value?.[1] || undefined,
      districtCode: searchArea.value?.[2] || undefined,
      townCode: searchArea.value?.[3] || undefined,
      villageCode: searchArea.value?.[4] || undefined,
      accountingPeriodStart: searchAccountingYear.value
        ? `${searchAccountingYear.value}-11-15`
        : undefined,
      accountingPeriodEnd: searchAccountingYear.value
        ? `${Number(searchAccountingYear.value) + 1}-03-15`
        : undefined,
    };
    const result = await props.api(params);
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
  searchAccountingYear.value = undefined;
  loadData(1);
}

function handleTableChange(p: any) {
  loadData(p.current, p.pageSize);
}

const [Modal, modalApi] = useVbenModal({
  class: 'w-[1100px]',
  title: '选择用户',
  centered: true,
  destroyOnClose: true,
  fullscreenButton: false,
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      searchUsername.value = '';
      searchIdCard.value = '';
      searchArea.value = [];
      searchReformType.value = undefined;
      searchAccountingYear.value = undefined;
      selectedRowKeys.value = [];
      selectedRows.value = [];
      return;
    }
    // 如果外部传入了行政区过滤，则默认填充并锁定
    const defaultArea = [
      props.provinceCode,
      props.cityCode,
      props.districtCode,
    ].filter((v) => v !== undefined && v !== null && v !== '');
    searchArea.value = defaultArea as (number | string)[];
    loadData();
  },
});

function handleConfirm() {
  if (selectedRows.value.length === 0) {
    return;
  }
  emit('confirm', [...selectedRows.value]);
  modalApi.close();
}

function open() {
  selectedRowKeys.value = [];
  selectedRows.value = [];
  modalApi.open();
}

defineExpose({
  open,
});
</script>

<template>
  <Modal class="w-[1100px]">
    <div class="user-multi-select-body">
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
          :disabled="lockArea"
          style="flex-shrink: 0; width: 200px"
        />
        <Select
          v-model:value="searchReformType"
          :options="reformTypeOptions"
          placeholder="改造类别"
          style="flex-shrink: 0; width: 120px"
          allow-clear
        />
        <div style="display: flex; align-items: center; gap: 4px;">
          <DatePicker
            v-model:value="searchAccountingYear"
            picker="year"
            value-format="YYYY"
            placeholder="核算年份"
            style="flex-shrink: 0; width: 130px"
          />
          <span
            v-if="searchAccountingYear"
            style="color: #666; white-space: nowrap; font-size: 12px;"
          >
            {{ searchAccountingYear }}-11-15 ~ {{ Number(searchAccountingYear) + 1 }}-03-15
          </span>
        </div>
        <Button type="primary" size="small" @click="handleSearch">查询</Button>
        <Button size="small" @click="handleReset">重置</Button>
      </div>

      <!-- 已选提示 -->
      <div v-if="selectedRows.length > 0" class="selected-tip">
        已选择 <span class="count">{{ selectedRows.length }}</span> 个用户
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
        确定（{{ selectedRows.length }}）
      </Button>
    </template>
  </Modal>
</template>

<style scoped>
.user-multi-select-body {
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
