package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@TableName("carbon_subsidy")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonSubsidyDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userInfoId;

    private BigDecimal subsidyAmount;

    private String subsidyCode;

}
