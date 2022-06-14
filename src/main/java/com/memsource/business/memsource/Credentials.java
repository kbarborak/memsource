package com.memsource.business.memsource;

import com.memsource.business.configuration.Configuration;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Memsource API credentials.
 */
@Data
@AllArgsConstructor
public class Credentials {

    private String userName;

    private String password;

    public Credentials(final Configuration configuration) {
        this.userName = configuration.getUsername();
        this.password = configuration.getPassword();
    }
}
