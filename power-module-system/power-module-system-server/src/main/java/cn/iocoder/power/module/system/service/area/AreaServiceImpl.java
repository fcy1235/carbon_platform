package cn.iocoder.power.module.system.service.area;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.system.convert.area.AreaConvert;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import cn.iocoder.power.module.system.controller.admin.area.vo.*;
import cn.iocoder.power.module.system.dal.dataobject.area.AreaDO;
import cn.iocoder.power.module.system.dal.mysql.area.AreaMapper;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.system.enums.ErrorCodeConstants.*;

/**
 * 行政区 Service 实现类
 *
 * @author system
 */
@Service
@Validated
@Slf4j
public class AreaServiceImpl implements AreaService {

    @Resource
    private AreaMapper areaMapper;

    @Override
    public Long createArea(AreaSaveReqVO createReqVO) {
        if (createReqVO.getParentId() == null) {
            createReqVO.setParentId(AreaDO.PARENT_ID_ROOT);
        }

        // 判断是否存在相同 ID 的记录（包括已删除的），如果存在则执行更新操作
        AreaDO existingArea = areaMapper.selectById(createReqVO.getId());

        if (existingArea != null) {
            // 已存在，执行更新操作
            // 如果记录已被软删除，先恢复
            if (areaMapper.selectById(createReqVO.getId()) == null) {
                areaMapper.updateById(existingArea);

            }
            // 校验父行政区的有效性
            validateParentArea(createReqVO.getId(), createReqVO.getParentId());
            // 校验行政区名的唯一性
            validateAreaNameUnique(createReqVO.getId(), createReqVO.getParentId(), createReqVO.getName());

            // 更新行政区
            AreaDO updateObj = AreaConvert.INSTANCE.convert(createReqVO);
            areaMapper.updateById(updateObj);
            return updateObj.getId();
        }

        // 不存在，执行新增操作
        // 校验父行政区的有效性
        validateParentArea(null, createReqVO.getParentId());
        // 校验行政区名的唯一性
        validateAreaNameUnique(null, createReqVO.getParentId(), createReqVO.getName());

        // 插入行政区
        AreaDO area = AreaConvert.INSTANCE.convert(createReqVO);
        areaMapper.insert(area);
        return area.getId();
    }

    @Override
    public void updateArea(AreaSaveReqVO updateReqVO) {
        if (updateReqVO.getParentId() == null) {
            updateReqVO.setParentId(AreaDO.PARENT_ID_ROOT);
        }
        // 校验自己存在
        validateAreaExists(updateReqVO.getId());
        // 校验父行政区的有效性
        validateParentArea(updateReqVO.getId(), updateReqVO.getParentId());
        // 校验行政区名的唯一性
        validateAreaNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());

