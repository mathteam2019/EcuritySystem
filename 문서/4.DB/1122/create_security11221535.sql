/*==============================================================*/
/* Database name:  DATABASE_1                                   */
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019/11/22 15:44:47                          */
/*==============================================================*/


drop database if exists DATABASE_1;

/*==============================================================*/
/* Database: DATABASE_1                                         */
/*==============================================================*/
create database DATABASE_1;

use DATABASE_1;

/*==============================================================*/
/* Table: SYS_WORK_MODE                                         */
/*==============================================================*/
create table SYS_WORK_MODE
(
   MODE_ID              bigint not null comment '����ģʽID',
   MODE_NAME            varchar(20) comment 'ģʽ����',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (MODE_ID)
);

alter table SYS_WORK_MODE comment '����ģʽ';

/*==============================================================*/
/* Table: SYS_ORG                                               */
/*==============================================================*/
create table SYS_ORG
(
   ORG_ID               bigint not null comment '����ID',
   PARENT_ORG_ID        bigint comment '����ID',
   ORG_NAME             varchar(50) comment '��������',
   ORG_NUMBER           varchar(50) comment '�������',
   LEADER               varchar(50) comment '������',
   MOBILE               varchar(20) comment '��ϵ��ʽ',
   STATUS               varchar(10) comment '��ǰ״̬',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ORG_ID),
   constraint FK_REFERENCE_12 foreign key (PARENT_ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
);

alter table SYS_ORG comment '����';

/*==============================================================*/
/* Table: SYS_FIELD                                             */
/*==============================================================*/
create table SYS_FIELD
(
   FIELD_ID             bigint not null comment '����ID',
   ORG_ID               bigint comment '����ID',
   PARENT_FIELD_ID      bigint comment '����ID',
   FIELD_SERIAL         varchar(50) comment '���ر��',
   FIELD_DESIGNATION    varchar(50) comment '��������',
   LEADER               varchar(50) comment '������(NEW)',
   MOBILE               varchar(50) comment '��ϵ�绰(NEW)',
   FIELD_STATUS         varchar(10) comment '����״̬',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (FIELD_ID),
   constraint FK_REFERENCE_13 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_61 foreign key (PARENT_FIELD_ID)
      references SYS_FIELD (FIELD_ID) on delete restrict on update restrict
);

alter table SYS_FIELD comment '����';

/*==============================================================*/
/* Table: SYS_DEVICE_CONFIG                                     */
/*==============================================================*/
create table SYS_DEVICE_CONFIG
(
   CONFIG_ID            bigint not null comment '��������',
   MODE_ID              bigint comment '����ģʽID',
   FIELD_ID             bigint comment '����ID',
   DEVICE_ID            bigint comment '�������豸ID',
   MANUAL_SWITCH        varchar(10) comment '�Ƿ����ּ칦��',
   ATR_SWITCH           varchar(10) comment 'ATR�����ɴ���',
   MAN_REMOTE_GENDER    varchar(10) comment '����ͼԱ��ͼ����',
   WOMAN_REMOTE_GENDER  varchar(10) comment 'Ů��ͼԱ��ͼ����',
   MAN_MANUAL_GENDER    varchar(10) comment '���ּ�Ա�ּ����',
   WOMAN_MANUAL_GENDER  varchar(10) comment 'Ů�ּ�Ա�ּ����',
   MAN_DEVICE_GENDER    varchar(10) comment '������Ա�������',
   WOMAN_DEVICE_GENDER  varchar(10) comment 'Ů����Ա�������',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (CONFIG_ID),
   constraint FK_REFERENCE_63 foreign key (MODE_ID)
      references SYS_WORK_MODE (MODE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_66 foreign key (FIELD_ID)
      references SYS_FIELD (FIELD_ID) on delete restrict on update restrict
);

alter table SYS_DEVICE_CONFIG comment '��������(NEW)';

/*==============================================================*/
/* Table: FROM_CONFIG_ID                                        */
/*==============================================================*/
create table FROM_CONFIG_ID
(
   FROM_CONFIG_ID       bigint comment '������ԴID',
   CONFIG_ID            bigint not null comment '��������',
   FROM_DEVICE_ID       bigint comment '�������豸ID',
   DEVICE_ID            bigint comment '�������豸ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   constraint FK_REFERENCE_82 foreign key (CONFIG_ID)
      references SYS_DEVICE_CONFIG (CONFIG_ID) on delete restrict on update restrict
);

alter table FROM_CONFIG_ID comment '�豸������Դ��';

/*==============================================================*/
/* Table: SER_ARCHIVES_TEMPLATE                                 */
/*==============================================================*/
create table SER_ARCHIVES_TEMPLATE
(
   ARCHIVES_TEMPLATE_ID bigint not null comment '����ģ��ID',
   S_TEMPLATE_NAME      varchar(50) comment '����ģ������',
   ARCHIVES_TEMPLATE_NUMBER varchar(50) comment '����ģ����',
   CATEGORY_ID          bigint comment '�豸����(NEW)',
   MANUFACTURER         varchar(10) comment '��������',
   ORIGINAL_MODEL       varchar(50) comment 'ԭ���ͺ�',
   STATUS               varchar(10) comment '��ǰ״̬',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ARCHIVES_TEMPLATE_ID)
);

alter table SER_ARCHIVES_TEMPLATE comment '����ģ��';

/*==============================================================*/
/* Table: SER_ARCHIVES                                          */
/*==============================================================*/
create table SER_ARCHIVES
(
   ARCHIVE_ID           bigint not null comment '����ID',
   ARCHIVES_TEMPLATE_ID bigint comment '����ģ��ID',
   ARCHIVES_NAME        varchar(50) comment '��������',
   ARCHIVES_NUMBER      varchar(50) comment '�������',
   CATEGORY_ID          bigint comment '�豸����(NEW)',
   MANUFACTURER         varchar(10) comment '��������',
   ORIGINAL_MODEL       varchar(50) comment 'ԭ���ͺ�',
   STATUS               varchar(10) comment '��ǰ״̬',
   IMAGE_URL            varchar(200) comment 'ͼ���ַ',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ARCHIVE_ID),
   constraint FK_REFERENCE_43 foreign key (ARCHIVES_TEMPLATE_ID)
      references SER_ARCHIVES_TEMPLATE (ARCHIVES_TEMPLATE_ID) on delete restrict on update restrict
);

alter table SER_ARCHIVES comment '��������';

/*==============================================================*/
/* Table: SYS_DEVICE_CATEGORY                                   */
/*==============================================================*/
create table SYS_DEVICE_CATEGORY
(
   CATEGORY_ID          bigint not null comment '��ͼվ���豸���ϵID',
   PARENT_CATEGORY_ID   bigint comment '��ͼվ���豸���ϵID',
   CATEGORY_NUMBER      varchar(50) comment '������',
   CATEGORY_NAME        varchar(50) comment '��������',
   STATUS               varchar(10) comment '�豸״̬��new��',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (CATEGORY_ID),
   constraint FK_REFERENCE_65 foreign key (PARENT_CATEGORY_ID)
      references SYS_DEVICE_CATEGORY (CATEGORY_ID) on delete restrict on update restrict
);

alter table SYS_DEVICE_CATEGORY comment '�豸����(NEW)';

/*==============================================================*/
/* Table: SER_DEVICE_REGISTER                                   */
/*==============================================================*/
create table SER_DEVICE_REGISTER
(
   REGISTER_ID          bigint not null comment 'ע����ϢID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (REGISTER_ID)
);

alter table SER_DEVICE_REGISTER comment '�豸ע����Ϣ';

