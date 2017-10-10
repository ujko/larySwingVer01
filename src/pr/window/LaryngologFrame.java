package pr.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.joda.time.DateTime;

import pr.connectionData.ConnectionData;
import pr.oracle.OracleQueries;
import pr.util.Utils;
import pr.window.buttons.ConnectButton;
import pr.window.buttons.FilterButton;
import pr.window.buttons.GraphButton;
import pr.window.buttons.SaveToXmlButton;
import pr.window.lists.LogsToLoadList;
import pr.window.lists.TaResult;
import pr.window.lists.UsersList;
import pr.window.table.FilterTable;

@SuppressWarnings("serial")
public class LaryngologFrame extends JFrame implements ActionListener {
	WindowAdapter windowAdapter = null;
	private JTabbedPane tabbedPane;
	private JTextField connectionNameTextField; // nazwa w³asna po³¹czenia
												// potrzebna tylko dla zapisu do
												// xml
	private JTextField userTextField; // nazwa u¿ytkownika
	private JPasswordField passTextField; // has³o u¿ytkownika
	private JTextField serverNameTextField; // nazwa serwera
	private JTextField portNameTextField; // numer portu
	private JTextField serviceNameTextField; // nazwa serwisu Oracle
	private JLabel utlFileDirLabel; // Wyœwietla katalog s³ownika
	private LogsToLoadList ltlList; // Lista logów do wczytania
	private ConnectButton connectButton; // button po³¹czenia
	private JButton btnStandardLogs; // logi bazy do wczytywania
	private JButton btnArchiveLogs; // archive logi do wczytywania
	private JButton btnOtherLogs; // dowolne logi do wczytywania
	private JButton btnClearAllFromLtlList; // czyœci ltlList
	private JButton btnClearSelectedFromLtlList; // kasuje zaznaczone elementy z
													// ltlList
	private JButton btnGenerateDictToFlatFile; // generuje plik s³ownika do
												// pliku
	private JButton btnGenerateDictToRedoLogs; // generuje plik s³ownika do
												// plików redo log
	private JRadioButton jrbtnDictionaryFromFile, jrbtnDictionaryFromLog, jrbtnDictionaryNone; // wybór
																								// s³ownika
																								// (z
																								// pliku,
																								// logu,
																								// brak)
	// ***********************************Zapytania**************************************************
	private JDesktopPane showData;
	private JSplitPane showDataSplitPane;
	private JTextField tfInstruction; // filtr Polecenie (sql_undo)
	private JFormattedTextField tfDateFrom; // filtr data od
	private JFormattedTextField tfDateTo; // filtr data do
	private JComboBox<String> cbOperation; // filtr operacja
	private JComboBox<String> cbTable; // filtr table_name
	private JComboBox<String> cbUser; // filtr user_name
	private TableModel showDataTableModel;
	private FilterTable showDataJTable;
	private TaResult taResult; // wyœwietla podœwietlony wynik z tabeli w
								// TextArea
	// **********************************Wykresy****************************************************
	private JFormattedTextField tfDateFromG; // filtr data od dla wykresów
	private JFormattedTextField tfDateToG; // filtr data do dla wykresów
	private ChartPanel chartPanel; // Panel wyœwietlania wykresów
	private JComboBox<String> cbUserGraph; // filtr user_name dla wykresów
	private JComboBox<String> chooseGraph; // wybór wykresu
	private JComboBox<String> cbOperationGraph; // wybór operacji dla wykresów

	public void clearLtlList() {
		ltlList.removeFromLogsToLoadList();
	}

	public void setActiveTab() {
		int tabIndex = 1; // tabbedPane Wyniki
		tabbedPane.setSelectedIndex(tabIndex);
	}

	public int getPositionFromChooseGraph() {
		return chooseGraph.getSelectedIndex();
	}

	public String getTfDateFromG() {
		return tfDateFromG.getText();
	}

	public String getTfDateToG() {
		return tfDateToG.getText();
	}

	public String getCbUserGraph() {
		return cbUserGraph.getSelectedItem().toString();
	}

	public String getCbOperationGraph() {
		return cbOperationGraph.getSelectedItem().toString();
	}

	public void showGraphInChartPanel(JFreeChart chart) {
		chartPanel.setChart(chart);
	}

