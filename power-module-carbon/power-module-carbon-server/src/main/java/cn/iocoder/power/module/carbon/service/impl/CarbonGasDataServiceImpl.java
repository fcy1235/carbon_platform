package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonGasDataConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonElectricityDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasDataDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonGasDataMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.service.CarbonGasDataService;
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
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.GAS_DATA_NOT_EXISTS;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.GAS_DATA_IMPORT_USER_NOT_FOUND;

@Service
@Validated
@Slf4j
public class CarbonGasDataServiceImpl implements CarbonGasDataService {

    @Resource
    private CarbonGasDataMapper gasDataMapper;

    @Resource
    private CarbonUserInfoMapper carbonUserInfoMapper;

    @Resource
    private CarbonUserInfoHelper carbonUserInfoHelper;

    @Resource
    private AreaApi areaApi;

    @Override
    public CarbonGasDataRespVO getGasData(Long id) {
        return CarbonGasDataConvert.INSTANCE.convert(gasDataMapper.selectById(id));
    }

    @Override
    public PageResult<CarbonGasDataRespVO> getGasDataPage(CarbonGasDataPageReqVO pageReqVO) {
        // 1. MPJLambdaWrapper 联表查询（分页 + SQL去重）
        PageResult<CarbonGasDataMapper.CarbonGasDataWithUserDTO> dtoPage = gasDataMapper.selectLatestPageWithUser(pageReqVO, pageReqVO.getPageNo(), pageReqVO.getPageSize());
        if (dtoPage.getList().isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }

        // 2. DTO → RespVO（MapStruct 自动映射）
        List<CarbonGasDataRespVO> voList = CarbonGasDataConvert.INSTANCE.convertDtoList(dtoPage.getList());

        // 3. 批量查询区县名称，填充 division（一次 RPC 调用）
        fillDivision(voList, dtoPage.getList());

        return new PageResult<>(voList, dtoPage.getTotal());
    }

    @Override
    public List<CarbonGasDataRespVO> getGasDataList(CarbonGasDataPageReqVO pageReqVO) {
        // 联表查询（不分页，用于导出，SQL已去重）
        List<CarbonGasDataMapper.CarbonGasDataWithUserDTO> dtoList =
                gasDataMapper.selectLatestListWithUser(pageReqVO);
        if (dtoList.isEmpty()) {
            return Collections.emptyList();
        }

        // DTO → RespVO（MapStruct 自动映射）
        List<CarbonGasDataRespVO> voList = CarbonGasDataConvert.INSTANCE.convertDtoList(dtoList);
        // 批量查询区县名称，填充 division（一次 RPC 调用）
        fillDivision(voList, dtoList);
        // 数据来源代码转中文
        fillDataSourceName(voList);
        return voList;
    }

    @Override
    public PageResult<CarbonGasDataRespVO> getGasDataDetail(String gasId) {
        List<CarbonGasDataDO> list = gasDataMapper.selectListByGasId(gasId);
        return new PageResult<>(CarbonGasDataConvert.INSTANCE.convertList(list), (long) list.size());
    }

    @Override
    public void syncGasData(Long id) {
        CarbonGasDataDO gasData = gasDataMapper.selectById(id);
        if (gasData == null) {
            throw exception(GAS_DATA_NOT_EXISTS);
        }
        BigDecimal usage = BigDecimal.valueOf(Math.floor(Math.random() * 5));
        BigDecimal oldTotal = gasData.getCurrentTotal() != null ? gasData.getCurrentTotal() : BigDecimal.ZERO;

        CarbonGasDataDO newRecord = new CarbonGasDataDO();
        newRecord.setGasId(gasData.getGasId());
        newRecord.setCarbonUserInfoId(gasData.getCarbonUserInfoId());
        newRecord.setDataSource(gasData.getDataSource());
        newRecord.setCurrentUsage(usage);
        newRecord.setCurrentTotal(oldTotal.add(usage));
        newRecord.setLastReading(oldTotal);
        newRecord.setReadingTime(LocalDateTime.now());
        newRecord.setTenantId(gasData.getTenantId());
        gasDataMapper.insert(newRecord);
    }

