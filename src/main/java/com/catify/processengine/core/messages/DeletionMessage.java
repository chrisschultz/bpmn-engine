package com.catify.processengine.core.messages;

import java.util.Set;

import com.catify.processengine.core.nodes.EndEventNode;
import com.catify.processengine.core.services.ProcessInstanceCleansingService;

/**
 * The DeletionMessage is sent from the {@link EndEventNode} node to the
 * {@link ProcessInstanceCleansingService}. It signalizes the
 * ProcessInstanceCleansingService to delete a process instance.
 */
public class DeletionMessage extends Message {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The unique process id. */
	private String uniqueProcessId;
	
	/** The data object ids. */
	private Set<String> dataObjectIds;

	/**
	 * Instantiates a new deletion message.
	 *
	 * @param uniqueProcessId the unique process id
	 * @param processInstanceId the process instance id
	 */
	public DeletionMessage(String uniqueProcessId, String processInstanceId, Set<String> dataObjectIds) {
		this.uniqueProcessId = uniqueProcessId;
		this.processInstanceId = processInstanceId;
		this.dataObjectIds = dataObjectIds;
	}
	
	/**
	 * Gets the unique process id.
	 *
	 * @return the unique process id
	 */
	public String getUniqueProcessId() {
		return uniqueProcessId;
	}

	/**
	 * Sets the unique process id.
	 *
	 * @param uniqueProcessId the new unique process id
	 */
	public void setUniqueProcessId(String uniqueProcessId) {
		this.uniqueProcessId = uniqueProcessId;
	}

	/**
	 * Gets the data object ids.
	 *
	 * @return the data object ids
	 */
	public Set<String> getDataObjectIds() {
		return dataObjectIds;
	}

	/**
	 * Sets the data object ids.
	 *
	 * @param dataObjectIds the new data object ids
	 */
	public void setDataObjectIds(Set<String> dataObjectIds) {
		this.dataObjectIds = dataObjectIds;
	}
}
