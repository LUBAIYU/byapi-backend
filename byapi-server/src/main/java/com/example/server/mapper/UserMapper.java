package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.model.dto.UserPageDto;
import com.example.common.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author by
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户信息
     *
     * @param pageCondition 分页条件
     * @param userPageDto   查询参数
     * @return 用户列表
     */
    IPage<User> listUsersByPage(@Param("pageCondition") IPage<User> pageCondition, @Param("dto") UserPageDto userPageDto);
}




