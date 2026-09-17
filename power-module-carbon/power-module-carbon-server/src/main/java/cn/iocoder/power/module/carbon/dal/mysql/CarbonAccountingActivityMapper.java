package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonAccountingActivityDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CarbonAccountingActivityMapper extends BaseMapperX<CarbonAccountingActivityDO> {

    default List<CarbonAccountingActivityDO> selectListByAccountingId(Long accountingId) {
        return selectList(new LambdaQueryWrapperX<CarbonAccountingActivityDO>()
                .eq(CarbonAccountingActivityDO::getAccountingId, accountingId)
                .orderByAsc(CarbonAccountingActivityDO::getId));
    }

    default List<CarbonAccountingActivityDO> selectListByAccountingIds(Collection<Long> accountingIds) {
        return selectList(new LambdaQueryWrapperX<CarbonAccountingActivityDO>()
                .in(CarbonAccountingActivityDO::getAccountingId, accountingIds)
                .orderByAsc(CarbonAccountingActivityDO::getId));
    }

    default void deleteByAccountingId(Long accountingId) {
        delete(new LambdaQueryWrapperX<CarbonAccountingActivityDO>()
                .eq(CarbonAccountingActivityDO::getAccountingId, accountingId));
    }

}
