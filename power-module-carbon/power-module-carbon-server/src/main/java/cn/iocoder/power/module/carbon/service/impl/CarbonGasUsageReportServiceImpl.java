package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceImportErrorVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceImportResultVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonGasUsageReportDataReqVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonGasUsageReportDataRespVO;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonGasUsageReportImportVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasUsageReportDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonGasUsageReportMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.service.CarbonGasUsageReportService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 燃气数据报表（采暖季用气量统计，新表）Service 实现
 *
 * 表结构：本表只存 carbonUserInfoId + 采暖季数据，用户字段（姓名/身份证/用户编码/表具号）不落表。
 * 导入规则：
 * 1. 模板列按索引映射（见 CarbonGasUsageReportImportVO）；
 * 2. 「燃气表具号」必填：按表号去 carbon_user_info 反查用户 ID 落表，查不到报"燃气表号不存在"；
 * 3. 表底数/用气量按数字解析，「合计用气量」为空且起止表底数都有值时自动计算（终止-起始）；
 * 4. 单行失败不阻断整批，逐行记录错误，最终返回成功/失败行数与错误明细。
 * 查询规则：按采暖季 + 用户侧条件（姓名/身份证/表具号）查询，用户字段联表回填。
 */
@Service
@Validated
@Slf4j
public class CarbonGasUsageReportServiceImpl implements CarbonGasUsageReportService {

    @Resource
    private CarbonGasUsageReportMapper gasUsageReportMapper;

    @Resource
    private CarbonUserInfoMapper userInfoMapper;

    @Override
    public CarbonDeviceImportResultVO importGasUsageReport(MultipartFile file) throws IOException {
        List<CarbonGasUsageReportImportVO> list = ExcelUtils.read(file, CarbonGasUsageReportImportVO.class);

        CarbonDeviceImportResultVO result = new CarbonDeviceImportResultVO();
        List<CarbonDeviceImportErrorVO> errors = new ArrayList<>();
        int successCount = 0;
        // 表头占第 1 行，数据从第 2 行开始
        int rowNum = 1;
        for (CarbonGasUsageReportImportVO vo : list) {
            rowNum++;
            if (isRowEmpty(vo)) {
                // 整行为空（模板自带的空白样式行），跳过不计入统计
                continue;
            }
            try {
                importRow(vo);
                successCount++;
            } catch (Exception e) {
                log.warn("[燃气数据报表导入] 第 {} 行导入失败: {}", rowNum, e.getMessage());
                errors.add(new CarbonDeviceImportErrorVO(rowNum, e.getMessage()));
            }
        }
        result.setTotalCount(successCount + errors.size());
        result.setSuccessCount(successCount);
        result.setFailCount(errors.size());
        result.setErrors(errors);
        log.info("[燃气数据报表导入] Excel 读取行数={}, 成功={}, 失败={}", list.size(), successCount, errors.size());
        return result;
    }

    @Override
    public List<CarbonGasUsageReportDataRespVO> getGasUsageDataList(CarbonGasUsageReportDataReqVO reqVO) {
        String keyword = trim(reqVO.getKeyword());
        String gasId = trim(reqVO.getGasId());

        // 用户侧条件（姓名/身份证/表具号）：先去用户表解析出匹配的用户 ID 集合
        List<Long> filterUserIds = null;
        if (StrUtil.isNotBlank(keyword) || StrUtil.isNotBlank(gasId)) {
            List<CarbonUserInfoDO> matchedUsers = userInfoMapper.selectList(new LambdaQueryWrapperX<CarbonUserInfoDO>()
                    .likeIfPresent(CarbonUserInfoDO::getGasId, gasId)
                    .and(StrUtil.isNotBlank(keyword), w -> w
                            .like(CarbonUserInfoDO::getUsername, keyword)
                            .or()
                            .like(CarbonUserInfoDO::getIdCard, keyword)));
            if (CollUtil.isEmpty(matchedUsers)) {
                // 用户侧条件一个都匹配不到，结果必然为空
                return Collections.emptyList();
            }
            filterUserIds = matchedUsers.stream().map(CarbonUserInfoDO::getId).collect(Collectors.toList());
        }

        List<CarbonGasUsageReportDO> list = gasUsageReportMapper.selectReportList(
                trim(reqVO.getHeatingSeason()), filterUserIds);
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 批量加载关联用户，回填 姓名/身份证/用户编码/表具号
        List<Long> userIds = list.stream()
                .map(CarbonGasUsageReportDO::getCarbonUserInfoId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, CarbonUserInfoDO> userMap = CollUtil.isEmpty(userIds)
                ? Collections.emptyMap()
                : userInfoMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(CarbonUserInfoDO::getId, Function.identity()));

        return list.stream().map(item -> {
            CarbonGasUsageReportDataRespVO respVO = new CarbonGasUsageReportDataRespVO();
            BeanUtils.copyProperties(item, respVO);
            CarbonUserInfoDO user = item.getCarbonUserInfoId() != null ? userMap.get(item.getCarbonUserInfoId()) : null;
            if (user != null) {
                respVO.setUsername(user.getUsername());
                respVO.setIdCard(user.getIdCard());
                respVO.setGasUserCode(user.getUserCode());
                respVO.setGasId(user.getGasId());
            }
            return respVO;
        }).collect(Collectors.toList());
    }

    /**
     * 判断整行是否为空（所有业务字段都为空则视为空行）
     */
    private boolean isRowEmpty(CarbonGasUsageReportImportVO vo) {
        String[] fields = {
                vo.getUsername(), vo.getGasUserCode(), vo.getIdCard(), vo.getGasId(),
                vo.getHeatingSeason(), vo.getStartReading(), vo.getEndReading(),
                vo.getTotalUsage(), vo.getRemark()
        };
        for (String field : fields) {
            if (StrUtil.isNotBlank(field)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 导入单行：表号反查用户 → 解析数值 → 合计用气量兜底计算 → 插入新表
     */
    private void importRow(CarbonGasUsageReportImportVO vo) {
        String gasId = trim(vo.getGasId());
        if (StrUtil.isBlank(gasId)) {
            throw new IllegalArgumentException("燃气表具号为空");
        }
        // 按表号反查用户，查不到则该行失败
        CarbonUserInfoDO user = userInfoMapper.selectByGasId(gasId);
        if (user == null) {
            throw new IllegalArgumentException("燃气表号不存在：" + gasId);
        }

        CarbonGasUsageReportDO data = new CarbonGasUsageReportDO();
        data.setCarbonUserInfoId(user.getId());
        data.setHeatingSeason(trim(vo.getHeatingSeason()));
        data.setStartReading(parseDecimal(vo.getStartReading(), "采暖季起始表底数"));
        data.setEndReading(parseDecimal(vo.getEndReading(), "采暖季终止表底数"));
        data.setTotalUsage(parseDecimal(vo.getTotalUsage(), "采暖季合计用气量"));
        // 合计用气量为空且起止表底数都有值时，自动按 终止-起始 计算
        if (data.getTotalUsage() == null && data.getStartReading() != null && data.getEndReading() != null) {
            data.setTotalUsage(data.getEndReading().subtract(data.getStartReading()));
        }
        data.setRemark(trim(vo.getRemark()));
        gasUsageReportMapper.insert(data);
    }

    /**
     * 解析数值字段：空返回 null；非数字抛出行级错误
     */
    private BigDecimal parseDecimal(String raw, String fieldName) {
        String value = trim(raw);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + "不是合法数字：" + raw);
        }
    }

    private String trim(String value) {
        return StrUtil.isBlank(value) ? null : value.trim();
    }

}
