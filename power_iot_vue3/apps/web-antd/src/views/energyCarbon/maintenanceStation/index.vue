<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />
    <ExportModal ref="exportModalRef" @success="handleRefresh" />

    <Grid table-title="维保网点列表">
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
import { ref } from 'vue';

import { Page } from '@vben/common-ui';
import { message } from 'ant-design-vue';
import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';

import FormModal from './modules/form.vue';
import ExportModal from './modules/export.vue';
import { useGridColumns, useGridFormSchema } from './data';
import {
  getMaintenanceStationPage,
  deleteMaintenanceStation,
} from '#/api/energyCarbon/maintenanceStation';
import type { MaintenanceStationPageParam } from '#/api/energyCarbon/maintenanceStation';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();

// 选中的行
const selectedRowKeys = ref<number[]>([]);

const toolbarActions = [
  {
    label: '新增',
    type: 'primary' as const,
    icon: ACTION_ICON.ADD,
    auth: ['carbon:maintenance-station:create'],
    onClick: handleCreate,
  },
  {
    label: '导出',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:maintenance-station:export'],
    onClick: handleExport,
  },
];

function getRowActions(row: any) {
  return [
    {
      label: '查看',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:maintenance-station:query'],
      onClick: () => handleView(row),
    },
    {
      label: '编辑',
      type: 'link' as const,
      icon: ACTION_ICON.EDIT,
      auth: ['carbon:maintenance-station:update'],
      onClick: () => handleEdit(row),
    },
    {
      label: '删除',
      type: 'link' as const,
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:maintenance-station:delete'],
      popConfirm: {
        title: '确定要删除该维保网点吗？删除后数据不可恢复，请谨慎操作！',
        confirm: () => handleDelete(row),
      },
    },
  ];
}

function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalRef.value?.open({ viewMode: false });
}

function handleView(row: any) {
  formModalRef.value?.open({ ...row, viewMode: true });
}

function handleEdit(row: any) {
  formModalRef.value?.open({ ...row, viewMode: false });
}

async function handleDelete(row: any) {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await deleteMaintenanceStation(row.id);
    message.success('删除成功');
    handleRefresh();
  } catch (error) {
    message.error('删除失败');
  } finally {
    hideLoading();
  }
}

function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const queryParams: Record<string, any> = {};
  if (formValues.stationName) queryParams.stationName = formValues.stationName;
  if (formValues.serviceType) queryParams.serviceType = formValues.serviceType;
  if (formValues.businessStatus) queryParams.businessStatus = formValues.businessStatus;

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
    commonConfig: {
      labelWidth: 120,
    },
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
          const params: MaintenanceStationPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            stationName: formValues.stationName,
            serviceType: formValues.serviceType,
            businessStatus: formValues.businessStatus,
          };

          const result = await getMaintenanceStationPage(params);
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
    emptyText: '暂无维保网点信息，请点击新增按钮添加',
  },
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>