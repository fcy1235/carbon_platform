package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerAuditLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonLedgerAuditLogMapper extends BaseMapperX<CarbonLedgerAuditLogDO> {

    default List<CarbonLedgerAuditLogDO> selectListByReportId(Long reportId) {
        return selectList(new LambdaQueryWrapperX<CarbonLedgerAuditLogDO>()
                .eq(CarbonLedgerAuditLogDO::getReportId, reportId)
                .orderByDesc(CarbonLedgerAuditLogDO::getChangeTime));
    }
}
