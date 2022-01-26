package com.praveen.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.praveen.dao.BreakRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.StatusRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.model.Break;
import com.praveen.model.BreakTypes;
import com.praveen.model.Campaing;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.Status;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

@Service
public class BreakService {
	@Autowired
	BreakRepository breakRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	GroupCampaingMappingRepository groupCampaingMappingRepository;
	@Autowired
	StatusRepository statusRepository;
	
	
	public Map<String,String> submitBreak(Map<String,String> request) {
		Map<String,String> response = new HashMap<>();
		Break breakobj= new  Break();
		breakobj.setBreakType(request.get("breakType"));
		breakobj.setInTime(request.get("inTime"));
		breakobj.setOutTime(request.get("outTime"));
		breakobj.setUserName(request.get("userName"));
		breakRepository.save(breakobj);
		response.put("status", "true");
		return response;
	}
}
