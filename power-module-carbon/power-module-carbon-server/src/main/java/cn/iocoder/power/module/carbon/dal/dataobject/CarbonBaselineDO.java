package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@TableName("carbon_baseline")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonBaselineDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long provinceCode;

    private Long cityCode;

    private Long districtCode;

    private String reformType;

    private BigDecimal intensity;

    private String unit;
}
