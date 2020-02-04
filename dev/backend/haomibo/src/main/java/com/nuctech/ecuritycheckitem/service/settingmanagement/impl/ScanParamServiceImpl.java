/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ScanParamServiceImpl）
 * 文件名：	ScanParamServiceImpl.java
 * 描述：	ScanParamService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.settingmanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerScanParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SerScanParamsFromRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.ScanParamService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ScanParamServiceImpl implements ScanParamService {

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SerScanParamRepository serScanParamRepository;

    @Autowired
    SerScanParamsFromRepository serScanParamsFromRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.ENGLISH;

    public String getJsonFromScanParam(SerScanParam scanParam) {
        SerScanParam newParam = SerScanParam.builder()
                .scanParamsId(scanParam.getScanParamsId())
                .deviceId(scanParam.getDeviceId())
                .airCaliWarnTime(scanParam.getAirCaliWarnTime())
                .standByTime(scanParam.getStandByTime())
                .alarmSound(scanParam.getAlarmSound())
                .passSound(scanParam.getPassSound())
                .posErrorSound(scanParam.getPosErrorSound())
                .standSound(scanParam.getStandSound())
                .scanSound(scanParam.getScanSound())
                .scanOverUseSound(scanParam.getScanOverUseSound())
                .autoRecognise(scanParam.getAutoRecognise())
                .recognitionRate(scanParam.getRecognitionRate())
                .saveScanData(scanParam.getSaveScanData())
                .saveSuspectData(scanParam.getSaveSuspectData())
                .facialBlurring(scanParam.getFacialBlurring())
                .chestBlurring(scanParam.getChestBlurring())
                .hipBlurring(scanParam.getHipBlurring())
                .groinBlurring(scanParam.getGroinBlurring())
                .autoConfig(scanParam.getAutoConfig())
                .dictionaryName(scanParam.getDictionaryName())
                .deviceStorageAlarm(scanParam.getDeviceStorageAlarm())
                .deviceStorageAlarmPercent(scanParam.getDeviceStorageAlarmPercent())
                .status(scanParam.getStatus())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newParam);
        } catch(Exception ex) {
        }
        return answer;
    }

    /**
     * @param deviceName
     * @param status
     * @return
     */
    BooleanBuilder getPredicate(String deviceName, String status) {
        QSerScanParam builder = QSerScanParam.serScanParam;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(deviceName) && !deviceName.isEmpty()) {
            predicate.and(builder.device.deviceName.contains(deviceName));
        }
        predicate.and(builder.device.status.eq(SysDevice.Status.ACTIVE));
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.status.eq(status));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }

        return predicate;

    }

    /**
     * @param paramId
     * @return
     */
    public SerScanParam getById(Long paramId) {

        Optional<SerScanParam> optionalSerScanParam = serScanParamRepository.findOne(QSerScanParam.
                serScanParam.scanParamsId.eq(paramId));
        if (!optionalSerScanParam.isPresent()) {
            return null;
        }

        SerScanParam serScanParam = optionalSerScanParam.get();

        return serScanParam;
    }

    /**
     * update Status of scan param
     * @param paramId
     * @return
     */
    @Override
    @Transactional
    public void updateStatus(Long paramId, String status) {
        Optional<SerScanParam> optionalSerScanParam = serScanParamRepository.findOne(QSerScanParam.
                serScanParam.scanParamsId.eq(paramId));
        SerScanParam serScanParam = optionalSerScanParam.get();
        String valueBefore = getJsonFromScanParam(serScanParam);
        serScanParam.setStatus(status);
        serScanParam.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        serScanParamRepository.save(serScanParam);
        String valueAfter = getJsonFromScanParam(serScanParam);
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("ScanParam", null, currentLocale), "", String.valueOf(paramId), null, true, valueBefore, valueAfter);
    }

    /**
     * @param deviceName
     * @param status
     * @param currentPage
     * @param perPage
     * @return
     */
    public PageResult<SerScanParam> getScanParamListByFilter(String sortBy, String order, String deviceName, String status, Integer currentPage, Integer perPage) {

        BooleanBuilder predicate = getPredicate(deviceName, status);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sortBy = "device.deviceSerial";
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }
        long total = serScanParamRepository.count(predicate);
        List<SerScanParam> data = serScanParamRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<SerScanParam>(total, data);

    }

    /**
     * @param deviceName
     * @param status
     * @return
     */
    public List<SerScanParam> getAllWithFilter(String deviceName, String status) {

        BooleanBuilder predicate = getPredicate(deviceName, status);

        List<SerScanParam> data = StreamSupport
                .stream(serScanParamRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        return data;

    }

    /**
     * get all records of ser_scan_param table
     *
     * @return
     */
    @Override
    public List<SerScanParam> getAllWithoutFilter() {

        return serScanParamRepository.findAll();

    }

    /**
     * modify scan param
     * @param paramDeviceIdList
     * @param serScanParamNew
     * @return
     */
    @Override
    public boolean modifyScanParam(List<Long> paramDeviceIdList, SerScanParam serScanParamNew) {

        SerScanParam serScanParam = serScanParamRepository.findOne(QSerScanParam.serScanParam
                .scanParamsId.eq(serScanParamNew.getScanParamsId())).orElse(null);
        String valueBefore = getJsonFromScanParam(serScanParam);

        //check if ser scan param is valid.
        if (serScanParam == null) {
            return false;
        }

        List<SerScanParamsFrom> fromParams = serScanParam.getFromParamsList();
        //check from params exist or not
        if (fromParams != null) {
            serScanParamsFromRepository.deleteAll(fromParams);
        }
        // Add edited info.
        serScanParamNew.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        serScanParamNew.setStatus(serScanParam.getStatus());
        serScanParamNew.setCreatedBy(serScanParam.getCreatedBy());
        serScanParamNew.setCreatedTime(serScanParam.getCreatedTime());
        serScanParamNew.setDeviceId(serScanParam.getDeviceId());

        serScanParamRepository.save(serScanParamNew);

        if (paramDeviceIdList != null && paramDeviceIdList.size() > 0) {
            List<SerScanParamsFrom> serScanParamsFromList = new ArrayList<>();
            List<SerScanParam> serScanParamList = StreamSupport
                    .stream(serScanParamRepository.findAll(QSerScanParam.serScanParam
                            .deviceId.in(paramDeviceIdList)).spliterator(), false)
                    .collect(Collectors.toList());
            for (int i = 0; i < paramDeviceIdList.size(); i++) {
                boolean isExist = false;
                SerScanParam scanParam = null;
                for (int j = 0; j < serScanParamList.size(); j++) {
                    if (serScanParamList.get(j).getDeviceId() == paramDeviceIdList.get(i)) {
                        scanParam = serScanParamList.get(j);
                        scanParam.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                        isExist = true;
                        break;
                    }
                }
                if (isExist == false) {
                    scanParam = SerScanParam.builder().deviceId(paramDeviceIdList.get(i)).build();
                    scanParam.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                    serScanParamList.add(scanParam);
                }
                scanParam.setAirCaliWarnTime(serScanParamNew.getAirCaliWarnTime());
                scanParam.setStandByTime(serScanParamNew.getStandByTime());
                scanParam.setAlarmSound(serScanParamNew.getAlarmSound());
                scanParam.setPassSound(serScanParamNew.getPassSound());
                scanParam.setPosErrorSound(serScanParamNew.getPosErrorSound());
                scanParam.setStandSound(serScanParamNew.getStandSound());
                scanParam.setScanSound(serScanParamNew.getScanSound());
                scanParam.setScanOverUseSound(serScanParamNew.getScanOverUseSound());
                scanParam.setAutoRecognise(serScanParamNew.getAutoRecognise());
                scanParam.setRecognitionRate(serScanParamNew.getRecognitionRate());
                scanParam.setSaveScanData(serScanParamNew.getSaveScanData());
                scanParam.setSaveSuspectData(serScanParamNew.getSaveSuspectData());
                scanParam.setFacialBlurring(serScanParamNew.getFacialBlurring());
                scanParam.setChestBlurring(serScanParamNew.getChestBlurring());
                scanParam.setHipBlurring(serScanParamNew.getHipBlurring());
                scanParam.setGroinBlurring(serScanParamNew.getGroinBlurring());
                scanParam.setDeviceStorageAlarm(serScanParamNew.getDeviceStorageAlarm());
                scanParam.setDeviceStorageAlarmPercent(serScanParamNew.getDeviceStorageAlarmPercent());
            }
            for (int i = 0; i < paramDeviceIdList.size(); i++) {
                Long deviceId = paramDeviceIdList.get(i);
                SerScanParamsFrom paramsFrom = SerScanParamsFrom.builder()
                        .deviceId(deviceId)
                        .fromDeviceId(serScanParam.getDeviceId())
                        .scanParamsId(serScanParam.getScanParamsId())
                        .build();
                paramsFrom.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                serScanParamsFromList.add(paramsFrom);
            }
            serScanParamRepository.saveAll(serScanParamList);
            serScanParamsFromRepository.saveAll(serScanParamsFromList);
        }
        String valueAfter = getJsonFromScanParam(serScanParam);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("ScanParam", null, currentLocale), "", serScanParam.getScanParamsId().toString(), null, true, valueBefore, valueAfter);

        return true;
    }

}
