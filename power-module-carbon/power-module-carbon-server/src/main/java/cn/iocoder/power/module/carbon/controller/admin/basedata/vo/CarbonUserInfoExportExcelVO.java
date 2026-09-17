package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.iocoder.power.framework.excel.core.annotations.DictFormat;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 用户基本信息 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonUserInfoExportExcelVO {

    @ExcelProperty("用户编码")
    private String userCode;

    @ExcelProperty("用户姓名")
    private String username;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("联系电话")
    private String phone;

    @Schema(description = "所属行政区", example = "河北省石家庄市裕华区裕强街道南王村")
    private String division;

    @ExcelProperty("详细地址")
    private String address;

    @ExcelProperty("供暖面积")
    private BigDecimal heatingArea;

    @ExcelProperty(value = "改造类别")
    @DictFormat("carbon_reform_type")
    private String reformType;

    @ExcelProperty(value = "改造类型")
    @DictFormat("carbon_reform_mode")
    private String reformMode;

    @ExcelProperty(value = "数据来源")
    @DictFormat("carbon_user_data_source")
    private String dataSource;

    @ExcelProperty("电力户号")
    private String electricityId;

    @ExcelProperty("燃气户号")
    private String gasId;

    @ExcelProperty("改造年份")
    private String reformYear;

    @ExcelProperty(value = "改造批次")
    @DictFormat("carbon_reform_batch")
    private String reformBatch;

    @ExcelProperty(value = "发放补贴方式")
    @DictFormat("carbon_subsidy_method")
    private String subsidyMethod;

    @ExcelProperty(value = "房屋用途")
    @DictFormat("carbon_house_usage")
    private String houseUsage;

    @ExcelProperty(value = "用户分类")
    @DictFormat("carbon_user_category")
    private String userCategory;

    @ExcelProperty("燃气用户编码")
    private String gasUserCode;

    @ExcelProperty(value = "使用状态")
    @DictFormat("carbon_use_status")
    private String useStatus;

    @ExcelProperty("备注")
    private String remark;
}