	public String getSelectedDataFromJTable() {
		int selectedRow = showDataJTable.getSelectedRow();
		int selectedCol = showDataJTable.getSelectedColumn();
		if (selectedCol == 0) {
			return "";
		} else {
			return (String) showDataJTable.getValueAt(selectedRow, selectedCol);
		}
	}

	public String getTaResult() {
		return taResult.getText();
	}

	public void setTaResult(String result) {
		taResult.setText(result);
	}

	public String getCbUser() {
		return cbUser.getSelectedItem().toString();
	}

	public void setCbUser(Set<String> rs) {
		cbUser.addItem("All");
		cbUser.addItem("No sys");
		cbUserGraph.addItem("All");
		cbUserGraph.addItem("No sys");
		for (String s : rs) {
			cbUser.addItem(s);
			cbUserGraph.addItem(s);
		}
	}

	public String getCbTable() {
		return String.valueOf(cbTable.getSelectedItem());
	}

	public void setCbTable(Set<String> rs) {
		cbTable.removeAllItems();
		cbTable.addItem("All");
		for (String s : rs) {
			cbTable.addItem(s);
		}
	}

	public String getCbOperation() {
		return String.valueOf(cbOperation.getSelectedItem());
	}

	public void setCbOperation(Set<String> rs) {
		cbOperation.removeAllItems();
		cbOperation.addItem("All");
		for (String s : rs) {
			cbOperation.addItem(s);
		}
	}

	public String getTfDateFrom() {
		return tfDateFrom.getText();
	}

	public String getTfDateTo() {
		return tfDateTo.getText();
	}

	public String getTfInstruction() {
		return tfInstruction.getText();
	}

	public void showJTable(String query) {
		Utils utils = new Utils();
		showDataTableModel = utils.showDataInJTable(query);
		showDataJTable.setModel(showDataTableModel);
		showDataJTable.setColumnWidtth();
	}

	public void filterInJtable(String textToFilter) {
		TableRowSorter<TableModel> trs = new TableRowSorter<TableModel>(showDataTableModel);
		showDataJTable.setRowSorter(trs);
		trs.setRowFilter(RowFilter.regexFilter("(?i)" + textToFilter));
	}
	// test 25022016 combo na razie nie u¿ywane
	// public Set<String> setComboTables(int column){
	// Set<String> tablesSet = new TreeSet<String>();
	// for(int i = 0; i<showDataTableModel.getRowCount();i++){
	// String s = (String)showDataTableModel.getValueAt(i, column);
	// if(s!=null){
	// tablesSet.add(s);
	// }
	// }
	// }

	public void setUtlFileDirLabel(String utlFileDir) {
		utlFileDirLabel.setText("Katalog pliku s³ownika - " + utlFileDir);
	}

	public void setNameTextField(String nameTextField) {
		this.connectionNameTextField.setText(nameTextField);
	}

	public void setUserTextField(String userTextField) {
		this.userTextField.setText(userTextField);
	}

	public void setPassTextField(String passTextField) {
		this.passTextField.setText(passTextField);
	}

	public void setServerNameTextField(String serverNameTextField) {
		this.serverNameTextField.setText(serverNameTextField);
	}

	public void setPortNameTextField(String portNameTextField) {
		this.portNameTextField.setText(portNameTextField);
	}

	public void setServiceNameTextField(String serviceNameTextField) {
		this.serviceNameTextField.setText(serviceNameTextField);
	}

	public boolean isLogsToLoad() {
		if (ltlList.getModel().getSize() == 0) {
			JOptionPane.showMessageDialog(null, "Nie wprowadzi³eœ ¿adnych logów");
			return false;
		} else {
			return true;
		}
	}

	public boolean isConnectionData() {
		if ("".equals(userTextField.getText())) {
			JOptionPane.showMessageDialog(null, "Nie wpisa³eœ nazwy u¿ytkownika Oracle");
			return false;
		}
		if ("".equals(new String(passTextField.getPassword()))) {
			JOptionPane.showMessageDialog(null, "Nie wpisa³eœ has³a");
			return false;
		}
		if ("".equals(serverNameTextField.getText())) {
			JOptionPane.showMessageDialog(null, "Nie wpisa³eœ nazwy serwera");
			return false;
		}
		if ("".equals(portNameTextField.getText())) {
			JOptionPane.showMessageDialog(null, "Nie wpisa³eœ numeru portu");
			return false;
		}
		if ("".equals(serviceNameTextField.getText())) {
			JOptionPane.showMessageDialog(null, "Nie wpisa³eœ nazwy serwisu");
			return false;
		}
		return true;
	}

