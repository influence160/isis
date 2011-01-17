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


package org.apache.isis.core.progmodel.facets.propparam.validate.mask;

import java.lang.reflect.Method;

import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facets.FacetFactory.ProcessClassContext;
import org.apache.isis.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.isis.core.metamodel.facets.FacetFactory.ProcessParameterContext;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.testspec.TestProxySpecification;
import org.apache.isis.core.progmodel.facets.AbstractFacetFactoryTest;
import org.apache.isis.core.progmodel.facets.object.mask.MaskFacet;
import org.apache.isis.core.progmodel.facets.object.mask.annotation.MaskAnnotationForTypeFacetFactory;
import org.apache.isis.core.progmodel.facets.object.mask.annotation.MaskFacetAnnotationForType;
import org.apache.isis.core.progmodel.facets.param.validate.maskannot.MaskAnnotationForParameterFacetFactory;
import org.apache.isis.core.progmodel.facets.param.validate.maskannot.MaskFacetAnnotationForParameter;
import org.apache.isis.core.progmodel.facets.properties.validate.maskannot.MaskAnnotationForPropertyFacetFactory;
import org.apache.isis.core.progmodel.facets.properties.validate.maskannot.MaskFacetAnnotationForProperty;


public class MaskAnnotationFacetFactoryTest extends AbstractFacetFactoryTest {

    private final ObjectSpecification customerNoSpec = new TestProxySpecification(String.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        reflector.setLoadSpecificationStringReturn(customerNoSpec);
    }


    public void testMaskAnnotationPickedUpOnClass() {
        MaskAnnotationForTypeFacetFactory facetFactory = new MaskAnnotationForTypeFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        @Mask("###")
        class Customer {}
        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(MaskFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MaskFacetAnnotationForType);
        final MaskFacetAnnotationForType maskFacet = (MaskFacetAnnotationForType) facet;
        assertEquals("###", maskFacet.value());
    }

    public void testMaskAnnotationPickedUpOnProperty() {
        MaskAnnotationForPropertyFacetFactory facetFactory = new MaskAnnotationForPropertyFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        @edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
        class Customer {
            @SuppressWarnings("unused")
            @Mask("###")
            public String getFirstName() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getFirstName");

        facetFactory.process(new ProcessMethodContext(Customer.class, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(MaskFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MaskFacetAnnotationForProperty);
        final MaskFacetAnnotationForProperty maskFacet = (MaskFacetAnnotationForProperty) facet;
        assertEquals("###", maskFacet.value());
    }

    public void testMaskAnnotationPickedUpOnActionParameter() {
        MaskAnnotationForParameterFacetFactory facetFactory = new MaskAnnotationForParameterFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        @edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
        class Customer {
            @SuppressWarnings("unused")
            public void someAction(@Mask("###") final String foo) {}
        }
        final Method method = findMethod(Customer.class, "someAction", new Class[] { String.class });

        facetFactory.processParams(new ProcessParameterContext(method, 0, facetedMethodParameter));

        final Facet facet = facetedMethodParameter.getFacet(MaskFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MaskFacetAnnotationForParameter);
        final MaskFacetAnnotationForParameter maskFacet = (MaskFacetAnnotationForParameter) facet;
        assertEquals("###", maskFacet.value());
    }

    public void testMaskAnnotationNotIgnoredForNonStringsProperty() {
        MaskAnnotationForPropertyFacetFactory facetFactory = new MaskAnnotationForPropertyFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        @edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
        class Customer {
            @SuppressWarnings("unused")
            @Mask("###")
            public int getNumberOfOrders() {
                return 0;
            }
        }
        final Method method = findMethod(Customer.class, "getNumberOfOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, method, methodRemover, facetedMethod));

        assertNotNull(facetedMethod.getFacet(MaskFacet.class));
    }

    public void testMaskAnnotationNotIgnoredForPrimitiveOnActionParameter() {
        MaskAnnotationForParameterFacetFactory facetFactory = new MaskAnnotationForParameterFacetFactory();
        facetFactory.setSpecificationLookup(reflector);

        @edu.umd.cs.findbugs.annotations.SuppressWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
        class Customer {
            @SuppressWarnings("unused")
            public void someAction(@Mask("###") final int foo) {}
        }
        final Method method = findMethod(Customer.class, "someAction", new Class[] { int.class });

        facetFactory.processParams(new ProcessParameterContext(method, 0, facetedMethodParameter));

        assertNotNull(facetedMethodParameter.getFacet(MaskFacet.class));
    }

}

