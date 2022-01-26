package com.praveen.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Campaing;
import com.praveen.model.LeadVersionCountAndData;
import com.praveen.model.LeadVersions;

public interface LeadVersionsRepository extends JpaRepository<LeadVersions, Integer> {
	@Query(value="select * from lead_versions  where filename=:filename ", nativeQuery = true)
	LeadVersions findByFileName(@Param("filename") String filename);
    @Query(value="select filename, count(*) from lead_versions group by filename ",nativeQuery = true)
    List<Object[]> findCountAndLeads();
}
