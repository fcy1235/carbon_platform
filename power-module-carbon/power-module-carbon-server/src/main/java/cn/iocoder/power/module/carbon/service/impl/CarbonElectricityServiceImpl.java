package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonElectricityConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonElectricityDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonElectricityMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.service.CarbonElectricityService;
import cn.iocoder.power.module.carbon.util.CarbonUserInfoHelper;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.power.module.carbon.enums.UserDataSourceEnum;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.ELECTRICITY_NOT_EXISTS;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.ELECTRICITY_IMPORT_USER_NOT_FOUND;

@Service
@Validated
@Slf4j
public class CarbonElectricityServiceImpl implements CarbonElectricityService {

    @Resource
    private CarbonElectricityMapper electricityMapper;

    @Resource
    private CarbonUserInfoMapper carbonUserInfoMapper;

    @Resource
    private CarbonUserInfoHelper carbonUserInfoHelper;

    @Resource
    private AreaApi areaApi;

    @Override
    public CarbonElectricityRespVO getElectricity(Long id) {
        return CarbonElectricityConvert.INSTANCE.convert(electricityMapper.selectById(id));
    }

    @Override
    public PageResult<CarbonElectricityRespVO> getElectricityPage(CarbonElectricityPageReqVO pageReqVO) {
        // 1. MPJLambdaWrapper 联表查询（分页 + SQL去重）
        PageResult<CarbonElectricityMapper.CarbonElectricityWithUserDTO> dtoPage =
                electricityMapper.selectLatestPageWithUser(pageReqVO, pageReqVO.getPageNo(), pageReqVO.getPageSize());
        if (dtoPage.getList().isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }

        // 2. DTO → RespVO（MapStruct 自动映射）
        List<CarbonElectricityRespVO> voList = CarbonElectricityConvert.INSTANCE.convertDtoList(dtoPage.getList());

        // 3. 批量查询区县名称，填充 division（一次 RPC 调用）
        fillDivision(voList, dtoPage.getList());

        return new PageResult<>(voList, dtoPage.getTotal());
    }

    @Override
    public List<CarbonElectricityRespVO> getElectricityList(CarbonElectricityPageReqVO pageReqVO) {
        // 联表查询（不分页，用于导出，SQL已去重）
        List<CarbonElectricityMapper.CarbonElectricityWithUserDTO> dtoList =
                electricityMapper.selectLatestListWithUser(pageReqVO);
        if (dtoList.isEmpty()) {
            return Collections.emptyList();
        }

        // DTO → RespVO（MapStruct 自动映射）
        List<CarbonElectricityRespVO> voList = CarbonElectricityConvert.INSTANCE.convertDtoList(dtoList);
        // 批量查询区县名称，填充 division（一次 RPC 调用）
        fillDivision(voList, dtoList);
        // 数据来源代码转中文
        fillDataSourceName(voList);
        return voList;
    }

    @Override
    public PageResult<CarbonElectricityRespVO> getElectricityDetail(String electricityId) {
        List<CarbonElectricityDO> list = electricityMapper.selectListByElectricityId(electricityId);
        return new PageResult<>(CarbonElectricityConvert.INSTANCE.convertList(list), (long) list.size());
    }

    @Override
    public void syncElectricity(Long id) {
        CarbonElectricityDO electricity = electricityMapper.selectById(id);
        if (electricity == null) {
            throw exception(ELECTRICITY_NOT_EXISTS);
        }
        BigDecimal usage = electricity.getCurrentUsage();
        BigDecimal oldTotal = electricity.getCurrentTotal() != null ? electricity.getCurrentTotal() : BigDecimal.ZERO;

        CarbonElectricityDO newRecord = new CarbonElectricityDO();
        newRecord.setElectricityId(electricity.getElectricityId());
        newRecord.setCarbonUserInfoId(electricity.getCarbonUserInfoId());
        newRecord.setDataSource(electricity.getDataSource());
        newRecord.setCurrentUsage(usage);
        newRecord.setCurrentTotal(oldTotal.add(usage));
        newRecord.setLastReading(oldTotal);
        newRecord.setReadingTime(LocalDateTime.now());
        newRecord.setTenantId(electricity.getTenantId());
        electricityMapper.insert(newRecord);
    }

