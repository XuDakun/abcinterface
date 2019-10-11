package com.wfhy.abcinterface.service;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface RefundService {

	String MerchantRefund(HttpServletRequest request);

	String MerchantBatchRefund(HttpServletRequest request);

	String MerchantBatchRefundQuery(HttpServletRequest request);
    
}
