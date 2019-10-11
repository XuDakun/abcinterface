package com.wfhy.abcinterface.service;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface PayService {
    String merchantPayment(HttpServletRequest request) ;

    String merchantPaymentIE(HttpServletRequest request);

	String merchantQuickPaymentReq(HttpServletRequest request);

	String merchantQueryOrder(HttpServletRequest request);

	String merchantQueryTrnxRecords(HttpServletRequest request);

	String merchantTrxSettleQuery(HttpServletRequest request);

	String merchantTrxSettlePlatForm(HttpServletRequest request);

	String identityVerify(HttpServletRequest request);

	String staticIdentityVerify(HttpServletRequest request);

	String submitPreAuthPayment(HttpServletRequest request);

	String merchantGetReceipt(HttpServletRequest request);

	String authenMerchantQuery(HttpServletRequest request);

	String transferOutQuery(HttpServletRequest request);
}