    @Override
    public void syncAllElectricity() {
        List<CarbonElectricityDO> latestList = electricityMapper.selectLatestList(new CarbonElectricityPageReqVO());
        if (CollUtil.isEmpty(latestList)) {
            return;
        }

        // Java 去重：按 electricity_id 分组，每组取 reading_time 最新的一条
        Map<String, CarbonElectricityDO> deduplicatedMap = new LinkedHashMap<>();
        for (CarbonElectricityDO electricity : latestList) {
            deduplicatedMap.putIfAbsent(electricity.getElectricityId(), electricity);
        }

        for (CarbonElectricityDO electricity : deduplicatedMap.values()) {
            BigDecimal usage = BigDecimal.valueOf(Math.floor(Math.random() * 10));
            BigDecimal oldTotal = electricity.getCurrentTotal() != null ? electricity.getCurrentTotal() : BigDecimal.ZERO;

            CarbonElectricityDO newRecord = new CarbonElectricityDO();
            newRecord.setElectricityId(electricity.getElectricityId());
            newRecord.setCarbonUserInfoId(electricity.getCarbonUserInfoId());
            newRecord.setDataSource(electricity.getDataSource());
            newRecord.setCurrentUsage(usage);
            newRecord.setCurrentTotal(oldTotal.add(usage));
            newRecord.setLastReading(oldTotal);
            newRecord.setReadingTime(LocalDateTime.now());
            newRecord.setTenantId(electricity.getTenantId());
            electricityMapper.insert(newRecord);
        }
    }

    /**
     * 批量查询区县名称，填充 division 字段
     */
    private void fillDivision(List<CarbonElectricityRespVO> voList,
                              List<CarbonElectricityMapper.CarbonElectricityWithUserDTO> dtoList) {
        Set<Long> areaIds = new HashSet<>();
        for (CarbonElectricityMapper.CarbonElectricityWithUserDTO dto : dtoList) {
            if (dto.getProvinceCode() != null) areaIds.add(dto.getProvinceCode());
            if (dto.getCityCode() != null) areaIds.add(dto.getCityCode());
            if (dto.getDistrictCode() != null) areaIds.add(dto.getDistrictCode());
        }
        if (areaIds.isEmpty()) return;

        var areaResult = areaApi.getAreaList(new ArrayList<>(areaIds));
        if (areaResult == null || areaResult.getData() == null) return;

        Map<Long, String> areaMap = areaResult.getData().stream()
                .collect(Collectors.toMap(AreaRespDTO::getId, AreaRespDTO::getName, (a, b) -> a));

        for (int i = 0; i < voList.size(); i++) {
            CarbonElectricityMapper.CarbonElectricityWithUserDTO dto = dtoList.get(i);
            String province = areaMap.get(dto.getProvinceCode());
            String city = areaMap.get(dto.getCityCode());
            String district = areaMap.get(dto.getDistrictCode());
            voList.get(i).setDivision(StringUtils.join(province, city, district));
        }
    }

    /**
     * 数据来源代码转中文名称
     */
    private void fillDataSourceName(List<CarbonElectricityRespVO> voList) {
        for (CarbonElectricityRespVO vo : voList) {
            if (vo.getDataSource() != null) {
                for (UserDataSourceEnum enumVal : UserDataSourceEnum.values()) {
                    if (enumVal.getType().equals(vo.getDataSource())) {
                        vo.setDataSource(enumVal.getName());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void importElectricityList(List<CarbonElectricityImportVO> list) {
        for (CarbonElectricityImportVO importVO : list) {
            // 根据身份证号+电力表户号查询用户
            CarbonUserInfoDO userInfo = carbonUserInfoMapper.selectByIdCardAndElectricityId(
                    importVO.getIdCard(), importVO.getElectricityId());
            if (userInfo == null) {
                throw exception(ELECTRICITY_IMPORT_USER_NOT_FOUND);
            }

            // 创建电力数据记录
            CarbonElectricityDO electricity = new CarbonElectricityDO();
            electricity.setCarbonUserInfoId(userInfo.getId());
            electricity.setElectricityId(importVO.getElectricityId());
            electricity.setDataSource(importVO.getDataSource());
            electricity.setCurrentTotal(importVO.getCurrentTotal());
            electricity.setCurrentUsage(importVO.getCurrentUsage());
            electricity.setReadingTime(importVO.getReadingTime());
            electricityMapper.insert(electricity);
        }
    }

    @Override
    public void deleteElectricityDataByElectricIds(Collection<String> electricIds) {
        if (CollUtil.isEmpty(electricIds)) {
            return;
        }
        electricityMapper.deleteBatch(CarbonElectricityDO::getElectricityId, electricIds);
    }

    @Override
    public void deleteElectricityData(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        electricityMapper.deleteBatchIds(ids);
    }
}
