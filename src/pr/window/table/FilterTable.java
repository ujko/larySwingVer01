package pr.window.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pr.util.Utils;
import pr.window.LaryngologFrame;

@SuppressWarnings("serial")
public class FilterTable extends JTable implements ActionListener, MouseListener {
	JPopupMenu menu;
	JMenuItem saveAllDataToCsv;
	JMenuItem saveSelecteddataToCsv;
	private LaryngologFrame laryngologMF;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	public FilterTable(LaryngologFrame laryngologMF) {
		super();
		this.laryngologMF = laryngologMF;
		this.setCellSelectionEnabled(true);
		this.setAutoCreateRowSorter(true);
		menu = new JPopupMenu();
		saveAllDataToCsv = new JMenuItem("Zapisz ca³oœæ do pliku ");
		saveSelecteddataToCsv = new JMenuItem("Zapisz zaznaczone do pliku ");
		saveSelecteddataToCsv.addActionListener(this);
		saveAllDataToCsv.addActionListener(this);
		menu.add(saveSelecteddataToCsv);
		menu.add(saveAllDataToCsv);
		this.add(menu);
		addMouseListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == saveAllDataToCsv) {
			saveAllToCsv();
		}
		if (source == saveSelecteddataToCsv) {
			saveSelectedToCsv();
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * Ustawia szerokoœæ kolumn w JTable
	 */
	public void setColumnWidtth() {
		TableColumn column = null;
		for (int i = 0; i < 6; i++) {
			column = this.getColumnModel().getColumn(i);
			switch (i) {
			case 0:
				column.setPreferredWidth(105); // time_stamp
				break;
			case 1:
				column.setPreferredWidth(60); // operation
				break;
			case 2:
				column.setPreferredWidth(60); // table_name
				break;
			case 3:
				column.setPreferredWidth(40); // username
				break;
			case 4:
				column.setPreferredWidth(250); // os_username //sql_redo
				break;
			case 5:
				column.setPreferredWidth(250); // machine_name //sql_undo
				break;
			// case 6:
			// column.setPreferredWidth(200); //sql_redo
			// break;
			// case 7:
			// column.setPreferredWidth(200); //sql_undo
			// break;
			default:
				column.setPreferredWidth(50);
			}
		}
	}

	/**
	 * Zapisuje zaznaczon¹ zawartoœæ tabeli do wybranego pliku csv
	 */
	private void saveSelectedToCsv() {
		Utils utils = new Utils();
		File csvFile = utils.createNewFile("Excell csv", "csv");
		if (csvFile == null) {
			JOptionPane.showMessageDialog(null, "Nie uda³o siê zapisaæ do pliku ", "B³¹d", JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				PrintWriter csvWriter = new PrintWriter(csvFile);
				int[] colSelected = getSelectedColumns();
				int[] rowSelected = getSelectedRows();
				for (int i = 0; i < rowSelected.length; i++) {
					for (int j = 0; j < colSelected.length; j++) {
						csvWriter.print(getValueAt(rowSelected[i], colSelected[j]) + "\t");
					}
					csvWriter.println();
				}
				csvWriter.flush();
				csvWriter.close();
				JOptionPane.showMessageDialog(null, "Zapisane do pliku: " + csvFile.getAbsolutePath(), "Zapisane",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Nie uda³o siê zapisaæ do pliku: " + csvFile.getAbsolutePath(),
						"B³¹d", JOptionPane.ERROR_MESSAGE);
				logger.error(e1.getMessage());
			}
		}
	}

	/**
	 * Zapisuje ca³¹ zawartoœæ tabeli do wybranego pliku csv
	 */
	private void saveAllToCsv() {
		Utils utils = new Utils();
		File csvFile = utils.createNewFile("Excell csv", "csv");
		if (csvFile == null) {
			JOptionPane.showMessageDialog(null, "Nie uda³o siê zapisaæ do pliku ", "B³¹d", JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				PrintWriter csvWriter = new PrintWriter(csvFile);
				int colCount = getColumnCount();
				int rowCount = getRowCount();
				for (int i = 0; i < rowCount; i++) {
					for (int j = 0; j < colCount; j++) {
						csvWriter.print(getValueAt(i, j) + "\t");
					}
					csvWriter.println();
				}
				csvWriter.flush();
				csvWriter.close();
				JOptionPane.showMessageDialog(null, "Zapisane do pliku: " + csvFile.getAbsolutePath(), "Zapisane",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Nie uda³o siê zapisaæ do pliku: " + csvFile.getAbsolutePath(),
						"B³¹d", JOptionPane.ERROR_MESSAGE);
				logger.error(e1.getMessage());
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			laryngologMF.setTaResult(laryngologMF.getSelectedDataFromJTable());
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			menu.show(e.getComponent(), e.getX(), e.getY());
			menu.setVisible(true);
		}

	}
}
