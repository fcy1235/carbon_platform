package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@TableName("carbon_factor_lib")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonFactorLibDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String factorCode;

    private String factorName;

    private String relatedDoc;

    private String emissionSource;

    private String unit;

    private BigDecimal factorValue;
}
