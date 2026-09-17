package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 设备信息导入 Excel VO
 *
 * 与「设备信息-导入模板.xlsx」的列一一对应（按列顺序做索引映射，避免依赖表头文字）：
 * 0-序号、1-所属城市、2-所属区县、3-所属乡镇、4-所属村名称、5-地址、6-户主姓名、7-身份证号、
 * 8-用户编码、9-改造类别、10-供气企业、11-企业信用代码、12-壁挂炉安装年份、13-燃气表具号、
 * 14-燃气表品牌及型号、15-燃气表类型、16-安全装置配备情况
 */
@Data
public class CarbonDeviceImportVO {

    @ExcelProperty(index = 0)
    private String index;

    @ExcelProperty(index = 1)
    private String cityName;

    @ExcelProperty(index = 2)
    private String districtName;

    @ExcelProperty(index = 3)
    private String townName;

    @ExcelProperty(index = 4)
    private String villageName;

    @ExcelProperty(index = 5)
    private String address;

    @ExcelProperty(index = 6)
    private String username;

    @ExcelProperty(index = 7)
    private String idCard;

    @ExcelProperty(index = 8)
    private String userCode;

    @ExcelProperty(index = 9)
    private String reformType;

    @ExcelProperty(index = 10)
    private String enterpriseName;

    @ExcelProperty(index = 11)
    private String enterpriseCreditCode;

    @ExcelProperty(index = 12)
    private String wallMountedStoveInstallYear;

    @ExcelProperty(index = 13)
    private String gasId;

    @ExcelProperty(index = 14)
    private String deviceBrandModel;

    @ExcelProperty(index = 15)
    private String gasMeterType;

    @ExcelProperty(index = 16)
    private String safetyDeviceStatus;
}
