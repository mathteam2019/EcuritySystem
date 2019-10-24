package com.haomibo.haomibo.models.reusables;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
