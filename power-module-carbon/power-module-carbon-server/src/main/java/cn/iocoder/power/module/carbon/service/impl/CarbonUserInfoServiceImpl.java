package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.core.ArrayValuable;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;

import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoImportVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoListReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonUserInfoConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.enums.*;
import cn.iocoder.power.module.carbon.service.CarbonElectricityService;
import cn.iocoder.power.module.carbon.service.CarbonGasDataService;
import cn.iocoder.power.module.carbon.service.CarbonUserInfoService;
import cn.iocoder.power.module.carbon.util.AreaUtils;
import cn.iocoder.power.module.carbon.util.UserCodeGenerator;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.power.module.carbon.dal.redis.RedisKeyConstants.CARBON_USER_INFO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class CarbonUserInfoServiceImpl implements CarbonUserInfoService {

    @Resource
    private CarbonUserInfoMapper userInfoMapper;

    @Resource
    private AreaApi areaApi;

    @Resource
    private AreaUtils areaUtils;

    @Resource
    private CarbonElectricityService carbonElectricityService;

    @Resource
    private CarbonGasDataService carbonGasDataService;

    @Resource
    private UserCodeGenerator userCodeGenerator;

    @Override
    @CacheEvict(cacheNames = CARBON_USER_INFO, allEntries = true)
    public Long createUserInfo(CarbonUserInfoSaveReqVO createReqVO) {
        validateUserInfoIdCardUnique(null, createReqVO.getIdCard());

        CarbonUserInfoDO userInfo = CarbonUserInfoConvert.INSTANCE.convert(createReqVO);
        userInfo.setUserCode(userCodeGenerator.generate(userInfo.getDataSource(), userInfo.getProvinceCode(),
                userInfo.getCityCode(), userInfo.getDistrictCode()));
        userInfoMapper.insert(userInfo);
        return userInfo.getId();
    }

    @Override
    @CacheEvict(cacheNames = CARBON_USER_INFO, allEntries = true)
    public void updateUserInfo(CarbonUserInfoSaveReqVO updateReqVO) {
        validateUserInfoExists(updateReqVO.getId());
        validateUserInfoIdCardUnique(updateReqVO.getId(), updateReqVO.getIdCard());

        CarbonUserInfoDO updateObj = CarbonUserInfoConvert.INSTANCE.convert(updateReqVO);
        userInfoMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = CARBON_USER_INFO, allEntries = true)
    public void deleteUserInfo(Long id) {
        validateUserInfoExists(id);
        userInfoMapper.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = CARBON_USER_INFO, allEntries = true)
    public void deleteUserInfoList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 查询用户信息
        List<CarbonUserInfoDO> userInfos = userInfoMapper.selectBatchIds(ids);

        userInfoMapper.deleteByIds(ids);

        // 删除燃气数据 和 电力数据
        Set<String> electricityIds = CollectionUtils.convertSet(userInfos, CarbonUserInfoDO::getElectricityId);
        Set<String> gasIds = CollectionUtils.convertSet(userInfos, CarbonUserInfoDO::getGasId);
        carbonElectricityService.deleteElectricityDataByElectricIds(electricityIds);
        carbonGasDataService.deleteGasDataByGasId(gasIds);
    }

    @Override
    @Cacheable(cacheNames = CARBON_USER_INFO, key = "#id")
    public CarbonUserInfoRespVO getUserInfo(Long id) {
        return CarbonUserInfoConvert.INSTANCE.convert(userInfoMapper.selectById(id));
    }

    @Override
    public PageResult<CarbonUserInfoRespVO> getUserInfoPage(CarbonUserInfoPageReqVO pageReqVO) {
        PageResult<CarbonUserInfoDO> pageResult = userInfoMapper.selectPage(pageReqVO);
        List<CarbonUserInfoDO> userInfos = pageResult.getList();
        PageResult<CarbonUserInfoRespVO> resultVo = CarbonUserInfoConvert.INSTANCE.convertPage(pageResult);
        if (!userInfos.isEmpty()) {
            areaUtils.fillAreaNames(resultVo.getList());
        }
        return resultVo;
    }

    @Override
    public List<CarbonUserInfoRespVO> getUserInfoList(CarbonUserInfoListReqVO listReqVO) {
        List<CarbonUserInfoRespVO> voList = CarbonUserInfoConvert.INSTANCE.convertList(userInfoMapper.selectList(listReqVO));
        areaUtils.fillAreaNames(voList);
        return voList;
    }


    @VisibleForTesting
    void validateUserInfoExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonUserInfoDO userInfo = userInfoMapper.selectById(id);
        if (userInfo == null) {
            throw exception(USER_INFO_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    void validateUserInfoIdCardUnique(Long id, String idCard) {
        if (idCard == null) {
            return;
        }
        CarbonUserInfoDO userInfo = userInfoMapper.selectByIdCard(idCard);
        if (userInfo == null) {
            return;
        }
        if (id == null) {
            throw exception(USER_INFO_ID_CARD_EXISTS);
        }
        if (!userInfo.getId().equals(id)) {
            throw exception(USER_INFO_ID_CARD_EXISTS);
        }
    }

    @Override
    @CacheEvict(cacheNames = CARBON_USER_INFO, allEntries = true)
    public void importUserInfoList(List<CarbonUserInfoImportVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<CarbonUserInfoDO> doList = new ArrayList<>();

        for (CarbonUserInfoImportVO importVO : list) {
            CarbonUserInfoDO userInfo = CarbonUserInfoConvert.INSTANCE.convert(importVO);

            // 根据行政区中文名称反查编码
            Long provinceCode = 13L;
            Long cityCode = resolveAreaCode(provinceCode, importVO.getCityName());
            Long districtCode = resolveAreaCode(cityCode, importVO.getDistrictName());
            Long townCode = resolveAreaCode(districtCode, importVO.getTownName());
            Long villageCode = resolveAreaCode(townCode, importVO.getVillageName());
            userInfo.setProvinceCode(provinceCode);
            userInfo.setCityCode(cityCode);
            userInfo.setDistrictCode(districtCode);
            userInfo.setTownCode(townCode);
            userInfo.setVillageCode(villageCode);

            // 根据字典中文名称反查编码
            userInfo.setReformType(resolveDictCode(ReformTypeEnum.values(), importVO.getReformType()));
            userInfo.setReformMode(resolveDictCode(ReformModeEnum.values(), importVO.getReformMode()));
            userInfo.setDataSource(resolveDictCode(UserDataSourceEnum.values(), importVO.getDataSource()));
            userInfo.setReformBatch(resolveDictCode(ReformBatchEnum.values(), importVO.getReformBatch()));
            userInfo.setSubsidyMethod(resolveDictCode(SubsidyMethodEnum.values(), importVO.getSubsidyMethod()));
            userInfo.setHouseUsage(resolveDictCode(HouseUsageEnum.values(), importVO.getHouseUsage()));
            userInfo.setUserCategory(resolveDictCode(UserCategoryEnum.values(), importVO.getUserCategory()));
            userInfo.setUseStatus(resolveDictCode(UseStatusEnum.values(), importVO.getUseStatus()));

            // 解析完区域编码与数据来源后再生成用户编码
            userInfo.setUserCode(userCodeGenerator.generate(userInfo.getDataSource(), userInfo.getProvinceCode(),
                    userInfo.getCityCode(), userInfo.getDistrictCode()));

            doList.add(userInfo);
        }
        userInfoMapper.insertBatch(doList);
    }

    /**
     * 根据父级编码和行政区名称解析编码
     */
    private Long resolveAreaCode(Long parentId, String name) {
        if (parentId == null || StrUtil.isBlank(name)) {
            return null;
        }
        AreaRespDTO area = areaApi.getAreaByName(parentId, name).getData();
        return area != null ? area.getId() : null;
    }

    /**
     * 根据字典中文名称反查编码
     */
    private String resolveDictCode(ArrayValuable<String>[] enumValues, String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        for (ArrayValuable<String> enumValue : enumValues) {
            // 通过反射获取name字段
            try {
                java.lang.reflect.Field nameField = enumValue.getClass().getDeclaredField("name");
                nameField.setAccessible(true);
                String enumName = (String) nameField.get(enumValue);
                if (name.equals(enumName)) {
                    java.lang.reflect.Field typeField = enumValue.getClass().getDeclaredField("type");
                    typeField.setAccessible(true);
                    return (String) typeField.get(enumValue);
                }
            } catch (Exception e) {
                log.warn("解析字典编码失败: {}", enumValue, e);
            }
        }
        return name; // 找不到匹配的枚举则返回原值
    }

}
