package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonEmissionSourceConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonEmissionSourceDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonEmissionSourceMapper;
import cn.iocoder.power.module.carbon.enums.EmissionScopeEnum;
import cn.iocoder.power.module.carbon.service.CarbonEmissionSourceService;
import cn.iocoder.power.module.carbon.service.CarbonFactorLibService;
import cn.iocoder.power.module.carbon.util.GenerateCode;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.power.module.carbon.dal.redis.RedisKeyConstants.CARBON_EMISSION_SOURCE;

import java.util.List;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;
import static cn.iocoder.power.module.infra.enums.ErrorCodeConstants.CARBON_EMISSION_CODE_USED;

@Service
@Validated
@Slf4j
public class CarbonEmissionSourceServiceImpl implements CarbonEmissionSourceService {

    @Resource
    private CarbonEmissionSourceMapper emissionSourceMapper;

    @Resource
    private CarbonFactorLibService carbonFactorLibService;

    @Override
    @CacheEvict(cacheNames = CARBON_EMISSION_SOURCE, allEntries = true)
    public Long createEmissionSource(CarbonEmissionSourceSaveReqVO createReqVO) {
        CarbonEmissionSourceDO emissionSource = CarbonEmissionSourceConvert.INSTANCE.convert(createReqVO);
        if(emissionSource.getSourceCode().isEmpty()){
            emissionSource.setSourceCode(generateSourceCode());
        }
        emissionSourceMapper.insert(emissionSource);
        return emissionSource.getId();
    }

    @Override
    @CacheEvict(cacheNames = CARBON_EMISSION_SOURCE, allEntries = true)
    public void updateEmissionSource(CarbonEmissionSourceSaveReqVO updateReqVO) {
        validateEmissionSourceExists(updateReqVO.getId());
        CarbonEmissionSourceDO updateObj = CarbonEmissionSourceConvert.INSTANCE.convert(updateReqVO);
        emissionSourceMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = CARBON_EMISSION_SOURCE, allEntries = true)
    public void deleteEmissionSource(Long id) {
        validateEmissionSourceExists(id);
        // 查询
        CarbonEmissionSourceDO emissionSource = emissionSourceMapper.selectById(id);

        // 判断是否在排放因子库中是否使用
        CarbonFactorLibReqVO carbonFactorLibReqVO = new CarbonFactorLibReqVO();
        carbonFactorLibReqVO.setEmissionSource(emissionSource.getSourceCode());
        if (!carbonFactorLibService.getFactorLibList(carbonFactorLibReqVO).isEmpty()) {
            throw exception(CARBON_EMISSION_CODE_USED);
        }

        emissionSourceMapper.deleteById(id);
    }

    @Override
    @Cacheable(cacheNames = CARBON_EMISSION_SOURCE, key = "#id")
    public CarbonEmissionSourceRespVO getEmissionSource(Long id) {
        return CarbonEmissionSourceConvert.INSTANCE.convert(emissionSourceMapper.selectById(id));
    }

    @Override
    public PageResult<CarbonEmissionSourceRespVO> getEmissionSourcePage(CarbonEmissionSourcePageReqVO pageReqVO) {
        return CarbonEmissionSourceConvert.INSTANCE.convertPage(emissionSourceMapper.selectPage(pageReqVO));
    }

    @VisibleForTesting
    void validateEmissionSourceExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonEmissionSourceDO emissionSource = emissionSourceMapper.selectById(id);
        if (emissionSource == null) {
            throw exception(EMISSION_SOURCE_NOT_EXISTS);
        }
    }

    public String generateSourceCode() {
        String prefix = "ES" ;
        CarbonEmissionSourceDO latestSource = emissionSourceMapper.selectLatestOne();
        String lastCode = latestSource != null ? latestSource.getSourceCode() : null;
        return GenerateCode.generateCode(prefix, lastCode);
    }

    @Override
    public List<CarbonEmissionSourceRespVO> getEmissionSourceList(CarbonEmissionSourceReqVO reqVO) {
        List<CarbonEmissionSourceDO> list = emissionSourceMapper.selectList(reqVO);
        return CarbonEmissionSourceConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<CarbonEmissionSourceRespVO> getEmissionSourceListByPage(CarbonEmissionSourcePageReqVO pageReqVO) {
        // 将 PageReqVO 转换为 ReqVO，保留筛选条件和 ids
        CarbonEmissionSourceReqVO reqVO = new CarbonEmissionSourceReqVO();
        reqVO.setIds(pageReqVO.getIds());
        reqVO.setSourceCode(pageReqVO.getSourceCode());
        reqVO.setSourceName(pageReqVO.getSourceName());
        reqVO.setScope(pageReqVO.getScope());
        List<CarbonEmissionSourceDO> list = emissionSourceMapper.selectList(reqVO);
        List<CarbonEmissionSourceRespVO> voList = CarbonEmissionSourceConvert.INSTANCE.convertList(list);
        // 排放范围代码转中文
        fillScopeName(voList);
        return voList;
    }

    /**
     * 排放范围代码转中文名称
     */
    private void fillScopeName(List<CarbonEmissionSourceRespVO> voList) {
        for (CarbonEmissionSourceRespVO vo : voList) {
            if (vo.getScope() != null) {
                for (EmissionScopeEnum enumVal : EmissionScopeEnum.values()) {
                    if (enumVal.getType().equals(vo.getScope())) {
                        vo.setScope(enumVal.getName());
                        break;
                    }
                }
            }
        }
    }
}
