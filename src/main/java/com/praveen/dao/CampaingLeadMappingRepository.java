package com.praveen.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.CampaingLeadMapping;
import com.praveen.model.Users;


public interface CampaingLeadMappingRepository extends JpaRepository<CampaingLeadMapping, Integer> {
	@Query(value="SELECT table1.leadid FROM campaing INNER JOIN campaing_lead_mapping as table1 ON campaing.id = table1.campaingid where campaing.name IN (:campaingname)", nativeQuery = true)
	List<Integer> findLeadByCampaingName(@Param("campaingname") List<String> campaingname);	
	@Query(value="SELECT id FROM leads where filename IN (select lead_version_name from campaing_lead_mapping where campaing_name IN (:campaingname)) and status!='ACTIVE' order by id", nativeQuery = true)
	List<Integer> findLeadsByCampaingName(@Param("campaingname") List<String> campaingname);	
}
