package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.computer.knauss.reqtDiscussion.Util;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.ui.DiscussionVisualizationPanel;

public class ExportVisualization extends AbstractCommand {

	private static final long serialVersionUID = 1L;
	private DiscussionVisualizationPanel discussionVisualization;

	public ExportVisualization(
			DiscussionVisualizationPanel discussionVisualization) {

		super("Export visualization");
		this.discussionVisualization = discussionVisualization;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Discussion[] discussions = getDiscussionTableModel()
				.getSelectedDiscussions();
		export(discussions);
	}

	public void export(Discussion[] discussions, String postfix) {
		Util.sortByID(discussions);

		// Create the picture
		this.discussionVisualization.setDiscussions(discussions);
		BufferedImage bi = new BufferedImage(
				this.discussionVisualization.getVisualizationDimension().width,
				this.discussionVisualization.getVisualizationDimension().height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		this.discussionVisualization.paint(g2);

		// Save it
		String filename = "";
		if (discussions != null && discussions.length > 0)
			for (Discussion d : discussions)
				filename += d.getID() + "-";
		else
			filename = "empty-";
		if (postfix.length() > 0)
			filename = postfix + filename;
		else
			filename = filename.substring(0, filename.length() - 1);
		File f = new File(filename + ".png");
		try {
			ImageIO.write(bi, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void export(Discussion[] discussions) {
		export(discussions, "");
	}

}
