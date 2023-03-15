-- Ajout de la période de validité pour les upload IDOC
ALTER TABLE ERD_REFDOC ADD ERD_DTDE DATE;
ALTER TABLE ERD_REFDOC ADD ERD_DTFI DATE;

INSERT INTO Editique_Version (id, VERSION, DESCRIPTION, INSTALL_DATE)
	VALUES ( hibernate_sequence.nextval, '1.2.19', 'upgrade from 1.1.0 : ajout de la période de validité pour les documents IDOC', TO_CHAR( CURRENT_TIMESTAMP, 'YYYY-DD-MM HH24:MI:SS'));
	
drop index I_ERD_REFDOC_EBD_2039;
create index I_ERD_REFDOC_EBD_2039 on ERD_REFDOC ( ERD_IDSTAR, ERD_TSSTAR) tablespace TSEDITIC_02;
