package com.wfhy.abcinterface.service.impl;

import com.abc.pay.client.Constants;
import com.abc.pay.client.JSON;
import com.abc.pay.client.MerchantConfig;
import com.abc.pay.client.MerchantPara;
import com.abc.pay.client.TrxException;
import com.abc.pay.client.ebus.AgentSignContractRequest;
import com.abc.pay.client.ebus.AgentUnSignRequest;
import com.abc.pay.client.ebus.PaymentRequest;
import com.abc.pay.client.ebus.QueryAgentSignRequest;
import com.abc.pay.client.ebus.QuickAgentSignConfirm;
import com.abc.pay.client.ebus.QuickAgentSignContractRequest;
import com.abc.pay.client.ebus.QuickAgentSignResendReq;
import com.abc.pay.client.ebus.QuickPaymentRequest;
import com.wfhy.abcinterface.service.PayService;
import com.wfhy.abcinterface.service.SignService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.LinkedHashMap;

@Service
public class SignServiceImpl  implements SignService {
/**
 * 返回的URL需要重新请求
 */
	@Override
	public String agentSignContract(HttpServletRequest request) {
		//1、生成授权支付签约请求对象
        AgentSignContractRequest tRequest = new AgentSignContractRequest();
        tRequest.agentSignRequest.put("CertificateNo", request.getParameter("txtCertificateNo"));;             //证件号码       （必要信息）
        tRequest.agentSignRequest.put("CertificateType", request.getParameter("ddlCertificateType"));//证件类型       （必要信息）农行卡类型
        tRequest.agentSignRequest.put("NotifyType", request.getParameter("txtNotifyType"));                 //通知类型 （必要信息）
        tRequest.agentSignRequest.put("ResultNotifyURL", request.getParameter("txtResultNotifyURL"));         //通知地址（必要信息）
        tRequest.agentSignRequest.put("OrderNo", request.getParameter("txtOrderNo"));                         //订单编号（必要信息）
        tRequest.agentSignRequest.put("PaymentLinkType", request.getParameter("txtPaymentLinkType"));                 //接入渠道 （必要信息）
        tRequest.agentSignRequest.put("MerCustomNo", request.getParameter("txtMerCustomNo"));                 //客户编号        
        tRequest.agentSignRequest.put("CardType", request.getParameter("txtCardType"));                         //农行卡类型 （必要信息）
        tRequest.agentSignRequest.put("RequestDate", request.getParameter("txtRequestDate"));                 //验证请求日期 （必要信息 - YYYY/MM/DD）
        tRequest.agentSignRequest.put("RequestTime", request.getParameter("txtRequestTime"));                 //验证请求时间 （必要信息 - HH:MM:SS）
        tRequest.agentSignRequest.put("InvaidDate", request.getParameter("txtInvaidDate"));                 //签约有效期 （必要信息）
        tRequest.agentSignRequest.put("IsSign", request.getParameter("txtIsSign"));                 //签约标识 （必要信息）

        //2、传送授权支付签约请求并取得签约网址
        JSON json = tRequest.postRequest();
        String ReturnCode = json.GetKeyValue("ReturnCode");
        String ErrorMessage = json.GetKeyValue("ErrorMessage");
        JSONObject returnjb = new JSONObject();
        try {
        if (ReturnCode.equals("0000"))
        {
        	
				returnjb.put("ReturnCode", ReturnCode);
			
        	returnjb.put("ErrorMessage", ErrorMessage);
        	returnjb.put("OrderNo", json.GetKeyValue("OrderNo"));
        	returnjb.put("TrxType", json.GetKeyValue("TrxType"));
            
            //3、授权支付签约请求提交成功，将客户端导向签约页面
        	returnjb.put("B2CAgentSignContractURL",json.GetKeyValue("B2CAgentSignContractURL"));
        }
        else
        {
            //4、授权支付签约请求提交失败，商户自定后续动作
        	returnjb.put("ReturnCode", ReturnCode);
        	returnjb.put("ErrorMessage", ErrorMessage);
        }
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			return returnjb.toString();
		}
	}

@Override
public String quickAgentSignContract(HttpServletRequest request) {
	 //1、生成授权支付签约请求对象
    QuickAgentSignContractRequest tRequest = new QuickAgentSignContractRequest();
    tRequest.dicRequest.put("OrderDate", request.getParameter("txtRequestDate"));            //请求日期 （必要信息 - YYYY/MM/DD）
    tRequest.dicRequest.put("OrderTime", request.getParameter("txtRequestTime"));            //请求时间 （必要信息 - HH:MM:SS）
    tRequest.dicRequest.put("OrderNo", request.getParameter("txtOrderNo"));                  //订单编号（必要信息）
    tRequest.dicRequest.put("PaymentLinkType", request.getParameter("txtPaymentLinkType"));  //接入渠道 （必要信息）
    tRequest.dicRequest.put("MerCustomNo", request.getParameter("txtMerCustomNo"));          //客户编号
    tRequest.dicRequest.put("AgentSignNo", request.getParameter("txtAgentSignNo"));          //签约编号
    tRequest.dicRequest.put("CardNo", request.getParameter("txtCardNo"));                    //签约账号  （必要信息）
    tRequest.dicRequest.put("CardType", request.getParameter("txtCardType"));                //农行卡类型（必要信息）
    tRequest.dicRequest.put("MobileNo", request.getParameter("txtMobileNo"));                //签约手机号（必要信息）
    tRequest.dicRequest.put("InvaidDate", request.getParameter("txtInvaidDate"));            //签约有效期（必要信息）
    tRequest.dicRequest.put("IsSign", request.getParameter("txtIsSign"));                    //签约/解约标识 （必要信息）
	tRequest.dicRequest.put("CertificateType", request.getParameter("txtCertificateType"));  //证件类型（必要信息）
	tRequest.dicRequest.put("CertificateNo", request.getParameter("txtCertificateNo")); 	 //证件号码（必要信息）
	tRequest.dicRequest.put("AccName", request.getParameter("txtAccName"));                  //客户姓名（必要信息）
	tRequest.dicRequest.put("CVV2", request.getParameter("txtCVV2"));                        //贷记卡CVV2码（农行卡类型为贷记卡时必输）
	tRequest.dicRequest.put("CardDueDate", request.getParameter("txtCardDueDate"));          //贷记卡到期日（农行卡类型为贷记卡时必输）

    //2、传送授权支付签约请求
    JSON json = tRequest.postRequest();
    String ReturnCode = json.GetKeyValue("ReturnCode");
    String ErrorMessage = json.GetKeyValue("ErrorMessage");
    JSONObject returnjb = new JSONObject();
	try {
    if (ReturnCode.equals("0000"))
    {
        //3、授权支付签约请求提交成功，获取返回信息
    
			returnjb.put("ReturnCode", ReturnCode);
		
    	returnjb.put("ErrorMessage", ErrorMessage);
    	returnjb.put("TrxType", json.GetKeyValue("TrxType"));
    	returnjb.put("OrderNo", json.GetKeyValue("OrderNo"));
    }
    else
    {
        //4、授权支付签约请求提交失败，商户自定后续动作
    	returnjb.put("ReturnCode", ReturnCode);
    	returnjb.put("ErrorMessage", ErrorMessage);
    }
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}

@Override
public String quickAgentSignConfirm(HttpServletRequest request) {
	//1、生成授权支付签约确认请求对象
    QuickAgentSignConfirm tRequest = new QuickAgentSignConfirm();
    tRequest.dicRequest.put("OrderNo", request.getParameter("txtOrderNo"));           //订单编号（必要信息）
    tRequest.dicRequest.put("VerifyCode", request.getParameter("txtVerifyCode"));     //验证码（必要信息）

    //2、传送授权支付签约确认请求
    JSON json = tRequest.postRequest();
    String ReturnCode = json.GetKeyValue("ReturnCode");
    String ErrorMessage = json.GetKeyValue("ErrorMessage");
    JSONObject returnjb = new JSONObject();
    try {
    if (ReturnCode.equals("0000"))
    {
        //3、授权支付签约确认请求提交成功
    	
			returnjb.put("ReturnCode", ReturnCode);
		
    	returnjb.put("ErrorMessage", ErrorMessage);
    	returnjb.put("TrxType",  json.GetKeyValue("TrxType"));
    	returnjb.put("OrderNo",  json.GetKeyValue("OrderNo"));
    	returnjb.put("AgentSignNo",  json.GetKeyValue("AgentSignNo"));
    }
    else
    {
        //4、授权支付签约请求提交失败，商户自定后续动作
    	returnjb.put("ReturnCode", ReturnCode);
    	returnjb.put("ErrorMessage", ErrorMessage);
    }
    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}

@Override
public String quickAgentSignResendReq(HttpServletRequest request) {
	//1、生成授权支付签约验证码重发请求对象
    QuickAgentSignResendReq tRequest = new QuickAgentSignResendReq();
    tRequest.dicRequest.put("OrderNo", request.getParameter("txtOrderNo"));                         //订单编号（必要信息）
    tRequest.dicRequest.put("CardNo", request.getParameter("txtCardNo"));                           //签约账号       （必要信息）

    //2、传送授权支付签约验证码重发请求
    JSON json = tRequest.postRequest();
    String ReturnCode = json.GetKeyValue("ReturnCode");
    String ErrorMessage = json.GetKeyValue("ErrorMessage");
    JSONObject returnjb = new JSONObject();
    try {
    if (ReturnCode.equals("0000"))
    {
        //3、授权支付签约验证码重发请求提交成功，获取返回信息
    	
    	returnjb.put("ReturnCode", ReturnCode);
    	
			returnjb.put("ErrorMessage", ErrorMessage);
		
    	returnjb.put("TrxType",  json.GetKeyValue("TrxType"));
    	returnjb.put("OrderNo",  json.GetKeyValue("OrderNo"));
    	returnjb.put("CardNo",  json.GetKeyValue("CardNo"));
       
    }
    else
    {
        //4、授权支付签约请求验证码重发提交失败，商户自定后续动作
    	returnjb.put("ReturnCode", ReturnCode);
    	returnjb.put("ErrorMessage", ErrorMessage);
    }
    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}

@Override
public String agentUnsignContract(HttpServletRequest request) {
	//1、生成授权支付解约请求对象
    AgentUnSignRequest tRequest = new AgentUnSignRequest();
    tRequest.dicRequest.put("OrderNo", request.getParameter("OrderNo"));                         //订单编号（必要信息）
    tRequest.dicRequest.put("AgentSignNo", request.getParameter("AgentSignNo"));                 //签约编号（必要信息）
    tRequest.dicRequest.put("RequestDate", request.getParameter("RequestDate"));                 //请求日期 （必要信息 - YYYY/MM/DD）
    tRequest.dicRequest.put("RequestTime", request.getParameter("RequestTime"));                 //请求时间 （必要信息 - HH:MM:SS）

    //3、传送授权支付解约请求并取得签约网址
    JSON json = tRequest.postRequest();
    String ReturnCode = json.GetKeyValue("ReturnCode");
    String ErrorMessage = json.GetKeyValue("ErrorMessage");
    JSONObject returnjb = new JSONObject();
	try {
    if (ReturnCode.equals("0000"))
    {
    
			returnjb.put("ReturnCode", ReturnCode);
		
    	returnjb.put("ErrorMessage", ErrorMessage);
    	returnjb.put("TrxType", json.GetKeyValue("TrxType"));
    	returnjb.put("OrderNo", json.GetKeyValue("OrderNo"));
    }
    else
    {
    	returnjb.put("ReturnCode", ReturnCode);
    	returnjb.put("ErrorMessage", ErrorMessage);
    }
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
	}

@Override
public String agentSignContractQuery(HttpServletRequest request) {
	//1、取得商户委托扣款签约查询所需要的信息
	String tAgentSignNo   = request.getParameter("AgentSignNo"  );

	//2、生成商户委托扣款签约查询请求对象
	QueryAgentSignRequest tRequest = new QueryAgentSignRequest();
	tRequest.dicRequest.put("OrderNo", request.getParameter("OrderNo"));  //签约编号           （必要信息）

	//3、传送商户委托扣款签约查询请求并取得订单查询结果
	JSON json = tRequest.postRequest();
	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");
	JSONObject returnjb = new JSONObject();
	try {
	if (ReturnCode.equals("0000"))
	{
		//4、获取结果信息
		returnjb.put("ReturnCode", ReturnCode);
		
			returnjb.put("ErrorMessage", ErrorMessage);
		
		returnjb.put("TrxType", json.GetKeyValue("TrxType"));
		returnjb.put("MerchantNo", json.GetKeyValue("MerchantNo"));
		returnjb.put("OrderNo", json.GetKeyValue("OrderNo"));
		returnjb.put("AgentSignNo", json.GetKeyValue("AgentSignNo"));
		returnjb.put("CertificateNo", json.GetKeyValue("CertificateNo"));
		returnjb.put("CertificateType", json.GetKeyValue("CertificateType"));
		returnjb.put("Last4CardNo", json.GetKeyValue("Last4CardNo"));
		returnjb.put("SignDate", json.GetKeyValue("SignDate"));
		returnjb.put("UnSignDate", json.GetKeyValue("UnSignDate"));
		returnjb.put("AgentSignStatus", json.GetKeyValue("AgentSignStatus"));
		returnjb.put("AccountType", json.GetKeyValue("AccountType"));
		returnjb.put("PaymentLinkType", json.GetKeyValue("PaymentLinkType"));
		returnjb.put("InvaidDate", json.GetKeyValue("InvaidDate"));
	}
	else
	{
		returnjb.put("ReturnCode", ReturnCode);
		returnjb.put("ErrorMessage", ErrorMessage);
	}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}
}
