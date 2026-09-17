<script lang="ts" setup>
import { ref, computed } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { useVbenForm } from '#/adapter/form';
import { message, Input, Button } from 'ant-design-vue';
import { $t } from '#/locales';

import {
  createDevice,
  generateDeviceCode,
  getDeviceDataDetail,
  updateDevice,
} from '#/api/energyCarbon/device';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { getUserInfoList } from '#/api/energyCarbon/basicData';

import { useDeviceDataGridColumns } from '../data';
import { useVbenVxeGrid } from '#/adapter/vxe-table';
import UserSelectModal from '../../components/user-select-modal.vue';
import EnterpriseSelectModal from './enterprise-select-modal.vue';

const emit = defineEmits(['success']);

const viewMode = ref(false);
const formData = ref<any>();
const userSelectModalRef = ref<InstanceType<typeof UserSelectModal>>();
const enterpriseSelectModalRef = ref<InstanceType<typeof EnterpriseSelectModal>>();

const title = computed(() => {
  if (viewMode.value) return '查看设备';
  return formData.value?.id ? '编辑设备' : '新增设备';
});

// 设备类型 -> 编码前缀（规则：EM-电表 GM-燃气表 ASHP-空气源热泵，编码 = 前缀-6位序号）。
// 用关键字模糊匹配字典 label，兼容「空气源（能）热泵」等写法变体
function resolveCodePrefix(typeLabel?: string): string | undefined {
  if (!typeLabel) {
    return undefined;
  }
  if (typeLabel.includes('燃气')) {
    return 'GM';
  }
  if (typeLabel.includes('热泵')) {
    return 'ASHP';
  }
  if (typeLabel.includes('电表')) {
    return 'EM';
  }
  return undefined;
}

// 根据所选设备类型生成设备编码并回填（仅新增模式；编辑/查看不动原编码）
async function refreshDeviceCodeByType(typeValue?: number) {
  if (formData.value?.id || viewMode.value) {
    return;
  }
  if (typeValue === undefined || typeValue === null) {
    return;
  }
  const options = getDictOptions(DICT_TYPE.ENERGY_CBON_DEVICE_TYPE, 'number');
  const label = options.find((o: any) => o.value === typeValue)?.label;
  const prefix = resolveCodePrefix(label);
  if (!prefix) {
    return;
  }
  try {
    const code = await generateDeviceCode(prefix);
    await deviceFormApi.setValues({ deviceCode: code || '' });
  } catch {
    // 接口失败不阻断流程，提交时后端会兜底生成
  }
}

// 处理用户选择回填
function handleUserSelect(user: any) {
  basicFormApi.setValues({
    carbonUserInfoId: user.id,
    userCode: user.userCode || '',
    username: user.username || '',
    idCard: user.idCard || '',
    phone: user.phone || '',
    division: user.division || '',
    address: user.address || '',
    dataSource: user.dataSource || '',
    reformType: user.reformType || '',
  });
  // 同步改造类型到设备表单
  if (user.reformType) {
    deviceFormApi.setValues({ reformType: user.reformType });
  }
}

// 处理维保企业选择回填
function handleEnterpriseSelect(enterprise: any) {
  deviceFormApi.setValues({
    gasEnterpriseId: enterprise.id,
    gasEnterpriseName: enterprise.enterpriseName || '',
    enterpriseCreditCode: enterprise.unifiedSocialCreditCode || '',
  });
}

// 基础信息表单 schema
function buildBasicSchema(): any[] {
  return [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'carbonUserInfoId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: {
        placeholder: '请选择用户',
      },
      rules: 'required',
    },
    {
      fieldName: 'userCode',
      label: '用户编码',
      component: 'Input',
      componentProps: {
        placeholder: '选择用户后自动带入',
        disabled: true,
      },
    },
    {
      fieldName: 'idCard',
      label: '身份证号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入身份证号',
        disabled: true,
      },
    },
    {
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入联系电话',
        disabled: true,
      },
    },
    {
      fieldName: 'division',
      label: '所属行政区',
      component: 'Input',
      componentProps: {
        placeholder: '请输入所属行政区',
        disabled: true,
      },
    },
    {
      fieldName: 'address',
      label: '地址',
      component: 'Input',
      componentProps: {
        placeholder: '请输入地址',
        disabled: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'dataSource',
      label: '数据来源',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_DATA_SOURCE, 'string'),
        placeholder: '选择用户后自动带入',
        disabled: true,
      },
    },
    // 改造类型 - 禁止修改
    {
      fieldName: 'reformType',
      label: '改造类别',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE,
          'string',
        ),
        placeholder: '选择用户后自动带入',
        disabled: true,
      },
      rules: 'required',
    },
    // 燃气用户编码（气代煤时显示）
    {
      fieldName: 'gasUserCode',
      label: '燃气用户编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入燃气用户编码',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
  ];
}

