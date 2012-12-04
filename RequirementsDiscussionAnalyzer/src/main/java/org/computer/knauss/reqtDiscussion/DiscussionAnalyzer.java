package org.computer.knauss.reqtDiscussion;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.computer.knauss.reqtDiscussion.io.DAORegistry;
import org.computer.knauss.reqtDiscussion.io.jazz.JazzDAOManager;
import org.computer.knauss.reqtDiscussion.io.sql.SQLDAOManager;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.VisualizationConfiguration;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.AbstractBucketBalancingStrategy;
import org.computer.knauss.reqtDiscussion.ui.DiscussionAnalyzerFrame;
import org.computer.knauss.reqtDiscussion.ui.ctrl.AbstractCommand;
import org.computer.knauss.reqtDiscussion.ui.ctrl.ChooseDAOManager;
import org.computer.knauss.reqtDiscussion.ui.ctrl.ClassifyDataCmd;
import org.computer.knauss.reqtDiscussion.ui.ctrl.ClearClassifierCmd;
import org.computer.knauss.reqtDiscussion.ui.ctrl.ConfigureJazzDAO;
import org.computer.knauss.reqtDiscussion.ui.ctrl.EditDatasourceCommand;
import org.computer.knauss.reqtDiscussion.ui.ctrl.InsertOrUpdateDiscussionEventClassification;
import org.computer.knauss.reqtDiscussion.ui.ctrl.KFoldCrossDiscussionEvaluationCmd;
import org.computer.knauss.reqtDiscussion.ui.ctrl.KFoldCrossDiscussionEventEvaluationCmd;
import org.computer.knauss.reqtDiscussion.ui.ctrl.LoadDiscussionByID;
import org.computer.knauss.reqtDiscussion.ui.ctrl.LoadDiscussions;
import org.computer.knauss.reqtDiscussion.ui.ctrl.LoadMoreDiscussions;
import org.computer.knauss.reqtDiscussion.ui.ctrl.LoadTrainingDataCmd;
import org.computer.knauss.reqtDiscussion.ui.ctrl.PrintTrajectoryFeatures;
import org.computer.knauss.reqtDiscussion.ui.ctrl.SetReferenceClassifierName;
import org.computer.knauss.reqtDiscussion.ui.ctrl.ShowStatistics;
import org.computer.knauss.reqtDiscussion.ui.ctrl.StoreDiscussionEventClassifications;
import org.computer.knauss.reqtDiscussion.ui.ctrl.StoreDiscussions;
import org.computer.knauss.reqtDiscussion.ui.ctrl.StoreTrainingDataCmd;
import org.computer.knauss.reqtDiscussion.ui.ctrl.TrainClassifierCmd;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

public class DiscussionAnalyzer {

	private static DAORegistry daoRegistry;
	private static DiscussionTableModel tableModel;
	private static VisualizationConfiguration configuration;

