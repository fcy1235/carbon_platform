package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@TableName("carbon_accounting_activity")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonAccountingActivityDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long accountingId;

    private String activityNameCode;

    private BigDecimal activityLevel;

    private BigDecimal emissionFactor;

    private BigDecimal emission;

    private Long carbonDeviceId;
}