    @Override
    public void syncAllGasData() {
        List<CarbonGasDataDO> latestList = gasDataMapper.selectLatestList(new CarbonGasDataPageReqVO());
        if (CollUtil.isEmpty(latestList)) {
            return;
        }

        // Java 去重：按 gas_id 分组，每组取 reading_time 最新的一条
        Map<String, CarbonGasDataDO> deduplicatedMap = new LinkedHashMap<>();
        for (CarbonGasDataDO gasData : latestList) {
            deduplicatedMap.putIfAbsent(gasData.getGasId(), gasData);
        }

        for (CarbonGasDataDO gasData : deduplicatedMap.values()) {
            BigDecimal usage = BigDecimal.valueOf(Math.floor(Math.random() * 5));
            BigDecimal oldTotal = gasData.getCurrentTotal() != null ? gasData.getCurrentTotal() : BigDecimal.ZERO;

            CarbonGasDataDO newRecord = new CarbonGasDataDO();
            newRecord.setGasId(gasData.getGasId());
            newRecord.setCarbonUserInfoId(gasData.getCarbonUserInfoId());
            newRecord.setDataSource(gasData.getDataSource());
            newRecord.setCurrentUsage(usage);
            newRecord.setCurrentTotal(oldTotal.add(usage));
            newRecord.setLastReading(oldTotal);
            newRecord.setReadingTime(LocalDateTime.now());
            newRecord.setTenantId(gasData.getTenantId());
            gasDataMapper.insert(newRecord);
        }
    }

    /**
     * 批量查询区县名称，填充 division 字段
     */
    private void fillDivision(List<CarbonGasDataRespVO> voList,
                              List<CarbonGasDataMapper.CarbonGasDataWithUserDTO> dtoList) {
        Set<Long> areaIds = new HashSet<>();
        for (CarbonGasDataMapper.CarbonGasDataWithUserDTO dto : dtoList) {
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
            CarbonGasDataMapper.CarbonGasDataWithUserDTO dto = dtoList.get(i);
            String province = areaMap.get(dto.getProvinceCode());
            String city = areaMap.get(dto.getCityCode());
            String district = areaMap.get(dto.getDistrictCode());
            voList.get(i).setDivision(StringUtils.join(province, city, district));
        }
    }

    /**
     * 数据来源代码转中文名称
     */
    private void fillDataSourceName(List<CarbonGasDataRespVO> voList) {
        for (CarbonGasDataRespVO vo : voList) {
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
    public void importGasDataList(List<CarbonGasDataImportVO> list) {
        for (CarbonGasDataImportVO importVO : list) {
            // 根据身份证号+燃气表户号查询用户
            CarbonUserInfoDO userInfo = carbonUserInfoMapper.selectByIdCardAndGasId(
                    importVO.getIdCard(), importVO.getGasId());
            if (userInfo == null) {
                throw exception(GAS_DATA_IMPORT_USER_NOT_FOUND);
            }

            // 创建燃气数据记录
            CarbonGasDataDO gasData = new CarbonGasDataDO();
            gasData.setCarbonUserInfoId(userInfo.getId());
            gasData.setGasId(importVO.getGasId());
            gasData.setDataSource(importVO.getDataSource());
            gasData.setCurrentTotal(importVO.getCurrentTotal());
            gasData.setCurrentUsage(importVO.getCurrentUsage());
            gasData.setReadingTime(importVO.getReadingTime());
            gasDataMapper.insert(gasData);
        }
    }

    @Override
    public void deleteGasData(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        gasDataMapper.deleteBatchIds(ids);
    }

    @Override
    public void deleteGasDataByGasId(Collection<String> gasIds) {
        if (CollUtil.isEmpty(gasIds)) {
            return;
        }
        gasDataMapper.deleteBatch(CarbonGasDataDO::getGasId, gasIds);
    }
}
