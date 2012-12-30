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
		super("Edit configuration for jazz.net");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		IDAOManager mgr = DAORegistry.getInstance().getDAOManager("jazz.net");

		try {
			JazzJDOMDAO jazzDAO = (JazzJDOMDAO) mgr.getDiscussionDAO();

			String input = JOptionPane.showInputDialog(
					"Number of Workitems per Query:", jazzDAO.getLimit());
			jazzDAO.setLimit(Integer.parseInt(input));

			String[] projectAreas = jazzDAO.getProjectAreas();
			input = (String)JOptionPane.showInputDialog(null, "Project Area",
					"Configure Jazz DAO", JOptionPane.QUESTION_MESSAGE, null,
					projectAreas, projectAreas[0]);
			jazzDAO.setProjectArea(input);
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
