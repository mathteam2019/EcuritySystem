package com.haomibo.haomibo.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haomibo.haomibo.models.db.SysOrg;
import com.haomibo.haomibo.models.db.SysUser;
import lombok.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewOrgRequestBody {

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
