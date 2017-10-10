package pr.window.lists;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdom2.Element;

import pr.encryption.CryptographyUtil;
import pr.window.LaryngologFrame;
import pr.xml.XmlUtils;

@SuppressWarnings("serial")
public class UsersList extends JList<String> implements ListSelectionListener {
	public static DefaultListModel<String> listModel;
	private JPopupMenu jpmenu;
	private LaryngologFrame laryngologMF;

	public UsersList(LaryngologFrame laryngologMF) {
		super(nodesForJList());
		this.laryngologMF = laryngologMF;
		jpmenu = new JPopupMenu();
		JMenuItem delNode = new JMenuItem("Skasuj");
		delNode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s = getSelectedValue();
				int i = getSelectedIndex();
				XmlUtils.deleteFromXml(s);
				if (i != -1) {
					listModel.remove(i);
				}
			}
		});
		jpmenu.add(delNode);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					jpmenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		addListSelectionListener(this);
	}

	private static DefaultListModel<String> nodesForJList() {
		listModel = new DefaultListModel<String>();
		List<Element> list = XmlUtils.getNodesFromXml();
		for (Element x : list) {
			listModel.addElement(x.getName());
		}
		return listModel;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		String s = getSelectedValue();
		String[] table = new String[5];
		table = XmlUtils.xmlNodeToTable(s);
		laryngologMF.setNameTextField(s);
		laryngologMF.setUserTextField(table[0]);
		// has³o
		CryptographyUtil cr = new CryptographyUtil();
		String encryptedPass = table[1];
		String decryptedPass = "";
		try {
			decryptedPass = cr.decryptData(encryptedPass);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		laryngologMF.setPassTextField(decryptedPass);
		laryngologMF.setServerNameTextField(table[2]);
		laryngologMF.setPortNameTextField(table[3]);
		laryngologMF.setServiceNameTextField(table[4]);
	}
}
