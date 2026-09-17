package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("carbon_document")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonDocumentDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String docCode;

    private String docTitle;

    private String docName;

    private String applicableBoundary;

    private String formula;

    private String uploader;

    private LocalDateTime uploadTime;

    private String fileUrl;

    private String remark;
}
