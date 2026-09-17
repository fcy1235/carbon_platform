package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.power.framework.excel.core.annotations.DictFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 设备管理 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonDeviceExcelVO {

    @ExcelProperty("设备编码")
    private String deviceCode;

    @ExcelProperty("用户姓名")
    private String username;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("行政区划")
    private String division;

    @ExcelProperty("详细地址")
    private String address;

    @ExcelProperty(value = "改造类别")
    @DictFormat("carbon_transformation_type")
    private String reformType;


    @ExcelProperty("乡镇/街道")
    private String townName;

    @ExcelProperty("村/社区")
    private String villageName;

    @ExcelProperty("燃气用户编码")
    private String gasUserCode;

    @ExcelProperty("燃气表具号")
    private String gasId;

    @ExcelProperty("电表号")
    private String electricityId;

    @ExcelProperty("设备名称")
    private String deviceName;

    @ExcelProperty("设备类型")
    @DictFormat("carbon_device_type")
    private String deviceType;

    @ExcelProperty("生产厂家")
    private String manufacturer;

    @ExcelProperty("生产日期")
    private String productionDate;

    @ExcelProperty("使用寿命(年)")
    private Integer serviceLife;

    @ExcelProperty("安装日期")
    private String installDate;

    @ExcelProperty("投运日期")
    private String operationDate;

    @ExcelProperty("停运日期")
    private String stopDate;

    @ExcelProperty("设备状态")
    @DictFormat("carbon_device_status")
    private String status;

    @ExcelProperty("企业信用代码")
    private String enterpriseCreditCode;

    @ExcelProperty("供气企业名称")
    private String gasEnterpriseName;

    @ExcelProperty("壁挂炉安装年份")
    private String wallMountedStoveInstallYear;

    @ExcelProperty("燃气表类型")
    @DictFormat("carbon_gas_meter_type")
    private String gasMeterType;

    @ExcelProperty("安全装置配备情况")
    @DictFormat("carbon_safe_device_status")
    private String safetyDeviceStatus;

    @ExcelProperty("设备品牌及型号")
    private String deviceBrandModel;

    @ExcelProperty("备注")
    private String remark;
}
