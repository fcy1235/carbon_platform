package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonGasInfoPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonGasInfoReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasInfoDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonGasInfoMapper extends BaseMapperX<CarbonGasInfoDO> {

    default CarbonGasInfoDO selectByGasCode(String gasCode) {
        return selectOne(CarbonGasInfoDO::getGasCode, gasCode);
    }

    default PageResult<CarbonGasInfoDO> selectPage(CarbonGasInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonGasInfoDO>()
                .likeIfPresent(CarbonGasInfoDO::getGasCode, reqVO.getGasCode())
                .likeIfPresent(CarbonGasInfoDO::getGasName, reqVO.getGasName())
                .eqIfPresent(CarbonGasInfoDO::getCategory, reqVO.getCategory())
                .orderByDesc(CarbonGasInfoDO::getId));
    }

   default CarbonGasInfoDO selectLatestOne(){
        return selectOne(new LambdaQueryWrapperX<CarbonGasInfoDO>()
                .orderByDesc(CarbonGasInfoDO::getId)
                .last("limit 1"));
   }

   default List<CarbonGasInfoDO> selectList(CarbonGasInfoReqVO reqVO){
        return selectList(new LambdaQueryWrapperX<CarbonGasInfoDO>()
                .inIfPresent(CarbonGasInfoDO::getId, reqVO.getIds())
                .likeIfPresent(CarbonGasInfoDO::getGasName, reqVO.getGasName())
                .likeIfPresent(CarbonGasInfoDO::getGasCode, reqVO.getGasCode())
                .inIfPresent(CarbonGasInfoDO::getGasCode, reqVO.getGasCodes())
                .eqIfPresent(CarbonGasInfoDO::getCategory, reqVO.getCategory())
                .orderByDesc(CarbonGasInfoDO::getCreateTime)
        ) ;
   }
}
