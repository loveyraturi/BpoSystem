package com.praveen.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.praveen.model.UserGroupMapping;
import com.praveen.model.Users;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UsersRepository extends JpaRepository<Users, Integer> {
	
//	@Query(value="SELECT id,filename,enabled,version FROM versions WHERE filename=:filename and version=:version", nativeQuery = true)
//	 List<Versions> findByFilenameAndVersion(@Param("filename")String filename,@Param("version") String version);
//	
//	@Query(value="SELECT distinct id,filename,enabled,version FROM versions WHERE filename=:filename", nativeQuery = true)
//	 List<Versions> findAllByName(@Param("filename") String filename);
	
	@Query(value="SELECT * FROM users where username=:username and password=:password", nativeQuery = true)
	Users validateUser(@Param("username") String username,@Param("password") String password);	
	
	@Query(value="SELECT * FROM users where username=:username", nativeQuery = true)
	List<Users> findByUsername(@Param("username") String username);	
	
	@Query(value="SELECT * FROM users where usergroup=:usergroup", nativeQuery = true)
	List<Users> findUserByGroupName(@Param("usergroup") String usergroup);	
	
	@Query(value="SELECT distinct table1.campaingname FROM users INNER JOIN group_campaing_mapping as table1 ON users.usergroup = table1.groupname where users.username IN (:username) and table1.campaingname IN (:campaingname)", nativeQuery = true)
	List<String> fetchCampaingOfUser(@Param("username") List<String> username,@Param("campaingname") List<String> campaingname);	
	
	@Query(value="SELECT users.* FROM campaing LEFT JOIN group_campaing_mapping ON group_campaing_mapping.campaingname=campaing.name LEFT JOIN users ON group_campaing_mapping.groupname=users.usergroup where campaing.name=:campaingName ", nativeQuery = true)
	List<Users> fetchActiveCampaingWithUsers(@Param("campaingName") String campaingName);
	
	@Query(value="SELECT users.* FROM campaing LEFT JOIN group_campaing_mapping ON group_campaing_mapping.campaingname=campaing.name LEFT JOIN users ON group_campaing_mapping.groupname=users.usergroup where campaing.name=:campaingName and users.online=1", nativeQuery = true)
	List<Users> fetchOnlineUsersByCampaingName(@Param("campaingName") String campaingName);
	
	@Query(value="SELECT users.username FROM campaing LEFT JOIN group_campaing_mapping ON group_campaing_mapping.campaingname=campaing.name LEFT JOIN users ON group_campaing_mapping.groupname=users.usergroup where campaing.name=:campaingName ", nativeQuery = true)
	List<String> fetchUserNameByCampaingName(@Param("campaingName") String campaingName);
	
	@Query(value="SELECT users.username FROM campaing LEFT JOIN group_campaing_mapping ON group_campaing_mapping.campaingname=campaing.name LEFT JOIN users ON group_campaing_mapping.groupname=users.usergroup where campaing.name IN (:campaingName) ", nativeQuery = true)
	List<String> fetchUserNameByCampaingNames(@Param("campaingName") List<String> campaingName);
	
	
//	@Modifying
//	@Query(value="DELETE FROM `versions` WHERE version=:version and filename=:filename", nativeQuery = true)
//	 void deleteByVersionAndFilename(@Param("filename") String filename,@Param("version") String version);	

}
