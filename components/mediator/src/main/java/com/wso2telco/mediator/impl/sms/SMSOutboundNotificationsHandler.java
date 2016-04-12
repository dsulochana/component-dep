/*******************************************************************************
 * Copyright  (c) 2015-2016, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
 * 
 * WSO2.Telco Inc. licences this file to you under  the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.mediator.impl.sms;


import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wso2telco.datapublisher.DataPublisherConstants;
import com.wso2telco.dbutils.AxiataDbService;
import com.wso2telco.mediator.OperatorEndpoint;
import com.wso2telco.mediator.entity.OutboundRequest;
import com.wso2telco.mediator.entity.OutboundRequestOp;
import com.wso2telco.mediator.internal.Type;
import com.wso2telco.mediator.internal.UID;
import com.wso2telco.mediator.internal.Util;
import com.wso2telco.mnc.resolver.MNCQueryClient;
import com.wso2telco.oneapivalidation.exceptions.CustomException;

import java.util.HashMap;

import org.wso2.carbon.apimgt.gateway.APIMgtGatewayConstants;


// TODO: Auto-generated Javadoc
/**
 * The Class SMSOutboundNotificationsHandler.
 */
public class SMSOutboundNotificationsHandler implements SMSHandler {

    /** The dbservice. */
    private AxiataDbService dbservice;
    
    /** The executor. */
    private SMSExecutor executor;
    
    /** The mnc queryclient. */
    MNCQueryClient mncQueryclient = null;

    /**
     * Instantiates a new SMS outbound notifications handler.
     *
     * @param executor the executor
     */
    public SMSOutboundNotificationsHandler(SMSExecutor executor) {
        this.executor = executor;
        dbservice = new AxiataDbService();
        mncQueryclient = new MNCQueryClient();
    }

    /* (non-Javadoc)
     * @see com.wso2telco.mediator.impl.sms.SMSHandler#handle(org.apache.synapse.MessageContext)
     */
    @Override
    public boolean handle(MessageContext context) throws CustomException, AxisFault, Exception {

        String requestid = UID.getUniqueID(Type.ALERTINBOUND.getCode(), context, executor.getApplicationid());

        String requestPath = executor.getSubResourcePath();
        String axiataid = requestPath.substring(requestPath.lastIndexOf("/") + 1);
        
        HashMap<String, String> dnSubscriptionDetails = dbservice.subscriptionDNNotifiMap(Integer.valueOf(axiataid));
        String notifyurl = dnSubscriptionDetails.get("notifyurl");
        String serviceProvider = dnSubscriptionDetails.get("serviceProvider");
        
        String notifyurlRoute = notifyurl;        
        Util.getPropertyFile();
        String requestRouterUrl = Util.getApplicationProperty("requestRouterUrl");
        if (requestRouterUrl != null) {
            notifyurlRoute = requestRouterUrl + notifyurlRoute;
        }

        String formattedString = null;
        String mcc = null;
        String operatormar ="+";

                //String[] params = executor.getSubResourcePath().split("/");
        Gson gson = new GsonBuilder().serializeNulls().create();
        if (executor.getJsonBody().toString().contains("operatorCode")) {
            
            OutboundRequestOp outboundRequestOp = gson.fromJson(executor.getJsonBody().toString(), OutboundRequestOp.class);
            formattedString = gson.toJson(outboundRequestOp);
            String[] params = outboundRequestOp.getDeliveryInfoNotification().getDeliveryInfo().getAddress().split(":");
            String  operator = mncQueryclient.QueryNetwork(mcc,params[1]);
            context.setProperty(DataPublisherConstants.MSISDN,params[1]);
            context.setProperty(DataPublisherConstants.OPERATOR_ID,operator);
            context.setProperty(APIMgtGatewayConstants.USER_ID, serviceProvider);
        } else {
            
            OutboundRequest outboundRequest = gson.fromJson(executor.getJsonBody().toString(), OutboundRequest.class);
            formattedString = gson.toJson(outboundRequest);
            String[] params = outboundRequest.getDeliveryInfoNotification().getDeliveryInfo().getAddress().split(":");
            String  operator = mncQueryclient.QueryNetwork(mcc,params[1]);
            context.setProperty(DataPublisherConstants.MSISDN,params[1]);
            context.setProperty(DataPublisherConstants.OPERATOR_ID,operator);
            context.setProperty(APIMgtGatewayConstants.USER_ID, serviceProvider);
        }



        int notifyret = executor.makeNorthBoundRequest(new OperatorEndpoint(new EndpointReference(notifyurl), null), notifyurlRoute, formattedString, true, context, false);

        executor.removeHeaders(context);
        
        if (notifyret == 0) {
            throw new CustomException("SVC1000", "", new String[]{null});
        }
        
        ((Axis2MessageContext) context).getAxis2MessageContext().setProperty("HTTP_SC", 200);
         

        return true;
    }

    /* (non-Javadoc)
     * @see com.wso2telco.mediator.impl.sms.SMSHandler#validate(java.lang.String, java.lang.String, org.json.JSONObject, org.apache.synapse.MessageContext)
     */
    @Override
    public boolean validate(String httpMethod, String requestPath, JSONObject jsonBody, MessageContext context) throws Exception {
        context.setProperty("mife.prop.operationType", 207);
        return true;
    }
}
