/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysAuditLogDetailRepository）
 * 文件名：	SysAuditLogDetailRepository.java
 * 描述：	SysAuditLogDetailRepository
 * 作者名：	Choe
 * 日期：	2020/01/19
 */


package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;
import com.nuctech.ecuritycheckitem.models.db.SysAuditLogDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysAuditLogDetailRepository extends
        JpaRepository<SysAuditLogDetail, Long>,
        PagingAndSortingRepository<SysAuditLogDetail, Long>,
        QuerydslPredicateExecutor<SysAuditLogDetail> {

}