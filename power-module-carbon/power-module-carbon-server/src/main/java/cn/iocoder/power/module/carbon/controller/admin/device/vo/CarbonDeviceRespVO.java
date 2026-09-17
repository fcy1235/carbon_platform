package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 设备管理 Response VO")
@Data
public class CarbonDeviceRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "设备编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "EQ20260101001")
    private String deviceCode;

    @Schema(description = "用户信息编号",  example = "1024")
    private Long carbonUserInfoId;

    @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String username;

    @Schema(description = "身份证号", example = "130102199001010001")
    private String idCard;

    @Schema(description = "联系电话", example = "13800130001")
    private String phone;

    @Schema(description = "行政区划", requiredMode = Schema.RequiredMode.REQUIRED, example = "河北省石家庄市裕华区裕强街道")
    private String division;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "测试地址1号")
    private String address;

    @Schema(description = "改造类别 1电代煤 2气代煤", example = "1")
    private String reformType;

    @Schema(description = "改造类型（改造类别为气代煤时显示）", example = "1")
    private String reformMode;

    @Schema(description = "乡镇/街道", example = "裕强街道")
    private String townName;

    @Schema(description = "村/社区", example = "南王村")
    private String villageName;

    @Schema(description = "燃气用户编码", example = "GAS20240101")
    private String gasUserCode;

    @Schema(description = "燃气表具号", example = "G20000")
    private String gasId;

    @Schema(description = "电表号", example = "E10000")
    private String electricityId;

    @Schema(description = "设备名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备1")
    private String deviceName;

    @Schema(description = "设备类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "电表")
    private String deviceType;

    @Schema(description = "生产厂家", example = "厂家1")
    private String manufacturer;

    @Schema(description = "生产日期", example = "2023-01-01")
    private LocalDate productionDate;

    @Schema(description = "使用寿命(年)", example = "10")
    private Integer serviceLife;

    @Schema(description = "安装日期", example = "2023-03-01")
    private LocalDate installDate;

    @Schema(description = "投运日期", example = "2023-03-15")
    private LocalDate operationDate;

    @Schema(description = "停运日期", example = "")
    private LocalDate stopDate;

    @Schema(description = "设备状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "正常")
    private String status;

    @Schema(description = "供气企业ID", example = "1")
    private Long gasEnterpriseId;

    @Schema(description = "企业信用代码", example = "91130100MA0XXXXX")
    private String enterpriseCreditCode;

    @Schema(description = "供气企业名称", example = "XX燃气公司")
    private String gasEnterpriseName;

    @Schema(description = "壁挂炉安装年份", example = "2023")
    private String wallMountedStoveInstallYear;

    @Schema(description = "燃气表类型 1物联网燃气表（无线远传） 2IC卡燃气表 3传统机器膜式燃气表 4其他", example = "1")
    private String gasMeterType;

    @Schema(description = "安全装置配备情况 1报警器-切断阀 2自闭阀 3以上两者都有", example = "1")
    private String safetyDeviceStatus;

    @Schema(description = "设备品牌及型号", example = "威星V300")
    private String deviceBrandModel;

    @Schema(description = "备注", example = "")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    // 临时字段，用于行政区划名称填充
    @Schema(description = "省编码", hidden = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long provinceCode;
    @Schema(description = "市编码", hidden = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long cityCode;
    @Schema(description = "区编码", hidden = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long districtCode;
}
