<script setup lang="ts">
import type { ProjectPageParam } from '#/api/energyCarbon/project';

import { computed, ref } from 'vue';

import { Page } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteProject,
  getProjectPage,
} from '#/api/energyCarbon/project';

import { getProjectStatusLabel, useGridColumns, useGridFormSchema } from './data';
import ExportModal from './modules/export.vue';
import FormModal from './modules/form.vue';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();

// 当前查询条件
const currentQueryParams = ref<Record<string, any>>({});

// 表格操作按钮
const toolbarActions = computed(() => [
  {
    label: '新增',
    type: 'primary' as const,
    icon: ACTION_ICON.ADD,
    auth: ['carbon:project:create'],
    onClick: handleCreate,
  },
  {
    label: '导出',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:project:export'],
    onClick: handleExport,
  },
]);

// 表格行内操作按钮
function getRowActions(row: any) {
  const actions: any[] = [
    {
      label: '查看',
      type: 'link',
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:project:query'],
      onClick: handleView.bind(null, row),
    },
  ];

   actions.push({
      label: '编辑',
      type: 'link',
      icon: ACTION_ICON.EDIT,
      auth: ['carbon:project:update'],
      onClick: handleEdit.bind(null, row),
    });
  // 如果项目状态不是审批中(2)，可以编辑
  // if (row.projectStatus !== 2) {
   
  // }

  actions.push({
    label: '删除',
    type: 'link',
    danger: true,
    icon: ACTION_ICON.DELETE,
    auth: ['carbon:project:delete'],
    popConfirm: {
      title: '确定要删除该项目吗？删除后数据不可恢复，请谨慎操作！',
      confirm: handleDelete.bind(null, row),
    },
  });

  return actions;
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
  formModalRef.value?.open({ id: row.id, viewMode: false });
}

/** 查看 */
function handleView(row: any) {
  formModalRef.value?.open({ id: row.id, viewMode: true });
}

/** 删除 */
async function handleDelete(row: any) {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await deleteProject(row.id);
    message.success('删除成功');
    handleRefresh();
  } finally {
    hideLoading();
  }
}

/** 获取选中行的ID列表 */
function getSelectedRowIds(): number[] {
  const checkboxRecords = gridApi.grid?.getCheckboxRecords() || [];
  return checkboxRecords.map((row: any) => row.id).filter(Boolean);
}

/** 导出 */
function handleExport() {
  const selectedIds = getSelectedRowIds();
  exportModalRef.value?.open({
    selectedIds,
    queryParams: currentQueryParams.value,
  });
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
      autoLoad: true,
      ajax: {
        query: async (
          { page }: { page: { currentPage: number; pageSize: number } },
          formValues: Record<string, any>,
        ) => {
          // 保存当前查询条件
          currentQueryParams.value = { ...formValues };

          const params: ProjectPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            projectName: formValues.projectName,
            contactName: formValues.projectLeader,
            projectStatus: formValues.projectStatus ? Number(formValues.projectStatus) : undefined,
            planStartDateStart: formValues.planStartDateStart,
            planEndDateEnd: formValues.planEndDateEnd,
          };
          const result = await getProjectPage(params);
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
    checkboxConfig: {
      reserve: true,
      highlight: true,
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
    pagerConfig: {
      pageSize: 20,
      pageSizes: [20, 50, 100],
    },
    emptyText: '暂无项目数据，请点击新增按钮添加',
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />
    <ExportModal ref="exportModalRef" @success="handleRefresh" />

    <Grid table-title="项目列表">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>

      <template #actions="{ row }">
        <TableAction
          :actions="getRowActions(row)"
        />
      </template>

      <template #status="{ row }">
        <span class="status-btn" :class="[`status-${row.projectStatus}`]">
          {{ getProjectStatusLabel(row.projectStatus) }}
        </span>
      </template>

      <template #planStartDate="{ row }">
        {{ row.planStartDate || '-' }}
      </template>

      <template #planEndDate="{ row }">
        {{ row.planEndDate || '-' }}
      </template>
    </Grid>
  </Page>
</template>

<style scoped>
.status-btn {
  display: inline-block;
  padding: 0px 6px;
  font-size: 11px;
  border-radius: 4px;
  min-width: 47px;
  text-align: center;
}

/* 申报 1 */
.status-1 {
  color: #d46b08;
  background-color: #fff7e6;
  border: 1px solid #ffd591;
}

/* 审批 2 */
.status-2 {
  color: #1890ff;
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
}

/* 实施 3 */
.status-3 {
  color: #52c41a;
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
}

/* 验收 4 */
.status-4 {
  color: #722ed1;
  background-color: #f9f0ff;
  border: 1px solid #d3adf7;
}

/* 结束 5 */
.status-5 {
  color: #8c8c8c;
  background-color: #f5f5f5;
  border: 1px solid #d9d9d9;
}
</style>
