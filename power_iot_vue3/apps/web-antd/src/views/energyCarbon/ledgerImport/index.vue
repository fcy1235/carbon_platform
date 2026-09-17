<script setup lang="ts">
import type { LedgerImportTaskPageParam } from '#/api/energyCarbon/ledgerImport';

import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteLedgerImportTask,
  downloadLedgerImportFile,
  executeLedgerImport,
  exportLedgerImportErrors,
  getLedgerImportTaskPage,
} from '#/api/energyCarbon/ledgerImport';

import FormModal from '../components/ledger-file-form-modal.vue';
import { useGridColumns, useGridFormSchema } from './data';
import DetailModal from './modules/detail-modal.vue';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const detailModalRef = ref<InstanceType<typeof DetailModal>>();

function getRowActions(row: any) {
  const actions: any[] = [
    {
      label: '导入明细',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:ledger-import:query'],
      onClick: handleDetail.bind(null, row),
    },
    {
      label: '下载',
      type: 'link' as const,
      icon: ACTION_ICON.DOWNLOAD,
      auth: ['carbon:ledger-import:query'],
      onClick: handleDownloadFile.bind(null, row),
    },
    {
      label: '导入',
      type: 'link' as const,
      icon: ACTION_ICON.UPLOAD,
      auth: ['carbon:ledger-import:execute'],
      popConfirm: {
        title: '确定要执行导入吗？导入后将把数据写入系统。',
        confirm: handleExecute.bind(null, row),
      },
    },
    {
      label: '导出异常项',
      type: 'link' as const,
      icon: ACTION_ICON.DOWNLOAD,
      auth: ['carbon:ledger-import:export'],
      onClick: handleExportErrors.bind(null, row),
    }
  ];
  return actions;
}

function handleRefresh() {
  gridApi.query();
}

function handleCreate() {
  formModalRef.value?.open();
}

function handleDetail(row: any) {
  detailModalRef.value?.open(row.id);
}

async function handleExecute(row: any) {
  const hideLoading = message.loading({ content: '正在执行导入...', duration: 0 });
  try {
    await executeLedgerImport(row.id);
    message.success('导入执行成功');
    handleRefresh();
  } catch {
    message.error('导入执行失败');
  } finally {
    hideLoading();
  }
}

async function handleDownloadFile(row: any) {
  try {
    const blob = (await downloadLedgerImportFile(row.id)) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = row.fileName || '导入文件.xlsx';
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch {
    message.error('文件下载失败');
  }
}

async function handleExportErrors(row: any) {
  const hideLoading = message.loading({ content: '导出中...', duration: 0 });
  try {
    const blob = (await exportLedgerImportErrors(row.id)) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `导入异常项_${row.id}.xls`;
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    message.success('导出成功');
  } catch {
    message.error('导出失败');
  } finally {
    hideLoading();
  }
}

async function handleDelete(row: any) {
  const hideLoading = message.loading({ content: '删除中...', duration: 0 });
  try {
    await deleteLedgerImportTask(row.id);
    message.success('删除成功');
    handleRefresh();
  } catch {
    message.error('删除失败');
  } finally {
    hideLoading();
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
          const params: LedgerImportTaskPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            uploadType: formValues.uploadType,
            importStatus: formValues.importStatus,
            uploadTime: formValues.uploadTime,
          };
          const result = await getLedgerImportTaskPage(params);
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
    emptyText: '暂无导入任务，请点击添加导入任务按钮',
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" data-level="city" @success="handleRefresh" />
    <DetailModal ref="detailModalRef" />

    <Grid table-title="导入任务列表">
      <template #toolbar-tools>
        <TableAction :actions="[
          {
            label: '添加导入任务',
            type: 'primary' as const,
            icon: ACTION_ICON.ADD,
            auth: ['carbon:ledger-import:create'],
            onClick: handleCreate,
          },
        ]" />
      </template>

      <template #actions="{ row }">
        <TableAction :actions="getRowActions(row)" />
      </template>
    </Grid>
  </Page>
</template>
