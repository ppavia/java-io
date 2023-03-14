-- maj du schéma

    
    create sequence hibernate_sequence;
    

    create table Editique_Version (
        id number(19,0) not null,
        DESCRIPTION varchar2(255 char),
        INSTALL_DATE varchar2(255 char),
        VERSION varchar2(255 char),
        primary key (id)
    );


    create table ERD_REFCAT (
        id number(19,0) not null,
        ERD_CDCAT varchar2(255 char),
        ERD_DT_MAJ timestamp,
        ERD_LBCAT varchar2(255 char),
        ERD_USER_MAJ varchar2(255 char),
        primary key (id)
    );

    
    create table ERD_REFTYPE (
        id number(19,0) not null,
        ERD_CDTYPE varchar2(255 char),
        ERD_DT_MAJ timestamp,
        ERD_DUREE_ARCH number(10,0),
        ERD_DUREE_PURGE number(10,0),
        ERD_DUREE_VISU_EXT number(10,0),
        ERD_INTEG_IDOC number(1,0),
        ERD_LBTYPE varchar2(255 char),
        ERD_NOTIF_EXT number(1,0),
        ERD_TYPE_E_S varchar2(255 char),
        ERD_USER_MAJ varchar2(255 char),
        ERD_VISU_EXT number(1,0),
        ERD_REFCAT_FK number(19,0),
        primary key (id)
    );

    create table ERD_REFTYPE_SURCHARGE (
        id number(19,0) not null,
        ERD_CDCORG varchar2(255 char),
        ERD_DT_MAJ timestamp,
        ERD_DUREE_PURGE number(10,0),
        ERD_DUREE_VISU_EXT number(10,0),
        ERD_NOTIF_EXT number(1,0),
        ERD_USER_MAJ varchar2(255 char),
        ERD_VISU_EXT number(1,0),
        ERD_REFTYPE_FK number(19,0),
        primary key (id)
    );

    
    
	-- alter table existantes	   
    alter table ERD_REFDOC add (
        ERD_REFTYPE_FK number(19,0), 
        ERD_SEFAS number(1,0),
        ERD_VISU_EXT number(1,0)
    );
    

