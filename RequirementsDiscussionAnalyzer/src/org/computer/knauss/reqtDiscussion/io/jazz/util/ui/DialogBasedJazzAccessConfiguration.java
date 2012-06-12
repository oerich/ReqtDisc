package org.computer.knauss.reqtDiscussion.io.jazz.util.ui;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.computer.knauss.reqtDiscussion.io.jazz.rest.IJazzAccessConfiguration;


public class DialogBasedJazzAccessConfiguration implements
		IJazzAccessConfiguration {

	private String hostname;
	private URI jazzAuthUrl;
	private String userName;
	private char[] password;
	private String error;
	private JTextField userNameField = new JTextField();
	private JPasswordField passwordField = new JPasswordField();
	private JTextField hostNameField = new JTextField("jazz.net");
	private JTextField authURLField = new JTextField(
		//	"https://jazz.net/jazz/oauth-authorize");
//	"https://jazz.net/hub/ccm/oauth-authorize");
	"https://jazz.net/auth/login");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uvic.segal.jazzClassifier.io.jazz.IJazzAccessConfiguration#getHostname
	 * ()
	 */
	@Override
	public String getHostname() {
		if (this.hostname == null)
			showUserDialog();
		return this.hostname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uvic.segal.jazzClassifier.io.jazz.IJazzAccessConfiguration#getJazzAuthURL
	 * ()
	 */
	@Override
	public URI getJazzAuthURL() {
		if (this.jazzAuthUrl == null)
			showUserDialog();
		return this.jazzAuthUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uvic.segal.jazzClassifier.io.jazz.IJazzAccessConfiguration#getUsername
	 * ()
	 */
	@Override
	public String getUsername() {
		if (this.userName == null) {
			showUserDialog();
		}
		return this.userName;
	}

	private void showUserDialog() {
		JOptionPane.showConfirmDialog(null, new Object[] { this.error, "User:",
				userNameField, "Pwd:", passwordField, "Hostname:",
				hostNameField, "Auth URL", authURLField },
				"Specify Connection Details", JOptionPane.OK_CANCEL_OPTION);
		this.userName = userNameField.getText();
		this.password = passwordField.getPassword();
		this.hostname = hostNameField.getText();
		try {
			this.jazzAuthUrl = new URI(authURLField.getText());
		} catch (URISyntaxException e) {
			this.error = e.getMessage();
			e.printStackTrace();
			showUserDialog();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uvic.segal.jazzClassifier.io.jazz.IJazzAccessConfiguration#getPassword
	 * ()
	 */
	@Override
	public String getPassword() {
		if (this.password == null)
			showUserDialog();
		return new String(this.password);
	}

}
