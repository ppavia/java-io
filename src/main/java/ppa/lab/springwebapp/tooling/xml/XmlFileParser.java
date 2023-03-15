package ppa.lab.springwebapp.tooling.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ppa.lab.springwebapp.exception.TechnicalException;
import ppa.lab.springwebapp.tooling.validation.XmlFileValidation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class XmlFileParser {
    private static final Logger LOG = LoggerFactory.getLogger(XmlFileParser.class);

    private XmlFileValidation xmlFileValidation;

    public XmlFileParser(XmlFileValidation xmlFileValidation) {
        this.xmlFileValidation = xmlFileValidation;
    }

    public List<Path> loadXmlFiles (String pathName) throws IOException {
        Path rootPath = (new File(pathName)).toPath();
        if(!Files.isDirectory(rootPath)) {
            return new ArrayList<>();
        }
        try (Stream<Path> files = Files.list(rootPath)) {
            return files
                    .filter(f -> Files.isRegularFile(f) && isXml(f))
                    .toList();
        }
    }

    private boolean isXml (Path file) {
        try {
            return xmlFileValidation.isXmlFile(file);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    public Map<String, String> readInputXmlFile(File xml) throws TechnicalException  {
        String errorMsg = "Impossible de parser le fichier xml : %s".formatted(xml.getName());
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            Map<String, String> xmlDataMap = new HashMap<>();
            Document document = db.parse(xml);

            Element node = document.getDocumentElement();
            for (int s = 0; s < node.getChildNodes().getLength(); s++) {
                Node fstNode = ((NodeList) node).item(s);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    xmlDataMap.put(fstElmnt.getNodeName(), fstElmnt.getTextContent());
                }
            }
            return xmlDataMap;

        } catch (ParserConfigurationException e) {
            throw new TechnicalException(errorMsg, "ParserConfigurationException", e);
        } catch (SAXException e) {
            throw new TechnicalException(errorMsg, "SAXException", e);
        } catch (IOException e) {
            throw new TechnicalException(errorMsg, "IOException", e);
        }
    }
}