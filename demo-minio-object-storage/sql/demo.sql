CREATE TABLE sys_object_storage
(
    id            varchar(50)          NOT NULL COMMENT '主键'
        PRIMARY KEY,
    version       int                  NULL COMMENT '版本',
    created_time  datetime(6)          NULL COMMENT '创建时间',
    created_user  varchar(50)          NULL COMMENT '创建人',
    is_delete     tinyint(1) DEFAULT 0 NOT NULL COMMENT '删除标记',
    modified_time datetime(6)          NULL COMMENT '修改时间',
    modified_user varchar(50)          NULL COMMENT '修改人',
    file_name     varchar(255)         NULL COMMENT '文件名',
    file_path     varchar(255)         NULL COMMENT '资源服务器文件相对路径',
    file_size     int(32)              NULL COMMENT '文件大小 单位 字节',
    file_type     varchar(255)         NULL COMMENT '文件类型',
    module_name   varchar(255)         NULL COMMENT '文件所属业务模块，作为文件父级目录',
    data_id       varchar(255)         NULL COMMENT '关联该文件的数据ID',
    read_only     tinyint(1) DEFAULT 0 NULL COMMENT '是否只读'
)
    COMMENT '文件信息表';