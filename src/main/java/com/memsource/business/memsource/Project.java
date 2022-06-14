package com.memsource.business.memsource;

import java.util.Set;

import lombok.Data;


/**
 * Memsource project.
 */
@Data
public class Project {

    private String uid;

    private Integer internalId;

    private String id;

    private String name;

    private String sourceLang;

    private Set<String> targetLangs;

    private String status;
}
