package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonFactorLibPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonFactorLibReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonEmissionSourceDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonFactorLibMapper extends BaseMapperX<CarbonFactorLibDO> {

    default CarbonFactorLibDO selectByFactorCode(String factorCode) {
        return selectOne(CarbonFactorLibDO::getFactorCode, factorCode);
    }

    default PageResult<CarbonFactorLibDO> selectPage(CarbonFactorLibPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonFactorLibDO>()
                .likeIfPresent(CarbonFactorLibDO::getFactorName, reqVO.getFactorName())
                .likeIfPresent(CarbonFactorLibDO::getEmissionSource, reqVO.getEmissionSource())
                .orderByDesc(CarbonFactorLibDO::getId));
    }

   default List<CarbonFactorLibDO> selectList(CarbonFactorLibReqVO carbonFactorLibReqVO){
        return selectList(new LambdaQueryWrapperX<CarbonFactorLibDO>()
                .eqIfPresent(CarbonFactorLibDO::getFactorCode, carbonFactorLibReqVO.getFactorCode())
                .likeIfPresent(CarbonFactorLibDO::getFactorName, carbonFactorLibReqVO.getFactorName())
                .eqIfPresent(CarbonFactorLibDO::getEmissionSource, carbonFactorLibReqVO.getEmissionSource())
                .orderByDesc(CarbonFactorLibDO::getCreateTime))
                ;
   }

   default CarbonFactorLibDO selectLatestOne(){
        return selectOne(new LambdaQueryWrapperX<CarbonFactorLibDO>()
                .orderByDesc(CarbonFactorLibDO::getCreateTime)
                .last("limit 1"));
   }
}
