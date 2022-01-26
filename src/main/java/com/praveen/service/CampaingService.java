package com.praveen.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.dao.StatusRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.model.Campaing;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.Leads;
import com.praveen.model.Status;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

@Service
public class CampaingService {
	@Autowired
	CampaingRepository campaingRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	GroupCampaingMappingRepository groupCampaingMappingRepository;
	@Autowired
	StatusRepository statusRepository;
	@Autowired
	LeadsRepository leadsRepository;

	public List<Campaing> fetchCampaingByUser(List<Integer> ids) {
		return campaingRepository.findAllById(ids);
	}
	
	public Map<String,String> fetchStatuByCampaingName(String CampaingName) {
		Map<String,String> response = new HashMap<>();
		Status status= statusRepository.findStatusCampaingName(CampaingName);
		if(status!=null) {
			response.put("statusFeedback",status.getStatus());
		}else {
			response.put("statusFeedback","");
		}
		 
		return response;
	}
	public JsonNode fetchCrm(String leadid) {
		JsonNode response = null;
		ObjectMapper mapper = new ObjectMapper();
		Leads leads= leadsRepository.findById(Integer.parseInt(leadid))!=null?leadsRepository.findById(Integer.parseInt(leadid)).get():new Leads();
		if(leads!=null) {
//			JsonParser parser = new JsonParser(); 
			
			String fields=leads.getCrm()!=null?leads.getCrm():"";
			System.out.println(fields.isEmpty());
			if(!fields.isEmpty()) {
				System.out.println("inside");
			 try {
				response = mapper.readTree(fields);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}else {
				try {
					response = mapper.readTree("{}");
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}else {
			try {
				response = mapper.readTree("{}");
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		ArrayNode arrayNode = mapper.createArrayNode();
		 ObjectNode crmResponse = mapper.createObjectNode();
////		System.out.println(response);
//		  Iterator<Map.Entry<String, JsonNode>> nodes = response.fields();
//		  Map.Entry<String, JsonNode> node = null;
//		  while (nodes.hasNext()) {
//			 ObjectNode crmCampaing = mapper.createObjectNode();
//		    node = nodes.next();
////		     System.out.println(node.getKey());
////		     System.out.println(node.getValue());
//		     crmCampaing.put("label", node.getKey());
//		     crmCampaing.put("field", node.getValue());
//		     arrayNode.add(crmCampaing);
//		  }
		  crmResponse.put("crm", response);
//		  System.out.println(arrayNode);
//		for (String key : response.fieldNames())
//		{
//		    if ("foo".equals(key))
//		    {
//		        // bar
//		    }
//		}
//		System.out.println(response.get(0));
//		ObjectNode user = mapper.createObjectNode();
//	    user3.put("id", 3);
//	    user3.put("name", "Emma Doe");

	    // create `ArrayNode` object
	    

	    // add JSON users to array
//	    arrayNode.addAll(Arrays.asList(user1, user2, user3));
		return crmResponse;
	}
	
	public void convert() {
		leadsRepository.findAll().forEach((leads)->{
			JsonNode response = null;
			ObjectMapper mapper = new ObjectMapper();
				
					String fields=leads.getCrm()!=null?leads.getCrm():"";
					System.out.println(fields.isEmpty());
					if(!fields.isEmpty()) {
						System.out.println("inside");
					 try {
						response = mapper.readTree(fields);
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}else {
						try {
							response = mapper.readTree("{}");
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
					}
				ArrayNode arrayNode = mapper.createArrayNode();
				 ObjectNode crmResponse = mapper.createObjectNode();
				  Iterator<Map.Entry<String, JsonNode>> nodes = response.fields();
				  Map.Entry<String, JsonNode> node = null;
				  while (nodes.hasNext()) {
					 ObjectNode crmCampaing = mapper.createObjectNode();
				    node = nodes.next();
				     crmCampaing.put("label", node.getKey());
				     crmCampaing.put("field", node.getValue());
				     arrayNode.add(crmCampaing);
				  }
//				  crmResponse.put("crm", arrayNode);
				  String json= "{}";
				try {
					json = new ObjectMapper().writeValueAsString(arrayNode);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  leads.setCrm(json);
			leadsRepository.save(leads);
		});
	}
	
	public List<Campaing> fetchCampaing() {
		return campaingRepository.findAll();
	}
	public void updateCrm(int id,Map<String,List<Map<String, String>>> request) {
		
//		Map<String,String> response= new HashMap<>();
//		request.get("crm").forEach((items)->{
//			System.out.println(items);
//			response.put(items.get("label"), items.get("field"));
//			
//		});
//		
//		for(Map.Entry<String, String> crm:request.entrySet()) {
//			crmMap.put("label", crm.getKey());
//			crmMap.put("field", crm.getValue());
//		}
		System.out.println("######################");
		System.out.println(request);
		Leads leads = leadsRepository.findById(id).get();
		if(leads!=null) {
			String json="{}";
			try {
				json = new ObjectMapper().writeValueAsString(request.get("crm"));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(json);
			leads.setCrm(json);
			leadsRepository.save(leads);
		}
	}
	public void createCampaing(Map<String, Object> request) {
		Gson gson = new Gson();
		String additionalFields=gson.toJson(request.get("additionalFields"));
		Campaing campaing = new Campaing();
		campaing.setActive(String.valueOf(request.get("active")));
		campaing.setName(String.valueOf(request.get("name")));
		campaing.setDialPrefix(String.valueOf(request.get("dial_prefix")));
		campaing.setAssignmentType(String.valueOf(request.get("assignmentType")));
		campaing.setLocalCallTime(String.valueOf(request.get("local_call_time")));
		campaing.setDialTimeout(String.valueOf(request.get("dial_timeout")));
		campaing.setManualDialPrefix(String.valueOf(request.get("manual_dial_prefix")));
		campaing.setAdditionalField(additionalFields);
		campaingRepository.save(campaing);
		if(request.containsKey("status")) {
			Status status= new Status();
			status.setCampaingName(campaing.getName());
			status.setStatus(String.valueOf(request.get("status")));
			statusRepository.save(status);
		}
	}
	
	public Map<String, String> fetchCampaingByUserName(String username){
		Map<String,String> campaingMap= new HashMap<String,String>();
		 campaingMap.put("campaing", String.join(",", campaingRepository.findCampaingByUserName(username)));
		 return campaingMap;
	}

	public Map<String, String> updateCampaing(Map<String, Object> request) {
		
		Campaing campaing = campaingRepository.findById(Integer.parseInt(String.valueOf(request.get("id")))).get();
		Gson gson = new Gson();
		String additionalFields=gson.toJson(request.get("additionalFields"));
		campaing.setActive(String.valueOf(request.get("active")));
		campaing.setName(String.valueOf(request.get("name")));
		campaing.setDialPrefix(String.valueOf(request.get("dial_prefix")));
		campaing.setLocalCallTime(String.valueOf(request.get("local_call_time")));
		campaing.setAssignmentType(String.valueOf(request.get("assignmentType")));
		campaing.setDialTimeout(String.valueOf(request.get("dial_timeout")));
		campaing.setManualDialPrefix(String.valueOf(request.get("manual_dial_prefix")));
		campaing.setAdditionalField(additionalFields);
		campaingRepository.save(campaing);
		Map<String, String> response = new HashMap<String, String>();
		response.put("status", "true");
		return response;

	}

	public void campaingGroupMapping(Map<String, String> request) {
		GroupCampaingMapping groupCampaingMapping = new GroupCampaingMapping();
		groupCampaingMapping.setCampaingname(request.get("campaingname"));
		groupCampaingMapping.setGroupname(request.get("groupname"));
		groupCampaingMappingRepository.save(groupCampaingMapping);
	}

	public void updateCampaingGroupMapping(Map<String, String> request) {
		System.out.println(request);
		GroupCampaingMapping groupCampaingMapping = groupCampaingMappingRepository
				.findGroupByCampaingName(request.get("campaingname"));
		groupCampaingMapping.setCampaingname(request.get("campaingname"));
		groupCampaingMapping.setGroupname(request.get("groupname"));
		groupCampaingMappingRepository.save(groupCampaingMapping);
	}

	public void attachUserGroupToCampaing(Map<String, String> request) {
		Optional<UserGroup> userGroup = userGroupRepository.findById(Integer.parseInt(request.get("group_id")));
		if (userGroup.isPresent()) {
			UserGroup userGroupFound = userGroup.get();
			Optional<Campaing> campaing = campaingRepository.findById(Integer.parseInt(request.get("campaing_id")));
			if (campaing.isPresent()) {
				Campaing campaingFound = campaing.get();
				GroupCampaingMapping groupCampaingMapping = new GroupCampaingMapping();
				groupCampaingMapping.setCampaingname(campaingFound.getName());
				groupCampaingMapping.setGroupname(userGroupFound.getName());
				System.out.println(campaingFound.getName());
				System.out.println(userGroupFound.getName());
				groupCampaingMappingRepository.save(groupCampaingMapping);
			}
		}
	}
}
