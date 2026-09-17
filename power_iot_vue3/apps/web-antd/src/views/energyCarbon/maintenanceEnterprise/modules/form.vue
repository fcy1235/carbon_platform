<script lang="ts" setup>
import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import { message, Pagination, Table } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createMaintenanceEnterprise,
  updateMaintenanceEnterprise,
} from '#/api/energyCarbon/maintenanceEnterprise';
import { getMaintenanceStationPage } from '#/api/energyCarbon/maintenanceStation';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<any>();
const viewMode = ref(false);
const getTitle = computed(() => {
  if (viewMode.value) {
    return '查看维保企业';
  }
  return formData.value?.id ? '编辑维保企业' : '新增维保企业';
});

// 查看模式弹窗加宽以容纳网点表格
const modalWidth = computed(() => (viewMode.value ? 900 : 800));

// 维保网点列表（查看模式，通过网点分页接口按 enterpriseId 查询）
const stationList = ref<any[]>([]);
const stationTotal = ref(0);
const stationPage = ref(1);
const stationPageSize = ref(10);
const stationLoading = ref(false);

const stationColumns = [
  { title: '网点名称', dataIndex: 'stationName', width: 110, ellipsis: true },
  {
    title: '网点服务类型',
    dataIndex: 'serviceType',
    width: 90,
    ellipsis: true,
    customRender: ({ text }: { text: string }) =>
      getDictLabel(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, text) || '-',
  },
  {
    title: '网点经营状态',
    dataIndex: 'businessStatus',
    width: 80,
    ellipsis: true,
    customRender: ({ text }: { text: string }) =>
      getDictLabel(DICT_TYPE.ENERGY_CBON_NETWORK_BUSINESS_STATUS, text) || '-',
  },
  {
    title: '维保人员数量',
    dataIndex: 'maintenanceStaffCount',
    width: 90,
    ellipsis: true,
  },
  {
    title: '覆盖村数',
    dataIndex: 'coveredVillages',
    width: 70,
    ellipsis: true,
  },
  {
    title: '覆盖户数',
    dataIndex: 'coveredHouseholds',
    width: 70,
    ellipsis: true,
  },
  {
    title: '服务范围',
    dataIndex: 'serviceScope',
    width: 120,
    ellipsis: true,
    customRender: ({ record }: any) => {
      const names = record?.serviceScopeNames;
      if (Array.isArray(names) && names.length > 0) {
        return names.join('、');
      }
      const text = record?.serviceScope;
      if (!text) return '-';
      try {
        const areas = JSON.parse(text);
        if (Array.isArray(areas)) {
          return areas.map((a: any) => a.label).join('、');
        }
      } catch {
        return text;
      }
      return text;
    },
  },
  {
    title: '网点负责人',
    dataIndex: 'managerName',
    width: 90,
    ellipsis: true,
  },
  {
    title: '网点负责人电话',
    dataIndex: 'managerPhone',
    width: 100,
    ellipsis: true,
  },
];

const stationPagination = computed(() => ({
  current: stationPage.value,
  pageSize: stationPageSize.value,
  total: stationTotal.value,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50'],
  showTotal: (t: number) => `共 ${t} 条`,
  size: 'small' as const,
}));

// 查看模式加载该企业下的维保网点（服务端分页）
async function loadStationList(page = 1, pageSize = stationPageSize.value) {
  if (!formData.value?.id) return;
  stationLoading.value = true;
  try {
    const result = await getMaintenanceStationPage({
      pageNo: page,
      pageSize,
      enterpriseId: formData.value.id,
    });
    stationList.value = result?.list || [];
    stationTotal.value = result?.total || 0;
    stationPage.value = page;
    stationPageSize.value = pageSize;
  } finally {
    stationLoading.value = false;
  }
}

function handleStationPageChange(page: number, pageSize: number) {
  loadStationList(page, pageSize);
}

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-2',
    labelWidth: 140,
  },
  layout: 'horizontal',
  wrapperClass: 'grid-cols-2',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  title: getTitle,
  width: modalWidth,
  footer: computed(() => !viewMode.value),
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    let data: any = await formApi.getValues();
    const originalData: any = formData.value;
    const enterpriseId: string = originalData?.id;

    // 从行政区级联值中提取省/市/区编码
    const division = data?.division || [];
    data = {
      ...data,
      ...(enterpriseId ? { id: enterpriseId } : undefined),
      provinceCode: division[0] ?? undefined,
      cityCode: division[1] ?? undefined,
      countyCode: division[2] ?? undefined,
    };
    delete data.division;

    try {
      await (enterpriseId
        ? updateMaintenanceEnterprise(data)
        : createMaintenanceEnterprise(data));
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = undefined;
      viewMode.value = false;
      stationList.value = [];
      stationTotal.value = 0;
      stationPage.value = 1;
      stationPageSize.value = 10;
      return;
    }
    const data = modalApi.getData<any>();
    if (!data) {
      return;
    }
    modalApi.lock();
    try {
      formData.value = data;
      viewMode.value = data.viewMode || false;
      // 查看模式：加载该企业下的维保网点列表
      if (viewMode.value) {
        await loadStationList(1);
      }
      // 编辑时回显行政区级联选择器，构造完整路径供LazyAreaCascader
      const division: any[] = [];
      if (formData.value.provinceCode) division[0] = formData.value.provinceCode;
      if (formData.value.cityCode) division[1] = formData.value.cityCode;
      if (formData.value.countyCode) division[2] = formData.value.countyCode;
      await formApi.setValues({
        ...formData.value,
        division: division.some(Boolean) ? division : undefined,
      });
    } finally {
      modalApi.unlock();
    }
  },
});

defineExpose({
  open: (data?: any) => {
    modalApi.setData(data).open();
  },
});
</script>

<template>
  <Modal>
    <Form class="mx-4" />
    <!-- 查看模式：该维保企业下的维保网点信息 -->
    <div v-if="viewMode" class="station-section">
      <div class="station-section-title">维保网点信息</div>
      <Table
        :columns="stationColumns"
        :data-source="stationList"
        row-key="id"
        :pagination="false"
        :loading="stationLoading"
        size="small"
        :scroll="{ x: 700 }"
      />
      <div class="station-pagination">
        <Pagination
          v-bind="stationPagination"
          @change="handleStationPageChange"
        />
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.station-section {
  margin: 8px 16px 16px;
}

.station-section-title {
  margin-bottom: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  border-left: 3px solid #1890ff;
  padding-left: 8px;
}

.station-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

:deep(.ant-table) {
  font-size: 12px;
}

:deep(.ant-table-thead > tr > th) {
  padding: 5px 8px !important;
  font-weight: 500;
  background-color: #f8f9fa;
}

:deep(.ant-table-tbody > tr > td) {
  padding: 4px 8px !important;
}
</style>
