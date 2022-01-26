package com.praveen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.dao.AttendanceRepository;
import com.praveen.dao.BreakTypesRepository;
import com.praveen.dao.CampaingLeadMappingRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.LeadVersionsRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.dao.RecordingRepository;
import com.praveen.dao.UserGroupMappingRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.Attendance;
import com.praveen.model.Users;

@Service
public class AttendanceService {

	@Autowired
	private UsersRepository userRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	UserGroupMappingRepository userGroupMappingRepository;
	@Autowired
	GroupCampaingMappingRepository groupCampaingMappingRepository;
	@Autowired
	LeadsRepository leadsRepository;
	@Autowired
	CampaingRepository campaingRepository;
	@Autowired
	CampaingLeadMappingRepository campaingLeadMappingRepository;
	@Autowired
	LeadVersionsRepository leadVersionsRepository;
	@Autowired
	RecordingRepository recordingRepository;
	@Autowired
	BreakTypesRepository breakTypesRepository;
	@Autowired
	AttendanceRepository attendanceRepository;

//	public List<Attendance> fetchAttendanceByCampaingName(String CampaingName){
//		List<String> userNames= userRepository.fetchUserNameByCampaingName(CampaingName);
//		return attendanceRepository.findAttendanceByUserName(userNames);
//	}
}
