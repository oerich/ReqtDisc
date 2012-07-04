package org.computer.knauss.reqtDiscussion.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.IFilteredWorkitemCommentList;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;
import org.computer.knauss.reqtDiscussion.ui.visualization.ICommentOverTimePartition;
import org.computer.knauss.reqtDiscussion.ui.visualization.IVisualizationStyle;
import org.computer.knauss.reqtDiscussion.ui.visualization.PixelPartition;
import org.computer.knauss.reqtDiscussion.ui.visualization.TimeIntervalPartition;
import org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns.AlignedRectangularCommentStyle;
import org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns.GreyBackgroundBox;
import org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns.Grid;
import org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns.LineOfUnderstanding;
import org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns.OverlappingCommentStyle;
import org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns.PatternClassVisualization;

public class DiscussionVisualizationPanel extends JPanel implements
		ListSelectionListener, ActionListener {

	private static final int TXT_LINE_HEIGHT = 16;
	private static final int TXT_HORIZONTAL_MARGIN = 2;
	private static final long serialVersionUID = 1L;
	private static final Dimension VISUALIZATION_DIMENSION = new Dimension(800,
			600);
	private DiscussionTableModel wtm;
	private Discussion[] selectedWorkitems;
	private Date firstDate;
	private Date lastDate;
	private ICommentOverTimePartition commentPartition = new TimeIntervalPartition();
	private ICommentOverTimePartition commentPixelPartition = new PixelPartition();
	private IVisualizationStyle visualizationStyleBoxes;
	private IVisualizationStyle visualizationGrid;
	private IVisualizationStyle visualizationLineOfUnderstanding;
	private IVisualizationStyle visualizationBackground;
	private IVisualizationStyle visualizationStyleText;
	private IVisualizationStyle visualizationPattern;
	private VisualizationConfigurationPanel configureVisualizationPanel;

	public DiscussionVisualizationPanel(
			VisualizationConfigurationPanel configureVisualizationPanel) {
		setPreferredSize(VISUALIZATION_DIMENSION);
		setMinimumSize(VISUALIZATION_DIMENSION);
		setMaximumSize(VISUALIZATION_DIMENSION);
		setSize(VISUALIZATION_DIMENSION);

		setBackground(Color.WHITE);

		this.configureVisualizationPanel = configureVisualizationPanel;
		this.configureVisualizationPanel
				.setCommentPartition(this.commentPartition);
		this.configureVisualizationPanel.addActionListener(this);

		this.visualizationStyleBoxes = new AlignedRectangularCommentStyle();
		this.visualizationGrid = new Grid();
		this.visualizationBackground = new GreyBackgroundBox();
		this.visualizationLineOfUnderstanding = new LineOfUnderstanding();
		this.visualizationStyleText = new OverlappingCommentStyle();
		this.visualizationPattern = new PatternClassVisualization();
		this.visualizationBackground.setDiscussionOverTimePartition(
				this.commentPartition, 100, 300);
		this.visualizationGrid.setDiscussionOverTimePartition(
				this.commentPartition, 100, 300);
		this.visualizationStyleBoxes.setDiscussionOverTimePartition(
				this.commentPartition, 100, 300);
		this.visualizationLineOfUnderstanding.setDiscussionOverTimePartition(
				this.commentPartition, 100, 300);
		this.visualizationPattern.setDiscussionOverTimePartition(
				this.commentPartition, 100, 300);
		this.visualizationStyleText.setDiscussionOverTimePartition(
				this.commentPixelPartition, 100, 300);
		this.commentPixelPartition.setPartitionCount(600);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		g2.clearRect(0, 0, 800, 600);
		g2.setBackground(Color.WHITE);
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, 800, 600);

		if (this.selectedWorkitems != null) {
			g2.setStroke(new BasicStroke(1f));
			g2.setColor(Color.black);

			g2.drawString("Workitem", TXT_HORIZONTAL_MARGIN,
					300 - (TXT_LINE_HEIGHT / 2));

			IFilteredWorkitemCommentList allCommentList = this.configureVisualizationPanel
					.getFilteredCommentList();
			allCommentList.clear();

			int y = 300 + TXT_LINE_HEIGHT / 2;
			this.firstDate = new Date(Long.MAX_VALUE);
			this.lastDate = new Date(0);
			for (Discussion wi : this.selectedWorkitems) {
				g2.drawString(String.valueOf(wi.getID()),
						TXT_HORIZONTAL_MARGIN, y);
				y = y + TXT_LINE_HEIGHT;
				if (this.firstDate.after(wi.getDateCreated()))
					this.firstDate = wi.getDateCreated();
				for (DiscussionEvent wc : wi.getAllComments()) {
					allCommentList.add(wc);
					if (this.lastDate.before(wc.getCreationDate()))
						this.lastDate = wc.getCreationDate();
				}
			}

			int size = allCommentList.getClassificationItemList().size();
			DiscussionEvent[] allComments = new DiscussionEvent[size];
			for (int i = 0; i < size; i++)
				allComments[i] = allCommentList.getWorkitemComment(i);

			this.commentPartition
					.setTimeInterval(this.firstDate, this.lastDate);
			this.commentPixelPartition.setTimeInterval(this.firstDate,
					this.lastDate);

			if (this.configureVisualizationPanel.isBackgroundStyle())
				applyVisualizationStyle(g2, allComments,
						this.visualizationBackground);
			if (this.configureVisualizationPanel.isGridStyle()) {
				((Grid) this.visualizationGrid)
						.setUsePartition(this.configureVisualizationPanel
								.isUsePartitionForGrid());
				applyVisualizationStyle(g2, allComments, this.visualizationGrid);
			}
			// draw a horizontal time line
			g2.setStroke(new BasicStroke(3f));
			g2.setColor(Color.BLACK);
			g2.drawLine(100, 300, 700, 300);
			if (this.configureVisualizationPanel.isLineOfUnderstandingStyle()) {
				((LineOfUnderstanding) this.visualizationLineOfUnderstanding)
						.setInterpolarize(this.configureVisualizationPanel
								.isLSUInterpolarization());
				applyVisualizationStyle(g2, allComments,
						this.visualizationLineOfUnderstanding);
			}
			if (this.configureVisualizationPanel.isBoxesStyle())
				applyVisualizationStyle(g2, allComments,
						this.visualizationStyleBoxes);
			if (this.configureVisualizationPanel.isPatternStyle()) {
				applyVisualizationStyle(g2, allComments,
						this.visualizationPattern);
			}
			if (this.configureVisualizationPanel.isTextStyle())
				applyVisualizationStyle(g2, allComments,
						this.visualizationStyleText);

		}
	}

	private void applyVisualizationStyle(Graphics2D g2,
			DiscussionEvent[] allComments,
			IVisualizationStyle visualizationStyle) {
		visualizationStyle.reset();
		g2.setStroke(visualizationStyle.getStroke());
		// paint the background
		for (Shape s : visualizationStyle.layout(allComments)) {
			Color fill = visualizationStyle.getFillColor(null, -1);
			Color draw = visualizationStyle.getDrawColor(null, -1);
			if (fill != null) {
				g2.setColor(fill);
				g2.fill(s);
			}
			if (draw != null) {
				g2.setColor(draw);
				g2.draw(s);
			}
		}

		// paint the comments
		for (DiscussionEvent wic : allComments) {
			Shape[] shapes = visualizationStyle.getShape(wic);
			for (int i = 0; i < shapes.length; i++) {
				Shape s = shapes[i];
				Color fill = visualizationStyle.getFillColor(wic, i);
				Color draw = visualizationStyle.getDrawColor(wic, i);
				if (fill != null) {
					g2.setColor(fill);
					g2.fill(s);
				}
				if (draw != null) {
					g2.setColor(draw);
					g2.draw(s);
				}
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		this.selectedWorkitems = this.wtm.getSelectedWorkitems();
		repaint();
	}

	public void setTableModel(DiscussionTableModel wtm) {
		this.wtm = wtm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	public void exportVisualization() {
		BufferedImage bi = new BufferedImage(VISUALIZATION_DIMENSION.width,
				VISUALIZATION_DIMENSION.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		paint(g2);
		String filename = "";
		if (this.selectedWorkitems != null && this.selectedWorkitems.length > 0)
			for (Discussion wi : this.selectedWorkitems)
				filename += wi.getID() + "-";
		else
			filename = "empty-";
		File f = new File(filename.substring(0, filename.length() - 1) + ".png");
		try {
			ImageIO.write(bi, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
