package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 导入明细异常项 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonLedgerImportDetailExcelVO {

    @ExcelProperty("用户姓名")
    private String username;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("联系方式")
    private String phone;

    @ExcelProperty("省")
    private String provinceName;

    @ExcelProperty("市")
    private String cityName;

    @ExcelProperty("区县")
    private String districtName;

    @ExcelProperty("乡镇")
    private String townName;

    @ExcelProperty("村")
    private String villageName;

    @ExcelProperty("地址")
    private String address;

    @ExcelProperty("采暖面积(m²)")
    private BigDecimal heatingArea;

    @ExcelProperty("改造类型")
    private String reformType;

    @ExcelProperty("数据来源")
    private String dataSource;

    @ExcelProperty("电力用户编号")
    private String electricityId;

    @ExcelProperty("燃气用户编号")
    private String gasId;

    @ExcelProperty("改造年份")
    private String reformYear;

    @ExcelProperty("改造批次")
    private String reformBatch;

    @ExcelProperty("补贴方式")
    private String subsidyMethod;

    @ExcelProperty("房屋用途")
    private String houseUsage;

    @ExcelProperty("用户类别")
    private String userCategory;

    @ExcelProperty("燃气用户编码")
    private String gasUserCode;

    @ExcelProperty("使用状态")
    private String useStatus;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("导入状态")
    private String importStatus;

    @ExcelProperty("错误信息")
    private String errorMsg;
}
