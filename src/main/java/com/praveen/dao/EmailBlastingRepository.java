package com.praveen.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Attendance;
import com.praveen.model.EmailBlasting;
import com.praveen.model.Recordings;

@Transactional
public interface EmailBlastingRepository extends JpaRepository<EmailBlasting, Integer> {
	@Query(value="select * from email_blasting where campaing_name=:campaingName ", nativeQuery = true)
	 List<EmailBlasting> fetchEmailByCampaingName(@Param("campaingName") String campaingName);
	@Query(value="select * from email_blasting where file_name=:fileName ", nativeQuery = true)
	 List<EmailBlasting> showEmailDataByFileName(@Param("fileName") String campaingName);
	@Query(value="select distinct file_name,campaing_name from email_blasting", nativeQuery = true)
	 List<Object[]> fileNames();
	@Query(value="select distinct file_name,campaing_name from email_blasting where campaing_name=:campaingName ", nativeQuery = true)
	 List<Object[]> fileNamesByCampaingName(@Param("campaingName") String campaingName);
	@Modifying
	@Query(value = "delete from email_blasting  where campaing_name=:campaingName ", nativeQuery = true)
	void deleteByCampaingName(@Param("campaingName") String campaingName);
}