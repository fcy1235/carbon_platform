package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.math.BigDecimal;

@TableName("carbon_project")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonProjectDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String projectCode;

    private String projectName;

    private Long contactId;

    private String projectDesc;

    private Long provinceCode;

    private Long cityCode;

    private Long districtCode;

    private LocalDate planStartDate;

    private LocalDate planEndDate;

    private String projectCycle;

    private String projectStatus;

    private BigDecimal totalReduction;
}
