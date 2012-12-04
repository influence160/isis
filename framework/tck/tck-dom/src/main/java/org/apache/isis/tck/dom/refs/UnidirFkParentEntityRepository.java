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

package org.apache.isis.tck.dom.refs;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.tck.dom.AbstractEntityRepository;

@Named("UnidirParentEntities")
@ObjectType("UnidirParentEntities")
public class UnidirFkParentEntityRepository extends AbstractEntityRepository<UnidirFkParentEntity> {

    public UnidirFkParentEntityRepository() {
        super(UnidirFkParentEntity.class, "UnidirParentEntities");
    }

    @MemberOrder(sequence = "2")
    public UnidirFkParentEntity newEntity(final String name) {
        final UnidirFkParentEntity entity = newTransientInstance(UnidirFkParentEntity.class);
        entity.setName(name);
        persist(entity);
        return entity;
    }

}
