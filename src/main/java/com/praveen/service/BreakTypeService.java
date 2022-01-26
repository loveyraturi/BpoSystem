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
import com.praveen.dao.BreakTypesRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.StatusRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.model.BreakTypes;
import com.praveen.model.Campaing;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.Status;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

@Service
public class BreakTypeService {
	@Autowired
	BreakTypesRepository breakTypesRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	GroupCampaingMappingRepository groupCampaingMappingRepository;
	@Autowired
	StatusRepository statusRepository;
	
	public Map<String,List<BreakTypes>> fetchAllBreakTypes() {
		Map<String,List<BreakTypes>> response = new HashMap<>();
		response.put("breakType", breakTypesRepository.findAll());
		return response;
	}
	public Map<String,String> createBreakTypes(Map<String,String> request) {
		BreakTypes breakTypes= new BreakTypes();
		breakTypes.setBreakType(request.get("break_type"));
		breakTypes.setCampaingName(request.get("campaing_name"));
		breakTypesRepository.save(breakTypes);
		Map<String,String> response = new HashMap<>();
		response.put("status", "true");
		return response;
	}
	public Map<String,List<BreakTypes>> findBreaksByCampaingName(String campaingName) {
		Map<String,List<BreakTypes>> response = new HashMap<>();
		response.put("breakType", breakTypesRepository.findBreaksByCampaingName(campaingName));
		return response;
	}
	
	
}
