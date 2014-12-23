/*
 * Licensed to Hewlett-Packard Development Company, L.P. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
package org.openscore.lang.tests.operation.actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hp.oo.sdk.content.annotations.Action;
import com.hp.oo.sdk.content.annotations.Output;
import com.hp.oo.sdk.content.annotations.Param;
import com.hp.oo.sdk.content.annotations.Response;
import com.hp.oo.sdk.content.plugin.ActionMetadata.MatchType;
import com.hp.oo.sdk.content.plugin.ActionMetadata.ResponseType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


public class OpenstackUtils {
	private static final String RETURN_RESULT_KEY = "returnResult";
	private static final String ID_KEY = "id";
	private static final String TOKEN_KEY = "token";
	private static final String TENANT_KEY = "tenant";
	private static final String PARSED_TOKEN_KEY = "parsedToken";
	private static final String PARSED_TENANT_KEY = "parsedTenant";
	private static final String ACCESS_KEY = "access";
	public static final String RETURN_CODE = "returnCode";
	public static final String SUCCESS_CODE = "0";
	public static final String FAILED_CODE = "-1";

	public static final String JSON_AUTHENTICATION_RESPONSE_KEY= "jsonAuthenticationResponse";
	private final static Logger logger = Logger.getLogger(OpenstackUtils.class);

	/**
	 * Parses authentication response to get the Tenant and Token and puts them
	 * back in the executionContext.
	 *
	 *
	 */
	@SuppressWarnings("unused")
    @Action(name = "Parse Authentication",
            outputs = {
                    @Output(PARSED_TENANT_KEY),
                    @Output(PARSED_TOKEN_KEY),
                    @Output("returnCode"),
                    @Output("returnResult")
            },
            responses = {
                    @Response(text = "success", field = "returnCode", value = "0", matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.RESOLVED),
                    @Response(text = "failure", field = "returnCode", value = "-1", matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.ERROR)
            }
    )
	public Map<String, String> parseAuthentication(@Param(JSON_AUTHENTICATION_RESPONSE_KEY)String jsonAuthenticationResponse) {

        Map<String, String> results = new HashMap<>();
        try {
            JsonElement parsedResult = new JsonParser().parse(jsonAuthenticationResponse);
            JsonObject parsedObject = parsedResult.getAsJsonObject();
            JsonObject accessObject = parsedObject.getAsJsonObject(ACCESS_KEY);
            JsonObject tokenObject = accessObject.getAsJsonObject(TOKEN_KEY);

            String resultToken = tokenObject.get(ID_KEY).toString();
            resultToken = resultToken.substring(1, resultToken.length() - 1);

            JsonObject tenantObject = tokenObject.getAsJsonObject(TENANT_KEY);
            String resultTenant = tenantObject.get(ID_KEY).toString();
            resultTenant = resultTenant.substring(1, resultTenant.length() - 1);

            results.put(PARSED_TENANT_KEY, resultTenant);
            results.put(PARSED_TOKEN_KEY, resultToken);
            results.put(RETURN_RESULT_KEY, "Parsing successful.");
            if (!(StringUtils.isEmpty(resultToken) && StringUtils.isEmpty(resultTenant))) {
                results.put(RETURN_CODE, SUCCESS_CODE);
            } else {
                results.put(RETURN_CODE, FAILED_CODE);
            }
        }catch(Exception e){

        }
        return results;
	}

}