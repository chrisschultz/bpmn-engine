/**
 * 
 */
package com.catify.processengine.core.nodes;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;

import com.catify.processengine.core.data.dataobjects.DataObjectService;
import com.catify.processengine.core.nodes.eventdefinition.SynchronousEventDefinition;
import com.catify.processengine.core.processdefinition.jaxb.TFlowNode;
import com.catify.processengine.core.processdefinition.jaxb.TProcess;
import com.catify.processengine.core.processdefinition.jaxb.TSequenceFlow;
import com.catify.processengine.core.processdefinition.jaxb.TSubProcess;

/**
 * A factory for creating akka node objects. The factory implementation will instantiate an object derived from the {@link FlowElement}.
 * 
 * @author chris
 */
public interface NodeFactory {

	/**
	 * Creates a service node from a jaxb flow node which can be used to create a service node actor
	 * actor.
	 *
	 * @param clientId the client id
	 * @param processJaxb the jaxb process
	 * @param flowNodeJaxb the flow nodeJaxb
	 * @param sequenceFlowsJaxb the list of jaxb sequence flows of that process
	 * @return the start event node
	 */
	FlowElement createServiceNode(String clientId, TProcess processJaxb, ArrayList<TSubProcess> subProcessesJaxb,
			TFlowNode flowNodeJaxb, List<TSequenceFlow> sequenceFlowsJaxb);
	
	/**
	 * Creates the service task worker node.
	 *
	 * @param uniqueProcessId the unique process id
	 * @param uniqueFlowNodeId the unique flow node id
	 * @param outgoingNodes the outgoing nodes
	 * @param messageEventDefinitionInOut the message event definition in out
	 * @param dataObjectHandling the data object handling
	 * @return the service task worker
	 */
	 FlowElement createServiceTaskWorkerNode(String uniqueProcessId, String uniqueFlowNodeId,
				List<ActorRef> outgoingNodes,
				SynchronousEventDefinition messageEventDefinitionInOut, DataObjectService dataObjectHandling);
}
