package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidyImportVO;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidyPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidyRespVO;
import cn.iocoder.power.module.carbon.controller.admin.subsidy.vo.CarbonSubsidySaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonSubsidyConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonSubsidyDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonSubsidyWithUserDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonSubsidyMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.enums.SubsidyStatusEnum;
import cn.iocoder.power.module.carbon.service.CarbonSubsidyService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service("carbonSubsidyService")
@Validated
@Slf4j
public class CarbonSubsidyServiceImpl implements CarbonSubsidyService {

    @Resource
    private CarbonSubsidyMapper subsidyMapper;

    @Resource
    private CarbonUserInfoMapper userInfoMapper;

    @Override
    public Long createSubsidy(CarbonSubsidySaveReqVO createReqVO) {
        CarbonSubsidyDO subsidy = CarbonSubsidyConvert.INSTANCE.convert(createReqVO);
        subsidy.setSubsidyCode(generateSubsidyCode());
        subsidyMapper.insert(subsidy);
        return subsidy.getId();
    }

    @Override
    public void updateSubsidy(CarbonSubsidySaveReqVO updateReqVO) {
        validateSubsidyExists(updateReqVO.getId());
        CarbonSubsidyDO updateObj = CarbonSubsidyConvert.INSTANCE.convert(updateReqVO);
        subsidyMapper.updateById(updateObj);
    }

    @Override
    public void deleteSubsidy(Long id) {
        validateSubsidyExists(id);
        subsidyMapper.deleteById(id);
    }

    @Override
    public CarbonSubsidyRespVO getSubsidy(Long id) {
        // 使用联表查询，包含用户基本信息
        CarbonSubsidyWithUserDO subsidyWithUser = subsidyMapper.selectByIdWithUser(id);
        return CarbonSubsidyConvert.INSTANCE.convertFromWithUser(subsidyWithUser);
    }

    @Override
    public PageResult<CarbonSubsidyRespVO> getSubsidyPage(CarbonSubsidyPageReqVO pageReqVO) {
        // 使用联表查询
        PageResult<CarbonSubsidyWithUserDO> pageResult = subsidyMapper.selectPageWithUser(pageReqVO);
        return CarbonSubsidyConvert.INSTANCE.convertPageFromWithUser(pageResult);
    }

    @Override
    public List<CarbonSubsidyRespVO> getSubsidyList(CarbonSubsidyPageReqVO reqVO) {
        // 使用联表查询（支持ids筛选和条件筛选）
        List<CarbonSubsidyWithUserDO> list = subsidyMapper.selectListWithUser(reqVO);
        return CarbonSubsidyConvert.INSTANCE.convertListFromWithUser(list);
    }

    @Override
    public void importSubsidyList(List<CarbonSubsidyImportVO> list) {
        List<String> errorMessages = new ArrayList<>();
        int successCount = 0;

        for (int i = 0; i < list.size(); i++) {
            CarbonSubsidyImportVO importVO = list.get(i);
            int rowNum = i + 2; // Excel 行号（从第2行开始，第1行是表头）

            // 参数校验
            if (StrUtil.isBlank(importVO.getUsername())) {
                errorMessages.add("第" + rowNum + "行：用户姓名不能为空");
                continue;
            }
            if (StrUtil.isBlank(importVO.getIdCard())) {
                errorMessages.add("第" + rowNum + "行：身份证号不能为空");
                continue;
            }
            if (StrUtil.isBlank(importVO.getAddress())) {
                errorMessages.add("第" + rowNum + "行：地址不能为空");
                continue;
            }
            if (importVO.getSubsidyAmount() == null) {
                errorMessages.add("第" + rowNum + "行：补贴金额不能为空");
                continue;
            }

            // 根据用户名+身份证号+地址查询唯一用户
            CarbonUserInfoDO userInfo = userInfoMapper.selectByUsernameAndIdCardAndAddress(
                    importVO.getUsername(), importVO.getIdCard(), importVO.getAddress());
            if (userInfo == null) {
                errorMessages.add("第" + rowNum + "行：未找到匹配的用户（用户名=" + importVO.getUsername()
                        + "，身份证号=" + importVO.getIdCard() + "，地址=" + importVO.getAddress() + "）");
                continue;
            }

            // 创建补贴记录
            CarbonSubsidyDO subsidy = new CarbonSubsidyDO();
            subsidy.setSubsidyCode(generateSubsidyCode());
            subsidy.setUserInfoId(userInfo.getId());
            subsidy.setSubsidyAmount(importVO.getSubsidyAmount());
            subsidyMapper.insert(subsidy);
            successCount++;
        }

        log.info("补贴导入完成：成功{}条，失败{}条", successCount, errorMessages.size());

        // 如果有错误，抛出异常
        if (!errorMessages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("导入完成，成功").append(successCount).append("条。");
            if (errorMessages.size() <= 10) {
                sb.append("失败原因：").append(String.join("；", errorMessages));
            } else {
                sb.append("失败").append(errorMessages.size()).append("条，前10条失败原因：");
                for (int i = 0; i < 10; i++) {
                    sb.append(errorMessages.get(i));
                    if (i < 9) sb.append("；");
                }
            }
            throw exception(SUBSIDY_IMPORT_PARTIAL_FAILED, sb.toString());
        }
    }

    @VisibleForTesting
    void validateSubsidyExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonSubsidyDO subsidy = subsidyMapper.selectById(id);
        if (subsidy == null) {
            throw exception(SUBSIDY_NOT_EXISTS);
        }
    }

    private String generateSubsidyCode() {
        return "SUB" + System.currentTimeMillis();
    }
}
