package com.praveen.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Campaing;
import com.praveen.model.UserGroup;
import com.praveen.model.UserGroupMapping;
import com.praveen.model.Users;

public interface UserGroupMappingRepository extends JpaRepository<UserGroupMapping, Integer> {
	@Query(value="select table1.* from users INNER JOIN user_group_mapping as table1 ON users.username = table1.username where users.username=:username ", nativeQuery = true)
	 List<UserGroupMapping> findGroupByUsername(@Param("username") String username);
	@Modifying
	@Query(value="delete from user_group_mapping where groupname=:name", nativeQuery = true)
	void deleteGroupByName(@Param("name") String name);
}
