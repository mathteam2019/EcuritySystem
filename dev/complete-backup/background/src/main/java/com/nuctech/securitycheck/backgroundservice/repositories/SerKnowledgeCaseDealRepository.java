/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerKnowledgeCaseDealRepository）
 * 文件名：	SerKnowledgeCaseDealRepository.java
 * 描述：	SerKnowledgeCaseDealRepository
 * 作者名：	Choe
 * 日期：	2019/11/26
 */

package com.nuctech.securitycheck.backgroundservice.repositories;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerKnowledgeCaseDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerKnowledgeCaseDealRepository extends
        JpaRepository<SerKnowledgeCaseDeal, Long> {

}