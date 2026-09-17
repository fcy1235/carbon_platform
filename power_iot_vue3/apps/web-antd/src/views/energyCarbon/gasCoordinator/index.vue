<script setup lang="ts">
import type { GasCoordinatorPageParam } from '#/api/energyCarbon/gasCoordinator';

import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteGasCoordinator,
  getGasCoordinatorPage,
} from '#/api/energyCarbon/gasCoordinator';

import { useGridColumns, useGridFormSchema } from './data';
import ExportModal from './modules/export.vue';
import FormModal from './modules/form.vue';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();

// 选中的行
const selectedRowKeys = ref<number[]>([]);

const toolbarActions = [
  {
    label: '新增',
    type: 'primary' as const,
    icon: ACTION_ICON.ADD,
    auth: ['carbon:gas-coordinator:create'],
    onClick: handleCreate,
  },
  {
    label: '导出',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:gas-coordinator:export'],
    onClick: handleExport,
  },
];

function getRowActions(row: any) {
  return [
    {
      label: '查看',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:gas-coordinator:query'],
      onClick: () => handleView(row),
    },
    {
      label: '编辑',
      type: 'link' as const,
      icon: ACTION_ICON.EDIT,
      auth: ['carbon:gas-coordinator:update'],
      onClick: () => handleEdit(row),
    },
    {
      label: '删除',
      type: 'link' as const,
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:gas-coordinator:delete'],
      popConfirm: {
        title: '确定要删除该协管员吗？删除后数据不可恢复，请谨慎操作！',
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
    await deleteGasCoordinator(row.id);
    message.success('删除成功');
    handleRefresh();
  } catch {
    message.error('删除失败');
  } finally {
    hideLoading();
  }
}

function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const queryParams: Record<string, any> = {};
  if (formValues.phone) queryParams.phone = formValues.phone;
  if (formValues.staffStatus) queryParams.staffStatus = formValues.staffStatus;
  if (formValues.isVillageCommitteeMember) queryParams.isVillageCommitteeMember = formValues.isVillageCommitteeMember;
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
    wrapperClass: 'grid-cols-6',
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
          const params: GasCoordinatorPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            phone: formValues.phone,
            staffStatus: formValues.staffStatus,
            isVillageCommitteeMember: formValues.isVillageCommitteeMember,
            areaCode: formValues.areaCode?.length > 0 ? formValues.areaCode.join(',') : undefined,
          };

          const result = await getGasCoordinatorPage(params);
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
    emptyText: '暂无协管员信息，请点击新增按钮添加',
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

    <Grid table-title="农村气代煤协管员列表">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>

      <template #actions="{ row }">
        <TableAction :actions="getRowActions(row)" />
      </template>
    </Grid>
  </Page>
</template>
