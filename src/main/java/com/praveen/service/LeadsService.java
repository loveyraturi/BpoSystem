package com.praveen.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.StringUtil;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.praveen.dao.AttendanceRepository;
import com.praveen.dao.CallLogsRepository;
import com.praveen.dao.CampaingLeadMappingRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.DndNumbersRepository;
import com.praveen.dao.EmailBlastingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.LeadVersionsRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.dao.RecordingRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.Attendance;
import com.praveen.model.CallLogs;
import com.praveen.model.Campaing;
import com.praveen.model.CampaingLeadMapping;
import com.praveen.model.DndNumbers;
import com.praveen.model.EmailBlasting;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.LeadVersions;
import com.praveen.model.Leads;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

@Service
public class LeadsService {
	@Autowired
	LeadsRepository leadRepository;
	@Autowired
	DndNumbersRepository dndNumbersRepository;
	@Autowired
	EmailBlastingRepository emailBlastingRepository;
	@Autowired
	CampaingRepository campaingRepository;
	@Autowired
	CampaingLeadMappingRepository campaingLeadMappingRepository;
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	GroupCampaingMappingRepository groupCampaingMappingRepository;
	@Autowired
	LeadsRepository leadsRepository;
	@Autowired
	LeadVersionsRepository leadVersionsRepository;
	@Autowired
	AttendanceRepository attendanceRepository;
	@Autowired
	RecordingRepository recordingRepository;
	@Autowired
	CallLogsRepository callLogsRepository;

	
	public Map<String,Integer> fetchTotalLeads() {
		Map<String,Integer> response = new HashMap<>();
		leadRepository.fetchTotalLeadsCount().forEach(items->{
			response.put(String.valueOf(items[1]), Integer.parseInt(String.valueOf(items[0])));
		});
		return response;
	}
	public Map<String,String> rechain(String fileName,String status) {
		List<Leads> response = new ArrayList<>();
		System.out.println(fileName+",     , "+status);
		leadRepository.findLeadsByFilenameAndStatus(fileName,status).forEach(items->{
			items.setStatus("ACTIVE");
			response.add(items);
		});
		System.out.println(response.size());
		leadRepository.saveAll(response);
		Map<String,String> responseMessage= new HashMap<String, String>();
		responseMessage.put("count", String.valueOf(response.size()));
		responseMessage.put("status", "true");
		return responseMessage;
	}
	public Map<String,Integer> fetchActiveLeads() {
		Map<String,Integer> response = new HashMap<>();
		leadRepository.fetchActiveLeads().forEach(items->{
			response.put(String.valueOf(items[1]), Integer.parseInt(String.valueOf(items[0])));
		});
		return response;
	}
	public List<Map<String,String>>  fetchLeadsCountAssignedToUser(String campaingName) {
//		List<Map<String,String>>> response = new HashMap<>();
		List<Map<String,String>> listRespose= new ArrayList();
		leadRepository.fetchLeadsCountAssignedToUser(campaingName).forEach(items->{
			Map<String,String> responseUser=new HashMap<>();
			responseUser.put("username", String.valueOf(items[1]));
			responseUser.put("count", String.valueOf(items[0]));
			listRespose.add(responseUser);
			
		});
		return listRespose;
	}
	
	
	public List<Leads> fetchAllLeads() {
		return leadRepository.findAll();
	}

	public List<String> findLeadByCampaingName(String campaingName) {
		return leadRepository.findLeadByCampaingName(campaingName);
	}

