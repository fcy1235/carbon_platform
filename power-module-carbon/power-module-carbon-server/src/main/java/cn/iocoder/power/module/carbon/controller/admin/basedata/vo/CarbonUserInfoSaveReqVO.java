package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 用户基本信息创建/修改 Request VO")
@Data
public class CarbonUserInfoSaveReqVO {

    @Schema(description = "用户编号", example = "1024")
    private Long id;

    @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "用户姓名不能为空")
    private String username;

    @Schema(description = "身份证号", example = "130102199001010001")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    private String idCard;

    @Schema(description = "联系电话", example = "13800130001")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "行政区划-省", example = "1")
    private Long provinceCode;

    @Schema(description = "行政区划-市", example = "2")
    private Long cityCode;

    @Schema(description = "行政区划-区", example = "3")
    private Long districtCode;

    @Schema(description = "行政区划-乡镇/街道", example = "4")
    private Long townCode;

    @Schema(description = "行政区划-村/社区", example = "5")
    private Long villageCode;

    @Schema(description = "详细地址", example = "测试地址1号")
    private String address;

    @Schema(description = "供暖面积", example = "80")
    private BigDecimal heatingArea;

    @Schema(description = "改造类别 1电代煤 2气代煤", example = "1")
    private String reformType;

    @Schema(description = "改造类型", example = "1")
    private String reformMode;

    @Schema(description = "数据来源 1设备数据 2接口数据", example = "1")
    private String dataSource;

    @Schema(description = "电力户号", example = "E10000")
    private String electricityId;

    @Schema(description = "燃气户号", example = "G20000")
    private String gasId;

    @Schema(description = "改造年份", example = "2024")
    private String reformYear;

    @Schema(description = "改造批次", example = "1")
    private String reformBatch;

    @Schema(description = "发放补贴方式", example = "1")
    private String subsidyMethod;

    @Schema(description = "房屋用途", example = "1")
    private String houseUsage;

    @Schema(description = "用户分类", example = "1")
    private String userCategory;

    @Schema(description = "燃气用户编码-改造类型为气代煤时显示", example = "GAS20240101")
    private String gasUserCode;

    @Schema(description = "使用状态 1正常 2销户", example = "1")
    private String useStatus;

    @Schema(description = "备注", example = "")
    private String remark;
}
