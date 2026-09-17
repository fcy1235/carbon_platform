<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SystemAreaApi } from '#/api/system/area';

import { nextTick, ref, watch } from 'vue';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteArea, getAreaPage } from '#/api/system/area';
import { $t } from '#/locales';

import { useGridColumns } from '../data';

const props = defineProps<{
  parentExtName?: string;
  parentId?: number;
}>();

const emit = defineEmits<{
  edit: [row: SystemAreaApi.Area];
  select: [row: SystemAreaApi.Area];
}>();

/** 编辑行政区划 */
function handleEdit(row: SystemAreaApi.Area) {
  emit('edit', row);
}

/** 删除行政区划 */
async function handleDelete(row: SystemAreaApi.Area) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.name]),
    duration: 0,
  });
  try {
    await deleteArea(row.id!);
    message.success($t('ui.actionMessage.deleteSuccess', [row.name]));
    gridApi.query();
  } finally {
    hideLoading();
  }
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

const [Grid, gridApi] = useVbenVxeGrid({
  gridOptions: {
    columns: useGridColumns(),
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
          await handleAfterQuery(result);
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
    cellClick: ({ row }) => {
      emit('select', row);
    },
  },
});

// 数据加载完成后选中第一行
const handleAfterQuery = async (result: SystemAreaApi.Area[]) => {
  if (result && result.length > 0) {
    await nextTick();
    // 设置当前选中行
    gridApi.grid.setCurrentRow?.(result[0]);
    // 触发选中事件，让主页面处理联动
    emit('select', result[0]!);
  }
};

// 监听 parentId 变化，自动重新查询
watch(
  () => props.parentId,
  () => {
    gridApi.query();
  },
);

defineExpose({
  refresh: () => gridApi.query(),
});
</script>

<template>
  <Grid :table-title="parentExtName || '下属行政区划列表'">
    <template #actions="{ row }">
      <TableAction
        :actions="[
          {
            label: $t('common.edit'),
            type: 'link',
            icon: ACTION_ICON.EDIT,
            auth: ['system:area:update'],
            onClick: handleEdit.bind(null, row),
          },
          {
            label: $t('common.delete'),
            type: 'link',
            danger: true,
            icon: ACTION_ICON.DELETE,
            auth: ['system:area:delete'],
            popConfirm: {
              title: $t('ui.actionMessage.deleteConfirm', [row.name]),
              confirm: handleDelete.bind(null, row),
            },
          },
        ]"
      />
    </template>
  </Grid>
</template>
