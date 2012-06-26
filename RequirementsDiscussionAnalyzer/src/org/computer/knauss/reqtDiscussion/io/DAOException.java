package org.computer.knauss.reqtDiscussion.io;

public class DAOException extends Exception {

	private static final long serialVersionUID = 1L;

	public DAOException(Exception cause) {
		super(cause);
	}

	public DAOException(String string) {
		super(string);
	}

	public DAOException(String string, Throwable cause) {
		super(string, cause);
	}
}
