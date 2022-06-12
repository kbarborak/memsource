package com.memsource.business.configuration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Configuration management repository.
 */
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, String> {

}
