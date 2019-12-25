/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/24
 * @CreatedBy Sandy.
 * @FileName FilteringAndPaginationResult.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.reusables;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Builder;

/**
 * this class is used for filtering and pagination result which is used in datatable.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder()
public class FilteringAndPaginationResult {

    @JsonProperty("total")
    long total;

    @JsonProperty("per_page")
    int perPage;

    @JsonProperty("current_page")
    int currentPage;

    @JsonProperty("last_page")
    int lastPage;

    @JsonProperty("from")
    int from;

    @JsonProperty("to")
    int to;

    Object data; // will hold list<>;

}
