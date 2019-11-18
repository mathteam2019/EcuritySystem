/*==============================================================*/
/* Database name:  DATABASE_1                                   */
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019/10/17 22:36:09                          */
/*==============================================================*/


/*drop database if exists SECURITY;*/

/*==============================================================*/
/* Database: DATABASE_1                                         */
/*==============================================================*/
/*create database SECURITY;*/

use SECURITY;

/*==============================================================*/
/* Table: SER_ARCHIVES                                          */
/*==============================================================*/
create table SER_ARCHIVES
(
   ARCHIVE_ID           bigint not null comment '档案ID',
   ARCHIVES_TEMPLATE_ID bigint comment '档案模板ID',
   ARCHIVES_NAME        varchar(50) comment '档案名称',
   ARCHIVES_NUMBER      varchar(50) comment '档案编号',
   STATUS               varchar(10) comment '当前状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ARCHIVE_ID)
);

alter table SER_ARCHIVES comment '档案管理';

/*==============================================================*/
/* Table: SER_ARCHIVES_INDICATORS                               */
/*==============================================================*/
create table SER_ARCHIVES_INDICATORS
(
   INDICATORS_ID        bigint not null comment '指标ID',
   ARCHIVES_TEMPLATE_ID bigint comment '档案模板ID',
   INDICATORS_NAME      varchar(20) comment '指标名称',
   INDICATORS_UNIT      varchar(20) comment '指标单位',
   IS_NULL              varchar(10) comment '是否必填',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (INDICATORS_ID)
);

alter table SER_ARCHIVES_INDICATORS comment '模板指标';

/*==============================================================*/
/* Table: SER_ARCHIVES_TEMPLATE                                 */
/*==============================================================*/
create table SER_ARCHIVES_TEMPLATE
(
   ARCHIVES_TEMPLATE_ID bigint not null comment '档案模板ID',
   S_TEMPLATE_NAME      varchar(50) comment '档案模板名称',
   ARCHIVES_TEMPLATE_NUMBER varchar(50) comment '档案模板编号',
   MANUFACTURER         varchar(10) comment '生产厂商',
   ORIGINAL_MODEL       varchar(50) comment '原厂型号',
   STATUS               varchar(10) comment '当前状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ARCHIVES_TEMPLATE_ID)
);

alter table SER_ARCHIVES_TEMPLATE comment '档案模板';

/*==============================================================*/
/* Table: SER_ARCHIVES_VALUE                                    */
/*==============================================================*/
create table SER_ARCHIVES_VALUE
(
   INDICATORS_ID        bigint comment '指标ID',
   ARCHIVE_ID           bigint comment '档案ID',
   VALUE_ID             bigint,
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注'
);

alter table SER_ARCHIVES_VALUE comment '档案指标值';

/*==============================================================*/
/* Table: SER_CHECK_RESULT                                      */
/*==============================================================*/
create table SER_CHECK_RESULT
(
   CHECK_RESULT_ID      bigint not null comment '结果ID',
   DEVICE_ID            bigint comment '设备ID',
   TASK_ID              bigint not null comment '任务ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (CHECK_RESULT_ID)
);

alter table SER_CHECK_RESULT comment '查验结果';

/*==============================================================*/
/* Table: SER_DEVICE_REGISTER                                   */
/*==============================================================*/
create table SER_DEVICE_REGISTER
(
   REGISTER_ID          bigint not null comment '注册信息ID',
   DEVICE_ID            bigint comment '设备ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (REGISTER_ID)
);

alter table SER_DEVICE_REGISTER comment '设备注册信息';

/*==============================================================*/
/* Table: SER_IMAGE                                             */
/*==============================================================*/
create table SER_IMAGE
(
   IMAGE_ID             bigint not null comment '图像ID',
   TASK_ID              bigint not null comment '任务ID',
   INSPECTED_ID         bigint comment '被查验人ID',
   IMAGE_FORMAT         varchar(10) comment '图像格式(原始图，jpg...)',
   IMAGE_CATEGORY       varchar(10) comment '图像类别（对比度、反色等）',
   IMAGE_URL            varchar(200) comment '图像地址',
   IMAGE_LABEL          varchar(50) comment '图像标签',
   STATUS               varchar(10) comment '当前状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (IMAGE_ID)
);

