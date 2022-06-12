package com.memsource.business.configuration;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;


/**s
 * Configuration domain object.
 */
@Entity
@Data
public class Configuration {

    @Id
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 12)
    private String password;
}
