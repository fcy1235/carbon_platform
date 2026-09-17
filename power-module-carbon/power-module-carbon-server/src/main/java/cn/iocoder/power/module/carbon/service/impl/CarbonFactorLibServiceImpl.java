package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonFactorLibConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonEmissionSourceDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibGasDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonFactorLibGasMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonFactorLibMapper;
import cn.iocoder.power.module.carbon.service.CarbonDocumentService;
import cn.iocoder.power.module.carbon.service.CarbonFactorLibService;
import cn.iocoder.power.module.carbon.service.CarbonGasInfoService;
import cn.iocoder.power.module.carbon.util.GenerateCode;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.power.module.carbon.dal.redis.RedisKeyConstants.CARBON_FACTOR_LIB;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class CarbonFactorLibServiceImpl implements CarbonFactorLibService {

    @Resource
    private CarbonFactorLibMapper factorLibMapper;

    @Resource
    private CarbonFactorLibGasMapper factorLibGasMapper;

    @Resource
    private CarbonGasInfoService carbonGasInfoService;

    @Resource
    private CarbonDocumentService carbonDocumentService;

    @Override
    @CacheEvict(cacheNames = CARBON_FACTOR_LIB, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Long createFactorLib(CarbonFactorLibSaveReqVO createReqVO) {
        CarbonFactorLibDO factorLib = CarbonFactorLibConvert.INSTANCE.convert(createReqVO);
        if (factorLib.getFactorCode() == null) {
            factorLib.setFactorCode(generateFactorCode());
        }
        // todo 设置 factor_value
        factorLibMapper.insert(factorLib);

        // Save gas list
        if (CollUtil.isNotEmpty(createReqVO.getGasList())) {
            List<CarbonFactorLibGasDO> gasList = CarbonFactorLibConvert.INSTANCE.convertGasList(createReqVO.getGasList());
            gasList.forEach(gas -> gas.setFactorLibId(factorLib.getId()));
            factorLibGasMapper.insertBatch(gasList);
        }

        return factorLib.getId();
    }

    @Override
    @CacheEvict(cacheNames = CARBON_FACTOR_LIB, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void updateFactorLib(CarbonFactorLibSaveReqVO updateReqVO) {
        validateFactorLibExists(updateReqVO.getId());
        CarbonFactorLibDO updateObj = CarbonFactorLibConvert.INSTANCE.convert(updateReqVO);
        // todo 设置 factor_value
        factorLibMapper.updateById(updateObj);

        factorLibGasMapper.deleteByFactorLibId(updateReqVO.getId());
        if (CollUtil.isNotEmpty(updateReqVO.getGasList())) {
            List<CarbonFactorLibGasDO> gasList = CarbonFactorLibConvert.INSTANCE.convertGasList(updateReqVO.getGasList());
            gasList.forEach(gas -> gas.setFactorLibId(updateReqVO.getId()));
            factorLibGasMapper.insertBatch(gasList);
        }
    }

    @Override
    @CacheEvict(cacheNames = CARBON_FACTOR_LIB, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteFactorLib(Long id) {
        validateFactorLibExists(id);
        factorLibGasMapper.deleteByFactorLibId(id);
        factorLibMapper.deleteById(id);
    }

    @Override
    @Cacheable(cacheNames = CARBON_FACTOR_LIB, key = "#id")
    public CarbonFactorLibRespVO getFactorLib(Long id) {
        CarbonFactorLibDO factorLib = factorLibMapper.selectById(id);
        if (factorLib == null) {
            return null;
        }
        CarbonFactorLibRespVO respVO = CarbonFactorLibConvert.INSTANCE.convert(factorLib);
        respVO.setGasList(CarbonFactorLibConvert.INSTANCE.convertGasRespList(factorLibGasMapper.selectListByFactorLibId(id)));
        respVO.setGasList(this.getFactorLibGasList(id));
        return respVO;
    }

    @Override
    public List<CarbonFactorLibRespVO> getFactorLibList(CarbonFactorLibReqVO carbonFactorLibReqVO) {
        List<CarbonFactorLibDO> list = factorLibMapper.selectList(carbonFactorLibReqVO);
        return CarbonFactorLibConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<CarbonFactorLibRespVO> getFactorLibListByIds(List<Long> ids) {
        List<CarbonFactorLibDO> list = factorLibMapper.selectBatchIds(ids);
        return CarbonFactorLibConvert.INSTANCE.convertList(list);
    }

    @Override
    public PageResult<CarbonFactorLibRespVO> getFactorLibPage(CarbonFactorLibPageReqVO pageReqVO) {
        PageResult<CarbonFactorLibRespVO> page = CarbonFactorLibConvert.INSTANCE.convertPage(factorLibMapper.selectPage(pageReqVO));
        List<CarbonFactorLibRespVO> list = page.getList();
        Set<Long> docIds = CollectionUtils.convertSet(list, CarbonFactorLibRespVO::getId);
        List<CarbonDocumentRespVO> documentRespVOS = carbonDocumentService.getDocumentListByIds(docIds);
        Map<Long, String> docIdToNameMap = CollectionUtils.convertMap(documentRespVOS, CarbonDocumentRespVO::getId, CarbonDocumentRespVO::getDocName);
        if(!list.isEmpty()){
            list.forEach(item -> item.setRelatedDoc(docIdToNameMap.get(item.getId())));
        }
        return page.setList(list);
    }

    /**
     * 根据因子ID 获取气体详情
     *
     * @param factorLibId
     * @return
     */
    @Cacheable(cacheNames = CARBON_FACTOR_LIB, key = "#factorLibId + ':gas'")
    public List<CarbonFactorLibGasRespVO> getFactorLibGasList(Long factorLibId) {
        // 查询关联表
        List<CarbonFactorLibGasDO> factorLibGasDOList = factorLibGasMapper.selectListByFactorLibId(factorLibId);
        List<CarbonFactorLibGasRespVO> factorLibGasRespVOS = CarbonFactorLibConvert.INSTANCE.convertGasRespList(factorLibGasDOList);
        // 查询气体信息
        Set<String> gasCodeSet = CollectionUtils.convertSet(factorLibGasDOList, CarbonFactorLibGasDO::getGasCode);
        CarbonGasInfoReqVO carbonGasInfoReqVO = new CarbonGasInfoReqVO();
        carbonGasInfoReqVO.setGasCodes(gasCodeSet);
        List<CarbonGasInfoRespVO> gasInfoRespVOS = carbonGasInfoService.getGasInfoList(carbonGasInfoReqVO);
        Map<String, CarbonGasInfoRespVO> gasInfoMap = CollectionUtils.convertMap(gasInfoRespVOS, CarbonGasInfoRespVO::getGasCode);
        factorLibGasRespVOS.forEach(item -> {
            item.setGasName(gasInfoMap.getOrDefault(item.getGasCode(), new CarbonGasInfoRespVO()).getGasName());
            item.setGwp(gasInfoMap.getOrDefault(item.getGasCode(), new CarbonGasInfoRespVO()).getGwp());
            item.setCategory(gasInfoMap.getOrDefault(item.getGasCode(), new CarbonGasInfoRespVO()).getCategory());
        });

        return factorLibGasRespVOS;
    }

    @VisibleForTesting
    void validateFactorLibExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonFactorLibDO factorLib = factorLibMapper.selectById(id);
        if (factorLib == null) {
            throw exception(FACTOR_LIB_NOT_EXISTS);
        }
    }

    public String generateFactorCode() {
        String prefix = "EF";
        CarbonFactorLibDO latestSource = factorLibMapper.selectLatestOne();
        String lastCode = latestSource != null ? latestSource.getFactorCode() : null;
        return GenerateCode.generateCode(prefix, lastCode);
    }
}
