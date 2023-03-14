-- drop des contraintes d'intégrité
alter table ERD_REFDOC DROP CONSTRAINT FK1ED187CD5697809E;
alter table ERD_REFTYPE DROP CONSTRAINT FKBB66DF2569C3820B;
alter table ERD_REFTYPE_SURCHARGE DROP CONSTRAINT FK9007E44A5697809E;

-- drop des index
DROP INDEX I_ERD_REFDOC_ERD_TYPE_FK;
DROP INDEX I_ERD_REFTYPE_ERD_CDTYPE;


-- drop des tables

    drop table ERD_REFTYPE_SURCHARGE  cascade constraints;
    drop table ERD_REFCAT cascade constraints;
    drop table ERD_REFTYPE  cascade constraints;
    
    
    ALTER TABLE ERD_REFDOC SET UNUSED (ERD_REFTYPE_FK, ERD_SEFAS, ERD_VISU_EXT);
    ALTER TABLE ERD_REFDOC DROP UNUSED COLUMNS CHECKPOINT 250;
    
    --alter table ERD_REFDOC drop column ERD_REFTYPE_FK; 
    --alter table ERD_REFDOC drop column ERD_SEFAS; 
    --alter table ERD_REFDOC drop column ERD_VISU_EXT; 

-- referencement du script 
INSERT INTO Editique_Version (id, VERSION, DESCRIPTION, INSTALL_DATE)
	VALUES ( hibernate_sequence.nextval, '1.1.0', 'rollback upgrade from 1.0.0 ', TO_CHAR( CURRENT_TIMESTAMP, 'YYYY-DD-MM HH24:MI:SS'));



