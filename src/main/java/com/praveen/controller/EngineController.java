package com.praveen.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.collections4.MultiMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.praveen.model.Attendance;
import org.apache.commons.collections4.MultiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.praveen.model.Recordings;
import com.praveen.model.BreakTypes;
import com.praveen.model.CallLogs;
import com.fasterxml.jackson.databind.JsonNode;
import com.praveen.dao.BreakTypesRepository;
import com.praveen.dao.CallLogsRepository;
import com.praveen.dao.CampaingLeadMappingRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.EmailBlastingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.LeadVersionsRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.dao.UserGroupMappingRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.Campaing;
import com.praveen.model.CampaingLeadMapping;
import com.praveen.model.EmailBlasting;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.LeadVersionCountAndData;
import com.praveen.model.LeadVersions;
import com.praveen.model.Leads;
import com.praveen.model.UserGroup;
import com.praveen.model.UserGroupMapping;
import com.praveen.model.Users;
import com.praveen.service.BreakService;
import com.praveen.service.BreakTypeService;
import com.praveen.service.CampaingService;
import com.praveen.service.EmailServiceImpl;
import com.praveen.service.LeadsService;
import com.praveen.service.UserGroupService;
import com.praveen.service.UsersService;
import com.praveen.dao.StatusRepository;