alter table SER_IMAGE comment '图像';

/*==============================================================*/
/* Table: SER_IMAGE_PROPERTY                                    */
/*==============================================================*/
create table SER_IMAGE_PROPERTY
(
   IMAGE_PROPERTY_ID    bigint not null comment '属性ID',
   IMAGE_ID             bigint comment '图像ID',
   IMAGE_PROPERTY_NAME  varchar(50) comment '属性名称',
   IMAGE_PROPERTY_VALUE varchar(50) comment '属性值',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (IMAGE_PROPERTY_ID)
);

alter table SER_IMAGE_PROPERTY comment '图像属性';

/*==============================================================*/
/* Table: SER_INSPECTED                                         */
/*==============================================================*/
create table SER_INSPECTED
(
   INSPECTED_ID         bigint not null comment '被查验人ID',
   DEVICE_ID            bigint comment '设备ID',
   TASK_ID              bigint not null comment '任务ID',
   SUSPECT_NAME         varchar(50) comment '姓名',
   IDENTITY_CARD        varchar(50) comment '证件号',
   IDENTITY_CATEGORY    varchar(10) comment '证件类型',
   GENDER               varchar(10) comment '性别',
   CHECK_LEVE           varchar(10) comment '查验等级',
   ACTIVATE_STATUS      varchar(10) comment '激活状态',
   CHECK_RESULT         char(10) comment '查验结果',
   CHECK_DESC           varchar(500) comment '查验描述',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (INSPECTED_ID)
);

alter table SER_INSPECTED comment '被查验人';

/*==============================================================*/
/* Table: SER_KNOWLEDGE                                         */
/*==============================================================*/
create table SER_KNOWLEDGE
(
   UNI_MAN_BASE_KNOWLEDGE_ID bigint not null,
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (UNI_MAN_BASE_KNOWLEDGE_ID)
);

alter table SER_KNOWLEDGE comment '知识库';

/*==============================================================*/
/* Table: SER_KNOWLEDGE_PROPERTY                                */
/*==============================================================*/
create table SER_KNOWLEDGE_PROPERTY
(
   PROPERTY_ID          bigint not null comment '属性ID',
   UNI_MAN_BASE_KNOWLEDGE_ID bigint,
   TASK_ID              bigint comment '任务ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (PROPERTY_ID)
);

alter table SER_KNOWLEDGE_PROPERTY comment '知识库任务属性';

/*==============================================================*/
/* Table: SER_LOG                                               */
/*==============================================================*/
create table SER_LOG
(
   LOG_ID               bigint not null comment '日志ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (LOG_ID)
);

alter table SER_LOG comment '日志';

/*==============================================================*/
/* Table: SER_LOGIN_INFO                                        */
/*==============================================================*/
create table SER_LOGIN_INFO
(
   LOGIN_INFO_ID        bigint not null comment '登录信息ID',
   USER_ID              bigint comment '用户ID',
   LOGIN_CATEGORY       varchar(10) comment '登录类型',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (LOGIN_INFO_ID)
);

alter table SER_LOGIN_INFO comment '登录信息';

/*==============================================================*/
/* Table: SER_MAINTAIN                                          */
/*==============================================================*/
create table SER_MAINTAIN
(
   MAINTAIN_ID          bigint not null comment '维保记录ID',
   MAINTAIN_NUMBER      varchar(50) comment '维保项编号',
   MAINTAIN_NAME        varchar(50) comment '维保名称',
   MAINTAIN_DESC        varchar(500) comment '描述',
   DEVICE_TYPE          varchar(10) comment '设备类型',
   DEVICE_POSITION      varchar(50) comment '设备位置',
   MAINTAIN_PERIOD      varchar(50) comment '执行时间或周期',
   STATUS               varchar(10) comment '激活状态',
   MAINTAIN_CATEGORY    varchar(10) comment '维保类型',
   MAINTAINER_NAME      varchar(50) comment '维保人员姓名',
   MAINTAIN_TIME        timestamp comment '维保时间',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (MAINTAIN_ID)
);

alter table SER_MAINTAIN comment '维保记录';

