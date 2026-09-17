package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 联系人 Response VO")
@Data
public class CarbonContactRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "联系人1")
    private String name;

    @Schema(description = "联系电话", example = "13900130001")
    private String phone;

    @Schema(description = "邮箱", example = "contact1@test.com")
    private String email;

    @Schema(description = "所属公司", example = "公司1")
    private String company;

    @Schema(description = "职位", example = "职位1")
    private String position;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
