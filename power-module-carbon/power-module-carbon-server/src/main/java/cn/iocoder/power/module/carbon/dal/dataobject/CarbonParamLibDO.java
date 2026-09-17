package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@TableName("carbon_param_lib")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonParamLibDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String paramCode;

    private String paramName;

    private BigDecimal paramValue;

    private String unit;

    private String category;

    private String remark;
}
