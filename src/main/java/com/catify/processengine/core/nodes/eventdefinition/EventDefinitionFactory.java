/**
 * *******************************************************
 * Copyright (C) 2013 catify <info@catify.com>
 * *******************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.catify.processengine.core.nodes.eventdefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import akka.actor.ActorRef;

import com.catify.processengine.core.data.services.impl.IdService;
import com.catify.processengine.core.processdefinition.jaxb.TCatchEvent;
import com.catify.processengine.core.processdefinition.jaxb.TEventDefinition;
import com.catify.processengine.core.processdefinition.jaxb.TFlowElement;
import com.catify.processengine.core.processdefinition.jaxb.TFlowNode;
import com.catify.processengine.core.processdefinition.jaxb.TMessageEventDefinition;
import com.catify.processengine.core.processdefinition.jaxb.TMessageIntegration;
import com.catify.processengine.core.processdefinition.jaxb.TProcess;
import com.catify.processengine.core.processdefinition.jaxb.TSubProcess;
import com.catify.processengine.core.processdefinition.jaxb.TTerminateEventDefinition;
import com.catify.processengine.core.processdefinition.jaxb.TThrowEvent;
import com.catify.processengine.core.processdefinition.jaxb.services.ExtensionService;
import com.catify.processengine.core.services.ActorReferenceService;

/**
 * A factory for creating EventDefinition objects.
 *
 * @author chris
 */
@Configurable
public class EventDefinitionFactory {
	
	public static final Logger LOG = LoggerFactory
			.getLogger(EventDefinitionFactory.class);

	public EventDefinitionFactory() {
		
	}
	
	public EventDefinition getEventDefinition(EventDefinitionParameter eventDefinitionParameter) {
//		// get the event definition (if any)
		TEventDefinition eventDefinitionJaxb = getTEventDefinition(eventDefinitionParameter.clientId, eventDefinitionParameter.processJaxb,
				eventDefinitionParameter.subProcessesJaxb, eventDefinitionParameter.flowNodeJaxb);
	
		// if there is no event definition, create an EmptyEventDefinition actor
		if (eventDefinitionJaxb == null) {
			return new EmptyEventDefinition();
			// else create the implementing event definition actor
		} else {
			// *** create a message event actor ***
			if (eventDefinitionJaxb.getClass().equals(
					TMessageEventDefinition.class)) {
				return createMessageEventDefinition(eventDefinitionParameter.clientId, eventDefinitionParameter.processJaxb, eventDefinitionParameter.subProcessesJaxb, 
						eventDefinitionParameter.flowNodeJaxb, (TMessageEventDefinition) eventDefinitionJaxb);
		
			// *** create a terminate event actor ***
			} else if (eventDefinitionJaxb.getClass().equals(
					TTerminateEventDefinition.class)) {
				return createTerminateEventDefinition(eventDefinitionParameter.clientId, eventDefinitionParameter.processJaxb, eventDefinitionParameter.subProcessesJaxb, 
						eventDefinitionParameter.flowNodeJaxb, eventDefinitionJaxb);
			}
			// return empty event definition for unimplemented event definitions
			LOG.error(String.format("Unimplemented event definition %s found. Associated events will fail!", getTEventDefinition(eventDefinitionParameter.clientId, eventDefinitionParameter.processJaxb,
					eventDefinitionParameter.subProcessesJaxb, eventDefinitionParameter.flowNodeJaxb)));
			return null;
		}
	}

	/**
	 * Creates a new MessageEventDefinition object.
	 *
	 * @param processJaxb the processJaxb
	 * @param flowNodeJaxb the flow nodeJaxb
	 * @param messageEventDefinitionJaxb the event definitionJaxb
	 * @return the event definition
	 */
	private EventDefinition createMessageEventDefinition(String clientId, TProcess processJaxb, ArrayList<TSubProcess> subProcessesJaxb,
			TFlowNode flowNodeJaxb, TEventDefinition eventDefinitionJaxb) {
		
		TMessageIntegration messageIntegration = ExtensionService.getTMessageIntegration((TMessageEventDefinition) eventDefinitionJaxb);
		EventDefinition eventDefinition = null;
		
		// message event is catching
		if (flowNodeJaxb.getClass().getSuperclass()
				.equals(TCatchEvent.class)) {
			
			// create catching message event definition to be used in akka actor	
			eventDefinition = getMessageEventDefinitionCatch(clientId,
					processJaxb, subProcessesJaxb, flowNodeJaxb,
					messageIntegration);
	
		// message event is throwing
		} else if (flowNodeJaxb.getClass().getSuperclass()
				.equals(TThrowEvent.class)) {
			
			eventDefinition = getMessageEventDefinitionThrow(clientId,
					processJaxb, subProcessesJaxb, flowNodeJaxb,
					messageIntegration);
		}
		
		return 	eventDefinition;
	}

	public EventDefinition getMessageEventDefinitionCatch(String clientId,
			TProcess processJaxb, ArrayList<TSubProcess> subProcessesJaxb,
			TFlowNode flowNodeJaxb, TMessageIntegration messageIntegration) {
		EventDefinition eventDefinition;
		eventDefinition = new MessageEventDefinition_Catch(
				IdService.getUniqueProcessId(clientId, processJaxb),
				IdService.getUniqueFlowNodeId(clientId, processJaxb, subProcessesJaxb,
						flowNodeJaxb), 
				ActorReferenceService.getActorReferenceString(
						IdService.getUniqueFlowNodeId(clientId, processJaxb, subProcessesJaxb,
								flowNodeJaxb)),  
				messageIntegration);
		return eventDefinition;
	}

