package fr.cimut.ged.entrant.mail;

import fr.cimut.ged.entrant.dto.oauth.PasswordGrantDto;
import fr.cimut.ged.entrant.dto.oauth.PasswordGrantResponseDto;
import fr.cimut.ged.entrant.utils.RestClientUtils;

import java.net.URLEncoder;
import java.util.Collections;

import static fr.cimut.ged.entrant.utils.GlobalVariable.getAuthServerUrl;

public class OAuthClient {

	private OAuthClient() {
	}

	public static String getToken(String cmroc, String username, String password) throws Exception {

		String tenantId = URLEncoder.encode(cmroc, "UTF-8");
		String url = getAuthServerUrl() + "/imap/"+tenantId+"/oauth2/password-grant";

		PasswordGrantDto passwordGrantDto = new PasswordGrantDto();
		passwordGrantDto.setUser(username);
		passwordGrantDto.setPassword(password);

		PasswordGrantResponseDto responseDto = RestClientUtils.executePostRequest(url, Collections.<String, String>emptyMap(),
			PasswordGrantResponseDto.class, passwordGrantDto);

		return responseDto.getToken();
	}
}