	public void loadCsvLeadData(MultipartFile file, String campaing, String duplicateAction, String duplicateCheck,
			String duplicateField, String filename) {
		Reader reader = null;
		try {
			System.out.println(
					campaing + "##############duplicateAction= " + duplicateAction + "##########duplicateCheck= "
							+ duplicateCheck + "############duplicateField" + duplicateField + "####" + filename);
			reader = new InputStreamReader(file.getInputStream());
			Map<String, String> uniqueNumber = new HashMap<>();

			if (campaingRepository.findCampaingByName(campaing) == null) {
				System.out.println("campaing not found");
				Campaing campaingNew = new Campaing();
				campaingNew.setActive("Y");
				campaingNew.setDialPrefix("NA");
				campaingNew.setName(campaing);
				campaingRepository.save(campaingNew);
				GroupCampaingMapping gcm = new GroupCampaingMapping();
				gcm.setCampaingname(campaing);
				if (userGroupRepository.findGroupByName(campaing) == null) {
					System.out.println("group not found");
					UserGroup newUserGroup = new UserGroup();
					newUserGroup.setActive("Y");
					newUserGroup.setName(campaing);
					userGroupRepository.save(newUserGroup);
					gcm.setGroupname(campaing);
				} else {
					gcm.setGroupname(campaing);
				}
				groupCampaingMappingRepository.save(gcm);
			}
			CampaingLeadMapping clm = new CampaingLeadMapping();

			clm.setCampaingName(campaing);
			clm.setLeadVersionName(filename);
			campaingLeadMappingRepository.save(clm);
			// CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> list = parser.getRecords();
			List<Leads> leadsArray = new ArrayList<>();
			List<String> leadsFound = new ArrayList<>();
			if ("Campaing".equals(duplicateCheck)) {
				leadsFound = leadsRepository.findLeadByCampaingName(campaing);
			}
			CSVRecord headers = list.get(0);
			for (int j = 1; j < list.size(); j++) {
				if ("List".equals(duplicateCheck)) {
					if ("phone_number".equals(duplicateField)) {
						if (uniqueNumber.get(list.get(j).get(0)) != null) {
							continue;
						} else {
							uniqueNumber.put(list.get(j).get(0), list.get(j).get(0));
						}
					}
				} else if ("Campaing".equals(duplicateCheck)) {
					if (leadsFound.contains(list.get(j).get(0))) {
						continue;
					}

				} else if ("Global".equals(duplicateCheck)) {
					if ("phone_number".equals(duplicateField)) {
						List<Leads> lead = leadsRepository.findLeadByNumber(list.get(j).get(0));
						if (lead.size() > 0) {
							continue;
						}
					}
				}
				List<Map<String, String>> crmList = new ArrayList<>();

				for (int i = 6; i < list.get(j).size(); i++) {
					Map<String, String> crm = new HashMap<>();
					crm.put("label", headers.get(i).replaceAll(" ", "_"));
					crm.put("field", list.get(j).get(i));
					crmList.add(crm);
				}

				Leads leads = new Leads();
				leads.setPhoneNumber(list.get(j).get(0));
				leads.setFirstName(list.get(j).get(1));
				leads.setState(list.get(j).get(2));
				leads.setCity(list.get(j).get(3));
				leads.setEmail(list.get(j).get(4));
				Gson gson = new Gson();
				String additionalFields = gson.toJson(crmList);
				leads.setCrm(additionalFields);
				leads.setAssignedTo(list.get(j).get(5));
				leads.setStatus("ACTIVE");
				leads.setFilename(filename);
				leadsArray.add(leads);

			}
			leadsRepository.saveAll(leadsArray);
			LeadVersions lv = new LeadVersions();
			lv.setCampaingName(campaing);
			lv.setFilename(filename);
			lv.setStatus("Y");
			leadVersionsRepository.save(lv);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<Map<String,String>> showUploadedEmailFiles() {
		List<Map<String,String>> responseList= new ArrayList<>();
		 emailBlastingRepository.fileNames().forEach(items-> {
			 Map<String,String> response= new HashMap<String, String>();
			 response.put("fileName", String.valueOf(items[0]));
			 response.put("campaingName", String.valueOf(items[1]));
			 responseList.add(response);
		 });
		return responseList;
	}
	public List<EmailBlasting>  showEmailDataByFileName(String filename) {
		return emailBlastingRepository.showEmailDataByFileName(filename);
	}
	public List<Map<String,String>> showUploadedEmailFilesByCampaingName(String campaingName) {
		List<Map<String,String>> responseList= new ArrayList<>();
		 emailBlastingRepository.fileNamesByCampaingName(campaingName).forEach(items-> {
			 Map<String,String> response= new HashMap<String, String>();
			 response.put("fileName", String.valueOf(items[0]));
			 response.put("campaingName", String.valueOf(items[1]));
			 responseList.add(response);
		 });
		 return responseList;
	}
	public Map<String,String> uploadDND(MultipartFile file) {
		Reader reader = null;
		try {
			String fileName= file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
			reader = new InputStreamReader(file.getInputStream());
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> list = parser.getRecords();
			List<DndNumbers> dndNumberList= new ArrayList<DndNumbers>();
			for (int j = 1; j < list.size(); j++) {
				DndNumbers dndNumber= new DndNumbers();
				dndNumber.setServiceAreaCode(list.get(j).get(0));
				dndNumber.setOpstype(list.get(j).get(3));
				dndNumber.setPhoneType(list.get(j).get(4));
				dndNumber.setPreferences(list.get(j).get(2));
				dndNumber.setPhoneNumber(list.get(j).get(1));
				dndNumberList.add(dndNumber);
			}
			dndNumbersRepository.saveAll(dndNumberList);
			Map<String,String> response= new HashMap<String, String>();
			response.put("status", "true");
			return response;

		} catch (IOException e) {
			e.printStackTrace();
			Map<String,String> response= new HashMap<String, String>();
			response.put("status", "false");
			return response;
		}
	}
	public Map<String,String> uploadEmails(MultipartFile file,String campaingName) {
		Reader reader = null;
		try {
			String fileName= file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
			System.out.println(fileName);
			reader = new InputStreamReader(file.getInputStream());
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> list = parser.getRecords();
			List<EmailBlasting> emailBlastingList= new ArrayList<EmailBlasting>();
			for (int j = 1; j < list.size(); j++) {
				EmailBlasting emailBlasting= new EmailBlasting();
				emailBlasting.setCampaingName(campaingName);
				emailBlasting.setEmail(list.get(j).get(0));
				emailBlasting.setStatus("Y");
				emailBlasting.setFileName(fileName);
				emailBlastingList.add(emailBlasting);
			}
			emailBlastingRepository.saveAll(emailBlastingList);
			Map<String,String> response= new HashMap<String, String>();
			response.put("status", "true");
			return response;

		} catch (IOException e) {
			e.printStackTrace();
			Map<String,String> response= new HashMap<String, String>();
			response.put("status", "false");
			return response;
		}
	}
	
	public Map<String,String> filterDND(MultipartFile file,String reportingLocation) {
		Reader reader = null;
		try {
			String fileName= file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
			System.out.println(fileName);
			reader = new InputStreamReader(file.getInputStream());
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> list = parser.getRecords();
			List<CSVRecord> dndRecords= new ArrayList<CSVRecord>();
			List<CSVRecord> nonDndRecords= new ArrayList<CSVRecord>();
			FileWriter dndOut = new FileWriter(reportingLocation+fileName+"DndRecords.csv");
			CSVPrinter writerDnd = CSVFormat.DEFAULT
			  .withHeader(String.join(", ", list.get(0))).print(dndOut);
			FileWriter nonDndOut = new FileWriter(reportingLocation+fileName+"NonDndRecords.csv");
			CSVPrinter writerNonDnd = CSVFormat.DEFAULT
			  .withHeader(String.join(", ", list.get(0))).print(nonDndOut);
			for (int j = 1; j < list.size(); j++) {
				String mobileNumber=list.get(j).get(0);
				DndNumbers dndNumbers= dndNumbersRepository.findByPhoneNumber(mobileNumber);
				if(dndNumbers!=null) {
					writerDnd.printRecords(String.join(", ", list.get(j)));
					dndRecords.add(list.get(j));
				}else {
					writerNonDnd.printRecords(String.join(", ", list.get(j)));
					nonDndRecords.add(list.get(j));
				}
			}
			writerDnd.flush();
			writerNonDnd.flush();
			Map<String,String> response= new HashMap<String, String>();
			response.put("Total_Records", String.valueOf(dndRecords.size()+nonDndRecords.size()));
			response.put("DND_Records", String.valueOf(dndRecords.size()));
			response.put("NON_DND_Records", String.valueOf(nonDndRecords.size()));
			response.put("filenameDnd", fileName+"DndRecords.csv");
			response.put("filenameNonDnd", fileName+"NonDndRecords.csv");
			response.put("status", "true");
			return response;

		} catch (IOException e) {
			e.printStackTrace();
			Map<String,String> response= new HashMap<String, String>();
			response.put("status", "false");
			return response;
		}
	}

	public static <T> List<T> getListFromIterator(Iterator<T> iterator) { 
	    Iterable<T> iterable = () -> iterator; 

	    return StreamSupport 
	              .stream(iterable.spliterator(), false) 
	              .collect(Collectors.toList()); 
	} 
	// public void createLead(Map<String, String> request) {
	// System.out.println(request);
	// Leads leads = new Leads();
	// leads.setAddress1(request.get("address1"));
	// leads.setAddress2(request.get("address2"));
	// leads.setAddress3(request.get("address3"));
	// leads.setAlternatePhonenumber(request.get("alternatePhonenumber"));
	// leads.setCity(request.get("city"));
	// leads.setCountryCode(request.get("countryCode"));
	// leads.setEmail(request.get("email"));
	// leads.setFirstName(request.get("firstName"));
	// leads.setGender(request.get("gender"));
	// leads.setLastName(request.get("lastName"));
	// leads.setMiddleName(request.get("middleName"));
	// leads.setPhoneNumber(request.get("phoneNumber"));
	// leads.setState(request.get("state"));
	// leads.setPostalCode(request.get("postalCode"));
	// leads.setStatus(request.get("status"));
	// leadRepository.save(leads);
	// }

	public List<Map<String, String>> fetchcountrecordingreportdatabetween(Map<String, Object> request,
			String reportingLocation) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		List<String> status = (List<String>) request.get("status");
		String toDate = String.valueOf(request.get("dateto"));
		String fromDate = String.valueOf(request.get("datefrom"));
		Integer limit = (Integer) request.get("limit");
		Integer offset = (Integer) request.get("offset");
		String phoneNumber = String.valueOf(request.get("phoneNumber"));
		System.out.println(request);
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dateTo = dateFormat.parse(toDate);
			Date dateFrom = dateFormat.parse(fromDate);
			dateToTimestamp = new java.sql.Timestamp(dateTo.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFrom.getTime());
		} catch (Exception e) {

			e.printStackTrace();
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		// List<String> campaings = usersRepository.fetchCampaingOfUser(users,
		// campaing);
		// System.out.println(campaings);
		// List<Integer> leadsId =
		// campaingLeadMappingRepository.findLeadsByCampaingName(campaings);
		// System.out.println(leadsId);
		List<Map<String, String>> response = new ArrayList<>();
		// XSSFWorkbook workbook = new XSSFWorkbook();
		// XSSFSheet sheet = workbook.createSheet("RecordingReport");
		// int rownum = 0;
		// int cellnumHeader = 0;
		// Row rowHeader = sheet.createRow(rownum++);
		// Cell header1 = rowHeader.createCell(cellnumHeader++);
		// header1.setCellValue("User");
		// Cell header2 = rowHeader.createCell(cellnumHeader++);
		// header2.setCellValue("CallEndDate");
		// Cell header3 = rowHeader.createCell(cellnumHeader++);
		// header3.setCellValue("callDate");
		// Cell header4 = rowHeader.createCell(cellnumHeader++);
		// header4.setCellValue("Status");
		// Cell header6 = rowHeader.createCell(cellnumHeader++);
		// header6.setCellValue("Duration");
		// Cell header7 = rowHeader.createCell(cellnumHeader++);
		// header7.setCellValue("Phone_number");
		// Cell header8 = rowHeader.createCell(cellnumHeader++);
		// header8.setCellValue("Phone_book");
		// Cell header9 = rowHeader.createCell(cellnumHeader++);
		// header9.setCellValue("Recording_file");
		// Cell header10 = rowHeader.createCell(cellnumHeader++);
		// header10.setCellValue("Campaing");
		if (status.equals("ALL")) {
			for (Object[] items : recordingRepository.fetchRecordingsByLeadIds(phoneNumber, users, dateFromTimestamp,
					dateToTimestamp, limit, offset)) {
				Map<String, String> responseMap = new HashMap<>();
				responseMap.put("USER", String.valueOf(items[0]));
				responseMap.put("CallEndDate", String.valueOf(items[1]));
				responseMap.put("callDate", String.valueOf(items[2]));
				responseMap.put("Status", String.valueOf(items[3]));
				responseMap.put("Duration", String.valueOf(items[4]));
				responseMap.put("Recording_file", String.valueOf(items[5]));
				responseMap.put("Phone_number", String.valueOf(items[6]));
				responseMap.put("Phone_book", String.valueOf(items[7]));
				response.add(responseMap);

			}
		} else {
			for (Object[] items : recordingRepository.fetchRecordingsByLeadIdsAndStatus(status, phoneNumber, users,
					dateFromTimestamp, dateToTimestamp, limit, offset)) {
				Map<String, String> responseMap = new HashMap<>();
				responseMap.put("USER", String.valueOf(items[0]));
				responseMap.put("CallEndDate", String.valueOf(items[1]));
				responseMap.put("callDate", String.valueOf(items[2]));
				responseMap.put("Status", String.valueOf(items[3]));
				responseMap.put("Duration", String.valueOf(items[4]));
				responseMap.put("Recording_file", String.valueOf(items[5]));
				responseMap.put("Phone_number", String.valueOf(items[6]));
				responseMap.put("Phone_book", String.valueOf(items[7]));
				response.add(responseMap);

			}
		}

		// File excelFile = new File(reportingLocation + "RecordingReport.xlsx");
		// OutputStream fos;
		// try {
		// fos = new FileOutputStream(excelFile);
		// workbook.write(fos);
		// fos.close();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		return response;
	}

	public List<Attendance> fetchcountattendancereportdatabetween(Map<String, Object> request,
			String reportingLocation) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		String toDate = String.valueOf(request.get("dateto"));
		String fromDate = String.valueOf(request.get("datefrom"));
		System.out.println(request);
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dateTo = dateFormat.parse(toDate);
			Date dateFrom = dateFormat.parse(fromDate);
			dateToTimestamp = new java.sql.Timestamp(dateTo.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFrom.getTime());
		} catch (Exception e) {

			e.printStackTrace();
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);

