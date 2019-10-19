package com.haomibo.haomibo.models.request;

import com.haomibo.haomibo.models.db.SysOrg;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateOrgStatusRequestBody {

    @NotNull
    Long orgId;

    @NotNull
    @Pattern(regexp = SysOrg.Status.ACTIVE + "|" + SysOrg.Status.INACTIVE)
    String status;

}
