package com.example.common.model.dto;

import com.example.common.utils.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author by
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfacePageDto extends PageRequest {
    /**
     * 主键
     */
    private Long id;

    /**
     * 调用接口用户ID
     */
    private Long userId;

    /**
     * 接口ID
     */
    private Long interfaceInfoId;
}
