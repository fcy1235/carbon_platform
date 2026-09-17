package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@TableName("carbon_project_doc")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonProjectDocDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String docName;

    private String docDesc;

    private String docSize;

    private String docPath;

    private String uploader;

    private LocalDateTime uploadTime;
}
