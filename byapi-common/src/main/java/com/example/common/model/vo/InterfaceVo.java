package com.example.common.model.vo;

import com.example.common.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口信息返回类
 *
 * @author by
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceVo extends InterfaceInfo {

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;
}
