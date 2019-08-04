package dao.mapper;

import dao.bean.*;

import java.util.List;

import org.apache.ibatis.annotations.*;

public interface UserMapper {

    @Select("select * from user where id = #{id}")
    User findByID(Integer id);

    //返回的Integer值是变化的行数，@Options()会填充到实体 person 中。
    @Insert("insert into user(name, age) values(#{name}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer addUser(User person);

//    @Insert("insert into person(name, age) values(#{name}, #{age})")
//    Integer addPerson(@Param("name") String name, @Param("age") Integer age);

    @Update("update user set name = #{name}, age = #{age} where id = #{id}")
    Integer updatePerson(@Param("name") String name, @Param("age") Integer age, @Param("id") int id);

    @Delete("delete from user where id = #{id}")
    Integer deletePerson(Integer id);

    @Select("select * from user")
    List<User> findAll();

    @Select("select * from user where name = #{name}")
    List<User> findByName(String name);
}