<script setup lang="ts">
import { Page } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { exportReformAccountReport, getReformAccountReport } from '#/api/energyCarbon/report';

import { useGridColumns, useGridFormSchema } from './data';

const toolbarActions = [
  {
    label: '导出Excel',
    type: 'primary' as const,
    icon: ACTION_ICON.DOWNLOAD,
    auth: ['carbon:report:export'],
    onClick: handleExport,
  },
];

function handleExport() {
  const formValues = gridApi.formApi?.getLatestSubmissionValues?.() || {};
  const division = formValues.division || [];
  const params = {
    provinceCode: division[0],
    cityCode: division[1],
    districtCode: division[2],
    townCode: division[3],
    villageCode: division[4],
    keyword: formValues.keyword,
    phone: formValues.phone,
    reformType: formValues.reformType,
    useStatus: formValues.useStatus,
    reformYear: formValues.reformYear
      ? (formValues.reformYear.format
          ? formValues.reformYear.format('YYYY')
          : String(formValues.reformYear))
      : undefined,
  };

  const hideLoading = message.loading({
    content: '导出中...',
    duration: 0,
  });

  exportReformAccountReport(params)
    .then((blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = '清洁取暖改造确户台账.xls';
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
          const division = formValues.division || [];
          const params = {
            provinceCode: division[0],
            cityCode: division[1],
            districtCode: division[2],
            townCode: division[3],
            villageCode: division[4],
            keyword: formValues.keyword,
            phone: formValues.phone,
            reformType: formValues.reformType,
            useStatus: formValues.useStatus,
            reformYear: formValues.reformYear
              ? (formValues.reformYear.format
                  ? formValues.reformYear.format('YYYY')
                  : String(formValues.reformYear))
              : undefined,
          };
          const result = await getReformAccountReport(params);
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
    emptyText: '暂无数据',
  },
});
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="清洁取暖改造确户台账">
      <template #toolbar-tools>
        <TableAction :actions="toolbarActions" />
      </template>
    </Grid>
  </Page>
</template>
