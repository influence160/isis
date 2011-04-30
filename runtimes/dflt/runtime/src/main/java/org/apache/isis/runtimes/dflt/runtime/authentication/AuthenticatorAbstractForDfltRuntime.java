/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.isis.runtimes.dflt.runtime.authentication;

import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.runtime.authentication.standard.AuthenticatorAbstract;
import org.apache.isis.runtimes.dflt.runtime.system.DeploymentType;
import org.apache.isis.runtimes.dflt.runtime.system.SystemConstants;



public abstract class AuthenticatorAbstractForDfltRuntime extends AuthenticatorAbstract {

	public AuthenticatorAbstractForDfltRuntime(IsisConfiguration configuration) {
		super(configuration);
	}

	
	////////////////////////////////////////////////////////
	// Helpers
	////////////////////////////////////////////////////////

    /**
     * Helper method for convenience of implementations that depend on the {@link DeploymentType}.
     */
	public DeploymentType getDeploymentType() {
		String deploymentTypeStr = getConfiguration().getString(SystemConstants.DEPLOYMENT_TYPE_KEY);
		if(deploymentTypeStr==null) {
			throw new IllegalStateException("Expect value for '" + SystemConstants.DEPLOYMENT_TYPE_KEY + "' to be bound into IsisConfiguration");
		}
    	return DeploymentType.lookup(deploymentTypeStr);
	}


	
}
