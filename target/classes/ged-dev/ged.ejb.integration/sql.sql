



select * from  ERD_REFDOC where ERD_REFDOC.ERD_IDSTAR = 000016330593 and  ERD_REFDOC.ERD_TSSTAR = 20150313122218;
select * from  ERD_REFDOC where ERD_REFDOC.ERD_IDSTAR = 000016306415 and  ERD_REFDOC.ERD_TSSTAR = 20150304112453;
000016306415_20150304112453

select document0_.ERD_LBNMFD as ERD1_34_1_, document0_.ERD_CDCORG as ERD2_34_1_, document0_.ERD_DTARCHDC as ERD3_34_1_, document0_.ERD_DTCSDC as ERD4_34_1_, document0_.ERD_DTCONSDC as ERD5_34_1_, document0_.ERD_DTCRDC as ERD6_34_1_, document0_.ERD_DTSLDC as ERD7_34_1_, document0_.ERD_IDSTAR as ERD8_34_1_, document0_.ERD_PRCPAR as ERD9_34_1_, document0_.ERD_PRFDPG as ERD10_34_1_, document0_.ERD_LBCHFP as ERD11_34_1_, document0_.ERD_LBNMFF as ERD12_34_1_, document0_.ERD_LBDCTP as ERD13_34_1_, document0_.ERD_MIMETYPE as ERD14_34_1_, document0_.ERD_NBCSDC as ERD15_34_1_, document0_.ERD_NBEXDC as ERD16_34_1_, document0_.ERD_NBPGDC as ERD17_34_1_, document0_.ERD_ORAERR as ERD18_34_1_, document0_.ERD_LBORDC as ERD19_34_1_, document0_.ERD_CDCONF as ERD20_34_1_, document0_.ERD_CDSERV as ERD21_34_1_, document0_.ERD_SIDSTAR as ERD22_34_1_, document0_.ERD_LBSITE as ERD23_34_1_, document0_.ERD_STATUT as ERD24_34_1_, document0_.ERD_TSSTAR as ERD25_34_1_, document0_.ERD_NODCTP as ERD26_34_1_, document0_.ERD_CDPAPI as ERD27_34_1_, document0_.ERD_CDUSCS as ERD28_34_1_, document0_.ERD_CDUSSL as ERD29_34_1_, json1_.ERD_LBNMFD as ERD1_36_0_, json1_.ERD_JSON as ERD2_36_0_, json1_.ERD_ORGA as ERD3_36_0_ from ERD_REFDOC document0_ left outer join ERD_REFJSONGED json1_ on document0_.ERD_LBNMFD=json1_.ERD_LBNMFD where document0_.ERD_LBNMFD= '9916_2014102111946_00086(0).doc';

delete from ERD_REFDOC where ERD_REFDOC.ERD_LBSITE = 'G';
update ERD_REFDOC set ERD_CDCONF = 0 where ERD_REFDOC.ERD_LBSITE = 'G';

select  distinct(ERD_LBCHFP) from ERD_REFDOC;

select count(*) from ERD_REFDOCGED;
select count(*) from ERD_REFJSONGED;

delete from ERD_REFDOCGED where ERD_REFDOC.ERD_LBNMFD = ;


SELECT
column_name "Name",
nullable "Null?",
concat(concat(concat(data_type,'('),data_length),')') "Type"
FROM user_tab_columns
WHERE table_name='ERD_REFDOCGED';

delete from ERD_REFDOCGED;

select *   from ERD_REFDOC where ERD_REFDOC.ERD_LBNMFD = '9916_20141021111946_00036(0).jpg';
select *   from ERD_REFDOC where ERD_REFDOC.ERD_LBNMFD = '9916_20141021111946_00083.pdf';
delete   from ERD_REFDOC where ERD_REFDOC.ERD_LBNMFD = '9916_20141021111946_00087.pdf';

-- 000016304860_20150211184742
update ERD_REFDOC set ERD_CDCONF = 0  where ERD_IDSTAR = 16304860 and ERD_TSSTAR = 20150211184742;
select *  from ERD_REFDOC where ERD_REFDOC.ERD_LBNMFD = '9916_20141021111946_00086.pdf';
select *  from ERD_REFDOCGED where ERD_REFDOCGED.ERD_LBNMFD = '9916_20141021111946_00013.pdf';
select ERD_REFDOCGED.ERD_LBNMFD  from ERD_REFDOCGED;
delete from ERD_REFJSONGED;

 select *  from ERD_HIST_GED;

select count(*) from ERD_REFDOCGED;
select count(*) from ERD_REFJSONGED;
drop table ERD_REFJSONGED;

CREATE TABLE ERD_REFJSONGED (
	ERD_LBNMFD VARCHAR2(50) NOT NULL,
	ERD_ORGA CHAR(4) NOT NULL,
	ERD_JSON CLOB NOT NULL
);

drop table ERD_HIST_GED;


delete from ERD_HIST_GED;
CREATE TABLE ERD_HIST_GED (
	ERD_ID NUMBER(10 , 0) NOT NULL,
	ERD_LBNMFD VARCHAR2(50) NOT NULL,
	ERD_MODIF VARCHAR2(4000) DEFAULT NULL,
	ERD_USER_MAJ VARCHAR2(20) NOT NULL,
	ERD_DT_MAJ DATE NOT NULL
);

delete from ERD_REFJSONGED;


ALTER TABLE ERD_HIST_GED ADD CONSTRAINT ERD_HIST_GED_PK PRIMARY KEY (ERD_ID);
alter table ERD_HIST_GED add constraint FK_HIST_REFDOCGED foreign key (ERD_LBNMFD) references ERD_REFJSONGED(ERD_LBNMFD) on delete cascade;

alter table
   ERD_REFJSONGED
drop constraint FK_ERD_REFDOCGED;


CREATE UNIQUE INDEX PK_ERD_REFJSONGED ON ERD_REFJSONGED (ERD_LBNMFD ASC);
ALTER TABLE ERD_REFJSONGED ADD CONSTRAINT PK_ERD_REFJSONGED PRIMARY KEY (ERD_LBNMFD);
alter table ERD_REFJSONGED add constraint FK_ERD_REFDOCGED foreign key (ERD_LBNMFD) references ERD_REFDOC(ERD_LBNMFD) on delete cascade;