/*==============================================================*/
/* Table: SER_MAINTAIN_ATTACHMENT                               */
/*==============================================================*/
create table SER_MAINTAIN_ATTACHMENT
(
   ATTACHMENT_ID        bigint not null comment '附件ID',
   MAINTAIN_ID          bigint comment '维保记录ID',
   ATTACHMENT_NAME      varchar(50) comment '附件名称',
   ATTACHEMTN_URL       varchar(200) comment '附件地址',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ATTACHMENT_ID)
);

alter table SER_MAINTAIN_ATTACHMENT comment '维保记录附件';

/*==============================================================*/
/* Table: SER_TASK                                              */
/*==============================================================*/
create table SER_TASK
(
   TASK_ID              bigint not null comment '任务ID',
   DEVICE_ID            bigint comment '设备ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (TASK_ID)
);

alter table SER_TASK comment '任务';

/*==============================================================*/
/* Table: SER_TASK_PROPERTY                                     */
/*==============================================================*/
create table SER_TASK_PROPERTY
(
   PROPERTY_ID          bigint not null comment '属性ID',
   TASK_ID              bigint comment '任务ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (PROPERTY_ID)
);

alter table SER_TASK_PROPERTY comment '任务属性';

/*==============================================================*/
/* Table: SER_TASK_STEP                                         */
/*==============================================================*/
create table SER_TASK_STEP
(
   STEP_ID              bigint not null comment '步骤ID',
   TASK_ID              bigint comment '任务ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (STEP_ID)
);

alter table SER_TASK_STEP comment '任务执行步骤';

/*==============================================================*/
/* Table: SER_TIMING_TASK                                       */
/*==============================================================*/
create table SER_TIMING_TASK
(
   TIMING_TASK_ID       bigint not null comment '定时任务记录ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (TIMING_TASK_ID)
);

alter table SER_TIMING_TASK comment '定时任务推送记录';

/*==============================================================*/
/* Table: SYS_ACCESS_ROLE                                       */
/*==============================================================*/
create table SYS_ACCESS_ROLE
(
   ACCESS_USER_ID       bigint not null comment '用户拥有访问权限ID',
   ACCESS_ID            bigint comment '访问权限ID',
   ROLE_ID              bigint comment '用户角色ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ACCESS_USER_ID)
);

alter table SYS_ACCESS_ROLE comment '用户拥有的权限';

/*==============================================================*/
/* Table: SYS_BLACKLIST                                         */
/*==============================================================*/
create table SYS_BLACKLIST
(
   BLACKLIST_ID         bigint not null comment '黑名单ID',
   SUSPECT_NAME         varchar(50) comment '姓名',
   IDENTITY_CARD        varchar(50) comment '证件号',
   IDENTITY_CATEGORY    varchar(10) comment '证件类型',
   GENDER               varchar(10) comment '性别',
   CHECK_LEVE           varchar(10) comment '查验等级',
   ACTIVATE_STATUS      varchar(10) comment '激活状态',
   INSPECTED_ID         bigint not null comment '被查验人ID',
   CHECK_DESC           varchar(500) comment '查验描述',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (BLACKLIST_ID)
);

alter table SYS_BLACKLIST comment '黑名单';

/*==============================================================*/
/* Table: SYS_DATA_GROUP                                        */
/*==============================================================*/
create table SYS_DATA_GROUP
(
   DATA_GROUP_ID        bigint not null comment '数据组ID',
   ORG_ID               bigint comment '机构ID',
   DATA_GROUP_NAME      varchar(50) comment '数据组名称',
   DATA_GROUP_FLAG      varchar(10) comment '数据组标记',
   DATA_GROUP_FLAG      varchar(10) comment '数据组标记',
   STATUS               varchar(10) comment '当前状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (DATA_GROUP_ID)
);

alter table SYS_DATA_GROUP comment '数据组';

/*==============================================================*/
/* Table: SYS_DATA_GROUP_USER                                   */
/*==============================================================*/
create table SYS_DATA_GROUP_USER
(
   DATA_GROUP_USER_ID   bigint not null comment '用户所属数据组ID',
   USER_ID              bigint comment '用户ID',
   DATA_GROUP_ID        bigint comment '数据组ID',
   DATA_FIXED_ID        int(11) DEFAULT '1' COMMENT '前3项固定值:1个人数据/2组内容/3所有人数据/',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (DATA_GROUP_USER_ID)
);

