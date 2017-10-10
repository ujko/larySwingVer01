package pr.window.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import pr.connectionData.ConnectionData;
import pr.window.LaryngologFrame;
import pr.window.lists.UsersList;
import pr.xml.XmlUtils;

public class SaveToXmlButton extends JButton implements ActionListener {
	private static final long serialVersionUID = -3274860253989636475L;
	private LaryngologFrame laryngologFrame;

	public SaveToXmlButton(LaryngologFrame laryngologFrame) {
		super("Zapisz");
		this.laryngologFrame = laryngologFrame;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ConnectionData cd = laryngologFrame.setConnectionData();
		String xName = cd.getConnectionName();
		String xUser = cd.getUserName();
		if (xName != null && xUser != null) {
			if (cd.getPassword().length > 0) {
				int res = JOptionPane.showConfirmDialog(null, "Czy zapisaæ równie¿ has³o?", "Has³o",
						JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.NO_OPTION) {
					cd.setPassword(null);
				}
			}
			if (XmlUtils.findGroupInXml(xName)) {
				int question = JOptionPane.showConfirmDialog(null,
						"Podana nazwa '" + xName + "' ju¿ istnieje. Nadpisaæ?", "Nazwa", JOptionPane.YES_NO_OPTION);
				if (question == JOptionPane.YES_OPTION) {
					XmlUtils.deleteFromXml(xName);
					XmlUtils.saveConnectionDataToXml(cd);
				}
			} else {
				XmlUtils.saveConnectionDataToXml(cd);
				UsersList.listModel.addElement(xName);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Nie poda³eœ nazwy lub u¿ytkownika", "B³¹d", JOptionPane.ERROR_MESSAGE);
		}
	}
}
