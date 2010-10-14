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


package org.apache.isis.extensions.dnd.view.control;

import org.apache.isis.metamodel.consent.Allow;
import org.apache.isis.metamodel.consent.Consent;
import org.apache.isis.metamodel.spec.feature.ObjectActionType;
import org.apache.isis.extensions.dnd.view.ButtonAction;
import org.apache.isis.extensions.dnd.view.View;


public abstract class AbstractButtonAction implements ButtonAction {
    private final String name;
    private final boolean defaultButton;

    public AbstractButtonAction(final String name) {
        this(name, false);
    }

    public AbstractButtonAction(final String name, final boolean defaultButton) {
        this.name = name;
        this.defaultButton = defaultButton;
    }

    public Consent disabled(final View view) {
        return Allow.DEFAULT;
    }

    public String getDescription(final View view) {
        return "";
    }

    public String getHelp(final View view) {
        return "No help available for button";
    }

    public String getName(final View view) {
        return name;
    }

    public ObjectActionType getType() {
        return ObjectActionType.USER;
    }

    public boolean isDefault() {
        return defaultButton;
    }
}