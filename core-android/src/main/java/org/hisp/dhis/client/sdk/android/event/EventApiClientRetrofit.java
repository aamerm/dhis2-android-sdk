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

package org.hisp.dhis.client.sdk.android.event;

import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.client.sdk.models.event.Event;

import java.util.Map;

import retrofit.Call;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

public interface EventApiClientRetrofit {

    @GET("/events?page=0")
    Call<JsonNode> getEvents(@QueryMap Map<String, String> queryParams);

    @GET("/events?paging=false&ouMode=ACCESSIBLE")
    Call<JsonNode> getEventsForEnrollment(@Query("program") String programUid,
                                          @Query("programStatus") String programStatus,
                                          @Query("trackedEntityInstance") String
                                                  trackedEntityInstanceUid,
                                          @QueryMap Map<String, String> queryParams);

    @GET("/events/{eventUid}")
    Call<Event> getEvent(@Path("eventUid") String eventUid, @QueryMap Map<String, String> queryMap);

    @POST("/events/")
    Call<Response> postEvent(@Body Event event);

    @PUT("/events/{eventUid}")
    Call<Response> putEvent(@Path("eventUid") String eventUid, @Body Event event);
}
