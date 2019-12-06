/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/12/7
 * @CreatedBy Sandy.
 * @FileName Constants.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.config;

import java.util.Map;

/**
 * Defines constants for this project.
 */

class Dictionary {
    String dataCode;
    String dataValue;
    Dictionary(String dataCode, String dataValue) {
        this.dataCode = dataCode;
        this.dataValue = dataValue;
    }
}
public class ConstantDictionary {

    private static Dictionary[] dictionaryList = {
        new Dictionary("active", "生效"),
            new Dictionary("inactive", "未生效"),
            new Dictionary("male", "男"),
            new Dictionary("female", "女"),
            new Dictionary("1000001301", "安检仪+(本地手检)"),
            new Dictionary("1000001302", "安检仪+手检端"),
            new Dictionary("1000001303", "安检仪+审图端"),
            new Dictionary("1000001304", "安检仪+审图端+手检端"),
            new Dictionary("noseizure", "无查获"),
            new Dictionary("seized", "有查获"),
            new Dictionary("doubt", "有嫌疑"),
            new Dictionary("nodoubt", "无嫌疑"),
            new Dictionary("pending_dispatch", "待分派"),
            new Dictionary("pending_review", "待审图"),
            new Dictionary("while_review", "审图中"),
            new Dictionary("pending_inspection", "待查验"),
            new Dictionary("while_inspection", "查验中"),

    };

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

}
