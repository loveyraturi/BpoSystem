package com.praveen.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Campaing;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {
	@Query(value="select * from user_group  where name=:name LIMIT 1", nativeQuery = true)
	 UserGroup findGroupByName(@Param("name") String name);
	
}
