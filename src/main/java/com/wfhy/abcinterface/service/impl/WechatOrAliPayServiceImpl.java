package com.wfhy.abcinterface.service.impl;

import com.abc.pay.client.Constants;
import com.abc.pay.client.JSON;
import com.abc.pay.client.MerchantConfig;
import com.abc.pay.client.MerchantPara;
import com.abc.pay.client.ebus.UnifiedPaymentRequest;
import com.abc.pay.client.TrxException;
import com.abc.pay.client.ebus.AgentBatchPaymentQueryRequest;
import com.abc.pay.client.ebus.AgentBatchPaymentRequest;
import com.abc.pay.client.ebus.AgentPaymentRequest;
import com.abc.pay.client.ebus.AlipayRequest;
import com.abc.pay.client.ebus.PaymentRequest;
import com.abc.pay.client.ebus.QuickPaymentRequest;
import com.wfhy.abcinterface.controller.WechatOrAliPayController;
import com.wfhy.abcinterface.service.AgentService;
import com.wfhy.abcinterface.service.PayService;
import com.wfhy.abcinterface.service.WechatOrAliPayService;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@PropertySource("classpath:log4j.properties")
public class WechatOrAliPayServiceImpl  implements WechatOrAliPayService {
	  Logger logger =Logger.getLogger(WechatOrAliPayController.class); 
	@Override
	public String wechatPayment(HttpServletRequest request) {
		UnifiedPaymentRequest upr = new UnifiedPaymentRequest();
		upr.dicRequest.put("PayTypeID", request.getParameter("PayTypeID"));
		
		upr.dicRequest.put("OrderDate", request.getParameter("OrderDate"));
		upr.dicRequest.put("OrderTime", request.getParameter("OrderTime"));
		upr.dicRequest.put("ExpiredDate", request.getParameter("ExpiredDate"));
		upr.dicRequest.put("CurrencyCode", request.getParameter("CurrencyCode"));
		upr.dicRequest.put("OrderNo", request.getParameter("OrderNo"));
		upr.dicRequest.put("OrderAmount", request.getParameter("OrderAmount"));
		upr.dicRequest.put("Fee", request.getParameter("Fee"));
		upr.dicRequest.put("AccountNo", request.getParameter("AccountNo"));
		upr.dicRequest.put("OpenID", request.getParameter("OpenID"));
		upr.dicRequest.put("ReceiverAddress", request.getParameter("ReceiverAddress"));
		upr.dicRequest.put("InstallmentMark", request.getParameter("InstallmentMark"));
		upr.dicRequest.put("InstallmentCode", request.getParameter("InstallmentCode"));
		upr.dicRequest.put("InstallmentNum", request.getParameter("InstallmentNum"));
		upr.dicRequest.put("CommodityType", request.getParameter("CommodityType"));
		upr.dicRequest.put("BuyIP", request.getParameter("BuyIP"));
		upr.dicRequest.put("OrderDesc", request.getParameter("OrderDesc"));
		upr.dicRequest.put("orderTimeoutDate", request.getParameter("orderTimeoutDate"));
		upr.dicRequest.put("LimitPay", request.getParameter("LimitPay"));
		upr.dicRequest.put("SubMerName", request.getParameter("SubMerName"));
		upr.dicRequest.put("SubMerId", request.getParameter("SubMerId"));
		upr.dicRequest.put("SubMerMCC", request.getParameter("SubMerMCC"));
		upr.dicRequest.put("SubMerchantRemarks", request.getParameter("SubMerchantRemarks"));
		upr.dicRequest.put("ProductID", request.getParameter("ProductID"));
		upr.dicRequest.put("ProductName", request.getParameter("ProductName"));
		upr.dicRequest.put("UnitPrice", request.getParameter("UnitPrice"));
		upr.dicRequest.put("Qty", request.getParameter("Qty"));
		upr.dicRequest.put("ProductRemarks", request.getParameter("ProductRemarks"));
		upr.dicRequest.put("ProductType", request.getParameter("ProductType"));
		upr.dicRequest.put("ProductDiscount", request.getParameter("ProductDiscount"));
		upr.dicRequest.put("ProductExpiredDate", request.getParameter("ProductExpiredDate"));
		upr.dicRequest.put("PaymentType", request.getParameter("PaymentType"));
		upr.dicRequest.put("PaymentLinkType", request.getParameter("PaymentLinkType"));
		upr.dicRequest.put("NotifyType", request.getParameter("NotifyType"));
		upr.dicRequest.put("ResultNotifyURL", request.getParameter("ResultNotifyURL"));
		
		upr.dicRequest.put("MerchantRemarks", request.getParameter("MerchantRemarks"));
		upr.dicRequest.put("H5SceneType", request.getParameter("H5SceneType"));
		upr.dicRequest.put("H5SceneUrl", request.getParameter("H5SceneUrl"));
		upr.dicRequest.put("H5SceneName", request.getParameter("H5SceneName"));
		upr.dicRequest.put("IsBreakAccount", request.getParameter("IsBreakAccount"));
		upr.dicRequest.put("SplitMerchantID", request.getParameter("SplitMerchantID"));
		upr.dicRequest.put("SplitAmount", request.getParameter("SplitAmount"));
		logger.info("wechatPayment向 农行 发送请求 参数:"+upr.toString());
		JSON returnjson = upr.postRequest();
		 JSONObject resultjb = new JSONObject();
	/*	if("0000".equals(returnjson.GetKeyValue("ReturnCode"))) {
			
		}else {
			
		*/
		 try {
			resultjb.put("ReturnCode",returnjson.GetKeyValue("ReturnCode"));
			resultjb.put("ErrorMessage",returnjson.GetKeyValue("ErrorMessage"));
			resultjb.put("PaymentURL",returnjson.GetKeyValue("PaymentURL"));
			resultjb.put("QRURL",returnjson.GetKeyValue("QRURL"));
			resultjb.put("MerchantID",returnjson.GetKeyValue("MerchantID"));
			resultjb.put("TrxType",returnjson.GetKeyValue("TrxType"));
			resultjb.put("OrderNo",returnjson.GetKeyValue("OrderNo"));
			resultjb.put("OrderAmount",returnjson.GetKeyValue("OrderAmount"));
			resultjb.put("HostDate",returnjson.GetKeyValue("HostDate"));
			resultjb.put("HostTime",returnjson.GetKeyValue("HostTime"));
			resultjb.put("PrePayID",returnjson.GetKeyValue("PrePayID"));
			resultjb.put("ThirdOrderNo",returnjson.GetKeyValue("ThirdOrderNo"));
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}finally {
			logger.info("wechatPayment返回参数:"+resultjb.toString());
			return resultjb.toString();
		}
	
	}

