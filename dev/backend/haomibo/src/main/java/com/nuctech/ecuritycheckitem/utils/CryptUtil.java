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
import org.springframework.beans.factory.annotation.Value;

import java.util.Random;


@Slf4j
public class CryptUtil {

    @Value("${client.public.key}")
    private static String CLIENT_PUBLIC_KEY;

    @Value("${aes.private.key}")
    private static String AES_PRIVATE_KEY;

    @Value("${server.private.key}")
    private static String SERVER_PRIVATE_KEY;

    /**
     * encrypt
     *
     * @param plainText
     * @return String
     */
    public static String encrypt(String plainText) {
/*        try {
            // 生成aes秘钥
            String aseKey = getRandomString(16);
            // rsa加密
            String encrypted = RSAUtil.encryptedDataOnJava(aseKey, CLIENT_PUBLIC_KEY);
            // aes加密
            String encryptedData = AESUtil.encrypt(plainText, aseKey);
            Map<String, String> map = new HashMap<>();
            map.put("encrypted", encrypted);
            map.put("requestData", encryptedData);
            // return map;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("返回数据进行解密出现异常：" + e.getMessage());
        }*/
        return plainText;
    }

    /**
     * decrypt
     *
     * @param cryptedText
     * @return String
     */
    public static String decrypt(String cryptedText) {
/*        if(cryptedText != null && !cryptedText.equals("")){
            Map<String,String> requestMap = new Gson().fromJson(cryptedText, new TypeToken<Map<String,String>>() {
            }.getType());
            // 密文
            String data = requestMap.get("requestData");
            // 加密的ase秘钥
            String encrypted = requestMap.get("encrypted");
            if(StringUtils.isEmpty(data) || StringUtils.isEmpty(encrypted)){
                return cryptedText;
            }else{
                String content = null ;
                String aesKey = null;
                try {
                    aesKey = RSAUtil.decryptDataOnJava(encrypted, SERVER_PRIVATE_KEY);
                }catch (Exception e){
                    return cryptedText;
                }
                try {
                    content  = AESUtil.decrypt(data, aesKey);
                }catch (Exception e){
                    return cryptedText;
                }
                if (StringUtils.isEmpty(content) || StringUtils.isEmpty(aesKey)){
                    return cryptedText;
                }
                return content;
            }
        }*/
        return cryptedText;
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

}