-- peuplement des tables

    -- insert de la categorie    
    INSERT INTO ERD_REFCAT (id, ERD_CDCAT,  ERD_LBCAT, ERD_DT_MAJ, ERD_USER_MAJ)
	select hibernate_sequence.nextval, 'TOUS',  'tous', sysdate, 'system'  from dual
	where not exists (
		select 1 from ERD_REFCAT cat
  		where cat.ERD_CDCAT = 'TOUS'
  	);
    
    
    -- insert des types hors fiche DSN et hors model de courrier
    INSERT INTO ERD_REFTYPE (id, ERD_CDTYPE, ERD_LBTYPE, ERD_DUREE_ARCH, ERD_DUREE_PURGE, ERD_DUREE_VISU_EXT, ERD_VISU_EXT, ERD_NOTIF_EXT, ERD_TYPE_E_S, ERD_DT_MAJ, ERD_USER_MAJ)
	select hibernate_sequence.nextval, doc.ERD_NODCTP, doc.ERD_LBDCTP, 30, 3650, 3650, 1, 0, null, sysdate, 'system' from ( 
			select distinct ERD_NODCTP, ERD_LBDCTP from ERD_REFDOC
			) doc				
			where not exists (
				select 1 from ERD_REFTYPE typ
		  		where typ.ERD_CDTYPE = doc.ERD_NODCTP
  			)
  			and doc.ERD_NODCTP <> 'ficheDSN'
  			and not REGEXP_LIKE (doc.ERD_NODCTP, '^[0-9]{4}L(R|P|T)[0-9]{4}$')
  			;
  			
   
    -- insert des types  fiche DSN
    INSERT INTO ERD_REFTYPE (id, ERD_CDTYPE, ERD_LBTYPE, ERD_DUREE_ARCH, ERD_DUREE_PURGE, ERD_DUREE_VISU_EXT, ERD_VISU_EXT, ERD_NOTIF_EXT, ERD_TYPE_E_S, ERD_DT_MAJ, ERD_USER_MAJ)
	select hibernate_sequence.nextval, doc.ERD_NODCTP, 'fiche DSN', 30, 3650, 3650, 1, 0, null, sysdate, 'system' from ( 
			select distinct ERD_NODCTP from ERD_REFDOC
			) doc				
			where not exists (
				select 1 from ERD_REFTYPE typ
		  		where typ.ERD_CDTYPE = doc.ERD_NODCTP
  			)
  			and doc.ERD_NODCTP = 'ficheDSN'
  			;
  	
  	
  	-- Insertion du type courrier
  	INSERT INTO ERD_REFTYPE (id, ERD_CDTYPE, ERD_LBTYPE, ERD_DUREE_ARCH, ERD_DUREE_PURGE, ERD_DUREE_VISU_EXT, ERD_VISU_EXT, ERD_NOTIF_EXT, ERD_TYPE_E_S, ERD_INTEG_IDOC, ERD_DT_MAJ, ERD_USER_MAJ) 
  	select hibernate_sequence.nextval, 'MODELE_COURRIER', 'Modèle de courrier', 30, 3650, 3650, 0, 0, null, 0, sysdate, 'system' from dual;
  	
  	
  	-- Insertion du type courrier demat
  	INSERT INTO ERD_REFTYPE (id, ERD_CDTYPE, ERD_LBTYPE, ERD_DUREE_ARCH, ERD_DUREE_PURGE, ERD_DUREE_VISU_EXT, ERD_VISU_EXT, ERD_NOTIF_EXT, ERD_TYPE_E_S, ERD_INTEG_IDOC, ERD_DT_MAJ, ERD_USER_MAJ) 
  	select hibernate_sequence.nextval, 'COURRIER_DEMATERIALISE', 'Courrier dématérialisé', 30, 3650, 3650, 0, 0, null, 0, sysdate, 'system' from dual;
  	
  	-- Insertion du type rapport intégration idoc
  	INSERT INTO ERD_REFTYPE (id, ERD_CDTYPE, ERD_LBTYPE, ERD_DUREE_ARCH, ERD_DUREE_PURGE, ERD_DUREE_VISU_EXT, ERD_VISU_EXT, ERD_NOTIF_EXT, ERD_TYPE_E_S, ERD_INTEG_IDOC, ERD_DT_MAJ, ERD_USER_MAJ) 
  	select hibernate_sequence.nextval, 'RAPPORT_INTEGRATION_IDOC', 'Rapport intégration IDOC', 30, 3650, 3650, 0, 0, null, 0, sysdate, 'system' from dual;
  			
  			
  	-- on insere également un type INCONNU pour permettre de gérer les cas aux limites par prorpiétés de conf
  	INSERT INTO ERD_REFTYPE (id, ERD_CDTYPE, ERD_LBTYPE, ERD_DUREE_ARCH, ERD_DUREE_PURGE, ERD_DUREE_VISU_EXT, ERD_VISU_EXT, ERD_NOTIF_EXT, ERD_TYPE_E_S, ERD_INTEG_IDOC, ERD_DT_MAJ, ERD_USER_MAJ) 
  	select hibernate_sequence.nextval, 'INCONNU', 'INCONNU', 30, 3650, 3650, 0, 0, null, 0, sysdate, 'system' from dual;
  	
  	        
	-- creation index type document sur type        
	create index I_ERD_REFTYPE_ERD_CDTYPE on ERD_REFTYPE (ERD_CDTYPE);
  	
  	
  	-- on supprime tous les types avec un code en doublon - on ne garde que l'id max par code
  	delete from  ERD_REFTYPE
    where ERD_CDTYPE in (
      select ERD_CDTYPE from (
      select typ.ERD_CDTYPE, count(*) nb from ERD_REFTYPE typ
      group by typ.ERD_CDTYPE
      ) sub 
      where sub.nb > 1
    )
    and id not in (
      select distinct maxId.maxId from (
        select max(id) maxId, ERD_CDTYPE from ERD_REFTYPE where ERD_CDTYPE in (
          select ERD_CDTYPE from (
            select typ.ERD_CDTYPE, count(*) nb from ERD_REFTYPE typ
            group by typ.ERD_CDTYPE
          ) sub where sub.nb > 1
        )group by ERD_CDTYPE
      ) maxId
    );
  			
    -- update des types pour la catégorie
    update ERD_REFTYPE set ERD_REFCAT_FK = (    
		select id from ERD_REFCAT cat
  		where cat.ERD_CDCAT = 'TOUS'
    )
    where ERD_REFCAT_FK is null;
    
    -- update des documents modeles de courrier
    update ERD_REFDOC doc set doc.ERD_REFTYPE_FK = (    
		select id from ERD_REFTYPE typ
  		where typ.ERD_CDTYPE = 'MODELE_COURRIER'
    )
    where doc.ERD_REFTYPE_FK is null
    and REGEXP_LIKE (doc.ERD_NODCTP, '^[0-9]{4}L(R|P|T)[0-9]{4}$');
    
    -- update des documents par rapport a leurs types
    update ERD_REFDOC doc set doc.ERD_REFTYPE_FK = (    
		select id from ERD_REFTYPE typ
  		where typ.ERD_CDTYPE = doc.ERD_NODCTP
    )
    where doc.ERD_REFTYPE_FK is null;
    
    
	-- valorisation des données types :
		-- booléen sefas/duree purge/archivage/visuExtranet/dureeVisuExtranet/integIdoc/entrantSortant		
    update ERD_REFTYPE set ERD_INTEG_IDOC = 1 where ERD_INTEG_IDOC is null;
    
    
    
    --modification du type ECART_UMC_107
    update ERD_REFTYPE set ERD_CDTYPE = 'TABLEAU_GARANTIE' where ERD_CDTYPE = 'ECART_UMC_107';
    update ERD_REFTYPE set ERD_CDTYPE = 'PIECE_JOINTE_SUDE' where ERD_CDTYPE = 'DEMANDE_ASSURE_SUDE';
    update ERD_REFTYPE set ERD_CDTYPE = 'MAIL_DEMATERIALISE' where ERD_CDTYPE = 'SUDE_EMAIL';
    
    -- update des libellés de types pour tous les types dont le libellé est ged entrante    
    update ERD_REFTYPE set ERD_LBTYPE = 'Bulletin d''adhésion' where ERD_CDTYPE = 'ALTO_BULLETIN_ADH';
    update ERD_REFTYPE set ERD_LBTYPE = 'Bulletin d''adhésion signé' where ERD_CDTYPE = 'ALTO_BULLETIN_ADH_SIGNED';
    update ERD_REFTYPE set ERD_LBTYPE = 'Devis' where ERD_CDTYPE = 'ALTO_DEVIS';
    update ERD_REFTYPE set ERD_LBTYPE = 'Fiche d''information et conseil' where ERD_CDTYPE = 'ALTO_FICHE_INFORMATION_ET_CONSEIL';
    update ERD_REFTYPE set ERD_LBTYPE = 'Fiche d''information et conseil signée' where ERD_CDTYPE = 'ALTO_FICHE_INFORMATION_ET_CONSEIL_SIGNED';
    update ERD_REFTYPE set ERD_LBTYPE = 'Fiche IPID' where ERD_CDTYPE = 'ALTO_FICHE_IPID';
    update ERD_REFTYPE set ERD_LBTYPE = 'Document de paramétrage' where ERD_CDTYPE = 'ALTO_GESTION';
    update ERD_REFTYPE set ERD_LBTYPE = 'Mandat de prélèvement SEPA' where ERD_CDTYPE = 'ALTO_MANDAT_SEPA';
    update ERD_REFTYPE set ERD_LBTYPE = 'Mandat de prélèvement SEPA signé' where ERD_CDTYPE = 'ALTO_MANDAT_SEPA_SIGNED';
    update ERD_REFTYPE set ERD_LBTYPE = 'Pièces justificatives' where ERD_CDTYPE = 'ALTO_PJ';
    update ERD_REFTYPE set ERD_LBTYPE = 'Tableau de remboursement' where ERD_CDTYPE = 'ALTO_TABLEAU_REMBOURSEMENT';
    update ERD_REFTYPE set ERD_LBTYPE = 'Modèle de bulletin d''adhésion' where ERD_CDTYPE = 'ALTO_TEMPLATE_BULLETIN_ADH';
    update ERD_REFTYPE set ERD_LBTYPE = 'Modèle de devis' where ERD_CDTYPE = 'ALTO_TEMPLATE_DEVIS';
    update ERD_REFTYPE set ERD_LBTYPE = 'Modèle de fiche d''information et conseil' where ERD_CDTYPE = 'ALTO_TEMPLATE_INFORMATION_ET_CONSEIL';
    update ERD_REFTYPE set ERD_LBTYPE = 'Modèle de mandat SEPA' where ERD_CDTYPE = 'ALTO_TEMPLATE_MANDAT_SEPA';
    update ERD_REFTYPE set ERD_LBTYPE = 'Documents courtage' where ERD_CDTYPE = 'COURTAGE';
    update ERD_REFTYPE set ERD_LBTYPE = 'Pièce jointe SUDE' where ERD_CDTYPE = 'DEMANDE_ASSURE_SUDE';
    update ERD_REFTYPE set ERD_LBTYPE = 'Pièce jointe SUDE' where ERD_CDTYPE = 'PIECE_JOINTE_SUDE';
    update ERD_REFTYPE set ERD_LBTYPE = 'Mail dématérialisé' where ERD_CDTYPE = 'SUDE_EMAIL';
    update ERD_REFTYPE set ERD_LBTYPE = 'Mail dématérialisé' where ERD_CDTYPE = 'MAIL_DEMATERIALISE';
    update ERD_REFTYPE set ERD_LBTYPE = 'Tableau de garanties' where ERD_CDTYPE = 'TABLEAU_GARANTIE';
    
    update ERD_REFTYPE set ERD_LBTYPE = 'Bulletin individuel d''affiliation' where ERD_CDTYPE = 'ALTO_BIA';
    update ERD_REFTYPE set ERD_LBTYPE = 'Contrat collectif' where ERD_CDTYPE = 'ALTO_CONTRAT';
    update ERD_REFTYPE set ERD_LBTYPE = 'Contrat collectif signé' where ERD_CDTYPE = 'ALTO_CONTRAT_SIGNED';
    update ERD_REFTYPE set ERD_LBTYPE = 'Dérogation' where ERD_CDTYPE = 'ALTO_DEROGATION';
    update ERD_REFTYPE set ERD_LBTYPE = 'Notice contrat collectif' where ERD_CDTYPE = 'ALTO_NOTICE';
    update ERD_REFTYPE set ERD_LBTYPE = 'Modèle bulletin individuel d''affiliation' where ERD_CDTYPE = 'ALTO_TEMPLATE_BIA';
    update ERD_REFTYPE set ERD_LBTYPE = 'Modèle de contrat collectif' where ERD_CDTYPE = 'ALTO_TEMPLATE_CONTRAT';
    update ERD_REFTYPE set ERD_LBTYPE = 'Modèle de notice' where ERD_CDTYPE = 'ALTO_TEMPLATE_NOTICE';
    update ERD_REFTYPE set ERD_LBTYPE = 'Document Alto' where ERD_CDTYPE = 'ALTO_TEMPLATE_REPRISE';
    

