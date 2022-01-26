package com.praveen.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.BreakTypes;
import com.praveen.model.Campaing;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

public interface BreakTypesRepository extends JpaRepository<BreakTypes, Integer> {
	@Query(value="select * from break_types where campaing_name=:campaingName ", nativeQuery = true)
	 List<BreakTypes> findBreaksByCampaingName(@Param("campaingName") String campaingName);
	
}
