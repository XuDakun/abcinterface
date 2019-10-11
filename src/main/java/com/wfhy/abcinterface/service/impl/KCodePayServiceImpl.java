package com.wfhy.abcinterface.service.impl;

import com.abc.pay.client.Constants;
import com.abc.pay.client.JSON;
import com.abc.pay.client.MerchantConfig;
import com.abc.pay.client.MerchantPara;
import com.abc.pay.client.TrxException;
import com.abc.pay.client.ebus.PaymentRequest;
import com.abc.pay.client.ebus.QuickPaymentReSend;
import com.abc.pay.client.ebus.QuickPaymentRequest;
import com.abc.pay.client.ebus.QuickPaymentSend;

import com.wfhy.abcinterface.service.KCodePayService;
import com.wfhy.abcinterface.service.PayService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class KCodePayServiceImpl  implements KCodePayService {

/**
 * 网上K码支付支付请求
 */
	@Override
	public String merchantQuickPaymentReq(HttpServletRequest request) {
		//1、生成支付请求对象
        QuickPaymentRequest tPaymentRequest = new QuickPaymentRequest();

        //1、生成定单订单对象，并将订单明细加入定单中
        tPaymentRequest.dicOrder.put("PayTypeID", request.getParameter("txtPayTypeID"));    //设定交易类型
        tPaymentRequest.dicOrder.put("orderTimeoutDate", request.getParameter("txtorderTimeoutDate"));                     //设定订单有效期
        tPaymentRequest.dicOrder.put("OrderNo", request.getParameter("txtPaymentRequestNo"));                       //设定订单编号 （必要信息）
        tPaymentRequest.dicOrder.put("CurrencyCode", request.getParameter("txtCurrencyCode"));    //设定交易币种
        tPaymentRequest.dicOrder.put("OrderAmount", request.getParameter("txtPaymentRequestAmount"));    //设定交易金额

        tPaymentRequest.dicOrder.put("ExpiredDate", request.getParameter("txtExpiredDate"));//设定订单保存时间
        tPaymentRequest.dicOrder.put("OrderDesc", request.getParameter("txtOrderDesc"));                   //设定订单说明
        tPaymentRequest.dicOrder.put("OrderDate", request.getParameter("txtOrderDate"));                   //设定订单日期 （必要信息 - YYYY/MM/DD）
        tPaymentRequest.dicOrder.put("OrderTime", request.getParameter("txtOrderTime"));                   //设定订单时间 （必要信息 - HH:MM:SS）
        tPaymentRequest.dicOrder.put("ReceiverAddress", request.getParameter("txtReceiverAddress"));     //收货地址
        //tPaymentRequest.dicOrder.put("OrderURL", request.getParameter("txtOrderURL"));                     //设定订单网址

        //tPaymentRequest.dicOrder.put("Fee", request.getParameter("txtFee")); //设定手续费金额
        tPaymentRequest.dicOrder.put("BuyIP", request.getParameter("txtBuyIP"));
        
        //2、订单明细
        String items =  request.getParameter("Items");
        JSONObject json  = null;
        LinkedHashMap orderitem = null;
        try{
             json = new JSONObject(items);
             //[{xx:xx},{xx,xx}]传此种格式json
            JSONArray jsonArray = new JSONArray(json);
            for(int i=0;i<jsonArray.length();i++){
                orderitem =  new LinkedHashMap();
                JSONObject jb = jsonArray.getJSONObject(i);
                orderitem.put("SubMerName", jb.get("SubMerName"));    //设定二级商户名称
                orderitem.put("SubMerId", jb.get("SubMerId"));    //设定二级商户代码
                orderitem.put("SubMerMCC",jb.get("SubMerMCC"));   //设定二级商户MCC码
                orderitem.put("SubMerchantRemarks", jb.get("SubMerchantRemarks"));   //二级商户备注项
                orderitem.put("ProductID", jb.get("ProductID"));//商品代码，预留字段
                orderitem.put("ProductName", jb.get("ProductName"));//商品名称
                orderitem.put("UnitPrice", jb.get("UnitPrice")); //商品总价
                orderitem.put("Qty", jb.get("Qty"));//商品数量
                orderitem.put("ProductRemarks",jb.get("ProductRemarks")); //商品备注项
                orderitem.put("ProductType", jb.get("ProductType"));//商品类型
                orderitem.put("ProductDiscount", jb.get("ProductDiscount"));//商品折扣
                orderitem.put("ProductExpiredDate", jb.get("ProductExpiredDate"));
                tPaymentRequest.orderitems.put(i+1, orderitem);
            }
          //2、生成支付请求对象
            tPaymentRequest.dicRequest.put("CardNo", request.getParameter("txtPaymentAcctNo")); //支付账户
            tPaymentRequest.dicRequest.put("MobileNo", request.getParameter("txtMobilePhone"));//手机号后四位
            tPaymentRequest.dicRequest.put("CommodityType", request.getParameter("txtCommodityType"));   //设置商品种类
            tPaymentRequest.dicRequest.put("Installment", request.getParameter("txtInstallment"));  //分期标识
            if (request.getParameter("txtInstallment").toString().equals("1"))
            {
                tPaymentRequest.dicRequest.put("ProjectID", request.getParameter("txtProjectID"));    //设定分期代码
                tPaymentRequest.dicRequest.put("Period", request.getParameter("txtPeriod"));    //设定分期期数
            }
            tPaymentRequest.dicRequest.put("PaymentType", request.getParameter("txtPaymentType"));          //设定支付类型
            tPaymentRequest.dicRequest.put("PaymentLinkType", request.getParameter("txtPaymentLinkType"));      //设定支付接入方式
            tPaymentRequest.dicRequest.put("ReceiveAccount", request.getParameter("txtReceiveAccount"));    //设定收款方账号
            tPaymentRequest.dicRequest.put("ReceiveAccName", request.getParameter("txtReceiveAccName"));    //设定收款方户名
            tPaymentRequest.dicRequest.put("MerchantRemarks", request.getParameter("txtMerchantRemarks"));    //设定附言
            tPaymentRequest.dicRequest.put("IsBreakAccount", request.getParameter("txtIsBreakAccount"));    //设定交易是否分账
            tPaymentRequest.dicRequest.put("SplitAccTemplate", request.getParameter("txtSplitAccTemplate"));      //分账模版编号

            //3、传送支付请求
            JSON json1 = tPaymentRequest.postRequest();
                    
            //多商户
            //com.abc.trustpay.client.JSON tTrxResponse = tPaymentRequest.extendPostJSONRequest(1);
            String ReturnCode = json1.GetKeyValue("ReturnCode");
            String ErrorMessage = json1.GetKeyValue("ErrorMessage");
            JSONObject resultjb = new JSONObject();
            if (ReturnCode.equals("0000"))
            {
                //4、支付请求提交成功，商户自定后续动作
                //strMessage.append("ECMerchantType   = [" + json.GetKeyValue("ECMerchantType") + "]<br/>");
            	resultjb.put("ReturnCode",ReturnCode );
            	resultjb.put("MerchantID",  json1.GetKeyValue("MerchantID"));
            	resultjb.put("TrxType",  json1.GetKeyValue("TrxType") );
            	resultjb.put("OrderNo", json1.GetKeyValue("OrderNo")  );
            	resultjb.put("Amount",json1.GetKeyValue("OrderAmount")  );
            	resultjb.put("VerifyDate",  json1.GetKeyValue("VerifyDate"));
            	resultjb.put("VerifyTime",  json1.GetKeyValue("VerifyTime"));
            	return resultjb.toString();
            }
            else if (ReturnCode.equals("AP5095"))
            {
                //5、如果客户未签约，跳转到签约页面
            	resultjb.put("ReturnCode",ReturnCode );
            	resultjb.put("PaymentURL",json1.GetKeyValue("PaymentURL") );
               
                return resultjb.toString();
            }
            else
            {
                //6、支付请求提交失败，商户自定后续动作
            	resultjb.put("ReturnCode",ReturnCode );
            	resultjb.put("ErrorMessage",ErrorMessage );
                
    			return resultjb.toString();
    		}

        }catch (Exception e) {
			e.printStackTrace();
			JSONObject resultjb1 = new JSONObject();
			try {
				resultjb1.put("ErrorMessage", e.getMessage());resultjb1.put("ReturnCode", "error");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}finally {
				return resultjb1.toString();
			}
		}
	}

@Override
public String merchantQuickPaymentSend(HttpServletRequest request) {
	//1、生成支付请求对象
    QuickPaymentSend tQuickPaymentSend = new QuickPaymentSend();

    tQuickPaymentSend.dicOrder.put("OrderNo", request.getParameter("txtOrderNo"));                       //设定订单编号 （必要信息）
    tQuickPaymentSend.dicOrder.put("CurrencyCode", request.getParameter("txtCurrencyCode"));    //设定交易币种，
    tQuickPaymentSend.dicOrder.put("OrderAmount", request.getParameter("txtOrderAmount")); //设定订单金额 （必要信息）
    tQuickPaymentSend.dicOrder.put("Fee", request.getParameter("txtFee")); //设定手续费金额
    tQuickPaymentSend.dicOrder.put("OrderDate", request.getParameter("txtOrderDate"));                   //设定订单日期 （必要信息 - YYYY/MM/DD）
    tQuickPaymentSend.dicOrder.put("OrderTime", request.getParameter("txtOrderTime"));                   //设定订单时间 （必要信息 - HH:MM:SS）

    tQuickPaymentSend.dicRequest.put("AccName", request.getParameter("txtAccName"));
    tQuickPaymentSend.dicRequest.put("CertificateType", request.getParameter("txtCertificateType"));
    tQuickPaymentSend.dicRequest.put("CertificateID", request.getParameter("txtCertificateID"));
    tQuickPaymentSend.dicRequest.put("ExpDate", request.getParameter("txtExpDate"));
    tQuickPaymentSend.dicRequest.put("CVV2", request.getParameter("txtCVV2"));
    tQuickPaymentSend.dicRequest.put("VerifyCode", request.getParameter("txtVerifyCode"));
    tQuickPaymentSend.dicRequest.put("PaymentType", request.getParameter("txtPaymentType"));          //设定支付类型
    tQuickPaymentSend.dicRequest.put("PayLinkType", request.getParameter("txtPaymentLinkType"));      //设定支付接入方式
    tQuickPaymentSend.dicRequest.put("MerchantRemarks", request.getParameter("txtMerchantRemarks"));  //设定商户备注信息

    //2、传送支付请求并返回结果
    JSON json = tQuickPaymentSend.postRequest();

    String ReturnCode = json.GetKeyValue("ReturnCode");
    String ErrorMessage = json.GetKeyValue("ErrorMessage");
    JSONObject returnmap = new JSONObject();
    try {
    if (ReturnCode.equals("0000"))
    {
			returnmap.put("ReturnCode", ReturnCode);
	
    	returnmap.put("ErrorMessage", ErrorMessage);
    	returnmap.put("MerchantID",  json.GetKeyValue("MerchantID"));
    	returnmap.put("TrxType", json.GetKeyValue("TrxType"));
    	returnmap.put("OrderNo",  json.GetKeyValue("OrderNo"));
    	returnmap.put("Amount", json.GetKeyValue("OrderAmount") );
    	returnmap.put("HostDate", json.GetKeyValue("HostDate"));
    	returnmap.put("HostTime", json.GetKeyValue("HostTime"));
        //3、支付请求提交成功，返回结果信息
     
    }
    else
    {
        //6、支付请求提交失败，商户自定后续动作
    	returnmap.put("ReturnCode", ReturnCode);
    	returnmap.put("ErrorMessage", ErrorMessage);
	}
	} catch (JSONException e) {
		e.printStackTrace();
	}finally {
		return returnmap.toString();
	}
}

@Override
public String MerchantQuickPaymentResend(HttpServletRequest request) {
	//1、生成支付请求对象
    QuickPaymentReSend tQuickPaymentReSend = new QuickPaymentReSend();
    tQuickPaymentReSend.dicOrder.put("OrderNo", request.getParameter("txtOrderNo"));                       //设定订单编号 （必要信息）
    tQuickPaymentReSend.dicOrder.put("CurrencyCode",request.getParameter("txtCurrencyCode"));    //设定交易币种
    tQuickPaymentReSend.dicOrder.put("OrderAmount",request.getParameter("txtOrderAmount")); //设定订单金额 （必要信息）
    tQuickPaymentReSend.dicOrder.put("OrderDate",request.getParameter("txtOrderDate"));    //设定订单日期 （必要信息 - YYYY/MM/DD）
    tQuickPaymentReSend.dicOrder.put("OrderTime",request.getParameter("txtOrderTime"));                   //设定订单时间 （必要信息 - HH:MM:SS）

    //2、传送支付请求
    JSON json = tQuickPaymentReSend.postRequest();

    String ReturnCode = json.GetKeyValue("ReturnCode");
    String ErrorMessage = json.GetKeyValue("ErrorMessage");
    JSONObject resultjb = new JSONObject();
    try {
    if (ReturnCode.equals("0000"))
    {
        //3、支付请求提交成功，商户自定义后继操作
    	
    	resultjb.put("ReturnCode", ReturnCode);
    	resultjb.put("ErrorMessage", ErrorMessage);
    	resultjb.put("MerchantID",  json.GetKeyValue("MerchantID"));
    	resultjb.put("TrxType", json.GetKeyValue("TrxType"));
    	resultjb.put("OrderNo",  json.GetKeyValue("OrderNo"));
    	resultjb.put("Amount", json.GetKeyValue("OrderAmount") );
    	resultjb.put("HostDate", json.GetKeyValue("HostDate"));
    	resultjb.put("HostTime", json.GetKeyValue("HostTime"));
    }
    else
    {
        //4、支付请求提交失败，商户自定后续动作
    	resultjb.put("ReturnCode", ReturnCode);
		resultjb.put("ErrorMessage", ErrorMessage);
	
	}
	} catch (JSONException e) {
		e.printStackTrace();
	}finally {
		return resultjb.toString();
	}
}
}
