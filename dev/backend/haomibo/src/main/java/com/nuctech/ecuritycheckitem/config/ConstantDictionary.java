/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（static constant dictionary values）
 * 文件名：	ConstantDictionary.java
 * 描述：	Static dictionary values used in System
 * 作者名：	Sandy
 * 日期：	2019/12/07
 *
 */

package com.nuctech.ecuritycheckitem.config;

import org.apache.commons.lang3.StringUtils;

public class ConstantDictionary {
    public static class Dictionary {
        String dataCode; //data code
        String dataValue; //data value of code
        String dictionaryName; //dictionary name
        public Dictionary(String dataCode, String dataValue) {
            this.dataCode = dataCode;
            this.dataValue = dataValue;
        }

        public Dictionary(String dataCode, String dataValue, String dictionaryName) {
            this.dataCode = dataCode;
            this.dataValue = dataValue;
            this.dictionaryName = dictionaryName;
        }
    }

    private static Dictionary[] dictionaryList = {};

    public static Dictionary[] originalChineseDictionaryList = {
        new Dictionary("active", "生效"),
            new Dictionary("inactive", "未效"),
            new Dictionary("male", "男"),
            new Dictionary("female", "女"),
            new Dictionary("1000001301", "安检仪+(本地手检)"),
            new Dictionary("1000001302", "安检仪+手检端"),
            new Dictionary("1000001303", "安检仪+审图端"),
            new Dictionary("1000001304", "安检仪+审图端+手检端"),
            new Dictionary("1000000501", "个人数据"),
            new Dictionary("1000000502", "隶属机构所有人数"),
            new Dictionary("1000000503", "隶属机构及下级机构所有人"),
            new Dictionary("1000000504", "所有人数据"),
            new Dictionary("1000000505", "自定义数据组"),
            new Dictionary("1000000506", "组内所有人数据"),
            new Dictionary("1000000901", "同方"),
            new Dictionary("1000000902", "威视"),
            new Dictionary("FALSE", "无查获"),
            new Dictionary("TRUE", "有查获"),
            new Dictionary("doubt", "有嫌疑"),
            new Dictionary("nodoubt", "无嫌疑"),
            new Dictionary("pending_dispatch", "待分派"),
            new Dictionary("pending_review", "待审图"),
            new Dictionary("while_review", "审图中"),
            new Dictionary("pending_inspection", "待查验"),
            new Dictionary("while_inspection", "查验中"),
            new Dictionary("0", "失败"),
            new Dictionary("1", "成功"),
            new Dictionary("0", "安检仪", "DeviceLogCategory"),
            new Dictionary("1", "审图站", "DeviceLogCategory"),
            new Dictionary("2", "手检站", "DeviceLogCategory"),
            new Dictionary("0", "调试", "DeviceLogLevel"),
            new Dictionary("1", "信息", "DeviceLogLevel"),
            new Dictionary("2", "警告", "DeviceLogLevel"),
            new Dictionary("3", "错误", "DeviceLogLevel"),
            new Dictionary("4", "致命", "DeviceLogLevel"),
            new Dictionary("1000001901", "扫描累计工时", "UserWorking"),
            new Dictionary("1000001902", "判图累计工时", "UserWorking"),
            new Dictionary("1000001903", "手检累计工时", "UserWorking"),
            new Dictionary("1000001904", "累计", "UserWorking"),
            new Dictionary("1000001901", "安检仪累计", "DeviceWorking"),
            new Dictionary("1000001902", "判图站累计", "DeviceWorking"),
            new Dictionary("1000001903", "手检站累计", "DeviceWorking"),
            new Dictionary("1000001904", "累计", "DeviceWorking"),
    };


    public static Dictionary[] originalEnglishDictionaryList = {
            new Dictionary("active", "Active"),
            new Dictionary("inactive", "Inactive"),
            new Dictionary("male", "Male"),
            new Dictionary("female", "Female"),
            new Dictionary("1000001301", "安检仪+(本地手检)"),
            new Dictionary("1000001302", "安检仪+手检端"),
            new Dictionary("1000001303", "安检仪+审图端"),
            new Dictionary("1000001304", "安检仪+审图端+手检端"),
            new Dictionary("1000000501", "个人数据"),
            new Dictionary("1000000502", "隶属机构所有人数"),
            new Dictionary("1000000503", "隶属机构及下级机构所有人"),
            new Dictionary("1000000504", "所有人数据"),
            new Dictionary("1000000505", "自定义数据组"),
            new Dictionary("1000000506", "组内所有人数据"),
            new Dictionary("1000000901", "同方"),
            new Dictionary("1000000902", "威视"),

            new Dictionary("FALSE", "Not Seized"),
            new Dictionary("TRUE", "Seized"),
            new Dictionary("doubt", "Suspect"),
            new Dictionary("nodoubt", "No Suspect"),
            new Dictionary("pending_dispatch", "Pending Dispatch"),
            new Dictionary("pending_review", "Pending Review"),
            new Dictionary("while_review", "Judging"),
            new Dictionary("pending_inspection", "Pending Inspection"),
            new Dictionary("while_inspection", "Inspecting"),
            new Dictionary("0", "Failure"),
            new Dictionary("1", "Success"),
            new Dictionary("0", "Manual", "DeviceLogCategory"),
            new Dictionary("1", "Judge", "DeviceLogCategory"),
            new Dictionary("2", "Hand", "DeviceLogCategory"),
            new Dictionary("0", "Debug", "DeviceLogLevel"),
            new Dictionary("1", "Info", "DeviceLogLevel"),
            new Dictionary("2", "Warn", "DeviceLogLevel"),
            new Dictionary("3", "Error", "DeviceLogLevel"),
            new Dictionary("4", "Fatal", "DeviceLogLevel"),
            new Dictionary("1000001901", "Scan working time", "UserWorking"),
            new Dictionary("1000001902", "Judge working time", "UserWorking"),
            new Dictionary("1000001903", "Hand working time", "UserWorking"),
            new Dictionary("1000001904", "Total", "UserWorking"),
            new Dictionary("1000001901", "Scan working time", "DeviceWorking"),
            new Dictionary("1000001902", "Judge working time", "DeviceWorking"),
            new Dictionary("1000001903", "Hand working time", "DeviceWorking"),
            new Dictionary("1000001904", "Total", "DeviceWorking"),
    };

    public static void setDictionaryList(Dictionary[] newDictionaryList) {
        dictionaryList = newDictionaryList;
    }

    /**
     * get data value from data code
     * @param dataCode
     * @return
     */
    public static String getDataValue(String dataCode) {
        String answer = "";
        for(int i = 0; i < dictionaryList.length; i ++) {
            Dictionary dicationary = dictionaryList[i];
            if(dicationary.dataCode.equals(dataCode) && StringUtils.isEmpty(dicationary.dictionaryName)) {
                answer = dicationary.dataValue;
            }
        }
        return answer;
    }

    /**
     * get data value from data code and dictionary name
     * @param dataCode
     * @param dictionaryName
     * @return
     */
    public static String getDataValue(String dataCode, String dictionaryName) {
        String answer = "";
        for(int i = 0; i < dictionaryList.length; i ++) {
            Dictionary dicationary = dictionaryList[i];
            if(dicationary.dataCode.equals(dataCode) && dictionaryName.equals(dicationary.dictionaryName)) {
                answer = dicationary.dataValue;
            }
        }
        return answer;
    }

}
