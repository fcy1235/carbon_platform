<script setup lang="ts">
import type { DevicePageParam } from '#/api/energyCarbon/device';

import { computed, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteDevice, getDevicePage } from '#/api/energyCarbon/device';

import { useGridColumns, useGridFormSchema } from './data';
import ExportModal from './modules/export.vue';
import FormModal from './modules/form.vue';
import ImportModal from './modules/import.vue';

const formModalRef = ref<InstanceType<typeof FormModal>>();
const exportModalRef = ref<InstanceType<typeof ExportModal>>();
const importModalRef = ref<InstanceType<typeof ImportModal>>();

// 当前查询条件
const currentQueryParams = ref<Record<string, any>>({});

// 表格操作按钮
const toolbarActions = computed(() => [
  {
    label: '新增',
    type: 'primary' as const,
    icon: ACTION_ICON.ADD,
    auth: ['carbon:device:create'],
    onClick: handleCreate,
  },
  {
    label: '导入',
    type: 'primary' as const,
    icon: ACTION_ICON.UPLOAD,
    onClick: handleImport,
  },
  {
    label: '导出',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:device:export'],
    onClick: handleExport,
  },
  {
    label: '同步接口',
    type: 'primary' as const,
    icon: ACTION_ICON.REFRESH,
  },
]);

// 表格行内操作按钮
function getRowActions(row: any) {
  return [
    {
      label: '查看',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      auth: ['carbon:device:query'],
      onClick: handleView.bind(null, row),
    },
    {
      label: '编辑',
      type: 'link' as const,
      icon: ACTION_ICON.EDIT,
      auth: ['carbon:device:update'],
      onClick: handleEdit.bind(null, row),
    },
    {
      label: '删除',
      type: 'link' as const,
      danger: true,
      icon: ACTION_ICON.DELETE,
      auth: ['carbon:device:delete'],
      popConfirm: {
        title: '确定要删除该设备信息吗？删除后数据不可恢复，请谨慎操作！',
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

/** 导入 */
function handleImport() {
  importModalRef.value?.open();
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
    await deleteDevice(row.id);
    message.success('删除成功');
    handleRefresh();
  } catch {
    message.error('删除失败');
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

          const params: DevicePageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            username: formValues.username,
            deviceName: formValues.deviceName,
            deviceType: formValues.deviceType,
            manufacturer: formValues.manufacturer,
            reformType: formValues.reformType,
            status: formValues.status,
          };

          // 处理行政区划级联选择器值
          if (formValues.division && formValues.division.length > 0) {
            params.provinceCode = formValues.division[0];
            if (formValues.division.length > 1) {
              params.cityCode = formValues.division[1];
            }
            if (formValues.division.length > 2) {
              params.districtCode = formValues.division[2];
            }
          }

          const result = await getDevicePage(params);
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
    emptyText: '暂无设备数据，请点击新增按钮添加',
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal ref="formModalRef" @success="handleRefresh" />
    <ImportModal ref="importModalRef" @success="handleRefresh" />
    <ExportModal ref="exportModalRef" @success="handleRefresh" />

    <Grid table-title="设备管理列表">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>

      <template #actions="{ row }">
        <TableAction :actions="getRowActions(row)" />
      </template>


    </Grid>
  </Page>
</template>

<style scoped>

</style>
