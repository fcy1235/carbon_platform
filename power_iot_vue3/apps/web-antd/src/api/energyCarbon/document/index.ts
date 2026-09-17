import type { PageParam } from '@vben/request';

import { requestClient } from '#/api/request';

// 文档分页请求参数
export interface DocumentList {
  docTitle?: string; // 文档标题
  docName?: string; // 文档名称
  uploader?: string; // 上传人
}

// 文档分页请求参数
export interface DocumentPageParam extends DocumentList, PageParam {
  ids?: number[]; // 选中的ID列表，用于选中导出
}

// 详细信息项
export interface DocumentDetail {
  id?: number;
  name: string; // 名称
  description: string; // 描述
  formula: string; // 计算公式
}

// 参数说明项
export interface DocumentParam {
  id?: number;
  paramName: string; // 参数名称
  paramDescription: string; // 参数描述
}

// 文档
export interface Document {
  id?: number; // 编号
  docCode?: string; // 文档编号
  docTitle: string; // 文档标题
  docName?: string; // 文档名称
  version?: string; // 版本号
  publishDate?: string; // 发布日期
  applicableBoundary?: string; // 适用边界
  formula?: string; // 计算公式
  fileUrl?: string; // 文件地址
  remark?: string; // 备注
  details?: DocumentDetail[]; // 详细信息列表
  params?: DocumentParam[]; // 参数说明列表
}

// 获得文档分页
export function getDocumentPage(params: DocumentPageParam) {
  return requestClient.get<any>('/carbon/document/page', { params });
}
// 获得文档列表
export function getDocumentList(params: DocumentList) {
  return requestClient.get<any>('/carbon/document/list', { params });
}

// 获得文档精简列表
export function getDocumentSimpleList() {
  return requestClient.get<any>('/carbon/document/simple-list');
}

// 获得文档编码
export function getDocumentCode() {
  return requestClient.get<any>(`/carbon/document/getCode`);
}

// 获得文档
export function getDocument(id: number) {
  return requestClient.get<any>(`/carbon/document/get?id=${id}`);
}

// 新增文档（纯JSON，无文件）
export function createDocument(data: Document) {
  return requestClient.post<any>('/carbon/document/create', data);
}

// 更新文档（纯JSON，无文件）
export function updateDocument(data: Document) {
  return requestClient.put<any>('/carbon/document/update', data);
}

// 新增文档（带文件上传）
export function createDocumentWithFile(data: Document, file: File) {
  const formData = new FormData();
  formData.append(
    'data',
    new Blob([JSON.stringify(data)], { type: 'application/json' }),
  );
  formData.append('file', file);
  return requestClient.post<any>('/carbon/document/create', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 300_000, // 5分钟超时，大文件上传需要更长时间
  });
}

// 更新文档（带文件上传，文件可选）
export function updateDocumentWithFile(data: Document, file?: File) {
  const formData = new FormData();
  formData.append(
    'data',
    new Blob([JSON.stringify(data)], { type: 'application/json' }),
  );
  if (file) {
    formData.append('file', file);
  }
  return requestClient.put<any>('/carbon/document/update', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 300_000, // 5分钟超时
  });
}

// 删除文档
export function deleteDocument(id: number) {
  return requestClient.delete<any>(`/carbon/document/delete?id=${id}`);
}

// 导出文档Excel
export function exportDocument(params: DocumentPageParam) {
  return requestClient.download('/carbon/document/export-excel', { params });
}

// 上传文档（旧接口，未使用）
export function uploadDocument() {
  return requestClient.post<any>('/carbon/document/upload');
}
