package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("carbon_contact")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonContactDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;


    private String name;

    private String phone;

    private String email;

    private String company;

    private String position;
}
