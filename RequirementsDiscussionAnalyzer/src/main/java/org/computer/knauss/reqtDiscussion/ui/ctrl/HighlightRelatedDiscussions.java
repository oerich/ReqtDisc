package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.computer.knauss.reqtDiscussion.Util;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

public class HighlightRelatedDiscussions {

	private List<int[]> ids = new Vector<int[]>();
	private Set<Integer> visited = new HashSet<Integer>();
	private List<Discussion[]> complexDiscussions;

	public HighlightRelatedDiscussions() throws IOException {
		URL url = getClass().getResource("/related-work-items.txt");
		if (url != null) {
			BufferedReader r;
			r = new BufferedReader(new InputStreamReader(new FileInputStream(
					url.getFile())));

			String line = r.readLine();
			while (line != null) {
				if (line.startsWith("=")) {
					int[] tmp = Util.stringToID(line);
					this.ids.add(tmp);
				}
				line = r.readLine();
			}
			r.close();
		}
	}

	public int[] getRelatedDiscussionIDs(int id) {
		for (int[] a : this.ids) {
			for (int i : a) {
				if (i == id) {
					return a;
				}
			}
		}
		return new int[] { id };
	}

	public void highlightRelatedDiscussions(DiscussionTableModel tm) {
		Discussion[] selectedItems = tm.getSelectedDiscussions();

		// Do not interfere when user does multiple selections...
		if (selectedItems.length == 1) {
			int[] ids = getRelatedDiscussionIDs(selectedItems[0].getID());

			if (ids.length > 1) {
				Discussion[] wis = tm.getDiscussions();
				for (int j = 0; j < wis.length; j++) {
					Discussion wi = wis[j];
					for (int i : ids) {
						if (wi.getID() == i) {
							int convertedRow = tm.getTable()
									.convertRowIndexToView(j);
							tm.getTable()
									.getSelectionModel()
									.addSelectionInterval(convertedRow,
											convertedRow);
						}
					}
				}

			}
		}
	}

	/**
	 * Computes a list of all discussions, where complex discussions are
	 * aggregated. Each discussion is represented by a Discussion-Array, this
	 * array has length = 1 for non-complex discussions.
	 * 
	 * @return
	 */
	public List<Discussion[]> getAllAggregatedDiscussions(
			Discussion[] discussions) {
		if (this.visited.size() == discussions.length && complexDiscussions != null) 
			return this.complexDiscussions;
		
		this.visited.clear();
		
		Map<Integer, List<Discussion>> results = new HashMap<Integer, List<Discussion>>();
		
		for (Discussion d : discussions) {
			if (!this.visited.contains(d.getID())) {
				int[] related = getRelatedDiscussionIDs(d.getID());
				List<Discussion> relDisc = results.get(related[0]);
				if (relDisc == null){
					relDisc = new LinkedList<Discussion>();
					results.put(related[0], relDisc);
				}
				relDisc.add(d);
				this.visited.add(d.getID());
			}
		}
		

		// Compute the result
		complexDiscussions = new LinkedList<Discussion[]>();
		for (List<Discussion> l : results.values()){
			complexDiscussions.add(l.toArray(new Discussion[0]));
		}
		return complexDiscussions;
	}

}
