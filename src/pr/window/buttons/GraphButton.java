package pr.window.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import pr.oracle.OracleQueries;
import pr.window.LaryngologFrame;

@SuppressWarnings("serial")
public class GraphButton extends JButton implements ActionListener {
	private static String title;
	private static String category;
	private static String value;
	private LaryngologFrame laryngologFrame;

	public GraphButton(LaryngologFrame laryngologFrame) {
		super("Poka¿");
		this.laryngologFrame = laryngologFrame;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String dateFrom = laryngologFrame.getTfDateFromG();
		String dateTo = laryngologFrame.getTfDateToG();
		String user = laryngologFrame.getCbUserGraph();
		String operation = laryngologFrame.getCbOperationGraph();
		int chooseGraph = laryngologFrame.getPositionFromChooseGraph();
		switch (chooseGraph) {
		case 0:
			usingTables(dateFrom, dateTo, user, operation);
			break;
		case 1:
			loginUsers(dateFrom, dateTo, user);
			break;
		}
	}

	private void usingTables(String dateFrom, String dateTo, String user, String operation) {
		title = "U¿ycie tabel";
		category = "Tabela";
		value = "Iloœæ u¿yæ";
		StringBuilder query = new StringBuilder("select ");
		query.append("username, table_name, count(*) as changes from v$logmnr_contents where ");
		query.append("table_name not like '%$%' and ");
		query.append("timestamp between to_date('" + dateFrom + "', 'yyyy-mm-dd hh24:mi')" + " and to_date('" + dateTo
				+ "', 'yyyy-mm-dd hh24:mi')");
		if ("No sys".equals(user)) {
			query.append(" and username<>'SYS' and username<>'UNKNOWN'");
		} else if (!"All".equals(user)) {
			query.append(" and username='" + user + "' ");
		}
		if (!"All".equals(operation)) {
			query.append(" and operation='" + operation + "'");
		}
		query.append(" group by username, table_name order by changes desc");
		showInChart(query);
	}

	private void showInChart(StringBuilder query) {
		OracleQueries oq = new OracleQueries();
		ResultSet rs = oq.oracleToResultSet(query.toString());
		laryngologFrame.showGraphInChartPanel(createDataset(rs));
	}

	private void loginUsers(String dateFrom, String dateTo, String user) {
		title = "Logowania u¿ytkowników";
		category = "U¿ytkownik";
		value = "Iloœæ logowañ";
		StringBuilder query = new StringBuilder("select ");
		query.append(
				"to_char(TIMESTAMP, 'YYYY-MM-DD') tstamp, username, count(*) as logins from v$logmnr_contents where ");
		query.append("operation = 'START' and ");
		query.append("timestamp between to_date('" + dateFrom + "', 'yyyy-mm-dd hh24:mi')" + " and to_date('" + dateTo
				+ "', 'yyyy-mm-dd hh24:mi')");
		if ("No sys".equals(user)) {
			query.append(" and username<>'SYS' and username<>'UNKNOWN'");
		} else if (!"All".equals(user)) {
			query.append(" and username='" + user + "' ");
		}
		query.append(" group by username, to_char(TIMESTAMP, 'YYYY-MM-DD')");
		query.append(" order by username, tstamp");
		showInChart(query);
	}

	private static JFreeChart createDataset(ResultSet rs) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		try {
			while (rs.next()) {
				String name = rs.getString(2) == null ? "NULL" : rs.getString(2);
				String owner = rs.getString(1) == null ? "NULL" : rs.getString(1);
				dataset.setValue(rs.getInt(3), name, owner);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		JFreeChart chart = ChartFactory.createBarChart(title, category, value, dataset, PlotOrientation.VERTICAL, false,
				true, false);
		return chart;
	}

}