		List<String> userNames = usersRepository.fetchUserNameByCampaingNames(campaing);
		List<Attendance> userAttendance;
		if (users.isEmpty()) {
			userAttendance = attendanceRepository.findAttendanceByUserName(userNames, dateFromTimestamp,
					dateToTimestamp);
		} else {
			userAttendance = attendanceRepository.findAttendanceByUserName(users, dateFromTimestamp, dateToTimestamp);
		}
		System.out.println(userAttendance);
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("MISReport");
			int rownum = 0;
			int cellnumHeader = 0;
			Row rowHeader = sheet.createRow(rownum++);
			Cell header1 = rowHeader.createCell(cellnumHeader++);
			header1.setCellValue("User");
			Cell header2 = rowHeader.createCell(cellnumHeader++);
			header2.setCellValue("Login Time");
			Cell header3 = rowHeader.createCell(cellnumHeader++);
			header3.setCellValue("Logout Time");
			Cell header4 = rowHeader.createCell(cellnumHeader++);
			header4.setCellValue("Work Hour");
			for (Attendance attendance : userAttendance) {
				// this creates a new row in the sheet
				Row row = sheet.createRow(rownum++);
				int cellnum = 0;
				Cell cell1 = row.createCell(cellnum++);
				cell1.setCellValue((String) attendance.getUsername());
				Cell cell2 = row.createCell(cellnum++);
				cell2.setCellValue(String.valueOf(attendance.getLoggedInTime()));
				Cell cell3 = row.createCell(cellnum++);
				cell3.setCellValue((String) String.valueOf(attendance.getLoggedOutTime()));
				Cell cell4 = row.createCell(cellnum++);
				cell4.setCellValue((String) attendance.getTotalWorkHour());
				Cell cell6 = row.createCell(cellnum++);
			}
			File excelFile = new File(reportingLocation + "AttendanceReport.xlsx");
			OutputStream fos = new FileOutputStream(excelFile);
			workbook.write(fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userAttendance;
	}

