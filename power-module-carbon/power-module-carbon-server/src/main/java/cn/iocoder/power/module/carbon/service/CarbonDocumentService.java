package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonDocumentRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDocumentDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

public interface CarbonDocumentService {

    Long createDocument(@Valid CarbonDocumentSaveReqVO createReqVO);

    void updateDocument(@Valid CarbonDocumentSaveReqVO updateReqVO);

    void deleteDocument(Long id);

    CarbonDocumentRespVO getDocument(Long id);

    PageResult<CarbonDocumentRespVO> getDocumentPage(CarbonDocumentPageReqVO pageReqVO);

    String generateCode();

    List<CarbonDocumentRespVO> getDocumentList(@Valid CarbonDocumentReqVO reqVO);

    List<CarbonDocumentRespVO> getDocumentListByIds(Collection<Long> ids);
}