alter table SYS_DATA_GROUP_USER comment '用户所属数据组';

/*==============================================================*/
/* Table: SYS_DEVICE                                            */
/*==============================================================*/
create table SYS_DEVICE
(
   DEVICE_ID            bigint not null comment '设备ID',
   DEVICE_GROUP_ID      bigint comment '设备组主键',
   FIELD_ID             bigint comment '场地ID',
   ARCHIVE_ID           bigint comment '档案ID',
   DEVICE_NAME          varchar(50) comment '设备名称',
   DEVICE_TYPE          varchar(50) comment '设备型号',
   DEVICE_SERIAL        varchar(50) comment '设备编号',
   DEVICE_DESC          varchar(500) comment '设备描述',
   DEVICE_IP            varchar(20) comment '设备IP',
   DEVICE_PASSAGEWAY    varchar(50) comment '设备通道',
   ORIGINAL_FACTORY_NUMBER varchar(50) comment '原厂编号',
   MANUFACTURE_DATE     date comment '生产日期',
   PURCHASE_DATE        date comment '采购日期',
   STATUS               varchar(10) comment '当前状态',
   DEVICE_STRATEGY      varchar(10) comment '策略组别（存疑）',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (DEVICE_ID)
);

alter table SYS_DEVICE comment '设备端';

/*==============================================================*/
/* Table: SYS_DEVICE_GROUP                                      */
/*==============================================================*/
create table SYS_DEVICE_GROUP
(
   DEVICE_GROUP_ID      bigint not null comment '设备组主键',
   MODE_ID              bigint comment '工作模式ID',
   DEVICE_GROUP_NAME    varchar(50) comment '设备组名称',
   DEVICE_GROUP_CATEGORY varchar(10) comment '设备组类型',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (DEVICE_GROUP_ID)
);

alter table SYS_DEVICE_GROUP comment '设备分发组';

/*==============================================================*/
/* Table: SYS_DEVICE_USER                                       */
/*==============================================================*/
create table SYS_DEVICE_USER
(
   DEVICE_USER_ID       bigint not null comment '设备与用户关系ID',
   DEVICE_ID            bigint comment '设备ID',
   USER_ID              bigint comment '用户ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (DEVICE_USER_ID)
);

alter table SYS_DEVICE_USER comment '设备与用户关联关系';

/*==============================================================*/
/* Table: SYS_DICTIONARY                                        */
/*==============================================================*/
create table SYS_DICTIONARY
(
   DICTIONARY_ID        bigint not null comment '数据字典ID',
   DICTIONARY_NAME      varchar(50) comment '数据字典名称',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (DICTIONARY_ID)
);

alter table SYS_DICTIONARY comment '数据字典';

/*==============================================================*/
/* Table: SYS_DICTIONARY_DATA                                   */
/*==============================================================*/
create table SYS_DICTIONARY_DATA
(
   DATA_ID              bigint not null,
   DICTIONARY_ID        bigint comment '数据字典ID',
   DATA_CODE            varchar(10) comment '数据编码',
   DATA_VALUE           varchar(200) comment '数据值',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (DATA_ID)
);

alter table SYS_DICTIONARY_DATA comment '数据值';

/*==============================================================*/
/* Table: SYS_FIELD                                             */
/*==============================================================*/
create table SYS_FIELD
(
   FIELD_ID             bigint not null comment '场地ID',
   ORG_ID               bigint comment '机构ID',
   PARENT_FIELD_ID      bigint comment '场地ID',
   FIELD_SERIAL         varchar(50) comment '场地编号',
   FIELD_DESIGNATION    varchar(50) comment '场地名称',
   FIELD_STATUS         varchar(10) comment '场地状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (FIELD_ID)
);

alter table SYS_FIELD comment '场地';

