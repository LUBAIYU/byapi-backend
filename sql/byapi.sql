create database byapi;

use byapi;

create table if not exists interface
(
    id              bigint auto_increment comment '主键'
        primary key,
    name            varchar(256)                       not null comment '名称',
    description     varchar(256)                       null comment '描述',
    url             varchar(512)                       not null comment '接口地址',
    method          varchar(256)                       not null comment '请求类型',
    request_params  text                               null comment '请求参数',
    request_header  text                               null comment '请求头',
    response_header text                               null comment '响应头',
    status          int      default 0                 not null comment '接口状态（0-关闭，1-开启）',
    code_example    text                               null,
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted      tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
)
    comment '接口信息' engine = InnoDB;

create table if not exists user
(
    id            bigint auto_increment comment 'id'
        primary key,
    user_name     varchar(256)                           null comment '用户昵称',
    user_account  varchar(256)                           null comment '账号',
    user_avatar   varchar(1024)                          null comment '用户头像',
    email         varchar(256)                           null comment '用户邮箱，用于邮箱登录',
    gender        tinyint                                null comment '性别（0-男，1-女）',
    status        tinyint      default 0                 not null comment '用户状态（0-启用，1-禁用）',
    user_role     varchar(256) default 'user'            not null comment '用户角色：user / admin',
    user_password varchar(512)                           null comment '密码',
    salt          varchar(10)                            null comment '盐，用于加密',
    access_key    varchar(512)                           null comment 'accessKey',
    secret_key    varchar(512)                           null comment 'secretKey',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted    tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount
        unique (user_account),
    constraint user_pk
        unique (email)
)
    comment '用户表' engine = InnoDB;

create table if not exists user_interface
(
    id           bigint auto_increment comment '主键'
        primary key,
    user_id      bigint                             not null comment '调用接口用户ID',
    interface_id bigint                             not null comment '接口ID',
    total_num    int      default 0                 not null comment '总调用次数',
    left_num     int      default 0                 not null comment '剩余调用次数',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted   tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
)
    comment '用户调用接口关系' engine = InnoDB;

