<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />
    <ExportModal ref="exportModalRef" @success="handleRefresh" />

    <Grid table-title="燃气安全员列表">
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
  getGasSafetyOfficerPage,
  deleteGasSafetyOfficer,
} from '#/api/energyCarbon/gasSafetyOfficer';
import type { GasSafetyOfficerPageParam } from '#/api/energyCarbon/gasSafetyOfficer';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();

// 选中的行
const selectedRowKeys = ref<number[]>([]);

const toolbarActions = [
  {
    label: '新增',
    type: 'primary' as const,
    icon: ACTION_ICON.ADD,
    auth: ['carbon:gas-safety-officer:create'],
    onClick: handleCreate,
  },
  {
    label: '导出',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:gas-safety-officer:export'],
    onClick: handleExport,
  },
];

function getRowActions(row: any) {
  return [
    {
      label: '查看',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:gas-safety-officer:query'],
      onClick: () => handleView(row),
    },
    {
      label: '编辑',
      type: 'link' as const,
      icon: ACTION_ICON.EDIT,
      auth: ['carbon:gas-safety-officer:update'],
      onClick: () => handleEdit(row),
    },
    {
      label: '删除',
      type: 'link' as const,
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:gas-safety-officer:delete'],
      popConfirm: {
        title: '确定要删除该燃气安全员吗？删除后数据不可恢复，请谨慎操作！',
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
    await deleteGasSafetyOfficer(row.id);
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
  if (formValues.name) queryParams.name = formValues.name;
  if (formValues.qualificationNo) queryParams.qualificationNo = formValues.qualificationNo;
  if (formValues.staffStatus) queryParams.staffStatus = formValues.staffStatus;
  if (formValues.areaCode?.length > 0) queryParams.areaCode = formValues.areaCode.join(',');

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
    compact: true,
    wrapperClass: 'grid-cols-5',
    commonConfig: {
      labelWidth: 70,
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
          const params: GasSafetyOfficerPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            name: formValues.name,
            qualificationNo: formValues.qualificationNo,
            staffStatus: formValues.staffStatus,
            areaCode: formValues.areaCode?.length > 0 ? formValues.areaCode.join(',') : undefined,
          };

          const result = await getGasSafetyOfficerPage(params);
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
    emptyText: '暂无燃气安全员信息，请点击新增按钮添加',
  },
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>
