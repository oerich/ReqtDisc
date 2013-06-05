package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;

public class ExportDiscussionEvents extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;
	private BufferedWriter out;

	public ExportDiscussionEvents() {
		super("Export discussion events");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void processDiscussionHook(Discussion[] discussions) {
		// TODO write arff header
		// TODO remove com.ibm stuff from type and status
		// TODO remove / escape href quotes
		if (this.out == null)
			return;
		try {
			for (Discussion d : discussions)
				for (DiscussionEvent de : d.getDiscussionEvents()) {
					this.out.write("\"" + de.getContent() + "\",");
					this.out.write("\"" + de.getCreator() + "\",");
					this.out.write("\"" + de.getCreationDate().getMonth()
							+ "\",");
					this.out.write("\"" + de.getCreationDate().getYear()
							+ "\",");
					this.out.write("\"" + d.getStatus() + "\",");
					this.out.write("\"" + d.getType() + "\",");
					for (AbstractDiscussionMetric m : AbstractDiscussionMetric.STANDARD_METRICS) {
						Double metricResult = m
								.considerDiscussions(new Discussion[] { DiscussionFactory
										.getInstance().getDiscussion(
												de.getDiscussionID()) });
						// we have only integer values in the standard metrics.
						// Also,
						// the classifier would replace the dot by a space.
						this.out.write("\"" + metricResult.intValue() + "\",");
					}
					this.out.write("\""
							+ de.getReferenceClassification().substring(0, 5)
									.toLowerCase() + "\"");
					this.out.newLine();
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void preProcessingHook() {
		JFileChooser fc = new JFileChooser();
		if (JFileChooser.CANCEL_OPTION == fc.showSaveDialog(null))
			return;

		try {
			out = new BufferedWriter(new FileWriter(fc.getSelectedFile()));

			this.out.write("\"Content\",");
			this.out.write("\"Creator\",");
			this.out.write("\"CreationDate: Month\",");
			this.out.write("\"CreationDate: Year\",");
			this.out.write("\"Status\",");
			this.out.write("\"Type\",");
			for (AbstractDiscussionMetric m : AbstractDiscussionMetric.STANDARD_METRICS) {
				this.out.write("\"" + m.getClass().getSimpleName() + "\",");
			}
			this.out.write("\"Class\"");
			this.out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void postProcessingHook() {
		try {
			this.out.flush();
			this.out.close();
			this.out = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
