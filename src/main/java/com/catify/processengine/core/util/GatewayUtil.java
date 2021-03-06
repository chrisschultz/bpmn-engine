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
package com.catify.processengine.core.util;

import com.catify.processengine.core.services.NodeInstanceMediatorService;

/**
 * Some utils for gateways
 * 
 * @author claus straube
 *
 */
public class GatewayUtil {

	/**
	 * Increments the value of the incoming fired sequence flows
	 * and sets the "sequence flows fired" value inside 
	 * {@link NodeInstanceMediatorService}.
	 * 
	 * @param nims a instance of {@link NodeInstanceMediatorService}
	 * @param fired number of fired incoming flows
	 * @param iid instance id
	 * @return fired of incoming flows plus one
	 */
	public static int setFiredPlusOne(NodeInstanceMediatorService nims, String iid) {
		int fired = nims.getSequenceFlowsFired(iid);
		int firedplus = ++fired;
		nims.setSequenceFlowsFired(iid, firedplus);
		return firedplus;
	}
	
}
