import type { PageParam } from '@vben/request';

import { requestClient } from '#/api/request';

// 导入任务分页请求参数
export interface LedgerImportTaskPageParam extends PageParam {
  cityCode?: string;
  districtCode?: string;
  uploadType?: string;
  importStatus?: string;
  uploadTime?: string[];
}

// 导入任务
export interface LedgerImportTask {
  id?: number;
  cityCode?: string;
  cityName?: string;
  districtCode?: string;
  districtName?: string;
  uploadType?: string;
  fileName?: string;
  fileUrl?: string;
  dataCount?: number;
  successCount?: number;
  failCount?: number;
  uploadStatus?: string;
  importStatus?: string;
  uploader?: string;
  uploadTime?: string;
  resultFileUrl?: string;
  remark?: string;
}

// 导入明细分页请求参数
export interface LedgerImportDetailPageParam extends PageParam {
  taskId: number;
  importStatus?: string;
}

// 导入明细
export interface LedgerImportDetail {
  id?: number;
  taskId?: number;
  cityName?: string;
  districtName?: string;
  townName?: string;
  villageName?: string;
  address?: string;
  ownerName?: string;
  username?: string;
  idCard?: string;
  phone?: string;
  heatingArea?: number;
  extFields?: string;
  importStatus?: string;
  errorMsg?: string;
  createTime?: string;
}

// 获得导入任务分页
export function getLedgerImportTaskPage(params: LedgerImportTaskPageParam) {
  return requestClient.get<any>('/carbon/ledger-import/page', { params });
}

// 获得导入任务
export function getLedgerImportTask(id: number) {
  return requestClient.get<any>(`/carbon/ledger-import/get?id=${id}`);
}

// 创建导入任务（multipart/form-data，检测阶段）
export function createLedgerImportTask(
  uploadType: string,
  file: File,
  cityCode: string,
  districtCode: string,
) {
  const formData = new FormData();
  formData.append('uploadType', uploadType);
  formData.append('file', file);
  formData.append('cityCode', cityCode);
  formData.append('districtCode', districtCode);
  return requestClient.post<any>('/carbon/ledger-import/create', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 删除导入任务
export function deleteLedgerImportTask(id: number) {
  return requestClient.delete<any>(`/carbon/ledger-import/delete?id=${id}`);
}

// 执行导入
export function executeLedgerImport(taskId: number) {
  return requestClient.post<any>('/carbon/ledger-import/execute', null, {
    params: { taskId },
  });
}

// 获得导入明细分页
export function getLedgerImportDetailPage(params: LedgerImportDetailPageParam) {
  return requestClient.get<any>('/carbon/ledger-import/detail-page', { params });
}

// 获得导入明细列表
export function getLedgerImportDetailList(taskId: number) {
  return requestClient.get<any>(`/carbon/ledger-import/detail-list?taskId=${taskId}`);
}

// 导出异常项
export function exportLedgerImportErrors(taskId: number) {
  return requestClient.download('/carbon/ledger-import/export-errors', {
    params: { taskId },
  });
}

// 下载导入模板
export function downloadLedgerImportTemplate(type: string) {
  return requestClient.download('/carbon/ledger-import/download-template', {
    params: { type },
  });
}

// 下载导入原文件
export function downloadLedgerImportFile(id: number) {
  return requestClient.download('/carbon/ledger-import/download', {
    params: { id },
  });
}
