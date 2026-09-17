<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />
    <ImportModal ref="importModalRef" @success="handleRefresh" />
    <ExportModal ref="exportModalRef" @success="handleRefresh" />
    <DeleteModalComp>
      <p>确定要删除选中的 {{ selectedRowKeys.length }} 条数据吗？</p>
    </DeleteModalComp>

    <Grid table-title="基本信息列表">
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
import ImportModal from './modules/import.vue';
import ExportModal from './modules/export.vue';
import { useGridColumns, useGridFormSchema } from './data';
import {
  getUserInfoList,
  deleteUserInfo,
  batchDeleteUserInfo,
} from '#/api/energyCarbon/basicData';
import type { UserInfoListParam } from '#/api/energyCarbon/basicData';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const importModalRef = ref<InstanceType<typeof ImportModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();

// FormModal 组件内部已经包含了 Modal 逻辑

// ExportModalComp 已替换为 ExportModal 组件

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
    type: 'primary',
    icon: ACTION_ICON.ADD,
    auth: ['carbon:user-info:create'],
    onClick: handleCreate,
  },
  {
    label: '导入',
    type: 'primary',
    icon: ACTION_ICON.UPLOAD,
    auth: ['carbon:user-info:import'],
    onClick: handleImport,
  },
  {
    label: '导出',
    type: 'primary',
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:user-info:export'],
    onClick: handleExport,
  },
  {
    label: '批量删除',
    type: 'primary',
    danger: selectedRowKeys.value.length > 0,
    icon: ACTION_ICON.DELETE,
    auth: ['carbon:user-info:delete'],
    disabled: isEmpty(selectedRowKeys.value),
    onClick: handleBatchDelete,
  },
]);

// 表格行内操作按钮
function getRowActions(row: any) {
  return [
    {
      label: '查看',
      type: 'link',
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:user-info:query'],
      onClick: handleView.bind(null, row),
    },
    {
      label: '编辑',
      type: 'link',
      icon: ACTION_ICON.EDIT,
      auth: ['carbon:user-info:update'],
      onClick: handleEdit.bind(null, row),
    },
    {
      label: '删除',
      type: 'link',
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:user-info:delete'],
      popConfirm: {
        title: '确定要删除这条数据吗？',
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
    // 调用真实删除 API
    await deleteUserInfo(row.id);
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

/** 确认删除 */
async function handleConfirmDelete() {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    // 调用真实批量删除 API
    await batchDeleteUserInfo(selectedRowKeys.value);
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

/** 导入 */
function handleImport() {
  importModalRef.value?.open();
}


/** 导出 */
function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const queryParams: Record<string, any> = {
    username: formValues.username,
    idCard: formValues.idCard,
    provinceCode: formValues.division?.[0],
    cityCode: formValues.division?.[1],
    districtCode: formValues.division?.[2],
    townCode: formValues.division?.[3],
    villageCode: formValues.division?.[4],
    reformType: formValues.reformType,
    reformMode: formValues.reformMode,
    useStatus: formValues.useStatus,
  };
  exportModalRef.value?.open({
    selectedIds: selectedRowKeys.value,
    queryParams,
  });
}

// 导出相关函数已移至 ExportModal 组件

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
      // autoLoad: true,
      ajax: {
        query: async (
          { page }: { page: { currentPage: number; pageSize: number } },
          formValues: Record<string, any>,
        ) => {
          // 构建请求参数
          const params: UserInfoListParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            username: formValues.username,
            idCard: formValues.idCard,
            provinceCode: formValues.division?.[0],
            cityCode: formValues.division?.[1],
            districtCode: formValues.division?.[2],
            townCode: formValues.division?.[3],
            villageCode: formValues.division?.[4],
            reformType: formValues.reformType,
            reformMode: formValues.reformMode,
            useStatus: formValues.useStatus,
          };

          // 调用真实API
          const result = await getUserInfoList(params);
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
  },
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>

<style scoped>
.import-content {
  padding: 20px 0;
}

.import-progress {
  margin-top: 20px;
}

.progress-text {
  display: block;
  margin-top: 10px;
  color: #606266;
  text-align: center;
}

.upload-tip {
  margin-top: 10px;
  color: #606266;
}
</style>
