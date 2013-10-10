/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.viewer.restfulobjects.rendering.domainobjects;

import java.util.List;
import java.util.Map;

import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionFacet;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import org.apache.isis.core.metamodel.facets.members.resolve.RenderFacet;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.rendering.LinkBuilder;
import org.apache.isis.viewer.restfulobjects.rendering.LinkFollowSpecs;
import org.apache.isis.viewer.restfulobjects.rendering.RendererContext;
import org.apache.isis.viewer.restfulobjects.rendering.domaintypes.CollectionDescriptionReprRenderer;

import com.google.common.collect.Lists;

public class ObjectCollectionReprRenderer extends AbstractObjectMemberReprRenderer<ObjectCollectionReprRenderer, OneToManyAssociation> {

    public ObjectCollectionReprRenderer(final RendererContext resourceContext, final LinkFollowSpecs linkFollower, final String collectionId, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, collectionId, RepresentationType.OBJECT_COLLECTION, representation, Where.PARENTED_TABLES);
    }

    @Override
    public JsonRepresentation render() {

        renderMemberContent();
        
        final RenderFacet renderFacet = objectMember.getFacet(RenderFacet.class);
        boolean eagerlyRender = renderFacet != null && renderFacet.value() == Type.EAGERLY;
        
        if ((mode.isInline() && eagerlyRender) || mode.isStandalone() || mode.isMutated() || mode.isEventSerialization() || !objectAdapter.representsPersistent()) {
            addValue();
        }
        if(!mode.isEventSerialization()) {
            putDisabledReasonIfDisabled();
        }

        if (mode.isStandalone() || mode.isMutated()) {
            addExtensionsIsisProprietaryChangedObjects();
        }

        return representation;
    }

    // ///////////////////////////////////////////////////
    // value
    // ///////////////////////////////////////////////////

    private void addValue() {
        final ObjectAdapter valueAdapter = objectMember.get(objectAdapter);
        if (valueAdapter == null) {
            return;
        }
        
        final RenderFacet renderFacet = objectMember.getFacet(RenderFacet.class);
        boolean eagerlyRender = renderFacet != null && renderFacet.value() == Type.EAGERLY && rendererContext.canEagerlyRender(valueAdapter);

        final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(valueAdapter);
        final List<JsonRepresentation> list = Lists.newArrayList();
        for (final ObjectAdapter elementAdapter : facet.iterable(valueAdapter)) {

            final LinkBuilder valueLinkBuilder = DomainObjectReprRenderer.newLinkToBuilder(rendererContext, Rel.VALUE, elementAdapter);
            if(eagerlyRender) {
                final DomainObjectReprRenderer renderer = new DomainObjectReprRenderer(getRendererContext(), getLinkFollowSpecs(), JsonRepresentation.newMap());
                renderer.with(elementAdapter);
                if(mode.isEventSerialization()) {
                    renderer.asEventSerialization();
                }

                valueLinkBuilder.withValue(renderer.render());
            }

            list.add(valueLinkBuilder.build());
        }

        representation.mapPut("value", list);
    }

    // ///////////////////////////////////////////////////
    // details link
    // ///////////////////////////////////////////////////

    /**
     * Mandatory hook method to support x-ro-follow-links
     */
    @Override
    protected void followDetailsLink(final JsonRepresentation detailsLink) {
        final ObjectCollectionReprRenderer renderer = new ObjectCollectionReprRenderer(getRendererContext(), getLinkFollowSpecs(), null, JsonRepresentation.newMap());
        renderer.with(new ObjectAndCollection(objectAdapter, objectMember)).asFollowed();
        detailsLink.mapPut("value", renderer.render());
    }

    // ///////////////////////////////////////////////////
    // mutators
    // ///////////////////////////////////////////////////

    @Override
    protected void addMutatorsIfEnabled() {
        if (usability().isVetoed()) {
            return;
        }

        final CollectionSemantics semantics = CollectionSemantics.determine(objectMember);
        addMutatorLink(semantics.getAddToKey());
        addMutatorLink(semantics.getRemoveFromKey());

        return;
    }

    private void addMutatorLink(final String key) {
        final Map<String, MutatorSpec> mutators = memberType.getMutators();
        final MutatorSpec mutatorSpec = mutators.get(key);
        addLinkFor(mutatorSpec);
    }

    // ///////////////////////////////////////////////////
    // extensions and links
    // ///////////////////////////////////////////////////

    @Override
    protected void addLinksToFormalDomainModel() {
        final LinkBuilder linkBuilder = CollectionDescriptionReprRenderer.newLinkToBuilder(rendererContext, Rel.DESCRIBEDBY, objectAdapter.getSpecification(), objectMember);
        getLinks().arrayAdd(linkBuilder.build());
    }

    @Override
    protected void addLinksIsisProprietary() {
        // none
    }

    @Override
    protected void putExtensionsIsisProprietary() {
        final CollectionSemantics semantics = CollectionSemantics.determine(objectMember);
        getExtensions().mapPut("collectionSemantics", semantics.name().toLowerCase());
    }


}