/*==============================================================*/
/* Table: SYS_MANUAL_DEVICE                                     */
/*==============================================================*/
create table SYS_MANUAL_DEVICE
(
   MANUAL_DEVICE_ID     bigint not null comment '手检站ID',
   DEVICE_NAME          varchar(50) comment '设备名称',
   DEVICE_TYPE          varchar(50) comment '设备型号',
   DEVICE_SERIAL        varchar(50) comment '设备编号',
   DEVICE_DESC          varchar(500) comment '设备描述',
   DEVICE_IP            varchar(20) comment '设备IP',
   DEVICE_PASSAGEWAY    varchar(50) comment '设备通道',
   DEVICE_STATUS        varchar(10) comment '设备状态',
   DEVICE_STRATEGY      varchar(10) comment '策略组别（存疑）',
   DEVICE_CHECKER_GENDER char(10) comment '查验性别（存疑）',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (MANUAL_DEVICE_ID)
);

alter table SYS_MANUAL_DEVICE comment '手检站';

/*==============================================================*/
/* Table: SYS_MANUAL_GROUP                                      */
/*==============================================================*/
create table SYS_MANUAL_GROUP
(
   MANUAL_GROUP_ID      bigint not null comment '手检站设备组关系ID',
   DEVICE_GROUP_ID      bigint comment '设备组主键',
   MANUAL_DEVICE_ID     bigint comment '手检站ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (MANUAL_GROUP_ID)
);

alter table SYS_MANUAL_GROUP comment '设备组和手检站关系';

/*==============================================================*/
/* Table: SYS_ORG                                               */
/*==============================================================*/
create table SYS_ORG
(
   ORG_ID               bigint not null comment '机构ID',
   PARENT_ORG_ID        bigint comment '机构ID',
   ORG_NAME             varchar(50) comment '机构名称',
   ORG_NUMBER           varchar(50) comment '机构编号',
   LEADER               varchar(50) comment '负责人',
   MOBILE               varchar(20) comment '联系方式',
   STATUS               varchar(10) comment '当前状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ORG_ID)
);

alter table SYS_ORG comment '机构';

/*==============================================================*/
/* Table: SYS_PARAMETER                                         */
/*==============================================================*/
create table SYS_PARAMETER
(
   PARAMETER_ID         bigint not null comment '参数ID',
   PARAMETER_CATEGORY_ID bigint comment '参数ID',
   PARAMETER_NAME       varchar(50) comment '参数名称',
   PARAMETER_CAPTION    varchar(50) comment '参数显示名称',
   PARAMETER_CATEGORY   varchar(10) comment '参数类型',
   DEVICE_CATEGORY      varchar(10) comment '设备类型',
   DEVICE_ID            char(10),
   PARAMETER_VALUE      varchar(50) comment '参数值',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (PARAMETER_ID)
);

alter table SYS_PARAMETER comment '参数';

/*==============================================================*/
/* Table: SYS_PARAMETER_CATEGORY                                */
/*==============================================================*/
create table SYS_PARAMETER_CATEGORY
(
   PARAMETER_CATEGORY_ID bigint not null comment '参数ID',
   PARAMETER_CATEGORY_NAME varchar(50) comment '参数名称',
   PARAMETER_CATEGORY_CAPTION varchar(50) comment '参数显示名称',
   PARAMETER_CATEGORY   varchar(10) comment '参数类型',
   DEVICE_CATEGORY      varchar(10) comment '设备类型',
   PARAMETER_VALUE      varchar(50) comment '参数值',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (PARAMETER_CATEGORY_ID)
);

alter table SYS_PARAMETER_CATEGORY comment '参数类型';

/*==============================================================*/
/* Table: SYS_REMOTE_DEVICE                                     */
/*==============================================================*/
create table SYS_REMOTE_DEVICE
(
   REMOTE_DEVICE_ID     bigint not null comment '判图站ID',
   DEVICE_NAME          varchar(50) comment '设备名称',
   DEVICE_TYPE          varchar(50) comment '设备型号',
   DEVICE_SERIAL        varchar(50) comment '设备编号',
   DEVICE_DESC          varchar(500) comment '设备描述',
   DEVICE_IP            varchar(20) comment '设备IP',
   DEVICE_PASSAGEWAY    varchar(50) comment '设备通道',
   DEVICE_STATUS        varchar(10) comment '设备状态',
   DEVICE_STRATEGY      varchar(10) comment '策略组别（存疑）',
   DEVICE_CHECKER_GENDER char(10) comment '查验性别（存疑）',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (REMOTE_DEVICE_ID)
);

