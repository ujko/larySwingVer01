package pr.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;

import pr.oracle.OracleQueries;

public class Utils {
	public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// format
																					// wyœwietlania
																					// dat

	public static MaskFormatter dateMask() {
		MaskFormatter dateMask = null;
		try {
			dateMask = new MaskFormatter("####-##-## ##:##");
		} catch (ParseException e1) {
			Utils utils = new Utils();
			utils.saveErrorToFile("Utils-001", e1.getMessage());
			e1.printStackTrace();
		}
		return dateMask;
	}

	/**
	 * Zapis b³êdu (parametr err) do pliku Laryngo.log
	 * 
	 * @param err
	 */
	public void saveErrorToFile(String errNo, String err) {
		FileWriter file = null;
		try {
			file = new FileWriter("Laryngo.log", true);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Nie moge zapisaæ do pliku 'laryngo.log'", "B³¹d",
					JOptionPane.ERROR_MESSAGE);
		}
		PrintWriter writer = new PrintWriter(file);
		writer.println("\t" + new Date() + "\t" + errNo + "\t");
		writer.println(err);
		writer.flush();
		writer.close();
	}

	/**
	 * Wpisuje ResultSet do JTable
	 * 
	 * @param query
	 * @return TableModel
	 */
	public TableModel showDataInJTable(String query) {
		OracleQueries oq = new OracleQueries();
		ResultSet rs = oq.oracleToResultSet(query);
		try {
			ResultSetMetaData metaData = rs.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			Vector<String> columnNames = new Vector<>();
			for (int column = 1; column <= numberOfColumns; column++) {
				columnNames.addElement(metaData.getColumnLabel(column));
			}
			Vector<Object> rows = new Vector<>();
			while (rs.next()) {
				Vector<Object> newRow = new Vector<>();
				for (int i = 1; i <= numberOfColumns; i++) {
					newRow.addElement(rs.getObject(i));
				}
				rows.addElement(newRow);
			}
			return new DefaultTableModel(rows, columnNames);
		} catch (Exception e) {
			e.printStackTrace();
			Utils utils = new Utils();
			utils.saveErrorToFile("Utils-002", e.getMessage());
			return null;
		}
	}

	/**
	 * Wywo³uje okno zapisu pliku do wybranego formatu
	 * 
	 * @param name
	 *            - nazwa formatu (np. "Excell *.csv")
	 * @param ext
	 *            - rozszerzenie(np. "csv")
	 * @return file
	 */
	public File createNewFile(String name, String ext) {
		File newFile = null;
		FileNameExtensionFilter filter = new FileNameExtensionFilter(name, ext);
		JFileChooser chooser = new JFileChooser(new File("").getAbsolutePath());
		chooser.setFileFilter(filter);
		int val = chooser.showSaveDialog(null);
		if (val == JFileChooser.APPROVE_OPTION) {
			newFile = chooser.getSelectedFile();
			String fileName = newFile.getName();
			if (!fileName.contains(".")) {
				newFile = new File(newFile.getAbsoluteFile() + "." + ext);
			}
		}
		if (newFile.exists()) {
			int answer = JOptionPane.showConfirmDialog(null,
					"Plik: " + newFile.getAbsolutePath() + " istnieje. Nadpisaæ?", "Uwaga!", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.NO_OPTION) {
				return null;
			}
		}
		return newFile;
	}

	public void zipFiles(List<File> listFiles, String destZipFile) {
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
			for (File file : listFiles) {
				zipFile(file, zos);
			}
			zos.flush();
			zos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void zipFile(File file, ZipOutputStream zos) {
		int bufferSize = 4096;
		try {
			zos.putNextEntry(new ZipEntry(file.getName()));
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			byte[] bytesIn = new byte[bufferSize];
			int read = 0;
			while ((read = bis.read(bytesIn)) != -1) {
				zos.write(bytesIn, 0, read);
			}
			bis.close();
			zos.closeEntry();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
