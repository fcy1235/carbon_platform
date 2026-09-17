package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonEmissionSourcePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonEmissionSourceReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonEmissionSourceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonEmissionSourceMapper extends BaseMapperX<CarbonEmissionSourceDO> {

    default CarbonEmissionSourceDO selectBySourceCode(String sourceCode) {
        return selectOne(CarbonEmissionSourceDO::getSourceCode, sourceCode);
    }

    default PageResult<CarbonEmissionSourceDO> selectPage(CarbonEmissionSourcePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonEmissionSourceDO>()
                .likeIfPresent(CarbonEmissionSourceDO::getSourceCode, reqVO.getSourceCode())
                .likeIfPresent(CarbonEmissionSourceDO::getSourceName, reqVO.getSourceName())
                .eqIfPresent(CarbonEmissionSourceDO::getScope, reqVO.getScope())
                .orderByDesc(CarbonEmissionSourceDO::getId));
    }

    default CarbonEmissionSourceDO selectLatestOne(){
        return selectOne(new LambdaQueryWrapperX<CarbonEmissionSourceDO>()
                .orderByDesc(CarbonEmissionSourceDO::getId)
                .last("LIMIT 1"));
    }

    default List<CarbonEmissionSourceDO> selectList(CarbonEmissionSourceReqVO reqVO){
        return selectList(new LambdaQueryWrapperX<CarbonEmissionSourceDO>()
                .inIfPresent(CarbonEmissionSourceDO::getId, reqVO.getIds())
                .eqIfPresent(CarbonEmissionSourceDO::getSourceCode, reqVO.getSourceCode())
                .likeIfPresent(CarbonEmissionSourceDO::getSourceName, reqVO.getSourceName())
                .eqIfPresent(CarbonEmissionSourceDO::getScope, reqVO.getScope())
                .orderByDesc(CarbonEmissionSourceDO::getCreateTime));
    }
}