-- création des contraintes d'intégrité

    alter table ERD_REFDOC 
        add constraint FK1ED187CD5697809E 
        foreign key (ERD_REFTYPE_FK) 
        references ERD_REFTYPE;

    alter table ERD_REFTYPE 
        add constraint FKBB66DF2569C3820B 
        foreign key (ERD_REFCAT_FK) 
        references ERD_REFCAT;

    alter table ERD_REFTYPE_SURCHARGE 
        add constraint FK9007E44A5697809E 
        foreign key (ERD_REFTYPE_FK) 
        references ERD_REFTYPE;

-- modification de la taille du nom du fichier physique
    alter table ERD_REFDOC 
        modify ERD_LBNMFD varchar2(255 char);
        
        
-- augmentation de la taille de la colonne MIME_TYPE pour pouvoir stocker le type XLSX
ALTER TABLE ERD_REFDOC MODIFY ERD_MIMETYPE varchar2(100); 
        

--création de l'index sur la foreign key type de document depuis document
create index I_ERD_REFDOC_ERD_TYPE_FK on ERD_REFDOC (ERD_REFTYPE_FK);

-- referencement du script 
INSERT INTO Editique_Version (id, VERSION, DESCRIPTION, INSTALL_DATE)
	VALUES ( hibernate_sequence.nextval, '1.1.16', 'upgrade from 1.0.0 : gestion electronique du document : typage des document classement et règles associées aux types', TO_CHAR( CURRENT_TIMESTAMP, 'YYYY-DD-MM HH24:MI:SS'));


