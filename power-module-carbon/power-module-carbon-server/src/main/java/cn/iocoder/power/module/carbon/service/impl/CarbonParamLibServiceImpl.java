package cn.iocoder.power.module.carbon.service.impl;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonParamLibPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonParamLibRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonParamLibSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonParamLibConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonParamLibDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonParamLibMapper;
import cn.iocoder.power.module.carbon.service.CarbonParamLibService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.power.module.carbon.dal.redis.RedisKeyConstants.CARBON_PARAM_LIB;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class CarbonParamLibServiceImpl implements CarbonParamLibService {

    @Resource
    private CarbonParamLibMapper paramLibMapper;

    @Override
    @CacheEvict(cacheNames = CARBON_PARAM_LIB, allEntries = true)
    public Long createParamLib(CarbonParamLibSaveReqVO createReqVO) {
        CarbonParamLibDO paramLib = CarbonParamLibConvert.INSTANCE.convert(createReqVO);
        paramLib.setParamCode(generateParamCode());
        paramLibMapper.insert(paramLib);
        return paramLib.getId();
    }

    @Override
    @CacheEvict(cacheNames = CARBON_PARAM_LIB, allEntries = true)
    public void updateParamLib(CarbonParamLibSaveReqVO updateReqVO) {
        validateParamLibExists(updateReqVO.getId());
        CarbonParamLibDO updateObj = CarbonParamLibConvert.INSTANCE.convert(updateReqVO);
        paramLibMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = CARBON_PARAM_LIB, allEntries = true)
    public void deleteParamLib(Long id) {
        validateParamLibExists(id);
        paramLibMapper.deleteById(id);
    }

    @Override
    @Cacheable(cacheNames = CARBON_PARAM_LIB, key = "#id")
    public CarbonParamLibRespVO getParamLib(Long id) {
        return CarbonParamLibConvert.INSTANCE.convert(paramLibMapper.selectById(id));
    }

    @Override
    public PageResult<CarbonParamLibRespVO> getParamLibPage(CarbonParamLibPageReqVO pageReqVO) {
        return CarbonParamLibConvert.INSTANCE.convertPage(paramLibMapper.selectPage(pageReqVO));
    }

    @VisibleForTesting
    void validateParamLibExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonParamLibDO paramLib = paramLibMapper.selectById(id);
        if (paramLib == null) {
            throw exception(PARAM_LIB_NOT_EXISTS);
        }
    }

    private String generateParamCode() {
        return "PAR" + System.currentTimeMillis();
    }
}
