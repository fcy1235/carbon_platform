package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportTaskPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerFileDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportTaskDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportTaskWithFileDO;
import cn.iocoder.power.module.carbon.util.LedgerDataPermissionHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonLedgerImportTaskMapper extends BaseMapperX<CarbonLedgerImportTaskDO> {

    /**
     * 联表分页查询：导入任务 + 台账文件信息
     */
    default PageResult<CarbonLedgerImportTaskWithFileDO> selectPageWithFile(CarbonLedgerImportTaskPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonLedgerImportTaskDO> wrapper = new MPJLambdaWrapper<CarbonLedgerImportTaskDO>()
                // 导入任务字段
                .select(CarbonLedgerImportTaskDO::getId, CarbonLedgerImportTaskDO::getLedgerFileId,
                        CarbonLedgerImportTaskDO::getSuccessCount, CarbonLedgerImportTaskDO::getFailCount,
                        CarbonLedgerImportTaskDO::getImportStatus, CarbonLedgerImportTaskDO::getUploader,
                        CarbonLedgerImportTaskDO::getUploadTime, CarbonLedgerImportTaskDO::getResultFileUrl,
                        CarbonLedgerImportTaskDO::getRemark, CarbonLedgerImportTaskDO::getCreateTime)
                // 台账文件字段
                .select(
                        CarbonLedgerFileDO::getUploadType, CarbonLedgerFileDO::getFileName,
                        CarbonLedgerFileDO::getFileUrl, CarbonLedgerFileDO::getDataCount,
                        CarbonLedgerFileDO::getUploadStatus, CarbonLedgerFileDO::getAuditStatus,
                        CarbonLedgerFileDO::getDataLevel,
                        CarbonLedgerFileDO::getCityCode, CarbonLedgerFileDO::getDistrictCode)
                // LEFT JOIN carbon_ledger_file
                .leftJoin(CarbonLedgerFileDO.class, CarbonLedgerFileDO::getId,
                        CarbonLedgerImportTaskDO::getLedgerFileId)
                // 筛选条件
                .eq(StrUtil.isNotBlank(reqVO.getCityCode()), CarbonLedgerFileDO::getCityCode, reqVO.getCityCode())
                .eq(StrUtil.isNotBlank(reqVO.getDistrictCode()), CarbonLedgerFileDO::getDistrictCode, reqVO.getDistrictCode())
                .eq(StrUtil.isNotBlank(reqVO.getUploadType()), CarbonLedgerFileDO::getUploadType, reqVO.getUploadType())
                .eq(StrUtil.isNotBlank(reqVO.getImportStatus()), CarbonLedgerImportTaskDO::getImportStatus, reqVO.getImportStatus())
                .eq(StrUtil.isNotBlank(reqVO.getAuditStatus()), CarbonLedgerFileDO::getAuditStatus, reqVO.getAuditStatus())
                .between(reqVO.getUploadTime() != null && reqVO.getUploadTime().length == 2,
                        CarbonLedgerImportTaskDO::getUploadTime,
                        reqVO.getUploadTime() != null && reqVO.getUploadTime().length == 2 ? reqVO.getUploadTime()[0] : null,
                        reqVO.getUploadTime() != null && reqVO.getUploadTime().length == 2 ? reqVO.getUploadTime()[1] : null)
                .orderByDesc(CarbonLedgerImportTaskDO::getId);

        Page<CarbonLedgerImportTaskWithFileDO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        page = selectJoinPage(page, CarbonLedgerImportTaskWithFileDO.class, wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 联表查询单个记录：导入任务 + 台账文件信息
     */
    default CarbonLedgerImportTaskWithFileDO selectOneWithFile(Long id) {
        MPJLambdaWrapper<CarbonLedgerImportTaskDO> wrapper = new MPJLambdaWrapper<CarbonLedgerImportTaskDO>()
                // 导入任务字段
                .select(CarbonLedgerImportTaskDO::getId, CarbonLedgerImportTaskDO::getLedgerFileId,
                        CarbonLedgerImportTaskDO::getSuccessCount, CarbonLedgerImportTaskDO::getFailCount,
                        CarbonLedgerImportTaskDO::getImportStatus, CarbonLedgerImportTaskDO::getUploader,
                        CarbonLedgerImportTaskDO::getUploadTime, CarbonLedgerImportTaskDO::getResultFileUrl,
                        CarbonLedgerImportTaskDO::getRemark, CarbonLedgerImportTaskDO::getCreateTime)
                // 台账文件字段
                .select(
                        CarbonLedgerFileDO::getUploadType, CarbonLedgerFileDO::getFileName,
                        CarbonLedgerFileDO::getFileUrl, CarbonLedgerFileDO::getDataCount,
                        CarbonLedgerFileDO::getUploadStatus, CarbonLedgerFileDO::getAuditStatus,
                        CarbonLedgerFileDO::getDataLevel,CarbonLedgerFileDO::getDeptId,
                        CarbonLedgerFileDO::getCityCode, CarbonLedgerFileDO::getDistrictCode)
                // LEFT JOIN carbon_ledger_file
                .leftJoin(CarbonLedgerFileDO.class, CarbonLedgerFileDO::getId,
                        CarbonLedgerImportTaskDO::getLedgerFileId)
                .eq(CarbonLedgerImportTaskDO::getId, id);
        return selectJoinOne(CarbonLedgerImportTaskWithFileDO.class, wrapper);
    }

    /**
     * 附加台账模块数据权限过滤条件
     */
    default void applyDataPermission(LambdaQueryWrapperX<CarbonLedgerImportTaskDO> wrapper) {
        LedgerDataPermissionHelper.UserAreaContext context = LedgerDataPermissionHelper.getCurrentUserAreaContext();
        switch (context.getScope()) {
            case DISTRICT -> {
                // 子查询：获取当前区县的台账文件ID
                LambdaQueryWrapper<CarbonLedgerFileDO> subQuery = new LambdaQueryWrapper<CarbonLedgerFileDO>()
                        .select(CarbonLedgerFileDO::getId)
                        .eq(CarbonLedgerFileDO::getDistrictCode, context.getDistrictCode());
                wrapper.inSql(CarbonLedgerImportTaskDO::getLedgerFileId, 
                        "SELECT id FROM carbon_ledger_file WHERE district_code = '" + context.getDistrictCode() + "'");
            }
            case CITY -> {
                // 子查询：获取当前城市的台账文件ID
                LambdaQueryWrapper<CarbonLedgerFileDO> subQuery = new LambdaQueryWrapper<CarbonLedgerFileDO>()
                        .select(CarbonLedgerFileDO::getId)
                        .eq(CarbonLedgerFileDO::getCityCode, context.getCityCode());
                wrapper.inSql(CarbonLedgerImportTaskDO::getLedgerFileId, 
                        "SELECT id FROM carbon_ledger_file WHERE city_code = '" + context.getCityCode() + "'");
            }
            default -> {
                // 无限制
            }
        }
    }
}
