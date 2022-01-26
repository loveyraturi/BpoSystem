package com.praveen.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Campaing;
import com.praveen.model.Leads;

public interface LeadsRepository extends JpaRepository<Leads, Integer> {

	@Query(value="select count(*),table1.campaing_name from leads INNER JOIN lead_versions as table1 ON table1.filename = leads.filename where table1.status='Y' GROUP BY table1.campaing_name", nativeQuery = true)
	 List<Object[]> fetchTotalLeadsCount();
	
	 @Query(value="select count(*),table1.campaing_name from leads INNER JOIN lead_versions as table1 ON table1.filename = leads.filename where table1.status='Y' and leads.status='ACTIVE' GROUP BY table1.campaing_name", nativeQuery = true)
	 List<Object[]> fetchActiveLeads();
	
	@Query(value="select count(*),leads.assigned_to from leads INNER JOIN lead_versions as table1 ON table1.filename = leads.filename where table1.status='Y' and leads.status='ACTIVE' and table1.campaing_name=:campaingName group by leads.assigned_to", nativeQuery = true)
	 List<Object[]> fetchLeadsCountAssignedToUser(@Param("campaingName") String campaingName);
	
	@Query(value="select lead_versions.filename from lead_versions INNER JOIN campaing_lead_mapping as table1 ON table1.lead_version_name = lead_versions.filename where table1.campaing_name=:campaingName and lead_versions.status='Y'", nativeQuery = true)
	 List<String> findLeadsVersionsByCampaingName(@Param("campaingName") String campaingName);
	
	@Query(value="select * from leads where filename IN (:filename) and (status='ACTIVE') LIMIT 1", nativeQuery = true)
	 List<Leads> findLeadsByFilename(@Param("filename") List<String> filename);
	@Query(value="select * from leads where filename = :filename and status= :status", nativeQuery = true)
	 List<Leads> findLeadsByFilenameAndStatus(@Param("filename") String filename,@Param("status") String status);
	@Query(value="select * from leads where filename IN (:filename) and assigned_to=:assignedTo and (status='ACTIVE') LIMIT 200", nativeQuery = true)
	 List<Leads> findLeadsByFilenameAndUserName(@Param("filename") List<String> filename,@Param("assignedTo") String assignedTo);
	
	@Query(value="select leads.* from leads INNER JOIN campaing_lead_mapping as table1 ON table1.leadid = leads.id where table1.campaingid=:campaingid and status='ACTIVE' LIMIT 1", nativeQuery = true)
	 List<Leads> findLeadByCampaingId(@Param("campaingid") int campaingid);
	
	@Query(value="select leads.phone_number from leads INNER JOIN lead_versions as table1 ON table1.filename = leads.filename where table1.campaing_name=:campaingName", nativeQuery = true)
	 List<String> findLeadByCampaingName(@Param("campaingName") String campaingName);
	
	@Query(value="select * from leads where phone_number=:phoneNumber LIMIT 1", nativeQuery = true)
	 List<Leads> findLeadByNumber(@Param("phoneNumber") String phoneNumber);
	
	@Query(value="select * from leads  where first_name=:firstname ", nativeQuery = true)
	 Leads findLeadByFirstName(@Param("firstname") String firstname);
	
//	@Query(value="select name,status,count(status) as count from leads where  date_modified<=:fromDate and date_modified>=:toDate and id IN (:leadIds) GROUP BY name,status", nativeQuery = true)
//	 List<Object[]> fetchcountreportdatabetween(@Param("leadIds") List<Integer> leadIds, @Param("fromDate") String fromDate,@Param("toDate") String toDate);
//	             
	
//	             "select name,status,count(status) as count from leads where  date_modified BETWEEN  TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS') and id IN (1,2) GROUP BY name,status"
//	@Query(value="select name,status,count(status) as count from leads where call_date >=  TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS') and id IN (:leadIds) GROUP BY name,status", nativeQuery = true)
//	 List<Object[]> fetchcountreportdatabetween(@Param("leadIds") List<Integer> leadIds, @Param("fromDate") String fromDate,@Param("toDate") String toDate);
//	 @Query(value="select status,count(status) as count from leads where call_date >=  TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS') and name=:userName GROUP BY status", nativeQuery = true)
//	 List<Object[]> fetchcountreportdatabetweenWithUsers(@Param("userName") String userName, @Param("fromDate") String fromDate,@Param("toDate") String toDate); 
//	 
//	 @Query(value="select * from leads where name=:userName and status!='ACTIVE'", nativeQuery = true)
//	 List<Leads> fetchreportdatabetweenWithUserName(@Param("userName") String userName);
//	
//	@Query(value="select * from leads where call_date >=  TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS') and id IN (:leadIds)", nativeQuery = true)
//	 List<Leads> fetchreportdatabetween(@Param("leadIds") List<Integer> leadIds, @Param("fromDate") String fromDate,@Param("toDate") String toDate);
//	
}
