package com.haomibo.haomibo.models.request;

import com.haomibo.haomibo.models.db.SysOrg;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeleteOrgRequestBody {

    @NotNull
    Long orgId;

}