        // 更新行政区
        AreaDO updateObj = AreaConvert.INSTANCE.convert(updateReqVO);
        areaMapper.updateById(updateObj);
    }

    @Override
    public void deleteArea(Long id) {
        // 校验是否存在
        validateAreaExists(id);
        // 校验是否有子行政区
        if (areaMapper.selectCountByParentId(id) > 0) {
            throw exception(AREA_EXITS_CHILDREN);
        }
        // 删除行政区
        areaMapper.deleteById(id);
    }

    @Override
    public void deleteAreaList(List<Long> ids) {

        // 校验是否有子行政区
        for (Long id : ids) {
            if (areaMapper.selectCountByParentId(id) > 0) {
                throw exception(AREA_EXITS_CHILDREN);
            }
        }
        // 批量删除行政区
        areaMapper.deleteByIds(ids);
    }

    @VisibleForTesting
    void validateAreaExists(Long id) {
        if (id == null) {
            return;
        }
        AreaDO area = areaMapper.selectById(id);
        if (area == null) {
            throw exception(AREA_NOT_FOUND);
        }
    }

    @VisibleForTesting
    void validateParentArea(Long id, Long parentId) {
        if (parentId == null || AreaDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父行政区
        if (Objects.equals(id, parentId)) {
            throw exception(AREA_PARENT_ERROR);
        }
        // 2. 父行政区不存在
        AreaDO parentArea = areaMapper.selectById(parentId);
        if (parentArea == null) {
            throw exception(AREA_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父行政区，如果父行政区是自己的子行政区，则报错，避免形成环路
        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentArea.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(AREA_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父行政区
            if (parentId == null || AreaDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentArea = areaMapper.selectById(parentId);
            if (parentArea == null) {
                break;
            }
        }
    }

    @VisibleForTesting
    void validateAreaNameUnique(Long id, Long parentId, String name) {
        AreaDO area = areaMapper.selectByParentIdAndName(parentId, name);
        if (area == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的行政区
        if (id == null) {
            throw exception(AREA_NAME_DUPLICATE);
        }
        if (ObjectUtil.notEqual(area.getId(), id)) {
            throw exception(AREA_NAME_DUPLICATE);
        }
    }

    @Override
    public AreaRespVO getArea(Long id) {
        return AreaConvert.INSTANCE.convert(areaMapper.selectById(id));
    }

    @Override
    public List<AreaRespVO> getAreaList(AreaListReqVO reqVO) {
        return AreaConvert.INSTANCE.convertList(areaMapper.selectList(reqVO));
    }

    @Override
    public PageResult<AreaRespVO> getAreaPage(AreaPageReqVO pageReqVO) {
        return AreaConvert.INSTANCE.convertPage(areaMapper.selectPage(pageReqVO));
    }

    @Override
    public List<AreaSimpleRespVO> getAreaSimpleList(AreaListReqVO reqVO) {
        return AreaConvert.INSTANCE.convertSimpleList(areaMapper.selectList(reqVO));
    }

    @Override
    public List<AreaTreeRespVO> getAreaTree(AreaListReqVO reqVO) {
        List<AreaDO> list = areaMapper.selectList(reqVO);
        Long rootParentId = reqVO.getParentId() != null ? reqVO.getParentId() : AreaDO.PARENT_ID_ROOT;
        return buildAreaTree(list, rootParentId);
    }

    /**
     * 构建行政区树
     *
     * @param list 行政区列表
     * @param rootParentId 根节点父级编号
     * @return 行政区树
     */
    private List<AreaTreeRespVO> buildAreaTree(List<AreaDO> list, Long rootParentId) {
        if (rootParentId == null) {
            rootParentId = AreaDO.PARENT_ID_ROOT;
        }
        final Long finalRootParentId = rootParentId;
        List<AreaTreeRespVO> rootList = list.stream()
                .filter(area -> finalRootParentId.equals(area.getParentId()))
                .map(area -> AreaConvert.INSTANCE.convertTree(area))
                .collect(Collectors.toList());
        // 递归设置子节点
        for (AreaTreeRespVO root : rootList) {
            setChildren(root, list);
        }
        return rootList;
    }

    /**
     * 递归设置子节点
     */
    private void setChildren(AreaTreeRespVO parent, List<AreaDO> list) {
        List<AreaTreeRespVO> children = list.stream()
                .filter(area -> parent.getId().equals(area.getParentId()))
                .map(area -> AreaConvert.INSTANCE.convertTree(area))
                .collect(Collectors.toList());
        parent.setChildren(children);
        for (AreaTreeRespVO child : children) {
            setChildren(child, list);
        }
    }

    @Override
    public List<AreaDO> getAllAreaList() {
        return areaMapper.selectList();
    }

    @Override
    public List<AreaDO> getChildAreaList(Long id) {
        List<AreaDO> children = new LinkedList<>();
        // 遍历每一层
        List<Long> parentIds = Collections.singletonList(id);
        for (int i = 0; i < Short.MAX_VALUE; i++) { // 使用 Short.MAX_VALUE 避免 bug 场景下，存在死循环
            // 查询当前层，所有的子行政区
            List<AreaDO> areas = new LinkedList<>();
            for (Long parentId : parentIds) {
                areas.addAll(areaMapper.selectListByParentId(parentId));
            }
            // 1. 如果没有子行政区，则结束遍历
            if (CollUtil.isEmpty(areas)) {
                break;
            }
            // 2. 如果有子行政区，继续遍历
            children.addAll(areas);
            parentIds = areas.stream().map(AreaDO::getId).toList();
        }
        return children;
    }

    @Override
    public AreaRespDTO getAreaDTO(Long id) {
        return AreaConvert.INSTANCE.convertDTO(areaMapper.selectById(id));
    }

    @Override
    public List<AreaRespDTO> getAreaDTOList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return AreaConvert.INSTANCE.convertDTOList(areaMapper.selectBatchIds(ids));
    }

    @Override
    public List<AreaRespDTO> getChildAreaDTOList(Long id) {
        return AreaConvert.INSTANCE.convertDTOList(getChildAreaList(id));
    }

    @Override
    public AreaRespDTO getAreaDTOByName(Long parentId, String name) {
        AreaDO area = areaMapper.selectByParentIdAndName(parentId, name);
        return AreaConvert.INSTANCE.convertDTO(area);
    }

}
