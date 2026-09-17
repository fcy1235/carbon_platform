import { requestClient } from '#/api/request';

// 设备信息导入结果
export interface DeviceImportResult {
  totalCount?: number; // 总行数（不含空行）
  successCount?: number; // 成功行数
  failCount?: number; // 失败行数
  errors?: DeviceImportError[]; // 逐行错误明细
}

export interface DeviceImportError {
  rowNum?: number; // 行号
  message?: string; // 错误信息
}

// 下载设备信息导入模板
export function getDeviceImportTemplate() {
  return requestClient.download('/carbon/device-import/get-import-template');
}

// 导入设备信息
export function importDeviceInfo(file: File) {
  return requestClient.upload<DeviceImportResult>(
    '/carbon/device-import/import',
    { file },
  );
}
