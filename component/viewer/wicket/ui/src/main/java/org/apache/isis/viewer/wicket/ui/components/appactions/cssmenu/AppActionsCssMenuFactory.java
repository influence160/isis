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

package org.apache.isis.viewer.wicket.ui.components.appactions.cssmenu;

import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import org.apache.isis.applib.filter.Filters;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facets.members.order.MemberOrderFacet;
import org.apache.isis.core.metamodel.facets.named.NamedFacet;
import org.apache.isis.core.metamodel.spec.ActionType;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.Contributed;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.progmodel.facets.actions.notinservicemenu.NotInServiceMenuFacet;
import org.apache.isis.viewer.wicket.model.mementos.ObjectAdapterMemento;
import org.apache.isis.viewer.wicket.model.models.ApplicationActionsModel;
import org.apache.isis.viewer.wicket.ui.ComponentFactory;
import org.apache.isis.viewer.wicket.ui.ComponentFactoryAbstract;
import org.apache.isis.viewer.wicket.ui.ComponentType;
import org.apache.isis.viewer.wicket.ui.components.widgets.cssmenu.CssMenuItem;
import org.apache.isis.viewer.wicket.ui.components.widgets.cssmenu.CssMenuItem.Builder;
import org.apache.isis.viewer.wicket.ui.components.widgets.cssmenu.CssMenuLinkFactory;
import org.apache.isis.viewer.wicket.ui.components.widgets.cssmenu.CssMenuPanel;

/**
 * {@link ComponentFactory} for a {@link CssMenuPanel} to represent the
 * {@link ApplicationActionsModel application action}s.
 */
public class AppActionsCssMenuFactory extends ComponentFactoryAbstract {

    private final static long serialVersionUID = 1L;
    
    private final static CssMenuLinkFactory cssMenuLinkFactory = new AppActionsCssMenuLinkFactory();

    static class LogicalServiceAction {
        private final String serviceName;
        private final ObjectAdapter serviceAdapter;
        private final ObjectAdapterMemento serviceAdapterMemento;
        private final ObjectAction objectAction;
        
        LogicalServiceAction(final String serviceName, final ObjectAdapter serviceAdapter, final ObjectAction objectAction) {
            this.serviceName = serviceName;
            this.serviceAdapter = serviceAdapter;
            this.serviceAdapterMemento = ObjectAdapterMemento.createOrNull(serviceAdapter);
            this.objectAction = objectAction;
        }
        @Override
        public String toString() {
            return serviceName + " ~ " + objectAction.getIdentifier().toFullIdentityString();
        }
    }

    public AppActionsCssMenuFactory() {
        super(ComponentType.APPLICATION_ACTIONS);
    }

    /**
     * Generic, so applies to all models.
     */
    @Override
    protected ApplicationAdvice appliesTo(final IModel<?> model) {
        return appliesIf(model instanceof ApplicationActionsModel);
    }

    @Override
    public Component createComponent(final String id, final IModel<?> model) {
        final ApplicationActionsModel applicationActionsModel = (ApplicationActionsModel) model;
        return new CssMenuPanel(id, CssMenuPanel.Style.REGULAR, buildMenu(applicationActionsModel));
    }

    private List<CssMenuItem> buildMenu(final ApplicationActionsModel appActionsModel) {

        final List<ObjectAdapter> serviceAdapters = appActionsModel.getObject();

        final List<LogicalServiceAction> serviceActions = Lists.newArrayList();
        for (final ObjectAdapter serviceAdapter : serviceAdapters) {
            collateServiceActions(serviceAdapter, ActionType.USER, serviceActions);
            collateServiceActions(serviceAdapter, ActionType.PROTOTYPE, serviceActions);
        }
        
        final List<String> serviceNamesInOrder = serviceNamesInOrder(serviceAdapters, serviceActions);
        final Map<String, List<LogicalServiceAction>> serviceActionsByName = groupByServiceName(serviceActions);
        
        // prune any service names that have no service actions
        serviceNamesInOrder.retainAll(serviceActionsByName.keySet());
        
        return buildMenuItems(serviceNamesInOrder, serviceActionsByName);
    }

