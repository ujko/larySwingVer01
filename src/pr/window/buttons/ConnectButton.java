package pr.window.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import pr.connectionData.ConnectionData;
import pr.oracle.OracleQueries;
import pr.window.LaryngologFrame;

public class ConnectButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 2412925884252880888L;
	private static boolean isConnected = false;
	private static String dictionaryFileDir;
	private static String dictionaryFile;
	private LaryngologFrame laryngologFrame;
	private ConnectionData cd;

	public ConnectButton(LaryngologFrame laryngologFrame) {
		super("Po³¹cz");
		this.laryngologFrame = laryngologFrame;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		OracleQueries oq = new OracleQueries();
		if (!isConnected) {
			cd = laryngologFrame.setConnectionData();
			if (isReadyForConnect(oq)) {
				setYesSupplementalLogData();
				loadLogsToLogMiner(cd.getDictionary());
				setCombos();
				this.setText("Roz³¹cz");
				laryngologFrame.setActiveTab();
				isConnected = true;
			}
		} else {
			unloadLogminer();
			laryngologFrame.clearLtlList();
			oq.conClose();
			this.setText("Po³¹cz");
			isConnected = false;
		}
	}

	private boolean isReadyForConnect(OracleQueries oq) {
		if (!laryngologFrame.isConnectionData()) {
			return false;
		}
		if (!oq.setConnection(cd)) {
			return false;
		}
		if (!laryngologFrame.isLogsToLoad()) {
			return false;
		}
		return true;
	}

	private File getDictionaryDialog() {
		JFileChooser jfc = new JFileChooser();
		int res = jfc.showOpenDialog(null);
		if (res == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile();
		}
		return null;
	}

	private void setDictionaryFile() {
		File dictFile = getDictionaryDialog();
		dictionaryFileDir = dictFile.getParent();
		dictionaryFile = dictFile.getName();
	}

	private void loadLogsToLogMiner(int dict) {
		switch (dict) {
		case 0: { // wgrywanie s³ownika z pliku
			setDictionaryFile();
			loadLogsToLogMiner();
			loadDictionaryFromFlatFile();
			break;
		}
		case 1: { // wgrywanie s³ownika z logów
			loadLogsToLogMiner();
			loadDictionaryFromRedoLogs();
			break;
		}
		default: { // wgrywanie bez s³ownika
			loadLogsToLogMiner();
			loadDictionaryFromOnlineCatalog();
			break;
		}
		}
	}

	public void unloadLogminer() {
		OracleQueries oq = new OracleQueries();
		oq.ddlOracle("BEGIN DBMS_LOGMNR.END_LOGMNR(); END;");

	}

	/**
	 * Ustawia pola wyborów w comboboxach filtrowania na podstawie danych z
	 * komórek bazy
	 */
	private void setCombos() {
		OracleQueries oq = new OracleQueries();
		// test
		Set<String> op = new TreeSet<String>();
		Set<String> tb = new TreeSet<String>();
		Set<String> us = new TreeSet<String>();
		ResultSet rs = oq.oracleToResultSet("select distinct operation, table_name, username from v$logmnr_contents ");
		try {
			while (rs.next()) {
				String opS = rs.getString(1);
				String opT = rs.getString(2);
				String opU = rs.getString(3);
				if (opS != null) {
					op.add(opS);
				}
				if (opT != null) {
					tb.add(opT);
				}
				if (opU != null) {
					us.add(opU);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		laryngologFrame.setCbOperation(op);
		laryngologFrame.setCbTable(tb);
		laryngologFrame.setCbUser(us);
	}

	/**
	 * Wczytuje logi z listboxa
	 */
	private void loadLogsToLogMiner() {
		String[] logList = cd.getLogs();
		StringBuilder logAdd = new StringBuilder("begin ");
		for (String logFile : logList) {
			logAdd.append("SYS.DBMS_LOGMNR.ADD_LOGFILE( LOGFILENAME => '" + logFile
					+ "', OPTIONS => SYS.DBMS_LOGMNR.ADDFILE); ");
		}
		logAdd.append(" end;");
		OracleQueries oq = new OracleQueries();
		oq.ddlOracle(logAdd.toString());
	}

	/**
	 * wczytywanie s³ownika z pliku
	 */
	private void loadDictionaryFromFlatFile() {
		if (dictionaryFileDir != null) {
			OracleQueries oq = new OracleQueries();
			oq.ddlOracle("begin SYS.DBMS_LOGMNR.START_LOGMNR(DICTFILENAME => '" + dictionaryFileDir + "\\"
					+ dictionaryFile + "', OPTIONS => DBMS_LOGMNR.NO_SQL_DELIMITER); end;");
		}
	}

	/**
	 * wczytywanie s³ownika z logów redo
	 */
	private void loadDictionaryFromRedoLogs() {
		OracleQueries oq = new OracleQueries();
		oq.ddlOracle(
				"BEGIN SYS.DBMS_LOGMNR.START_LOGMNR(OPTIONS => DBMS_LOGMNR.DICT_FROM_REDO_LOGS + DBMS_LOGMNR.NO_SQL_DELIMITER); END;");
	}

	/**
	 * wczytywanie bez s³ownika
	 */
	private void loadDictionaryFromOnlineCatalog() {
		OracleQueries oq = new OracleQueries();
		oq.ddlOracle(
				"BEGIN SYS.DBMS_LOGMNR.START_LOGMNR(OPTIONS => DBMS_LOGMNR.DICT_FROM_ONLINE_CATALOG + DBMS_LOGMNR.NO_SQL_DELIMITER); END;");
	}

	/**
	 * Sprawdza i ewentualnie zmienia parametr 'supplemental_log_data_min' na
	 * YES
	 */
	private void setYesSupplementalLogData() {
		OracleQueries oq = new OracleQueries();
		String supplementalLogDataMin = oq.oracleGetScalar("select supplemental_log_data_min from v$database");
		String message = "<html>Do dzia³ania programu konieczne jest wykonanie polecenia <br/>"
				+ "'alter database add supplemental log data;', czy wykonaæ je teraz?</html>";
		if ("NO".equals(supplementalLogDataMin)) {
			int confirm = JOptionPane.showConfirmDialog(null, message, "Uwaga", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				oq.ddlOracle("alter database add supplemental log data");
			}
		}
	}
}
