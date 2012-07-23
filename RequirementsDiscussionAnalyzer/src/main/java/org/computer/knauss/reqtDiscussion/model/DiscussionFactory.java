package org.computer.knauss.reqtDiscussion.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Flyweightish factory that ensures that only one Discussion per ID exists.
 * DAOs should feel free to override the values of this discussion, to make it
 * up to date!
 * 
 * @author eknauss
 * 
 */
public class DiscussionFactory {

	public static DiscussionFactory INSTANCE;

	private DiscussionFactory() {

	}

	public static synchronized DiscussionFactory getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DiscussionFactory();
		return INSTANCE;
	}

	private Map<Integer, Discussion> cache = new HashMap<Integer, Discussion>();

	public Discussion getDiscussion(int id) {
		Discussion ret = this.cache.get(id);
		if (ret == null) {
			ret = new Discussion();
			ret.setId(id);
			this.cache.put(id, ret);
		}
		return ret;
	}

	public void clear() {
		this.cache.clear();
	}

	public boolean exists(int i) {
		return this.cache.containsKey(i);
	}

}
