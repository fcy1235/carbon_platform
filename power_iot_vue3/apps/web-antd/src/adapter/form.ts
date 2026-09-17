import type {
  VbenFormProps as FormProps,
  VbenFormSchema as FormSchema,
} from '@vben/common-ui';

import type { ComponentPropsMap, ComponentType } from './component';

import { setupVbenForm, useVbenForm as useForm, z } from '@vben/common-ui';
import { $t } from '@vben/locales';
import { isMobile } from '@vben/utils';

// 身份证号正则（18位）
const ID_CARD_REGEX = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/;

async function initSetupVbenForm() {
  setupVbenForm<ComponentType>({
    config: {
      // ant design vue组件库默认都是 v-model:value
      baseModelPropName: 'value',

      // 一些组件是 v-model:checked 或者 v-model:fileList
      modelPropNameMap: {
        Checkbox: 'checked',
        Radio: 'checked',
        Switch: 'checked',
        Upload: 'fileList',
      },
    },
    defineRules: {
      // 输入项目必填国际化适配
      required: (value, _params, ctx) => {
        if (value === undefined || value === null || value.length === 0) {
          return $t('ui.formRules.required', [ctx.label]);
        }
        return true;
      },
      // 选择项目必填国际化适配
      selectRequired: (value, _params, ctx) => {
        if (value === undefined || value === null) {
          return $t('ui.formRules.selectRequired', [ctx.label]);
        }
        return true;
      },
      // 手机号非必填
      mobile: (value, _params, ctx) => {
        if (value === undefined || value === null || value.length === 0) {
          return true;
        } else if (!isMobile(value)) {
          return $t('ui.formRules.mobile', [ctx.label]);
        }
        return true;
      },
      // 手机号必填
      mobileRequired: (value, _params, ctx) => {
        if (value === undefined || value === null || value.length === 0) {
          return $t('ui.formRules.required', [ctx.label]);
        }
        if (!isMobile(value)) {
          return $t('ui.formRules.mobile', [ctx.label]);
        }
        return true;
      },
      // 身份证号非必填
      idCard: (value, _params, ctx) => {
        if (value === undefined || value === null || value.length === 0) {
          return true;
        }
        if (!ID_CARD_REGEX.test(value)) {
          return `${ctx.label}格式不正确`;
        }
        return true;
      },
      // 身份证号必填
      idCardRequired: (value, _params, ctx) => {
        if (value === undefined || value === null || value.length === 0) {
          return $t('ui.formRules.required', [ctx.label]);
        }
        if (!ID_CARD_REGEX.test(value)) {
          return `${ctx.label}格式不正确`;
        }
        return true;
      },
    },
  });
}

const useVbenForm = useForm<ComponentType, ComponentPropsMap>;

export { initSetupVbenForm, useVbenForm, z };

export type VbenFormSchema = FormSchema<ComponentType, ComponentPropsMap>;
export type VbenFormProps = FormProps<ComponentType, ComponentPropsMap>;
