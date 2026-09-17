package cn.iocoder.power.module.carbon.service.impl;

import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselinePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoRespVO;
import cn.iocoder.power.module.carbon.convert.CarbonBaselineConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonBaselineDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonBaselineMapper;
import cn.iocoder.power.module.carbon.enums.ClimateZoneEnum;
import cn.iocoder.power.module.carbon.service.CarbonBaselineService;
import cn.iocoder.power.module.carbon.util.AreaUtils;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.power.module.carbon.dal.redis.RedisKeyConstants.CARBON_BASELINE;

import java.util.List;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.BASELINE_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class CarbonBaselineServiceImpl implements CarbonBaselineService {

    @Resource
    private CarbonBaselineMapper baselineMapper;

    @Resource
    private AreaUtils areaUtils;

    @Override
    @CacheEvict(cacheNames = CARBON_BASELINE, allEntries = true)
    public Long createBaseline(CarbonBaselineSaveReqVO createReqVO) {
        CarbonBaselineDO baseline = CarbonBaselineConvert.INSTANCE.convert(createReqVO);
        baselineMapper.insert(baseline);
        return baseline.getId();
    }

    @Override
    @CacheEvict(cacheNames = CARBON_BASELINE, allEntries = true)
    public void updateBaseline(CarbonBaselineSaveReqVO updateReqVO) {
        validateBaselineExists(updateReqVO.getId());
        CarbonBaselineDO updateObj = CarbonBaselineConvert.INSTANCE.convert(updateReqVO);
        log.info("更新基准线，id={}, provinceCode={}, cityCode={}, districtCode={}",
                updateReqVO.getId(), updateReqVO.getProvinceCode(),
                updateReqVO.getCityCode(), updateReqVO.getDistrictCode());
        baselineMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = CARBON_BASELINE, allEntries = true)
    public void deleteBaseline(Long id) {
        validateBaselineExists(id);
        baselineMapper.deleteById(id);
    }

    @Override
    @Cacheable(cacheNames = CARBON_BASELINE, key = "#id")
    public CarbonBaselineRespVO getBaseline(Long id) {
        return CarbonBaselineConvert.INSTANCE.convert(baselineMapper.selectById(id));
    }

    @Override
    public PageResult<CarbonBaselineRespVO> getBaselinePage(CarbonBaselinePageReqVO pageReqVO) {
        PageResult<CarbonBaselineRespVO> baselineRespList = CarbonBaselineConvert.INSTANCE.convertPage(baselineMapper.selectPage(pageReqVO));
        areaUtils.fillDivision(baselineRespList.getList());
        return baselineRespList;
    }

    @Override
    public List<CarbonBaselineRespVO> getBaselineList(CarbonBaselinePageReqVO reqVO) {
        List<CarbonBaselineRespVO> list = CarbonBaselineConvert.INSTANCE.convertList(baselineMapper.selectList(reqVO));
        areaUtils.fillDivision(list);
        // 气候子区代码转中文
        fillClimateZoneName(list);
        return list;
    }

    /**
     * 气候子区代码转中文名称
     */
    private void fillClimateZoneName(List<CarbonBaselineRespVO> list) {
        for (CarbonBaselineRespVO vo : list) {
            if (vo.getReformType() != null) {
                for (ClimateZoneEnum enumVal : ClimateZoneEnum.values()) {
                    if (enumVal.getType().equals(vo.getReformType())) {
                        vo.setClimateZone(enumVal.getName());
                        break;
                    }
                }
            }
        }
    }

    @VisibleForTesting
    void validateBaselineExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonBaselineDO baseline = baselineMapper.selectById(id);
        if (baseline == null) {
            throw exception(BASELINE_NOT_EXISTS);
        }
    }
}
