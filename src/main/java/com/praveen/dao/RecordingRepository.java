package com.praveen.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Break;
import com.praveen.model.Campaing;
import com.praveen.model.Recordings;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

public interface RecordingRepository extends JpaRepository<Recordings, Integer> {	
	@Query(value="select * from recordings where username=:username ", nativeQuery = true)
	 List<Recordings> fetchRecordingsByUsername(@Param("username") String username);
	@Query(value="select * from recordings  where recording_name=:filename ", nativeQuery = true)
	 Recordings fetchRecordingsByFilename(@Param("filename") String filename);
	@Query(value="select call_logs.assigned_to,call_logs.call_end_date,call_logs.call_date,call_logs.status,call_logs.call_duration,recordings.recording_name,leads.phone_number,leads.filename,recordings.campaing from recordings INNER JOIN call_logs ON call_logs.lead_id=recordings.lead_id INNER JOIN leads ON leads.id = recordings.lead_id where call_logs.assigned_to IN (:users) and call_logs.call_date >= :fromDate and call_logs.call_end_date <= :toDate or leads.phone_number=:phoneNumber  order by call_logs.call_date desc LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<Object[]> fetchRecordingsByLeadIds(@Param("phoneNumber") String phoneNumber,@Param("users") List<String> users,@Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate,@Param("limit") Integer limit,@Param("offset") Integer offset);
	@Query(value="select call_logs.assigned_to,call_logs.call_end_date,call_logs.call_date,call_logs.status,call_logs.call_duration,recordings.recording_name,leads.phone_number,leads.filename,recordings.campaing from recordings INNER JOIN call_logs ON call_logs.lead_id=recordings.lead_id INNER JOIN leads ON leads.id = recordings.lead_id where ((call_logs.assigned_to IN (:users) and call_logs.call_date >= :fromDate and call_logs.call_end_date <= :toDate) or leads.phone_number=:phoneNumber) and call_logs.status IN (:status) order by call_logs.call_date desc LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<Object[]> fetchRecordingsByLeadIdsAndStatus(@Param("status") List<String> status,@Param("phoneNumber") String phoneNumber,@Param("users") List<String> users,@Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate,@Param("limit") Integer limit,@Param("offset") Integer offset);
}
