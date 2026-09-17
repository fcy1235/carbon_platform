<script setup lang="ts">
import type { LedgerReportPageParam } from '#/api/energyCarbon/ledgerReport';

import { computed, ref } from 'vue';

import { Page } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { updateAuditStatus } from '#/api/energyCarbon/ledgerFile';
import {
  deleteLedgerReport,
  getLedgerReportPage,
} from '#/api/energyCarbon/ledgerReport';

import { useGridColumns, useGridFormSchema } from './data';
import FormModal from './modules/form.vue';

const formModalRef = ref<InstanceType<typeof FormModal>>();


const toolbarActions = computed(() => [
  {
    label: '数据上报',
    type: 'primary' as const,
    icon: ACTION_ICON.ADD,
    auth: ['carbon:ledger-report:create'],
    onClick: handleCreate,
  },
]);

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

  // 待提交状态：编辑、删除、提交
  if (row.auditStatus === '1') {
    actions.push(
      {
        label: '编辑',
        type: 'link' as const,
        icon: ACTION_ICON.EDIT,
        auth: ['carbon:ledger-report:update'],
        onClick: handleEdit.bind(null, row),
      },
      {
        label: '删除',
        type: 'link' as const,
        danger: true,
        icon: ACTION_ICON.DELETE,
        auth: ['carbon:ledger-report:delete'],
        popConfirm: {
          title: '确定要删除该上报记录吗？删除后数据不可恢复，请谨慎操作！',
          confirm: handleDelete.bind(null, row),
        },
      },
      {
        label: '提交',
        type: 'link' as const,
        icon: ACTION_ICON.SUBMIT,
        auth: ['carbon:ledger-report:submit'],
        popConfirm: {
          title: '确定要提交该上报记录吗？提交后将进入审核流程，无法修改。',
          confirm: handleSubmit.bind(null, row),
        },
      },
    );
  }

  // 审核中状态：撤回
  if (row.auditStatus === '2') {
    actions.push({
      label: '撤回',
      type: 'link' as const,
      icon: ACTION_ICON.REVOKE,
      auth: ['carbon:ledger-report:update'],
      popConfirm: {
        title: '确定要撤回该上报记录吗？撤回后可重新编辑提交。',
        confirm: handleWithdraw.bind(null, row),
      },
    });
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
    await deleteLedgerReport(row.id);
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
    await updateAuditStatus(row.ledgerFileId, '2'); // 提交审核，状态改为审核中
    message.success('提交成功');
    handleRefresh();
  } catch {
    message.error('提交失败');
  } finally {
    hideLoading();
  }
}

async function handleWithdraw(row: any) {
  const hideLoading = message.loading({ content: '撤回中...', duration: 0 });
  try {
    await updateAuditStatus(row.ledgerFileId, '1'); // 撤回，状态改回待提交
    message.success('撤回成功');
    handleRefresh();
  } catch {
    message.error('撤回失败');
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
          const params: LedgerReportPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            uploadType: formValues.uploadType,
            auditStatus: formValues.auditStatus,
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
    emptyText: '暂无台账上报记录，请点击数据上报按钮添加',
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />

    <Grid table-title="台账上报列表">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>

      <template #uploadType="{ row }">
        {{ getDictLabel(DICT_TYPE.ENERGY_CBON_LEDGER_TYPE, row.uploadType) }}
      </template>

      <template #auditStatus="{ row }">
        <Tag>
          {{ getDictLabel(DICT_TYPE.ENERGY_CBON_LEDGER_REPORT_STATUS, row.auditStatus) }}
        </Tag>
      </template>

      <template #actions="{ row }">
        <TableAction :actions="getRowActions(row)" />
      </template>
    </Grid>
  </Page>
</template>