alter table SYS_REMOTE_DEVICE comment '判图站';

/*==============================================================*/
/* Table: SYS_REMOTE_GROUP                                      */
/*==============================================================*/
create table SYS_REMOTE_GROUP
(
   REMOTE_GROUP_ID      bigint not null comment '判图站与设备组关系ID',
   REMOTE_DEVICE_ID     bigint comment '判图站ID',
   DEVICE_GROUP_ID      bigint comment '设备组主键',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (REMOTE_GROUP_ID)
);

alter table SYS_REMOTE_GROUP comment '设备组和判图站关系';

/*==============================================================*/
/* Table: SYS_RESOURCE                                          */
/*==============================================================*/
create table SYS_RESOURCE
(
   RESOURCE_ID          bigint not null comment '资源ID',
   PARENT_RESOURCE_ID   bigint comment '资源ID',
   RESOURCE_NAME        varchar(50) comment '资源名称',
   RESOURCE_CAPTION     varchar(50) comment '资源在页面上显示的文字',
   RESOURCE_URL         varchar(200) comment '资源地址',
   RESOURCE_CATEGORY    varchar(10) comment '资源类型',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (RESOURCE_ID)
);

alter table SYS_RESOURCE comment '用户资源';

/*==============================================================*/
/* Table: SYS_ROLE                                              */
/*==============================================================*/
create table SYS_ROLE
(
   ROLE_ID              bigint not null comment '用户角色ID',
   ORG_ID               bigint comment '机构ID',
   ROLE_NAME            varchar(50) comment '角色名称',
   ROLE_FLAG            varchar(10) comment '角色标记',
   STATUS               varchar(10) comment '当前状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ROLE_ID)
);

alter table SYS_ROLE comment '用户角色';

/*==============================================================*/
/* Table: SYS_ROLE_ACCESS                                       */
/*==============================================================*/
create table SYS_ROLE_ACCESS
(
   ACCESS_ID            bigint not null comment '访问权限ID',
   ACCESS_NAME          varchar(20) comment '访问权限名称',
   ACCESS_CATEGORY      varchar(50) comment '权限类型',
   ACCESS_CATEGORY_CODE varchar(10) comment '权限类型编码',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ACCESS_ID)
);

alter table SYS_ROLE_ACCESS comment '用户访问权限;此表考虑用数据字典实现';

/*==============================================================*/
/* Table: SYS_ROLE_RESOURCE                                     */
/*==============================================================*/
create table SYS_ROLE_RESOURCE
(
   ROLE_RESOURCE_ID     bigint not null comment '角色拥有的资源ID',
   RESOURCE_ID          bigint comment '资源ID',
   ROLE_ID              bigint comment '用户角色ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ROLE_RESOURCE_ID)
);

alter table SYS_ROLE_RESOURCE comment '用户角色拥有的资源';

/*==============================================================*/
/* Table: SYS_ROLE_USER                                         */
/*==============================================================*/
create table SYS_ROLE_USER
(
   ROLE_USER_ID         bigint not null comment '用户所属角色ID',
   ROLE_ID              bigint comment '用户角色ID',
   USER_ID              bigint comment '用户ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ROLE_USER_ID)
);

alter table SYS_ROLE_USER comment '用户所属角色';

/*==============================================================*/
/* Table: SYS_STRATEGY                                          */
/*==============================================================*/
create table SYS_STRATEGY
(
   STRATEGY_ID          bigint not null comment '策略ID',
   MODE_ID              bigint comment '工作模式ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (STRATEGY_ID)
);

alter table SYS_STRATEGY comment '分发策略定义';

