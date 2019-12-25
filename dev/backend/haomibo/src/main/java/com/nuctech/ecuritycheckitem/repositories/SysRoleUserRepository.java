/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysRoleUserRepository）
 * 文件名：	SysRoleUserRepository.java
 * 描述：	SysRoleUserRepository
 * 作者名：	Sandy
 * 日期：	2019/11/01
 */

package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysRoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleUserRepository extends
        JpaRepository<SysRoleUser, Long>,
        PagingAndSortingRepository<SysRoleUser, Long>,
        QuerydslPredicateExecutor<SysRoleUser> {

}