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

package org.apache.isis.viewer.wicket.model.util;

import com.google.common.base.Function;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager.ConcurrencyChecking;
import org.apache.isis.viewer.wicket.model.mementos.ObjectAdapterMemento;

public final class ObjectAdapterFunctions {

    private ObjectAdapterFunctions() {
    }

    /**
     * Use {@link ObjectAdapter.Functions}
     */
    @Deprecated
    public static Function<Object, ObjectAdapter> fromPojo(final AdapterManager adapterManager) {
        return ObjectAdapter.Functions.adapterForUsing(adapterManager);
    }

    public static Function<ObjectAdapterMemento, ObjectAdapter> fromMemento() {
        return new Function<ObjectAdapterMemento, ObjectAdapter>() {
            @Override
            public ObjectAdapter apply(final ObjectAdapterMemento from) {
                return from.getObjectAdapter(ConcurrencyChecking.NO_CHECK);
            }
        };
    }

    public static Function<ObjectAdapter, ObjectAdapterMemento> toMemento() {
        return new Function<ObjectAdapter, ObjectAdapterMemento>() {

            @Override
            public ObjectAdapterMemento apply(ObjectAdapter from) {
                return ObjectAdapterMemento.createOrNull(from);
            }
            
        };
    }

}
