package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerReportPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerFileDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerReportDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerReportWithFileDO;
import cn.iocoder.power.module.carbon.util.LedgerDataPermissionHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarbonLedgerReportMapper extends BaseMapperX<CarbonLedgerReportDO> {

    default PageResult<CarbonLedgerReportDO> selectPage(CarbonLedgerReportPageReqVO reqVO) {
        // 构建子查询：通过 ledger_file_id 关联 file 表的字段进行过滤
        LambdaQueryWrapper<CarbonLedgerFileDO> subQuery = new LambdaQueryWrapper<CarbonLedgerFileDO>()
                .select(CarbonLedgerFileDO::getId)
                .eq(StrUtil.isNotBlank(reqVO.getCityCode()), CarbonLedgerFileDO::getCityCode, reqVO.getCityCode())
                .eq(StrUtil.isNotBlank(reqVO.getDistrictCode()), CarbonLedgerFileDO::getDistrictCode, reqVO.getDistrictCode())
                .eq(StrUtil.isNotBlank(reqVO.getUploadType()), CarbonLedgerFileDO::getUploadType, reqVO.getUploadType())
                .eq(StrUtil.isNotBlank(reqVO.getAuditStatus()), CarbonLedgerFileDO::getAuditStatus, reqVO.getAuditStatus())
                .in(CollUtil.isNotEmpty(reqVO.getAuditStatusCollection()), CarbonLedgerFileDO::getAuditStatus, reqVO.getAuditStatusCollection())
                .between(reqVO.getUploadTime() != null && reqVO.getUploadTime().length == 2,
                        CarbonLedgerFileDO::getUploadTime,
                        reqVO.getUploadTime() != null && reqVO.getUploadTime().length == 2 ? reqVO.getUploadTime()[0] : null,
                        reqVO.getUploadTime() != null && reqVO.getUploadTime().length == 2 ? reqVO.getUploadTime()[1] : null);
        LambdaQueryWrapperX<CarbonLedgerReportDO> wrapper = new LambdaQueryWrapperX<CarbonLedgerReportDO>()
                .inIfPresent(CarbonLedgerReportDO::getLedgerFileId, subQuery)
                .orderByDesc(CarbonLedgerReportDO::getId);
        return selectPage(reqVO, wrapper);
    }

    /**
     * 联表分页查询：台账上报 + 台账文件信息
     */
    default PageResult<CarbonLedgerReportWithFileDO> selectPageWithFile(CarbonLedgerReportPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonLedgerReportDO> wrapper = new MPJLambdaWrapper<CarbonLedgerReportDO>()
                // 台账上报字段
                .select(CarbonLedgerReportDO::getId, CarbonLedgerReportDO::getLedgerFileId,
                        CarbonLedgerReportDO::getReportDesc,
                        CarbonLedgerReportDO::getStampedReportUrl, CarbonLedgerReportDO::getStampedReportName,
                        CarbonLedgerReportDO::getRemark,
                        CarbonLedgerReportDO::getCreateTime)
                // 台账文件字段
                .select(CarbonLedgerFileDO::getCityCode, CarbonLedgerFileDO::getDistrictCode,
                        CarbonLedgerFileDO::getUploadType, CarbonLedgerFileDO::getDataCount,
                        CarbonLedgerFileDO::getAuditStatus, CarbonLedgerFileDO::getUploadTime,
                        CarbonLedgerFileDO::getFileName, CarbonLedgerFileDO::getFileUrl,
                        CarbonLedgerFileDO::getReviewer, CarbonLedgerFileDO::getReviewTime,
                        CarbonLedgerFileDO::getRejectReason,CarbonLedgerFileDO::getUploader)
                // LEFT JOIN carbon_ledger_file
                .leftJoin(CarbonLedgerFileDO.class, CarbonLedgerFileDO::getId,
                        CarbonLedgerReportDO::getLedgerFileId)
                // 筛选条件
                .eq(reqVO.getCityCode() != null, CarbonLedgerFileDO::getCityCode, reqVO.getCityCode())
                .eq(reqVO.getDistrictCode() != null, CarbonLedgerFileDO::getDistrictCode, reqVO.getDistrictCode())
                .eq(reqVO.getUploadType() != null, CarbonLedgerFileDO::getUploadType, reqVO.getUploadType())
                .eq(reqVO.getAuditStatus() != null, CarbonLedgerFileDO::getAuditStatus, reqVO.getAuditStatus())
                .in(reqVO.getAuditStatusCollection() != null && !reqVO.getAuditStatusCollection().isEmpty(),
                        CarbonLedgerFileDO::getAuditStatus, reqVO.getAuditStatusCollection())
                .between(reqVO.getUploadTime() != null && reqVO.getUploadTime().length == 2,
                        CarbonLedgerFileDO::getUploadTime,
                        reqVO.getUploadTime() != null && reqVO.getUploadTime().length == 2 ? reqVO.getUploadTime()[0] : null,
                        reqVO.getUploadTime() != null && reqVO.getUploadTime().length == 2 ? reqVO.getUploadTime()[1] : null)
                .orderByDesc(CarbonLedgerReportDO::getId);

        Page<CarbonLedgerReportWithFileDO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        page = selectJoinPage(page, CarbonLedgerReportWithFileDO.class, wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 联表查询单个记录：台账上报 + 台账文件信息
     */
    default CarbonLedgerReportWithFileDO selectOneWithFile(Long id) {
        MPJLambdaWrapper<CarbonLedgerReportDO> wrapper = new MPJLambdaWrapper<CarbonLedgerReportDO>()
                // 台账上报字段
                .select(CarbonLedgerReportDO::getId, CarbonLedgerReportDO::getLedgerFileId,
                        CarbonLedgerReportDO::getReportDesc,
                        CarbonLedgerReportDO::getStampedReportUrl, CarbonLedgerReportDO::getStampedReportName,
                        CarbonLedgerReportDO::getRemark,
                        CarbonLedgerReportDO::getCreateTime)
                // 台账文件字段
                .select(CarbonLedgerFileDO::getCityCode, CarbonLedgerFileDO::getDistrictCode,
                        CarbonLedgerFileDO::getUploadType, CarbonLedgerFileDO::getDataCount,
                        CarbonLedgerFileDO::getAuditStatus, CarbonLedgerFileDO::getUploadTime,
                        CarbonLedgerFileDO::getFileName, CarbonLedgerFileDO::getFileUrl,
                        CarbonLedgerFileDO::getReviewer, CarbonLedgerFileDO::getReviewTime,
                        CarbonLedgerFileDO::getRejectReason)
                // LEFT JOIN carbon_ledger_file
                .leftJoin(CarbonLedgerFileDO.class, CarbonLedgerFileDO::getId,
                        CarbonLedgerReportDO::getLedgerFileId)
                .eq(CarbonLedgerReportDO::getId, id);
        return selectJoinOne(CarbonLedgerReportWithFileDO.class, wrapper);
    }
}
