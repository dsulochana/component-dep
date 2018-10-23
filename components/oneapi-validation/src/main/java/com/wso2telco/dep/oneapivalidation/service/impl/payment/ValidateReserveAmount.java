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
package com.wso2telco.dep.oneapivalidation.service.impl.payment;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.wso2telco.dep.oneapivalidation.exceptions.CustomException;
import com.wso2telco.dep.oneapivalidation.service.IServiceValidate;
import com.wso2telco.dep.oneapivalidation.util.UrlValidator;
import com.wso2telco.dep.oneapivalidation.util.Validation;
import com.wso2telco.dep.oneapivalidation.util.ValidationRule;

 
// TODO: Auto-generated Javadoc
/**
 * The Class ValidateReserveAmount.
 */
public class ValidateReserveAmount implements IServiceValidate {

	private static Log logger = LogFactory.getLog(ValidateReserveAmount.class);
    /** The validation rules. */
    private final String[] validationRules = {"*", "transactions", "amountReservation"};

    /* (non-Javadoc)
     * @see com.wso2telco.oneapivalidation.service.IServiceValidate#validate(java.lang.String)
     */
    public void validate(String json) throws CustomException {
        String clientCorrelator = null;
        String endUserId = null;
        Double amount = null;
        String currency = null;
        String description = null;
        Double totalAmountCharged = null;
        Double amountReserved = null;
        String onBehalfOf = null;
        String purchaseCategoryCode = null;
        String channel = null;
        Double taxAmount = null;
        String referenceCode = null;
        int referenceSequence = 0;
        String transactionOperationStatus = null;

        try {
            JSONObject objJSONObject = new JSONObject(json);
            if(!objJSONObject.isNull("amountReservationTransaction")) {
            	
                JSONObject objAmountReservationTransaction = (JSONObject) objJSONObject.get("amountReservationTransaction");
                if (!objAmountReservationTransaction.isNull("clientCorrelator")) {
                    clientCorrelator = nullOrTrimmed(objAmountReservationTransaction.get("clientCorrelator").toString());
                }
                if (!objAmountReservationTransaction.isNull("endUserId")) {
                    endUserId = nullOrTrimmed(objAmountReservationTransaction.get("endUserId").toString());
                }
                if (!objAmountReservationTransaction.isNull("referenceCode")) {
                    referenceCode = nullOrTrimmed(objAmountReservationTransaction.get("referenceCode").toString());
                }
                if (!objAmountReservationTransaction.isNull("transactionOperationStatus")) {
                    transactionOperationStatus = nullOrTrimmed(objAmountReservationTransaction.get("transactionOperationStatus").toString());
                }
                if (!objAmountReservationTransaction.isNull("referenceSequence")) {
                    referenceSequence = Integer.parseInt(nullOrTrimmed(objAmountReservationTransaction.get("referenceSequence").toString()));
                }
                
                if (!objAmountReservationTransaction.isNull("paymentAmount")) {
                	JSONObject objPaymentAmount = (JSONObject) objAmountReservationTransaction.get("paymentAmount");
                    if (!objPaymentAmount.isNull("totalAmountCharged")) {
                        totalAmountCharged = Double.parseDouble(nullOrTrimmed(objPaymentAmount.get("totalAmountCharged").toString()));
                    }
                    if (!objPaymentAmount.isNull("amountReserved")) {
                        amountReserved = Double.parseDouble(nullOrTrimmed(objPaymentAmount.get("amountReserved").toString()));
                    }
                    if (!objPaymentAmount.isNull("chargingInformation")) {
                    	JSONObject objChargingInformation = (JSONObject) objPaymentAmount.get("chargingInformation");

                        if (!objChargingInformation.isNull("amount")) {
                            amount = Double.parseDouble(nullOrTrimmed(objChargingInformation.get("amount").toString()));
                        }
                        if (!objChargingInformation.isNull("currency")) {
                            currency = nullOrTrimmed(objChargingInformation.get("currency").toString());
                        }
                        if (!objChargingInformation.isNull("description")) {
                            description = nullOrTrimmed(objChargingInformation.get("description").toString());
                        }
                    } else {
                    	logger.error("Missing mandatory parameter: chargingInformation");
                    	throw new CustomException("SVC0002", "Invalid input value for message part %1", new String[]{"Missing mandatory parameter: chargingInformation"});
                    }
                    
                    if (!objPaymentAmount.isNull("chargingMetaData")) {
                    	JSONObject objChargingMetaData = (JSONObject) objPaymentAmount.get("chargingMetaData");

                        if (!objChargingMetaData.isNull("onBehalfOf")) {
                            onBehalfOf = nullOrTrimmed(objChargingMetaData.get("onBehalfOf").toString());
                        }
                        if (!objChargingMetaData.isNull("purchaseCategoryCode")) {
                            purchaseCategoryCode = nullOrTrimmed(objChargingMetaData.get("purchaseCategoryCode").toString());
                        }
                        if (!objChargingMetaData.isNull("channel")) {
                            channel = nullOrTrimmed(objChargingMetaData.get("channel").toString());
                        }
                        if (!objChargingMetaData.isNull("taxAmount")) {
                            taxAmount = Double.parseDouble(nullOrTrimmed(objChargingMetaData.get("taxAmount").toString()));
                        }
                    }
                    
                } else {
                	logger.error("Missing mandatory parameter: paymentAmount");
                	throw new CustomException("SVC0002", "Invalid input value for message part %1", new String[]{"Missing mandatory parameter: paymentAmount"});
                }
                
            } else {
            	logger.error("Missing mandatory parameter: amountReservationTransaction");
            	throw new CustomException("SVC0002", "Invalid input value for message part %1", new String[]{"Missing mandatory parameter: amountReservationTransaction"});
            }

        } catch (CustomException e) {
			throw e;
		} catch (Exception e) {
        	logger.error("Manipulating recived JSON Object: " + e);
            throw new CustomException("POL0299", "Unexpected Error", new String[]{""});
        }

        ValidationRule[] rules = null;
        rules = new ValidationRule[]{
            new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY_TEL_END_USER_ID, "endUserId", endUserId),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY, "referenceCode", referenceCode),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY, "transactionOperationStatus", transactionOperationStatus, "reserved"),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL, "referenceSequence", Integer.toString(referenceSequence)),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY, "description", description),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY_CURRENCY, "currency", currency),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY_DOUBLE_GT_ZERO, "amount", amount),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_MANDATORY, "clientCorrelator", clientCorrelator),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL, "onBehalfOf", onBehalfOf),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL, "purchaseCategoryCode", purchaseCategoryCode),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL_PAYMENT_CHANNEL, "channel", channel),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL_DOUBLE_GE_ZERO, "taxAmount", taxAmount),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL_DOUBLE_GE_ZERO, "totalAmountCharged", totalAmountCharged),
            new ValidationRule(ValidationRule.VALIDATION_TYPE_OPTIONAL_DOUBLE_GE_ZERO, "amountReserved", amountReserved)};

        Validation.checkRequestParams(rules);
    }

    /* (non-Javadoc)
     * @see com.wso2telco.oneapivalidation.service.IServiceValidate#validateUrl(java.lang.String)
     */
    public void validateUrl(String pathInfo) throws CustomException {
        String[] requestParts = null;
        if (pathInfo != null) {
            if (pathInfo.startsWith("/")) {
                pathInfo = pathInfo.substring(1);
            }
            requestParts = pathInfo.split("/");
        }

        UrlValidator.validateRequest(requestParts, validationRules);
    }

    /* (non-Javadoc)
     * @see com.wso2telco.oneapivalidation.service.IServiceValidate#validate(java.lang.String[])
     */
    public void validate(String[] params) throws CustomException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Null or trimmed.
     *
     * @param s the s
     * @return the string
     */
    private static String nullOrTrimmed(String s) {
        String rv = null;
        if (s != null && s.trim().length() > 0) {
            rv = s.trim();
        }
        return rv;
    }
}
