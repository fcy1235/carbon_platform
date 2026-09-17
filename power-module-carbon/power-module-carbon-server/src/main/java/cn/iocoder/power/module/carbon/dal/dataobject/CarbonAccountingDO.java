package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("carbon_accounting")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonAccountingDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long carbonUserInfoId;

    private LocalDate accountingPeriodStart;

    private LocalDate accountingPeriodEnd;

    private BigDecimal actualEmission;

    private BigDecimal reduction;
}
