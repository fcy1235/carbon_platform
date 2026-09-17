package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerReportDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 台账上报文件关联 Mapper（已废弃，表已移除，保留接口兼容）
 */
@Mapper
public interface CarbonLedgerReportFileMapper {

    /**
     * 统计关联指定台账文件的上报数量
     * 表已移除，始终返回0
     */
    default Long countByFileId(Long fileId) {
        return 0L;
    }
}