	public EventDefinition getMessageEventDefinitionThrow(String clientId,
			TProcess processJaxb, ArrayList<TSubProcess> subProcessesJaxb,
			TFlowNode flowNodeJaxb, TMessageIntegration messageIntegration) {
		EventDefinition eventDefinition;
		// create throwing message event definition to be used in akka actor
		eventDefinition = new MessageEventDefinition_Throw(
				IdService.getUniqueProcessId(clientId, processJaxb),
				IdService.getUniqueFlowNodeId(clientId, processJaxb, subProcessesJaxb,
						flowNodeJaxb), 
				ActorReferenceService.getActorReferenceString(
						IdService.getUniqueFlowNodeId(clientId, processJaxb, subProcessesJaxb,
								flowNodeJaxb)),  
				messageIntegration);
		return eventDefinition;
	}

	/**
	 * Creates a new TerminateEventDefinition object.
	 *
	 * @param processJaxb the processJaxb
	 * @param flowNodeJaxb the flow nodeJaxb
	 * @param terminateEventDefinitionJaxb the event definitionJaxb
	 * @return the event definition
	 */
	private EventDefinition createTerminateEventDefinition(String clientId, TProcess processJaxb, ArrayList<TSubProcess> subProcessesJaxb,
			TFlowNode flowNodeJaxb, TEventDefinition eventDefinitionJaxb) {

		EventDefinition eventDefinition = new TerminateEventDefinition(
				IdService.getUniqueProcessId(clientId, processJaxb),
				IdService.getUniqueFlowNodeId(clientId, processJaxb, subProcessesJaxb, flowNodeJaxb),
				new ActorReferenceService().getActorReference(
						IdService.getUniqueFlowNodeId(clientId, processJaxb, subProcessesJaxb,
								flowNodeJaxb)),
				getOtherActorReferences(clientId, processJaxb, subProcessesJaxb, flowNodeJaxb)				
				);
		
		return eventDefinition;
	}
	
	/**
	 * Gets all actor references of the process (including sub processes) 
	 * but excluding the actor reference of a given flow node.
	 *
	 * @param clientId the client id
	 * @param processJaxb the process jaxb
	 * @param subProcessesJaxb the sub processes jaxb
	 * @param flowNodeJaxb the jaxb flow node to be excluded
	 * @return the other actor references
	 */
	private Set<ActorRef> getOtherActorReferences(String clientId, TProcess processJaxb, ArrayList<TSubProcess> subProcessesJaxb,
			TFlowNode flowNodeJaxb) {
		Set<ActorRef> actorReferences = getAllActorReferences(clientId, processJaxb, subProcessesJaxb, null);
		
		// remove actor reference of given flow node
		actorReferences.remove(new ActorReferenceService().getActorReference(
						IdService.getUniqueFlowNodeId(clientId, processJaxb, subProcessesJaxb,
								flowNodeJaxb)));

		return actorReferences;
	}
	
	/**
	 * Gets all actor references of the process (including sub processes).
	 *
	 * @param clientId the client id
	 * @param processJaxb the process jaxb
	 * @param subProcessesJaxb the sub processes jaxb
	 * @param flowNodeJaxb the flow node jaxb
	 * @return the all actor references
	 */
	private Set<ActorRef> getAllActorReferences(String clientId, TProcess processJaxb, ArrayList<TSubProcess> subProcessesJaxb,
			TSubProcess subProcessJaxb) {
		Set<ActorRef> actorReferences = new HashSet<ActorRef>();
		
		List<JAXBElement<? extends TFlowElement>> flowElements = new ArrayList<JAXBElement<? extends TFlowElement>>();
		if (subProcessJaxb == null) {
			flowElements = processJaxb.getFlowElement();
		} else {
			flowElements = subProcessJaxb.getFlowElement();
		}
		
		for (JAXBElement<? extends TFlowElement> flowElement : flowElements) {
			if (flowElement.getValue() instanceof TFlowNode) {
				actorReferences.add(new ActorReferenceService().getActorReference(
						IdService.getUniqueFlowNodeId(clientId, processJaxb, subProcessesJaxb,
								(TFlowNode) flowElement.getValue())));
			}
			if (flowElement.getValue() instanceof TSubProcess) {
				// create local copies for recursion
				ArrayList<TSubProcess> recursiveSubProcessesJaxb = new ArrayList<TSubProcess>(subProcessesJaxb);
				recursiveSubProcessesJaxb.add((TSubProcess) flowElement.getValue());
				TSubProcess newSubProcessJaxb = (TSubProcess) flowElement.getValue();
				
				actorReferences.addAll(getAllActorReferences(clientId, processJaxb, recursiveSubProcessesJaxb, newSubProcessJaxb));
			}
		}
		
		return actorReferences;
	}
	
	/**
	 * Gets the jaxb event definition.
	 *
	 * @param clientId the client id
	 * @param processJaxb the jaxb process 
	 * @param subProcessesJaxb the jaxb sub processes
	 * @param flowNodeJaxb the jaxb flow node 
	 * @return the jaxb event definition or null if none is found
	 */
	private TEventDefinition getTEventDefinition(String clientId,
			TProcess processJaxb, ArrayList<TSubProcess> subProcessesJaxb,
			TFlowNode flowNodeJaxb) {
		if (flowNodeJaxb instanceof TCatchEvent) {
			if (((TCatchEvent) flowNodeJaxb).getEventDefinition().size() > 0) {
				return ((TCatchEvent) flowNodeJaxb).getEventDefinition().get(0).getValue();
			} else {
				return 	null;
			}
		} else if (flowNodeJaxb instanceof TThrowEvent) {
			if (((TThrowEvent) flowNodeJaxb).getEventDefinition().size() > 0) {
				return ((TThrowEvent) flowNodeJaxb).getEventDefinition().get(0).getValue();
			} else {
				return 	null;
			}
		} else return null;
	}

}
