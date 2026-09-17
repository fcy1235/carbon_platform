package cn.iocoder.power.module.system.api.area.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 行政区 Response DTO
 *
 * @author system
 */
@Data
public class AreaRespDTO {

    /**
     * 编号
     */
    private Long id;

    /**
     * 父级编号
     */
    private Long parentId;

    /**
     * 行政级别
     */
    private String level;

    /**
     * 名称
     */
    private String name;

    /**
     * 拼音首字母
     */
    private String pinyinPrefix;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 扩展ID
     */
    private String extId;

    /**
     * 扩展名称
     */
    private String extName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
