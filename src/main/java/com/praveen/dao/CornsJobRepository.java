package com.praveen.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.praveen.model.Corns;


import javax.transaction.Transactional;

@Repository
@Transactional
public interface CornsJobRepository extends JpaRepository<Corns, Integer> {
	
}
