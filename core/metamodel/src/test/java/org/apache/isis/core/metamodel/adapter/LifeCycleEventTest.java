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


package org.apache.isis.core.metamodel.adapter;

import junit.framework.TestCase;

import org.apache.isis.core.metamodel.adapter.LifeCycleEvent;
import org.apache.isis.core.metamodel.facets.object.callbacks.CreatedCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.LoadedCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.LoadingCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.PersistedCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.PersistingCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.RemovedCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.RemovingCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.UpdatedCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.UpdatingCallbackFacet;


public class LifeCycleEventTest extends TestCase {

    private LifeCycleEvent lifeCycleEvent;

    public void testGetFacetClassforCreated() {
        lifeCycleEvent = LifeCycleEvent.CREATED;
        assertEquals(CreatedCallbackFacet.class, lifeCycleEvent.getFacetClass());
    }

    public void testGetFacetClassforDeleted() {
        lifeCycleEvent = LifeCycleEvent.DELETED;
        assertEquals(RemovedCallbackFacet.class, lifeCycleEvent.getFacetClass());
    }

    public void testGetFacetClassforDeleting() {
        lifeCycleEvent = LifeCycleEvent.DELETING;
        assertEquals(RemovingCallbackFacet.class, lifeCycleEvent.getFacetClass());
    }

    public void testGetFacetClassforLoaded() {
        lifeCycleEvent = LifeCycleEvent.LOADED;
        assertEquals(LoadedCallbackFacet.class, lifeCycleEvent.getFacetClass());
    }

    public void testGetFacetClassforLoading() {
        lifeCycleEvent = LifeCycleEvent.LOADING;
        assertEquals(LoadingCallbackFacet.class, lifeCycleEvent.getFacetClass());
    }

    public void testGetFacetClassforSaved() {
        lifeCycleEvent = LifeCycleEvent.SAVED;
        assertEquals(PersistedCallbackFacet.class, lifeCycleEvent.getFacetClass());
    }

    public void testGetFacetClassforSaving() {
        lifeCycleEvent = LifeCycleEvent.SAVING;
        assertEquals(PersistingCallbackFacet.class, lifeCycleEvent.getFacetClass());
    }

    public void testGetFacetClassforUpdated() {
        lifeCycleEvent = LifeCycleEvent.UPDATED;
        assertEquals(UpdatedCallbackFacet.class, lifeCycleEvent.getFacetClass());
    }

    public void testGetFacetClassforUpdating() {
        lifeCycleEvent = LifeCycleEvent.UPDATING;
        assertEquals(UpdatingCallbackFacet.class, lifeCycleEvent.getFacetClass());
    }

}
