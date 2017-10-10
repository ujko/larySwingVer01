package pr.window.lists;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import pr.oracle.OracleQueries;
import pr.window.LaryngologFrame;

/**
 * Rozszerzenie JTextArea, do wyœwietlania zawartoœci zaznaczonej komórki w
 * tabeli
 * 
 */
@SuppressWarnings("serial")
public class TaResult extends JTextArea implements ActionListener {
	JPopupMenu pMenu;
	LaryngologFrame laryngologFrame;
	JMenuItem runDml, filter;

	public TaResult(LaryngologFrame laryngologFrame) {
		super();
		this.laryngologFrame = laryngologFrame;
		pMenu = new JPopupMenu();
		filter = new JMenuItem("Filtruj wg zaznaczonego");
		filter.addActionListener(this);
		pMenu.add(filter);
		runDml = new JMenuItem("Wykonaj");
		runDml.addActionListener(this);
		pMenu.add(runDml);
		this.add(pMenu);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					pMenu.show(e.getComponent(), e.getX(), e.getY());
					pMenu.setVisible(true);
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == filter) {
			String toFilter = this.getSelectedText();
			laryngologFrame.filterInJtable(toFilter);
		}
		if (source == runDml) {
			dmlRun();
		}
	}

	private void dmlRun() {
		String dml = this.getText();
		if (dml.endsWith(";")) {
			dml = dml.substring(0, dml.length() - 1);
		}
		OracleQueries oq = new OracleQueries();
		int res = oq.ddlOracle(dml);
		if (res == -1) {
			JOptionPane.showMessageDialog(null, "B³¹d. Nic nie zmieniono", "B³¹d", JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Zmieniono: " + res, "OK", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
