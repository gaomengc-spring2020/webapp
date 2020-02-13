package com.mengchen.webapp.repository;

import com.mengchen.webapp.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {

}
