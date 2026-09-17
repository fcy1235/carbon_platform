package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@TableName("carbon_gas_data")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonGasDataDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dataSource;

    private Long carbonUserInfoId;

    private String gasId;

    private BigDecimal currentTotal;

    private LocalDateTime readingTime;

    private BigDecimal currentUsage;

    private BigDecimal lastReading;
}
