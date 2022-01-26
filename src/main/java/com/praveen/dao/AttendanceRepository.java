package com.praveen.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
	@Query(value="select * from attendance  where username IN (:username) and logged_in_time < :dateTOTimestamp and logged_in_time > :dateFromTimestamp", nativeQuery = true)
	 List<Attendance> findAttendanceByUserName(@Param("username") List<String> username,@Param("dateFromTimestamp") Timestamp dateFromTimestamp,@Param("dateTOTimestamp") Timestamp dateTOTimestamp);
	@Query(value="select * from attendance  where username=:username and logged_in_time > :loggedInTime ORDER  BY logged_in_time  DESC ", nativeQuery = true)
	 List<Attendance> findAttendanceByUserNameAndDate(@Param("username") String username,@Param("loggedInTime") Timestamp loggedInTime);

	//	@Query(value="select username,MAX(logged_in_time),MAX() from attendance  where username=:username and logged_in_time > :loggedInTime ORDER  BY logged_in_time  DESC ", nativeQuery = true)
	//	 List<Attendance> findUserAttendanceByUserNameAndDate(@Param("username") String username,@Param("loggedInTime") Timestamp loggedInTime);
	//	@Modifying
	//	@Query("update attendance set logged_out_time=:loggedOutTime,total_work_hour=:totalWorkHour where id=:id")
	//	void userLoggedOut(@Param("id") int id,@Param("totalWorkHour") int totalWorkHour);
	//	@Modifying
	//	@Query("update attendance set logged_out_time=:loggedOutTime,total_work_hour=DATEDIFF(hour, loggedInTime, :logged_out_time) where id=:id")
	//	void userLoggedOut(@Param("id") int id,@Param("loggedInTime") Timestamp loggedInTime,@Param("loggedOutTime") Timestamp loggedOutTime);
	//	@Modifying
	//	@Query("update attendance set logged_in_time=:loggedInTime,logged_out_time=:loggedOutTime,total_work_hour=DATEDIFF(hour, :loggedInTime, :logged_out_time) where id=:id")
	//	void userLoggedIn(@Param("date") Date date);
}
