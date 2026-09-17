package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("carbon_document_detail")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonDocumentDetailDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long documentId;

    private String name;

    private String description;

    private String formula;
}
