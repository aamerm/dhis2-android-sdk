/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.program;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.common.meta.DbFlowOperation;
import org.hisp.dhis.client.sdk.android.flow.OrganisationUnit$Flow;
import org.hisp.dhis.client.sdk.android.flow.OrganisationUnitToProgramRelation$Flow;
import org.hisp.dhis.client.sdk.android.flow.OrganisationUnitToProgramRelation$Flow$Table;
import org.hisp.dhis.client.sdk.android.flow.Program$Flow;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.program.IProgramStore;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProgramStore extends AbsIdentifiableObjectStore<Program,
        Program$Flow> implements IProgramStore {
    private final ITransactionManager transactionManager;
    private final IMapper<OrganisationUnit, OrganisationUnit$Flow> organisationUnitMapper;

    public ProgramStore(IMapper<Program, Program$Flow> mapper,
                        ITransactionManager transactionManager, IMapper<OrganisationUnit, OrganisationUnit$Flow> organisationUnitMapper) {
        super(mapper);
        this.transactionManager = transactionManager;
        this.organisationUnitMapper = organisationUnitMapper;
    }

    @Override
    public List<Program> query(OrganisationUnit organisationUnit) {
        List<OrganisationUnitToProgramRelation$Flow> organisationUnitToProgramRelations =
                new Select().from(OrganisationUnitToProgramRelation$Flow.class).
                        where(Condition.column(OrganisationUnitToProgramRelation$Flow$Table
                                .ORGANISATIONUNIT_ORGANISATIONUNIT).is(organisationUnitMapper.
                                mapToDatabaseEntity(organisationUnit))).queryList();
        List<Program> programs = new ArrayList<>();
        for (OrganisationUnitToProgramRelation$Flow relationFlows : organisationUnitToProgramRelations) {
            programs.add(getMapper().mapToModel(relationFlows.getProgram()));
        }
        return programs;
    }

    @Override
    public List<Program> query(OrganisationUnit organisationUnit, Program.ProgramType... programTypes) {
        return null;
    }

    @Override
    public void assign(Program program, Set<OrganisationUnit> organisationUnits) {
        List<IDbOperation> operations = new ArrayList<>();
        List<OrganisationUnitToProgramRelation$Flow> relationFlows = new Select().from(OrganisationUnitToProgramRelation$Flow.class).
                where(Condition.column
                        (OrganisationUnitToProgramRelation$Flow$Table.PROGRAM_PROGRAM).
                        is(getMapper().mapToDatabaseEntity(program))).queryList();
        Map<String, OrganisationUnitToProgramRelation$Flow> relationFlowMap = new HashMap<>();
        for (OrganisationUnitToProgramRelation$Flow relationFlow : relationFlows) {
            relationFlowMap.put(relationFlow.getOrganisationUnit().getUId(), relationFlow);
        }
        for (OrganisationUnit organisationUnit : organisationUnits) {
            if (!relationFlowMap.containsValue(organisationUnit.getUId())) {
                OrganisationUnitToProgramRelation$Flow newRelationFlow = new OrganisationUnitToProgramRelation$Flow();
                newRelationFlow.setOrganisationUnit(organisationUnitMapper.mapToDatabaseEntity(organisationUnit));
                newRelationFlow.setProgram(getMapper().mapToDatabaseEntity(program));
                operations.add(DbFlowOperation.insert(newRelationFlow));
            }
        }
        transactionManager.transact(operations);
    }
}
