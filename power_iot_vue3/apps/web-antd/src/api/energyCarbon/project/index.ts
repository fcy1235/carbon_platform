import type { PageParam } from '@vben/request';

import { requestClient } from '#/api/request';

// 项目关联用户（前端展示用）
export interface ProjectUser {
  id?: number;
  projectId?: number;
  carbonUserInfoId?: number;
  accountingId?: number;
  username?: string;
  idCard?: string;
  phone?: string;
  division?: string;
  address?: string;
  reformType?: string;
  baselineEmission?: number;
  actualEmission?: number;
  reduction?: number;
  electricityId?: string;
  gasId?: string;
}

// 项目文档
export interface ProjectDoc {
  id?: number;
  projectId?: number;
  docName?: string;
  docDesc?: string;
  docSize?: string;
  docPath?: string;
  uploader?: string;
  uploadTime?: string;
}

// 项目进度记录
export interface ProjectProgress {
  id?: number;
  projectId?: number;
  stage?: number;
  status?: number;
  updateTime?: string;
  updater?: string;
  content?: string;
}

// 项目
export interface Project {
  id?: number;
  projectCode?: string;
  projectName?: string;
  contactId?: number;
  contactName?: string;
  projectLeader?: string;
  projectDesc?: string;
  provinceCode?: string;
  cityCode?: string;
  districtCode?: string;
  planStartDate?: string;
  planEndDate?: string;
  projectCycle?: string;
  projectStatus?: number;
  totalReduction?: number;
  userList?: ProjectUser[];
  docs?: ProjectDoc[];
  progressList?: ProjectProgress[];
  createTime?: string;
}

// 项目分页请求参数
export interface ProjectPageParam extends PageParam {
  projectName?: string;
  contactName?: string;
  projectStatus?: number;
  planStartDateStart?: string;
  planEndDateEnd?: string;
  ids?: number[];
}

// 项目状态更新请求
export interface ProjectStatusUpdateReq {
  id: number;
  projectStatus: number;
  reason?: string;
}

// 项目进度记录创建请求
export interface ProjectProgressSaveReq {
  projectId: number;
  content: string;
}

// 获得项目分页
export function getProjectPage(params: ProjectPageParam) {
  return requestClient.get<any>('/carbon/project/page', { params });
}

// 项目精简信息
export interface ProjectSimple {
  projectId?: number;
  projectName?: string;
}

// 获得项目精简列表
export function getProjectSimpleList() {
  return requestClient.get<ProjectSimple[]>('/carbon/project/simple-list');
}

// 获得项目详情
export function getProjectDetail(id: number) {
  return requestClient.get<any>(`/carbon/project/get?id=${id}`);
}

// 创建项目
export function addProject(data: Project) {
  return requestClient.post<any>('/carbon/project/create', data);
}

// 更新项目
export function updateProject(data: Project) {
  return requestClient.put<any>('/carbon/project/update', data);
}

// 删除项目
export function deleteProject(id: number) {
  return requestClient.delete<any>(`/carbon/project/delete?id=${id}`);
}

// 变更项目状态
export function updateProjectStatus(data: ProjectStatusUpdateReq) {
  return requestClient.put<any>('/carbon/project/update-status', data);
}

// 删除项目文档
export function deleteProjectDoc(projectId: number, docName: string) {
  return requestClient.delete<any>(
    `/carbon/project/delete-doc?projectId=${projectId}&docName=${encodeURIComponent(docName)}`,
  );
}

// 上传项目文档
export function uploadProjectDoc(
  file: File,
  projectId: number,
  docDesc?: string,
) {
  return requestClient.upload<any>('/carbon/project/upload-doc', {
    file,
    projectId: String(projectId),
    docDesc: docDesc || '',
  });
}

// 添加项目进度记录
export function addProgressRecord(data: ProjectProgressSaveReq) {
  return requestClient.post<any>('/carbon/project/add-progress', data);
}

// 导出项目Excel
export function exportProject(params: any) {
  return requestClient.download('/carbon/project/export-excel', { params });
}
