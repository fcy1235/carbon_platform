package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户基本信息 Response VO")
@Data
public class CarbonUserInfoRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "用户编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "USER20260101001")
    private String userCode;

    @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String username;

    @Schema(description = "身份证号", example = "130102199001010001")
    private String idCard;

    @Schema(description = "联系电话", example = "13800130001")
    private String phone;

    @Schema(description = "行政区划-省", example = "111")
    private Long provinceCode;

    @Schema(description = "行政区划-市", example = "111")
    private Long cityCode;

    @Schema(description = "行政区划-区", example = "111")
    private Long districtCode;

    @Schema(description = "行政区划-乡镇/街道", example = "111")
    private Long townCode;

    @Schema(description = "行政区划-村/社区", example = "111")
    private Long villageCode;

    @Schema(description = "省名称", example = "河北省")
    private String provinceName;

    @Schema(description = "市名称", example = "石家庄市")
    private String cityName;

    @Schema(description = "区名称", example = "裕华区")
    private String districtName;

    @Schema(description = "乡镇/街道名称", example = "裕强街道")
    private String townName;

    @Schema(description = "村/社区名称", example = "南王村")
    private String villageName;

    @Schema(description = "所属行政区", example = "河北省石家庄市裕华区裕强街道南王村")
    private String division;

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

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "修改时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;
}
