package ppa.lab.springwebapp.model.mock;

import ppa.lab.springwebapp.exception.ServiceException;

import java.net.URISyntaxException;
import java.net.URL;

public class FileMock {

    private FileMock() {
    }

    public static String getFilePathsTest() throws ServiceException {
        ClassLoader classLoader = FileMock.class.getClassLoader();
        String xmlPathName = "filestest";
        URL resource = classLoader.getResource(xmlPathName);
        try {
            return resource.toURI().getPath();
        } catch (NullPointerException e) {
            throw new ServiceException("file not found! %s".formatted(xmlPathName));
        } catch (URISyntaxException e) {
            throw new ServiceException(e);
        }
    }
}
