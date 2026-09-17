<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />
    <ImportModal ref="importModalRef" @success="handleRefresh" />
    <ExportModal ref="exportModalRef" @success="handleRefresh" />

    <Grid table-title="气体信息列表">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>

      <template #actions="{ row }">
        <TableAction :actions="getRowActions(row)" />
      </template>
    </Grid>
  </Page>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

import { Page } from '@vben/common-ui';
import { message } from 'ant-design-vue';
import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';

import FormModal from './modules/form.vue';
import ImportModal from './modules/import.vue';
import ExportModal from './modules/export.vue';
import { useGridColumns, useGridFormSchema } from './data';
import { getGasInfoPage, deleteGasInfo } from '#/api/energyCarbon/gasInfo';
import type { GasInfoPageParam } from '#/api/energyCarbon/gasInfo';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const importModalRef = ref<InstanceType<typeof ImportModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();

// 选中的行
const selectedRowKeys = ref<number[]>([]);

// 表格操作按钮
const toolbarActions = computed(() => [
  {
    label: '新增',
    type: 'primary' as const,
    icon: ACTION_ICON.ADD,
    auth: ['carbon:gas-info:create'],
    onClick: handleCreate,
  },
  {
    label: '导入',
    type: 'primary' as const,
    icon: ACTION_ICON.UPLOAD,
    auth: ['carbon:gas-info:import'],
    onClick: handleImport,
  },
  {
    label: '导出',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:gas-info:export'],
    onClick: handleExport,
  },
]);

// 表格行内操作按钮
function getRowActions(row: any) {
  return [
    {
      label: '查看',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:gas-info:query'],
      onClick: handleView.bind(null, row),
    },
    {
      label: '编辑',
      type: 'link' as const,
      icon: ACTION_ICON.EDIT,
      auth: ['carbon:gas-info:update'],
      onClick: handleEdit.bind(null, row),
    },
    {
      label: '删除',
      type: 'link' as const,
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:gas-info:delete'],
      popConfirm: {
        title: '确定要删除该气体信息吗？删除后数据不可恢复，请谨慎操作！',
        confirm: handleDelete.bind(null, row),
      },
    },
  ];
}

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 创建 */
function handleCreate() {
  formModalRef.value?.open({ viewMode: false });
}

/** 编辑 */
function handleEdit(row: any) {
  formModalRef.value?.open({ ...row, viewMode: false });
}

/** 查看 */
function handleView(row: any) {
  formModalRef.value?.open({ ...row, viewMode: true });
}

/** 删除 */
async function handleDelete(row: any) {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await deleteGasInfo(row.id);
    message.success('删除成功');
    handleRefresh();
  } catch (error) {
    message.error('删除失败');
  } finally {
    hideLoading();
  }
}

/** 导入 */
function handleImport() {
  importModalRef.value?.open();
}

/** 导出 */
function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const queryParams: Record<string, any> = {};
  if (formValues.gasName) queryParams.gasName = formValues.gasName;
  if (formValues.category) queryParams.category = formValues.category;

  exportModalRef.value?.open({
    selectedIds: selectedRowKeys.value,
    queryParams,
  });
}

function handleRowCheckboxChange({ records }: { records: any[] }) {
  selectedRowKeys.value = records.map((item) => item.id!);
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
          const params: GasInfoPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            gasName: formValues.gasName,
            category: formValues.category,
          };

          const result = await getGasInfoPage(params);
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
    emptyText: '暂无气体信息，请点击新增按钮添加',
  },
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>