// 气代煤判断
function isGasType(values: any) {
  return !!values.reformType && values.reformType === '2';
}

// 非气代煤（电代煤等）判断
function isElectricType(values: any) {
  return !!values.reformType && values.reformType === '1';
}

// 设备信息表单 schema
function buildDeviceSchema(): any[] {
  return [
    {
      fieldName: 'gasEnterpriseId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    // 隐藏的reformType，用于设备表单内字段联动
    {
      fieldName: 'reformType',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'isCarbon',
      label: '是否为碳核算设备',
      component: 'Select',
      componentProps: {
        placeholder: '请选择',
        options: [
          { label: '是', value: 1 },
          { label: '否', value: 0 },
        ],
      },
      rules: 'required',
    },
    {
      fieldName: 'deviceCode',
      label: '设备编码',
      component: 'Input',
      componentProps: {
        placeholder: '设备编码由系统自动生成',
        disabled: true,
      },
    },
    {
      fieldName: 'deviceName',
      label: '设备名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入设备名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'deviceType',
      label: '设备类型',
      component: 'Select',
      componentProps: {
        placeholder: '请选择设备类型',
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_DEVICE_TYPE, 'number'),
        // 选择设备类型后按「前缀-6位序号」规则自动生成设备编码
        onChange: (val: number) => refreshDeviceCodeByType(val),
      },
      rules: 'required',
    },
    // 供气企业（气代煤时显示）- 使用slot方式，点击弹框选择
    {
      fieldName: 'gasEnterpriseName',
      label: '供气企业',
      component: 'Input',
      componentProps: {
        placeholder: '请选择供气企业',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'enterpriseCreditCode',
      label: '企业信用代码',
      component: 'Input',
      componentProps: {
        placeholder: '根据供气企业自动带入',
        disabled: true,
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'wallMountedStoveInstallYear',
      label: '壁挂炉安装年份',
      component: 'DatePicker',
      componentProps: {
        picker: 'year',
        // 后端该字段是 String（如 "2023"），必须配 valueFormat，
        // 否则 DatePicker 收到字符串值会直接抛异常导致整个弹窗组件树崩溃（关不掉/卡死）
        valueFormat: 'YYYY',
        placeholder: '请选择壁挂炉安装年份',
        style: { width: '100%' },
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'gasId',
      label: '燃气表具号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入燃气表具号',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'deviceBrandModel',
      label: '设备品牌及型号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入设备品牌及型号',
      },
    },
    {
      fieldName: 'gasMeterType',
      label: '燃气表类型',
      component: 'Select',
      componentProps: {
        placeholder: '请选择燃气表类型',
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_GAS_METER_TYPE,
          'string',
        ),
        allowClear: true,
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    {
      fieldName: 'safetyDeviceStatus',
      label: '安全装置配备情况',
      component: 'Select',
      componentProps: {
        placeholder: '请选择安全装置配备情况',
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_SAFE_DEVICE_STATUS,
          'string',
        ),
        allowClear: true,
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isGasType(values),
      },
    },
    // 电表号（非气代煤且有改造类型值时显示）
    {
      fieldName: 'electricityId',
      label: '电表号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入电表号',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => isElectricType(values),
      },
    },
    // 以下字段暂时不用（2026-09-09 用户要求注释掉），如需恢复直接解开注释即可
    // {
    //   fieldName: 'manufacturer',
    //   label: '生产厂家',
    //   component: 'Input',
    //   componentProps: {
    //     placeholder: '请输入生产厂家',
    //   },
    //   rules: 'required',
    // },
    // {
    //   fieldName: 'productionDate',
    //   label: '生产日期',
    //   component: 'DatePicker',
    //   componentProps: {
    //     placeholder: '请选择生产日期',
    //     format: 'YYYY-MM-DD',
    //     valueFormat: 'YYYY-MM-DD',
    //   },
    //   rules: 'required',
    // },
    // {
    //   fieldName: 'serviceLife',
    //   label: '使用年限',
    //   component: 'InputNumber',
    //   componentProps: {
    //     min: 0,
    //     placeholder: '请输入使用年限（年）',
    //     style: { width: '100%' },
    //   },
    //   rules: 'required',
    // },
    // {
    //   fieldName: 'installDate',
    //   label: '安装日期',
    //   component: 'DatePicker',
    //   componentProps: {
    //     placeholder: '请选择安装日期',
    //     format: 'YYYY-MM-DD',
    //     valueFormat: 'YYYY-MM-DD',
    //   },
    //   rules: 'required',
    // },
    // {
    //   fieldName: 'operationDate',
    //   label: '投运日期',
    //   component: 'DatePicker',
    //   componentProps: {
    //     placeholder: '请选择投运日期',
    //     format: 'YYYY-MM-DD',
    //     valueFormat: 'YYYY-MM-DD',
    //   },
    //   rules: 'required',
    // },
    // {
    //   fieldName: 'stopDate',
    //   label: '停运日期',
    //   component: 'DatePicker',
    //   componentProps: {
    //     placeholder: '请选择停运日期',
    //     format: 'YYYY-MM-DD',
    //     valueFormat: 'YYYY-MM-DD',
    //   },
    // },
    {
      fieldName: 'status',
      label: '设备状态',
      component: 'Select',
      componentProps: {
        placeholder: '请选择设备状态',
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_DEVICE_STATUS, 'number'),
      },
      rules: 'required',
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Input',
      componentProps: {
        placeholder: '请输入备注',
      },
    },
  ];
}

