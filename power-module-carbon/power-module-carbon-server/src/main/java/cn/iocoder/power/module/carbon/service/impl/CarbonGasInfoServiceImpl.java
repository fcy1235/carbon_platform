package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;

import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonGasInfoConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonGasInfoMapper;
import cn.iocoder.power.module.carbon.enums.GasCategoryEnum;
import cn.iocoder.power.module.carbon.service.CarbonGasInfoService;
import cn.iocoder.power.module.carbon.util.GenerateCode;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class CarbonGasInfoServiceImpl implements CarbonGasInfoService {

    @Resource
    private CarbonGasInfoMapper gasInfoMapper;

    @Override
    public Long createGasInfo(CarbonGasInfoSaveReqVO createReqVO) {
        CarbonGasInfoDO gasInfo = CarbonGasInfoConvert.INSTANCE.convert(createReqVO);
        if(gasInfo.getGasCode() == null){
            gasInfo.setGasCode(generateGasCode());
        }
        gasInfoMapper.insert(gasInfo);
        return gasInfo.getId();
    }

    @Override
    public void updateGasInfo(CarbonGasInfoSaveReqVO updateReqVO) {
        validateGasInfoExists(updateReqVO.getId());
        CarbonGasInfoDO updateObj = CarbonGasInfoConvert.INSTANCE.convert(updateReqVO);
        gasInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteGasInfo(Long id) {
        validateGasInfoExists(id);
        gasInfoMapper.deleteById(id);
    }

    @Override
    public CarbonGasInfoRespVO getGasInfo(Long id) {
        return CarbonGasInfoConvert.INSTANCE.convert(gasInfoMapper.selectById(id));
    }

    @Override
    public PageResult<CarbonGasInfoRespVO> getGasInfoPage(CarbonGasInfoPageReqVO pageReqVO) {
        return CarbonGasInfoConvert.INSTANCE.convertPage(gasInfoMapper.selectPage(pageReqVO));
    }

    @VisibleForTesting
    void validateGasInfoExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonGasInfoDO gasInfo = gasInfoMapper.selectById(id);
        if (gasInfo == null) {
            throw exception(GAS_INFO_NOT_EXISTS);
        }
    }

    @Override
    public void importGasInfoList(List<CarbonGasInfoImportVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            CarbonGasInfoImportVO importVO = list.get(i);
            CarbonGasInfoDO gasInfo = CarbonGasInfoConvert.INSTANCE.convert(importVO);
            if (StrUtil.isBlank(gasInfo.getGasCode())) {
                gasInfo.setGasCode("GAS" + System.currentTimeMillis() + i);
            }
            gasInfoMapper.insert(gasInfo);
        }
    }

    public String generateGasCode() {
        String prefix = "GAS" ;
        CarbonGasInfoDO latestSource = gasInfoMapper.selectLatestOne();
        String lastCode = latestSource != null ? latestSource.getGasCode() : null;
        return GenerateCode.generateCode(prefix, lastCode);
    }

    @Override
    public List<CarbonGasInfoRespVO> getGasInfoList(CarbonGasInfoReqVO reqVO) {
        return CarbonGasInfoConvert.INSTANCE.convertList(gasInfoMapper.selectList(reqVO));
    }

    @Override
    public List<CarbonGasInfoRespVO> getGasInfoListByPage(CarbonGasInfoPageReqVO pageReqVO) {
        // 将 PageReqVO 转换为 ReqVO，保留筛选条件和 ids
        CarbonGasInfoReqVO reqVO = new CarbonGasInfoReqVO();
        reqVO.setIds(pageReqVO.getIds());
        reqVO.setGasCode(pageReqVO.getGasCode());
        reqVO.setGasName(pageReqVO.getGasName());
        reqVO.setCategory(pageReqVO.getCategory());
        List<CarbonGasInfoDO> list = gasInfoMapper.selectList(reqVO);
        List<CarbonGasInfoRespVO> voList = CarbonGasInfoConvert.INSTANCE.convertList(list);
        // 气体类别代码转中文
        fillCategoryName(voList);
        return voList;
    }

    /**
     * 气体类别代码转中文名称
     */
    private void fillCategoryName(List<CarbonGasInfoRespVO> voList) {
        for (CarbonGasInfoRespVO vo : voList) {
            if (vo.getCategory() != null) {
                for (GasCategoryEnum enumVal : GasCategoryEnum.values()) {
                    if (enumVal.getType().equals(vo.getCategory())) {
                        vo.setCategory(enumVal.getName());
                        break;
                    }
                }
            }
        }
    }
}
