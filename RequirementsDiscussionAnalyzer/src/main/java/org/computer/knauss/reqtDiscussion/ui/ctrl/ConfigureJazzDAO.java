package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.DAORegistry;
import org.computer.knauss.reqtDiscussion.io.IDAOManager;
import org.computer.knauss.reqtDiscussion.io.jazz.rest.JazzJDOMDAO;

public class ConfigureJazzDAO extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	public ConfigureJazzDAO() {
		super("Configure Jazz Datasource");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		IDAOManager mgr = DAORegistry.getInstance().getDAOManager("jazz.net");
		
		try {
			JazzJDOMDAO jazzDAO = (JazzJDOMDAO) mgr.getDiscussionDAO();
			
			String input = JOptionPane.showInputDialog("Number of Workitems per Query:", "10");
			jazzDAO.setLimit(Integer.parseInt(input));
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

}
