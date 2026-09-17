<script setup lang="ts">
import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  exportGasUsageData,
  getGasUsageDataList,
} from '#/api/energyCarbon/report';

import { useGridColumns, useGridFormSchema } from './data';
import ImportModal from './modules/import.vue';

const importModalRef = ref<InstanceType<typeof ImportModal>>();

const toolbarActions = [
  {
    label: '导入',
    type: 'primary' as const,
    icon: ACTION_ICON.UPLOAD,
    onClick: handleImport,
  },
  {
    label: '导出Excel',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:report:export'],
    onClick: handleExport,
  },
];

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 导入 */
function handleImport() {
  importModalRef.value?.open();
}

function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const params = {
    heatingSeason: formValues.heatingSeason,
    keyword: formValues.keyword,
    gasId: formValues.gasId,
  };

  const hideLoading = message.loading({
    content: '导出中...',
    duration: 0,
  });

  exportGasUsageData(params)
    .then((blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = '农村煤改气用户气量统计表（采暖季）.xls';
      document.body.append(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      message.success('导出成功');
    })
    .catch(() => {
      message.error('导出失败');
    })
    .finally(() => {
      hideLoading();
    });
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
    compact: true,
    wrapperClass: 'grid-cols-5',
    commonConfig: {
      labelWidth: 70,
    },
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async (_: any, formValues: Record<string, any>) => {
          const params = {
            heatingSeason: formValues.heatingSeason,
            keyword: formValues.keyword,
            gasId: formValues.gasId,
          };
          const result = await getGasUsageDataList(params);
          return {
            list: result || [],
            total: result?.length || 0,
          };
        },
      },
    },
    rowConfig: {
      isHover: true,
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
    pagerConfig: {
      enabled: false,
    },
    emptyText: '暂无数据，请点击导入按钮导入燃气数据',
  },
});
</script>

<template>
  <Page auto-content-height>
    <ImportModal ref="importModalRef" @success="handleRefresh" />

    <Grid table-title="农村气代煤用户用气量统计">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>
    </Grid>
  </Page>
</template>
