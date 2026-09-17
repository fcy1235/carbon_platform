package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselinePageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonBaselineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonBaselineMapper extends BaseMapperX<CarbonBaselineDO> {

    default PageResult<CarbonBaselineDO> selectPage(CarbonBaselinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonBaselineDO>()
                .eqIfPresent(CarbonBaselineDO::getProvinceCode, reqVO.getProvinceCode())
                .eqIfPresent(CarbonBaselineDO::getCityCode, reqVO.getCityCode())
                .eqIfPresent(CarbonBaselineDO::getDistrictCode, reqVO.getDistrictCode())
                .eqIfPresent(CarbonBaselineDO::getReformType, reqVO.getReformType())
                .orderByDesc(CarbonBaselineDO::getId));
    }

    default List<CarbonBaselineDO> selectList(CarbonBaselinePageReqVO reqVO) {
        LambdaQueryWrapperX<CarbonBaselineDO> wrapper = new LambdaQueryWrapperX<CarbonBaselineDO>()
                .inIfPresent(CarbonBaselineDO::getId, reqVO.getIds());
        // 行政区划条件三选一：districtCode(单值) / cityCodes(市列表) / provinceCode(单值)，
        // 按顺序取第一个非空即止，其余两个不参与查询（避免同义条件叠加影响走索引）
        if (CollUtil.isNotEmpty(reqVO.getDistrictCodes())) {
            wrapper.in(CarbonBaselineDO::getDistrictCode, reqVO.getDistrictCodes());
        } else if (CollUtil.isNotEmpty(reqVO.getCityCodes())) {
            wrapper.in(CarbonBaselineDO::getCityCode, reqVO.getCityCodes());
        } else if (CollUtil.isNotEmpty(reqVO.getProvinceCodes())) {
            wrapper.in(CarbonBaselineDO::getProvinceCode, reqVO.getProvinceCodes());
        }
        wrapper.eqIfPresent(CarbonBaselineDO::getReformType, reqVO.getReformType());
        return selectList(wrapper.orderByDesc(CarbonBaselineDO::getId));
    }


}
