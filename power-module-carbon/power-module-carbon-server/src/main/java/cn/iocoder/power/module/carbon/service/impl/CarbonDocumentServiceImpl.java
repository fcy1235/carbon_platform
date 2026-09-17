package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonDocumentConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentDetailDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentParamDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonFactorLibDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonDocumentDetailMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonDocumentMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonDocumentParamMapper;
import cn.iocoder.power.module.carbon.service.CarbonDocumentService;
import cn.iocoder.power.module.carbon.util.GenerateCode;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.DOCUMENT_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class CarbonDocumentServiceImpl implements CarbonDocumentService {

    @Resource
    private CarbonDocumentMapper documentMapper;

    @Resource
    private CarbonDocumentDetailMapper documentDetailMapper;

    @Resource
    private CarbonDocumentParamMapper documentParamMapper;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDocument(CarbonDocumentSaveReqVO createReqVO) {
        CarbonDocumentDO document = CarbonDocumentConvert.INSTANCE.convert(createReqVO);
        if (StrUtil.isBlank(document.getDocCode())) {
            document.setDocCode(this.generateCode());
        }
        document.setUploader(SecurityFrameworkUtils.getLoginUserNickname());
        document.setUploadTime(LocalDateTime.now());
        documentMapper.insert(document);

        if (CollUtil.isNotEmpty(createReqVO.getDetails())) {
            List<CarbonDocumentDetailDO> details = CarbonDocumentConvert.INSTANCE.convertDetailSaveList(createReqVO.getDetails());
            details.forEach(detail -> detail.setDocumentId(document.getId()));
            documentDetailMapper.insertBatch(details);
        }

        if (CollUtil.isNotEmpty(createReqVO.getParams())) {
            List<CarbonDocumentParamDO> params = CarbonDocumentConvert.INSTANCE.convertParamSaveList(createReqVO.getParams());
            params.forEach(param -> param.setDocumentId(document.getId()));
            documentParamMapper.insertBatch(params);
        }

        return document.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDocument(CarbonDocumentSaveReqVO updateReqVO) {
        validateDocumentExists(updateReqVO.getId());
        CarbonDocumentDO updateObj = CarbonDocumentConvert.INSTANCE.convert(updateReqVO);
        documentMapper.updateById(updateObj);

        documentDetailMapper.deleteByDocumentId(updateReqVO.getId());
        if (CollUtil.isNotEmpty(updateReqVO.getDetails())) {
            List<CarbonDocumentDetailDO> details = CarbonDocumentConvert.INSTANCE.convertDetailSaveList(updateReqVO.getDetails());
            details.forEach(detail -> detail.setDocumentId(updateReqVO.getId()));
            documentDetailMapper.insertBatch(details);
        }

        documentParamMapper.deleteByDocumentId(updateReqVO.getId());
        if (CollUtil.isNotEmpty(updateReqVO.getParams())) {
            List<CarbonDocumentParamDO> params = CarbonDocumentConvert.INSTANCE.convertParamSaveList(updateReqVO.getParams());
            params.forEach(param -> param.setDocumentId(updateReqVO.getId()));
            documentParamMapper.insertBatch(params);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(Long id) {
        validateDocumentExists(id);
        documentDetailMapper.deleteByDocumentId(id);
        documentParamMapper.deleteByDocumentId(id);
        documentMapper.deleteById(id);
    }

    @Override
    public CarbonDocumentRespVO getDocument(Long id) {
        CarbonDocumentDO document = documentMapper.selectById(id);
        if (document == null) {
            throw exception(DOCUMENT_NOT_EXISTS);
        }
        CarbonDocumentRespVO respVO = CarbonDocumentConvert.INSTANCE.convert(document);
        respVO.setDetails(CarbonDocumentConvert.INSTANCE.convertDetailList(documentDetailMapper.selectListByDocumentId(id)));
        respVO.setParams(CarbonDocumentConvert.INSTANCE.convertParamList(documentParamMapper.selectListByDocumentId(id)));
        return respVO;
    }

    @Override
    public PageResult<CarbonDocumentRespVO> getDocumentPage(CarbonDocumentPageReqVO pageReqVO) {
        return CarbonDocumentConvert.INSTANCE.convertPage(documentMapper.selectPage(pageReqVO));
    }

    @Override
    public String generateCode() {
        String prefix = "DOC" ;
        CarbonDocumentDO latestSource = documentMapper.selectLatestOne();
        String lastCode = latestSource != null ? latestSource.getDocCode() : null;
        return GenerateCode.generateCode(prefix, lastCode);
    }

    @Override
    public List<CarbonDocumentRespVO> getDocumentList(CarbonDocumentReqVO reqVO) {
        return CarbonDocumentConvert.INSTANCE.convertList(documentMapper.selectList(reqVO));
    }

    @Override
    public List<CarbonDocumentRespVO> getDocumentListByIds(Collection<Long> ids) {
        List<CarbonDocumentDO> list = documentMapper.selectBatchIds(ids);
        return CarbonDocumentConvert.INSTANCE.convertList(list);
    }

    @VisibleForTesting
    void validateDocumentExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonDocumentDO document = documentMapper.selectById(id);
        if (document == null) {
            throw exception(DOCUMENT_NOT_EXISTS);
        }
    }
}