    /**
     * Builds a hierarchy of {@link CssMenuItem}s, following the provided map of {@link LogicalServiceAction}s (keyed by their service Name).
     */
    private List<CssMenuItem> buildMenuItems(final List<String> serviceNamesInOrder, final Map<String, List<LogicalServiceAction>> serviceActionsByName) {
        final List<CssMenuItem> menuItems = Lists.newArrayList();
        for (String serviceName : serviceNamesInOrder) {
            final CssMenuItem serviceMenuItem = CssMenuItem.newMenuItem(serviceName).build();
            final List<LogicalServiceAction> serviceActionsForName = serviceActionsByName.get(serviceName);
            for (LogicalServiceAction logicalServiceAction : serviceActionsForName) {
                final ObjectAdapter serviceAdapter = logicalServiceAction.serviceAdapter;
                final ObjectSpecification serviceSpec = serviceAdapter.getSpecification();
                if (serviceSpec.isHidden()) {
                    continue;
                }
                final ObjectAdapterMemento serviceAdapterMemento = logicalServiceAction.serviceAdapterMemento;
                final ObjectAction objectAction = logicalServiceAction.objectAction;
                final Builder subMenuItemBuilder = serviceMenuItem.newSubMenuItem(serviceAdapterMemento, objectAction, cssMenuLinkFactory);
                if (subMenuItemBuilder == null) {
                    // not visible
                    continue;
                } 
                subMenuItemBuilder.build();
            }
            if (serviceMenuItem.hasSubMenuItems()) {
                menuItems.add(serviceMenuItem);
            }
        }
        return menuItems;
    }


    // //////////////////////////////////////

    /**
     * Spin through all object actions of the service adapter, and add to the provided List of {@link LogicalServiceAction}s. 
     */
    private static void collateServiceActions(final ObjectAdapter serviceAdapter, ActionType actionType, List<LogicalServiceAction> serviceActions) {
        final ObjectSpecification serviceSpec = serviceAdapter.getSpecification();
        for (final ObjectAction objectAction : serviceSpec.getObjectActions(
                actionType, Contributed.INCLUDED, Filters.<ObjectAction>any())) {
            // skip if annotated to not be included in repository menu
            if (objectAction.getFacet(NotInServiceMenuFacet.class) != null) {
                continue;
            }

            final MemberOrderFacet memberOrderFacet = objectAction.getFacet(MemberOrderFacet.class);
            String serviceName = memberOrderFacet != null? memberOrderFacet.name(): null;
            if(Strings.isNullOrEmpty(serviceName)){
                serviceName = serviceSpec.getFacet(NamedFacet.class).value();
            }
            serviceActions.add(new LogicalServiceAction(serviceName, serviceAdapter, objectAction));
        }
    }

    /**
     * The unique service names, as they appear in order of the provided List of {@link LogicalServiceAction}s.
     * @param serviceAdapters 
     */
    private List<String> serviceNamesInOrder(
            final List<ObjectAdapter> serviceAdapters, final List<LogicalServiceAction> serviceActions) {
        final List<String> serviceNameOrder = Lists.newArrayList();

        // first, order as defined in isis.properties
        for (ObjectAdapter serviceAdapter : serviceAdapters) {
            final ObjectSpecification serviceSpec = serviceAdapter.getSpecification();
            String serviceName = serviceSpec.getFacet(NamedFacet.class).value();
            serviceNameOrder.add(serviceName);
        }
        // then, any other services (eg due to misspellings, at the end)
        for (LogicalServiceAction serviceAction : serviceActions) {
            if(!serviceNameOrder.contains(serviceAction.serviceName)) {
                serviceNameOrder.add(serviceAction.serviceName);
            }
        }
        return serviceNameOrder;
    }

    /**
     * Group the provided {@link LogicalServiceAction}s by their service name. 
     */
    private static Map<String, List<LogicalServiceAction>> groupByServiceName(final List<LogicalServiceAction> serviceActions) {
        final Map<String, List<LogicalServiceAction>> serviceActionsByName = Maps.newTreeMap();
        
        // map available services
        for (LogicalServiceAction serviceAction : serviceActions) {
            List<LogicalServiceAction> serviceActionsForName = serviceActionsByName.get(serviceAction.serviceName);
            if(serviceActionsForName == null) {
                serviceActionsForName = Lists.newArrayList();
                serviceActionsByName.put(serviceAction.serviceName, serviceActionsForName);
            }
            serviceActionsForName.add(serviceAction);
        }
        
        return serviceActionsByName;
    }



}
