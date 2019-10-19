package com.haomibo.haomibo.models.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetOrgByFilterAndPageRequestBody {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        String orgName;
    }

    int currentPage;
    int perPage;

    Filter filter;


}
