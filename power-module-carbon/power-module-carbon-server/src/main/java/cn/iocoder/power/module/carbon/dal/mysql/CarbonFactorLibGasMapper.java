package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibGasDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CarbonFactorLibGasMapper extends BaseMapperX<CarbonFactorLibGasDO> {

    default List<CarbonFactorLibGasDO> selectListByFactorLibId(Long factorLibId) {
        return selectList(new LambdaQueryWrapperX<CarbonFactorLibGasDO>()
                .eq(CarbonFactorLibGasDO::getFactorLibId, factorLibId)
                .orderByAsc(CarbonFactorLibGasDO::getId));
    }

    default void deleteByFactorLibId(Long factorLibId) {
        delete(new LambdaQueryWrapperX<CarbonFactorLibGasDO>()
                .eq(CarbonFactorLibGasDO::getFactorLibId, factorLibId));
    }
}
