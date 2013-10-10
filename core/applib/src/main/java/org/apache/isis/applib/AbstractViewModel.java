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

package org.apache.isis.applib;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.ViewModel;

/**
 * Convenience super class for all view models that wish to interact with the
 * container.
 * 
 * <p>
 * Subclassing is NOT mandatory; the methods in this superclass can be pushed
 * down into domain objects and another superclass used if required.
 * 
 * @see org.apache.isis.applib.DomainObjectContainer
 */
public abstract class AbstractViewModel extends AbstractContainedObject implements ViewModel {

    @Override
    public abstract String viewModelMemento();
    
    @Override
    public abstract void viewModelInit(final String memento);
    
    /**
     * Equivalent to annotating every property and collection with {@link Disabled}.
     */
    public String disabled(Identifier.Type type) {
        return type == Identifier.Type.PROPERTY_OR_COLLECTION? "Read-only": null;
    }

}
