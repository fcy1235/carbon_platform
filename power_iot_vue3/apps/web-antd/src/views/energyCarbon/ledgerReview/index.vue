<script setup lang="ts">
import type { LedgerReportPageParam } from '#/api/energyCarbon/ledgerReport';

import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { getLedgerReportPage } from '#/api/energyCarbon/ledgerReport';

import { useGridColumns, useGridFormSchema } from './data';
import FormModal from './modules/form.vue';

const formModalRef = ref<InstanceType<typeof FormModal>>();

function getRowActions(row: any) {
  const actions: any[] = [
    {
      label: '查看',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:ledger-report:query'],
      onClick: handleView.bind(null, row),
    },
  ];

  // 审核中状态：显示审核按钮
  if (row.auditStatus === '2') {
    actions.push({
      label: '审核',
      type: 'link' as const,
      icon: ACTION_ICON.AUDIT,
      auth: ['carbon:ledger-file:update'],
      onClick: handleAudit.bind(null, row),
    });
  }

  return actions;
}

function handleRefresh() {
  gridApi.query();
}

function handleView(row: any) {
  formModalRef.value?.open({ ...row, viewMode: true });
}

function handleAudit(row: any) {
  formModalRef.value?.open({ ...row, auditMode: true });
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
          const params: LedgerReportPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            uploadType: formValues.uploadType,
            auditStatus: formValues.auditStatus,
            auditStatusCollection: ['2', '3', '4'],
            uploadTime: formValues.uploadTime,
          };
          const result = await getLedgerReportPage(params);
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
    emptyText: '暂无待核查台账上报记录',
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />

    <Grid table-title="台账核查列表">
      <template #actions="{ row }">
        <TableAction :actions="getRowActions(row)" />
      </template>
    </Grid>
  </Page>
</template>
