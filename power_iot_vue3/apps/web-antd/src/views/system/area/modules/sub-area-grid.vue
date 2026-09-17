<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SystemAreaApi } from '#/api/system/area';

import { computed, ref, watch } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { isEmpty } from '@vben/utils';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteArea, deleteListArea, getAreaPage } from '#/api/system/area';
import { $t } from '#/locales';

import { useGridColumns2 } from '../data';

const props = defineProps<{
  parentExtName?: string;
  parentId?: number;
}>();

const emit = defineEmits<{
  create: [];
  edit: [row: SystemAreaApi.Area];
  select: [row: SystemAreaApi.Area];
}>();

// 批量删除弹窗
const [DeleteModalComp, deleteModalApi] = useVbenModal({
  title: '确认删除',
  destroyOnClose: true,
  async onConfirm() {
    await handleConfirmDelete();
  },
});

/** 编辑行政区划 */
function handleEdit(row: SystemAreaApi.Area) {
  emit('edit', row);
}

/** 删除行政区划 */
async function handleDelete(row: SystemAreaApi.Area) {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await deleteArea(row.id!);
    message.success('删除成功');
    gridApi.query();
  } catch {
    message.error('删除失败');
  } finally {
    hideLoading();
  }
}

/** 批量删除 - 打开弹窗 */
function handleBatchDelete() {
  if (checkedIds.value.length === 0) {
    message.warning('请先选择要删除的行政区划');
    return;
  }
  deleteModalApi.open();
}

/** 批量删除 - 确认删除 */
async function handleConfirmDelete() {
  const hideLoading = message.loading({
    content: '删除中...',
    duration: 0,
  });
  try {
    await deleteListArea(checkedIds.value);
    message.success(`成功删除 ${checkedIds.value.length} 条数据`);
    checkedIds.value = [];
    gridApi.query();
  } catch {
    message.error('批量删除失败');
  } finally {
    hideLoading();
  }
  deleteModalApi.close();
}

/** 新增 */
function handleCreate() {
  emit('create');
}

// 选中的行（用于批量删除）
const checkedIds = ref<number[]>([]);

function handleRowCheckboxChange({
  records,
}: {
  records: SystemAreaApi.Area[];
}) {
  checkedIds.value = records.map((item) => item.id!);
}

// 顶部工具栏操作按钮
const toolbarActions = computed(() => [
  {
    label: '新增',
    type: 'primary',
    icon: ACTION_ICON.ADD,
    onClick: handleCreate,
  },
  {
    label: '批量删除',
    type: 'primary',
    danger: checkedIds.value.length > 0,
    icon: ACTION_ICON.DELETE,
    disabled: isEmpty(checkedIds.value),
    onClick: handleBatchDelete,
  },
]);

const [Grid, gridApi] = useVbenVxeGrid({
  gridOptions: {
    columns: useGridColumns2(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          if (!props.parentId) {
            return { list: [], total: 0 };
          }
          const params = {
            parentId: props.parentId,
            levels: [],
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          };
          const result = await getAreaPage(params);
          for (const item of result || []) {
            item.parentExtName = props.parentExtName;
            item.parentExtId = props.parentId;
          }
          return {
            list: result || [],
            total: result?.length || 0,
          };
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isCurrent: true,
      isHover: true,
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
    pagerConfig: {
      pageSize: 50,
      pageSizes: [20, 50, 100],
    },
    checkboxConfig: {
      reserve: true,
    },
    emptyText: '当前行政区划无下属层级',
  } as VxeTableGridOptions<SystemAreaApi.Area>,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});

// 监听 parentId 变化，自动重新查询
watch(
  () => props.parentId,
  (parentId) => {
    if (parentId) {
      gridApi.query();
    }
  },
  { immediate: true },
);

defineExpose({
  refresh: () => {
    gridApi.query();
  },
});
</script>

<template>
  <DeleteModalComp>
    <p>确定要删除选中的 {{ checkedIds.length }} 条数据吗？</p>
  </DeleteModalComp>

  <Grid :table-title="parentExtName || '下属行政区划列表'">
    <!-- 顶部工具栏操作按钮 -->
    <template #toolbar-tools>
      <TableAction :actions="toolbarActions" />
    </template>

    <!-- 每行的操作按钮 -->
    <template #actions="{ row }">
      <TableAction
        :actions="[
          {
            label: $t('common.edit'),
            type: 'link',
            icon: ACTION_ICON.EDIT,
            onClick: handleEdit.bind(null, row),
          },
          {
            label: $t('common.delete'),
            type: 'link',
            danger: true,
            icon: ACTION_ICON.DELETE,
            popConfirm: {
              title: '确定要删除这条数据吗？',
              confirm: handleDelete.bind(null, row),
            },
          },
        ]"
      />
    </template>
  </Grid>
</template>
