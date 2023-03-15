CREATE SCHEMA IF NOT EXISTS learningtools;

CREATE TABLE IF NOT EXISTS SIMPLEPERSON
(
    id    NUMBER(10) not null
        constraint id_sp
            primary key,
    firstName   VARCHAR2(50)  not null,
    lastName  VARCHAR2(50)  not null
);