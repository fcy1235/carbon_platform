import type { PageParam } from '@vben/request';

import { requestClient } from '#/api/request';

// 设备分页请求参数
export interface DevicePageParam extends PageParam {
  username?: string; // 用户姓名
  carbonUserInfoId?: number; // 用户编号
  deviceName?: string; // 设备名称
  deviceType?: number; // 设备类型
  manufacturer?: string; // 生产厂家
  status?: number; // 设备状态
  reformType?: string; // 改造类型
  provinceCode?: string; // 省编码
  cityCode?: string; // 市编码
  districtCode?: string; // 区编码
  isCarbon?: number; // 是否碳设备 1-是 0-否
  ids?: number[]; // 选中的ID列表，用于选中导出
}

// 设备
export interface Device {
  id?: number; // 编号
  carbonUserInfoId?: number; // 用户编号
  deviceCode?: string; // 设备编码
  username: string; // 用户姓名
  idCard?: string; // 身份证号
  phone?: string; // 联系电话
  division: string; // 行政区划
  address: string; // 详细地址
  reformType?: string; // 改造类型 1电代煤 2气代煤
  gasUserCode?: string; // 燃气用户编码
  gasId?: string; // 燃气表具号
  electricityId?: string; // 电表号
  deviceName: string; // 设备名称
  deviceType: number; // 设备类型
  manufacturer?: string; // 生产厂家
  productionDate?: string; // 生产日期
  serviceLife?: number; // 使用寿命
  installDate?: string; // 安装日期
  operationDate?: string; // 投运日期
  stopDate?: string; // 停运日期
  status: number; // 设备状态
  gasEnterpriseId?: number; // 供气企业ID
  enterpriseCreditCode?: string; // 企业信用代码
  wallMountedStoveInstallYear?: string; // 壁挂炉安装年份
  gasMeterType?: string; // 燃气表类型
  safetyDeviceStatus?: string; // 安全装置配备情况
  deviceBrandModel?: string; // 设备品牌及型号
  remark?: string; // 备注
}

// 设备状态变更
export interface DeviceStatusUpdate {
  id: number; // 编号
  status: number; // 设备状态
  reason?: string; // 变更原因
}

// 设备简易信息（下拉选择用）
export interface DeviceSimple {
  id?: number; // 设备编号
  deviceCode?: string; // 设备编码
  deviceName?: string; // 设备名称
  deviceType?: string; // 设备类型
  carbonUserInfoId?: number; // 用户编号
  manufacturer?: string; // 生产厂家
  deviceBrandModel?: string; // 设备品牌及型号
  status?: string; // 设备状态
}

// 获得设备简易列表（下拉选择用）
export function getDeviceSimpleList(params: DevicePageParam) {
  return requestClient.get<DeviceSimple[]>('/carbon/device/simple-list', {
    params,
  });
}

// 获得设备分页
export function getDevicePage(params: DevicePageParam) {
  return requestClient.get<any>('/carbon/device/page', { params });
}

// 获得设备
export function getDevice(id: number) {
  return requestClient.get<any>(`/carbon/device/get?id=${id}`);
}

// 新增设备
export function createDevice(data: Device) {
  return requestClient.post<any>('/carbon/device/create', data);
}

// 更新设备
export function updateDevice(data: Device) {
  return requestClient.put<any>('/carbon/device/update', data);
}

// 删除设备
export function deleteDevice(id: number) {
  return requestClient.delete<any>(`/carbon/device/delete?id=${id}`);
}

// 变更设备状态
export function updateDeviceStatus(data: DeviceStatusUpdate) {
  return requestClient.put<any>('/carbon/device/update-status', data);
}

// 导出设备Excel
export function exportDevice(params: DevicePageParam) {
  return requestClient.download('/carbon/device/export-excel', { params });
}

// 生成设备编码（按设备类型前缀：EM-电表 GM-燃气表 ASHP-空气源热泵，规则：前缀-6位序号）
export function generateDeviceCode(prefix: string) {
  return requestClient.get<string>('/carbon/device/getCode', {
    params: { prefix },
  });
}

// 获得设备数据详情（该设备所有上报记录，煤改电设备数据）
export function getDeviceDataDetail(deviceCode: string) {
  return requestClient.get<any>('/carbon/device-data/detail', {
    params: { deviceCode },
  });
}
