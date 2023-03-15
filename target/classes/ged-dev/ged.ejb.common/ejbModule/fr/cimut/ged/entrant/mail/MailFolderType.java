package fr.cimut.ged.entrant.mail;

public enum MailFolderType {
	INBOX(
			""),
	ERROR(
			"_ERREUR"),
	BACKUP(
			"_REPONSE"),
	SPAM(
			"_SPAM");

	private final String folderNameSuffix;

	private MailFolderType(String folderNameSuffix) {
		this.folderNameSuffix = folderNameSuffix;
	}

	public String getFolderName(String inBoxName) {
		return inBoxName + folderNameSuffix;
	}
}
