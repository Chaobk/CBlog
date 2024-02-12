package com.chaobk.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.chaobk.entity.User;

/**
 * @Description: 用户持久层接口
 * @Author: Naccl
 * @Date: 2020-07-19
 */
@Mapper
@Repository
public interface UserMapper {
	User findByUsername(String username);

	User findById(Long id);

	int updateUserByUsername(String username, User user);
}
