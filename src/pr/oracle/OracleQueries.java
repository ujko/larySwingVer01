package pr.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import pr.connectionData.ConnectionData;
import pr.util.Utils;

public class OracleQueries {
	private static Connection con;

	/**
	 * Definiuje po³¹czenie. Pobiera dane z klasy ConnectionData
	 */
	public boolean setConnection(ConnectionData cd) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(
					"jdbc:oracle:thin:@" + cd.getServerName() + ":" + cd.getServerPort() + ":" + cd.getServiceName(),
					cd.getUserName(), String.valueOf(cd.getPassword()));
			return true;
		} catch (Exception e) {
			Utils utils = new Utils();
			utils.saveErrorToFile("OracleQueries-001", e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public ResultSet oracleToResultSet(String query) {
		ResultSet rs = null;
		try {
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "<html>B³¹d - szczegó³y w pliku 'laryngo.log'", "B³¹d",
					JOptionPane.ERROR_MESSAGE);
			Utils utils = new Utils();
			utils.saveErrorToFile("OracleQueries-002", e.getMessage());
		}
		return rs;
	}

	/**
	 * Tylko dla zapytañ skalarnych
	 * 
	 * @param query
	 * @return String (wartoœæ skalarna z zapytania)
	 */
	public String oracleGetScalar(String query) {
		String resultScalar = "";
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				resultScalar = rs.getString(1);
			}
		} catch (SQLException e) {
			Utils utils = new Utils();
			utils.saveErrorToFile("OracleQueries-003", e.getMessage());
			e.printStackTrace();
		}
		return resultScalar;
	}

	public List<String> oracleGetList(String query) {
		List<String> resultList = new ArrayList<>();
		try {
			ResultSet rs = oracleToResultSet(query);
			while (rs.next()) {
				resultList.add(rs.getString(1));
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "<html>B³¹d danych<br/>szczegó³y w pliku 'laryngo.log'", "B³¹d",
					JOptionPane.ERROR_MESSAGE);
			Utils utils = new Utils();
			utils.saveErrorToFile("OracleQueries-004", e.getMessage());
		}
		return resultList;
	}

	public void conClose() {
		try {
			if (con != null) {
				con.close();
				System.out.println("Baza od³¹czona");
			}

		} catch (SQLException e) {
			Utils utils = new Utils();
			utils.saveErrorToFile("OracleQueries-005", e.getMessage());
		}
	}

	public int ddlOracle(String ddl) {
		try {
			Statement stmt = con.createStatement();
			int res = stmt.executeUpdate(ddl);
			return res;
		} catch (Exception e) {
			Utils utils = new Utils();
			utils.saveErrorToFile("OracleQueries-006", e.getMessage());
			e.printStackTrace();
			return -1;
		}
	}

	private String getUtlFileDir() {
		String ufd = oracleGetScalar("select distinct value from v$parameter where name = 'utl_file_dir'");
		return ufd;
	}

	/**
	 * Sprawdza czy istnieje w bazie katalog dla pliku s³ownika<br/>
	 * i ewentualnie tworzy go, lub pozwala na wczytanie s³ownika do logów
	 */
	private void setUtlFileDir() {
		String message = "<html>W bazie nie zdefiniowano katalogu dla pliku s³ownika<br/>"
				+ "czy stworzyæ taki katalog? - wymaga restartu bazy </html>";
		int res = JOptionPane.showConfirmDialog(null, message, "Uwaga", JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION) {
			String catalog = JOptionPane.showInputDialog("Podaj nazwê katalogu (tak jak widzi serwer Oracle)");
			ddlOracle("alter system set utl_file_dir='" + catalog + "' scope = spfile");
			JOptionPane.showMessageDialog(null, "Katalog dodany, zrestartuj bazê i program", "OK",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Tworzy plik s³ownika 'dictionary.ora' w katalogu z parametru utl_file_dir
	 * 
	 */
	public String generateDictionaryFile(ConnectionData cd) {
		setConnection(cd);
		String dictionaryFileDir = getUtlFileDir();
		String dictionaryFile = JOptionPane.showInputDialog(null, "Podaj nazwê pliku s³ownika");

		if (dictionaryFileDir == null) {
			setUtlFileDir();
			return null;
		}
		String query = "begin " + "DBMS_LOGMNR_D.BUILD('" + dictionaryFile + "', '" + dictionaryFileDir
				+ "', DBMS_LOGMNR_D.STORE_IN_FLAT_FILE);" + " end;";
		ddlOracle(query);
		return dictionaryFileDir + "\\" + dictionaryFile;
	}

	/**
	 * Wczytywanie s³ownika do logów
	 */
	public void generateDictionaryToLogFile(ConnectionData cd) {
		setConnection(cd);
		ddlOracle("BEGIN SYS.DBMS_LOGMNR_D.BUILD(OPTIONS=> DBMS_LOGMNR_D.STORE_IN_REDO_LOGS); END;");
	}
}
