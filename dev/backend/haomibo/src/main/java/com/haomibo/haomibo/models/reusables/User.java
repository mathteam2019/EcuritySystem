package com.haomibo.haomibo.models.reusables;

import lombok.*;

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
