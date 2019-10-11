package com.wfhy.abcinterface.service;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface SignService {

	String agentSignContract(HttpServletRequest request);

	String quickAgentSignContract(HttpServletRequest request);

	String quickAgentSignConfirm(HttpServletRequest request);

	String quickAgentSignResendReq(HttpServletRequest request);

	String agentUnsignContract(HttpServletRequest request);

	String agentSignContractQuery(HttpServletRequest request);
    
}