/*==============================================================*/
/* Table: SYS_DEVICE                                            */
/*==============================================================*/
create table SYS_DEVICE
(
   DEVICE_ID            bigint not null comment '�豸ID',
   DEVICE_NAME          varchar(50) comment '�豸����',
   DEVICE_TYPE          varchar(50) comment '�豸�ͺ�',
   DEVICE_SERIAL        varchar(50) comment '�豸���',
   MANUFACTURER         varchar(10) comment '��������(NEW)',
   ORIGINAL_MODEL       varchar(50) comment 'ԭ���ͺ�(NEW)',
   ORIGINAL_FACTORY_NUMBER varchar(50) comment 'ԭ�����',
   MANUFACTURE_DATE     date comment '��������',
   PURCHASE_DATE        date comment '�ɹ�����',
   SUPPLIER             varchar(50) comment '��Ӧ��(NEW)',
   CONTACTS             varchar(50) comment '��ϵ��(NEW)',
   MOBILE               varchar(50) comment '��ϵ��ʽ(NEW)',
   REGISTRATION_NUMBER  varchar(50) comment '�豸ע���(NEW)',
   IMAGE_URL            varchar(200) comment 'ͼ���ַ(NEW)',
   FIELD_ID             bigint comment '����ID',
   ARCHIVE_ID           bigint comment '����ID',
   CATEGORY_ID          bigint comment '����ID',
   REGISTER_ID          bigint comment 'ע����ϢID',
   DEVICE_DESC          varchar(500) comment '�豸����',
   DEVICE_IP            varchar(20) comment '�豸IP',
   DEVICE_PASSAGEWAY    varchar(50) comment '�豸ͨ��',
   STATUS               varchar(10) comment '��ǰ״̬',
   DEVICE_STRATEGY      varchar(10) comment '������𣨴��ɣ�',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   GUID                 varchar(255) comment '�豸GUID(NEW)',
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

alter table SYS_DEVICE comment '�豸��(NEW)';

/*==============================================================*/
/* Table: SER_TASK                                              */
/*==============================================================*/
create table SER_TASK
(
   TASK_ID              bigint not null comment '����ID',
   DEVICE_ID            bigint comment '�豸ID(�����ǣ�',
   TASK_NUMBER          varchar(50) comment '������',
   MODE_ID              bigint comment 'ģʽID    (new)',
   MODE_NAME            varchar(200) comment 'ģʽ���� (new)',
   TASK_STATUS          varchar(10) comment '״̬��ϵͳ�ֵ�� DICTIONARY_ID=11',
   WORKFLOW_ID          varchar(10) comment '����ID',
   SCENE                varchar(50) comment '�ֳ�',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (TASK_ID),
   constraint FK_REFERENCE_37 foreign key (DEVICE_ID)
      references SYS_DEVICE (DEVICE_ID) on delete restrict on update restrict
);

alter table SER_TASK comment '����';

/*==============================================================*/
/* Table: HISTORY                                               */
/*==============================================================*/
create table HISTORY
(
   HISTORY_ID           bigint not null comment 'ɨ��ID',
   TASK_ID              bigint comment '����ID',
   MODE                 bigint comment '����ģʽ',
   SCAN_ID              bigint comment 'ɨ��ID',
   SCAN_WORKFLOW_ID     bigint comment '����ID',
   SCAN_DEVICE_ID       bigint comment '�豸ID',
   SCAN_IMAGE_ID        bigint comment 'ɨ��ͼ��ID',
   SCAN_ATR_RESULT      varchar(10) comment 'ATR����',
   SCAN_FOOT_ALARM      varchar(10) comment '�Ų�����',
   SCAN_START_TIME      timestamp comment '��ʼʱ��',
   SCAN_END_TIME        timestamp comment '����ʱ��',
   SCAN_POINTSMAN_ID    bigint comment '����ԱID',
   SCAN_POINTSMAN_NAME  varchar(50) comment '����Ա',
   ASSIGNSCAN_ID        bigint comment '���ɻ���ID',
   ASSIGN_WORKFLOW_ID   bigint comment '����ID',
   ASSIGN_USER_ID       bigint comment '�����ˣ�ϵͳĬ��',
   ASSIGN_USER_NAME     varchar(50) comment '����������',
   ASSIGN_JUDGE_DEVICE_ID varchar(10) comment '��ͼվ',
   ASSIGN_HAND_DEVICE_ID varchar(10) comment '�ּ��豸(�����ǻ���ipad��',
   ASSIGN_START_TIME    timestamp comment '��ʼʱ��',
   ASSIGN_END_TIME      timestamp comment '����ʱ��',
   ASSIGN_TIMEOUT       varchar(10) comment '���ɳ�ʱ',
   ASSIGN_STATUS        varchar(10) comment '����״̬',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   JUDGE_ID             bigint comment '��ͼID',
   JUDGE_WORKFLOW_ID    bigint comment '����ID',
   JUDGE_DEVICE_ID      bigint comment '��ͼվ',
   JUDGE_RESULT         varchar(10) comment '��ͼ����',
   JUDGE_TIMEOUT        varchar(10) comment '��ͼ��ʱ',
   HAND_EXAMINATION_ID  bigint comment '�ּ�ID',
   HAND_WORKFLOW_ID     bigint comment '����ID',
   HAND_DEVICE_ID       bigint comment '�豸ID',
   HAND_RESULT          varchar(10) comment '�ּ����',
   HAND_START_TIME      timestamp comment '��ʼʱ��',
   HAND_END_TIME        timestamp comment '����ʱ��',
   HAND_USER_ID         bigint comment '�ּ�Ա',
   HAND_TASK_RESULT     varchar(10) comment '�������',
   HAND_GOODS           varchar(255) comment '�����Ʒ',
   HAND_GOODS_GRADE     varchar(10) comment '�����Ʒ�ȼ�',
   HAND_COLLECT_SIGN    varchar(10) comment '�ղر�ʶ',
   HAND_ATTACHED_ID     bigint comment '����ID',
   HAND_COLLECT_LABEL   varchar(10) comment '�ղر�ǩ',
   HAND_APPRAISE        varchar(10) comment '������ͼ',
   JUDGE_START_TIME     timestamp comment '��ʼʱ��',
   JUDGE_END_TIME       timestamp comment '����ʱ��',
   JUDGE_USER_ID        bigint comment '��ͼԱID',
   JUDGE_ASSIGN_TIMEOUT varchar(10) comment '���ɳ�ʱ',
   JUDGE_STATUS         varchar(10) comment '״̬',
   primary key (HISTORY_ID),
   constraint FK_REFERENCE_79 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table HISTORY comment '������ʷ��';

/*==============================================================*/
/* Table: SER_ARCHIVES_INDICATORS                               */
/*==============================================================*/
create table SER_ARCHIVES_INDICATORS
(
   INDICATORS_ID        bigint not null comment 'ָ��ID',
   ARCHIVES_TEMPLATE_ID bigint comment '����ģ��ID',
   INDICATORS_NAME      varchar(20) comment 'ָ������',
   INDICATORS_UNIT      varchar(20) comment 'ָ�굥λ',
   IS_NULL              varchar(10) comment '�Ƿ����',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (INDICATORS_ID),
   constraint FK_REFERENCE_42 foreign key (ARCHIVES_TEMPLATE_ID)
      references SER_ARCHIVES_TEMPLATE (ARCHIVES_TEMPLATE_ID) on delete restrict on update restrict
);

alter table SER_ARCHIVES_INDICATORS comment 'ģ��ָ��';

/*==============================================================*/
/* Table: SER_ARCHIVES_VALUE                                    */
/*==============================================================*/
create table SER_ARCHIVES_VALUE
(
   VALUE_ID             bigint not null comment 'ֵID',
   INDICATORS_ID        bigint comment 'ָ��ID',
   ARCHIVE_ID           bigint comment '����ID',
   VALUE                varchar(255) comment 'ָ��ֵ��update��',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (VALUE_ID),
   constraint FK_REFERENCE_59 foreign key (INDICATORS_ID)
      references SER_ARCHIVES_INDICATORS (INDICATORS_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_60 foreign key (ARCHIVE_ID)
      references SER_ARCHIVES (ARCHIVE_ID) on delete restrict on update restrict
);

alter table SER_ARCHIVES_VALUE comment '����ָ��ֵ';

/*==============================================================*/
/* Table: SER_ASSIGN                                            */
/*==============================================================*/
create table SER_ASSIGN
(
   ASSIGN_ID            bigint not null comment 'ɨ��ID',
   TASK_ID              bigint comment '����ID',
   WORKFLOW_ID          bigint comment '����ID',
   MODE                 bigint comment '����ģʽ',
   ASSIGN_USER_ID       bigint comment '������ϵͳĬ��',
   ASSIGN_JUDGE_DEVICE_ID varchar(10) comment '��ͼվ',
   ASSIGN_HAND_DEVICE_ID varchar(10) comment '�ּ��豸(�����ǻ���ipad��',
   ASSIGN_START_TIME    timestamp comment '��ʼʱ��',
   ASSIGN_END_TIME      timestamp comment '����ʱ��',
   ASSIGN_TIMEOUT       varchar(10) comment '���ɳ�ʱ    ��ʱ������ʱ        sys_dictionary_data   dictionary_id=17',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ASSIGN_ID),
   constraint FK_REFERENCE_77 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_ASSIGN comment '���ȷ��ɱ�';

/*==============================================================*/
/* Table: SER_CHECK_RESULT                                      */
/*==============================================================*/
create table SER_CHECK_RESULT
(
   CHECK_RESULT_ID      bigint not null comment '���ID',
   DEVICE_ID            bigint comment '�豸ID',
   TASK_ID              bigint not null comment '����ID',
   HAND_TASK_RESULT     varchar(10) comment '�������  �ּ����   TREU���FALSE δ���   sys_device_dictionary_data  dictionary_id=6',
   HAND_GOODS           varchar(255) comment '�����Ʒ �����������ַ����ָ���     ����� sys_dictionary_data   dictionary_id=17 ',
   HAND_GOODS_GRADE     varchar(10) comment '�����Ʒ�ȼ�',
   HAND_COLLECT_SIGN    varchar(10) comment '�ղر�ʶ',
   HAND_ATTACHED_ID     bigint comment '����ID',
   HAND_COLLECT_LABEL   varchar(10) comment '�ղر�ǩ',
   HAND_APPRAISE        varchar(10) comment '������ͼ     ©������    sys_dictionary_data   dictionary_id=17 ',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (CHECK_RESULT_ID),
   constraint FK_REFERENCE_73 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_CHECK_RESULT comment '������';

/*==============================================================*/
/* Table: SER_DEVICE_STATUS                                     */
/*==============================================================*/
create table SER_DEVICE_STATUS
(
   STATUS_ID            bigint not null comment '״̬ID',
   DEVICE_ID            bigint comment '�豸ID',
   FIELD_ID             bigint comment '����ID',
   CURRENT_WORKFLOW     varchar(50) comment '��ǰ����',
   CURRENT_STATUS       varchar(50) comment '����״̬',
   CATEGORY_ID          varchar(50) comment '�豸����(NEW)',
   MANUFACTURER         varchar(50) comment '��������(NEW)',
   ORIGINAL_MODEL       varchar(50) comment 'ԭ���ͺ�(NEW)',
   CHECK_COUNT          int comment '��������',
   PASS_COUNT           int comment 'ͨ������',
   WARNING_COUNT        int comment '��������',
   MANUAL_COUNT         int comment '�ּ�����',
   ACCOUNT              varchar(50) comment '�˺�',
   LOGIN_TIME           timestamp comment '��¼ʱ��',
   IP_ADDRESS           varchar(50) comment 'IP',
   PLC_STATUS           varchar(50) comment 'PLC����״̬',
   MASTER_CARD_STATUS   varchar(50) comment '���ɼ���״̬',
   SLAVE_CARD_STATUS    varchar(50) comment '�Ӳɼ���״̬',
   SERVO                varchar(50) comment '�ŷ�',
   SLIDE_POSITION       varchar(50) comment '����λ��',
   EMERGENCY_STOP       varchar(50) comment '��ͣ',
   FOOT_WARNING         varchar(50) comment '�Ų�����',
   DISK_SPACE           varchar(50) comment '���̿ռ�',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (STATUS_ID),
   constraint FK_REFERENCE_67 foreign key (DEVICE_ID)
      references SYS_DEVICE (DEVICE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_68 foreign key (FIELD_ID)
      references SYS_FIELD (FIELD_ID) on delete restrict on update restrict
);

alter table SER_DEVICE_STATUS comment '�豸״̬';

/*==============================================================*/
/* Table: SER_DEVICE_VALUE                                      */
/*==============================================================*/
create table SER_DEVICE_VALUE
(
   DEVICE_ID            bigint comment '�豸ID',
   INDICATORS_ID        bigint comment 'ָ��ID',
   ARCHIVE_ID           bigint comment '����ID',
   VALUE_ID             bigint,
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   constraint FK_REFERENCE_62 foreign key (DEVICE_ID)
      references SYS_DEVICE (DEVICE_ID) on delete restrict on update restrict
);

alter table SER_DEVICE_VALUE comment '�豸ָ��ֵ';

/*==============================================================*/
/* Table: SER_DEV_LOG                                           */
/*==============================================================*/
create table SER_DEV_LOG
(
   ID                   bigint not null comment '����ID',
   GUID                 varchar(255) comment '�豸',
   DEV_TYPE             varchar(10) comment '��־�豸����',
   LOGIN_NAME           varchar(200) comment '�˺�',
   CATEGORY             tinyint(2) comment '���',
   LEVEL                int comment '����',
   CONTENT              varchar(2000) comment '����',
   TIME                 varchar(255) comment '����ʱ��',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ID)
);

alter table SER_DEV_LOG comment '�豸������־';

/*==============================================================*/
/* Table: SER_HAND_EXAMINATION                                  */
/*==============================================================*/
create table SER_HAND_EXAMINATION
(
   HAND_EXAMINATION_ID  bigint not null comment '�ּ�ID',
   TASK_ID              bigint comment '����ID',
   WORKFLOW_ID          bigint comment '����ID',
   HAND_DEVICE_ID       bigint comment '�豸ID',
   HAND_RESULT          varchar(10) comment '�ּ����   TREU���FALSE δ���   sys_device_dictionary_data  dictionary_id=6',
   HAND_START_TIME      timestamp comment '��ʼʱ��',
   HAND_END_TIME        timestamp comment '����ʱ��',
   HAND_USER_ID         bigint comment '�ּ�Ա',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (HAND_EXAMINATION_ID),
   constraint FK_REFERENCE_69 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_HAND_EXAMINATION comment '�ּ����� ';

/*==============================================================*/
/* Table: SER_SCAN                                              */
/*==============================================================*/
create table SER_SCAN
(
   SCAN_ID              bigint not null comment 'ɨ��ID',
   TASK_ID              bigint comment '����ID',
   WORKFLOW_ID          bigint comment '����ID',
   SCAN_DEVICE_ID       bigint comment '�豸ID',
   SCAN_IMAGE_ID        bigint comment 'ɨ��ͼ��ID',
   SCAN_ATR_RESULT      varchar(10) comment 'ATR����      TRUE������FALSEͨ��   sys_device_dictionary_data  dictionary_id=8',
   SCAN_INVALID         varchar(10) comment 'true��Чɨ��\false ��Чɨ��  sys_device_dictionary_data  dictionary_id=4',
   SCAN_FOOT_ALARM      varchar(10) comment '�Ų�����    TRUE������FALSE������  sys_device_dictionary_data  dictionary_id=9',
   SCAN_START_TIME      timestamp comment '��ʼʱ��',
   SCAN_END_TIME        timestamp comment '����ʱ��',
   SCAN_POINTSMAN_ID    bigint comment '����ԱID',
   SCAN_ASSIGN_TIMEOUT  varchar(10) comment '���ɳ�ʱ',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (SCAN_ID),
   constraint FK_REFERENCE_75 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_SCAN comment '������ɨ�����ݱ�';

/*==============================================================*/
/* Table: SER_IMAGE                                             */
/*==============================================================*/
create table SER_IMAGE
(
   IMAGE_ID             bigint not null comment 'ͼ��ID',
   SCAN_ID              bigint comment 'ɨ��ID',
   TASK_ID              bigint not null comment '����ID',
   IMAGE_FORMAT         varchar(10) comment 'ͼ���ʽ(ԭʼͼ��jpg...)',
   IMAGE_CATEGORY       varchar(10) comment 'ͼ����𣨶Աȶȡ���ɫ�ȣ�',
   IMAGE_URL            varchar(200) comment 'ͼ���ַ',
   IMAGE_LABEL          varchar(50) comment 'ͼ���ǩ',
   STATUS               varchar(10) comment '��ǰ״̬',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (IMAGE_ID),
   constraint FK_REFERENCE_86 foreign key (SCAN_ID)
      references SER_SCAN (SCAN_ID) on delete restrict on update restrict
);

alter table SER_IMAGE comment 'ͼ��';

/*==============================================================*/
/* Table: SER_IMAGE_PROPERTY                                    */
/*==============================================================*/
create table SER_IMAGE_PROPERTY
(
   IMAGE_PROPERTY_ID    bigint not null comment '����ID',
   IMAGE_ID             bigint comment 'ͼ��ID',
   IMAGE_PROPERTY_NAME  varchar(50) comment '��������',
   IMAGE_PROPERTY_VALUE varchar(50) comment '����ֵ',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (IMAGE_PROPERTY_ID),
   constraint FK_REFERENCE_74 foreign key (IMAGE_ID)
      references SER_IMAGE (IMAGE_ID) on delete restrict on update restrict
);

alter table SER_IMAGE_PROPERTY comment 'ͼ������';

/*==============================================================*/
/* Table: SER_INSPECTED                                         */
/*==============================================================*/
create table SER_INSPECTED
(
   INSPECTED_ID         bigint not null comment '��������ID',
   TASK_ID              bigint not null comment '����ID',
   SUSPECT_NAME         varchar(50) comment '����',
   IDENTITY_CARD        varchar(50) comment '֤����',
   IDENTITY_CATEGORY    varchar(10) comment '֤������',
   GENDER               varchar(10) comment '�Ա�',
   CHECK_LEVE           varchar(10) comment '����ȼ�',
   ACTIVATE_STATUS      varchar(10) comment '����״̬',
   CHECK_RESULT         varchar(10) comment '������',
   CHECK_DESC           varchar(500) comment '��������',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (INSPECTED_ID),
   constraint FK_REFERENCE_76 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_INSPECTED comment '��������';

/*==============================================================*/
/* Table: SER_JUDGE_GRAPH                                       */
/*==============================================================*/
create table SER_JUDGE_GRAPH
(
   JUDGE_ID             bigint not null comment '��ͼID',
   TASK_ID              bigint comment '����ID',
   WORKFLOW_ID          bigint comment '����ID',
   JUDGE_DEVICE_ID      bigint comment '��ͼվ',
   JUDGE_RESULT         varchar(10) comment '��ͼ����   ������true��������false     sys_device_dictionary_data     dictionary_id=5',
   JUDGE_TIMEOUT        varchar(10) comment '��ͼ��ʱ    ��ʱ������ʱ        sys_dictionary_data   dictionary_id=17',
   JUDGE_SUBMITRECTS    varchar(255) comment '��ͼ�ύ���ɿ�  new��json�ַ�����������ӵ����ɿ��ɾ�������ɿ�',
   JUDGE_TIME           varchar(255) comment '��ͼʱ��',
   JUDGE_START_TIME     timestamp comment '��ʼʱ��',
   JUDGE_END_TIME       timestamp comment '����ʱ��',
   JUDGE_USER_ID        bigint comment '��ͼԱID����ʱϵͳ��ͼдsystem�û���',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (JUDGE_ID),
   constraint FK_REFERENCE_72 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_JUDGE_GRAPH comment '��ͼ����';

/*==============================================================*/
/* Table: SER_KNOWLEDGE_CASE_DEAL                               */
/*==============================================================*/
create table SER_KNOWLEDGE_CASE_DEAL
(
   CASE_DETAL_ID        bigint not null comment 'ɨ��ID',
   CASE_ID              bigint comment '֪ʶ��id',
   TASK_ID              bigint comment '����ID',
   MODE                 bigint comment '����ģʽ',
   SCAN_ID              bigint comment 'ɨ��ID',
   SCAN_WORKFLOW_ID     bigint comment '����ID',
   SCAN_DEVICE_ID       bigint comment '�豸ID',
   SCAN_IMAGE_ID        bigint comment 'ɨ��ͼ��ID',
   SCAN_ATR_RESULT      varchar(10) comment 'ATR����',
   SCAN_FOOT_ALARM      varchar(10) comment '�Ų�����',
   SCAN_START_TIME      timestamp comment '��ʼʱ��',
   SCAN_END_TIME        timestamp comment '����ʱ��',
   SCAN_POINTSMAN_ID    bigint comment '����ԱID',
   SCAN_POINTSMAN_NAME  varchar(50) comment '����Ա',
   ASSIGNSCAN_ID        bigint comment '���ɻ���ID',
   ASSIGN_WORKFLOW_ID   bigint comment '����ID',
   ASSIGN_USER_ID       bigint comment '�����ˣ�ϵͳĬ��',
   ASSIGN_USER_NAME     varchar(50) comment '����������',
   ASSIGN_JUDGE_DEVICE_ID varchar(10) comment '��ͼվ',
   ASSIGN_HAND_DEVICE_ID varchar(10) comment '�ּ��豸(�����ǻ���ipad��',
   ASSIGN_START_TIME    timestamp comment '��ʼʱ��',
   ASSIGN_END_TIME      timestamp comment '����ʱ��',
   ASSIGN_TIMEOUT       varchar(10) comment '���ɳ�ʱ',
   ASSIGN_STATUS        varchar(10) comment '����״̬',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   JUDGE_ID             bigint comment '��ͼID',
   JUDGE_WORKFLOW_ID    bigint comment '����ID',
   JUDGE_DEVICE_ID      bigint comment '��ͼվ',
   JUDGE_RESULT         varchar(10) comment '��ͼ����',
   JUDGE_TIMEOUT        varchar(10) comment '��ͼ��ʱ',
   HAND_EXAMINATION_ID  bigint comment '�ּ�ID',
   HAND_WORKFLOW_ID     bigint comment '����ID',
   HAND_DEVICE_ID       bigint comment '�豸ID',
   HAND_RESULT          varchar(10) comment '�ּ����',
   HAND_START_TIME      timestamp comment '��ʼʱ��',
   HAND_END_TIME        timestamp comment '����ʱ��',
   HAND_USER_ID         bigint comment '�ּ�Ա',
   HAND_TASK_RESULT     varchar(10) comment '�������',
   HAND_GOODS           varchar(255) comment '�����Ʒ',
   HAND_GOODS_GRADE     varchar(10) comment '�����Ʒ�ȼ�',
   HAND_COLLECT_SIGN    varchar(10) comment '�ղر�ʶ',
   HAND_ATTACHED_ID     bigint comment '����ID',
   HAND_COLLECT_LABEL   varchar(10) comment '�ղر�ǩ',
   HAND_APPRAISE        varchar(10) comment '������ͼ',
   JUDGE_START_TIME     timestamp comment '��ʼʱ��',
   JUDGE_END_TIME       timestamp comment '����ʱ��',
   JUDGE_USER_ID        bigint comment '��ͼԱID',
   JUDGE_ASSIGN_TIMEOUT varchar(10) comment '���ɳ�ʱ',
   JUDGE_STATUS         varchar(10) comment '״̬',
   primary key (CASE_DETAL_ID)
);

alter table SER_KNOWLEDGE_CASE_DEAL comment '֪ʶ�������';

/*==============================================================*/
/* Table: SER_KNOWLEDGE_CASE                                    */
/*==============================================================*/
create table SER_KNOWLEDGE_CASE
(
   CASE_ID              bigint not null comment '֪ʶ��ID',
   TASK_ID              bigint comment '����ID',
   CASE_DETAL_ID        bigint comment 'ɨ��ID',
   CASE_STATUS          varchar(10) comment '״̬: 1�ύ���� 2������ 3�������ɹ�',
   CASE_COLLECT_USERID  bigint comment '�ղ���',
   CASE_APPROVAL_USERID bigint comment '������',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (CASE_ID),
   constraint FK_REFERENCE_71 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_89 foreign key (CASE_DETAL_ID)
      references SER_KNOWLEDGE_CASE_DEAL (CASE_DETAL_ID) on delete restrict on update restrict
);

alter table SER_KNOWLEDGE_CASE comment '֪ʶ����Ա����';

/*==============================================================*/
/* Table: SER_LOG                                               */
/*==============================================================*/
create table SER_LOG
(
   LOG_ID               bigint not null comment '��־ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (LOG_ID)
);

alter table SER_LOG comment '��־';

/*==============================================================*/
/* Table: SYS_USER                                              */
/*==============================================================*/
create table SYS_USER
(
   USER_ID              bigint not null comment '�û�ID',
   ORG_ID               bigint comment '����ID',
   USER_NAME            varchar(50) comment '�û�����',
   USER_ACCOUNT         varchar(20) comment '�ʺ�',
   PASSWORD             varchar(20) comment '����',
   USER_NUMBER          varchar(50) comment '�û����',
   DATA_RANGE_CATEGORY  varchar(10) comment '���ݷ�Χ���ͣ�Ĭ�ϸ������ݷ�Χ�������������������������������¼����������ˡ����������ݡ��Զ���������',
   GENDER               varchar(10) comment '�Ա�',
   IDENTITY_CARD        varchar(50) comment '֤����',
   POST                 varchar(50) comment '��λ',
   EDUCATION            varchar(10) comment 'ѧ��',
   DEGREE               varchar(10) comment 'ѧλ',
   EMAIL                varchar(50) comment '����',
   MOBILE               varchar(20) comment '��ϵ�绰',
   ADDRESS              varchar(50) comment '��ͥסַ',
   CATEGORY             varchar(10) comment '��Ա���(�Ƿ�ʹ�ô���)',
   STATUS               varchar(10) comment '��ǰ״̬',
   PORTRAIT             varchar(200) comment '��Ƭ',
   TASK_ID              bigint comment '����ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (USER_ID),
   constraint FK_REFERENCE_52 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
);

alter table SYS_USER comment '�û�';

/*==============================================================*/
/* Table: SER_LOGIN_INFO                                        */
/*==============================================================*/
create table SER_LOGIN_INFO
(
   LOGIN_INFO_ID        bigint not null comment '��¼��ϢID',
   USER_ID              bigint comment '�û�ID',
   DEVICE_ID            bigint comment '�豸ID',
   LOGIN_CATEGORY       varchar(10) comment '��¼����',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (LOGIN_INFO_ID),
   constraint FK_REFERENCE_51 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict
);

alter table SER_LOGIN_INFO comment '��¼��Ϣ';

/*==============================================================*/
/* Table: SER_MAINTAIN                                          */
/*==============================================================*/
create table SER_MAINTAIN
(
   MAINTAIN_ID          bigint not null comment 'ά����¼ID',
   MAINTAIN_NUMBER      varchar(50) comment 'ά������',
   MAINTAIN_NAME        varchar(50) comment 'ά������',
   MAINTAIN_DESC        varchar(500) comment '����',
   DEVICE_TYPE          varchar(10) comment '�豸����',
   DEVICE_POSITION      varchar(50) comment '�豸λ��',
   MAINTAIN_PERIOD      varchar(50) comment 'ִ��ʱ�������',
   STATUS               varchar(10) comment '����״̬',
   MAINTAIN_CATEGORY    varchar(10) comment 'ά������',
   MAINTAINER_NAME      varchar(50) comment 'ά����Ա����',
   MAINTAIN_TIME        timestamp comment 'ά��ʱ��',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (MAINTAIN_ID)
);

alter table SER_MAINTAIN comment 'ά����¼';

/*==============================================================*/
/* Table: SER_MAINTAIN_ATTACHMENT                               */
/*==============================================================*/
create table SER_MAINTAIN_ATTACHMENT
(
   ATTACHMENT_ID        bigint not null comment '����ID',
   MAINTAIN_ID          bigint comment 'ά����¼ID',
   ATTACHMENT_NAME      varchar(50) comment '��������',
   ATTACHEMTN_URL       varchar(200) comment '������ַ',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ATTACHMENT_ID),
   constraint FK_REFERENCE_50 foreign key (MAINTAIN_ID)
      references SER_MAINTAIN (MAINTAIN_ID) on delete restrict on update restrict
);

alter table SER_MAINTAIN_ATTACHMENT comment 'ά����¼����';

/*==============================================================*/
/* Table: SER_PLATFORM_CHECK_PARAMS                             */
/*==============================================================*/
create table SER_PLATFORM_CHECK_PARAMS
(
   SCAN_ID              bigint not null,
   SCAN_RECOGNISE_COLOUR varchar(255) comment '��ɫ��ѡ��ATRʶ������ɿ���ɫ��֧����ɫ���ѡ���RGBֵ������',
   SCAN_OVERTIME        int comment '����0-600֮������֣���λ����',
   JUDGE_ASSIGN_TIME    int comment '0-30֮������֣���λ��',
   JUDGE_PROCESSING_TIME int comment '0-30֮������֣���λ��',
   JUDGE_SCAN_OVERTIME  int comment '����0-600֮������֣���λ����',
   JUDGE_RECOGNISE_COLOUR varchar(255) comment '��ɫ��ѡ����ͼ�����ɿ���ɫ��֧����ɫ���ѡ���RGBֵ������',
   HAND_OVERTIME        int comment '����0-600֮������֣���λ����',
   HAND_RECOGNISE_COLOUR varchar(255) comment '��ɫ��ѡ���ּ�����ɿ���ɫ��֧����ɫ���ѡ���RGBֵ�����룬Ԥ��',
   HISTORY_DATA_STORAGE varchar(255) comment '��ѡ��ҵ�����ݣ�Ĭ�ϱ�ѡ������ͨͼ��ת��ͼ��ԭʼͼ',
   HISTORY_DATA_EXPORT  varchar(255) comment '��ѡ��ҵ�����ݣ�Ĭ�ϱ�ѡ������ͨͼ��ת��ͼ��ԭʼͼ',
   DISPLAY_DELETE_SUSPICION varchar(10) comment '��ʾ��ɾ�������ɿ�;��Χ �ּ졢����������ʷ����',
   DISPLAY_DELETE_SUSPICION_COLOUR varchar(255) comment '��ɫ��ѡ��;��Χ �ּ졢����������ʷ�����ּ졢����������ʷ����',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (SCAN_ID)
);

alter table SER_PLATFORM_CHECK_PARAMS comment 'ϵͳ����-ƽ̨������Ա����';

/*==============================================================*/
/* Table: SER_PLATFORM_OTHER_PARAMS                             */
/*==============================================================*/
create table SER_PLATFORM_OTHER_PARAMS
(
   INITIAL_PASSWORD     varchar(255) comment '6-20λ����������ϵͳ�û�ͳһ�ĳ�ʼ����',
   LOGIN_NUMBER         int comment '0-8֮������֣�0��ʾ������',
   LOG_MAX_NUMBER       int comment '�趨��־�����������������ʱ������ϵͳ��ʾ������Ԥ��������������������Ӱ��ϵͳ���ܣ��Ƿ������',
   DEVICE_TRAFFIC_SETTINGS int comment '�͡��С���',
   STORAGE_DETECTION_CYCLE int comment '0-600֮������֣���λ����',
   STORAGE_ALARM        int comment '0-1000֮������֣���λG',
   HISTORY_DATA_CYCLE   int comment '0-5000֮������֣���λ��'
);

alter table SER_PLATFORM_OTHER_PARAMS comment 'ϵͳ����-ƽ̨��������';

/*==============================================================*/
/* Table: SER_SCAN_PARAMS                                       */
/*==============================================================*/
create table SER_SCAN_PARAMS
(
   SCAN_PARAMS_ID       bigint not null comment '����ID',
   DEV_ID               bigint comment '�豸ID',
   AIRCALIWARNTIME      int comment '120:  0-600֮������֣���λ����',
   STANDBYTIME          int comment '0 �豸�˵�½���޲���������ʱ�䣬�˻ص�½���档0-600֮������֣���λ����',
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
   ATUOCONFIG           varchar(10) comment '��',
   DICTIONARY_NAME      varchar(50) comment '�����ֵ�����',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (SCAN_PARAMS_ID)
);

alter table SER_SCAN_PARAMS comment 'ϵͳ����-������';

/*==============================================================*/
/* Table: SER_SCAN_PARAMS_FROM                                  */
/*==============================================================*/
create table SER_SCAN_PARAMS_FROM
(
   FROM_CONFIG_ID       bigint comment '������ԴID',
   SCAN_PARAMS_ID       bigint not null comment '�����ǲ���ID',
   FROM_DEVICE_ID       bigint comment '�������豸ID',
   DEVICE_ID            bigint comment '�������豸ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   constraint FK_REFERENCE_84 foreign key (SCAN_PARAMS_ID)
      references SER_SCAN_PARAMS (SCAN_PARAMS_ID) on delete restrict on update restrict
);

alter table SER_SCAN_PARAMS_FROM comment '�����ǲ���������Դ��';

/*==============================================================*/
/* Table: SER_SEIZED_GOODS                                      */
/*==============================================================*/
create table SER_SEIZED_GOODS
(
   GOODS_ID             bigint not null comment 'GOODS_ID',
   SEIZED_GOODS         varchar(255) comment '�����Ʒ   sys_dictionary_data  DICTIONARY_ID=16',
   SEIZED_GOODS_TYPE    varchar(10) comment '��Ʒ����    sys_dictionary_data  DICTIONARY_ID=14',
   SEIZED_GOODS_LEVEL   varchar(10) comment '��Ʒ����   sys_dictionary_data   DICTIONARY_ID=15',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (GOODS_ID)
);

alter table SER_SEIZED_GOODS comment '����� new 1121';

/*==============================================================*/
/* Table: SER_TASK_STEP                                         */
/*==============================================================*/
create table SER_TASK_STEP
(
   STEP_ID              bigint not null comment '����ID',
   TASK_ID              bigint comment '����ID',
   WORKFLOW_ID          bigint not null comment '��������ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (STEP_ID),
   constraint FK_REFERENCE_33 foreign key (TASK_ID)
      references SER_TASK (TASK_ID) on delete restrict on update restrict
);

alter table SER_TASK_STEP comment '����ִ�в���';

/*==============================================================*/
/* Table: SER_TIMING_TASK                                       */
/*==============================================================*/
create table SER_TIMING_TASK
(
   TIMING_TASK_ID       bigint not null comment '��ʱ�����¼ID',
   TASK_NUMBER          varchar(50) comment '��ʱ������',
   SCHEDULED_TIME       timestamp comment 'Ԥ�ƿ�ʼʱ��',
   TASK_COUNT           int comment 'ִ�д���',
   TIMER_START          timestamp comment '��ʱ��Ч��ʼʱ��',
   TIMER_END            timestamp comment '��ʱ��Ч����ʱ��',
   STATUS               varchar(10) comment '��ǰ״̬',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (TIMING_TASK_ID)
);

alter table SER_TIMING_TASK comment '��ʱ�������ͼ�¼';

/*==============================================================*/
/* Table: SYS_ACCESS_LOG                                        */
/*==============================================================*/
create table SYS_ACCESS_LOG
(
   ID                   bigint comment '����ID',
   OPERATE_TIME         datetime comment '����ʱ��',
   CLIENT_IP            varchar(255) comment '�ͻ���IP',
   OPERATE_ID           bigint comment '����ԱID',
   OPERATE_ACCOUNT      varchar(200) comment '����Ա��',
   ACTION               varchar(10) comment 'login/logout/kickout/timeout',
   OPERATE_RESULT       varchar(10) comment '�ɹ�  \ʧ��',
   REASON_CODE          varchar(255) comment 'ʧ��ԭ�����',
   ONLINE_TIME          int comment '����ʱ��(��)',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע'
);

alter table SYS_ACCESS_LOG comment 'ϵͳ������־';

/*==============================================================*/
/* Table: SYS_ROLE_ACCESS                                       */
/*==============================================================*/
create table SYS_ROLE_ACCESS
(
   ACCESS_ID            bigint not null comment '����Ȩ��ID',
   DEVICE_ID            varchar(50) comment '1.ȡUUID
            2.UUID�������豸�ˣ������ּ�ˡ���ͼ��',
   DEVICE_TYPE          varchar(10) comment 'ȡ�����ֵ�',
   ACCESS_NAME          varchar(20) comment '����Ȩ������',
   ACCESS_CATEGORY      varchar(50) comment 'Ȩ������',
   ACCESS_CATEGORY_CODE varchar(10) comment 'Ȩ�����ͱ���',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ACCESS_ID)
);

alter table SYS_ROLE_ACCESS comment '�û�����Ȩ��;�˱����������ֵ�ʵ��';

/*==============================================================*/
/* Table: SYS_ROLE                                              */
/*==============================================================*/
create table SYS_ROLE
(
   ROLE_ID              bigint not null comment '�û���ɫID',
   ORG_ID               bigint comment '����ID',
   ROLE_NUMBER          varchar(50) comment '��ɫ���',
   ROLE_NAME            varchar(50) comment '��ɫ����',
   ROLE_FLAG            varchar(10) comment '��ɫ���',
   STATUS               varchar(10) comment '��ǰ״̬',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ROLE_ID),
   constraint FK_REFERENCE_20 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
);

alter table SYS_ROLE comment '�û���ɫ';

/*==============================================================*/
/* Table: SYS_ACCESS_ROLE                                       */
/*==============================================================*/
create table SYS_ACCESS_ROLE
(
   ACCESS_USER_ID       bigint not null comment '�û�ӵ�з���Ȩ��ID',
   ACCESS_ID            bigint comment '����Ȩ��ID',
   ROLE_ID              bigint comment '�û���ɫID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ACCESS_USER_ID),
   constraint FK_REFERENCE_40 foreign key (ACCESS_ID)
      references SYS_ROLE_ACCESS (ACCESS_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_49 foreign key (ROLE_ID)
      references SYS_ROLE (ROLE_ID) on delete restrict on update restrict
);

alter table SYS_ACCESS_ROLE comment '�û�ӵ�е�Ȩ��';

/*==============================================================*/
/* Table: SYS_AUDIT_LOG                                         */
/*==============================================================*/
create table SYS_AUDIT_LOG
(
   ID                   bigint not null comment '����ID',
   OPERATE_TIME         datetime not null comment '����ʱ��',
   CLIENT_IP            varchar(255) comment '�ͻ���IP',
   OPERATOR_ID          bigint comment '����ԱID',
   OPERATE_ACCOUNT      varchar(200) comment '����Ա��',
   OPERATE_OBJECT       varchar(255) comment '��������',
   ACTION               varchar(10) comment '����',
   OPERATE_RESULT       varchar(10) comment '�������',
   OPERATE_CONTENT      varchar(255) comment '��������',
   REASON_CODE          varchar(255) comment 'ʧ��ԭ�����',
   ONLINE_TIME          int comment '����ʱ��(��)',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ID)
);

alter table SYS_AUDIT_LOG comment '������־';

/*==============================================================*/
/* Table: SYS_AUDIT_LOG_DETAIL                                  */
/*==============================================================*/
create table SYS_AUDIT_LOG_DETAIL
(
   ID                   bigint comment '����ID',
   AUDIT_LOG_ID         bigint comment '������־ID',
   FIELD_NAME           varchar(255) comment '������',
   VALUE_BEFORE         varchar(2000) comment '����ǰ����',
   VALUE_AFTER          varchar(2000) comment '����������',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   constraint FK_REFERENCE_83 foreign key (ID)
      references SYS_AUDIT_LOG (ID) on delete restrict on update restrict
);

alter table SYS_AUDIT_LOG_DETAIL comment '��־��ϸ��Ϣ';

/*==============================================================*/
/* Table: SYS_BLACKLIST                                         */
/*==============================================================*/
create table SYS_BLACKLIST
(
   BLACKLIST_ID         bigint not null comment '������ID',
   SUSPECT_NAME         varchar(50) comment '����',
   IDENTITY_CARD        varchar(50) comment '֤����',
   IDENTITY_CATEGORY    varchar(10) comment '֤������',
   GENDER               varchar(10) comment '�Ա�',
   CHECK_LEVE           varchar(10) comment '����ȼ�',
   ACTIVATE_STATUS      varchar(10) comment '����״̬',
   INSPECTED_ID         bigint not null comment '��������ID',
   CHECK_DESC           varchar(500) comment '��������',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (BLACKLIST_ID)
);

alter table SYS_BLACKLIST comment '������';

/*==============================================================*/
/* Table: SYS_DATA_GROUP                                        */
/*==============================================================*/
create table SYS_DATA_GROUP
(
   DATA_GROUP_ID        bigint not null comment '������ID',
   ORG_ID               bigint comment '����ID',
   DATA_GROUP_NUMBER    varchar(50) comment '��������(NEW)',
   DATA_GROUP_NAME      varchar(50) comment '����������',
   DATA_GROUP_FLAG      varchar(10) comment '��������',
   STATUS               varchar(10) comment '��ǰ״̬',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (DATA_GROUP_ID),
   constraint FK_REFERENCE_23 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
);

alter table SYS_DATA_GROUP comment '������';

/*==============================================================*/
/* Table: SYS_DATA_GROUP_USER                                   */
/*==============================================================*/
create table SYS_DATA_GROUP_USER
(
   DATA_GROUP_USER_ID   bigint not null comment '�û�����������ID',
   USER_ID              bigint comment '�û�ID',
   DATA_GROUP_ID        bigint comment '������ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (DATA_GROUP_USER_ID),
   constraint FK_REFERENCE_24 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_25 foreign key (DATA_GROUP_ID)
      references SYS_DATA_GROUP (DATA_GROUP_ID) on delete restrict on update restrict
);

alter table SYS_DATA_GROUP_USER comment '�û�����������';

/*==============================================================*/
/* Table: SYS_DEVICE_USER                                       */
/*==============================================================*/
create table SYS_DEVICE_USER
(
   DEVICE_USER_ID       bigint not null comment '�豸���û���ϵID',
   DEVICE_ID            bigint comment '�豸ID',
   USER_ID              bigint comment '�û�ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (DEVICE_USER_ID),
   constraint FK_REFERENCE_14 foreign key (DEVICE_ID)
      references SYS_DEVICE (DEVICE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_15 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict
);

alter table SYS_DEVICE_USER comment '�豸���û�������ϵ';

/*==============================================================*/
/* Table: SYS_DICTIONARY                                        */
/*==============================================================*/
create table SYS_DICTIONARY
(
   DICTIONARY_ID        bigint not null comment '�����ֵ�ID',
   DICTIONARY_NAME      varchar(50) comment '�����ֵ�����',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (DICTIONARY_ID)
);

alter table SYS_DICTIONARY comment '�����ֵ�';

/*==============================================================*/
/* Table: SYS_DICTIONARY_DATA                                   */
/*==============================================================*/
create table SYS_DICTIONARY_DATA
(
   DATA_ID              bigint not null,
   DICTIONARY_ID        bigint comment '�����ֵ�ID',
   DATA_CODE            varchar(10) comment '���ݱ���',
   DATA_VALUE           varchar(200) comment '����ֵ',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (DATA_ID),
   constraint FK_REFERENCE_41 foreign key (DICTIONARY_ID)
      references SYS_DICTIONARY (DICTIONARY_ID) on delete restrict on update restrict
);

alter table SYS_DICTIONARY_DATA comment '����ֵ';

/*==============================================================*/
/* Table: SYS_JUDGE_DEVICE                                      */
/*==============================================================*/
create table SYS_JUDGE_DEVICE
(
   JUDGE_DEVICE_ID      bigint not null comment '��ͼվID',
   DEVICE_STATUS        varchar(10) comment '�豸״̬',
   DEVICE_STRATEGY      varchar(10) comment '������𣨴��ɣ�',
   DEVICE_CHECKER_GENDER varchar(10) comment '�����Ա𣨴��ɣ�',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (JUDGE_DEVICE_ID)
);

alter table SYS_JUDGE_DEVICE comment '��ͼվ';

/*==============================================================*/
/* Table: SYS_JUDGE_GROUP                                       */
/*==============================================================*/
create table SYS_JUDGE_GROUP
(
   JUDGE_GROUP_ID       bigint not null comment '�ּ�վ�豸���ϵID',
   CONFIG_ID            bigint comment '��������',
   JUDGE_DEVICE_ID      bigint comment '��ͼվID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (JUDGE_GROUP_ID),
   constraint FK_REFERENCE_80 foreign key (CONFIG_ID)
      references SYS_DEVICE_CONFIG (CONFIG_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_81 foreign key (JUDGE_DEVICE_ID)
      references SYS_JUDGE_DEVICE (JUDGE_DEVICE_ID) on delete restrict on update restrict
);

alter table SYS_JUDGE_GROUP comment '�����豸���ּ�վ��ϵ(NEW)';

/*==============================================================*/
/* Table: SYS_MANUAL_DEVICE                                     */
/*==============================================================*/
create table SYS_MANUAL_DEVICE
(
   MANUAL_DEVICE_ID     bigint not null comment '�ּ�վID',
   DEVICE_STATUS        varchar(10) comment '�豸״̬',
   DEVICE_STRATEGY      varchar(10) comment '������𣨴��ɣ�',
   DEVICE_CHECKER_GENDER varchar(10) comment '�����Ա𣨴��ɣ�',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (MANUAL_DEVICE_ID)
);

alter table SYS_MANUAL_DEVICE comment '�ּ�վ';

/*==============================================================*/
/* Table: SYS_MANUAL_GROUP                                      */
/*==============================================================*/
create table SYS_MANUAL_GROUP
(
   MANUAL_GROUP_ID      bigint not null comment '�ּ�վ�豸���ϵID',
   CONFIG_ID            bigint comment '��������',
   MANUAL_DEVICE_ID     bigint comment '�ּ�վID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (MANUAL_GROUP_ID),
   constraint FK_REFERENCE_7 foreign key (CONFIG_ID)
      references SYS_DEVICE_CONFIG (CONFIG_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_8 foreign key (MANUAL_DEVICE_ID)
      references SYS_MANUAL_DEVICE (MANUAL_DEVICE_ID) on delete restrict on update restrict
);

alter table SYS_MANUAL_GROUP comment '�����豸���ּ�վ��ϵ(NEW)';

/*==============================================================*/
/* Table: SYS_PARAMETER_CATEGORY                                */
/*==============================================================*/
create table SYS_PARAMETER_CATEGORY
(
   PARAMETER_CATEGORY_ID bigint not null comment '����ID',
   PARAMETER_CATEGORY_NAME varchar(50) comment '��������',
   PARAMETER_CATEGORY_CAPTION varchar(50) comment '������ʾ����',
   PARAMETER_CATEGORY   varchar(10) comment '��������',
   DEVICE_CATEGORY      varchar(10) comment '�豸����',
   PARAMETER_VALUE      varchar(50) comment '����ֵ',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (PARAMETER_CATEGORY_ID)
);

alter table SYS_PARAMETER_CATEGORY comment '��������';

/*==============================================================*/
/* Table: SYS_PARAMETER                                         */
/*==============================================================*/
create table SYS_PARAMETER
(
   PARAMETER_ID         bigint not null comment '����ID',
   PARAMETER_CATEGORY_ID bigint comment '����ID',
   PARAMETER_NAME       varchar(50) comment '��������',
   PARAMETER_CAPTION    varchar(50) comment '������ʾ����',
   PARAMETER_CATEGORY   varchar(10) comment '��������',
   DEVICE_CATEGORY      varchar(10) comment '�豸����',
   DEVICE_ID            bigint comment '�豸ID',
   PARAMETER_VALUE      varchar(50) comment '����ֵ',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (PARAMETER_ID),
   constraint FK_REFERENCE_46 foreign key (PARAMETER_CATEGORY_ID)
      references SYS_PARAMETER_CATEGORY (PARAMETER_CATEGORY_ID) on delete restrict on update restrict
);

alter table SYS_PARAMETER comment '����';

/*==============================================================*/
/* Table: SYS_RESOURCE                                          */
/*==============================================================*/
create table SYS_RESOURCE
(
   RESOURCE_ID          bigint not null comment '��ԴID',
   PARENT_RESOURCE_ID   bigint comment '��ԴID',
   RESOURCE_NAME        varchar(50) comment '��Դ����',
   RESOURCE_CAPTION     varchar(50) comment '��Դ��ҳ������ʾ������',
   RESOURCE_URL         varchar(200) comment '��Դ��ַ',
   RESOURCE_CATEGORY    varchar(10) comment '��Դ����',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (RESOURCE_ID),
   constraint FK_REFERENCE_26 foreign key (PARENT_RESOURCE_ID)
      references SYS_RESOURCE (RESOURCE_ID) on delete restrict on update restrict
);

alter table SYS_RESOURCE comment '�û���Դ';

/*==============================================================*/
/* Table: SYS_ROLE_RESOURCE                                     */
/*==============================================================*/
create table SYS_ROLE_RESOURCE
(
   ROLE_RESOURCE_ID     bigint not null comment '��ɫӵ�е���ԴID',
   RESOURCE_ID          bigint comment '��ԴID',
   ROLE_ID              bigint comment '�û���ɫID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ROLE_RESOURCE_ID),
   constraint FK_REFERENCE_27 foreign key (RESOURCE_ID)
      references SYS_RESOURCE (RESOURCE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_28 foreign key (ROLE_ID)
      references SYS_ROLE (ROLE_ID) on delete restrict on update restrict
);

alter table SYS_ROLE_RESOURCE comment '�û���ɫӵ�е���Դ';

/*==============================================================*/
/* Table: SYS_ROLE_USER                                         */
/*==============================================================*/
create table SYS_ROLE_USER
(
   ROLE_USER_ID         bigint not null comment '�û�������ɫID',
   ROLE_ID              bigint comment '�û���ɫID',
   USER_ID              bigint comment '�û�ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (ROLE_USER_ID),
   constraint FK_REFERENCE_21 foreign key (ROLE_ID)
      references SYS_ROLE (ROLE_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_22 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict
);

alter table SYS_ROLE_USER comment '�û�������ɫ';

/*==============================================================*/
/* Table: SYS_STRATEGY                                          */
/*==============================================================*/
create table SYS_STRATEGY
(
   STRATEGY_ID          bigint not null comment '����ID',
   MODE_ID              bigint comment '����ģʽID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (STRATEGY_ID),
   constraint FK_REFERENCE_6 foreign key (MODE_ID)
      references SYS_WORK_MODE (MODE_ID) on delete restrict on update restrict
);

alter table SYS_STRATEGY comment '�ַ����Զ���';

/*==============================================================*/
/* Table: SYS_USER_GROUP                                        */
/*==============================================================*/
create table SYS_USER_GROUP
(
   USERGROUP_ID         bigint not null comment '�û���ID',
   ORG_ID               bigint comment '����ID',
   DATA_RANGE_CATEGORY  varchar(10) comment '���ݷ�Χ���ͣ�Ĭ�ϸ������ݷ�Χ�������������������������������¼����������ˡ����������ݡ��Զ���������',
   GROUP_NUMBER         varchar(50) comment '�û�����',
   GROUP_NAME           varchar(50) comment '�û�������',
   GROUP_FLAG           varchar(10) comment '�û�����(����ȷ)',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (USERGROUP_ID),
   constraint FK_REFERENCE_16 foreign key (ORG_ID)
      references SYS_ORG (ORG_ID) on delete restrict on update restrict
);

alter table SYS_USER_GROUP comment '�û���';

/*==============================================================*/
/* Table: SYS_USER_GROUP_LOOKUP                                 */
/*==============================================================*/
create table SYS_USER_GROUP_LOOKUP
(
   LOOKUP_ID            bigint not null comment '�û��ܲ鿴��������ID',
   USER_ID              bigint comment '�û�ID',
   DATA_GROUP_ID        bigint comment '������ID',
   USERGROUP_ID         bigint comment '�û���ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (LOOKUP_ID),
   constraint FK_REFERENCE_55 foreign key (DATA_GROUP_ID)
      references SYS_DATA_GROUP (DATA_GROUP_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_57 foreign key (USERGROUP_ID)
      references SYS_USER_GROUP (USERGROUP_ID) on delete restrict on update restrict
);

alter table SYS_USER_GROUP_LOOKUP comment '�û����ܲ鿴��������';

/*==============================================================*/
/* Table: SYS_USER_GROUP_ROLE                                   */
/*==============================================================*/
create table SYS_USER_GROUP_ROLE
(
   USER_GROUP_ROLE_ID   bigint not null comment '�û���ͽ�ɫ��Ӧ��ϵID',
   USERGROUP_ID         bigint comment '�û���ID',
   ROLE_ID              bigint comment '�û���ɫID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (USER_GROUP_ROLE_ID),
   constraint FK_REFERENCE_56 foreign key (USERGROUP_ID)
      references SYS_USER_GROUP (USERGROUP_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_58 foreign key (ROLE_ID)
      references SYS_ROLE (ROLE_ID) on delete restrict on update restrict
);

alter table SYS_USER_GROUP_ROLE comment '�û��������Ľ�ɫ';

/*==============================================================*/
/* Table: SYS_USER_GROUP_USER                                   */
/*==============================================================*/
create table SYS_USER_GROUP_USER
(
   USER_ID              bigint not null comment '�û�ID',
   USERGROUP_ID         bigint comment '�û���ID',
   GROUP_USER_ID        bigint comment '�û����û����ϵID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (USER_ID),
   constraint FK_REFERENCE_18 foreign key (USERGROUP_ID)
      references SYS_USER_GROUP (USERGROUP_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_78 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict
);

alter table SYS_USER_GROUP_USER comment '�û������û���';

/*==============================================================*/
/* Table: SYS_USER_LOOKUP                                       */
/*==============================================================*/
create table SYS_USER_LOOKUP
(
   LOOKUP_ID            bigint not null comment '�û����ܲ鿴������ID',
   USER_ID              bigint comment '�û�ID',
   DATA_GROUP_ID        bigint comment '������ID',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (LOOKUP_ID),
   constraint FK_REFERENCE_53 foreign key (USER_ID)
      references SYS_USER (USER_ID) on delete restrict on update restrict,
   constraint FK_REFERENCE_54 foreign key (DATA_GROUP_ID)
      references SYS_DATA_GROUP (DATA_GROUP_ID) on delete restrict on update restrict
);

alter table SYS_USER_LOOKUP comment '�û��ܲ鿴��������';

/*==============================================================*/
/* Table: SYS_WORKFLOW                                          */
/*==============================================================*/
create table SYS_WORKFLOW
(
   WORKFLOW_ID          bigint not null comment '��������ID',
   MODE_ID              bigint comment '����ģʽID',
   STEP_SEQUENCE        int comment '�������',
   STEP_NAME            varchar(50) comment '��������',
   TASK_TYPE            varchar(10) comment '��������',
   CREATEDBY            bigint comment '��¼������',
   CREATEDTIME          timestamp comment '��¼����ʱ��',
   EDITEDBY             bigint comment '��¼�޸���',
   EDITEDTIME           timestamp comment '��¼�޸�ʱ��',
   NOTE                 varchar(500) comment '��ע',
   primary key (WORKFLOW_ID),
   constraint FK_REFERENCE_5 foreign key (MODE_ID)
      references SYS_WORK_MODE (MODE_ID) on delete restrict on update restrict
);

alter table SYS_WORKFLOW comment '�������̶���';

