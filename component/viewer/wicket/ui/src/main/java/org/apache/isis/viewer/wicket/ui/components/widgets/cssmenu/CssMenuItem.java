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

package org.apache.isis.viewer.wicket.ui.components.widgets.cssmenu;

import static org.hamcrest.CoreMatchers.is;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.Model;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.ensure.Ensure;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager.ConcurrencyChecking;
import org.apache.isis.core.metamodel.consent.Consent;
import org.apache.isis.core.metamodel.facets.describedas.DescribedAsFacet;
import org.apache.isis.core.metamodel.facets.members.cssclass.CssClassFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.viewer.wicket.model.links.LinkAndLabel;
import org.apache.isis.viewer.wicket.model.mementos.ObjectAdapterMemento;
import org.apache.isis.viewer.wicket.ui.pages.PageAbstract;
import org.apache.isis.viewer.wicket.ui.util.Components;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;

public class CssMenuItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ID_MENU_LINK = "menuLink";

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public static class Builder {
        private final CssMenuItem cssMenuItem;

        private Builder(final String name) {
            cssMenuItem = new CssMenuItem(name);
        }

        public Builder parent(final CssMenuItem parent) {
            cssMenuItem.setParent(parent);
            return this;
        }

        public <T extends Page> Builder link(final Class<T> pageClass) {
            final AbstractLink link = new SubmitLink(ID_MENU_LINK); 
            return link(link);
        }

        public <T extends Page> Builder link(final AbstractLink link) {
            Ensure.ensureThatArg(link.getId(), is(ID_MENU_LINK));
            cssMenuItem.setLink(link);
            return this;
        }

        public <T extends Page> Builder enabled(final String disabledReasonIfAny) {
            cssMenuItem.setEnabled(disabledReasonIfAny == null);
            cssMenuItem.setDisabledReason(disabledReasonIfAny);
            return this;
        }

        public Builder describedAs(String descriptionIfAny) {
            cssMenuItem.setDescription(descriptionIfAny);
            return this;
        }

        public Builder returnsBlobOrClob(boolean blobOrClob) {
            cssMenuItem.setReturnsBlobOrClob(blobOrClob);
            return this;
        }

        /**
         * Access the {@link CssMenuItem} before it is attached to its parent.
         * 
         * @see #build()
         */
        public CssMenuItem itemBeingBuilt() {
            return cssMenuItem;
        }

        public Builder prototyping(boolean prototype) {
            cssMenuItem.setPrototyping(prototype);
            return this;
        }

        public Builder withActionIdentifier(String actionIdentifier) {
            cssMenuItem.setActionIdentifier(actionIdentifier);
            return this;
        }

        public void withCssClass(String cssClass) {
            cssMenuItem.setCssClass(cssClass);
        }

        /**
         * Returns the built {@link CssMenuItem}, associating with
         * {@link #parent(CssMenuItem) parent} (if specified).
         */
        public CssMenuItem build() {
            if (cssMenuItem.parent != null) {
                cssMenuItem.parent.subMenuItems.add(cssMenuItem);
            }
            return cssMenuItem;
        }

    }

    private final String name;
    private final List<CssMenuItem> subMenuItems = Lists.newArrayList();
    private CssMenuItem parent;

    private AbstractLink link;
    private boolean enabled = true; // unless disabled
    private String disabledReason;
    private boolean blobOrClob = false; // unless set otherwise
    private boolean prototype = false; // unless set otherwise

    static final String ID_MENU_LABEL = "menuLabel";

    static final String ID_SUB_MENU_ITEMS = "subMenuItems";

    private String actionIdentifier;
    private String cssClass;

    private String description;



    /**
     * Factory method returning {@link Builder builder}.
     */
    public static Builder newMenuItem(final String name) {
        return new Builder(name);
    }


    public void setActionIdentifier(String actionIdentifier) {
        this.actionIdentifier = actionIdentifier;
    }

    public void setPrototyping(boolean prototype) {
        this.prototype = prototype;
        
    }

    private CssMenuItem(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean hasParent() {
        return parent != null;
    }

    private void setParent(final CssMenuItem parent) {
        this.parent = parent;
    }

    public Builder newSubMenuItem(final String name) {
        return CssMenuItem.newMenuItem(name).parent(this);
    }

    public List<CssMenuItem> getSubMenuItems() {
        return Collections.unmodifiableList(subMenuItems);
    }

    public boolean hasSubMenuItems() {
        return subMenuItems.size() > 0;
    }

    public AbstractLink getLink() {
        return link;
    }

    private void setLink(final AbstractLink link) {
        this.link = link;
    }

    public boolean isEnabled() {
        return enabled;
    }

    private void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setReturnsBlobOrClob(boolean blobOrClob) {
        this.blobOrClob = blobOrClob;
    }

    /**
     * Only populated if not {@link #isEnabled() enabled}.
     */
    public String getDisabledReason() {
        return disabledReason;
    }

    public void setDisabledReason(final String disabledReason) {
        this.disabledReason = disabledReason;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // //////////////////////////////////////////////////////////////
    // To add submenu items
    // //////////////////////////////////////////////////////////////

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final Where where = Where.ANYWHERE;

    /**
     * Creates a {@link Builder} for a submenu item invoking an action on the provided
     * {@link ObjectAdapterMemento target adapter}.
     * 
     * @return the builder, else <tt>null</tt> if the action is not visible for
     *         the current user.
     */
    public Builder newSubMenuItem(final ObjectAdapterMemento targetAdapterMemento, final ObjectAction objectAction, final CssMenuLinkFactory cssMenuLinkFactory) {

        // check visibility
        final AuthenticationSession session = getAuthenticationSession();
        final ObjectAdapter adapter = targetAdapterMemento.getObjectAdapter(ConcurrencyChecking.CHECK);
        final Consent visibility = objectAction.isVisible(session, adapter, where);
        if (visibility.isVetoed()) {
            return null;
        }

        // build the link
        final LinkAndLabel linkAndLabel = cssMenuLinkFactory.newLink(targetAdapterMemento, objectAction, PageAbstract.ID_MENU_LINK);
        if(linkAndLabel==null) {
            // can only get a null if invisible, so this should not happen given guard above
            return null;
        }
        final AbstractLink link = linkAndLabel.getLink();
        final String actionLabel = linkAndLabel.getLabel();

        final Consent usability = objectAction.isUsable(session, adapter, where);
        final String reasonDisabledIfAny = usability.getReason();
        
        final DescribedAsFacet describedAsFacet = objectAction.getFacet(DescribedAsFacet.class);
        final String descriptionIfAny = describedAsFacet != null? describedAsFacet.value(): null;
        
        final CssClassFacet cssClassFacet = objectAction.getFacet(CssClassFacet.class);

        final boolean blobOrClob = returnsBlobOrClob(objectAction);
        final boolean prototype = isExplorationOrPrototype(objectAction);
        final String actionIdentifier = actionIdentifierFor(objectAction);

        Builder builder = newSubMenuItem(actionLabel)
                .link(link)
                .describedAs(descriptionIfAny)
                .enabled(reasonDisabledIfAny)
                .returnsBlobOrClob(blobOrClob)
                .prototyping(prototype)
                .withActionIdentifier(actionIdentifier);
        if (cssClassFacet != null) {
            builder.withCssClass(cssClassFacet.value());
        }
        return builder;
    }

    public static boolean returnsBlobOrClob(final ObjectAction objectAction) {
        boolean blobOrClob = false;
        final ObjectSpecification returnType = objectAction.getReturnType();
        if(returnType != null) {
            Class<?> cls = returnType.getCorrespondingClass();
            if (Blob.class.isAssignableFrom(cls) || Clob.class.isAssignableFrom(cls)) {
                blobOrClob = true;
            }
        }
        return blobOrClob;
    }

    public static boolean isExplorationOrPrototype(final ObjectAction action) {
        return action.getType().isExploration() || action.getType().isPrototype();
    }

    public static String actionIdentifierFor(ObjectAction action) {
        @SuppressWarnings("unused")
        final Identifier identifier = action.getIdentifier();
        
        final String className = action.getOnType().getShortIdentifier();
        final String actionId = action.getId();
        return className + "-" + actionId;
    }

    public static String cssClassFor(ObjectAction action) {
        CssClassFacet cssClassFacet = action.getFacet(CssClassFacet.class);
        return cssClassFacet != null ? cssClassFacet.value() : null;
    }

    /**
     * Creates a {@link Builder} for a submenu item where the provided {@link CssMenuLinkFactory} is able to provide the target adapter. 
     */
    public Builder newSubMenuItem(final ObjectAction objectAction, final CssMenuLinkFactory cssMenuLinkFactory) {

        final LinkAndLabel linkAndLabel = cssMenuLinkFactory.newLink(null, objectAction, PageAbstract.ID_MENU_LINK);

        final AbstractLink link = linkAndLabel.getLink();
        final String actionLabel = linkAndLabel.getLabel();
        Builder builder = this.newSubMenuItem(actionLabel).link(link).prototyping(linkAndLabel.isPrototype());

        CssClassFacet cssClassFacet = objectAction.getFacet(CssClassFacet.class);
        if (cssClassFacet != null) {
            builder.withCssClass(cssClassFacet.value());
        }
        return builder;
    }

    // //////////////////////////////////////////////////////////////
    // Build wicket components from the menu item.
    // //////////////////////////////////////////////////////////////

    void addTo(final MarkupContainer markupContainer) {

        final Component menuItemComponent = addMenuItemComponentTo(markupContainer);
        addSubMenuItemComponentsIfAnyTo(markupContainer);

        addCssClassAttributesIfRequired(menuItemComponent);
    }

    private Component addMenuItemComponentTo(final MarkupContainer markupContainer) {
        final AbstractLink link = getLink();
        final Label label = new Label(CssMenuItem.ID_MENU_LABEL, Model.of(this.getName()));

        if (this.isEnabled() && link != null) {
            // show link...
            markupContainer.add(link);
            link.add(label);

            if(this.description != null) {
                label.add(new AttributeModifier("title", Model.of(description)));
            }
            if(this.blobOrClob) {
                link.add(new CssClassAppender("noVeil"));
            }
            if(this.prototype) {
                link.add(new CssClassAppender("prototype"));
            }
            if(this.cssClass != null) {
                markupContainer.add(new CssClassAppender(this.cssClass));
            }
            link.add(new CssClassAppender(this.actionIdentifier));

            // .. and hide label
            Components.permanentlyHide(markupContainer, CssMenuItem.ID_MENU_LABEL);
            return link;
        } else {
            // hide link...
            Components.permanentlyHide(markupContainer, ID_MENU_LINK);
            // ... and show label, along with disabled reason
            label.add(new AttributeModifier("title", Model.of(this.getDisabledReason())));
            label.add(new AttributeModifier("class", Model.of("disabled")));

            markupContainer.add(label);

            return label;
        }
    }

    private void addSubMenuItemComponentsIfAnyTo(final MarkupContainer menuItemMarkup) {
        final List<CssMenuItem> subMenuItems = getSubMenuItems();
        if (subMenuItems.isEmpty()) {
            Components.permanentlyHide(menuItemMarkup, CssMenuItem.ID_SUB_MENU_ITEMS);
        } else {
            menuItemMarkup.add(new CssSubMenuItemsPanel(CssMenuItem.ID_SUB_MENU_ITEMS, subMenuItems));
        }
    }

    private void addCssClassAttributesIfRequired(final Component linkComponent) {
        if (!hasSubMenuItems()) {
            return;
        }
        if (this.hasParent()) {
            linkComponent.add(new CssClassAppender("parent"));
        } else {
            linkComponent.add(new CssClassAppender("top-parent"));
        }
    }

    // //////////////////////////////////////////////////////////////
    // dependencies
    // //////////////////////////////////////////////////////////////

    protected AuthenticationSession getAuthenticationSession() {
        return IsisContext.getAuthenticationSession();
    }


}