@RestController
@RequestMapping("/goautodial")
public class EngineController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private UsersService usersService;
	@Autowired
	BreakService breakService;
	@Autowired
	BreakTypeService breakTypeService;
	@Autowired
	UserGroupService userGroupService;
	@Autowired
	CampaingService campaingService;
	@Autowired
	BreakTypesRepository breakTypesRepository;
	@Autowired
	LeadsService leadsService;
	@Autowired
	GroupCampaingMappingRepository groupCampaingMappingRepository;
	@Autowired
	LeadVersionsRepository leadVersionsRepository;
	@Autowired
	CampaingLeadMappingRepository campaingLeadMappingRepository;
	@Autowired
	LeadsRepository leadsRepository;
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	CallLogsRepository callLogsRepository;
	@Autowired
	EmailServiceImpl emailServiceImpl;
	@Autowired
	EmailBlastingRepository emailBlastingRepository;

	@Value("${reporting.location}")
	String reportingLocation;
	@Value("${recording.location}")
	String recordingLocation;

	@Autowired
	private UsersRepository userRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private CampaingRepository campaingRepository;
	@Autowired
	StatusRepository statusRepository;

	@GetMapping("/")
	public String getEmployees() {
		return "hihihi";
	}

	@PostMapping(path = "/validateuser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> validateUser(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.validateUser(resp);
	}

	@CrossOrigin
	@PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Users login(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.login(resp);
	}

	@CrossOrigin
	@PostMapping(path = "/scheduleCornJob", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> scheduleCornJob(@RequestBody(required = true) Map<String, String> req) {
		Map<String, String> response = new HashMap<String, String>();
		usersService.scheduleCornJob(req);
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/setHourlyPrice", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> setHourlyPrice(@RequestBody(required = true) Map<String, String> req) {
		Map<String, String> response = new HashMap<String, String>();
		userGroupService.setHourlyPrice(req);
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/addBalance", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> addBalance(@RequestBody(required = true) Map<String, String> req) {
		Map<String, String> response = new HashMap<String, String>();
		userGroupService.addBalance(req);
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@GetMapping("/fetchAllGroups")
	public List<UserGroup> fetchAllGroups() {
		return userGroupRepository.findAll();
	}

	@CrossOrigin
	@GetMapping("/fetchTotalLeads")
	public Map<String, Integer> fetchTotalLeads() {
		return leadsService.fetchTotalLeads();
	}

	@CrossOrigin
	@GetMapping("/fetchActiveLeads")
	public Map<String, Integer> fetchActiveLeads() {
		return leadsService.fetchActiveLeads();
	}

	@CrossOrigin
	@GetMapping("/fetchLeadsCountAssignedToUser/{campaingName}")
	public List<Map<String, String>> fetchLeadsCountAssignedToUser(@PathVariable("campaingName") String campaingName) {
		return leadsService.fetchLeadsCountAssignedToUser(campaingName);
	}

	@CrossOrigin
	@GetMapping("/syncDataToCallLogs")
	public void syncDataToCallLogs() {
		usersService.syncDataToCallLogs();
	}

	@CrossOrigin
	@GetMapping("/getData")
	public void getData() {
		usersService.getData();
	}

	@CrossOrigin
	@GetMapping("/fetchactivecampaing")
	public List<Campaing> fetchActiveCampaing() {
		return campaingRepository.findActiveCampaing();
	}

	@CrossOrigin
	@PostMapping("/uploadFile")
	public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam("username") String username, @RequestParam("leadId") String leadId,
			@RequestParam("campaing") String campaing) {
		usersService.uploadFile(file, recordingLocation + "amr/", username, leadId, campaing);
		Map<String, String> response = new HashMap<String, String>();
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@PostMapping("/fetchactivecampaingwithusers")
	public Map<String, List<Users>> fetchActiveCampaingWithUsers(@RequestBody(required = true) List<String> resp) {
		Map<String, List<Users>> resultArray = new HashMap<>();
		resp.forEach((items) -> {
			List<Users> users = userRepository.fetchActiveCampaingWithUsers(items);
			resultArray.put(items, users);
		});
		return resultArray;
	}

	@CrossOrigin
	@PostMapping("/paymentSchedule")
	public Map<String, String> paymentSchedule(@RequestBody(required = true) Map<String, String> req) {
		Map<String, String> response = new HashMap<>();
		req.get("paymentAmount");
		req.get("group");
		req.get("userName");
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@GetMapping("/fetchGroupsWithCampaings")
	public List<Map<String, Object>> fetchGroupsWithCampaings() {
		return userGroupService.fetchGroupsWithCampaings();
	}

	@CrossOrigin
	@GetMapping("/fetchAllUsers")
	public List<Users> fetchAllUsers() {
		return userRepository.findAll();
	}

	@CrossOrigin
	@GetMapping("/fetchOnlineUsers")
	public List<Users> fetchOnlineUsers() {
		return userRepository.findAll();
	}

	@CrossOrigin
	@GetMapping("/fetchLeadVersions")
	public List<LeadVersions> fetchLeadVersions() {
		return leadVersionsRepository.findAll();
	}

	@CrossOrigin
	@GetMapping("/fetchLeadVersionsWithCount")
	public Map<String, Integer> fetchLeadVersionsWithCount() {
		Map<String, Integer> conter = new HashMap<String, Integer>();
		List<Object[]> dbResult = leadVersionsRepository.findCountAndLeads();
		if (dbResult.size() > 0) {
			for (Object[] items : dbResult) {
				BigInteger valueCounter=(BigInteger) items[1];
				conter.put(String.valueOf(items[0]), valueCounter.intValue());
			}
		}
		return conter;
	}
	@CrossOrigin
	@GetMapping("/rechain/{fileName}/{status}")
	public Map<String,String> rechain(@PathVariable("fileName") String fileName, @PathVariable("status") String status) {
		return leadsService.rechain(fileName,status);
	}

	@CrossOrigin
	@GetMapping("/updatecampaingstatus/{id}/{status}")
	public Campaing updateCampaingStatus(@PathVariable("id") int id, @PathVariable("status") String status) {
		Campaing campaing = campaingRepository.findById(id).get();
		campaing.setActive(status);
		return campaingRepository.save(campaing);
	}

	@CrossOrigin
	@GetMapping("/fetchCampaingByUserName/{username}")
	public Map<String, String> fetchCampaingByUserName(@PathVariable("username") String username) {
		return campaingService.fetchCampaingByUserName(username);
	}

	@CrossOrigin
	@GetMapping("/fetchOnlineUsersByCampaingName/{campaingName}")
	public List<Users> fetchOnlineUsersByCampaingName(@PathVariable("campaingName") String campaingName) {
		return usersService.fetchOnlineUsersByCampaingName(campaingName);
	}

	@CrossOrigin
	@GetMapping("/deleteUser/{id}")
	public Map<String, String> deleteUser(@PathVariable("id") int id) {
		return usersService.deleteUser(id);
	}

	@CrossOrigin
	@GetMapping("/deleteCampaing/{id}")
	public Map<String, String> deleteCampaing(@PathVariable("id") int id) {
		return usersService.deleteCampaing(id);
	}
	@CrossOrigin
    @GetMapping("/deleteLeads/{id}")
    public Map<String, String> deleteLeads(@PathVariable("id") int id) {
        Map<String,String> response= new HashMap<String, String>();
        try {
	    leadVersionsRepository.deleteById(id);
	    response.put("status", "success");
        }catch(Exception e) {
            e.printStackTrace();
            response.put("status", "failure");
        }
        return response;
	}

	@CrossOrigin
	@GetMapping("/deleteGroup/{name}")
	public Map<String, String> deleteGroup(@PathVariable("name") String name) {
		return usersService.deleteGroup(name);
	}

	@CrossOrigin
	@GetMapping("/fetchBalance/{groupName}")
	public UserGroup fetchBalance(@PathVariable("groupName") String groupName) {
		return userGroupRepository.findGroupByName(groupName);
	}

	@CrossOrigin
	@GetMapping("/deleteBreakType/{id}")
	public Map<String, String> deleteBreakType(@PathVariable("id") int id) {
		return usersService.deleteBreakType(id);
	}

	@CrossOrigin
	@GetMapping("/logout/{username}")
	public Map<String, String> logout(@PathVariable("username") String username) {
		return usersService.logout(username);
	}

	@CrossOrigin
	@GetMapping("/fetchStatuByCampaingName/{campaingname}")
	public Map<String, String> fetchStatuByCampaingName(@PathVariable("campaingname") String campaingname) {
		return campaingService.fetchStatuByCampaingName(campaingname);
	}

	@CrossOrigin
	@GetMapping("/fetchCrm/{leadid}")
	public JsonNode fetchCrm(@PathVariable("leadid") String leadid) {
		return campaingService.fetchCrm(leadid);
	}

	@CrossOrigin
	@GetMapping("/convert")
	public void convert() {
		campaingService.convert();
	}

	@CrossOrigin
	@PostMapping(path = "/updateCrm/{leadid}", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> updateCrm(@PathVariable("leadid") int leadid,
			@RequestBody(required = true) Map<String, List<Map<String, String>>> request) {
		Map<String, String> response = new HashMap<>();
		campaingService.updateCrm(leadid, request);
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@GetMapping("/updateleadstatus/{id}/{status}")
	public LeadVersions updateLeadStatus(@PathVariable("id") int id, @PathVariable("status") String status) {
		LeadVersions leadVersions = leadVersionsRepository.findById(id).get();
		leadVersions.setStatus(status);
		return leadVersionsRepository.save(leadVersions);
	}

	@CrossOrigin
	@GetMapping("/updateEmailStatus/{id}/{status}")
	public EmailBlasting updateEmailStatus(@PathVariable("id") int id, @PathVariable("status") String status) {
		EmailBlasting emailBlasting = emailBlastingRepository.findById(id).get();
		emailBlasting.setStatus(status);
		return emailBlastingRepository.save(emailBlasting);
	}

	@CrossOrigin
	@GetMapping("/fetchPhoneBook/{campaingName}")
	public List<String> fetchPhoneBook(@PathVariable("campaingName") String campaingName) {
		return leadsRepository.findLeadByCampaingName(campaingName);
	}

	@CrossOrigin
	@GetMapping("/updateuserstatus/{id}/{status}")
	public Users updateUserStatus(@PathVariable("id") int id, @PathVariable("status") String status) {
		Users user = usersRepository.findById(id).get();
		user.setStatus(status);
		return usersRepository.save(user);
	}

	@CrossOrigin
	@GetMapping("/fetchusersbycampaing/{campaing}")
	public List<Map<String, String>> fetchUsersByCampaing(@PathVariable("campaing") String campaing) {
		return usersService.fetchUsersByCampaing(campaing);
	}

	@CrossOrigin
	@GetMapping("/fetchAllBreakTypes")
	public Map<String, List<BreakTypes>> fetchAllBreakTypes() {
		return breakTypeService.fetchAllBreakTypes();
	}

	@CrossOrigin
	@GetMapping("/findBreaksByCampaingName/{campaing}")
	public Map<String, List<BreakTypes>> findBreaksByCampaingName(@PathVariable("campaing") String campaing) {
		return breakTypeService.findBreaksByCampaingName(campaing);
	}

	@CrossOrigin
	@PostMapping(path = "/createBreakTypes", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> createBreakTypes(@RequestBody(required = true) Map<String, String> request) {
		return breakTypeService.createBreakTypes(request);
	}

	@CrossOrigin
	@PostMapping(path = "/submitBreak", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> submitBreak(@RequestBody(required = true) Map<String, String> request) {
		return breakService.submitBreak(request);
	}

	@CrossOrigin
	@GetMapping("/fetchusersByName/{username}")
	public List<Users> fetchusersByName(@PathVariable("username") String username) {
		return userRepository.findByUsername(username);

	}

	@CrossOrigin
	@GetMapping("/fetchCampaingByName/{campaingname}")
	public Campaing fetchCampaingByName(@PathVariable("campaingname") String campaingname) {
		return campaingRepository.findCampaingByName(campaingname);

	}

	@CrossOrigin
	@GetMapping("/fetchRecordingsByUsername/{username}")
	public List<Recordings> fetchRecordingsByUsername(@PathVariable("username") String username) {
		return usersService.fetchRecordingsByUsername(username);

	}
	// @CrossOrigin
	// @PostMapping(path = "/fetchRecordingsByUsername", consumes =
	// "application/json", produces = "application/json")
	// @ResponseBody
	// public List<Map<String,Object>>
	// fetchRecordingsByUsername(@RequestBody(required = true) Map<String, String>
	// resp) {
	//
	// return usersService.fetchRecordingsByUsername(resp);
	// }

	@CrossOrigin
	@GetMapping("/fetchRecordings")
	public List<Recordings> fetchRecordings() {
		return usersService.fetchRecordings();

	}

	@CrossOrigin
	@GetMapping("/fetchGroupByCampaings/{campaingname}")
	public List<String> fetchGroupByCampaings(@PathVariable("campaingname") String campaingname) {
		return groupCampaingMappingRepository.findDistinctGroupByCampaingName(campaingname);

	}

	@CrossOrigin
	@GetMapping("/fetchBreakType/{id}")
	public BreakTypes fetchGroupByCampaings(@PathVariable("id") int id) {
		return breakTypesRepository.findById(id).get();

	}

	@CrossOrigin
	@GetMapping("/showEmailDataByFileName/{fileName}")
	public List<EmailBlasting> showEmailDataByFileName(@PathVariable("fileName") String fileName) {
		return leadsService.showEmailDataByFileName(fileName);

	}

	@CrossOrigin
	@GetMapping("/showUploadedEmailFilesByCampaingName/{campaingName}")
	public List<Map<String, String>> showUploadedEmailFilesByCampaingName(
			@PathVariable("campaingName") String campaingName) {
		return leadsService.showUploadedEmailFilesByCampaingName(campaingName);

	}

	@CrossOrigin
	@GetMapping("/showUploadedEmailFiles")
	public List<Map<String, String>> showUploadedEmailFiles() {
		return leadsService.showUploadedEmailFiles();

	}

	@CrossOrigin
	@GetMapping("/fetchGroupByGroupName/{groupName}")
	public UserGroup fetchGroupByGroupName(@PathVariable("groupName") String groupName) {
		return userGroupRepository.findGroupByName(groupName);
	}

	@CrossOrigin
	@GetMapping("/fetchGroupByName/{groupName}")
	public List<GroupCampaingMapping> fetchGroupByName(@PathVariable("groupName") String groupName) {
		return groupCampaingMappingRepository.findByGroupName(groupName);
	}

	@CrossOrigin
	@GetMapping("/fetchcampaings")
	public List<Campaing> fetchCampaings() {
		return campaingRepository.findAll();
	}

	@CrossOrigin
	@GetMapping("/fetchcallbacks/{username}")
	public Map<String, List<Map<String, Object>>> fetchCallbacks(@PathVariable("username") String username) {
		Map<String, List<Map<String, Object>>> callbacks = new HashMap<>();
		List<Map<String, Object>> callbacksList = new ArrayList<>();
		callLogsRepository.findAllCallbacks(username).forEach((items) -> {
			Map<String, Object> callbacksObject = new HashMap<>();
			callbacksObject.put("id", items[10]);
			callbacksObject.put("name", items[0]);
			callbacksObject.put("assignedTo", items[0]);
			callbacksObject.put("phoneNumber", items[1]);
			callbacksObject.put("firstName", items[3]);
			callbacksObject.put("city", "");
			callbacksObject.put("state", "");
			callbacksObject.put("email", "");
			callbacksObject.put("crm", items[9]);
			callbacksObject.put("status", items[2]);
			callbacksObject.put("callBackDateTime", items[8]);
			callbacksObject.put("last_local_call_time", items[5]);
			callbacksObject.put("comments", items[4]);
			callbacksObject.put("callDate", items[6]);
			callbacksObject.put("callEndDate", items[7]);
			callbacksList.add(callbacksObject);
		});

		callbacks.put("callback", callbacksList);
		return callbacks;
	}

	@PostMapping(path = "/statusCall", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> statusCall(@RequestBody(required = true) Map<String, String> resp) {

		return usersService.validateUser(resp);
	}

	@CrossOrigin
	@PostMapping(path = "/fetchcountreportdatabetween", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public MultiMap<String, Map<String, Object>> fetchcountreportdatabetween(
			@RequestBody(required = true) Map<String, Object> request) {
		return leadsService.fetchcountreportdatabetween(request);
	}

	@CrossOrigin
	@PostMapping(path = "/fetchcountattendancereportdatabetween", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Attendance> fetchcountattendancereportdatabetween(
			@RequestBody(required = true) Map<String, Object> request) {
		return leadsService.fetchcountattendancereportdatabetween(request, reportingLocation);
	}

	@CrossOrigin
	@PostMapping(path = "/updateBreakType", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> updateBreakType(@RequestBody(required = true) Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		BreakTypes breakType = breakTypesRepository.findById(Integer.parseInt(request.get("breakId"))).get();
		if (breakType != null) {
			breakType.setBreakType(request.get("breakName"));
			breakType.setCampaingName(request.get("campaingName"));
		}
		breakTypesRepository.save(breakType);
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/cloneBreakType", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> cloneBreakType(@RequestBody(required = true) Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		BreakTypes breakType = new BreakTypes();
		breakType.setBreakType(request.get("breakName"));
		breakType.setCampaingName(request.get("campaingName"));
		breakTypesRepository.save(breakType);
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/fetchcountrecordingreportdatabetween", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Map<String, String>> fetchcountrecordingreportdatabetween(
			@RequestBody(required = true) Map<String, Object> request) {
		return leadsService.fetchcountrecordingreportdatabetween(request, reportingLocation);
	}

	@CrossOrigin
	@PostMapping(path = "/sendEmailToCampaing", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> sendEmailToCampaing(@RequestBody(required = true) Map<String, String> request) {
		emailServiceImpl.sendEmailJava(request.get("campaingName"), request.get("message"), request.get("email"),
				request.get("password"), request.get("subject"));
		Map<String, String> response = new HashMap<String, String>();
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/updategroup", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> updategroup(@RequestBody(required = true) Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		List<GroupCampaingMapping> groupCampaingMappingList = new ArrayList<>();
		synchronized (this) {
			groupCampaingMappingRepository
					.deleteInBatch(groupCampaingMappingRepository.findByGroupName(request.get("name")));

			String[] campaingList = request.get("allowed_campaigns").split(",");
			for (int i = 0; i < campaingList.length; i++) {
				GroupCampaingMapping gcm = new GroupCampaingMapping();
				gcm.setGroupname(request.get("name"));
				gcm.setCampaingname(campaingList[i]);
				groupCampaingMappingList.add(gcm);
			}
		}

		groupCampaingMappingRepository.saveAll(groupCampaingMappingList);
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/fetchcountreportdatabetweenbyuser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public MultiMap<String, Map<String, Object>> fetchcountreportdatabetweenByUser(
			@RequestBody(required = true) Map<String, Object> request) {
		return leadsService.fetchcountreportdatabetweenByUser(request);
	}

	@CrossOrigin
	@PostMapping(path = "/fetchreportdatabetweenwithusername", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, List<CallLogs>> fetchreportdatabetweenWithUserName(
			@RequestBody(required = true) Map<String, Object> request) {
		return leadsService.fetchreportdatabetweenWithUserName(request);
	}

	@CrossOrigin
	@PostMapping(path = "/fetchreportdatabetween", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> fetchreportdatabetween(@RequestBody(required = true) Map<String, Object> request) {
		leadsService.fetchreportdatabetween(request, reportingLocation);
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/fetchattendancereportdatabetween", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> fetchAttendancereportdatabetween(
			@RequestBody(required = true) Map<String, Object> request) {
		return leadsService.fetchAttendancereportdatabetween(request, reportingLocation);
	}

	@PostMapping(path = "/loadEmailCsv/{campaingname}", consumes = "multipart/form-data", produces = "application/json")
	public Map<String, String> loadEmailCsv(@RequestParam("file") MultipartFile file,
			@PathVariable("campaingname") String campaingname) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(file.getInputStream());

			// CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> list = parser.getRecords();
			List<EmailBlasting> emailBlastingList = new ArrayList<EmailBlasting>();
			for (int j = 1; j < list.size(); j++) {
				// for(int i=0;i<=list.get(j).size();i++) {
				EmailBlasting emailBlasting = new EmailBlasting();
				emailBlasting.setCampaingName(campaingname);
				emailBlasting.setEmail(list.get(j).get(0));
				emailBlasting.setStatus("Y");
				emailBlastingList.add(emailBlasting);
			}
			emailBlastingRepository.deleteByCampaingName(campaingname);
			emailBlastingRepository.saveAll(emailBlastingList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@PostMapping(path = "/loadCsv", consumes = "multipart/form-data", produces = "application/json")
	public Map<String, String> loadCsv(@RequestParam("file") MultipartFile file) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(file.getInputStream());

			// CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> list = parser.getRecords();
			UserGroup userGroup = new UserGroup();
			userGroup.setActive("Y");
			userGroup.setName(list.get(2).get(4));
			Campaing campaing = new Campaing();
			campaing.setActive("Y");
			campaing.setDialPrefix("555");
			campaing.setDialTimeout("100");
			campaing.setLocalCallTime("9AM - 10PM");
			campaing.setManualDialPrefix("555");
			campaing.setName(list.get(2).get(3));
			for (int j = 1; j < list.size(); j++) {
				// for(int i=0;i<=list.get(j).size();i++) {
				Users user = new Users();
				user.setFullName(list.get(j).get(1));
				user.setUsername(list.get(j).get(1));
				user.setPassword(list.get(j).get(2));
				user.setLevel(Integer.parseInt(list.get(j).get(5)));

				UserGroupMapping userGroupMapping = new UserGroupMapping();
				userGroupMapping.setGroupname(userGroup.getName());
				userGroupMapping.setUsername(user.getUsername());
				GroupCampaingMapping gcm = new GroupCampaingMapping();
				gcm.setCampaingname(campaing.getName());
				gcm.setGroupname(userGroup.getName());
				usersService.loadCSV(user, campaing, userGroup, userGroupMapping, gcm);
				// }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@PostMapping(path = "/loadUserNameCsv", consumes = "multipart/form-data", produces = "application/json")
	public Map<String, String> loadUserNameCsv(@RequestParam("file") MultipartFile file) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(file.getInputStream());

			// CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> list = parser.getRecords();
			for (int j = 1; j < list.size(); j++) {
				// for(int i=0;i<=list.get(j).size();i++) {
				Users user = new Users();
				user.setFullName(list.get(j).get(0));
				user.setUsername(list.get(j).get(0));
				user.setPassword("1234");
				user.setLevel(1);

				UserGroupMapping userGroupMapping = new UserGroupMapping();
				userGroupMapping.setGroupname("HATHWAY");
				userGroupMapping.setUsername(user.getUsername());
				usersService.loadUserNameCsv(user, userGroupMapping);
				// }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, String> response = new HashMap<>();
		return response;
	}

	// @PostMapping(path = "/loadLeadsWithUser", consumes = "multipart/form-data",
	// produces = "application/json")
	// public Map<String, String> loadLeadsWithUser(@RequestParam("file")
	// MultipartFile file) {
	// Reader reader = null;
	// try {
	// reader = new InputStreamReader(file.getInputStream());
	//
	// // CSVReader csvReader = new
	// CSVReaderBuilder(reader).withSkipLines(1).build();
	// CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
	// List<CSVRecord> list = parser.getRecords();
	// for (int j = 1; j < list.size(); j++) {
	// // for(int i=0;i<=list.get(j).size();i++) {
	// String[] userName = list.get(j).get(1).split("\\s");
	// if (userRepository.findByUsername(userName[0]).size() > 0) {
	// Leads leads = new Leads();
	// leads.setPhoneNumber(list.get(j).get(0));
	// leads.setAlternatePhonenumber(list.get(j).get(1));
	// leads.setAddress1("NA");
	// leads.setAddress2("NA");
	// leads.setAddress3("NA");
	// leads.setName("NA");
	// leads.setCity("NA");
	// leads.setEmail("NA");
	// leads.setEmail("NA");
	// leads.setGender("NA");
	// usersService.createLead(leads);
	// } else {
	// Users user = new Users();
	// user.setFullName(list.get(j).get(1));
	// user.setUsername(userName[0]);
	// user.setPassword("1234");
	// user.setLevel(1);
	// UserGroupMapping userGroupMapping = new UserGroupMapping();
	// userGroupMapping.setGroupname("HATHWAY");
	// userGroupMapping.setUsername(userName[0]);
	// Leads leads = new Leads();
	// leads.setPhoneNumber(list.get(j).get(0));
	// leads.setAlternatePhonenumber(list.get(j).get(1));
	// leads.setAddress1("NA");
	// leads.setAddress2("NA");
	// leads.setAddress3("NA");
	// leads.setName("NA");
	// leads.setCity("NA");
	// leads.setEmail("NA");
	// leads.setEmail("NA");
	// leads.setGender("NA");
	// usersService.loadLeadsWithUser(user, leads, userGroupMapping);
	// }
	//
	// // }
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// Map<String, String> response = new HashMap<>();
	// return response;
	// }

	// @PostMapping(path = "/loadCsvLead", consumes = "multipart/form-data",
	// produces = "application/json")
	// public Map<String, String> loadCsvLead(@RequestParam("file") MultipartFile
	// file) {
	// Reader reader = null;
	// try {
	// reader = new InputStreamReader(file.getInputStream());
	//
	// // CSVReader csvReader = new
	// CSVReaderBuilder(reader).withSkipLines(1).build();
	// CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
	// List<CSVRecord> list = parser.getRecords();
	// for (int j = 1; j < list.size(); j++) {
	// Leads leads = new Leads();
	// leads.setFirstName(list.get(j).get(2));
	// leads.setCity(list.get(j).get(5));
	// leads.setState(list.get(j).get(6));
	// leads.setStatus("ACTIVE");
	// leads.setLastName(list.get(j).get(2));
	// leads.setPhoneNumber(list.get(j).get(0));
	// leads.setCity(list.get(j).get(5));
	// leads.setPostalCode(list.get(j).get(4));
	// leads.setAddress1(list.get(j).get(3));
	// leads.setEmail(list.get(j).get(7));
	//// usersService.loadCSVLead(leads);
	// // }
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// Map<String, String> response = new HashMap<>();
	// return response;
	// }

	@CrossOrigin
	@PostMapping(path = "/loadCsvLeadData", consumes = "multipart/form-data", produces = "application/json")
	public Map<String, String> loadCsvLeadData(@RequestParam("file") MultipartFile file,
			@RequestParam("campaing") String campaing, @RequestParam("duplicateAction") String duplicateAction,
			@RequestParam("duplicateCheck") String duplicateCheck,
			@RequestParam("duplicateField") String duplicateField, @RequestParam("filename") String filename) {
		leadsService.loadCsvLeadData(file, campaing, duplicateAction, duplicateCheck, duplicateField, filename);
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/filterDnd", consumes = "multipart/form-data", produces = "application/json")
	public Map<String, String> filterDnd(@RequestParam("file") MultipartFile file) {
		return leadsService.filterDND(file, reportingLocation);
	}

	@CrossOrigin
	@PostMapping(path = "/uploadEmails", consumes = "multipart/form-data", produces = "application/json")
	public Map<String, String> uploadEmails(@RequestParam("file") MultipartFile file,
			@RequestParam("campaingName") String campaingName) {
		return leadsService.uploadEmails(file, campaingName);
	}

	@CrossOrigin
	@PostMapping(path = "/uploadDND", consumes = "multipart/form-data", produces = "application/json")
	public Map<String, String> uploadDND(@RequestParam("file") MultipartFile file) {
		return leadsService.uploadDND(file);
	}

	@CrossOrigin
	@PostMapping(path = "/createUserGroup", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, Boolean> createUserGroup(@RequestBody(required = true) Map<String, String> request) {
		userGroupService.createUserGroup(request);
		Map<String, Boolean> response = new HashMap<>();
		response.put("status", true);
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/createCampaing", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, Boolean> createCampaing(@RequestBody(required = true) Map<String, Object> request) {
		campaingService.createCampaing(request);
		Map<String, Boolean> response = new HashMap<>();
		response.put("status", true);
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/campaingGroupMapping", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> campaingGroupMapping(@RequestBody(required = true) Map<String, String> request) {
		campaingService.campaingGroupMapping(request);
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/updateCampaingGroupMapping", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, Boolean> updateCampaingGroupMapping(@RequestBody(required = true) Map<String, String> request) {
		campaingService.updateCampaingGroupMapping(request);
		Map<String, Boolean> response = new HashMap<>();
		response.put("status", true);
		return response;
	}

	// @PostMapping(path = "/attachLeadToCampaing", consumes = "application/json",
	// produces = "application/json")
	// @ResponseBody
	// public Map<String, String> attachLeadToCampaing(@RequestBody(required = true)
	// Map<String, String> request) {
	// leadsService.attachLeadToCampaing(request);
	// Map<String, String> response = new HashMap<>();
	// return response;
	// }

	@PostMapping(path = "/attachUserGroupToCampaing", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> attachUserGroupToCampaing(@RequestBody(required = true) Map<String, String> request) {
		campaingService.attachUserGroupToCampaing(request);
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/assignUserToGroup", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> assignUserToGroup(@RequestBody(required = true) Map<String, String> request) {
		return usersService.assignUserToGroup(request);
	}

	@CrossOrigin
	@PostMapping(path = "/updateAssignUserToGroup", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> updateAssignUserToGroup(@RequestBody(required = true) Map<String, String> request) {
		return usersService.updateAssignUserToGroup(request);
	}

	@CrossOrigin
	@PostMapping(path = "/createuser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> createUser(@RequestBody(required = true) Map<String, String> request) {
		return usersService.createUser(request);
	}

	@CrossOrigin
	@PostMapping(path = "/updateUser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> updateUser(@RequestBody(required = true) Map<String, String> request) {
		return usersService.updateUser(request);
	}

	@CrossOrigin
	@PostMapping(path = "/updateCampaing", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> updateCampaing(@RequestBody(required = true) Map<String, Object> request) {
		return campaingService.updateCampaing(request);
	}

	@PostMapping(path = "/feedbackLeads", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, Boolean> feedbackLeads(@RequestBody(required = true) Map<String, String> request) {
		System.out.println("#######FEEDBACKLEADSs");
		System.out.println(request);
		leadsService.feedbackLead(request);

		Map<String, Boolean> response = new HashMap<>();
		response.put("status", true);
		return response;
	}

	@PostMapping(path = "/feedbackLeadsMutiple", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> feedbackLeadsMutiple(@RequestBody(required = true) List<Map<String, String>> request) {
		System.out.println(request);
		leadsService.feedbackLeadsMutiple(request);

		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		return response;
	}

	@GetMapping(path = "/feedback/{leadid}/{status}/{calltime}")
	@ResponseBody
	public Map<String, Boolean> feedback(@PathVariable("leadid") int id, @PathVariable("status") String status,
			@PathVariable("calltime") String calltime) {
		leadsService.feedback(id, status, calltime);

		Map<String, Boolean> response = new HashMap<>();
		response.put("status", true);
		return response;
	}

	// @GetMapping(path =
	// "/feedbackLead/{leadid}/{status}/{calltime}/{comments}/{callBackDateTime}")
	// @ResponseBody
	// public Map<String, Boolean> feedbackLead(@PathVariable("leadid") int id,
	// @PathVariable("status") String status,
	// @PathVariable("calltime") String calltime, @PathVariable("comments") String
	// comments, @PathVariable("callBackDateTime") String callBackDateTime) {
	// leadsService.feedbackLead(id, status, calltime, comments,callBackDateTime);
	// Map<String, Boolean> response = new HashMap<>();
	// response.put("status", true);
	// return response;
	// }

	@GetMapping(path = "/fetchLeadByPhoneNumber/{phoneNumber}")
	@ResponseBody
	public List<Leads> fetchLeadByPhoneNumber(@PathVariable("phoneNumber") String phoneNumber) {
		return leadsService.fetchLeadByPhoneNumber(phoneNumber);
	}

	@GetMapping(path = "/fetchAllLeads")
	@ResponseBody
	public List<Leads> fetchAllLeads() {
		return leadsService.fetchAllLeads();
	}

	@GetMapping(path = "/findGroupById/{id}")
	@ResponseBody
	public Optional<UserGroup> findGroupById(@PathVariable("id") int id) {
		return userGroupService.fetchUserGroupById(id);
	}

	@GetMapping(path = "/fetchCampaing")
	@ResponseBody
	public Map<String, String> fetchCampaing() {
		List<String> campaingName = new ArrayList<>();
		campaingService.fetchCampaing().forEach((campaing) -> {
			campaingName.add(campaing.getName());
		});
		System.out.println(campaingName);
		String res = String.join(",", campaingName);
		Map<String, String> respp = new HashMap<>();
		respp.put("campaing", res);
		return respp;
	}

	@GetMapping(path = "/fetchLeadsByUserAndCampaing/{username}/{campaing}")
	@ResponseBody
	public List<Map<String, Object>> fetchLeadsByUserAndCampaing(@PathVariable("username") String username,
			@PathVariable("campaing") String campaing) {
		Map<String, String> request = new HashMap<>();
		request.put("username", username);
		request.put("campaing", campaing);
		return usersService.fetchLeadsByUserAndCampaing(request);
	}

	@GetMapping(path = "/fetchLeadsByUserAndCampaingApp/{username}/{campaing}")
	@ResponseBody
	public JSONObject fetchLeadsByUserAndCampaingApp(@PathVariable("username") String username,
			@PathVariable("campaing") String campaing) {
		Map<String, String> request = new HashMap<>();
		request.put("username", username);
		request.put("campaing", campaing);
		JSONArray array = null;
		try {
			array = new JSONArray();
			array.put(usersService.fetchLeadsByUserAndCampaing(request).get(0));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject respon = new JSONObject();
		try {
			respon.put("error", "false");
			respon.put("message", "updated successfully");
			respon.put("user", array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return respon;
	}

	@GetMapping(path = "/fetchLeadByUserAndCampaing/{username}/{campaing}")
	@ResponseBody
	public List<Leads> fetchLeadByUserAndCampaing(@PathVariable("username") String username,
			@PathVariable("campaing") String campaing) {
		System.out.println("#####################3");
		Map<String, String> request = new HashMap<>();
		request.put("username", username);
		request.put("campaing", campaing);
		return usersService.fetchLeadByUserAndCampaing(request);
	}

}
