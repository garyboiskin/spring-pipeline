package com.example.springpipeline.repository;

import com.example.springpipeline.entities.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unused")
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