// 基础信息表单
const [BasicForm, basicFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-1',
    labelWidth: 140,
  },
  layout: 'horizontal',
  wrapperClass: 'grid-cols-2',
  schema: buildBasicSchema(),
  showDefaultActions: false,
});

// 设备信息表单
const [DeviceForm, deviceFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
      disabled: viewMode,
    },
    formItemClass: 'col-span-1',
    labelWidth: 140,
  },
  layout: 'horizontal',
  wrapperClass: 'grid-cols-2',
  schema: buildDeviceSchema(),
  showDefaultActions: false,
});

// ===== 设备数据（仅查看模式 + 煤改电设备且有上报数据时展示） =====
const showDeviceData = ref(false);

// 获取设备类型标签
function getDeviceTypeLabel(deviceType?: number): string {
  if (deviceType === undefined || deviceType === null) return '';
  const options = getDictOptions(DICT_TYPE.ENERGY_CBON_DEVICE_TYPE, 'number');
  return options.find((o: any) => o.value === deviceType)?.label || '';
}

// 动态计算设备数据列
const deviceDataColumns = computed(() =>
  useDeviceDataGridColumns({
    reformType: formData.value?.reformType,
    reformMode: formData.value?.reformMode,
    deviceTypeLabel: getDeviceTypeLabel(formData.value?.deviceType),
  }),
);

