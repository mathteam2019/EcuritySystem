/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/15
 * @CreatedBy Sandy.
 * @FileName User.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.reusables;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Builder;

/**
 * This class is used for user's information in login response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder()
public class User {

    long id;
    String name;
    String category;
    String portrait;
}
