<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />
    <ExportModal ref="exportModalRef" @success="handleRefresh" />
    <ImportModal ref="importModalRef" @success="handleRefresh" />

    <Grid table-title="补贴管理列表">
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

import { deleteSubsidy, getSubsidyPage } from '#/api/energyCarbon/subsidy';

import FormModal from './modules/form.vue';
import ExportModal from './modules/export.vue';
import ImportModal from './modules/import.vue';
import { useGridColumns, useGridFormSchema } from './data';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();
const importModalRef = ref<InstanceType<typeof ImportModal>>();

// 选中的行
const selectedRowKeys = ref<number[]>([]);

// 当前筛选条件
const currentFormValues = ref<Record<string, any>>({});

// 表格操作按钮
const toolbarActions = computed(() => [
  {
    label: '新增',
    type: 'primary',
    icon: ACTION_ICON.ADD,
    auth: ['carbon:subsidy:create'],
    onClick: handleCreate,
  },
  {
    label: '导入',
    type: 'primary',
    icon: ACTION_ICON.IMPORT,
    auth: ['carbon:subsidy:import'],
    onClick: handleImport,
  },
  {
    label: '导出',
    type: 'primary',
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:subsidy:export'],
    onClick: handleExport,
  },
]);

// 表格行内操作按钮
function getRowActions(row: any) {
  return [
    {
      label: '查看',
      type: 'link',
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:subsidy:query'],
      onClick: handleView.bind(null, row),
    },
    {
      label: '编辑',
      type: 'link',
      icon: ACTION_ICON.EDIT,
      auth: ['carbon:subsidy:update'],
      onClick: handleEdit.bind(null, row),
    },
    {
      label: '删除',
      type: 'link',
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:subsidy:delete'],
      popConfirm: {
        title: '确定要删除该补贴信息吗？删除后数据不可恢复，请谨慎操作！',
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
    await deleteSubsidy(row.id);
    message.success('删除成功');
    handleRefresh();
  } catch {
    message.error('删除失败');
  } finally {
    hideLoading();
  }
}

/** 导出 */
function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const queryParams: Record<string, any> = {
    username: formValues.username,
    provinceCode: formValues.division?.[0],
    cityCode: formValues.division?.[1],
    districtCode: formValues.division?.[2],
    reformType: formValues.reformType,
    status: formValues.status,
  };
  exportModalRef.value?.open({
    selectedIds: selectedRowKeys.value,
    queryParams,
  });
}

/** 导入 */
function handleImport() {
  importModalRef.value?.open();
}

function handleRowCheckboxChange({
  records,
}: {
  records: any[];
}) {
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
          // 保存当前筛选条件
          currentFormValues.value = { ...formValues };

          const params = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            username: formValues.username,
            provinceCode: formValues.division?.[0],
            cityCode: formValues.division?.[1],
            districtCode: formValues.division?.[2],
            reformType: formValues.reformType,
            status: formValues.status,
          };
          const result = await getSubsidyPage(params);
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
    emptyConfig: {
      icon: 'empty',
      text: '暂无用户补贴信息，请点击新增按钮添加',
    },
  },
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>
