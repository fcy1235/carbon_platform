package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 用户基本信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonUserInfoPageReqVO extends PageParam {

    @Schema(description = "用户信息编号", example = "1024")
    private Long id;

    @Schema(description = "用户姓名", example = "张三")
    private String username;

    @Schema(description = "身份证号", example = "130102199001010001")
    private String idCard;

    @Schema(description = "联系电话", example = "13800130001")
    private String phone;

    @Schema(description = "行政区划-省", example = "111")
    private Long provinceCode;

    @Schema(description = "行政区划-市", example = "2222")
    private Long cityCode;

    @Schema(description = "行政区划-区", example = "1111")
    private Long districtCode;

    @Schema(description = "行政区划-乡镇/街道", example = "1111")
    private Long townCode;

    @Schema(description = "行政区划-村/社区", example = "1111")
    private Long villageCode;

    @Schema(description = "改造类别", example = "1")
    private String reformType;

    @Schema(description = "改造类型", example = "1")
    private String reformMode;

    @Schema(description = "数据来源", example = "1")
    private String dataSource;

    @Schema(description = "改造年份", example = "2024")
    private String reformYear;

    @Schema(description = "改造批次", example = "1")
    private String reformBatch;

    @Schema(description = "用户分类", example = "1")
    private String userCategory;

    @Schema(description = "使用状态", example = "1")
    private String useStatus;
}
