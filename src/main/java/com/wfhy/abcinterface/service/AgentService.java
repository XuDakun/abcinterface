package com.wfhy.abcinterface.service;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface AgentService {

	String agentPayment(HttpServletRequest request);

	String agentBatchPayment(HttpServletRequest request);

	String agentBatchPaymentQuery(HttpServletRequest request);

}
