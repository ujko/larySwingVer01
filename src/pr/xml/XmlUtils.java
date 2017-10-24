package pr.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pr.connectionData.ConnectionData;
import pr.encryption.CryptographyUtil;

public class XmlUtils {
	private static Logger logger = LoggerFactory.getLogger(XmlUtils.class.getName());
	static final String file = "laryusers.xml";

	/**
	 * Zwraca tablicê z danymi logowania
	 * 
	 * @param groupInXmlToFind
	 *            - wybrana nazwa
	 * @return tablica z danymi logowania pobrana z pliku xml dla wybranej nazwy
	 */
	public static String[] xmlNodeToTable(String groupInXmlToFind) {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(file);
		String[] tablica = new String[5];
		try {
			Document doc = builder.build(xmlFile);
			Element rootElement = doc.getRootElement();
			Element connectionName = rootElement.getChild(groupInXmlToFind);
			if (connectionName != null) {
				tablica[0] = connectionName.getChild("user").getText();
				tablica[1] = connectionName.getChild("pass").getText();
				tablica[2] = connectionName.getChild("server").getText();
				tablica[3] = connectionName.getChild("port").getText();
				tablica[4] = connectionName.getChild("service").getText();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return tablica;
	}

	/**
	 * Kasuje grupê danych logowania z pliku xml
	 * 
	 * @param groupInXmlToFind
	 *            - grupa do skasowania
	 */
	public static void deleteFromXml(String groupInXmlToFind) {
		try {
			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(file);
			Document doc = builder.build(xmlFile);
			Element rootElement = doc.getRootElement();
			rootElement.removeChild(groupInXmlToFind);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(file));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Zwraca pojedynczy element z grupy
	 * 
	 * @param groupInXmlToFind
	 * @param fieldInXmlToFind
	 * @return znaleziony element
	 */
	public static String findInXml(String groupInXmlToFind, String fieldInXmlToFind) {
		String foundChild = " ";
		try {
			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(file);
			Document doc = builder.build(xmlFile);
			Element rootElement = doc.getRootElement();
			Element connectionName = rootElement.getChild(groupInXmlToFind);
			foundChild = connectionName.getChildText(fieldInXmlToFind);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return foundChild;
	}

	/**
	 * Dodaje wpis do pliku xml
	 * 
	 * @return - zwraca 0 gdy dodane, 1 gdy b³¹d
	 */
	public static int saveConnectionDataToXml(ConnectionData cd) {
		String xConnectionName = cd.getConnectionName();
		String xUser = cd.getUserName();
		String pass = String.valueOf(cd.getPassword());
		String xPass = encryptPassword(pass);
		String xServer = cd.getServerName();
		String xPort = cd.getServerPort();
		String xService = cd.getServiceName();
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(file);
		if (xmlFile.exists()) {
			try {
				Document doc = builder.build(xmlFile);
				Element connectionName = new Element(xConnectionName);
				doc.getRootElement().addContent(connectionName);
				connectionName.addContent(new Element("user").setText(xUser));
				connectionName.addContent(new Element("pass").setText(xPass));
				connectionName.addContent(new Element("server").setText(xServer));
				connectionName.addContent(new Element("port").setText(xPort));
				connectionName.addContent(new Element("service").setText(xService));
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, new FileWriter(file));
			} catch (Exception e) {
				logger.error(e.getMessage());
				return 1;
			}
		} else {
			// JOptionPane.showMessageDialog(null, "Plik '"+file+" nie istnieje,
			// tworzê nowy", "Dane XML", JOptionPane.INFORMATION_MESSAGE);
			createXml(cd);
		}
		return 0;
	}

	private static String encryptPassword(String pass) {
		String xPass = "";
		try {
			CryptographyUtil cr = new CryptographyUtil();
			xPass = cr.encryptData(pass);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return xPass;
	}

	/**
	 * Tworzy plik xml gdy ten nie istnieje
	 * 
	 * @param xConnectionName
	 *            - nazwa po³¹czenia
	 * @param xUser
	 *            - nazwa u¿ytkownika
	 * @param xPass
	 *            - has³o u¿ytkownika
	 * @param xServer
	 *            - nazwa serwera
	 * @param xPort
	 *            - numer portu
	 */
	private static void createXml(ConnectionData cd) {
		File fileXml = new File(file);
		if (!fileXml.exists()) {
			try {
				Element connectionData = new Element("ConnectionData");
				Document doc = new Document(connectionData);
				Element connectionName = new Element(cd.getConnectionName());
				doc.getRootElement().addContent(connectionName);
				connectionName.addContent(new Element("user").setText(cd.getUserName()));
				String p = String.valueOf(cd.getPassword());
				;
				connectionName.addContent(new Element("pass").setText(encryptPassword(p)));
				connectionName.addContent(new Element("server").setText(cd.getServerName()));
				connectionName.addContent(new Element("port").setText(cd.getServerPort()));
				connectionName.addContent(new Element("service").setText(cd.getServiceName()));
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, new FileWriter(file));
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * Sprawdza czy istnieje ju¿ podana nazwa w xml
	 * 
	 * @param groupInXmlToFind
	 *            - nazwa grupy do sprawdzenia
	 * @return boolean
	 */
	public static boolean findGroupInXml(String groupInXmlToFind) {
		boolean ifExist = false;
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(file);
		if (xmlFile.exists()) {
			try {
				Document doc = builder.build(xmlFile);
				Element rootElement = doc.getRootElement();
				Element connectionName = rootElement.getChild(groupInXmlToFind);
				if (connectionName != null) {
					ifExist = true;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return ifExist;
	}

	/**
	 * Zwraca Listê z nazwami grup z xml
	 * 
	 * @return - Lista z nazwami grup z xml
	 */
	public static List<Element> getNodesFromXml() {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(file);
		Document doc;
		List<Element> list = new ArrayList<>();
		if (xmlFile.exists()) {
			try {
				doc = builder.build(xmlFile);
				Element rootElement = doc.getRootElement();
				list = rootElement.getChildren();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return list;
	}
}
