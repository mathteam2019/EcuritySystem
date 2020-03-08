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

    public static Dictionary[] originalDictionaryList = {
        new Dictionary("active", "生效"),
            new Dictionary("inactive", "未生效"),
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
            if(dicationary.dataCode.equals(dataCode)) {
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
            if(dicationary.dataCode.equals(dataCode) && dicationary.dictionaryName.equals(dictionaryName)) {
                answer = dicationary.dataValue;
            }
        }
        return answer;
    }

}
