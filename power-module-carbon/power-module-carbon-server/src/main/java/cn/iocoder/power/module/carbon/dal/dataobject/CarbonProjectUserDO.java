package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("carbon_project_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonProjectUserDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private Long carbonUserInfoId;

    private Long accountingId;

}
