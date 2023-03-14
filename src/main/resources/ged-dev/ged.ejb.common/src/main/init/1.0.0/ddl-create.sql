

set define off;


    create table ERD_HIST_GED (
        ERD_ID number(19,0) not null unique,
        ERD_DT_MAJ timestamp,
        ERD_LBNMFD varchar2(255 char),
        ERD_MODIF varchar2(255 char),
        ERD_USER_MAJ varchar2(255 char),
        primary key (ERD_ID)
    );

    create table ERD_REFDOC (
        ERD_LBNMFD varchar2(255 char) not null,
        ERD_CDCORG varchar2(255 char),
        ERD_DTARCHDC number(19,0),
        ERD_DTCSDC timestamp,
        ERD_DTCONSDC number(19,0),
        ERD_DTCRDC timestamp,
        ERD_DTSLDC timestamp,
        ERD_IDSTAR number(19,0),
        ERD_PRCPAR varchar2(255 char),
        ERD_PRFDPG varchar2(255 char),
        ERD_LBCHFP varchar2(255 char),
        ERD_LBNMFF varchar2(255 char),
        ERD_LBDCTP varchar2(255 char),
        ERD_MIMETYPE varchar2(255 char),
        ERD_NBCSDC number(19,0),
        ERD_NBEXDC number(10,0),
        ERD_NBPGDC number(10,0),
        ERD_ORAERR varchar2(255 char),
        ERD_LBORDC varchar2(255 char),
        ERD_CDCONF number(10,0),
        ERD_CDSERV varchar2(255 char),
        ERD_SIDSTAR varchar2(255 char),
        ERD_LBSITE varchar2(255 char),
        ERD_STATUT varchar2(255 char),
        ERD_TSSTAR number(19,0),
        ERD_NODCTP varchar2(255 char),
        ERD_CDPAPI varchar2(255 char),
        ERD_CDUSCS varchar2(255 char),
        ERD_CDUSSL varchar2(255 char),
        primary key (ERD_LBNMFD)
    );

    create table ERD_REFJSONGED (
        ERD_LBNMFD varchar2(255 char) not null,
        ERD_JSON clob,
        ERD_ORGA varchar2(255 char),
        primary key (ERD_LBNMFD)
    );

    create sequence S_ERD_HIST_GED;


	-- version applicative du pom : 1.0.0

commit;

