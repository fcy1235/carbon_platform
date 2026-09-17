import type { PageParam } from '@vben/request';
import { requestClient } from '#/api/request';

// 联系人分页请求参数
export interface ContactPageParam extends PageParam {
  name?: string; // 姓名
  phone?: string; // 联系电话
  company?: string; // 所属公司
  ids?: number[]; // 选中的ID列表，用于选中导出
}

// 联系人
export interface Contact {
  id?: number; // 编号
  name: string; // 姓名
  phone?: string; // 联系电话
  email?: string; // 邮箱
  company?: string; // 所属公司
  position?: string; // 职位
}

// 获得联系人分页
export function getContactPage(params: ContactPageParam) {
  return requestClient.get<any>('/carbon/contact/page', { params });
}

// 获得联系人
export function getContact(id: number) {
  return requestClient.get<any>(`/carbon/contact/get?id=${id}`);
}

// 新增联系人
export function createContact(data: Contact) {
  return requestClient.post<any>('/carbon/contact/create', data);
}

// 更新联系人
export function updateContact(data: Contact) {
  return requestClient.put<any>('/carbon/contact/update', data);
}

// 删除联系人
export function deleteContact(id: number) {
  return requestClient.delete<any>(`/carbon/contact/delete?id=${id}`);
}

// 批量删除联系人
export function batchDeleteContact(ids: number[]) {
  return requestClient.delete<any>(
    `/carbon/contact/delete-list?ids=${ids.join(',')}`,
  );
}

// 导出联系人Excel
export function exportContact(params: ContactPageParam) {
  return requestClient.download('/carbon/contact/export-excel', { params });
}
