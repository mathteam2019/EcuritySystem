/*==============================================================*/
/* Database name:  DATABASE_1                                   */
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019/11/20 23:44:04                          */
/*==============================================================*/


use DATABASE_1;

use DATABASE_1;

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
   primary key (ORG_ID),
   constraint FK_REFERENCE_12 foreign key (PARENT_ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
);

alter table SYS_ORG comment '机构';

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
   LEADER               varchar(50) comment '负责人(NEW)',
   MOBILE               varchar(50) comment '联系电话(NEW)',
   FIELD_STATUS         varchar(10) comment '场地状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (FIELD_ID),
   constraint FK_REFERENCE_13 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_61 foreign key (PARENT_FIELD_ID)
      references SYS_FIELD (FIELD_ID) on delete restrict on update restrict
);

alter table SYS_FIELD comment '场地';

/*==============================================================*/
/* Table: SYS_DEVICE_CONFIG                                     */
/*==============================================================*/
create table SYS_DEVICE_CONFIG
(
   CONFIG_ID            bigint not null comment '配置主键',
   MODE_ID              bigint comment '工作模式ID',
   FIELD_ID             bigint comment '场地ID',
   DEVICE_ID            bigint comment '安检仪设备ID',
   MANUAL_SWITCH        varchar(10) comment '是否有手检功能',
   ATR_SWITCH           varchar(10) comment 'ATR无嫌疑处理',
   MAN_REMOTE_GENDER    varchar(10) comment '男判图员判图对象',
   WOMAN_REMOTE_GENDER  varchar(10) comment '女判图员判图对象',
   MAN_MANUAL_GENDER    varchar(10) comment '男手检员手检对象',
   WOMAN_MANUAL_GENDER  varchar(10) comment '女手检员手检对象',
   MAN_DEVICE_GENDER    varchar(10) comment '男引导员查验对象',
   WOMAN_DEVICE_GENDER  varchar(10) comment '女引导员查验对象',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (CONFIG_ID),
   constraint FK_REFERENCE_63 foreign key (MODE_ID)
      references SYS_WORK_MODE (MODE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_66 foreign key (FIELD_ID)
      references SYS_FIELD (FIELD_ID) on delete restrict on update restrict
);

alter table SYS_DEVICE_CONFIG comment '运行配置(NEW)';

/*==============================================================*/
/* Table: FROM_CONFIG_ID                                        */
/*==============================================================*/
create table FROM_CONFIG_ID
(
   FROM_CONFIG_ID       bigint comment '配置来源ID',
   CONFIG_ID            bigint not null comment '配置主键',
   FROM_DEVICE_ID       bigint comment '安检仪设备ID',
   DEVICE_ID            bigint comment '安检仪设备ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   constraint FK_REFERENCE_82 foreign key (CONFIG_ID)
      references SYS_DEVICE_CONFIG (CONFIG_ID) on delete restrict on update restrict
);

alter table FROM_CONFIG_ID comment '设备配置来源表';

/*==============================================================*/
/* Table: SER_ARCHIVES_TEMPLATE                                 */
/*==============================================================*/
create table SER_ARCHIVES_TEMPLATE
(
   ARCHIVES_TEMPLATE_ID bigint not null comment '档案模板ID',
   S_TEMPLATE_NAME      varchar(50) comment '档案模板名称',
   ARCHIVES_TEMPLATE_NUMBER varchar(50) comment '档案模板编号',
   CATEGORY_ID          bigint comment '设备分类(NEW)',
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
/* Table: SER_ARCHIVES                                          */
/*==============================================================*/
create table SER_ARCHIVES
(
   ARCHIVE_ID           bigint not null comment '档案ID',
   ARCHIVES_TEMPLATE_ID bigint comment '档案模板ID',
   ARCHIVES_NAME        varchar(50) comment '档案名称',
   ARCHIVES_NUMBER      varchar(50) comment '档案编号',
   CATEGORY_ID          bigint comment '设备分类(NEW)',
   MANUFACTURER         varchar(10) comment '生产厂商',
   ORIGINAL_MODEL       varchar(50) comment '原厂型号',
   STATUS               varchar(10) comment '当前状态',
   IMAGE_URL            varchar(200) comment '图像地址',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ARCHIVE_ID),
   constraint FK_REFERENCE_43 foreign key (ARCHIVES_TEMPLATE_ID)
      references SER_ARCHIVES_TEMPLATE (ARCHIVES_TEMPLATE_ID) on delete restrict on update restrict
);

alter table SER_ARCHIVES comment '档案管理';

/*==============================================================*/
/* Table: SYS_DEVICE_CATEGORY                                   */
/*==============================================================*/
create table SYS_DEVICE_CATEGORY
(
   CATEGORY_ID          bigint not null comment '判图站与设备组关系ID',
   PARENT_CATEGORY_ID   bigint comment '判图站与设备组关系ID',
   CATEGORY_NUMBER      varchar(50) comment '分类编号',
   CATEGORY_NAME        varchar(50) comment '分类名称',
   STATUS               varchar(10) comment '设备状态（new）',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (CATEGORY_ID),
   constraint FK_REFERENCE_65 foreign key (PARENT_CATEGORY_ID)
      references SYS_DEVICE_CATEGORY (CATEGORY_ID) on delete restrict on update restrict
);

alter table SYS_DEVICE_CATEGORY comment '设备分类(NEW)';

/*==============================================================*/
/* Table: SER_DEVICE_REGISTER                                   */
/*==============================================================*/
create table SER_DEVICE_REGISTER
(
   REGISTER_ID          bigint not null comment '注册信息ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (REGISTER_ID)
);

alter table SER_DEVICE_REGISTER comment '设备注册信息';

/*==============================================================*/
/* Table: SYS_DEVICE                                            */
/*==============================================================*/
create table SYS_DEVICE
(
   DEVICE_ID            bigint not null comment '设备ID',
   DEVICE_NAME          varchar(50) comment '设备名称',
   DEVICE_TYPE          varchar(50) comment '设备型号',
   DEVICE_SERIAL        varchar(50) comment '设备编号',
   MANUFACTURER         varchar(10) comment '生产厂商(NEW)',
   ORIGINAL_MODEL       varchar(50) comment '原厂型号(NEW)',
   ORIGINAL_FACTORY_NUMBER varchar(50) comment '原厂编号',
   MANUFACTURE_DATE     date comment '生产日期',
   PURCHASE_DATE        date comment '采购日期',
   SUPPLIER             varchar(50) comment '供应商(NEW)',
   CONTACTS             varchar(50) comment '联系人(NEW)',
   MOBILE               varchar(50) comment '联系方式(NEW)',
   REGISTRATION_NUMBER  varchar(50) comment '设备注册号(NEW)',
   IMAGE_URL            varchar(200) comment '图像地址(NEW)',
   FIELD_ID             bigint comment '场地ID',
   ARCHIVE_ID           bigint comment '档案ID',
   CATEGORY_ID          bigint comment '分类ID',
   REGISTER_ID          bigint comment '注册信息ID',
   DEVICE_DESC          varchar(500) comment '设备描述',
   DEVICE_IP            varchar(20) comment '设备IP',
   DEVICE_PASSAGEWAY    varchar(50) comment '设备通道',
   STATUS               varchar(10) comment '当前状态',
   DEVICE_STRATEGY      varchar(10) comment '策略组别（存疑）',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   GUID                 varchar(255) comment '设备GUID(NEW)',
   primary key (DEVICE_ID),
   constraint FK_REFERENCE_11 foreign key (FIELD_ID)
      references SYS_FIELD (FIELD_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_44 foreign key (ARCHIVE_ID)
      references SER_ARCHIVES (ARCHIVE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_64 foreign key (CATEGORY_ID)
      references SYS_DEVICE_CATEGORY (CATEGORY_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_70 foreign key (REGISTER_ID)
      references SER_DEVICE_REGISTER (REGISTER_ID) on delete restrict on update restrict
);

alter table SYS_DEVICE comment '设备端(NEW)';

/*==============================================================*/
/* Table: SER_TASK                                              */
/*==============================================================*/
create table SER_TASK
(
   TASK_ID              bigint not null comment '任务ID',
   DEVICE_ID            bigint comment '设备ID(安检仪）',
   TASK_NUMBER          varchar(50) comment '任务编号',
   STATUS               varchar(255) comment '状态',
   INVALIDSCAN          bigint comment '无效扫描',
   SCENE                varchar(50) comment '现场',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (TASK_ID),
   constraint FK_REFERENCE_37 foreign key (DEVICE_ID)
      references SYS_DEVICE (DEVICE_ID) on delete restrict on update restrict
);

alter table SER_TASK comment '任务';

/*==============================================================*/
/* Table: HISTORY                                               */
/*==============================================================*/
create table HISTORY
(
   HISTORY_ID           bigint not null comment '扫描ID',
   TASK_ID              bigint comment '任务ID',
   MODE                 bigint comment '工作模式',
   SCAN_ID              bigint comment '扫描ID',
   SCAN_WORKFLOW_ID     bigint comment '流程ID',
   SCAN_DEVICE_ID       bigint comment '设备ID',
   SCAN_IMAGE_ID        bigint comment '扫描图像ID',
   SCAN_ATR_RESULT      varchar(10) comment 'ATR结论',
   SCAN_FOOT_ALARM      varchar(10) comment '脚步报警',
   SCAN_START_TIME      timestamp comment '开始时间',
   SCAN_END_TIME        timestamp comment '结束时间',
   SCAN_POINTSMAN_ID    bigint comment '引导员ID',
   SCAN_POINTSMAN_NAME  varchar(50) comment '引导员',
   ASSIGNSCAN_ID        bigint comment '分派环节ID',
   ASSIGN_WORKFLOW_ID   bigint comment '流程ID',
   ASSIGN_USER_ID       bigint comment '分派人（系统默认',
   ASSIGN_USER_NAME     varchar(50) comment '分派人姓名',
   ASSIGN_JUDGE_DEVICE_ID varchar(10) comment '判图站',
   ASSIGN_HAND_DEVICE_ID varchar(10) comment '手检设备(安检仪或者ipad）',
   ASSIGN_START_TIME    timestamp comment '开始时间',
   ASSIGN_END_TIME      timestamp comment '结束时间',
   ASSIGN_TIMEOUT       varchar(10) comment '分派超时',
   ASSIGN_STATUS        varchar(10) comment '分派状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   JUDGE_ID             bigint comment '判图ID',
   JUDGE_WORKFLOW_ID    bigint comment '流程ID',
   JUDGE_DEVICE_ID      bigint comment '判图站',
   JUDGE_RESULT         varchar(10) comment '判图结论',
   JUDGE_TIMEOUT        varchar(10) comment '判图超时',
   HAND_EXAMINATION_ID  bigint comment '手检ID',
   HAND_WORKFLOW_ID     bigint comment '流程ID',
   HAND_DEVICE_ID       bigint comment '设备ID',
   HAND_RESULT          varchar(10) comment '手检结论',
   HAND_START_TIME      timestamp comment '开始时间',
   HAND_END_TIME        timestamp comment '结束时间',
   HAND_USER_ID         bigint comment '手检员',
   HAND_TASK_RESULT     varchar(10) comment '任务结论',
   HAND_GOODS           varchar(255) comment '查获物品',
   HAND_GOODS_GRADE     varchar(10) comment '查获物品等级',
   HAND_COLLECT_SIGN    varchar(10) comment '收藏标识',
   HAND_ATTACHED_ID     bigint comment '附件ID',
   HAND_COLLECT_LABEL   varchar(10) comment '收藏标签',
   HAND_APPRAISE        varchar(10) comment '评价判图',
   JUDGE_START_TIME     timestamp comment '开始时间',
   JUDGE_END_TIME       timestamp comment '结束时间',
   JUDGE_USER_ID        bigint comment '判图员ID',
   JUDGE_ASSIGN_TIMEOUT varchar(10) comment '分派超时',
   JUDGE_STATUS         varchar(10) comment '状态',
   primary key (HISTORY_ID),
   constraint FK_REFERENCE_79 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table HISTORY comment '查验历史表';

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
   primary key (INDICATORS_ID),
   constraint FK_REFERENCE_42 foreign key (ARCHIVES_TEMPLATE_ID)
      references SER_ARCHIVES_TEMPLATE (ARCHIVES_TEMPLATE_ID) on delete restrict on update restrict
);

alter table SER_ARCHIVES_INDICATORS comment '模板指标';

/*==============================================================*/
/* Table: SER_ARCHIVES_VALUE                                    */
/*==============================================================*/
create table SER_ARCHIVES_VALUE
(
   INDICATORS_ID        bigint comment '指标ID',
   ARCHIVE_ID           bigint comment '档案ID',
   VALUE                varchar(255) comment '指标值（update）',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   ARCHIVES_VALUE_ID    bigint,
   key AK_KEY_1 (ARCHIVES_VALUE_ID),
   constraint FK_REFERENCE_59 foreign key (INDICATORS_ID)
      references SER_ARCHIVES_INDICATORS (INDICATORS_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_60 foreign key (ARCHIVE_ID)
      references SER_ARCHIVES (ARCHIVE_ID) on delete restrict on update restrict
);

alter table SER_ARCHIVES_VALUE comment '档案指标值';

/*==============================================================*/
/* Table: SER_ASSIGN                                            */
/*==============================================================*/
create table SER_ASSIGN
(
   ASSIGN_ID            bigint not null comment '扫描ID',
   TASK_ID              bigint comment '任务ID',
   WORKFLOW_ID          bigint comment '流程ID',
   MODE                 bigint comment '工作模式',
   ASSIGN_USER_ID       bigint comment '分派人系统默认',
   ASSIGN_JUDGE_DEVICE_ID varchar(10) comment '判图站',
   ASSIGN_HAND_DEVICE_ID varchar(10) comment '手检设备(安检仪或者ipad）',
   ASSIGN_START_TIME    timestamp comment '开始时间',
   ASSIGN_END_TIME      timestamp comment '结束时间',
   ASSIGN_TIMEOUT       varchar(10) comment '分派超时',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ASSIGN_ID),
   constraint FK_REFERENCE_77 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_ASSIGN comment '调度分派表';

/*==============================================================*/
/* Table: SER_CHECK_RESULT2                                     */
/*==============================================================*/
create table SER_CHECK_RESULT2
(
   CHECK_RESULT_ID      bigint not null comment '结果ID',
   DEVICE_ID            bigint comment '设备ID',
   TASK_ID              bigint not null comment '任务ID',
   HAND_TASK_RESULT     varchar(10) comment '任务结论',
   HAND_GOODS           varchar(255) comment '查获物品',
   HAND_GOODS_GRADE     varchar(10) comment '查获物品等级',
   HAND_COLLECT_SIGN    varchar(10) comment '收藏标识',
   HAND_ATTACHED_ID     bigint comment '附件ID',
   HAND_COLLECT_LABEL   varchar(10) comment '收藏标签',
   HAND_APPRAISE        varchar(10) comment '评价判图',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (CHECK_RESULT_ID),
   constraint FK_REFERENCE_73 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_CHECK_RESULT2 comment '查验结果';

/*==============================================================*/
/* Table: SER_DEVICE_STATUS                                     */
/*==============================================================*/
create table SER_DEVICE_STATUS
(
   STATUS_ID            bigint not null comment '状态ID',
   DEVICE_ID            bigint comment '设备ID',
   FIELD_ID             bigint comment '场地ID',
   CURRENT_WORKFLOW     varchar(50) comment '当前流程',
   CURRENT_STATUS       varchar(50) comment '流程状态',
   CATEGORY_ID          varchar(50) comment '设备分类(NEW)',
   MANUFACTURER         varchar(50) comment '生产厂商(NEW)',
   ORIGINAL_MODEL       varchar(50) comment '原厂型号(NEW)',
   CHECK_COUNT          int comment '查验人数',
   PASS_COUNT           int comment '通过人数',
   WARNING_COUNT        int comment '报警人数',
   MANUAL_COUNT         int comment '手检人数',
   ACCOUNT              varchar(50) comment '账号',
   LOGIN_TIME           timestamp comment '登录时间',
   IP_ADDRESS           varchar(50) comment 'IP',
   PLC_STATUS           varchar(50) comment 'PLC连接状态',
   MASTER_CARD_STATUS   varchar(50) comment '主采集卡状态',
   SLAVE_CARD_STATUS    varchar(50) comment '从采集卡状态',
   SERVO                varchar(50) comment '伺服',
   SLIDE_POSITION       varchar(50) comment '滑块位置',
   EMERGENCY_STOP       varchar(50) comment '急停',
   FOOT_WARNING         varchar(50) comment '脚部报警',
   DISK_SPACE           varchar(50) comment '磁盘空间',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (STATUS_ID),
   constraint FK_REFERENCE_67 foreign key (DEVICE_ID)
      references SYS_DEVICE (DEVICE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_68 foreign key (FIELD_ID)
      references SYS_FIELD (FIELD_ID) on delete restrict on update restrict
);

alter table SER_DEVICE_STATUS comment '设备状态';

/*==============================================================*/
/* Table: SER_DEVICE_VALUE                                      */
/*==============================================================*/
create table SER_DEVICE_VALUE
(
   DEVICE_ID            bigint comment '设备ID',
   INDICATORS_ID        bigint comment '指标ID',
   ARCHIVE_ID           bigint comment '档案ID',
   VALUE_ID             bigint,
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   constraint FK_REFERENCE_62 foreign key (DEVICE_ID)
      references SYS_DEVICE (DEVICE_ID) on delete restrict on update restrict
);

alter table SER_DEVICE_VALUE comment '设备指标值';

/*==============================================================*/
/* Table: SER_DEV_LOG                                           */
/*==============================================================*/
create table SER_DEV_LOG
(
   ID                   bigint not null comment '主键ID',
   GUID                 varchar(255) comment '设备',
   DEV_TYPE             varchar(10) comment '日志设备类型',
   LOGIN_NAME           varchar(200) comment '账号',
   CATEGORY             tinyint(2) comment '类别',
   LEVEL                int comment '级别',
   CONTENT              varchar(2000) comment '内容',
   TIME                 varchar(255) comment '操作时间',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ID)
);

alter table SER_DEV_LOG comment '设备访问日志';

/*==============================================================*/
/* Table: SER_HAND_EXAMINATION                                  */
/*==============================================================*/
create table SER_HAND_EXAMINATION
(
   HAND_EXAMINATION_ID  bigint not null comment '手检ID',
   TASK_ID              bigint comment '任务ID',
   WORKFLOW_ID          bigint comment '流程ID',
   HAND_DEVICE_ID       bigint comment '设备ID',
   HAND_RESULT          varchar(10) comment '手检结论',
   HAND_START_TIME      timestamp comment '开始时间',
   HAND_END_TIME        timestamp comment '结束时间',
   HAND_USER_ID         bigint comment '手检员',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (HAND_EXAMINATION_ID),
   constraint FK_REFERENCE_69 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_HAND_EXAMINATION comment '手检查验表 ';

/*==============================================================*/
/* Table: SER_SCAN                                              */
/*==============================================================*/
create table SER_SCAN
(
   SCAN_ID              bigint not null comment '扫描ID',
   TASK_ID              bigint comment '任务ID',
   WORKFLOW_ID          bigint comment '流程ID',
   SCAN_DEVICE_ID       bigint comment '设备ID',
   SCAN_IMAGE_ID        bigint comment '扫描图像ID',
   SCAN_ATR_RESULT      varchar(10) comment 'ATR结论',
   SCAN_FOOT_ALARM      varchar(10) comment '脚步报警',
   SCAN_START_TIME      timestamp comment '开始时间',
   SCAN_END_TIME        timestamp comment '结束时间',
   SCAN_POINTSMAN_ID    bigint comment '引导员ID',
   SCAN_ASSIGN_TIMEOUT  varchar(10) comment '分派超时',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (SCAN_ID),
   constraint FK_REFERENCE_75 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_SCAN comment '安检仪扫描数据表';

/*==============================================================*/
/* Table: SER_IMAGE                                             */
/*==============================================================*/
create table SER_IMAGE
(
   IMAGE_ID             bigint not null comment '图像ID',
   SCAN_ID              bigint comment '扫描ID',
   TASK_ID              bigint not null comment '任务ID',
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
   primary key (IMAGE_ID),
   constraint FK_REFERENCE_86 foreign key (SCAN_ID)
      references SER_SCAN (SCAN_ID) on delete restrict on update restrict
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
   primary key (IMAGE_PROPERTY_ID),
   constraint FK_REFERENCE_74 foreign key (IMAGE_ID)
      references SER_IMAGE (IMAGE_ID) on delete restrict on update restrict
);

alter table SER_IMAGE_PROPERTY comment '图像属性';

/*==============================================================*/
/* Table: SER_INSPECTED                                         */
/*==============================================================*/
create table SER_INSPECTED
(
   INSPECTED_ID         bigint not null comment '被查验人ID',
   TASK_ID              bigint not null comment '任务ID',
   SUSPECT_NAME         varchar(50) comment '姓名',
   IDENTITY_CARD        varchar(50) comment '证件号',
   IDENTITY_CATEGORY    varchar(10) comment '证件类型',
   GENDER               varchar(10) comment '性别',
   CHECK_LEVE           varchar(10) comment '查验等级',
   ACTIVATE_STATUS      varchar(10) comment '激活状态',
   CHECK_RESULT         varchar(10) comment '查验结果',
   CHECK_DESC           varchar(500) comment '查验描述',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (INSPECTED_ID),
   constraint FK_REFERENCE_76 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_INSPECTED comment '被查验人';

/*==============================================================*/
/* Table: SER_JUDGE_GRAPH                                       */
/*==============================================================*/
create table SER_JUDGE_GRAPH
(
   JUDGE_ID             bigint not null comment '判图ID',
   TASK_ID              bigint comment '任务ID',
   WORKFLOW_ID          bigint comment '流程ID',
   JUDGE_DEVICE_ID      bigint comment '判图站',
   JUDGE_RESULT         varchar(10) comment '判图结论',
   JUDGE_TIMEOUT        varchar(10) comment '判图超时',
   JUDGE_START_TIME     timestamp comment '开始时间',
   JUDGE_END_TIME       timestamp comment '结束时间',
   JUDGE_USER_ID        bigint comment '判图员ID（超时系统判图写system用户）',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (JUDGE_ID),
   constraint FK_REFERENCE_72 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_JUDGE_GRAPH comment '判图审查表';

/*==============================================================*/
/* Table: SER_KNOWLEDGE_CASE_DEAL                               */
/*==============================================================*/
create table SER_KNOWLEDGE_CASE_DEAL
(
   CASE_DETAL_ID        bigint not null comment '扫描ID',
   CASE_ID              bigint comment '知识库id',
   TASK_ID              bigint comment '任务ID',
   MODE                 bigint comment '工作模式',
   SCAN_ID              bigint comment '扫描ID',
   SCAN_WORKFLOW_ID     bigint comment '流程ID',
   SCAN_DEVICE_ID       bigint comment '设备ID',
   SCAN_IMAGE_ID        bigint comment '扫描图像ID',
   SCAN_ATR_RESULT      varchar(10) comment 'ATR结论',
   SCAN_FOOT_ALARM      varchar(10) comment '脚步报警',
   SCAN_START_TIME      timestamp comment '开始时间',
   SCAN_END_TIME        timestamp comment '结束时间',
   SCAN_POINTSMAN_ID    bigint comment '引导员ID',
   SCAN_POINTSMAN_NAME  varchar(50) comment '引导员',
   ASSIGNSCAN_ID        bigint comment '分派环节ID',
   ASSIGN_WORKFLOW_ID   bigint comment '流程ID',
   ASSIGN_USER_ID       bigint comment '分派人（系统默认',
   ASSIGN_USER_NAME     varchar(50) comment '分派人姓名',
   ASSIGN_JUDGE_DEVICE_ID varchar(10) comment '判图站',
   ASSIGN_HAND_DEVICE_ID varchar(10) comment '手检设备(安检仪或者ipad）',
   ASSIGN_START_TIME    timestamp comment '开始时间',
   ASSIGN_END_TIME      timestamp comment '结束时间',
   ASSIGN_TIMEOUT       varchar(10) comment '分派超时',
   ASSIGN_STATUS        varchar(10) comment '分派状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   JUDGE_ID             bigint comment '判图ID',
   JUDGE_WORKFLOW_ID    bigint comment '流程ID',
   JUDGE_DEVICE_ID      bigint comment '判图站',
   JUDGE_RESULT         varchar(10) comment '判图结论',
   JUDGE_TIMEOUT        varchar(10) comment '判图超时',
   HAND_EXAMINATION_ID  bigint comment '手检ID',
   HAND_WORKFLOW_ID     bigint comment '流程ID',
   HAND_DEVICE_ID       bigint comment '设备ID',
   HAND_RESULT          varchar(10) comment '手检结论',
   HAND_START_TIME      timestamp comment '开始时间',
   HAND_END_TIME        timestamp comment '结束时间',
   HAND_USER_ID         bigint comment '手检员',
   HAND_TASK_RESULT     varchar(10) comment '任务结论',
   HAND_GOODS           varchar(255) comment '查获物品',
   HAND_GOODS_GRADE     varchar(10) comment '查获物品等级',
   HAND_COLLECT_SIGN    varchar(10) comment '收藏标识',
   HAND_ATTACHED_ID     bigint comment '附件ID',
   HAND_COLLECT_LABEL   varchar(10) comment '收藏标签',
   HAND_APPRAISE        varchar(10) comment '评价判图',
   JUDGE_START_TIME     timestamp comment '开始时间',
   JUDGE_END_TIME       timestamp comment '结束时间',
   JUDGE_USER_ID        bigint comment '判图员ID',
   JUDGE_ASSIGN_TIMEOUT varchar(10) comment '分派超时',
   JUDGE_STATUS         varchar(10) comment '状态',
   primary key (CASE_DETAL_ID)
);

alter table SER_KNOWLEDGE_CASE_DEAL comment '知识库详情表';

/*==============================================================*/
/* Table: SER_KNOWLEDGE_CASE                                    */
/*==============================================================*/
create table SER_KNOWLEDGE_CASE
(
   CASE_ID              bigint not null comment '知识库ID',
   TASK_ID              bigint comment '任务ID',
   CASE_DETAL_ID        bigint comment '扫描ID',
   CASE_STATUS          varchar(10) comment '状态: 1提交审批 2、驳回 3、审批成功',
   CASE_COLLECT_USERID  bigint comment '收藏人',
   CASE_APPROVAL_USERID bigint comment '审批人',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (CASE_ID),
   constraint FK_REFERENCE_71 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_89 foreign key (CASE_DETAL_ID)
      references SER_KNOWLEDGE_CASE_DEAL (CASE_DETAL_ID) on delete restrict on update restrict
);

alter table SER_KNOWLEDGE_CASE comment '知识库人员案例';

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
   DATA_RANGE_CATEGORY  varchar(10) comment '数据范围类型：默认个人数据范围、隶属机构所有人数、隶属机构及下级机构所有人、所有人数据、自定义数据组',
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
   primary key (USER_ID),
   constraint FK_REFERENCE_52 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
);

alter table SYS_USER comment '用户';

/*==============================================================*/
/* Table: SER_LOGIN_INFO                                        */
/*==============================================================*/
create table SER_LOGIN_INFO
(
   LOGIN_INFO_ID        bigint not null comment '登录信息ID',
   USER_ID              bigint comment '用户ID',
   DEVICE_ID            bigint comment '设备ID',
   LOGIN_CATEGORY       varchar(10) comment '登录类型',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (LOGIN_INFO_ID),
   constraint FK_REFERENCE_51 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict
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
   primary key (ATTACHMENT_ID),
   constraint FK_REFERENCE_50 foreign key (MAINTAIN_ID)
      references SER_MAINTAIN (MAINTAIN_ID) on delete restrict on update restrict
);

alter table SER_MAINTAIN_ATTACHMENT comment '维保记录附件';

/*==============================================================*/
/* Table: SER_PLATFORM_CHECK_PARAMS                             */
/*==============================================================*/
create table SER_PLATFORM_CHECK_PARAMS
(
   SCAN_ID              bigint not null,
   SCAN_RECOGNISE_COLOUR varchar(255) comment '调色盘选择；ATR识别的嫌疑框颜色，支持颜色块的选择和RGB值的输入',
   SCAN_OVERTIME        int comment '设置0-600之间的数字，单位分钟',
   JUDGE_ASSIGN_TIME    int comment '0-30之间的数字，单位秒',
   JUDGE_PROCESSING_TIME int comment '0-30之间的数字，单位秒',
   JUDGE_SCAN_OVERTIME  int comment '设置0-600之间的数字，单位分钟',
   JUDGE_RECOGNISE_COLOUR varchar(255) comment '调色盘选择；判图的嫌疑框颜色，支持颜色块的选择和RGB值的输入',
   HAND_OVERTIME        int comment '设置0-600之间的数字，单位分钟',
   HAND_RECOGNISE_COLOUR varchar(255) comment '调色盘选择；手检的嫌疑框颜色，支持颜色块的选择和RGB值的输入，预留',
   HISTORY_DATA_STORAGE varchar(255) comment '多选：业务数据（默认必选），卡通图，转换图，原始图',
   HISTORY_DATA_EXPORT  varchar(255) comment '多选：业务数据（默认必选），卡通图，转换图，原始图',
   DISPLAY_DELETE_SUSPICION varchar(10) comment '显示被删除的嫌疑框;范围 手检、过程任务、历史任务',
   DISPLAY_DELETE_SUSPICION_COLOUR varchar(255) comment '调色盘选择;范围 手检、过程任务、历史任务手检、过程任务、历史任务',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (SCAN_ID)
);

alter table SER_PLATFORM_CHECK_PARAMS comment '系统参数-平台参数人员查验';

/*==============================================================*/
/* Table: SER_PLATFORM_OTHER_PARAMS                             */
/*==============================================================*/
create table SER_PLATFORM_OTHER_PARAMS
(
   INITIAL_PASSWORD     varchar(255) comment '6-20位，可以设置系统用户统一的初始密码',
   LOGIN_NUMBER         int comment '0-8之间的数字，0表示不控制',
   LOG_MAX_NUMBER       int comment '设定日志导出条数，如果导出时超出，系统提示：超出预设条数，继续导出可能影响系统性能，是否继续。',
   DEVICE_TRAFFIC_SETTINGS int comment '低、中、高',
   STORAGE_DETECTION_CYCLE int comment '0-600之间的数字，单位分钟',
   STORAGE_ALARM        int comment '0-1000之间的数字，单位G',
   HISTORY_DATA_CYCLE   int comment '0-5000之间的数字，单位天'
);

alter table SER_PLATFORM_OTHER_PARAMS comment '系统参数-平台参数其他';

/*==============================================================*/
/* Table: SER_SCAN_PARAMS                                       */
/*==============================================================*/
create table SER_SCAN_PARAMS
(
   SCAN_PARAMS_ID       bigint not null comment '配置ID',
   DEV_ID               bigint comment '设备ID',
   AIRCALIWARNTIME      int comment '120:  0-600之间的数字，单位分钟',
   STANDBYTIME          int comment '0 设备端登陆后无操作超过此时间，退回登陆界面。0-600之间的数字，单位分钟',
   ALARMSOUND           varchar(10) comment 'TRUE',
   PASSSOUND            varchar(10) comment 'TRUE',
   POSERRORSOUND        varchar(10) comment 'TRUE',
   STANDSOUND           varchar(10) comment 'FALSE',
   SCANSOUND            varchar(10) comment 'FALSE',
   SCANOVERUSESOUND     varchar(10) comment 'FALSE',
   AUTORECOGNISE        varchar(10) comment 'TRUE',
   RECOGNITIONRATE      int comment '0',
   SAVESCANDATA         varchar(10) comment 'TRUE',
   SAVESUSPECTDATA      varchar(10) comment 'FALSE',
   FACIALBLURRING       varchar(10) comment 'TRUE',
   CHESTBLURRING        varchar(10) comment 'TRUE',
   HIPBLURRING          varchar(10) comment 'TRUE',
   GROINBLURRING        varchar(10) comment 'TRUE',
   ATUOCONFIG           varchar(10) comment '是',
   DICTIONARY_NAME      varchar(50) comment '数据字典名称',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (SCAN_PARAMS_ID)
);

alter table SER_SCAN_PARAMS comment '系统参数-安检仪';

/*==============================================================*/
/* Table: SER_SCAN_PARAMS_FROM                                  */
/*==============================================================*/
create table SER_SCAN_PARAMS_FROM
(
   FROM_CONFIG_ID       bigint comment '配置来源ID',
   SCAN_PARAMS_ID       bigint not null comment '安检仪参数ID',
   FROM_DEVICE_ID       bigint comment '安检仪设备ID',
   DEVICE_ID            bigint comment '安检仪设备ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   constraint FK_REFERENCE_84 foreign key (SCAN_PARAMS_ID)
      references SER_SCAN_PARAMS (SCAN_PARAMS_ID) on delete restrict on update restrict
);

alter table SER_SCAN_PARAMS_FROM comment '安检仪参数设置来源表';

/*==============================================================*/
/* Table: SER_TASK_STEP                                         */
/*==============================================================*/
create table SER_TASK_STEP
(
   STEP_ID              bigint not null comment '步骤ID',
   TASK_ID              bigint comment '任务ID',
   WORKFLOW_ID          bigint not null comment '工作流程ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (STEP_ID),
   constraint FK_REFERENCE_33 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_TASK_STEP comment '任务执行步骤';

/*==============================================================*/
/* Table: SER_TIMING_TASK                                       */
/*==============================================================*/
create table SER_TIMING_TASK
(
   TIMING_TASK_ID       bigint not null comment '定时任务记录ID',
   TASK_NUMBER          varchar(50) comment '定时任务编号',
   SCHEDULED_TIME       timestamp comment '预计开始时间',
   TASK_COUNT           int comment '执行次数',
   TIMER_START          timestamp comment '定时生效起始时间',
   TIMER_END            timestamp comment '定时生效截至时间',
   STATUS               varchar(10) comment '当前状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (TIMING_TASK_ID)
);

alter table SER_TIMING_TASK comment '定时任务推送记录';

/*==============================================================*/
/* Table: SYS_ACCESS_LOG                                        */
/*==============================================================*/
create table SYS_ACCESS_LOG
(
   ID                   bigint comment '主键ID',
   OPERATE_TIME         datetime comment '操作时间',
   CLIENT_IP            varchar(255) comment '客户端IP',
   OPERATE_ID           bigint comment '操作员ID',
   OPERATE_ACCOUNT      varchar(200) comment '操作员名',
   ACTION               varchar(10) comment 'login/logout/kickout/timeout',
   OPERATE_RESULT       varchar(10) comment '成功  \失败',
   REASON_CODE          varchar(255) comment '失败原因代码',
   ONLINE_TIME          int comment '在线时长(秒)',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注'
);

alter table SYS_ACCESS_LOG comment '系统访问日志';

/*==============================================================*/
/* Table: SYS_ROLE_ACCESS                                       */
/*==============================================================*/
create table SYS_ROLE_ACCESS
(
   ACCESS_ID            bigint not null comment '访问权限ID',
   DEVICE_ID            varchar(50) comment '1.取UUID
            2.UUID可能是设备端，或者手检端、判图端',
   DEVICE_TYPE          varchar(10) comment '取数据字典',
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
/* Table: SYS_ROLE                                              */
/*==============================================================*/
create table SYS_ROLE
(
   ROLE_ID              bigint not null comment '用户角色ID',
   ORG_ID               bigint comment '机构ID',
   ROLE_NUMBER          varchar(50) comment '角色编号',
   ROLE_NAME            varchar(50) comment '角色名称',
   ROLE_FLAG            varchar(10) comment '角色标记',
   STATUS               varchar(10) comment '当前状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ROLE_ID),
   constraint FK_REFERENCE_20 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
);

alter table SYS_ROLE comment '用户角色';

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
   primary key (ACCESS_USER_ID),
   constraint FK_REFERENCE_40 foreign key (ACCESS_ID)
      references SYS_ROLE_ACCESS (ACCESS_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_49 foreign key (ROLE_ID)
      references SYS_ROLE (ROLE_ID) on delete restrict on update restrict
);

alter table SYS_ACCESS_ROLE comment '用户拥有的权限';

/*==============================================================*/
/* Table: SYS_AUDIT_LOG                                         */
/*==============================================================*/
create table SYS_AUDIT_LOG
(
   ID                   bigint not null comment '主键ID',
   OPERATE_TIME         datetime not null comment '操作时间',
   CLIENT_IP            varchar(255) comment '客户端IP',
   OPERATOR_ID          bigint comment '操作员ID',
   OPERATE_ACCOUNT      varchar(200) comment '操作员名',
   OPERATE_OBJECT       varchar(255) comment '操作对象',
   ACTION               varchar(10) comment '操作',
   OPERATE_RESULT       varchar(10) comment '操作结果',
   OPERATE_CONTENT      varchar(255) comment '操作内容',
   REASON_CODE          varchar(255) comment '失败原因代码',
   ONLINE_TIME          int comment '在线时长(秒)',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (ID)
);

alter table SYS_AUDIT_LOG comment '操作日志';

/*==============================================================*/
/* Table: SYS_AUDIT_LOG_DETAIL                                  */
/*==============================================================*/
create table SYS_AUDIT_LOG_DETAIL
(
   ID                   bigint comment '主键ID',
   AUDIT_LOG_ID         bigint comment '操作日志ID',
   FIELD_NAME           varchar(255) comment '数据项',
   VALUE_BEFORE         varchar(2000) comment '操作前内容',
   VALUE_AFTER          varchar(2000) comment '操作后内容',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   constraint FK_REFERENCE_83 foreign key (ID)
      references SYS_AUDIT_LOG (ID) on delete restrict on update restrict
);

alter table SYS_AUDIT_LOG_DETAIL comment '日志详细信息';

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
   DATA_GROUP_NUMBER    varchar(50) comment '数据组编号(NEW)',
   DATA_GROUP_NAME      varchar(50) comment '数据组名称',
   DATA_GROUP_FLAG      varchar(10) comment '数据组标记',
   STATUS               varchar(10) comment '当前状态',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (DATA_GROUP_ID),
   constraint FK_REFERENCE_23 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
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
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (DATA_GROUP_USER_ID),
   constraint FK_REFERENCE_24 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_25 foreign key (DATA_GROUP_ID)
      references SYS_DATA_GROUP (DATA_GROUP_ID) on delete restrict on update restrict
);

alter table SYS_DATA_GROUP_USER comment '用户所属数据组';

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
   primary key (DEVICE_USER_ID),
   constraint FK_REFERENCE_14 foreign key (DEVICE_ID)
      references SYS_DEVICE (DEVICE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_15 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict
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
   primary key (DATA_ID),
   constraint FK_REFERENCE_41 foreign key (DICTIONARY_ID)
      references SYS_DICTIONARY (DICTIONARY_ID) on delete restrict on update restrict
);

alter table SYS_DICTIONARY_DATA comment '数据值';

/*==============================================================*/
/* Table: SYS_JUDGE_DEVICE                                      */
/*==============================================================*/
create table SYS_JUDGE_DEVICE
(
   JUDGE_DEVICE_ID      bigint not null comment '判图站ID',
   DEVICE_STATUS        varchar(10) comment '设备状态',
   DEVICE_STRATEGY      varchar(10) comment '策略组别（存疑）',
   DEVICE_CHECKER_GENDER varchar(10) comment '查验性别（存疑）',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (JUDGE_DEVICE_ID)
);

alter table SYS_JUDGE_DEVICE comment '判图站';

/*==============================================================*/
/* Table: SYS_JUDGE_GROUP                                       */
/*==============================================================*/
create table SYS_JUDGE_GROUP
(
   JUDGE_GROUP_ID       bigint not null comment '手检站设备组关系ID',
   CONFIG_ID            bigint comment '配置主键',
   JUDGE_DEVICE_ID      bigint comment '判图站ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (JUDGE_GROUP_ID),
   constraint FK_REFERENCE_80 foreign key (CONFIG_ID)
      references SYS_DEVICE_CONFIG (CONFIG_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_81 foreign key (JUDGE_DEVICE_ID)
      references SYS_JUDGE_DEVICE (JUDGE_DEVICE_ID) on delete restrict on update restrict
);

alter table SYS_JUDGE_GROUP comment '安检设备和手检站关系(NEW)';

/*==============================================================*/
/* Table: SYS_MANUAL_DEVICE                                     */
/*==============================================================*/
create table SYS_MANUAL_DEVICE
(
   MANUAL_DEVICE_ID     bigint not null comment '手检站ID',
   DEVICE_STATUS        varchar(10) comment '设备状态',
   DEVICE_STRATEGY      varchar(10) comment '策略组别（存疑）',
   DEVICE_CHECKER_GENDER varchar(10) comment '查验性别（存疑）',
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
   CONFIG_ID            bigint comment '配置主键',
   MANUAL_DEVICE_ID     bigint comment '手检站ID',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (MANUAL_GROUP_ID),
   constraint FK_REFERENCE_7 foreign key (CONFIG_ID)
      references SYS_DEVICE_CONFIG (CONFIG_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_8 foreign key (MANUAL_DEVICE_ID)
      references SYS_MANUAL_DEVICE (MANUAL_DEVICE_ID) on delete restrict on update restrict
);

alter table SYS_MANUAL_GROUP comment '安检设备和手检站关系(NEW)';

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
   DEVICE_ID            bigint comment '设备ID',
   PARAMETER_VALUE      varchar(50) comment '参数值',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (PARAMETER_ID),
   constraint FK_REFERENCE_46 foreign key (PARAMETER_CATEGORY_ID)
      references SYS_PARAMETER_CATEGORY (PARAMETER_CATEGORY_ID) on delete restrict on update restrict
);

alter table SYS_PARAMETER comment '参数';

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
   primary key (RESOURCE_ID),
   constraint FK_REFERENCE_26 foreign key (PARENT_RESOURCE_ID)
      references SYS_RESOURCE (RESOURCE_ID) on delete restrict on update restrict
);

alter table SYS_RESOURCE comment '用户资源';

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
   primary key (ROLE_RESOURCE_ID),
   constraint FK_REFERENCE_27 foreign key (RESOURCE_ID)
      references SYS_RESOURCE (RESOURCE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_28 foreign key (ROLE_ID)
      references SYS_ROLE (ROLE_ID) on delete restrict on update restrict
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
   primary key (ROLE_USER_ID),
   constraint FK_REFERENCE_21 foreign key (ROLE_ID)
      references SYS_ROLE (ROLE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_22 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict
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
   primary key (STRATEGY_ID),
   constraint FK_REFERENCE_6 foreign key (MODE_ID)
      references SYS_WORK_MODE (MODE_ID) on delete restrict on update restrict
);

alter table SYS_STRATEGY comment '分发策略定义';

/*==============================================================*/
/* Table: SYS_USER_GROUP                                        */
/*==============================================================*/
create table SYS_USER_GROUP
(
   USERGROUP_ID         bigint not null comment '用户组ID',
   ORG_ID               bigint comment '机构ID',
   DATA_RANGE_CATEGORY  varchar(10) comment '数据范围类型：默认个人数据范围、隶属机构所有人数、隶属机构及下级机构所有人、所有人数据、自定义数据组',
   GROUP_NUMBER         varchar(50) comment '用户组编号',
   GROUP_NAME           varchar(50) comment '用户组名称',
   GROUP_FLAG           varchar(10) comment '用户组标记(待明确)',
   CREATEDBY            bigint comment '记录创建人',
   CREATEDTIME          timestamp comment '记录创建时间',
   EDITEDBY             bigint comment '记录修改人',
   EDITEDTIME           timestamp comment '记录修改时间',
   NOTE                 varchar(500) comment '备注',
   primary key (USERGROUP_ID),
   constraint FK_REFERENCE_16 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
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
   primary key (LOOKUP_ID),
   constraint FK_REFERENCE_55 foreign key (DATA_GROUP_ID)
      references SYS_DATA_GROUP (DATA_GROUP_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_57 foreign key (USERGROUP_ID)
      references SYS_USER_GROUP (USERGROUP_ID) on delete restrict on update restrict
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
   primary key (USER_GROUP_ROLE_ID),
   constraint FK_REFERENCE_56 foreign key (USERGROUP_ID)
      references SYS_USER_GROUP (USERGROUP_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_58 foreign key (ROLE_ID)
      references SYS_ROLE (ROLE_ID) on delete restrict on update restrict
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
   primary key (USER_ID),
   constraint FK_REFERENCE_18 foreign key (USERGROUP_ID)
      references SYS_USER_GROUP (USERGROUP_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_78 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict
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
   primary key (LOOKUP_ID),
   constraint FK_REFERENCE_53 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_54 foreign key (DATA_GROUP_ID)
      references SYS_DATA_GROUP (DATA_GROUP_ID) on delete restrict on update restrict
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
   primary key (WORKFLOW_ID),
   constraint FK_REFERENCE_5 foreign key (MODE_ID)
      references SYS_WORK_MODE (MODE_ID) on delete restrict on update restrict
);

alter table SYS_WORKFLOW comment '工作流程定义';

