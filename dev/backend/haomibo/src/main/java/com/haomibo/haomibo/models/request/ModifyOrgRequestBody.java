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
public class ModifyOrgRequestBody {

    @NotNull
    Long orgId;

    @NotNull
    String orgName;

    @NotNull
    String orgNumber;

    @NotNull
    Long parentOrgId;

    String leader;

    String mobile;

    String note;

    public SysOrg convert2SysOrg() {

        SysOrg sysOrg = new SysOrg();

        sysOrg.setOrgId(this.getOrgId());
        sysOrg.setOrgName(this.getOrgName());
        sysOrg.setOrgNumber(this.getOrgNumber());
        sysOrg.setParentOrgId(this.getParentOrgId());
        sysOrg.setLeader(Optional.of(this.getLeader()).orElse(""));
        sysOrg.setMobile(Optional.of(this.getMobile()).orElse(""));
        sysOrg.setStatus(SysOrg.Status.INACTIVE);
        sysOrg.setNote(Optional.of(this.getNote()).orElse(""));

        return sysOrg;
    }

}
