/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysUserGroupUserRepository）
 * 文件名：	SysUserGroupUserDetailRepository.java
 * 描述：	SysUserGroupUserRepository
 * 作者名：	Choe
 * 日期：	2020/1/1
 */

package com.nuctech.ecuritycheckitem.repositories;

import com.nuctech.ecuritycheckitem.models.db.SysUserGroupUserCategory;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroupUserDetail;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysUserGroupDataRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserGroupDataRangeRepository extends
        JpaRepository<SysUserGroupDataRange, Long>,
        PagingAndSortingRepository<SysUserGroupDataRange, Long>,
        QuerydslPredicateExecutor<SysUserGroupDataRange> {

}