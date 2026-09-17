<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />
    <ExportModal ref="exportModalRef" @success="handleRefresh" />
    <DeleteModalComp>
      <p>
        确定要删除选中的
        {{ selectedRowKeys.length }}
        条联系人吗？删除后数据不可恢复，请谨慎操作！
      </p>
    </DeleteModalComp>

    <Grid table-title="联系人列表">
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

import { Page, useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';
import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { isEmpty } from '@vben/utils';

import FormModal from './modules/form.vue';
import ExportModal from './modules/export.vue';
import { useGridColumns, useGridFormSchema } from './data';
import {
  getContactPage,
  deleteContact,
  batchDeleteContact,
} from '#/api/energyCarbon/contact';
import type { ContactPageParam } from '#/api/energyCarbon/contact';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();

// 当前查询条件
const currentQueryParams = ref<Record<string, any>>({});

const [DeleteModalComp, deleteModalApi] = useVbenModal({
  title: '确认删除',
  destroyOnClose: true,
  async onConfirm() {
    await handleConfirmDelete();
  },
});

// 选中的行
const selectedRowKeys = ref<number[]>([]);

// 表格操作按钮
const toolbarActions = computed(() => [
  {
    label: '新增',
    type: 'primary' as const,
    icon: ACTION_ICON.ADD,
    auth: ['carbon:contact:create'],
    onClick: handleCreate,
  },
  {
    label: '导出',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:contact:export'],
    onClick: handleExport,
  },
  {
    label: '批量删除',
    type: 'primary' as const,
    danger: selectedRowKeys.value.length > 0,
    icon: ACTION_ICON.DELETE,
    auth: ['carbon:contact:delete'],
    disabled: isEmpty(selectedRowKeys.value),
    onClick: handleBatchDelete,
  },
]);

// 表格行内操作按钮
function getRowActions(row: any) {
  return [
    {
      label: '查看',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:contact:query'],
      onClick: handleView.bind(null, row),
    },
    {
      label: '编辑',
      type: 'link' as const,
      icon: ACTION_ICON.EDIT,
      auth: ['carbon:contact:update'],
      onClick: handleEdit.bind(null, row),
    },
    {
      label: '删除',
      type: 'link' as const,
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:contact:delete'],
      popConfirm: {
        title: '确定要删除该联系人吗？删除后数据不可恢复，请谨慎操作！',
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
    await deleteContact(row.id);
    message.success('删除成功');
    handleRefresh();
  } catch (error) {
    message.error('删除失败');
  } finally {
    hideLoading();
  }
}

/** 批量删除 */
function handleBatchDelete() {
  deleteModalApi.open();
}

/** 确认批量删除 */
async function handleConfirmDelete() {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await batchDeleteContact(selectedRowKeys.value);
    message.success(`成功删除 ${selectedRowKeys.value.length} 条数据`);
    selectedRowKeys.value = [];
    handleRefresh();
  } catch (error) {
    message.error('批量删除失败');
  } finally {
    hideLoading();
  }
  deleteModalApi.close();
}

/** 导出 */
function handleExport() {
  exportModalRef.value?.open({
    selectedIds: selectedRowKeys.value,
    queryParams: currentQueryParams.value,
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
      autoLoad: true,
      ajax: {
        query: async (
          { page }: { page: { currentPage: number; pageSize: number } },
          formValues: Record<string, any>,
        ) => {
          // 保存当前查询条件
          currentQueryParams.value = { ...formValues };

          const params: ContactPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            name: formValues.name,
            phone: formValues.phone,
            company: formValues.company,
          };

          const result = await getContactPage(params);
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
    emptyText: '暂无联系人信息，请点击新增按钮添加',
  },
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>
