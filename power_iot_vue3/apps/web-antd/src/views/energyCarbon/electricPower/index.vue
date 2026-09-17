<script setup lang="ts">
import { computed, ref } from 'vue';

import { confirm, Page, useVbenModal } from '@vben/common-ui';
import { isEmpty } from '@vben/utils';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteElectricityDataByElectricIds,
  getElectricityPage,
  syncAllElectricityData,
  syncElectricityData,
} from '#/api/energyCarbon/electricPower';

import { useGridColumns, useGridFormSchema } from './data';
import ExportModal from './modules/export.vue';
import FormModal from './modules/form.vue';
import ImportModal from './modules/import.vue';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();
const importModalRef = ref<InstanceType<typeof ImportModal>>();

const [DeleteModalComp, deleteModalApi] = useVbenModal({
  title: '确认删除',
  destroyOnClose: true,
  async onConfirm() {
    await handleConfirmDelete();
  },
});

// 选中的行
const selectedRowKeys = ref<number[]>([]);

const toolbarActions = computed(() => [
  {
    label: '导入',
    type: 'primary' as const,
    icon: ACTION_ICON.IMPORT,
    auth: ['carbon:electricity:import'],
    onClick: handleImport,
  },
  {
    label: '导出',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:electricity:export'],
    onClick: handleExport,
  },
  {
    label: '全部同步',
    type: 'primary' as const,
    icon: ACTION_ICON.REFRESH,
    auth: ['carbon:electricity:update'],
    onClick: handleSyncAll,
  },
  {
    label: '批量删除',
    type: 'primary' as const,
    danger: selectedRowKeys.value.length > 0,
    icon: ACTION_ICON.DELETE,
    auth: ['carbon:electricity:delete'],
    disabled: isEmpty(selectedRowKeys.value),
    onClick: handleBatchDelete,
  },
]);

function getRowActions(row: any) {
  return [
    {
      label: '查看',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:electricity:query'],
      onClick: () => handleView(row),
    },
    {
      label: '同步',
      type: 'link' as const,
      icon: ACTION_ICON.REFRESH,
      auth: ['carbon:electricity:update'],
      onClick: () => handleSyncSingle(row),
    },
    {
      label: '删除',
      type: 'link' as const,
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:electricity:delete'],
      popConfirm: {
        title: '确定要删除这条数据吗？',
        confirm: handleDelete.bind(null, row),
      },
    },
  ];
}

function handleRefresh() {
  gridApi.query();
}

function handleImport() {
  importModalRef.value?.open();
}

function handleView(row: any) {
  formModalRef.value?.open(row);
}

function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const queryParams: Record<string, any> = {};
  if (formValues.username) queryParams.username = formValues.username;
  if (formValues.division?.[0]) queryParams.provinceCode = formValues.division[0];
  if (formValues.division?.[1]) queryParams.cityCode = formValues.division[1];
  if (formValues.division?.[2]) queryParams.districtCode = formValues.division[2];
  if (formValues.electricityId) queryParams.electricityId = formValues.electricityId;

  exportModalRef.value?.open({
    selectedIds: selectedRowKeys.value,
    queryParams,
  });
}

function handleRowCheckboxChange({ records }: { records: any[] }) {
  selectedRowKeys.value = records.map((item) => item.electricityId!);
}

function handleSyncSingle(row: any) {
  confirm(`确定要同步用户「${row.username}」的电力数据吗？`).then(async () => {
    const hideLoading = message.loading({
      content: '同步中...',
      duration: 0,
    });
    try {
      await syncElectricityData(row.id);
      message.success('同步成功');
      handleRefresh();
    } catch {
      message.error('同步失败');
    } finally {
      hideLoading();
    }
  });
}

function handleSyncAll() {
  confirm('确定要同步所有电力数据吗？这可能需要一些时间。').then(async () => {
    const hideLoading = message.loading({
      content: '同步中...',
      duration: 0,
    });
    try {
      await syncAllElectricityData();
      message.success('全部同步成功');
      handleRefresh();
    } catch {
      message.error('同步失败');
    } finally {
      hideLoading();
    }
  });
}

/** 删除单条 */
async function handleDelete(row: any) {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await deleteElectricityDataByElectricIds([row.electricityId]);
    message.success('删除成功');
    handleRefresh();
  } catch {
    message.error('删除失败');
  } finally {
    hideLoading();
  }
}

/** 批量删除 */
function handleBatchDelete() {
  deleteModalApi.open();
}

/** 确认批量删除 */
async function handleConfirmDelete() {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await deleteElectricityDataByElectricIds(selectedRowKeys.value);
    message.success(`成功删除 ${selectedRowKeys.value.length} 条数据`);
    selectedRowKeys.value = [];
    handleRefresh();
  } catch {
    message.error('批量删除失败');
  } finally {
    hideLoading();
  }
  deleteModalApi.close();
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async (
          { page }: { page: { currentPage: number; pageSize: number } },
          formValues: Record<string, any>,
        ) => {
          const params = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            username: formValues.username,
            provinceCode: formValues.division?.[0],
            cityCode: formValues.division?.[1],
            districtCode: formValues.division?.[2],
            electricityId: formValues.electricityId,
          };

          const result = await getElectricityPage(params);
          return {
            list: result?.list || [],
            total: result?.total || 0,
          };
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
    pagerConfig: {
      pageSize: 20,
      pageSizes: [20, 50, 100],
    },
    emptyText: '暂无电力数据',
  },
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />
    <ExportModal ref="exportModalRef" @success="handleRefresh" />
    <ImportModal ref="importModalRef" @success="handleRefresh" />
    <DeleteModalComp>
      <p>确定要删除选中的 {{ selectedRowKeys.length }} 条数据吗？</p>
    </DeleteModalComp>

    <Grid table-title="电力数据列表">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>

      <template #actions="{ row }">
        <TableAction :actions="getRowActions(row)" />
      </template>
    </Grid>
  </Page>
</template>