	// WORK IN PROGRESS
	public Map<String, String> fetchAttendancereportdatabetween(Map<String, Object> request, String reportingLocation) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;
		Map<String, String> response = new HashMap<>();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date dateTo = dateFormat.parse(String.valueOf(request.get("dateto")));
			Date dateFrom = dateFormat.parse(String.valueOf(request.get("datefrom")));
			dateToTimestamp = new java.sql.Timestamp(dateTo.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFrom.getTime());
			response.put("status", "true");
		} catch (Exception e) { // this generic but you can control another types of exception
			// look the origin of excption
			response.put("status", "false");
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		List<String> userNames = usersRepository.fetchUserNameByCampaingNames(campaing);
		List<Attendance> userAttendance;
		if (users.isEmpty()) {
			userAttendance = attendanceRepository.findAttendanceByUserName(userNames, dateFromTimestamp,
					dateToTimestamp);
		} else {
			userAttendance = attendanceRepository.findAttendanceByUserName(users, dateFromTimestamp, dateToTimestamp);
		}

		return response;
	}

	public MultiMap<String, Map<String, Object>> fetchcountreportdatabetween(Map<String, Object> request) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		// List<String> campaings = usersRepository.fetchCampaingOfUser(users,
		// campaing);
		// System.out.println(campaings);
		// List<Integer> leadsId =
		// campaingLeadMappingRepository.findLeadsByCampaingName(campaings);
		// List<Map<String, Map<String, Object>>> resultArray = new ArrayList<>();
		// System.out.println(leadsId);
		String dateTo = String.valueOf(request.get("dateto"));
		String dateFrom = String.valueOf(request.get("datefrom"));
		String phoneNumber = String.valueOf(request.get("phoneNumber"));
		// call_date >= TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and call_end_date
		// <= TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS')
		System.out.println("select name,status,count(status) as count from leads where  call_date >= TO_TIMESTAMP('"
				+ dateTo + "','YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP('" + dateFrom
				+ "','YYYY-MM-DD HH24:MI:SS') and id IN (1,2) GROUP BY name,status");
		// Map<String,List<Map<String,Object>>> resultUsers = new HashMap<>();
		// MultivaluedHashMap<String, Map<String,Object>> resultUsers = new
		// MultivaluedHashMap<>();
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date dateToDate = dateFormat.parse(dateTo);
			Date dateFromDate = dateFormat.parse(dateFrom);
			// System.out.println(dateToDate);
			dateToTimestamp = new java.sql.Timestamp(dateToDate.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFromDate.getTime());
		} catch (Exception e) { // this generic but you can control another types of exception
			// look the origin of excption
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		MultiMap<String, Map<String, Object>> resultUsers = new MultiValueMap<>();
		if (phoneNumber.equals("")) {
			callLogsRepository.fetchcountreportdatabetween(users, dateFromTimestamp, dateToTimestamp)
					.forEach((items) -> {
						System.out.println(items[0]);
						System.out.println(items[1]);
						System.out.println(items[2]);
						System.out.println("#######");
						Map<String, Object> rslt = new HashMap<>();
						rslt.put(String.valueOf(items[1]).replaceAll("\\s", ""), items[2]);
						resultUsers.put(String.valueOf(items[0]), rslt);
						System.out.println(resultUsers);
					});
		} else {
			callLogsRepository.fetchcountreportdatabetweenByPhoneNumber(phoneNumber, dateFromTimestamp, dateToTimestamp)
					.forEach((items) -> {
						System.out.println(items[0]);
						System.out.println(items[1]);
						System.out.println(items[2]);
						System.out.println("#######");
						Map<String, Object> rslt = new HashMap<>();
						rslt.put(String.valueOf(items[1]).replaceAll("\\s", ""), items[2]);
						resultUsers.put(String.valueOf(items[0]), rslt);
						System.out.println(resultUsers);
					});
		}
		return resultUsers;
	}

	public MultiMap<String, Map<String, Object>> fetchcountreportdatabetweenByUser(Map<String, Object> request) {
		String userName = String.valueOf(request.get("userName"));
		// List<Integer> leadsId =
		// campaingLeadMappingRepository.findLeadsByUsernameName(userName);
		//// List<Map<String, Map<String, Object>>> resultArray = new ArrayList<>();
		// System.out.println(leadsId);
		// String dateTo = String.valueOf(request.get("dateto"));
		// String dateFrom = String.valueOf(request.get("datefrom"));
		// System.out.println(
		// "select name,status,count(status) as count from leads where date_modified
		// BETWEEN TO_TIMESTAMP('"
		// + dateTo + "','YYYY-MM-DD HH24:MI:SS') and TO_TIMESTAMP('" + dateFrom
		// + "','YYYY-MM-DD HH24:MI:SS') and id IN (1,2) GROUP BY name,status");
		// Map<String,List<Map<String,Object>>> resultUsers = new HashMap<>();
		// MultivaluedHashMap<String, Map<String,Object>> resultUsers = new
		// MultivaluedHashMap<>();
		MultiMap<String, Map<String, Object>> resultUsers = new MultiValueMap<>();
		callLogsRepository.fetchcountreportdatabetweenWithUsers(userName, String.valueOf(request.get("dateto")),
				String.valueOf(request.get("datefrom"))).forEach((items) -> {
					System.out.println(items[0]);
					System.out.println(items[1]);
					System.out.println(items[2]);
					System.out.println("#######");
					Map<String, Object> rslt = new HashMap<>();
					rslt.put(String.valueOf(items[1]).replaceAll("\\s", ""), items[2]);
					// rsltArray.add(rslt);
					resultUsers.put(String.valueOf(items[0]), rslt);
					System.out.println(resultUsers);
					// resultArray.add(resultUsers);
				});

		// Map<String,Map<String,Integer>> resultmap = new HashMap<>();
		// for(Map.Entry<String,Object> items:resultUsers.entrySet()) {
		// String[] keys=items.getKey().split("=");
		// Map<String,Integer> rslt = new HashMap<>();
		// rslt.put(keys[1], (Integer)items.getValue());
		// resultmap.put(keys[0], rslt);
		// }
		return resultUsers;
	}

	public void feedbackLeadsMutiple(List<Map<String, String>> request) {
		List<Leads> listUsers = new ArrayList<>();
		List<CallLogs> listCallLogs = new ArrayList<>();

		request.forEach(items -> {
			if (Integer.parseInt(items.get("leadId")) != 0) {
				Leads leads = leadRepository.findById(Integer.parseInt(items.get("leadId"))).get();
				CallLogs callLogs = new CallLogs();
				leads.setStatus(items.get("status"));
				String callStartTime = "";
				String callEndTime = "";
				try {
					callStartTime = LeadsService.formatDate(items.get("callStartedTime"), "dd-MM-yyyy HH:mm:ss",
							"yyyy-MM-dd HH:mm:ss");
					callEndTime = LeadsService.formatDate(items.get("callEndTime"), "dd-MM-yyyy HH:mm:ss",
							"yyyy-MM-dd HH:mm:ss");

				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println(callStartTime);
				Date startTimeCall = null;
				Date endTimeCall = null;
				try {
					startTimeCall = dateFormat.parse(callStartTime);
					endTimeCall = dateFormat.parse(callEndTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("###############batch feedback");
				System.out.println(startTimeCall);
				long difference_In_Time = endTimeCall.getTime() - startTimeCall.getTime();
				System.out.println(TimeUnit.MILLISECONDS.toSeconds(difference_In_Time));
				long difference_In_Seconds = TimeUnit.MILLISECONDS.toSeconds(difference_In_Time);
				System.out.println(difference_In_Time + "###############TIME DIFFERENCE");
				System.out.println(difference_In_Seconds);
				leads.setName(items.get("username"));
				listUsers.add(leads);
				callLogs.setAssignedTo(items.get("username"));
				callLogs.setCallDate(startTimeCall);
				callLogs.setCallEndDate(endTimeCall);
				callLogs.setCallBackDateTime(items.get("callBackDateTime"));
				callLogs.setComments(items.get("comment"));
				callLogs.setCallDuration(String.valueOf(difference_In_Seconds));
				callLogs.setLeadId(leads.getId());
				callLogs.setPhoneNumber(leads.getPhoneNumber());
				callLogs.setCallType(items.get("callType"));
				if (items.get("recordingName") != null) {
					callLogs.setRecording(items.get("recordingName"));
				}
				callLogs.setCrm(leads.getCrm());
				callLogs.setStatus(leads.getStatus());
				listCallLogs.add(callLogs);
			} else {
				List<String> listCampName = campaingRepository.findCampaingByUserName(items.get("username"));
				String campName = listCampName != null ? listCampName.get(0) : "";
				Leads lead = new Leads();
				lead.setAssignedTo(items.get("username"));
				lead.setCallBackDateTime(items.get("callBackDateTime"));
				lead.setFilename("manual" + campName);
				lead.setPhoneNumber(items.get("phone_number"));
				lead.setStatus(items.get("status"));
				LeadVersions leadVersion = leadVersionsRepository.findByFileName("manual" + campName);
				if (leadVersion == null) {
					leadVersion = new LeadVersions();
					leadVersion.setCampaingName(campName);
					leadVersion.setFilename("manual" + campName);
					leadVersion.setStatus("Active");
					leadVersionsRepository.save(leadVersion);
				}
				leadRepository.save(lead);
				CallLogs callLogs = new CallLogs();
				callLogs.setStatus(items.get("status"));
				String callStartTime = "";
				String callEndTime = "";
				try {
					callStartTime = LeadsService.formatDate(items.get("callStartedTime"), "dd-MM-yyyy HH:mm:ss",
							"yyyy-MM-dd HH:mm:ss");
					callEndTime = LeadsService.formatDate(items.get("callEndTime"), "dd-MM-yyyy HH:mm:ss",
							"yyyy-MM-dd HH:mm:ss");

				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println(callStartTime);
				Date startTimeCall = null;
				Date endTimeCall = null;
				try {
					startTimeCall = dateFormat.parse(callStartTime);
					endTimeCall = dateFormat.parse(callEndTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("###############batch feedback");
				System.out.println(startTimeCall);
				long difference_In_Time = endTimeCall.getTime() - startTimeCall.getTime();
				long difference_In_Seconds = TimeUnit.MILLISECONDS.toSeconds(difference_In_Time);
				callLogs.setAssignedTo(items.get("username"));
				callLogs.setCallDate(startTimeCall);
				callLogs.setCallEndDate(endTimeCall);
				callLogs.setCallBackDateTime(items.get("callBackDateTime"));
				callLogs.setComments(items.get("comment"));
				callLogs.setCallDuration(String.valueOf(difference_In_Seconds));
				callLogs.setLeadId(lead.getId());
				callLogs.setPhoneNumber(lead.getPhoneNumber());
				callLogs.setCrm(lead.getCrm());
				callLogs.setCallType(items.get("callType"));
				if (items.get("recordingName") != null) {
					callLogs.setRecording(items.get("recordingName"));
				}
				callLogs.setStatus(lead.getStatus());
				listCallLogs.add(callLogs);
			}
		});
		leadRepository.saveAll(listUsers);
		callLogsRepository.saveAll(listCallLogs);

	}

	public static String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException {

		Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
		SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
		String parsedDate = formatter.format(initDate);

		return parsedDate;
	}

	public Map<String, List<CallLogs>> fetchreportdatabetweenWithUserName(Map<String, Object> request) {
		Map<String, List<CallLogs>> response = new HashMap<>();
		String userName = String.valueOf(request.get("userName"));
		// List<String> campaing = (List<String>) request.get("campaingName");
		// List<String> campaings = usersRepository.fetchCampaingOfUser(users,
		// campaing);
		// List<Integer> leadsId =
		// campaingLeadMappingRepository.findLeadsByCampaingName(campaings);
		String dateTo = (String) request.get("dateto");
		String dateFrom = (String) request.get("datefrom");
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date dateToDate = dateFormat.parse(dateTo);
			Date dateFromDate = dateFormat.parse(dateFrom);
			// System.out.println(dateToDate);
			dateToTimestamp = new java.sql.Timestamp(dateToDate.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFromDate.getTime());
		} catch (Exception e) { // this generic but you can control another types of exception
			// look the origin of excption
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		List<CallLogs> resultLeads = callLogsRepository.fetchreportdatabetweenWithUserName(userName, dateFromTimestamp,
				dateToTimestamp);
		response.put("leads", resultLeads);
		// try {
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// XSSFWorkbook workbook = new XSSFWorkbook();
		// XSSFSheet sheet = workbook.createSheet("MISReport");
		// int rownum = 0;
		// int cellnumHeader = 0;
		// Row rowHeader = sheet.createRow(rownum++);
		// Cell header1 = rowHeader.createCell(cellnumHeader++);
		// header1.setCellValue("Assigned User");
		// Cell header2 = rowHeader.createCell(cellnumHeader++);
		// header2.setCellValue("Phone Nmber");
		// Cell header3 = rowHeader.createCell(cellnumHeader++);
		// header3.setCellValue("Status");
		// Cell header4 = rowHeader.createCell(cellnumHeader++);
		// header4.setCellValue("FirstName");
		// Cell header5 = rowHeader.createCell(cellnumHeader++);
		// header5.setCellValue("Address");
		// Cell header6 = rowHeader.createCell(cellnumHeader++);
		// header6.setCellValue("Comments");
		// Cell header7 = rowHeader.createCell(cellnumHeader++);
		// header7.setCellValue("Call Duration");
		// Cell header8 = rowHeader.createCell(cellnumHeader++);
		// header8.setCellValue("Call Date");
		// for (Leads lead : resultLeads) {
		// // this creates a new row in the sheet
		// Row row = sheet.createRow(rownum++);
		// int cellnum = 0;
		// Cell cell1 = row.createCell(cellnum++);
		// cell1.setCellValue((String) lead.getName());
		// Cell cell2 = row.createCell(cellnum++);
		// cell2.setCellValue((String) lead.getPhoneNumber());
		// Cell cell3 = row.createCell(cellnum++);
		// cell3.setCellValue((String) lead.getStatus());
		// Cell cell4 = row.createCell(cellnum++);
		// cell4.setCellValue((String) lead.getFirstName());
		// Cell cell5 = row.createCell(cellnum++);
		// cell5.setCellValue((String) lead.getAddress1());
		// Cell cell6 = row.createCell(cellnum++);
		// cell6.setCellValue((String) lead.getComments());
		// Cell cell7 = row.createCell(cellnum++);
		// cell7.setCellValue((String) lead.getLastLocalCallTime());
		// Cell cell8 = row.createCell(cellnum++);
		// cell8.setCellValue((String.valueOf(lead.getDateModified())));
		// }
		// File excelFile = new File(reportingLocation + "MISReport.xlsx");
		// OutputStream fos = new FileOutputStream(excelFile);
		// workbook.write(fos);
		// fos.close();
		// System.out.println("MISReport.xlsx written successfully on disk.");
		// FileInputStream fis = new FileInputStream(excelFile);
		// byte[] buf = new byte[1024];
		// try {
		// for (int readNum; (readNum = fis.read(buf)) != -1;) {
		// stream.write(buf, 0, readNum); // no doubt here is 0
		// System.out.println("read " + readNum + " bytes,");
		// }
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }
		// byte[] bytes = stream.toByteArray();
		//
		// HttpHeaders header = new HttpHeaders();
		// header.setContentType(new MediaType("application", "force-download"));
		// header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;
		// filename=ProductTemplate.xlsx");
		// return new ByteArrayResource(bytes);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return response;
	}

	public ByteArrayResource fetchreportdatabetween(Map<String, Object> request, String reportingLocation) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		String phoneNumber = (String) request.get("phoneNumber");
		// List<String> campaings = usersRepository.fetchCampaingOfUser(users,
		// campaing);
		// List<Integer> leadsId =
		// campaingLeadMappingRepository.findLeadsByCampaingName(campaing);
		String dateTo = String.valueOf(request.get("dateto"));
		String dateFrom = String.valueOf(request.get("datefrom"));
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date dateToDate = dateFormat.parse(dateTo);
			Date dateFromDate = dateFormat.parse(dateFrom);
			// System.out.println(dateToDate);
			dateToTimestamp = new java.sql.Timestamp(dateToDate.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFromDate.getTime());
		} catch (Exception e) { // this generic but you can control another types of exception
			// look the origin of exception
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		List<Object[]> resultLeads = new ArrayList<>();
		if (phoneNumber.equals("")) {
			resultLeads.addAll(callLogsRepository.fetchreportdatabetween(users, dateFromTimestamp, dateToTimestamp));
		} else {
			resultLeads.addAll(callLogsRepository.fetchreportdatabetweenByPhoneNumber(phoneNumber, dateFromTimestamp,
					dateToTimestamp));
		}
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("MISReport");
			int rownum = 0;
			int cellnumHeader = 0;
			Row rowHeader = sheet.createRow(rownum++);
			Cell header1 = rowHeader.createCell(cellnumHeader++);
			header1.setCellValue("Assigned User");
			Cell header2 = rowHeader.createCell(cellnumHeader++);
			header2.setCellValue("Phone Nmber");
			Cell header3 = rowHeader.createCell(cellnumHeader++);
			header3.setCellValue("Status");
			Cell header4 = rowHeader.createCell(cellnumHeader++);
			header4.setCellValue("FirstName");
			Cell header6 = rowHeader.createCell(cellnumHeader++);
			header6.setCellValue("Comments");
			Cell header7 = rowHeader.createCell(cellnumHeader++);
			header7.setCellValue("Call Duration");
			Cell header8 = rowHeader.createCell(cellnumHeader++);
			header8.setCellValue("Call Start Date");
			Cell header9 = rowHeader.createCell(cellnumHeader++);
			header9.setCellValue("Call End Date");
			Cell header11 = rowHeader.createCell(cellnumHeader++);
			header11.setCellValue("Call Type");
			Cell header10 = rowHeader.createCell(cellnumHeader++);
			header10.setCellValue("Recording");
			Font hlink_font = workbook.createFont();
			hlink_font.setUnderline(Font.U_SINGLE);
			hlink_font.setColor(IndexedColors.BLUE.getIndex());
			CellStyle hlink_style = workbook.createCellStyle();
			CreationHelper createHelper = workbook.getCreationHelper();
			for (Object[] lead : resultLeads) {
				// this creates a new row in the sheet
				Row row = sheet.createRow(rownum++);
				int cellnum = 0;
				Cell cell1 = row.createCell(cellnum++);
				cell1.setCellValue((String) lead[0]);
				Cell cell2 = row.createCell(cellnum++);
				cell2.setCellValue((String) lead[1]);
				Cell cell3 = row.createCell(cellnum++);
				cell3.setCellValue((String) lead[2]);
				Cell cell4 = row.createCell(cellnum++);
				cell4.setCellValue((String) lead[3]);
				Cell cell5 = row.createCell(cellnum++);
				cell5.setCellValue((String) lead[4]);
				Cell cell6 = row.createCell(cellnum++);
				cell6.setCellValue((String) lead[5]);
				Cell cell7 = row.createCell(cellnum++);
				cell7.setCellValue(String.valueOf(lead[6]));
				Cell cell8 = row.createCell(cellnum++);
				cell8.setCellValue((String.valueOf(lead[7])));
				Cell cell10 = row.createCell(cellnum++);
				cell10.setCellValue((String.valueOf(lead[9])));
				Cell cell9 = row.createCell(cellnum++);
				hlink_style.setFont(hlink_font);
				org.apache.poi.ss.usermodel.Hyperlink hp = createHelper
						.createHyperlink(org.apache.poi.ss.usermodel.Hyperlink.LINK_FILE);
				String filename = String.valueOf(lead[8]);
				if (filename.indexOf(".") > 0)
					filename = filename.substring(0, filename.lastIndexOf(".")) + ".mp3";
				String url = "http://157.245.109.0:8080/microsmartui/dist/assets/recording/mp3/" + filename;
				url = URLEncoder.encode(url.replace("\\", "/"));
				hp.setAddress(url);
				try {
					hp.setLabel(url);
					cell9.setCellValue(filename);
					cell9.setHyperlink(hp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			File excelFile = new File(reportingLocation + "MISReport.xlsx");
			OutputStream fos = new FileOutputStream(excelFile);
			workbook.write(fos);
			fos.close();
			System.out.println("MISReport.xlsx written successfully on disk.");
			FileInputStream fis = new FileInputStream(excelFile);
			byte[] buf = new byte[1024];
			try {
				for (int readNum; (readNum = fis.read(buf)) != -1;) {
					stream.write(buf, 0, readNum); // no doubt here is 0
					System.out.println("read " + readNum + " bytes,");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			byte[] bytes = stream.toByteArray();

			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "force-download"));
			header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ProductTemplate.xlsx");
			return new ByteArrayResource(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void feedback(int id, String status, String calltime) {
		Leads leads = leadRepository.findById(id).get();
		leads.setStatus(status);
		// leads.setLastLocalCallTime(calltime);
		leadRepository.save(leads);

	}

	public void feedbackLead(Map<String, String> items) {

		if (Integer.parseInt(items.get("leadId")) != 0) {
			Leads leads = leadRepository.findById(Integer.parseInt(items.get("leadId"))).get();
			CallLogs callLogs = new CallLogs();
			leads.setStatus(items.get("status"));
			String callStartTime = "";
			String callEndTime = "";
			try {
				callStartTime = LeadsService.formatDate(items.get("callStartedTime"), "dd-MM-yyyy HH:mm:ss",
						"yyyy-MM-dd HH:mm:ss");
				callEndTime = LeadsService.formatDate(items.get("callEndTime"), "dd-MM-yyyy HH:mm:ss",
						"yyyy-MM-dd HH:mm:ss");

			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(callStartTime);
			Date startTimeCall = null;
			Date endTimeCall = null;
			try {
				startTimeCall = dateFormat.parse(callStartTime);
				endTimeCall = dateFormat.parse(callEndTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("###############batch feedback");
			System.out.println(startTimeCall);
			long difference_In_Time = endTimeCall.getTime() - startTimeCall.getTime();
			System.out.println(TimeUnit.MILLISECONDS.toSeconds(difference_In_Time));
			long difference_In_Seconds = TimeUnit.MILLISECONDS.toSeconds(difference_In_Time);
			System.out.println(difference_In_Time + "###############TIME DIFFERENCE");
			System.out.println(difference_In_Seconds);
			leads.setName(items.get("username"));
			leadsRepository.save(leads);
			// listUsers.add(leads);
			callLogs.setAssignedTo(items.get("username"));
			callLogs.setCallDate(startTimeCall);
			callLogs.setCallEndDate(endTimeCall);
			callLogs.setCallBackDateTime(items.get("callBackDateTime"));
			callLogs.setComments(items.get("comment"));
			callLogs.setCallDuration(String.valueOf(difference_In_Seconds));
			callLogs.setLeadId(leads.getId());
			callLogs.setCallType(items.get("callType"));
			if (items.get("recordingName") != null) {
				callLogs.setRecording(items.get("recordingName"));
			}
			callLogs.setPhoneNumber(leads.getPhoneNumber());
			callLogs.setCrm(leads.getCrm());
			callLogs.setStatus(leads.getStatus());
			callLogsRepository.save(callLogs);
			// listCallLogs.add(callLogs);
		} else {
			List<String> listCampName = campaingRepository.findCampaingByUserName(items.get("username"));
			String campName = listCampName != null ? listCampName.get(0) : "";
			Leads lead = new Leads();
			lead.setAssignedTo(items.get("username"));
			lead.setCallBackDateTime(items.get("callBackDateTime"));
			lead.setFilename("manual" + campName);
			lead.setPhoneNumber(items.get("phone_number"));
			lead.setStatus(items.get("status"));
			LeadVersions leadVersion = leadVersionsRepository.findByFileName("manual" + campName);
			if (leadVersion == null) {
				leadVersion = new LeadVersions();
				leadVersion.setCampaingName(campName);
				leadVersion.setFilename("manual" + campName);
				leadVersion.setStatus("Active");
				leadVersionsRepository.save(leadVersion);
			}
			leadRepository.save(lead);
			CallLogs callLogs = new CallLogs();
			callLogs.setStatus(items.get("status"));
			String callStartTime = "";
			String callEndTime = "";
			try {
				callStartTime = LeadsService.formatDate(items.get("callStartedTime"), "dd-MM-yyyy HH:mm:ss",
						"yyyy-MM-dd HH:mm:ss");
				callEndTime = LeadsService.formatDate(items.get("callEndTime"), "dd-MM-yyyy HH:mm:ss",
						"yyyy-MM-dd HH:mm:ss");

			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(callStartTime);
			Date startTimeCall = null;
			Date endTimeCall = null;
			try {
				startTimeCall = dateFormat.parse(callStartTime);
				endTimeCall = dateFormat.parse(callEndTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("###############batch feedback");
			System.out.println(startTimeCall);
			long difference_In_Time = endTimeCall.getTime() - startTimeCall.getTime();
			long difference_In_Seconds = TimeUnit.MILLISECONDS.toSeconds(difference_In_Time);
			callLogs.setAssignedTo(items.get("username"));
			callLogs.setCallDate(startTimeCall);
			callLogs.setCallEndDate(endTimeCall);
			callLogs.setCallBackDateTime(items.get("callBackDateTime"));
			callLogs.setComments(items.get("comment"));
			callLogs.setCallDuration(String.valueOf(difference_In_Seconds));
			callLogs.setLeadId(lead.getId());
			callLogs.setPhoneNumber(lead.getPhoneNumber());
			if (items.get("recordingName") != null) {
				callLogs.setRecording(items.get("recordingName"));
			}
			callLogs.setCrm(lead.getCrm());
			callLogs.setCallType(items.get("callType"));
			callLogs.setStatus(lead.getStatus());
			callLogsRepository.save(callLogs);
			// listCallLogs.add(callLogs);
		}
	}

	// public void createLeadWithCampaing(Map<String, String> request) {
	// Leads leads = new Leads();
	// MapString
	// leads.setAddress1(request.get("address1"));
	// leads.setAddress2(request.get("address2"));
	// leads.setAddress3(request.get("address3"));
	// leads.setAlternatePhonenumber(request.get("alternatePhonenumber"));
	// leads.setCity(request.get("city"));
	// leads.setCountryCode(request.get("countryCode"));
	// leads.setEmail(request.get("email"));
	// leads.setFirstName(request.get("firstName"));
	// leads.setGender(request.get("gender"));
	// leads.setLastName(request.get("lastName"));
	// leads.setMiddleName(request.get("middleName"));
	// leads.setPhoneNumber(request.get("phoneNumber"));
	// leads.setState(request.get("state"));
	// leads.setPostalCode(request.get("postalCode"));
	// leads.setStatus(request.get("status"));
	// leadRepository.save(leads);
	// }

	public List<Leads> fetchLeadByPhoneNumber(String phoneNumber) {
		return leadRepository.findLeadByNumber(phoneNumber);
	}

	// public void attachLeadToCampaing(Map<String, String> request) {
	// Optional<Leads> leads =
	// leadRepository.findById(Integer.parseInt(request.get("lead_id")));
	// if (leads.isPresent()) {
	// Leads lead = leads.get();
	// Optional<Campaing> campaing =
	// campaingRepository.findById(Integer.parseInt(request.get("campaing_id")));
	// if (campaing.isPresent()) {
	// Campaing campaingFound = new Campaing();
	// CampaingLeadMapping campaingLeadMapping = new CampaingLeadMapping();
	// campaingLeadMapping.setCampaingid(campaingFound.getId());
	// campaingLeadMapping.setLeadid(lead.getId());
	// campaingLeadMappingRepository.save(campaingLeadMapping);
	// }
	// }
	// }
}
