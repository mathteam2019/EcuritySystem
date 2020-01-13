/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2020。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DictionaryService）
 * 文件名：	DictionaryService.java
 * 描述：	DictionaryService interface
 * 作者名：	Choe
 * 日期：	2020/01/10
 */

package com.nuctech.ecuritycheckitem.service.settingmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerSeizedGood;
import com.nuctech.ecuritycheckitem.models.db.SysDictionary;
import com.nuctech.ecuritycheckitem.models.db.SysDictionaryData;
import com.nuctech.ecuritycheckitem.utils.PageResult;

public interface DictionaryService {

    boolean checkDictionary(String dictionaryName, Long dictionaryId);

    boolean checkDictionaryData(String dataValue, Long dataId);

    boolean checkDictionaryDataCode(String dataCode, Long dataId);

    void createDictionary(SysDictionary sysDictionary);

    void createDictionaryData(SysDictionaryData sysDictionaryData);

    void modifyDictionary(SysDictionary sysDictionary);

    void modifyDictionaryData(SysDictionaryData sysDictionaryData);

    void removeDictionary(Long dictionaryId);

    void removeDictionaryData(Long dataId);

    boolean checkDictionaryExist(Long dictionaryId);

    boolean checkDictionaryDataExist(Long dataId);

    boolean checkDictionaryChildExist(Long dictionaryId);

    PageResult<SysDictionary> getDictionaryListByFilter(String sortBy, String order, String dictionaryName, int currentPage, int perPage);

    PageResult<SysDictionaryData> getDictionaryDataListByFilter(String sortBy, String order, String dataCode, String dataValue, Long dictionaryId,
                                                            int currentPage, int perPage);

}
