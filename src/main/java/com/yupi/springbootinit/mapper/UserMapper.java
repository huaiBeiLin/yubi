package com.yupi.springbootinit.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.yupi.springbootinit.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author hp
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-07-10 11:21:48
* @Entity generator.domain.User
*/
public interface UserMapper extends BaseMapper<User> {

}




