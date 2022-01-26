package com.praveen.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CronJobs implements Job {
	@Autowired
	EmailServiceImpl emailServiceImpl;



	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println("#############");
		System.out.println(emailServiceImpl);
		emailServiceImpl.sendAttachment();
	}
}
