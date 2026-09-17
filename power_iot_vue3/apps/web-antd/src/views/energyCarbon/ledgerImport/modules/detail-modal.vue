<script lang="ts" setup>
import type { LedgerImportDetailPageParam } from '#/api/energyCarbon/ledgerImport';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import { Button, message, Select, Tag, Tooltip } from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  exportLedgerImportErrors,
  getLedgerImportDetailPage,
} from '#/api/energyCarbon/ledgerImport';

import { useDetailColumns } from '../data';

const taskId = ref<number>();
const importStatus = ref<string>();
const exporting = ref(false);

const statusOptions = [
  { label: '全部', value: '' },
  { label: '成功', value: '1' },
  { label: '失败', value: '2' },
];

const [Grid, gridApi] = useVbenVxeGrid({
  gridOptions: {
    columns: useDetailColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async (
          { page }: { page: { currentPage: number; pageSize: number } },
        ) => {
          if (!taskId.value) {
            return { list: [], total: 0 };
          }
          const params: LedgerImportDetailPageParam = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            taskId: taskId.value,
            importStatus: importStatus.value || undefined,
          };
          const result = await getLedgerImportDetailPage(params);
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
    },
    pagerConfig: {
      pageSize: 20,
      pageSizes: [20, 50, 100],
    },
    emptyText: '暂无导入明细',
  },
});

const [Modal, modalApi] = useVbenModal({
  title: '导入明细',
  class: 'w-5/6 h-[80vh]',
  contentClass: 'flex flex-col overflow-hidden',
  centered: true,
  destroyOnClose: true,
  fullscreenButton: false,
  footer: false,
  onOpenChange(isOpen: boolean) {
    if (isOpen) {
      gridApi.query();
    } else {
      taskId.value = undefined;
      importStatus.value = undefined;
    }
  },
});

function handleStatusChange() {
  gridApi.query();
}

async function handleExportErrors() {
  if (!taskId.value) return;
  exporting.value = true;
  try {
    const blob = (await exportLedgerImportErrors(taskId.value)) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `导入异常项_${taskId.value}.xls`;
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    message.success('导出成功');
  } catch {
    message.error('导出失败');
  } finally {
    exporting.value = false;
  }
}

function open(id: number) {
  taskId.value = id;
  importStatus.value = '';
  modalApi.open();
}

defineExpose({ open });
</script>

<template>
  <Modal>
    <div class="flex h-full flex-col">
      <div class="mb-2 flex shrink-0 items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="text-sm">导入状态：</span>
          <Select
            v-model:value="importStatus"
            :options="statusOptions"
            style="width: 120px"
            size="small"
            @change="handleStatusChange"
          />
        </div>
        <Button
          type="primary"
          size="small"
          :loading="exporting"
          @click="handleExportErrors"
        >
          导出异常项
        </Button>
      </div>
      <div class="min-h-0 flex-1">
        <Grid>
          <template #detailStatus="{ row }">
            <Tag :color="row.importStatus === '1' ? 'green' : 'red'">
              {{ getDictLabel(DICT_TYPE.ENERGY_CBON_SUCCESS_STATUS, row.importStatus) }}
            </Tag>
          </template>
          <template #errorMsg="{ row }">
            <Tooltip v-if="row.errorMsg" :title="row.errorMsg" placement="topLeft">
              <div class="truncate">{{ row.errorMsg }}</div>
            </Tooltip>
            <span v-else>-</span>
          </template>
        </Grid>
      </div>
    </div>
  </Modal>
</template>
