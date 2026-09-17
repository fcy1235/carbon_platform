import { requestClient } from '#/api/request';

// 燃气安全员分页请求参数
export interface GasSafetyOfficerPageParam {
  pageNo?: number;
  pageSize?: number;
  ids?: number[];
  name?: string;
  qualificationNo?: string;
  enterpriseId?: number;
  staffStatus?: string;
  areaCode?: string;
}

// 燃气安全员
export interface GasSafetyOfficer {
  id?: number;
  name?: string;
  gender?: string;
  idCardNo?: string;
  idCardFrontImage?: string;
  idCardBackImage?: string;
  education?: string;
  phone?: string;
  qualificationNo?: string;
  area1?: string;
  area2?: string;
  area3?: string;
  area4?: string;
  area5?: string;
  area1Name?: string;
  area2Name?: string;
  area3Name?: string;
  area4Name?: string;
  area5Name?: string;
  enterpriseId?: number;
  enterpriseName?: string;
  staffStatus?: string;
  onDutyDate?: string;
  offDutyDate?: string;
  createTime?: string;
}

// 创建燃气安全员（multipart/form-data，图片作为文件上传）
export function createGasSafetyOfficer(
  data: Record<string, any>,
  idCardFrontImage?: File,
) {
  const formData = new FormData();
  // 将表单数据拼接为 JSON 字符串放入 RequestPart
  formData.append(
    'data',
    new Blob([JSON.stringify(data)], { type: 'application/json' }),
  );
  if (idCardFrontImage) {
    formData.append('idCardFrontImage', idCardFrontImage);
  }
  return requestClient.post<any>('/carbon/gas-safety-officer/create', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 更新燃气安全员（multipart/form-data，图片作为文件上传）
export function updateGasSafetyOfficer(
  data: Record<string, any>,
  idCardFrontImage?: File,
) {
  const formData = new FormData();
  formData.append(
    'data',
    new Blob([JSON.stringify(data)], { type: 'application/json' }),
  );
  if (idCardFrontImage) {
    formData.append('idCardFrontImage', idCardFrontImage);
  }
  return requestClient.put<any>('/carbon/gas-safety-officer/update', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 删除燃气安全员
export function deleteGasSafetyOfficer(id: number) {
  return requestClient.delete<any>(
    `/carbon/gas-safety-officer/delete?id=${id}`,
  );
}

// 获得燃气安全员分页
export function getGasSafetyOfficerPage(params: GasSafetyOfficerPageParam) {
  return requestClient.get<any>('/carbon/gas-safety-officer/page', {
    params,
  });
}

// 获得燃气安全员详情
export function getGasSafetyOfficer(id: number) {
  return requestClient.get<any>(`/carbon/gas-safety-officer/get?id=${id}`);
}

// 获得指定企业的安全员列表
export function getGasSafetyOfficerListByEnterpriseId(enterpriseId: number) {
  return requestClient.get<any>(
    `/carbon/gas-safety-officer/list-by-enterprise?enterpriseId=${enterpriseId}`,
  );
}

// 导出燃气安全员
export function exportGasSafetyOfficer(params: GasSafetyOfficerPageParam) {
  return requestClient.download('/carbon/gas-safety-officer/export-excel', { params });
}
