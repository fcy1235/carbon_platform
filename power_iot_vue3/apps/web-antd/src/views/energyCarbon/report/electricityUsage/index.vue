<script setup lang="ts">
import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  exportElectricityUsageData,
  getElectricityUsageDataImportTemplate,
  getElectricityUsageDataList,
  importElectricityUsageData,
} from '#/api/energyCarbon/report';

import { useGridColumns, useGridFormSchema } from './data';
import ImportModal from './modules/import.vue';

const importModalRef = ref<InstanceType<typeof ImportModal> | null>(null);

function buildQueryParams(formValues: Record<string, any>) {
  return {
    heatingSeason: formValues.heatingSeason,
    keyword: formValues.keyword,
    electricityId: formValues.electricityId,
  };
}

function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const params = buildQueryParams(formValues);

  const hideLoading = message.loading({
    content: '导出中...',
    duration: 0,
  });

  exportElectricityUsageData(params)
    .then((blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = '电力用电量报表.xls';
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

function handleImport() {
  importModalRef.value?.open();
}

async function handleDownloadTemplate() {
  try {
    const blob = (await getElectricityUsageDataImportTemplate()) as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '电力用电量报表导入模板.xlsx';
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    message.success('模板下载成功');
  } catch {
    message.error('模板下载失败');
  }
}

async function handleImportSubmit(file: File) {
  try {
    await importElectricityUsageData(file);
    gridApi.query();
  } catch {
    message.error('导入失败');
    throw new Error('导入失败');
  }
}

const toolbarActions = [
  {
    label: '导入',
    type: 'default' as const,
    icon: ACTION_ICON.UPLOAD,
    auth: ['carbon:electricity-usage-report:import'],
    onClick: handleImport,
  },
  {
    label: '导出Excel',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:electricity-usage-report:export'],
    onClick: handleExport,
  },
];

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
    compact: true,
    wrapperClass: 'grid-cols-4',
    commonConfig: {
      labelWidth: 90,
    },
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async (_: any, formValues: Record<string, any>) => {
          const params = buildQueryParams(formValues);
          const result = await getElectricityUsageDataList(params);
          return {
            list: result.list || [],
            total: result.total || 0,
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
      enabled: true,
    },
    emptyText: '暂无数据',
  },
});
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="农村电代煤用户用电量统计">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>
    </Grid>

    <ImportModal
      ref="importModalRef"
      title="导入电力用电量报表"
      :on-submit="handleImportSubmit"
      @download-template="handleDownloadTemplate"
    />
  </Page>
</template>
