package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonParamLibPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonParamLibDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarbonParamLibMapper extends BaseMapperX<CarbonParamLibDO> {

    default CarbonParamLibDO selectByParamCode(String paramCode) {
        return selectOne(CarbonParamLibDO::getParamCode, paramCode);
    }

    default PageResult<CarbonParamLibDO> selectPage(CarbonParamLibPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonParamLibDO>()
                .likeIfPresent(CarbonParamLibDO::getParamName, reqVO.getParamName())
                .eqIfPresent(CarbonParamLibDO::getCategory, reqVO.getCategory())
                .orderByDesc(CarbonParamLibDO::getId));
    }
}
