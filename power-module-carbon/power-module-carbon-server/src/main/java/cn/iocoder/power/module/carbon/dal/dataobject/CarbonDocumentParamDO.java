package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("carbon_document_param")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonDocumentParamDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long documentId;

    private String paramName;

    private String paramDescription;
}
