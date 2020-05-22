/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（CryptUtil)
 * 文件名：	CryptUtil.java
 * 描述：	This class contains some useful utility methods.
 * 作者名：	Choe
 * 日期：	2020/01/05
 *
 */
package com.nuctech.ecuritycheckitem.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Random;


@Slf4j
public class CryptUtil {



    private static String baseString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._";
    private static int key = 11;

    private static String getXorString(String plainText) {
        String changeStr = "";
        int length = plainText.length();
        for(int i = 0; i < length; i ++) {
            int index = 0;
            for(int j = 0; j < baseString.length(); j ++) {
                if(baseString.charAt(j) == plainText.charAt(i)) {
                    index = j;
                }
            }
            index = index ^ key;
            changeStr = changeStr + baseString.charAt(index);
        }
        return changeStr;
    }

    private static String getPermutation(String preAnswer) {
        int length = preAnswer.length();
        String answer = "";
        for(int i = 0; i < length; i += 2) {
            char first = preAnswer.charAt(i);
            int j = i + 1;
            if(j == length) {
                answer = answer + first;
                break;
            }
            char second = preAnswer.charAt(j);
            answer = answer + second + first;
        }
        return answer;
    }

    /**
     * encrypt
     *
     * @param plainText
     * @return String
     */
    public static String encrypt(String token, String plainText) {
        String changeStr = getXorString(plainText);
        String preAnswer = token;
        preAnswer = preAnswer + changeStr;
        preAnswer = StringUtils.reverse(preAnswer);
        String answer = getPermutation(preAnswer);

        return answer;
    }

    /**
     * decrypt
     *
     * @param cryptedText
     * @return String
     */
    public static String decrypt(String token, String cryptedText) {
        String original = getPermutation(cryptedText);
        original = StringUtils.reverse(original);
        String realStr = original.substring(token.length());
        String answer = getXorString(realStr);
        return answer;
    }

    /**
     * 创建指定位数的随机字符串
     *
     * @param length 表示生成字符串的长度
     * @return 字符串
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String encode(CharSequence rawPassword) {
        return rawPassword.toString();
        //return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt(4));
    }

    public static boolean matches(String encodedPassword, String originalString) {
        String sha256hex = DigestUtils.sha256Hex(originalString);
        return sha256hex.equals(encodedPassword);
    }

}
