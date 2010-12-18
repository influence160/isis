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


package org.apache.isis.core.progmodel.facets.properties.choices;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.apache.isis.core.commons.lang.ArrayUtils;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facets.FacetHolder;
import org.apache.isis.core.metamodel.java5.ImperativeFacet;
import org.apache.isis.core.metamodel.runtimecontext.RuntimeContext;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.metamodel.util.ObjectAdapterUtils;
import org.apache.isis.core.metamodel.util.ObjectInvokeUtils;


public class PropertyChoicesFacetViaMethod extends PropertyChoicesFacetAbstract implements ImperativeFacet {

    private final Method method;
    private final Class<?> choicesClass;
	private final RuntimeContext runtimeContext;

    public PropertyChoicesFacetViaMethod(
    		final Method method, 
    		final Class<?> choicesClass, 
    		final FacetHolder holder, 
    		final RuntimeContext runtimeContext) {
        super(holder);
        this.method = method;
        this.choicesClass = choicesClass;
        this.runtimeContext = runtimeContext;
    }

    /**
     * Returns a singleton list of the {@link Method} provided in the constructor. 
     */
    @Override
    public List<Method> getMethods() {
    	return Collections.singletonList(method);
    }

	@Override
    public boolean impliesResolve() {
		return true;
	}

	@Override
    public boolean impliesObjectChanged() {
		return false;
	}

	@Override
    public Object[] getChoices(final ObjectAdapter owningAdapter, final SpecificationLoader specificationLoader) {
        final Object options = ObjectInvokeUtils.invoke(method, owningAdapter);
        if (options == null) {
            return null;
        }
        if (options.getClass().isArray()) {
            return ArrayUtils.getObjectAsObjectArray(options);
        }
        final ObjectSpecification specification = specificationLoader.loadSpecification(choicesClass);
        return ObjectAdapterUtils.getCollectionAsObjectArray(options, specification, getRuntimeContext());
    }

	@Override
    protected String toStringValues() {
        return "method=" + method + ",class=" + choicesClass;
    }


	//////////////////////////////////////////////////////////
	// Dependencies (from constructor)
	//////////////////////////////////////////////////////////
	
	private RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

}

