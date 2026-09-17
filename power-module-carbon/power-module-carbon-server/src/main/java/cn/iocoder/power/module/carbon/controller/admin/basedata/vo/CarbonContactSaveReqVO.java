package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "管理后台 - 联系人创建/修改 Request VO")
@Data
public class CarbonContactSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "联系人1")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "联系电话", example = "13900130001")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "邮箱", example = "contact1@test.com")
    private String email;

    @Schema(description = "所属公司", example = "公司1")
    private String company;

    @Schema(description = "职位", example = "职位1")
    private String position;
}
