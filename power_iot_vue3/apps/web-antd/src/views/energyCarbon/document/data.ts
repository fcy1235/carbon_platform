import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

/** 新增/修改的表单 - 基础信息 */
export function useBaseFormSchema(): VbenFormSchema[] {
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
      fieldName: 'docCode',
      label: '文档编号',
      component: 'Input',
      componentProps: {
        placeholder: '系统自动生成',
        disabled: true,
      },
    },
    {
      fieldName: 'docTitle',
      label: '文档标题',
      component: 'Input',
      componentProps: {
        placeholder: '请输入文档标题',
      },
      rules: 'required',
    },
    {
      fieldName: 'docName',
      label: '文档名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入文档名称（如：文件名.pdf）',
      },
    },
    {
      fieldName: 'applicableBoundary',
      label: '适用边界',
      component: 'Input',
      componentProps: {
        placeholder: '请输入适用边界',
      },
    },
    {
      fieldName: 'formula',
      label: '计算公式',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入计算公式（如：E=AD×EF）',
        rows: 2,
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入备注',
      },
    },
  ];
}

/** 详细信息表单 */
export function useDetailFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'name',
      label: '名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'description',
      label: '描述',
      component: 'Input',
      componentProps: {
        placeholder: '请输入描述',
      },
      rules: 'required',
    },
    {
      fieldName: 'formula',
      label: '计算公式',
      component: 'Input',
      componentProps: {
        placeholder: '请输入计算公式',
      },
      rules: 'required',
    },
  ];
}

/** 参数说明表单 */
export function useParamFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'paramName',
      label: '参数名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入参数名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'paramDescription',
      label: '参数描述',
      component: 'Input',
      componentProps: {
        placeholder: '请输入参数描述',
      },
      rules: 'required',
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'docTitle',
      label: '文档标题',
      component: 'Input',
      componentProps: {
        placeholder: '请输入文档标题',
        allowClear: true,
      },
    },
    {
      fieldName: 'uploader',
      label: '上传人',
      component: 'Input',
      componentProps: {
        placeholder: '请输入上传人',
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'checkbox', width: 60 },
    { type: 'seq', width: 60 },
    {
      field: 'docCode',
      title: '文档编号',
      minWidth: 150,
    },
    {
      field: 'docTitle',
      title: '文档标题',
      minWidth: 150,
    },
    {
      field: 'applicableBoundary',
      title: '适用边界',
      minWidth: 150,
    },
    {
      field: 'uploader',
      title: '上传人',
      minWidth: 100,
    },
    {
      field: 'uploadTime',
      title: '上传时间',
      minWidth: 160,
    },
    {
      field: 'remark',
      title: '备注',
      minWidth: 150,
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
