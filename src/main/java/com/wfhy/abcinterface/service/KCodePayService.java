package com.wfhy.abcinterface.service;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface KCodePayService {
   

	String merchantQuickPaymentReq(HttpServletRequest request);

	String merchantQuickPaymentSend(HttpServletRequest request);

	String MerchantQuickPaymentResend(HttpServletRequest request);
}