	public ConnectionData setConnectionData() {
		ConnectionData cd = new ConnectionData();
		cd.setConnectionName(connectionNameTextField.getText());
		cd.setUserName(this.userTextField.getText());
		cd.setPassword(this.passTextField.getPassword());
		cd.setServerName(this.serverNameTextField.getText());
		cd.setServerPort(this.portNameTextField.getText());
		cd.setServiceName(this.serviceNameTextField.getText());
		cd.setLogs(ltlList.getLogsToLoadList());
		if (jrbtnDictionaryFromFile.isSelected()) {
			cd.setDictionary(0);
		} else if (jrbtnDictionaryFromLog.isSelected()) {
			cd.setDictionary(1);
		} else if (jrbtnDictionaryNone.isSelected()) {
			cd.setDictionary(2);
		}
		return cd;
	}

	public LaryngologFrame() {
		super("LaryngoLog");
		inintUI();
	}

	private void inintUI() {
		Dimension tfDimension = new Dimension(120, 27); // rozmiar pól
														// tekstowych
		Color labelColor = new Color(255, 255, 255);
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Dimension minFrameSize = new Dimension(1000, 600); // Minimalny rozmiar
															// okna aplikacji
		setLocation(180, 80);
		setResizable(true);
		setMinimumSize(minFrameSize);
		windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				closeLaryngoLog();
			}
		};

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(windowAdapter);
		// ************************Panel gówny JTabbedPane*******************
		tabbedPane = new JTabbedPane();
		// ************************Panel po³¹czenia**************************
		JDesktopPane connectPane = new JDesktopPane();
		// ************************Komponenty Panelu po³¹czenia**************
		// ************************Nazwa w³asna po³¹czenia*******************
		connectionNameTextField = new JTextField();
		connectionNameTextField.setSize(tfDimension);
		connectionNameTextField.setLocation(10, 20);
		connectionNameTextField.setToolTipText("Nasza nazwa po³¹czenia (nie u¿ywaæ spacji)");
		JLabel nameLabel = new JLabel("Nazwa");
		nameLabel.setBounds(10, 5, 160, 15);
		nameLabel.setForeground(labelColor);
		nameLabel.setDisplayedMnemonic(KeyEvent.VK_N);
		nameLabel.setLabelFor(connectionNameTextField);
		connectPane.add(nameLabel);
		connectPane.add(connectionNameTextField);
		// ************************Nazwa u¿ytkownika Oracle******************
		userTextField = new JTextField("sys as sysdba");
		userTextField.setSize(tfDimension);
		userTextField.setLocation(10, 60);
		userTextField.setToolTipText("Nazwa u¿ytkownika Oracle - mo¿na wpisaæ sys as sysdba lub sys as sysoper");
		JLabel userLabel = new JLabel("U¿ytkownik");
		userLabel.setBounds(10, 45, 160, 15);
		userLabel.setForeground(labelColor);
		userLabel.setDisplayedMnemonic(KeyEvent.VK_U);
		userLabel.setLabelFor(userTextField);
		connectPane.add(userLabel);
		connectPane.add(userTextField);
		// ************************Has³o u¿ytkownika Oracle******************
		passTextField = new JPasswordField();
		passTextField.setSize(tfDimension);
		passTextField.setLocation(10, 100);
		JLabel passLabel = new JLabel("Has³o");
		passLabel.setBounds(10, 85, 160, 15);
		passLabel.setForeground(labelColor);
		passTextField.setToolTipText("Has³o u¿ytkownika Oracle");
		passLabel.setDisplayedMnemonic(KeyEvent.VK_H);
		passLabel.setLabelFor(passTextField);
		connectPane.add(passLabel);
		connectPane.add(passTextField);
		// ************************Nazwa serwera Oracle**********************
		serverNameTextField = new JTextField("localhost");
		serverNameTextField.setSize(tfDimension);
		serverNameTextField.setLocation(10, 140);
		JLabel serverNameLabel = new JLabel("Serwer");
		serverNameLabel.setBounds(10, 125, 160, 15);
		serverNameLabel.setForeground(labelColor);
		serverNameLabel.setDisplayedMnemonic(KeyEvent.VK_S);
		serverNameLabel.setLabelFor(serverNameTextField);
		serverNameTextField.setToolTipText("Nazwa lub IP serwera z Oracle");
		connectPane.add(serverNameLabel);
		connectPane.add(serverNameTextField);
		// ************************Numer portu Oracle************************
		portNameTextField = new JTextField("1521");
		portNameTextField.setSize(tfDimension);
		portNameTextField.setLocation(10, 180);
		JLabel portNameLabel = new JLabel("Port");
		portNameLabel.setBounds(10, 165, 160, 15);
		portNameLabel.setForeground(labelColor);
		portNameLabel.setDisplayedMnemonic(KeyEvent.VK_P);
		portNameLabel.setLabelFor(portNameTextField);
		portNameTextField.setToolTipText("Numer portu dla po³¹czenia Oracle");
		connectPane.add(portNameLabel);
		connectPane.add(portNameTextField);
		// ************************Nazwa serwisu Oracle**********************
		serviceNameTextField = new JTextField("orcl");
		serviceNameTextField.setSize(tfDimension);
		serviceNameTextField.setLocation(10, 220);
		JLabel serviceNameLabel = new JLabel("Serwis Oracle");
		serviceNameLabel.setBounds(10, 205, 160, 15);
		serviceNameLabel.setForeground(labelColor);
		serviceNameLabel.setDisplayedMnemonic(KeyEvent.VK_E);
		serviceNameLabel.setLabelFor(serviceNameTextField);
		serviceNameTextField.setToolTipText("Nazwa serwisu Oracle");
		connectPane.add(serviceNameLabel);
		connectPane.add(serviceNameTextField);
		// ************************Button po³¹czenia z baz¹******************
		connectButton = new ConnectButton(this);
		connectButton.setBounds(50, 350, 170, 70);
		connectButton.setToolTipText("Po³¹cz z baz¹ Oracle");
		connectPane.add(connectButton);
		// ************************Button zapisywanie danych do XML**********
		SaveToXmlButton sXmlButton = new SaveToXmlButton(this);
		sXmlButton.setBounds(180, 20, 100, 20);
		sXmlButton.setToolTipText("Zapisz dane po³¹czenia Oracle");
		connectPane.add(sXmlButton);
		// ************************Informacja o katalogu utl_file_dir********
		utlFileDirLabel = new JLabel();
		utlFileDirLabel.setForeground(labelColor);
		utlFileDirLabel.setBounds(300, 220, 400, 20);
		connectPane.add(utlFileDirLabel);
		// ************************Lista loginów*****************************
		JLabel uListLabel = new JLabel("Uzytkownicy");
		uListLabel.setBounds(180, 45, 160, 15);
		uListLabel.setForeground(labelColor);
		connectPane.add(uListLabel);
		UsersList uList = new UsersList(this);
		uList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		uList.setToolTipText("Lista zapisanych loginów");
		JScrollPane listScrollPane = new JScrollPane(uList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScrollPane.setBorder(new LineBorder(Color.BLACK));
		listScrollPane.setBounds(180, 60, 100, 180);
		connectPane.add(listScrollPane);
		// ************************Lista logów do zaimportowania*************
		JLabel ltlListLabel = new JLabel("Lista logów do zaimportowania");
		ltlListLabel.setBounds(300, 45, 200, 15);
		ltlListLabel.setForeground(labelColor);
		connectPane.add(ltlListLabel);
		ltlList = new LogsToLoadList();
		ltlList.setToolTipText("Lista logów do zaimportowania");
		JScrollPane ltlScrollPane = new JScrollPane(ltlList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ltlScrollPane.setBorder(new LineBorder(Color.BLACK));
		ltlScrollPane.setBounds(300, 60, 550, 80);
		connectPane.add(ltlScrollPane);
		// ************************Radiobutton sk¹d s³ownik******************
		ButtonGroup jrbtnDictionaryGroup = new ButtonGroup(); // Grupa
		// ************************Z pliku dictionary.ora********************
		jrbtnDictionaryFromFile = new JRadioButton("S³ownik z pliku zewnêtrznego"); // Z
																					// pliku
		jrbtnDictionaryFromFile.setBounds(10, 265, 200, 15);
		jrbtnDictionaryFromFile.setForeground(labelColor);
		jrbtnDictionaryFromFile.setSelected(false);
		jrbtnDictionaryFromFile.addActionListener(this);
		jrbtnDictionaryFromFile.setBackground(getForeground());
		connectPane.add(jrbtnDictionaryFromFile);
		jrbtnDictionaryGroup.add(jrbtnDictionaryFromFile);
		// ************************Z redo logów*****************************
		jrbtnDictionaryFromLog = new JRadioButton("S³ownik z logów");
		jrbtnDictionaryFromLog.setBounds(10, 290, 200, 15);
		jrbtnDictionaryFromLog.setForeground(labelColor);
		jrbtnDictionaryGroup.add(jrbtnDictionaryFromLog);
		jrbtnDictionaryFromLog.addActionListener(this);
		jrbtnDictionaryFromLog.setBackground(getForeground());
		connectPane.add(jrbtnDictionaryFromLog);
		// ************************Bez s³ownika***************************
		jrbtnDictionaryNone = new JRadioButton("Nie wczytuj s³ownika");
		jrbtnDictionaryNone.setBounds(10, 315, 200, 15);
		jrbtnDictionaryNone.setForeground(labelColor);
		jrbtnDictionaryGroup.add(jrbtnDictionaryNone);
		jrbtnDictionaryNone.setBackground(getForeground());
		jrbtnDictionaryNone.setSelected(true);
		jrbtnDictionaryNone.addActionListener(this);
		connectPane.add(jrbtnDictionaryNone);
		// ************************Button generowanie nowego s³ownika********
		btnGenerateDictToFlatFile = new JButton("Generuj s³ownik do pliku");
		btnGenerateDictToFlatFile.setBounds(220, 260, 170, 25);
		btnGenerateDictToFlatFile.addActionListener(this);
		connectPane.add(btnGenerateDictToFlatFile);
		// ************************Button generowanie do logów redo**********
		btnGenerateDictToRedoLogs = new JButton("Generuj s³ownik do logów");
		btnGenerateDictToRedoLogs.setBounds(220, 285, 170, 25);
		btnGenerateDictToRedoLogs.addActionListener(this);
		connectPane.add(btnGenerateDictToRedoLogs);
		// ************************Buttony - dodawanie logów*****************
		JLabel lblAddingLogs = new JLabel("Dodaj logi");
		lblAddingLogs.setBounds(300, 150, 200, 20);
		lblAddingLogs.setForeground(labelColor);
		connectPane.add(lblAddingLogs);
		// ************************Button standardowe logi*******************
		btnStandardLogs = new JButton("Systemu");
		btnStandardLogs.setBounds(300, 175, 100, 20);
		btnStandardLogs.addActionListener(this);
		connectPane.add(btnStandardLogs);
		// ************************Button archive logi***********************
		btnArchiveLogs = new JButton("Archive");
		btnArchiveLogs.setBounds(410, 175, 100, 20);
		btnArchiveLogs.addActionListener(this);
		connectPane.add(btnArchiveLogs);
		// ************************Button inne logi***************************
		btnOtherLogs = new JButton("Inne");
		btnOtherLogs.setBounds(520, 175, 100, 20);
		btnOtherLogs.addActionListener(this);
		connectPane.add(btnOtherLogs);
		// ************************Buttony usuwanie***************************
		JLabel lblDeleteLogs = new JLabel("Usuñ");
		lblDeleteLogs.setBounds(640, 150, 200, 20);
		lblDeleteLogs.setForeground(labelColor);
		connectPane.add(lblDeleteLogs);
		// ************************Button wyczyœæ wszystko********************
		btnClearAllFromLtlList = new JButton("Wszystko");
		btnClearAllFromLtlList.setBounds(640, 175, 100, 20);
		btnClearAllFromLtlList.addActionListener(this);
		connectPane.add(btnClearAllFromLtlList);
		// ************************Button wyczyœæ zaznaczone*****************
		btnClearSelectedFromLtlList = new JButton("Wybrane");
		btnClearSelectedFromLtlList.setBounds(750, 175, 100, 20);
		btnClearSelectedFromLtlList.addActionListener(this);
		connectPane.add(btnClearSelectedFromLtlList);
		// ************************Logo**************************************
		JLabel logoLabel = new JLabel();
		ImageIcon logo = new ImageIcon("logo.png");
		logoLabel.setIcon(logo);
		logoLabel.setBounds(360, 280, 500, 180);
		connectPane.add(logoLabel);
		// ******************************************************************
		// ************************Panel wyœwietlania zapytañ****************
		showData = new JDesktopPane();
		// ************************Button Filtruj****************************
		FilterButton filterButton = new FilterButton(this);
		filterButton.setBounds(860, 5, 100, 40);
		showData.add(filterButton);
		// ************************Data od***********************************
		int timeMinus = 3; // wyœwietlany czas - teraz minus iloœæ godzin
		JLabel labelDateFrom = new JLabel("Data Od:");
		labelDateFrom.setBounds(10, 5, 100, 15);
		labelDateFrom.setForeground(labelColor);
		showData.add(labelDateFrom);
		DateTime df = new DateTime(new Date());
		Date dateFrom = df.minusHours(timeMinus).toDate();
		tfDateFrom = new JFormattedTextField(Utils.df.format(dateFrom));
		tfDateFrom.setSize(tfDimension);
		tfDateFrom.setLocation(10, 20);
		showData.add(tfDateFrom);
		Utils.dateMask().install(tfDateFrom);
		// ************************Data do***********************************
		int timePlus = 10; // wyœwietlany czas - teraz plus iloœæ godzin
		JLabel labelDateTo = new JLabel("Data Do:");
		labelDateTo.setBounds(135, 5, 100, 15);
		labelDateTo.setForeground(labelColor);
		showData.add(labelDateTo);
		DateTime dt = new DateTime(new Date());
		Date DateTo = dt.plusHours(timePlus).toDate();
		tfDateTo = new JFormattedTextField(Utils.df.format(DateTo));
		tfDateTo.setSize(tfDimension);
		tfDateTo.setLocation(135, 20);
		showData.add(tfDateTo);
		Utils.dateMask().install(tfDateTo);
		// ************************ComboBox Operacja**************************
		JLabel labelOperation = new JLabel("Operacja");
		labelOperation.setBounds(260, 5, 100, 15);
		labelOperation.setForeground(labelColor);
		showData.add(labelOperation);
		cbOperation = new JComboBox<>();
		cbOperation.setSize(tfDimension);
		cbOperation.setLocation(260, 20);
		cbOperation.setBackground(getForeground());
		cbOperation.setEditable(false);
		showData.add(cbOperation);
		// ************************ComboBox Tabela**************************
		JLabel labelTable = new JLabel("Tabela");
		labelTable.setBounds(385, 5, 100, 15);
		labelTable.setForeground(labelColor);
		showData.add(labelTable);
		cbTable = new JComboBox<String>();
		cbTable.setSize(tfDimension);
		cbTable.setLocation(385, 20);
		cbTable.setBackground(getForeground());
		cbTable.setEditable(false);
		showData.add(cbTable);
		// ************************ComboBox U¿ytkownik***********************
		JLabel labelUser = new JLabel("U¿ytkownik");
		labelUser.setBounds(510, 5, 100, 15);
		labelUser.setForeground(labelColor);
		showData.add(labelUser);
		cbUser = new JComboBox<String>();
		cbUser.setSize(tfDimension);
		cbUser.setLocation(510, 20);
		cbUser.setBackground(getForeground());
		cbUser.setEditable(false);
		showData.add(cbUser);
		// ************************TextField Polecenie***********************
		JLabel labelInstruction = new JLabel("Polecenie");
		labelInstruction.setBounds(635, 5, 100, 15);
		labelInstruction.setForeground(labelColor);
		showData.add(labelInstruction);
		tfInstruction = new JTextField();
		tfInstruction.setSize(tfDimension.width + 100, tfDimension.height);
		tfInstruction.setLocation(635, 20);
		tfInstruction.addActionListener(this);
		showData.add(tfInstruction);
		// ************************JTable z wynikami*************************
		showDataJTable = new FilterTable(this);
		// ************************TextArea - podœwietlony wynik*************
		taResult = new TaResult(this);
		taResult.setLineWrap(true);
		JScrollPane showDataScrollPane = new JScrollPane(showDataJTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JSplitPane showTableSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, showDataScrollPane, taResult);
		showTableSplitPane.setDividerLocation(400);
		showDataSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, showData, showTableSplitPane);
		showDataSplitPane.setDividerLocation(50);
		// ******************************************************************
		// ************Komponenty Panelu wykresów****************************
		// ************************Panel wykresów****************************
		JPanel showGraphPane = new JPanel();
		JSeparator separator = new JSeparator();
		showGraphPane.setLayout(new BorderLayout());
		JToolBar showGraphTB = new JToolBar();
		showGraphTB.setRollover(false);
		showGraphTB.setFloatable(false);
		showGraphPane.add(showGraphTB, BorderLayout.NORTH);
		chartPanel = new ChartPanel(null);
		showGraphPane.add(chartPanel, BorderLayout.CENTER);
		Dimension graphLabelsDim = new Dimension(110, 25);
		// ************************Data od***********************************
		int tMinus = 3; // wyœwietlany czas - teraz minus iloœæ godzin
		JLabel labelDateFromGraph = new JLabel("Data Od:");
		showGraphTB.add(labelDateFromGraph);
		DateTime dfG = new DateTime(new Date());
		Date dateFromG = dfG.minusHours(tMinus).toDate();
		tfDateFromG = new JFormattedTextField(Utils.df.format(dateFromG));
		tfDateFromG.setPreferredSize(graphLabelsDim);
		tfDateFromG.setMinimumSize(graphLabelsDim);
		tfDateFromG.setMaximumSize(graphLabelsDim);
		labelDateFromGraph.setLabelFor(tfDateFromG);
		showGraphTB.add(tfDateFromG);
		Utils.dateMask().install(tfDateFromG);
		// ************************Data do***********************************
		int timePlusG = 10; // wyœwietlany czas - teraz plus iloœæ godzin
		JLabel labelDateToGraph = new JLabel("Data Do:");
		showGraphTB.add(labelDateToGraph);
		DateTime dtG = new DateTime(new Date());
		Date dateToG = dtG.plusHours(timePlusG).toDate();
		tfDateToG = new JFormattedTextField(Utils.df.format(dateToG));
		tfDateToG.setPreferredSize(graphLabelsDim);
		tfDateToG.setMinimumSize(graphLabelsDim);
		tfDateToG.setMaximumSize(graphLabelsDim);
		showGraphTB.add(tfDateToG);
		Utils.dateMask().install(tfDateToG);
		// ************************Combobox - wybór u¿ytkownika**************
		showGraphTB.add(new JLabel("U¿ytkownik: "));
		cbUserGraph = new JComboBox<String>();
		cbUserGraph.setPreferredSize(graphLabelsDim);
		cbUserGraph.setMinimumSize(graphLabelsDim);
		cbUserGraph.setMaximumSize(graphLabelsDim);
		showGraphTB.add(cbUserGraph);
		showGraphTB.add(separator);
		// ************************Combobox - wybór wykresu******************
		showGraphTB.add(new JLabel("Wykres: "));
		String[] comboItems = { "U¿ycie tabel", "U¿ytkownicy" };
		chooseGraph = new JComboBox<>(comboItems);
		chooseGraph.setPreferredSize(graphLabelsDim);
		chooseGraph.setMinimumSize(graphLabelsDim);
		chooseGraph.setMaximumSize(graphLabelsDim);
		showGraphTB.add(chooseGraph);
		showGraphTB.add(separator);
		// ************************Combobox - wybór wykresu******************
		showGraphTB.add(new JLabel("Operacja: "));
		String[] comboOperations = { "All", "INSERT", "UPDATE", "DELETE" };
		cbOperationGraph = new JComboBox<>(comboOperations);
		cbOperationGraph.setPreferredSize(graphLabelsDim);
		cbOperationGraph.setMinimumSize(graphLabelsDim);
		cbOperationGraph.setMaximumSize(graphLabelsDim);
		showGraphTB.add(cbOperationGraph);
		showGraphTB.add(separator);
		// ************************Button Poka¿******************************
		GraphButton graphButton = new GraphButton(this);
		Dimension btDimension = new Dimension(70, 25);
		graphButton.setPreferredSize(btDimension);
		graphButton.setMinimumSize(btDimension);
		graphButton.setMaximumSize(btDimension);
		showGraphTB.add(graphButton);
		// ******************************************************************
		tabbedPane.addTab("Po³¹czenie", connectPane);
		tabbedPane.addTab("Wyniki", showDataSplitPane);
		tabbedPane.addTab("Wykresy", showGraphPane);
		add(tabbedPane, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnStandardLogs) {
			logsToJlist();
		}
		if (source == btnArchiveLogs) {
			archiveLogsToJlist();
		}
		if (source == btnOtherLogs) {
			ltlList.getOtherLogs();
		}
		if (source == btnClearAllFromLtlList) {
			ltlList.removeFromLogsToLoadList();
		}
		if (source == btnClearSelectedFromLtlList) {
			ltlList.removeSelectedFromLogsToLoadList(ltlList.getSelectedIndices());
		}
		if (source == tfInstruction) {
			filterInJtable(tfInstruction.getText());
		}
		if (source == btnGenerateDictToFlatFile) {
			OracleQueries oq = new OracleQueries();
			String utlFile = oq.generateDictionaryFile(setConnectionData());
			if (utlFile != null) {
				setUtlFileDirLabel(utlFile);
				int res = JOptionPane.showConfirmDialog(null,
						"<html>S³ownik wygenerowany do katalogu " + utlFile
								+ "<br/>Czy spakowaæ wszystko? (s³ownik i wybrane logi)</html>",
						"S³ownik", JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.YES_OPTION) {
					zipFiles(utlFile);
				}
			}
		}
		if (source == btnGenerateDictToRedoLogs) {
			OracleQueries oq = new OracleQueries();
			oq.generateDictionaryToLogFile(setConnectionData());
			String beginLog = oq.oracleGetScalar("SELECT NAME FROM V$ARCHIVED_LOG WHERE DICTIONARY_BEGIN='YES'");
			String endLog = oq.oracleGetScalar("SELECT NAME FROM V$ARCHIVED_LOG WHERE DICTIONARY_END='YES'");
			int res = JOptionPane
					.showConfirmDialog(
							null, "<html>S³ownik wygenerowany do logów redo <br/>" + "Od - " + beginLog + "<br/>"
									+ "Do - " + endLog + "<br/>Czy spakowaæ? </html>",
							"S³ownik", JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				zipFiles(null);
			}
		}
	}

	private void zipFiles(String utlFile) {
		JFileChooser jfc = new JFileChooser();
		int r = jfc.showSaveDialog(null);
		if (r == JFileChooser.APPROVE_OPTION) {
			String zipFile = jfc.getSelectedFile().getAbsolutePath();
			List<File> filesToZip = new ArrayList<File>();
			if (utlFile != null) {
				filesToZip.add(new File(utlFile));
			}
			for (String s : ltlList.getLogsToLoadList()) {
				filesToZip.add(new File(s));
			}
			Utils u = new Utils();
			u.zipFiles(filesToZip, zipFile);
			JOptionPane.showMessageDialog(null, "Spakowane do pliku '" + zipFile + "'");
		}
	}

	/**
	 * Wczytuje listê logów select member from v$logfile
	 */
	private void logsToJlist() {
		ConnectionData cd = this.setConnectionData();
		OracleQueries oq = new OracleQueries();
		oq.setConnection(cd);
		List<String> logsList = oq.oracleGetList("select member from v$logfile");
		ltlList.setLogListModel(logsList);
	}

	private void archiveLogsToJlist() {
		ConnectionData cd = this.setConnectionData();
		OracleQueries oq = new OracleQueries();
		oq.setConnection(cd);
		List<String> archiveLogsList = oq.oracleGetList("select name from v$archived_log");
		ltlList.setLogListModel(archiveLogsList);
	}

	private void closeLaryngoLog() {
		int res = JOptionPane.showConfirmDialog(null, "Zakoñczyæ program?", "Koniec", JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION) {
			OracleQueries oq = new OracleQueries();
			connectButton.unloadLogminer();
			oq.conClose();
			System.exit(0);
		}
	}
}
