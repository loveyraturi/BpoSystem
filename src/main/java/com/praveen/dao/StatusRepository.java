package com.praveen.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.praveen.model.Status;
import com.praveen.model.Users;

@Repository
@Transactional
public interface StatusRepository extends JpaRepository<Status, Integer> {
	@Query(value="SELECT * FROM status where campaing_name=:campaingName LIMIT 1", nativeQuery = true)
	Status findStatusCampaingName(@Param("campaingName") String campaingName);
}
