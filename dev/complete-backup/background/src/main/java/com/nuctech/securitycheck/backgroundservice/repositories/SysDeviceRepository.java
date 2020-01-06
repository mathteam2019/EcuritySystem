package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDevice;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceStatusType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceWorkStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SysDeviceRepository
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-29
 */
@Repository
public interface SysDeviceRepository extends JpaRepository<SysDevice, Long> {

    @Query(value = "select * from sys_device where DEVICE_TYPE = ?1 and STATUS in ?2", nativeQuery = true)
    List<SysDevice> getLoginedDeviceList(@Param("deviceType") String deviceType, @Param("statusList") String[] statusList);

    @Query(value = "select * from sys_device e WHERE e.GUID = ?1", nativeQuery = true)
    SysDevice getDeviceByGuid(String guid);

    @Query(value = "SELECT * from sys_device e where e.DEVICE_TYPE =:deviceType ", nativeQuery = true)
    List<SysDevice> getManualDeviceList(@Param("deviceType") String deviceType);

    @Query(value = "SELECT * from sys_device e where e.CURRENT_STATUS in:curStatus and e.WORK_STATUS = :workStatus ", nativeQuery = true)
    List<SysDevice> getFreeManualDeviceList(@Param("curStatus") DeviceStatusType[] curStatus, @Param("workStatus") DeviceWorkStatusType workStatus);

}
