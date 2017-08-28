package com.test.fileUpload.repository;

import com.test.fileUpload.entity.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Long>{

}
