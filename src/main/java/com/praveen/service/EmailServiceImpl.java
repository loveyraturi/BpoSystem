package com.praveen.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.map.HashedMap;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.praveen.dao.CallLogsRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.EmailBlastingRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.EmailBlasting;
import com.praveen.model.Users;
import javax.mail.PasswordAuthentication;


@Component
public class EmailServiceImpl {

	@Autowired
	private JavaMailSender emailSender;
	@Autowired
	CallLogsRepository callLogsRepository;
	@Autowired
	UsersRepository userRepository;
	@Autowired
	EmailBlastingRepository emailBlastingRepository;
	@Value("${spring.mail.username}")
	String username;
	@Value("${reporting.location}")
	String reportingLocation;

	String fromDate;
	String toDate;
	ExecutorService executorService;
	
	EmailServiceImpl(){
		executorService= Executors.newFixedThreadPool(10);  

	}
	List<Map<String, Object>> emailScheduleData = new ArrayList<Map<String, Object>>();

	public void createRequest(Map<String, String> request) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime now = LocalDateTime.now();
		if (String.valueOf(request.get("scheduleType")).equals("firstHalf")) {
			this.fromDate = dtf.format(now) + " 01:00:00";
			this.toDate = dtf.format(now) + " 14:00:00";
		} else {
			this.fromDate = dtf.format(now) + " 14:00:00";
			this.toDate = dtf.format(now) + " 23:59:00";
		}
		Map<String, Object> emailData = new HashedMap<String, Object>();

		emailData.put("dateto", this.toDate);
		emailData.put("datefrom", this.fromDate);
		emailData.put("to", request.get("to"));
		emailData.put("status", request.get("status"));
		emailData.put("campaing", request.get("campaing"));
		System.out.println(emailData);
		emailScheduleData.add(emailData);

	}

	public void sendAttachment() {
		emailScheduleData.forEach(req -> {
			fetchreportdatabetween(req, reportingLocation);
			Map<String, String> response = new HashMap<>();
			response.put("status", "true");
			String to = (String) req.get("to");
			String from = username;
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = null;
			try {
				helper = new MimeMessageHelper(message, true);
				helper.setFrom(from);
				helper.setTo(to);
				helper.setSubject("MIS REPORT");
				helper.setText("MIS Report");
				FileSystemResource file = new FileSystemResource(new File(reportingLocation + "/MISReport.xlsx"));
				helper.addAttachment("MISReport.xlsx", file);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			emailSender.send(message);
		});
	}
	public void sendEmail(String campaingName, String messages,String email,String password) {
		emailBlastingRepository.fetchEmailByCampaingName(campaingName).forEach(items->{
			Map<String, String> response = new HashMap<>();
			response.put("status", "true");
			String to = items.getEmail();
			String from = username;
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = null;
			try {
				helper = new MimeMessageHelper(message, true);
				helper.setFrom(email);
				helper.setTo(to);
				helper.setSubject("MICROSMART");
				helper.setText(messages);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			emailSender.send(message);
		});
	}
	public void sendEmailJava(String campaingName, String messages,String from,String password,String subject) {

		emailBlastingRepository.fetchEmailByCampaingName(campaingName).forEach(items->{
			executorService.execute(new Runnable() {  
	              
	            @Override  
	            public void run() {  
	               
			System.out.println(items.getEmail());
			
	      Properties properties = System.getProperties();

	      properties.setProperty("mail.smtp.host", "smtp.gmail.com");
	      properties.setProperty("mail.smtp.user", from);
	      properties.setProperty("mail.password", password);
	      properties.setProperty("mail.port","587");
	      properties.setProperty("mail.smtp.port", "587"); //TLS Port
	      properties.setProperty("mail.smtp.auth", "true"); //enable authentication
	      properties.setProperty("mail.smtp.starttls.enable", "true");
	      Authenticator auth = new Authenticator() {
				//override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from, password);
				}
			};
	      Session session = Session.getInstance(properties, auth);
	      try {
	         MimeMessage message = new MimeMessage(session);
	         MimeMessageHelper helper = null;
				try {
					helper = new MimeMessageHelper(message, true);
					helper.setFrom(from);
					helper.setTo(items.getEmail());
					helper.setSubject(subject);
					helper.setText(messages);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
	         message.setFrom(new InternetAddress(from));
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(items.getEmail()));

	         message.setSubject(subject);

	         message.setText(messages);

	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
          
            }  
          });
		});
	}
	public void fetchreportdatabetween(Map<String, Object> request, String reportingLocation) {
		String status = (String) request.get("status");
		String campaing = (String) request.get("campaing");
		String dateTo = String.valueOf(request.get("dateto"));
		String dateFrom = String.valueOf(request.get("datefrom"));
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;
		List<String> users = userRepository.fetchUserNameByCampaingName(campaing);
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date dateToDate = dateFormat.parse(dateTo);
			Date dateFromDate = dateFormat.parse(dateFrom);
			dateToTimestamp = new java.sql.Timestamp(dateToDate.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFromDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		List<Object[]> resultLeads = new ArrayList<>();

		resultLeads.addAll(callLogsRepository.fetchreportdatabetween(users, dateFromTimestamp, dateToTimestamp));

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}