package com.praveen.service;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.praveen.dao.AttendanceRepository;
import com.praveen.dao.BreakTypesRepository;
import com.praveen.dao.CallLogsRepository;
import com.praveen.dao.CampaingLeadMappingRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.CornsJobRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.LeadVersionsRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.dao.RecordingRepository;
import com.praveen.dao.UserGroupMappingRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.Attendance;
import com.praveen.model.CallLogs;
import com.praveen.model.Campaing;
import com.praveen.model.CampaingLeadMapping;
import com.praveen.model.Corns;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.LeadVersions;
import com.praveen.model.Leads;
import com.praveen.model.Recordings;
import com.praveen.model.UserGroup;
import com.praveen.model.UserGroupMapping;
import com.praveen.model.Users;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UsersService {

	@Autowired
	private UsersRepository userRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	CallLogsRepository callLogsRepository;
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
	@Autowired
	CornsJobRepository cornsJobRepository;
	@Autowired
	EmailServiceImpl emailServiceImpl;

	public Map<String,String> scheduleCornJob(Map<String,String> request){
		try {
			emailServiceImpl.createRequest(request);
		JobDetail job = JobBuilder.newJob(CronJobs.class)
                .withIdentity("job", "group").build();
		Trigger trigger = null;
		System.out.println(request.get("scheduleType"));
		if (request.get("scheduleType").equals("firstHalf")) {
			trigger= TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();
		}else {
			 
			trigger =TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group")
		                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
		                .build();
		}
//        Corns cj= new Corns();
//        cj.setCampaingName(request.get("campaing"));
//        cj.setCronName(request.get("campaing")+"-"+request.get("scheduleType")+"-"+request.get("status"));
//        cj.setScheduleTime(request.get("scheduleType"));
//        cj.setStatus(request.get("status"));
//        cj.setTo(request.get("to"));
//        cornsJobRepository.save(cj);
        Scheduler scheduler1 = new StdSchedulerFactory().getScheduler();
        scheduler1.start();
        scheduler1.scheduleJob(job, trigger);
		}catch (Exception e) {
			e.printStackTrace();
		}

        
		return null;
		
	}
	public void getData() {
		callLogsRepository.findAll().forEach((elem) -> {
			System.out.println(elem.getLeadId());
		});
	}

	public void syncDataToCallLogs() {
		List<CallLogs> lcl = new ArrayList<>();
		leadsRepository.findAll().forEach((item) -> {
			CallLogs cl = new CallLogs();
			cl.setAssignedTo(item.getAssignedTo());
			cl.setCallDate(item.getCallDate());
			cl.setCallEndDate(item.getCallEndDate());
			cl.setCallDuration(item.getLast_local_call_time());
			cl.setCallType("Auto");
			cl.setComments(item.getComments());
			cl.setLeadId(item.getId());
			cl.setStatus(item.getStatus());
			lcl.add(cl);
		});
		callLogsRepository.saveAll(lcl);
		System.out.println("done##############33");
	}

	public Map<String, String> deleteUser(int id) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		userRepository.deleteById(id);
		return response;
	}

	public Map<String, String> deleteCampaing(int id) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		campaingRepository.deleteById(id);
		return response;
	}

	public Map<String, String> deleteGroup(String name) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		synchronized(this.getClass()) {
			userGroupRepository.deleteById(userGroupRepository.findGroupByName(name).getId());
			groupCampaingMappingRepository.deleteInBatch(groupCampaingMappingRepository.findByGroupName(name));
		}
		return response;
	}

	public Map<String, String> deleteBreakType(int id) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		breakTypesRepository.deleteById(id);
		return response;
	}

	public void uploadFile(MultipartFile file, String filePath, String username, String leadId, String campaing) {
		try {
			// System.out.println(filePath + file.getOriginalFilename());
			byte[] bytes = file.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(filePath + file.getOriginalFilename())));
			stream.write(bytes);
			stream.close();
			Recordings recordings = new Recordings();
			recordings.setRecordingName(file.getOriginalFilename());
			if (username != null) {
				recordings.setUsername(username);
			}
			if (leadId != null) {
				recordings.setLeadId(Integer.parseInt(leadId));
			}
			if (campaing != null) {
				recordings.setCampaing(campaing);
			}
			recordingRepository.save(recordings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadFile(MultipartFile file, String filePath, String username) {
		try {
			System.out.println(filePath + file.getOriginalFilename());
			byte[] bytes = file.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(filePath + file.getOriginalFilename())));
			stream.write(bytes);
			stream.close();
			Recordings recordings = new Recordings();
			recordings.setRecordingName(file.getOriginalFilename());
			recordings.setUsername(username);
			recordingRepository.save(recordings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> validateUser(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();

		if (userRepository.validateUser(request.get("username"), request.get("password")) == null) {
			response.put("status", "false");
			response.put("message", "Invalid Username or password");
		} else {
			Users user = userRepository.validateUser(request.get("username"), request.get("password"));
			if (user.getId() > 0) {

				if ("0".equals(user.getOnline()) || user.getOnline().isEmpty() || user.getOnline() == null) {
					user.setOnline("1");
					response.put("status", "true");
					response.put("message", "Successfully logged in");
					userRepository.save(user);

					LocalDateTime now = LocalDateTime.now();
					java.sql.Date currentDate = java.sql.Date.valueOf(now.toLocalDate());
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(currentDate);
					calendar.set(Calendar.MILLISECOND, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
					System.out.println(timestamp);
					Attendance attendance = new Attendance();
					attendance.setUsername(user.getUsername());
					attendance.setLoggedInTime(new Timestamp((new Date()).getTime()));
					attendance.setDeviceId(request.get("device_id"));
					attendanceRepository.save(attendance);

					// Attendance attendance = new Attendance();

				} else {
					response.put("status", "false");
					response.put("message", "User already logged in");
				}

			} else {
				response.put("status", "false");
				response.put("message", "Invalid Username or password");
			}
		}

		return response;
	}
	public Users login(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();

		if (userRepository.validateUser(request.get("userName"), request.get("password")) == null) {
			response.put("status", "false");
		} else {
			Users user = userRepository.validateUser(request.get("userName"), request.get("password"));
			if (user.getId() > 0) {
				return user;
			} else {
				response.put("status", "false");
			}
		}

		return new Users();
	}

	public List<Map<String, String>> fetchUsersByCampaing(String campaingName) {
		List<Map<String, String>> users = new ArrayList<>();
		groupCampaingMappingRepository.findDistinctGroupByCampaingName(campaingName).forEach((item) -> {
			userRepository.findUserByGroupName(item).forEach((element) -> {
				Map<String, String> userMap = new HashMap<>();
				userMap.put("id", String.valueOf(element.getId()));
				userMap.put("name", element.getUsername());
				users.add(userMap);
			});
		});
		return users;
	}

	public List<Users> fetchOnlineUsersByCampaingName(String campaingName) {
		return userRepository.fetchOnlineUsersByCampaingName(campaingName);
	}

	public List<Map<String, Object>> fetchLeadsByUserAndCampaing(Map<String, String> request) {
		List<Leads> response = new ArrayList<>();
		userGroupMappingRepository.findGroupByUsername(request.get("username")).forEach((groupMapping) -> {
			List<Campaing> campaings = campaingRepository.findCampaingByGroupName(groupMapping.getGroupname(),
					request.get("campaing"));

			// int count=0;
			campaings.forEach((campaing) -> {
				// campaing.getAssignmentType()
				List<String> filenames = leadsRepository.findLeadsVersionsByCampaingName(campaing.getName());
				List<Leads> leads = leadsRepository.findLeadsByFilename(filenames);
				response.addAll(leads);
			});
		});
		for (int i = 0; i < response.size(); i++) {
			if (i == 0) {
				System.out.println(response);
				Leads currentLead = response.get(i);
				System.out.println("############################");
				System.out.println(response.get(i).getId());
				System.out.println(request.get("username"));
				currentLead.setStatus("OCCUPIED");
				currentLead.setName(request.get("username"));
				leadsRepository.save(currentLead);
			}
		}
		List<Map<String, Object>> respList = new ArrayList<>();

		response.forEach((items) -> {
			JsonNode responseCrm = null;
			ObjectMapper mapper = new ObjectMapper();
			String fields = items.getCrm();
			if (!fields.isEmpty()) {
				System.out.println("inside");
				try {
					responseCrm = mapper.readTree(fields);
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					responseCrm = mapper.readTree("{}");
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Map<String, Object> resp = new HashMap<>();
			resp.put("id", items.getId());
			resp.put("name", items.getName());
			resp.put("assignedTo", items.getAssignedTo());
			resp.put("phoneNumber", items.getPhoneNumber());
			resp.put("firstName", items.getFirstName());
			resp.put("city", items.getCity());
			resp.put("state", items.getState());
			resp.put("email", items.getEmail());
			resp.put("crm", responseCrm);
			resp.put("status", items.getStatus());
			resp.put("callCount", items.getCallCount());
			resp.put("filename", items.getFilename());
			resp.put("dateCreated", items.getDateCreated());
			resp.put("dateModified", items.getDateModified());
			respList.add(resp);
		});
		return respList;
	}

	public void createLead(Leads leads) {
		leadsRepository.save(leads);
	}

	public List<Recordings> fetchRecordingsByUsername(String username) {
		return recordingRepository.fetchRecordingsByUsername(username);
	}

	public List<Recordings> fetchRecordings() {
		return recordingRepository.findAll();
	}

	// public void loadLeadsWithUser(Users user,Leads leads,UserGroupMapping
	// userGroupMapping) {
	// leadsRepository.save(leads);
	// userRepository.save(user);
	// userGroupMappingRepository.save(userGroupMapping);
	//
	// List<Leads> leadFound=
	// leadsRepository.findLeadByNumber(leads.getPhoneNumber());
	// if(leadFound.size()>0) {
	// CampaingLeadMapping clm = new CampaingLeadMapping();
	// clm.setCampaingid(1);
	// clm.setLeadid(leadFound.get(0).getId());
	// campaingLeadMappingRepository.save(clm);
	// }
	//
	// }
	public synchronized List<Leads>  fetchLeadByUserAndCampaing(Map<String, String> request) {
		List<Leads> response = new ArrayList<>();
		userGroupMappingRepository.findGroupByUsername(request.get("username")).forEach((groupMapping) -> {
			System.out.println(request.get("campaing"));
			List<Campaing> campaings = campaingRepository.findCampaingByGroupName(groupMapping.getGroupname(),
					request.get("campaing"));
			campaings.forEach((campaing) -> {
				List<String> filenames = leadsRepository.findLeadsVersionsByCampaingName(campaing.getName());
				System.out.println(filenames);
				if (filenames.size() > 0) {
					if (campaing.getAssignmentType().equals("byUser")) {
						List<Leads> leads = leadsRepository.findLeadsByFilenameAndUserName(filenames,
								request.get("username"));
						response.addAll(leads);
					} else {
						List<Leads> leads = leadsRepository.findLeadsByFilename(filenames);
						System.out.println(leads.get(0).getPhoneNumber()+"#######@@@@@@@@@@@@@##########");
						for (int i = 0; i < leads.size(); i++) {
							if (i == 0) {
								System.out.println(leads);
								Leads currentLead = leads.get(i);
								Leads leadCloned=null;
								try {
									leadCloned = currentLead.clone();
								} catch (CloneNotSupportedException e) {
									e.printStackTrace();
								}
								response.add(leadCloned);
								System.out.println(leadCloned.getPhoneNumber()+"############################");
								System.out.println(leads.get(i).getId());
								System.out.println(request.get("username"));
								currentLead.setStatus("OCCUPIED");
								currentLead.setName(request.get("username"));
								leadsRepository.save(currentLead);
							}
						}
					}
				}
			});
		});

		return response;
	}

	public Map<String, String> logout(String username) {
		Map<String, String> response = new HashMap<>();
		Users user = userRepository.findByUsername(username).get(0);
		user.setOnline("0");
		userRepository.save(user);
		response.put("status", "true");
		LocalDateTime now = LocalDateTime.now();
		java.sql.Date currentDate = java.sql.Date.valueOf(now.toLocalDate());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
		System.out.println(timestamp);
		List<Attendance> attendanceList = attendanceRepository.findAttendanceByUserNameAndDate(username, timestamp);
		if (attendanceList.size() != 0) {
			Attendance attendance = attendanceList.get(0);
			Timestamp currentTimeStamp = new Timestamp((new Date()).getTime());
			attendance.setLoggedOutTime(currentTimeStamp);
			int hours = Math.abs(currentTimeStamp.getHours() - attendance.getLoggedInTime().getHours());
			int minute = Math.abs((currentTimeStamp.getMinutes() - attendance.getLoggedInTime().getMinutes()));
			int seconds = Math.abs((currentTimeStamp.getSeconds() - attendance.getLoggedInTime().getSeconds()));
			String workTime = hours + " Hours," + minute + " Minutes," + seconds + " Seconds";
			attendance.setHours(hours);
			attendance.setMinute(minute);
			attendance.setSeconds(seconds);
			attendance.setTotalWorkHour(workTime);
			System.out.println(workTime);
			UserGroup userGroup=userGroupRepository.findGroupByName(user.getUsergroup());
				if(userGroup!=null) {
					int totalAmount=userGroup.getAmount();
					int amountToDeduct= totalAmount - (hours*(userGroup.getAmountPerHours()!=0?userGroup.getAmountPerHours():14));
					if(amountToDeduct>0) {
						userGroup.setAmount(amountToDeduct);
						userGroupRepository.save(userGroup);
					} else {
						List<Users> usersLists= new ArrayList<Users>();
						userRepository.findUserByGroupName(user.getUsergroup()).forEach(userr->{
							userr.setStatus("Inactive");
							usersLists.add(userr);
							
						});
						userRepository.saveAll(usersLists);
					}
				}
			
			

			attendanceRepository.save(attendance);
			//
			// attendanceRepository.userLoggedOut();
		}

		return response;
	}

	public Map<String, String> createUser(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		Users user = new Users();
		user.setFullName(request.get("fullname"));
		user.setLevel(Integer.parseInt(request.get("level")));
		user.setPassword(request.get("password"));
		user.setOnline("0");
		user.setUsername(request.get("username"));
		user.setUsergroup(request.get("usergroup"));
		user.setStatus(request.get("status"));
		userRepository.save(user);
		response.put("status", "true");
		return response;
	}

	public Map<String, String> updateUser(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();

		Users user = userRepository.findById(Integer.parseInt(request.get("id"))).get();
		user.setFullName(request.get("fullname"));
		user.setLevel(Integer.parseInt(request.get("level")));
		user.setPassword(request.get("password"));
		user.setUsername(request.get("username"));
		user.setUsergroup(request.get("usergroup"));
		user.setStatus(request.get("status"));
		userRepository.save(user);
		response.put("status", "true");
		return response;
	}

	public Map<String, String> attachUserWithGroup(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		Optional<Users> user = userRepository.findById(Integer.parseInt(request.get("user_id")));
		if (user.isPresent()) {
			Users userFound = user.get();
			Optional<UserGroup> group = userGroupRepository.findById(Integer.parseInt(request.get("group_id")));
			if (group.isPresent()) {
				UserGroup userGroup = group.get();
				UserGroupMapping userGroupMapping = new UserGroupMapping();
				userGroupMapping.setUsername(userFound.getFullName());
				userGroupMapping.setGroupname(userGroup.getName());
				userGroupMappingRepository.save(userGroupMapping);
			}
		}

		response.put("status", "true");
		return response;
	}

	public Map<String, String> assignUserToGroup(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		UserGroupMapping userGroupMapping = new UserGroupMapping();
		userGroupMapping.setUsername(request.get("username"));
		userGroupMapping.setGroupname(request.get("groupname"));
		userGroupMappingRepository.save(userGroupMapping);
		response.put("status", "true");
		return response;
	}

	public Map<String, String> updateAssignUserToGroup(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		UserGroupMapping userGroupMapping = userGroupMappingRepository.findGroupByUsername(request.get("username"))
				.get(0);
		userGroupMapping.setUsername(request.get("username"));
		userGroupMapping.setGroupname(request.get("groupname"));
		userGroupMappingRepository.save(userGroupMapping);
		response.put("status", "true");
		return response;
	}

	public void createUser(Users user) {
		userRepository.save(user);
	}

	public void loadCSV(Users users, Campaing campaing, UserGroup usergroup, UserGroupMapping ugm,
			GroupCampaingMapping gcm) {
		userRepository.save(users);
		campaingRepository.save(campaing);
		userGroupRepository.save(usergroup);
		userGroupMappingRepository.save(ugm);
		groupCampaingMappingRepository.save(gcm);
	}

	public void loadUserNameCsv(Users users, UserGroupMapping ugm) {
		userRepository.save(users);
		userGroupMappingRepository.save(ugm);
	}
	// public void loadCSVLead(Leads leads,String campaing,String fileName){
	//
	//
	//
	//
	//
	// }
	//
	// public void deleteByVersionAndFilename(String version, String filename) {
	// repository.deleteByVersionAndFilename(filename, version);
	// }
	//
	// public ArrayList<Versions> fetchAllVersions() {
	// return (ArrayList<Versions>) repository.findAll();
	// }
	//
	// public ArrayList<Versions> fetchByName(String name) {
	//
	// return (ArrayList<Versions>) repository.findAllByName(name);
	// }
	//
	// public List<Versions> fetchByFilenameAndVersions(String filename, String
	// version) {
	// return repository.findByFilenameAndVersion(filename, version);
	// }
	//
	// public static boolean deleteDirectory(File dir) {
	// if (dir.isDirectory()) {
	// File[] children = dir.listFiles();
	// for (int i = 0; i < children.length; i++) {
	// boolean success = deleteDirectory(children[i]);
	// if (!success) {
	// return false;
	// }
	// }
	// }
	// System.out.println("removing file or directory : " + dir.getName());
	// return dir.delete();
	// }
}
