package com.xue.repository.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xue.entity.model.User;
import com.xue.entity.model.UserExample;

public interface UserMapper {
    long countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    //    增
    int addUser(User user);
    //  查
    int selectUser(String username);
    //  改
    int updateUser(User user);
    //删除临时表
    int importDropTemTable();
    //创建临时表
    int importCreateTemTabel();
	//批量插入数据库       
    int importBatchInsert(List<User> list);
    
    void improtBatchSave();
    //批量插入临时表
    int importBatchTempInsert(List<User> list);
    //批量更新    
    int importBatchUpdate();
    
    int selectAll();
    
}