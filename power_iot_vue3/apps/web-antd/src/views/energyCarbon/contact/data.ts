import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

/** 新增/修改的表单 */
export function useFormSchema(): VbenFormSchema[] {
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
      fieldName: 'name',
      label: '姓名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入姓名',
      },
      rules: 'required',
    },
    {
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入联系电话',
      },
      rules: 'mobileRequired',
    },
    {
      fieldName: 'email',
      label: '邮箱',
      component: 'Input',
      componentProps: {
        placeholder: '请输入邮箱',
      },
    },
    {
      fieldName: 'company',
      label: '所属公司',
      component: 'Input',
      componentProps: {
        placeholder: '请输入所属公司',
      },
    },
    {
      fieldName: 'position',
      label: '职位',
      component: 'Input',
      componentProps: {
        placeholder: '请输入职位',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'name',
      label: '姓名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入姓名',
        allowClear: true,
      },
    },
    {
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入联系电话',
        allowClear: true,
      },
    },
    {
      fieldName: 'company',
      label: '所属公司',
      component: 'Input',
      componentProps: {
        placeholder: '请输入所属公司',
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'name',
      title: '姓名',
      minWidth: 100,
    },
    {
      field: 'phone',
      title: '联系电话',
      minWidth: 120,
    },
    {
      field: 'email',
      title: '邮箱',
      minWidth: 150,
    },
    {
      field: 'company',
      title: '所属公司',
      minWidth: 120,
    },
    {
      field: 'position',
      title: '职位',
      minWidth: 100,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 160,
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