	@Override
	public String aliPayment(HttpServletRequest request) {
		AlipayRequest upr = new AlipayRequest();
		upr.dicRequest.put("PayTypeID", request.getParameter("PayTypeID"));
		
		upr.dicRequest.put("OrderDate", request.getParameter("OrderDate"));
		upr.dicRequest.put("OrderTime", request.getParameter("OrderTime"));
		upr.dicRequest.put("ExpiredDate", request.getParameter("ExpiredDate"));
		upr.dicRequest.put("PAYED_RETURN_URL", request.getParameter("PAYED_RETURN_URL"));
		
		upr.dicRequest.put("CurrencyCode", request.getParameter("CurrencyCode"));
		upr.dicRequest.put("OrderNo", request.getParameter("OrderNo"));
		upr.dicRequest.put("OrderAmount", request.getParameter("OrderAmount"));
		upr.dicRequest.put("Fee", request.getParameter("Fee"));
		upr.dicRequest.put("AccountNo", request.getParameter("AccountNo"));
		upr.dicRequest.put("ReceiverAddress", request.getParameter("ReceiverAddress"));
		upr.dicRequest.put("InstallmentMark", request.getParameter("InstallmentMark"));
		upr.dicRequest.put("InstallmentCode", request.getParameter("InstallmentCode"));
		upr.dicRequest.put("InstallmentNum", request.getParameter("InstallmentNum"));
		upr.dicRequest.put("CommodityType", request.getParameter("CommodityType"));
		upr.dicRequest.put("BuyIP", request.getParameter("BuyIP"));
		upr.dicRequest.put("OrderDesc", request.getParameter("OrderDesc"));
		upr.dicRequest.put("orderTimeoutDate", request.getParameter("orderTimeoutDate"));
		upr.dicRequest.put("WapQuitUrl", request.getParameter("WapQuitUrl"));
		upr.dicRequest.put("PcQrPayMode", request.getParameter("PcQrPayMode"));
		upr.dicRequest.put("PcQrCodeWidth", request.getParameter("PcQrCodeWidth"));
		upr.dicRequest.put("TimeoutExpress", request.getParameter("TimeoutExpress"));
		upr.dicRequest.put("ChildMerchantNo", request.getParameter("ChildMerchantNo"));
		upr.dicRequest.put("LimitPay", request.getParameter("LimitPay"));
		upr.dicRequest.put("SubMerName", request.getParameter("SubMerName"));
		upr.dicRequest.put("SubMerId", request.getParameter("SubMerId"));
		upr.dicRequest.put("SubMerMCC", request.getParameter("SubMerMCC"));
		upr.dicRequest.put("SubMerchantRemarks", request.getParameter("SubMerchantRemarks"));
		upr.dicRequest.put("ProductID", request.getParameter("ProductID"));
		upr.dicRequest.put("ProductName", request.getParameter("ProductName"));
		upr.dicRequest.put("UnitPrice", request.getParameter("UnitPrice"));
		upr.dicRequest.put("Qty", request.getParameter("Qty"));
		upr.dicRequest.put("ProductRemarks", request.getParameter("ProductRemarks"));
		upr.dicRequest.put("ProductType", request.getParameter("ProductType"));
		upr.dicRequest.put("ProductDiscount", request.getParameter("ProductDiscount"));
		upr.dicRequest.put("ProductExpiredDate", request.getParameter("ProductExpiredDate"));
		upr.dicRequest.put("PaymentType", request.getParameter("PaymentType"));
		upr.dicRequest.put("PaymentLinkType", request.getParameter("PaymentLinkType"));
		upr.dicRequest.put("NotifyType", request.getParameter("NotifyType"));
		upr.dicRequest.put("ResultNotifyURL", request.getParameter("ResultNotifyURL"));
		
		upr.dicRequest.put("MerchantRemarks", request.getParameter("MerchantRemarks"));
		upr.dicRequest.put("IsBreakAccount", request.getParameter("IsBreakAccount"));
		
		upr.dicRequest.put("ChildMerchantNo", request.getParameter("ChildMerchantNo"));
		upr.dicRequest.put("SplitMerchantID", request.getParameter("SplitMerchantID"));
		upr.dicRequest.put("SplitAmount", request.getParameter("SplitAmount"));

		logger.info("aliPayment向 农行 请求参数:"+upr.toString());
		JSON returnjson = upr.postRequest();
		 JSONObject resultjb = new JSONObject();
	/*	if("0000".equals(returnjson.GetKeyValue("ReturnCode"))) {
			
		}else {
			
		*/
		 try {
			resultjb.put("ReturnCode",returnjson.GetKeyValue("ReturnCode"));
			resultjb.put("ErrorMessage",returnjson.GetKeyValue("ErrorMessage"));
			resultjb.put("MerchantID",returnjson.GetKeyValue("MerchantID"));
			resultjb.put("TrxType",returnjson.GetKeyValue("TrxType"));
			resultjb.put("OrderNo",returnjson.GetKeyValue("OrderNo"));
			resultjb.put("OrderAmount",returnjson.GetKeyValue("OrderAmount"));
			resultjb.put("HostDate",returnjson.GetKeyValue("HostDate"));
			resultjb.put("HostTime",returnjson.GetKeyValue("HostTime"));
			resultjb.put("ThirdOrderNo",returnjson.GetKeyValue("ThirdOrderNo"));
			resultjb.put("QRURL",returnjson.GetKeyValue("QRURL"));
			resultjb.put("PrePayID",returnjson.GetKeyValue("PrePayID"));
			resultjb.put("ThirdReOrderNo",returnjson.GetKeyValue("ThirdReOrderNo"));
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}finally {
			logger.info("aliPayment返回参数:"+resultjb.toString());
			return resultjb.toString();
		}
	
	}
	
}

