package pr.window.lists;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class LogsToLoadList extends JList<String> {
	private static DefaultListModel<String> logListModel = new DefaultListModel<String>();

	public LogsToLoadList() {
		super(logListModel);
	}

	public void setLogListModel(List<String> list) {
		for (String x : list) {
			logListModel.addElement(x);
		}
	}

	public void removeFromLogsToLoadList() {
		logListModel.removeAllElements();
	}

	public void removeSelectedFromLogsToLoadList(int[] selectedItems) {
		for (int i = 0; i < selectedItems.length; i++) {
			logListModel.removeElementAt(selectedItems[i] - i);
		}
	}

	public String[] getLogsToLoadList() {
		String[] logs = new String[logListModel.getSize()];
		for (int i = 0; i < logListModel.getSize(); i++) {
			logs[i] = logListModel.get(i);
		}
		return logs;
	}

	public void getLogs() {
		String logCatalog = JOptionPane.showInputDialog(null, "Podaj katalog logów", "Katalog logów",
				JOptionPane.INFORMATION_MESSAGE);
		File logCatalogFile = new File(logCatalog);
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String fileName) {
				return fileName.endsWith(".log") || fileName.endsWith(".arc");
			}
		};
		if (logCatalogFile.exists()) {
			String[] logFiles = logCatalogFile.list(filter);
			for (String x : logFiles) {
				logListModel.addElement(logCatalogFile + "\\" + x);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Katalog '" + logCatalogFile + "' nie istnieje", "Brak katalogu",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void getOtherLogs() {
		File[] selectedFiles = getSelectedFiles();
		for (File f : selectedFiles) {
			logListModel.addElement(f.getAbsolutePath());
		}
	}

	private File[] getSelectedFiles() {
		JFileChooser addLogsDialog = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivelog (*.log) & (*.arc)", "log", "arc");
		addLogsDialog.setMultiSelectionEnabled(true);
		addLogsDialog.setFileFilter(filter);
		int res = addLogsDialog.showOpenDialog(this);
		if (res == JFileChooser.APPROVE_OPTION) {
			return addLogsDialog.getSelectedFiles();
		}
		return null;
	}
}
