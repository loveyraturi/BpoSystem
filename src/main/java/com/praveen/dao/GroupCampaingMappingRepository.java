package com.praveen.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Campaing;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.UserGroup;
import com.praveen.model.UserGroupMapping;
import com.praveen.model.Users;

public interface GroupCampaingMappingRepository extends JpaRepository<GroupCampaingMapping, Integer> {
	@Query(value = "select * from group_campaing_mapping  where groupname=:groupname ", nativeQuery = true)
	List<GroupCampaingMapping> findByGroupName(@Param("groupname") String groupname);

	@Query(value = "select distinct groupname from group_campaing_mapping  where campaingname=:campaingname ", nativeQuery = true)
	List<String> findDistinctGroupByCampaingName(@Param("campaingname") String campaingname);

	@Query(value = "select * from group_campaing_mapping  where campaingname=:campaingname ", nativeQuery = true)
	GroupCampaingMapping findGroupByCampaingName(@Param("campaingname") String campaingname);
	
	@Modifying
	@Query(value = "delete from group_campaing_mapping  where groupname=:groupName ", nativeQuery = true)
	void deleteByGroupName(@Param("groupName") String groupName);

}