/*==============================================================*/
/* Table: SYS_USER                                              */
/*==============================================================*/
create table SYS_USER
(
   USER_ID              bigint not null comment '用户ID',
   ORG_ID               bigint comment '机构ID',
   USER_NAME            varchar(50) comment '用户姓名',
   USER_ACCOUNT         varchar(20) comment '帐号',
   PASSWORD             varchar(20) comment '密码',
   USER_NUMBER          varchar(50) comment '用户编号',
   GENDER               varchar(10) comment '性别',
   IDENTITY_CARD        varchar(50) comment '证件号',
   POST                 varchar(50) comment '岗位',
   EDUCATION            varchar(10) comment '学历',
   DEGREE               varchar(10) comment '学位',
   EMAIL                varchar(50) comment '邮箱',
   MOBILE               varchar(20) comment '联系电话',
   ADDRESS              varchar(50) comment '家庭住址',
   CATEGORY             varchar(10) comment '人员类别(是否使用待定)',
   STATUS               varchar(10) comment '当前状态',
   PORTRAIT             varchar(200) comment '照片',
   TASK_ID              bigint comment '任务ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (USER_ID)
);

alter table SYS_USER comment '用户';

/*==============================================================*/
/* Table: SYS_USER_GROUP                                        */
/*==============================================================*/
create table SYS_USER_GROUP
(
   USERGROUP_ID         bigint not null comment '用户组ID',
   ORG_ID               bigint comment '机构ID',
   GROUP_NAME           varchar(50) comment '用户组名称',
   GROUP_FLAG           varchar(10) comment '用户组标记(待明确)',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (USERGROUP_ID)
);

alter table SYS_USER_GROUP comment '用户组';

/*==============================================================*/
/* Table: SYS_USER_GROUP_LOOKUP                                 */
/*==============================================================*/
create table SYS_USER_GROUP_LOOKUP
(
   LOOKUP_ID            bigint not null comment '用户能查看的数据组ID',
   USER_ID              bigint comment '用户ID',
   DATA_GROUP_ID        bigint comment '数据组ID',
   USERGROUP_ID         bigint comment '用户组ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (LOOKUP_ID)
);

alter table SYS_USER_GROUP_LOOKUP comment '用户组能查看的数据组';

/*==============================================================*/
/* Table: SYS_USER_GROUP_ROLE                                   */
/*==============================================================*/
create table SYS_USER_GROUP_ROLE
(
   USER_GROUP_ROLE_ID   bigint not null comment '用户组和角色对应关系ID',
   USERGROUP_ID         bigint comment '用户组ID',
   ROLE_ID              bigint comment '用户角色ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (USER_GROUP_ROLE_ID)
);

alter table SYS_USER_GROUP_ROLE comment '用户组所属的角色';

/*==============================================================*/
/* Table: SYS_USER_GROUP_USER                                   */
/*==============================================================*/
create table SYS_USER_GROUP_USER
(
   USER_ID              bigint not null comment '用户ID',
   USERGROUP_ID         bigint comment '用户组ID',
   GROUP_USER_ID        bigint comment '用户与用户组关系ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (USER_ID)
);

alter table SYS_USER_GROUP_USER comment '用户所属用户组';

/*==============================================================*/
/* Table: SYS_USER_LOOKUP                                       */
/*==============================================================*/
create table SYS_USER_LOOKUP
(
   LOOKUP_ID            bigint not null comment '用户所能查看数据组ID',
   USER_ID              bigint comment '用户ID',
   DATA_GROUP_ID        bigint comment '数据组ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (LOOKUP_ID)
);

alter table SYS_USER_LOOKUP comment '用户能查看的数据组';

/*==============================================================*/
/* Table: SYS_WORKFLOW                                          */
/*==============================================================*/
create table SYS_WORKFLOW
(
   WORKFLOW_ID          bigint not null comment '工作流程ID',
   MODE_ID              bigint comment '工作模式ID',
   STEP_SEQUENCE        int comment '步骤序号',
   STEP_NAME            varchar(50) comment '步骤名称',
   TASK_TYPE            varchar(10) comment '任务类型',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (WORKFLOW_ID)
);

alter table SYS_WORKFLOW comment '工作流程定义';

/*==============================================================*/
/* Table: SYS_WORK_MODE                                         */
/*==============================================================*/
create table SYS_WORK_MODE
(
   MODE_ID              bigint not null comment '工作模式ID',
   MODE_NAME            varchar(20) comment '模式名称',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (MODE_ID)
);

alter table SYS_WORK_MODE comment '工作模式';

