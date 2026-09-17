package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 维保网点 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonMaintenanceStationExcelVO {

    @ExcelProperty("所属企业")
    private String enterpriseName;

    @ExcelProperty("网点名称")
    private String stationName;

    @ExcelProperty("服务类型")
    private String serviceType;

    @ExcelProperty("经营状态")
    private String businessStatus;

    @ExcelProperty("维保人员数")
    private Integer maintenanceStaffCount;

    @ExcelProperty("覆盖村数")
    private Integer coveredVillages;

    @ExcelProperty("覆盖户数")
    private Integer coveredHouseholds;

    @ExcelProperty("服务范围")
    private String serviceScope;

    @ExcelProperty("网点负责人")
    private String managerName;

    @ExcelProperty("负责人电话")
    private String managerPhone;
}
