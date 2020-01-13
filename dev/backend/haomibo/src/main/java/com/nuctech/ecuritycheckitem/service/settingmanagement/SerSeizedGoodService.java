/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2020。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerSeizedGoodService）
 * 文件名：	SerSeizedGoodService.java
 * 描述：	SerSeizedGoodService interface
 * 作者名：	Choe
 * 日期：	2020/01/09
 */

package com.nuctech.ecuritycheckitem.service.settingmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerScanParam;
import com.nuctech.ecuritycheckitem.models.db.SerSeizedGood;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface SerSeizedGoodService {
    boolean checkGood(String seizedGood, Long goodId);

    void createGood(SerSeizedGood serSeizedGood);

    void modifyGood(SerSeizedGood serSeizedGood);

    void removeGood(Long goodsId);

    boolean checkSeizedExist(Long goodsId);

    PageResult<SerSeizedGood> getGoodsListByFilter(String sortBy, String order, String goods, int currentPage, int perPage);

}
