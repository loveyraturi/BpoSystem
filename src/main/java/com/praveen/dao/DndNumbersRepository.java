package com.praveen.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Break;
import com.praveen.model.BreakTypes;
import com.praveen.model.Campaing;
import com.praveen.model.DndNumbers;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

public interface DndNumbersRepository extends JpaRepository<DndNumbers, Integer> {
	@Query(value="select * from dnd_numbers where phone_number=:phoneNumber LIMIT 1 ", nativeQuery = true)
	DndNumbers findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
