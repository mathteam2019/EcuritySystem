package com.haomibo.haomibo.models.response;

import com.haomibo.haomibo.models.db.SysOrg;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder()
public class GetOrgByFilterAndPageResponseBody {

    long total;
    int perPage;
    int currentPage;
    int lastPage;
    int from;
    int to;

    List<SysOrg> data;
}