const [DeviceDataGrid, deviceDataGridApi] = useVbenVxeGrid({
  gridOptions: {
    columns: deviceDataColumns.value,
    height: 300,
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async () => {
          showDeviceData.value = false;
          const deviceCode = formData.value?.deviceCode;
          if (!deviceCode) {
            return { list: [], total: 0 };
          }
          try {
            const result: any = await getDeviceDataDetail(deviceCode);
            const list = result?.list || [];
            showDeviceData.value = list.length > 0;
            return { list, total: list.length };
          } catch {
            return { list: [], total: 0 };
          }
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    pagerConfig: {
      enabled: false,
    },
    emptyText: '暂无设备数据',
  },
});

const [Modal, modalApi] = useVbenModal({
  title: title as any,
  footer: computed(() => !viewMode.value) as any,
  width: 900,
  centered: true,
  class: 'max-w-[900px]',
  async onConfirm() {
    if (viewMode.value) {
      return;
    }
    const { valid: basicValid } = await basicFormApi.validate();
    const { valid: deviceValid } = await deviceFormApi.validate();

    if (!basicValid || !deviceValid) {
      return;
    }

    modalApi.lock();
    try {
      const basicData = await basicFormApi.getValues();
      let deviceData = await deviceFormApi.getValues();

      // 新增模式：提交前按最终所选设备类型【重新】取一次编码。
      // 不依赖 deviceType 的 onChange 是否触发过，保证编码与类型一致（EM/GM/ASHP-000001），
      // 避免编码为空落到后端兜底前缀（DEV）。
      if (!formData.value?.id) {
        await refreshDeviceCodeByType(deviceData.deviceType);
        deviceData = await deviceFormApi.getValues();
      }

      const submitData: any = {
        ...basicData,
        ...deviceData,
      };

      if (formData.value?.id) {
        await updateDevice(submitData as any);
      } else {
        await createDevice(submitData as any);
      }

      message.success($t('ui.actionMessage.operationSuccess'));
      emit('success');
      await modalApi.close();
    } catch (error) {
      message.error('保存失败');
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      // 关闭时【什么都不要做】：
      // 1) 此时改 viewMode/formData 会让标题从「查看设备」闪成「新增设备」、底部按钮冒出；
      // 2) 在关闭过渡中 resetForm 一旦抛异常，vben 的关闭流程会被中断，
      //    弹窗重新弹出（表现为"关闭后又弹出一个新增设备窗口"）。
      // 所有状态统一在下次打开时重置（见下方打开分支）。
      return;
    }

    const data = modalApi.getData<any>();
    if (!data) {
      return;
    }

    // 注意：这里【不要】用 modalApi.lock()/unlock()。lock 会把 store.submitting 置 true，
    // 一旦某个 await 没走到 unlock，submitting 卡住 -> 关闭按钮被 close-disabled 禁掉，
    // 表现为"点关闭没反应"。打开数据填充很快，无需上锁。
    try {
      // 每次打开前先重置两个表单，避免上一次「查看/编辑」的数据残留
      await basicFormApi.resetForm();
      await deviceFormApi.resetForm();

      formData.value = data;
      viewMode.value = data.viewMode || false;

      const formValues = { ...data };

      // 确保deviceType转换为number类型（字典配置为number）
      if (formValues.deviceType !== undefined && formValues.deviceType !== null) {
        formValues.deviceType = Number(formValues.deviceType);
      }
      // 确保status转换为number类型
      if (formValues.status !== undefined && formValues.status !== null) {
        formValues.status = Number(formValues.status);
      }

      await basicFormApi.setValues(formValues);
      await deviceFormApi.setValues(formValues);

      // 查看模式 + 煤改电设备：查询设备上报数据（有数据才展示表格）
      showDeviceData.value = false;
      if (viewMode.value && isElectricType({ reformType: data.reformType })) {
        // 更新表格列（根据 reformType/reformMode/deviceType 动态显示）
        deviceDataGridApi.setGridOptions({ columns: deviceDataColumns.value });
        deviceDataGridApi.query();
      }

      // 新增模式：设备编码改为「选择设备类型后按前缀规则生成」（见 deviceType 的 onChange）。
      // 打开时通常还没选类型，这里仅在已带类型时兜底触发一次。
      if (!data.id && !viewMode.value && formValues.deviceType !== undefined) {
        await refreshDeviceCodeByType(formValues.deviceType);
      }
    } catch (error) {
      // 打开失败也要保证 modal 处于可关闭状态，并暴露错误
      console.error('[设备表单] 打开/回显数据异常 ->', error);
    }
  },
});

function open(data?: Record<string, any>) {
  modalApi.setData(data).open();
}

defineExpose({
  open,
});
</script>

<template>
  <Modal>
    <div class="mx-4">
      <!-- 基础信息 -->
      <div class="form-section">
        <h3 class="section-title">基础信息</h3>
        <BasicForm>
          <template #username="slotProps">
            <Input :value="slotProps.value" disabled placeholder="请选择用户">
              <template #suffix>
                <Button
                  type="link"
                  size="small"
                  :disabled="slotProps.disabled"
                  @click="userSelectModalRef?.open()"
                >
                  选择
                </Button>
              </template>
            </Input>
          </template>
        </BasicForm>
      </div>

      <!-- 设备信息 -->
      <div class="form-section">
        <h3 class="section-title">设备信息</h3>
        <DeviceForm>
          <template #gasEnterpriseName="slotProps">
            <Input
              :value="slotProps.value"
              disabled
              placeholder="请选择供气企业"
            >
              <template #suffix>
                <Button
                  type="link"
                  size="small"
                  :disabled="slotProps.disabled"
                  @click="enterpriseSelectModalRef?.open()"
                >
                  选择
                </Button>
              </template>
            </Input>
          </template>
        </DeviceForm>
      </div>

      <!-- 设备数据（仅煤改电设备且有上报数据时展示） -->
      <div v-show="showDeviceData" class="form-section">
        <h3 class="section-title">设备数据</h3>
        <DeviceDataGrid />
      </div>
    </div>
  </Modal>
  <UserSelectModal ref="userSelectModalRef" :api="getUserInfoList" @confirm="handleUserSelect" />
  <EnterpriseSelectModal
    ref="enterpriseSelectModalRef"
    @confirm="handleEnterpriseSelect"
  />
</template>

<style scoped>
.form-section {
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2329;
}
</style>
