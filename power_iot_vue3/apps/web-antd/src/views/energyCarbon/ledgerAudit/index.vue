<script setup lang="ts">
import {type LedgerFilePageParam, updateAuditStatus} from '#/api/energyCarbon/ledgerFile';

import { computed, ref } from 'vue';

import { Page } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteLedgerFile,
  exportLedgerFile,
  getLedgerFilePage,
} from '#/api/energyCarbon/ledgerFile';

import FormModal from '../components/ledger-file-form-modal.vue';
import { useGridColumns, useGridFormSchema } from './data';

const formModalRef = ref<InstanceType<typeof FormModal>>();

const toolbarActions = computed(() => [
  {
    label: '新增',
    type: 'primary' as const,
    icon: ACTION_ICON.ADD,
    auth: ['carbon:ledger-file:create'],
    onClick: handleCreate,
  },
  {
    label: '导出',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:ledger-file:export'],
    onClick: handleExport,
  },
]);
function getRowActions(row: any) {
  const actions: any[] = [
    {
      label: '查看',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:ledger-file:query'],
      onClick: handleView.bind(null, row),
    },
  ];
  if (row.auditStatus === '0' || row.auditStatus === '4') {
    actions.push(
      {
        label: '编辑',
        type: 'link' as const,
        icon: ACTION_ICON.EDIT,
        auth: ['carbon:ledger-file:update'],
        onClick: handleEdit.bind(null, row),
      },
      {
        label: '删除',
        type: 'link' as const,
        danger: true,
        icon: ACTION_ICON.DELETE,
        auth: ['carbon:ledger-file:delete'],
        popConfirm: {
          title: '确定要删除该台账文件吗？删除后数据不可恢复，请谨慎操作！',
          confirm: handleDelete.bind(null, row),
        },
      },

    );
    if((row.auditStatus === '0' || row.auditStatus === '4') && row.uploadStatus === '1') {
      actions.push( {
        label: '提交',
        type: 'link' as const,
        icon: ACTION_ICON.SUBMIT,
        auth: ['carbon:ledger-file:update'],
        popConfirm: {
          title: '确定要提交该审定记录吗？提交后将进入上报流程，无法修改。',
          confirm: handleSubmit.bind(null, row),
        },
      });
    }
  }
  return actions;
}


function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalRef.value?.open({ viewMode: false });
}

function handleEdit(row: any) {
  formModalRef.value?.open({ ...row, viewMode: false });
}

function handleView(row: any) {
  formModalRef.value?.open({ ...row, viewMode: true });
}

async function handleDelete(row: any) {
  const hideLoading = message.loading({ content: '删除中...', duration: 0 });
  try {
    await deleteLedgerFile(row.id);
    message.success('删除成功');
    handleRefresh();
  } catch {
    message.error('删除失败');
  } finally {
    hideLoading();
  }
}

async function handleSubmit(row: any) {
  const hideLoading = message.loading({ content: '提交中...', duration: 0 });
  try {
    await updateAuditStatus(row.id, '1'); // 提交审定，状态改为审定通过中
    message.success('提交成功');
    handleRefresh();
  } catch {
    message.error('提交失败');
  } finally {
    hideLoading();
  }
}

async function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const params: LedgerFilePageParam = {
    pageNo: 1,
    pageSize: 9999,
    cityCode: formValues.cityCode,
    districtCode: formValues.districtCode,
    uploadType: formValues.uploadType,
    uploadStatus: formValues.uploadStatus,
    uploadTime: formValues.uploadTime,
  };
  try {
    const blob = (await exportLedgerFile(params)) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '台账审定.xls';
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    message.success('导出成功');
  } catch {
    message.error('导出失败');
  }
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
          const params: LedgerFilePageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            cityCode: formValues.cityCode,
            districtCode: formValues.districtCode,
            uploadType: formValues.uploadType,
            uploadStatus: formValues.uploadStatus,
            uploadTime: formValues.uploadTime,
          };
          const result = await getLedgerFilePage(params);
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
    emptyText: '暂无台账文件，请点击新增按钮添加',
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" data-level="county" @success="handleRefresh" />

    <Grid table-title="台账审定列表">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>

      <template #actions="{ row }">
        <TableAction :actions="getRowActions(row)" />
      </template>
    </Grid>
  </Page>
</template>
