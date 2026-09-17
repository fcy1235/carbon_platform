package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 碳排放核算初始化数据 Response VO")
@Data
public class CarbonAccountingInitRespVO {

    @Schema(description = "用户信息编号")
    private Long carbonUserInfoId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "行政区划")
    private String division;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "改造类别")
    private String reformType;

    @Schema(description = "数据来源")
    private String dataSource;

    @Schema(description = "清洁建筑取暖面积")
    private BigDecimal heatingArea;

    @Schema(description = "省编码")
    private Long provinceCode;

    @Schema(description = "市编码")
    private Long cityCode;

    @Schema(description = "区编码")
    private Long districtCode;

    // ========== 基准线 ==========

    @Schema(description = "区域基准碳排放强度")
    private BigDecimal baselineIntensity;

    @Schema(description = "基准线排放量")
    private BigDecimal baselineEmission;

    // ========== 用量 ==========

    @Schema(description = "用电量")
    private BigDecimal electricityUsage;

    @Schema(description = "用气量")
    private BigDecimal gasUsage;

    // ========== 活动 ==========

    @Schema(description = "活动名称编码")
    private String activityNameCode;

    @Schema(description = "活动水平数据")
    private BigDecimal activityLevel;

    @Schema(description = "排放因子")
    private BigDecimal emissionFactor;

    @Schema(description = "排放因子名称")
    private String emissionFactorName;

    // ========== 计算结果 ==========

    @Schema(description = "实际排放量")
    private BigDecimal actualEmission;

    @Schema(description = "减排量")
    private BigDecimal reduction;

    // ========== 设备列表 ==========

    @Schema(description = "设备列表")
    private List<DeviceItem> deviceList;

    @Data
    @Schema(description = "设备简要信息")
    public static class DeviceItem {

        @Schema(description = "设备编号")
        private Long id;

        @Schema(description = "设备名称")
        private String deviceName;

        @Schema(description = "设备编码")
        private String deviceCode;
    }
}
