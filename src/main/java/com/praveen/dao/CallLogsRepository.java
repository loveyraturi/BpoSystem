package com.praveen.dao;

import java.util.Date;

import java.sql.Timestamp;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.CallLogs;
import com.praveen.model.Campaing;
import com.praveen.model.Leads;

public interface CallLogsRepository extends JpaRepository<CallLogs, Integer> {
	@Query(value="select assigned_to,phone_number,status,assigned_to as name,comments,call_duration,call_date,call_end_date,recording,call_type from call_logs where  call_date >= :fromDate and call_end_date <= :toDate and assigned_to IN (:userNames) ", nativeQuery = true)
	 List<Object[]> fetchreportdatabetween(@Param("userNames") List<String> userNames, @Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);
	 @Query(value="select assigned_to,phone_number,status,assigned_to as name,comments,call_duration,call_date,call_end_date,recording,call_type from call_logs where call_date >= :fromDate and call_end_date <= :toDate and phone_number=:phoneNumber ", nativeQuery = true)
	 List<Object[]> fetchreportdatabetweenByPhoneNumber(@Param("phoneNumber") String phoneNumber, @Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);
	 @Query(value="select assigned_to,status,count(status) as count from call_logs where call_date >=  :fromDate and call_end_date <= :toDate and assigned_to IN (:userNames) GROUP BY assigned_to,status", nativeQuery = true)
	 List<Object[]> fetchcountreportdatabetween(@Param("userNames") List<String> userNames, @Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);
	 @Query(value="select call_logs.assigned_to,call_logs.status,count(call_logs.status) as count from call_logs INNER JOIN leads as table1 ON table1.id = call_logs.lead_id  where call_logs.call_date >=  :fromDate and call_logs.call_end_date <= :toDate and table1.phone_number=:phoneNumber GROUP BY call_logs.assigned_to,call_logs.status", nativeQuery = true)
	 List<Object[]> fetchcountreportdatabetweenByPhoneNumber(@Param("phoneNumber") String phoneNumber, @Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);
	 @Query(value="select status,count(status) as count from call_logs where call_date >=  TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS') and assigned_to=:userName GROUP BY status", nativeQuery = true)
	 List<Object[]> fetchcountreportdatabetweenWithUsers(@Param("userName") String userName, @Param("fromDate") String fromDate,@Param("toDate") String toDate); 
	 @Query(value="select * from call_logs where assigned_to=:userName and status!='ACTIVE' and call_date >=  :fromDate and call_end_date <= :toDate", nativeQuery = true)
	 List<CallLogs> fetchreportdatabetweenWithUserName(@Param("userName") String userName,@Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);	
//	 @Query(value="select * from call_logs where assigned_to=:userName and status='CALLBACK' and call_date BETWEEN NOW() - INTERVAL '24 HOURS' AND NOW()", nativeQuery = true)
//	 List<CallLogs> findAllCallbacks(@Param("userName") String userName);
	 @Query(value="select call_logs.assigned_to,table1.phone_number,call_logs.status,table1.first_name,call_logs.comments,call_logs.call_duration,call_logs.call_date,call_logs.call_end_date,call_logs.call_back_date_time,table1.crm,table1.id from call_logs INNER JOIN leads as table1 ON table1.id = call_logs.lead_id where call_logs.assigned_to=:userName and table1.status='CALLBACK'", nativeQuery = true)
	 List<Object[]> findAllCallbacks(@Param("userName") String userName);
	
}