	public static void main(String[] args) {

		// create the model:
		tableModel = new DiscussionTableModel();
		configuration = new VisualizationConfiguration();

		// create the view:
		DiscussionAnalyzerFrame daFrame = new DiscussionAnalyzerFrame(
				configuration);
		daFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		daFrame.pack();

		// add model to view:
		daFrame.setTableModel(tableModel);
		try {
			daoRegistry = DAORegistry.getInstance();
			// add the data sources
			daoRegistry.register("PSQL (default)", new SQLDAOManager(
					new String[] { "/local-postgres-properties.txt",
							"/psql-default-schema-queries.txt" }));
			daoRegistry.register("PSQL (ballroom)", new SQLDAOManager(
					new String[] { "/ballroom-postgres-properties.txt",
							"/psql-ballroom-schema-queries.txt" }));
			daoRegistry.register("jazz.net", new JazzDAOManager());
			// daoRegistry.register("Jira (xml)", new XmlDAOManager(
			// "./bizzdesign-jira.txt"));
			daoRegistry.register("Jira (sql)", new SQLDAOManager(
					new String[] { "/bizzdesign-psql.txt" }));
			// "/jira-xml-properties.txt"));
			daoRegistry.register("MySQL (trento)", new SQLDAOManager(
					new String[] { "/mysql-default-schema-queries.txt",
							"/trento-mysql-properties.txt" }));

			// add the commands
			daFrame.addSeperator(DiscussionAnalyzerFrame.DATA_MENU,
					"configure data access");
			daFrame.addAction(DiscussionAnalyzerFrame.DATA_MENU,
					configureCommand(new ChooseDAOManager()));
			daFrame.addAction(DiscussionAnalyzerFrame.DATA_MENU,
					configureCommand(new EditDatasourceCommand()));
			daFrame.addAction(DiscussionAnalyzerFrame.DATA_MENU,
					configureCommand(new ConfigureJazzDAO()));
			daFrame.addSeperator(DiscussionAnalyzerFrame.DATA_MENU,
					"load discussions");
			daFrame.addAction(DiscussionAnalyzerFrame.DATA_MENU,
					configureCommand(new LoadDiscussions()));
			daFrame.addAction(DiscussionAnalyzerFrame.DATA_MENU,
					configureCommand(new LoadMoreDiscussions()));
			daFrame.addAction(DiscussionAnalyzerFrame.DATA_MENU,
					configureCommand(new LoadDiscussionByID()));
			daFrame.addSeperator(DiscussionAnalyzerFrame.DATA_MENU,
					"store discussions");
			daFrame.addAction(DiscussionAnalyzerFrame.DATA_MENU,
					configureCommand(new StoreDiscussions()));
			daFrame.addSeperator(DiscussionAnalyzerFrame.DATA_MENU,
					"store classifications");
			daFrame.addAction(DiscussionAnalyzerFrame.DATA_MENU,
					configureCommand(new StoreDiscussionEventClassifications()));

			daFrame.addSeperator(DiscussionAnalyzerFrame.EDIT_MENU,
					"configure classification");
			daFrame.addAction(DiscussionAnalyzerFrame.EDIT_MENU,
					configureCommand(new SetReferenceClassifierName()));

			AbstractCommand insertOrUpdateDiscussionEventClassification = configureCommand(new InsertOrUpdateDiscussionEventClassification());
			daFrame.getEditClassificationFrame().setInsertOrUpdateCommand(
					insertOrUpdateDiscussionEventClassification);

			daFrame.addAction(DiscussionAnalyzerFrame.STATISTICS_MENU,
					configureCommand(new ShowStatistics()));
			daFrame.addAction(DiscussionAnalyzerFrame.STATISTICS_MENU,
					configureCommand(new PrintTrajectoryFeatures()));

			daFrame.addAction(DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(new TrainClassifierCmd()));
			StoreTrainingDataCmd storeTrainingDataCmd = new StoreTrainingDataCmd();
			storeTrainingDataCmd.setEnabled(false);
			daFrame.addAction(DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(storeTrainingDataCmd));
			LoadTrainingDataCmd loadTrainingDataCmd = new LoadTrainingDataCmd();
			loadTrainingDataCmd.setEnabled(false);
			daFrame.addAction(DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(loadTrainingDataCmd));
			daFrame.addSeperator(DiscussionAnalyzerFrame.ACTION_MENU,
					"use automatic classifier");
			ClassifyDataCmd classifyEvents = new ClassifyDataCmd();
			classifyEvents
					.setInsertOrUpdateCommand(insertOrUpdateDiscussionEventClassification);
			daFrame.addAction(DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(classifyEvents));
			// daFrame.addAction(
			// DiscussionAnalyzerFrame.ACTION_MENU,
			// configureCommand(new SimpleDiscussionClassifierEvaluationCmd()));
			daFrame.addAction(
					DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(new KFoldCrossDiscussionEvaluationCmd(
							AbstractBucketBalancingStrategy.RANDOM_BUCKET,
							KFoldCrossDiscussionEvaluationCmd.TAB_SEPARATED_STYLE,
							true)));
			daFrame.addAction(
					DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(new KFoldCrossDiscussionEvaluationCmd(
							AbstractBucketBalancingStrategy.GREEDY_DISCUSSION_EVENT,
							KFoldCrossDiscussionEvaluationCmd.TAB_SEPARATED_STYLE,
							true)));
			daFrame.addAction(
					DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(new KFoldCrossDiscussionEvaluationCmd(
							AbstractBucketBalancingStrategy.RANDOM_BUCKET,
							KFoldCrossDiscussionEvaluationCmd.LATEX_STYLE, true)));
			daFrame.addAction(
					DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(new KFoldCrossDiscussionEvaluationCmd(
							AbstractBucketBalancingStrategy.GREEDY_DISCUSSION_EVENT,
							KFoldCrossDiscussionEvaluationCmd.LATEX_STYLE, true)));
			daFrame.addAction(DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(new KFoldCrossDiscussionEvaluationCmd(
							AbstractBucketBalancingStrategy.RANDOM_BUCKET,
							KFoldCrossDiscussionEvaluationCmd.LATEX_STYLE,
							false)));
			daFrame.addAction(
					DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(new KFoldCrossDiscussionEventEvaluationCmd()));
			daFrame.addSeperator(DiscussionAnalyzerFrame.ACTION_MENU,
					"reset automatic classifier");
			daFrame.addAction(DiscussionAnalyzerFrame.ACTION_MENU,
					configureCommand(new ClearClassifierCmd()));

			IClassificationFilter.NAME_FILTER.setName("no rater name set");

		} catch (NullPointerException e) {
			System.err
					.println("Failed initialization. Perhaps the database could not be reached? Try to (re-)connect to: ssh -L 5432:localhost:5432 ballroom.segal.uvic.ca");
			e.printStackTrace();
		}
		// Clean up after work:
		daFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try {
					daoRegistry.closeAllConnections();
				} catch (Throwable t) {
					t.printStackTrace();
				}
				System.exit(0);
			}
		});
		daFrame.setVisible(true);
	}

	private static AbstractCommand configureCommand(AbstractCommand cmd) {
		cmd.setDAORegistry(daoRegistry);
		cmd.setDiscussionTableModel(tableModel);
		cmd.setVisualizationConfiguration(configuration);
		return cmd;
	}
}
