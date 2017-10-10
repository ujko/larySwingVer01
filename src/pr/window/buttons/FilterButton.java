package pr.window.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import pr.window.LaryngologFrame;

@SuppressWarnings("serial")
public class FilterButton extends JButton implements ActionListener {
	private LaryngologFrame laryngologMF;

	public FilterButton(LaryngologFrame laryngologMF) {
		super("Fitruj");
		this.laryngologMF = laryngologMF;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		filterData();
	}

	private void filterData() {
		String operation = laryngologMF.getCbOperation();
		String table = laryngologMF.getCbTable();
		String user = laryngologMF.getCbUser();
		String sqlRedoInstr = laryngologMF.getTfInstruction(); // filtr dla
																// sql_redo
		String dateFrom = laryngologMF.getTfDateFrom(); // filtr data od
		String dateTo = laryngologMF.getTfDateTo();// filtr data do
		StringBuilder query = new StringBuilder("select");
		// query.append(" timestamp, operation, table_name, username,
		// os_username, machine_name, sql_redo, sql_undo");
		query.append(" timestamp, operation, table_name, username, sql_redo, sql_undo");
		query.append(" from v$logmnr_contents");
		query.append(" where timestamp BETWEEN to_date('" + dateFrom + "', 'yyyy-mm-dd hh24:mi')" + " and to_date('"
				+ dateTo + "', 'yyyy-mm-dd hh24:mi')");
		if (!"All".equals(operation)) {
			if ("null".equals(operation)) {
				query.append(" and operation is null");
			} else {
				query.append(" and operation='" + operation + "' ");
			}
		}
		if (!"All".equals(table)) {
			if ("null".equals(table)) {
				query.append(" and table_name is null");
			} else {
				query.append(" and table_name='" + table + "'");
			}
		}
		if (!"All".equals(user)) {
			if ("No sys".equals(user)) {
				query.append(" and username<>'SYS' and username<>'UNKNOWN'");
			} else {
				query.append(" and username='" + user + "'");
			}
		}
		if (!"".equals(sqlRedoInstr)) {
			query.append(" and upper(sql_redo) like upper ('%" + sqlRedoInstr + "%') ");
		}
		laryngologMF.showJTable(query.toString());
		// laryngologMF.setComboTables();
	}
}
