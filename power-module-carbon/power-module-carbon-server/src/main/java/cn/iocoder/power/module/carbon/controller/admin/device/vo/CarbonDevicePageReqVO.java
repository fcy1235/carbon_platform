package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.List;

@Schema(description = "管理后台 - 设备管理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CarbonDevicePageReqVO extends PageParam {

    @Schema(description = "用户姓名", example = "张三")
    private String username;

    @Schema(description = "用户编号", example = "1")
    private Long carbonUserInfoId;

    @Schema(description = "用户编号集合")
    private Collection<Long> carbonUserInfoIds;

    @Schema(description = "设备名称", example = "设备1")
    private String deviceName;

    @Schema(description = "设备类型", example = "电表")
    private String deviceType;

    @Schema(description = "设备品牌及型号", example = "威星V300")
    private String deviceBrandModel;

    @Schema(description = "生产厂家", example = "厂家1")
    private String manufacturer;

    @Schema(description = "设备状态", example = "正常")
    private String status;

    @Schema(description = "行政区划-省", example = "11")
    private Long provinceCode;

    @Schema(description = "行政区划-市", example = "1111")
    private Long cityCode;

    @Schema(description = "行政区划-区", example = "111111")
    private Long districtCode;

    @Schema(description = "改造类型", example = "1,2")
    private String reformType;

    @Schema(description = "改造类别", example = "1,2,4")
    private String reformMode;

    @Schema(description = "选中的ID列表，用于选中导出", example = "1,2,3")
    private List<Long> ids;

    @Schema(description = "是否能碳设备", example = "1:是 0:否")
    private Integer isCarbon;
}
