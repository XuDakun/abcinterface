package com.wfhy.abcinterface.service.impl;

import com.abc.pay.client.Base64;
import com.abc.pay.client.Constants;
import com.abc.pay.client.JSON;
import com.abc.pay.client.MerchantConfig;
import com.abc.pay.client.MerchantPara;
import com.abc.pay.client.TrxException;
import com.abc.pay.client.ZipUtil;
import com.abc.pay.client.ebus.AuthenMerchantQueryRequest;
import com.abc.pay.client.ebus.GetReceiptRequest;
import com.abc.pay.client.ebus.IdentityVerifyRequest;
import com.abc.pay.client.ebus.PaymentRequest;
import com.abc.pay.client.ebus.PreAuthPaymentRequest;
import com.abc.pay.client.ebus.QueryOrderRequest;
import com.abc.pay.client.ebus.QueryTrnxRecords;
import com.abc.pay.client.ebus.QuickIdentityVerifyRequest;
import com.abc.pay.client.ebus.QuickPaymentRequest;
import com.abc.pay.client.ebus.SettleRequest;
import com.abc.pay.client.ebus.SettleRequestPlatForm;
import com.abc.pay.client.ebus.TransferOutQueryRequest;
import com.wfhy.abcinterface.service.PayService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PayServiceImpl  implements PayService {
    /**
     * 发送交易请求
     * @param request
     * @return 返回的json数据/报错信息
     */
	@Override
    public String merchantPayment(HttpServletRequest request)  {
		System.out.println("进入service");
        PaymentRequest tPaymentRequest = new PaymentRequest();
        tPaymentRequest.dicOrder.put("PayTypeID", request.getParameter("PayTypeID"));                   //设定交易类型
        tPaymentRequest.dicOrder.put("OrderDate", request.getParameter("OrderDate") );                  //设定订单日期 （必要信息 - YYYY/MM/DD）
        tPaymentRequest.dicOrder.put("OrderTime", request.getParameter("OrderTime"));                   //设定订单时间 （必要信息 - HH:MM:SS）
        tPaymentRequest.dicOrder.put("orderTimeoutDate", request.getParameter("orderTimeoutDate"));     //设定订单有效期
        tPaymentRequest.dicOrder.put("OrderNo", request.getParameter("OrderNo"));                       //设定订单编号 （必要信息）
        tPaymentRequest.dicOrder.put("CurrencyCode", request.getParameter("CurrencyCode"));             //设定交易币种
        tPaymentRequest.dicOrder.put("OrderAmount", request.getParameter("PaymentRequestAmount"));      //设定交易金额
        tPaymentRequest.dicOrder.put("Fee", request.getParameter("Fee"));                               //设定手续费金额
        tPaymentRequest.dicOrder.put("AccountNo", request.getParameter("AccountNo"));                   //设定支付账户
        tPaymentRequest.dicOrder.put("OrderDesc", request.getParameter("OrderDesc"));                   //设定订单说明
        tPaymentRequest.dicOrder.put("OrderURL", request.getParameter("OrderURL"));                     //设定订单地址
        tPaymentRequest.dicOrder.put("ReceiverAddress", request.getParameter("ReceiverAddress"));       //收货地址
        tPaymentRequest.dicOrder.put("InstallmentMark", request.getParameter("InstallmentMark"));       //分期标识
        if (request.getParameter("InstallmentMark") == "1" && request.getParameter("PayTypeID") == "DividedPay")
        {
            tPaymentRequest.dicOrder.put("InstallmentCode", request.getParameter("InstallmentCode"));   //设定分期代码
            tPaymentRequest.dicOrder.put("InstallmentNum", request.getParameter("InstallmentNum"));     //设定分期期数
        }
        tPaymentRequest.dicOrder.put("CommodityType", request.getParameter("CommodityType"));           //设置商品种类
        tPaymentRequest.dicOrder.put("BuyIP", request.getParameter("BuyIP"));                           //IP
        tPaymentRequest.dicOrder.put("ExpiredDate", request.getParameter("ExpiredDate"));               //设定订单保存时间
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

        String paymentType = request.getParameter("PaymentType");
        tPaymentRequest.dicRequest.put("PaymentType", paymentType);            //设定支付类型
        String paymentLinkType  = request.getParameter("PaymentLinkType");
        tPaymentRequest.dicRequest.put("PaymentLinkType", paymentLinkType);    //设定支付接入方式
        if (paymentType.equals(Constants.PAY_TYPE_UCBP) && paymentLinkType.equals(Constants.PAY_LINK_TYPE_MOBILE))
        {
            tPaymentRequest.dicRequest.put("UnionPayLinkType",request.getParameter("UnionPayLinkType"));  //当支付类型为6，支付接入方式为2的条件满足时，需要设置银联跨行移动支付接入方式
        }
        tPaymentRequest.dicRequest.put("ReceiveAccount", request.getParameter("ReceiveAccount"));      //设定收款方账号
        tPaymentRequest.dicRequest.put("ReceiveAccName", request.getParameter("ReceiveAccName"));      //设定收款方户名
        tPaymentRequest.dicRequest.put("NotifyType", request.getParameter("NotifyType"));              //设定通知方式
        tPaymentRequest.dicRequest.put("ResultNotifyURL", request.getParameter("ResultNotifyURL"));    //设定通知URL地址
        tPaymentRequest.dicRequest.put("MerchantRemarks", request.getParameter("MerchantRemarks"));    //设定附言
        tPaymentRequest.dicRequest.put("ReceiveMark",request.getParameter("ReceiveMark"));             //交易是否直接入二级商户账户
        tPaymentRequest.dicRequest.put("ReceiveMerchantType",request.getParameter("ReceiveMerchantType")); //设定收款方账户类型
        tPaymentRequest.dicRequest.put("IsBreakAccount", request.getParameter("IsBreakAccount"));      //设定交易是否分账、交易是否支持向二级商户入账
        tPaymentRequest.dicRequest.put("SplitAccTemplate", request.getParameter("SplitAccTemplate"));  //分账模版编号

//4、添加分账信息
        String[] SubMerchantID_arr = new String[]{};
        String[] SplitAmount_arr = new String[]{};

        SubMerchantID_arr    = request.getParameterValues("SplitMerchantID");
        SplitAmount_arr      = request.getParameterValues("SplitAmount");

        LinkedHashMap map = null;

        if(SubMerchantID_arr != null){
            for (int i = 0; i < SubMerchantID_arr.length; i++)
            {
                map = new LinkedHashMap();
                //map.put("SeqNo       ", String.valueOf(i + 1));
                map.put("SplitMerchantID",SubMerchantID_arr[i]);
                map.put("SplitAmount",SplitAmount_arr[i]);

                tPaymentRequest.dicSplitAccInfo.put(i+1, map);
            }
        }

        JSON resultjson = tPaymentRequest.postRequest();
        //JSON json = tPaymentRequest.extendPostRequest(1);

        String ReturnCode = resultjson.GetKeyValue("ReturnCode");
        String ErrorMessage = resultjson.GetKeyValue("ErrorMessage");
        String  PaymentURL  = resultjson.GetKeyValue("PaymentURL");
        JSONObject resultjb = new JSONObject();
            resultjb.put("ReturnCode",ReturnCode);
            resultjb.put("ErrorMessage",ErrorMessage);
            resultjb.put("PaymentURL",PaymentURL);

         return resultjb.toString();
        }catch (Exception e){
            //e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 页面发送交易请求
     * @param request
     * @return
     */
	@Override
    public String merchantPaymentIE(HttpServletRequest request) {
		PaymentRequest tPaymentRequest = new PaymentRequest();
		tPaymentRequest.dicOrder.put("PayTypeID", request.getParameter("PayTypeID"));                   //设定交易类型
		tPaymentRequest.dicOrder.put("OrderDate", request.getParameter("OrderDate") );                  //设定订单日期 （必要信息 - YYYY/MM/DD）
		tPaymentRequest.dicOrder.put("OrderTime", request.getParameter("OrderTime"));                   //设定订单时间 （必要信息 - HH:MM:SS）
		tPaymentRequest.dicOrder.put("orderTimeoutDate", request.getParameter("orderTimeoutDate"));     //设定订单有效期
		tPaymentRequest.dicOrder.put("OrderNo", request.getParameter("OrderNo"));                       //设定订单编号 （必要信息）
		tPaymentRequest.dicOrder.put("CurrencyCode", request.getParameter("CurrencyCode"));             //设定交易币种
		tPaymentRequest.dicOrder.put("OrderAmount", request.getParameter("PaymentRequestAmount"));      //设定交易金额
		tPaymentRequest.dicOrder.put("Fee", request.getParameter("Fee"));                               //设定手续费金额
		tPaymentRequest.dicOrder.put("AccountNo", request.getParameter("AccountNo"));                   //设定支付账户
		tPaymentRequest.dicOrder.put("OrderDesc", request.getParameter("OrderDesc"));                   //设定订单说明
		tPaymentRequest.dicOrder.put("OrderURL", request.getParameter("OrderURL"));                     //设定订单地址
		tPaymentRequest.dicOrder.put("ReceiverAddress", request.getParameter("ReceiverAddress"));       //收货地址
		tPaymentRequest.dicOrder.put("InstallmentMark", request.getParameter("InstallmentMark"));       //分期标识
		if (request.getParameter("InstallmentMark") == "1" && request.getParameter("PayTypeID") == "DividedPay")
		{
		    tPaymentRequest.dicOrder.put("InstallmentCode", request.getParameter("InstallmentCode"));   //设定分期代码
		    tPaymentRequest.dicOrder.put("InstallmentNum", request.getParameter("InstallmentNum"));     //设定分期期数
		}
		tPaymentRequest.dicOrder.put("CommodityType", request.getParameter("CommodityType"));           //设置商品种类
		tPaymentRequest.dicOrder.put("BuyIP", request.getParameter("BuyIP"));                           //IP
		tPaymentRequest.dicOrder.put("ExpiredDate", request.getParameter("ExpiredDate"));   
		//2、订单明细
		 //3、生成支付请求对象
		String paymentType = request.getParameter("PaymentType");
		tPaymentRequest.dicRequest.put("PaymentType", paymentType);            //设定支付类型
		String paymentLinkType  = request.getParameter("PaymentLinkType");                                         
		tPaymentRequest.dicRequest.put("PaymentLinkType", paymentLinkType);    //设定支付接入方式
		if (paymentType.equals(Constants.PAY_TYPE_UCBP) && paymentLinkType.equals(Constants.PAY_LINK_TYPE_MOBILE))
		{
		    tPaymentRequest.dicRequest.put("UnionPayLinkType",request.getParameter("UnionPayLinkType"));  //当支付类型为6，支付接入方式为2的条件满足时，需要设置银联跨行移动支付接入方式
		}
		tPaymentRequest.dicRequest.put("ReceiveAccount", request.getParameter("ReceiveAccount"));    //设定收款方账号
		tPaymentRequest.dicRequest.put("ReceiveAccName", request.getParameter("ReceiveAccName"));    //设定收款方户名
		tPaymentRequest.dicRequest.put("NotifyType", request.getParameter("NotifyType"));    //设定通知方式
		tPaymentRequest.dicRequest.put("ResultNotifyURL", request.getParameter("ResultNotifyURL"));    //设定通知URL地址
		tPaymentRequest.dicRequest.put("MerchantRemarks", request.getParameter("MerchantRemarks"));    //设定附言
		tPaymentRequest.dicRequest.put("IsBreakAccount", request.getParameter("IsBreakAccount"));    //设定交易是否分账
		tPaymentRequest.dicRequest.put("SplitAccTemplate", request.getParameter("SplitAccTemplate"));      //分账模版编号        
		//问题：发送交易请求
		return null;
		/*
		 * MerchantPara para = MerchantConfig.getUniqueInstance().getPara(); String
		 * sTrustPayIETrxURL=para.getTrustPayTrxIEURL(); String
		 * sErrorUrl=para.getMerchantErrorURL(); String tSignature=""; 
		 * try{
		 *  tSignature =
		 * tPaymentRequest.genSignature(1); }catch (TrxException e){
		 * request.setAttribute("tReturnCode", e.getCode());
		 * request.setAttribute("tErrorMsg", e.getMessage());
		 * request.getRequestDispatcher("/ErrorPageInternal.jsp").forward(request);
		 * return null; }
		 */
    }
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
public String merchantQueryOrder(HttpServletRequest request) {
	//1、生成交易查询对象
	String payTypeID = request.getParameter("PayTypeID");
	String queryTpye = request.getParameter("QueryType");
	if(queryTpye.equals("0")){
		queryTpye = "false";
	}else if (queryTpye.equals("1")){
		queryTpye="true";
	}

	QueryOrderRequest tQueryRequest = new QueryOrderRequest();
	tQueryRequest.queryRequest.put("PayTypeID", payTypeID);    //设定交易类型
	tQueryRequest.queryRequest.put("OrderNo", request.getParameter("OrderNo"));    //设定订单编号 （必要信息）
	tQueryRequest.queryRequest.put("QueryDetail", queryTpye);//设定查询方式
	//如果需要专线地址，调用此方法：
	//tQueryRequest.setConnectionFlag(true);
	JSON json = tQueryRequest.postRequest();
	//JSON json = tQueryRequest.extendPostRequest(1);

	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");
JSONObject returnjb = new JSONObject();
try {
	if (ReturnCode.equals("0000"))
	{
	
			returnjb.put("ReturnCode", ReturnCode);
	
		returnjb.put("ErrorMessage", ErrorMessage);
	    
	    //4、获取结果信息
	    String orderInfo = json.GetKeyValue("Order");
	    if (orderInfo.length() < 1)
	    {
	    	returnjb.put("Order", "查询结果为空");
	    }
	    else
	    {
	        //1、还原经过base64编码的信息 
	    	Base64 tBase64 = new Base64();
	  		String orderDetail = new String(tBase64.decode(orderInfo));
	        json.setJsonString(orderDetail);
	        returnjb.put("Order", orderDetail);
	        
	        if(queryTpye.equals("0")) {
	        	 returnjb.put("PayTypeID", json.GetKeyValue("PayTypeID"));
	        	 returnjb.put("OrderNo", json.GetKeyValue("OrderNo"));
	        	 returnjb.put("OrderDate", json.GetKeyValue("OrderDate"));
	        	 returnjb.put("OrderTime", json.GetKeyValue("OrderTime"));
	        	 returnjb.put("OrderAmount", json.GetKeyValue("OrderAmount"));
	        	 returnjb.put("Status", json.GetKeyValue("Status"));
	        }
	        else
	        {
	            LinkedHashMap hashMap = new LinkedHashMap();
	            if (payTypeID.equals("ImmediatePay") || payTypeID.equals("PreAuthPay"))
	            {
	            	 returnjb.put("PayTypeID", json.GetKeyValue("PayTypeID"));
	            	 returnjb.put("OrderNo", json.GetKeyValue("OrderNo"));
	            	 returnjb.put("OrderDate", json.GetKeyValue("OrderDate"));
	            	 returnjb.put("OrderTime", json.GetKeyValue("OrderTime"));
	            	 returnjb.put("OrderAmount", json.GetKeyValue("OrderAmount"));
	            	 returnjb.put("Status", json.GetKeyValue("Status"));
	            	 returnjb.put("OrderDesc", json.GetKeyValue("OrderDesc"));
	            	 returnjb.put("OrderURL", json.GetKeyValue("OrderURL"));
	            	 returnjb.put("PaymentLinkType", json.GetKeyValue("PaymentLinkType"));
	            	 returnjb.put("AcctNo", json.GetKeyValue("AcctNo"));
	            	 returnjb.put("CommodityType", json.GetKeyValue("CommodityType"));
	            	 returnjb.put("ReceiverAddress", json.GetKeyValue("ReceiverAddress"));
	            	 returnjb.put("BuyIP", json.GetKeyValue("BuyIP"));
	            	 returnjb.put("iRspRef", json.GetKeyValue("iRspRef"));
	            	 returnjb.put("ReceiveAccount", json.GetKeyValue("ReceiveAccount"));
	            	 returnjb.put("ReceiveAccName", json.GetKeyValue("ReceiveAccName"));
	            	 returnjb.put("MerchantRemarks", json.GetKeyValue("MerchantRemarks"));
	            	 
	            	 
	                //out.println("HostTime      = [" + json.GetKeyValue("HostTime") + "]<br/>");
	                //out.println("HostDate      = [" + json.GetKeyValue("HostDate") + "]<br/>");
	                
	                //5、商品明细
	                hashMap = json.GetArrayValue("OrderItems");
	                if(hashMap.size() == 0){
	                	 returnjb.put("OrderItems","商品明细为空");
	                }
	                else
	                {               
	                    Iterator iter = hashMap.entrySet().iterator();
	                    while (iter.hasNext()) {
	                    	Map.Entry entry = (Map.Entry) iter.next();
	                    	//Object key = entry.getKey();
	                    	Hashtable val = (Hashtable)entry.getValue();                  	
	                    	returnjb.put("SubMerName", (String)val.get("SubMerName") );
	                    	returnjb.put("SubMerId", (String)val.get("SubMerId") );
	                    	returnjb.put("SubMerMCC", (String)val.get("SubMerMCC") );
	                    	returnjb.put("SubMerchantRemarks", (String)val.get("SubMerchantRemarks") );
	                    	returnjb.put("ProductID", (String)val.get("ProductID") );
	                    	returnjb.put("ProductName", (String)val.get("ProductName") );
	                    	returnjb.put("UnitPrice", (String)val.get("UnitPrice") );
	                    	returnjb.put("Qty", (String)val.get("Qty") );
	                    	returnjb.put("ProductRemarks", (String)val.get("ProductRemarks") );
		                }    
	                }
	            }            
	            else if (payTypeID.equals("DividedPay"))
	            {
	            	returnjb.put("PayTypeID",  json.GetKeyValue("PayTypeID")  );
	            	returnjb.put("OrderNo",  json.GetKeyValue("OrderNo")  );
	            	returnjb.put("OrderDate",  json.GetKeyValue("OrderDate")  );
	            	returnjb.put("OrderTime",  json.GetKeyValue("OrderTime")  );
	            	returnjb.put("OrderAmount",  json.GetKeyValue("OrderAmount")  );
	            	returnjb.put("Status",  json.GetKeyValue("Status")  );
	            	returnjb.put("InstallmentCode",  json.GetKeyValue("InstallmentCode")  );
	            	returnjb.put("InstallmentNum",  json.GetKeyValue("InstallmentNum")  );
	            	returnjb.put("PaymentLinkType",  json.GetKeyValue("PaymentLinkType")  );
	            	returnjb.put("AcctNo",  json.GetKeyValue("AcctNo")  );
	            	returnjb.put("CommodityType",  json.GetKeyValue("CommodityType")  );
	            	returnjb.put("ReceiverAddress",  json.GetKeyValue("ReceiverAddress")  );
	            	returnjb.put("BuyIP",  json.GetKeyValue("BuyIP")  );
	            	returnjb.put("iRspRef",  json.GetKeyValue("iRspRef")  );
	            	returnjb.put("ReceiveAccount",  json.GetKeyValue("ReceiveAccount")  );
	            	returnjb.put("ReceiveAccName",  json.GetKeyValue("ReceiveAccName")  );
	            	returnjb.put("MerchantRemarks",  json.GetKeyValue("MerchantRemarks")  );
	            	
	            	
	            	
	                //out.println("HostTime      = [" + json.GetKeyValue("HostTime") + "]<br/>");
	                //out.println("HostDate      = [" + json.GetKeyValue("HostDate") + "]<br/>");

	                hashMap = json.GetArrayValue("OrderItems");
	                if(hashMap.size() == 0){
	                	returnjb.put("OrderItems","商品明细为空" );
	                }
	                else
	                {
	                   
	                    Iterator iter = hashMap.entrySet().iterator();
	                    while (iter.hasNext()) {
	                    	Map.Entry entry = (Map.Entry) iter.next();
	                    	//Object key = entry.getKey();
	                    	Hashtable val = (Hashtable)entry.getValue();           
	                    	returnjb.put("SubMerName", (String)val.get("SubMerName") );
	                    	returnjb.put("SubMerId", (String)val.get("SubMerId") );
	                    	returnjb.put("SubMerMCC", (String)val.get("SubMerMCC") );
	                    	returnjb.put("SubMerchantRemarks", (String)val.get("SubMerchantRemarks") );
	                    	returnjb.put("ProductID", (String)val.get("ProductID") );
	                    	returnjb.put("ProductName", (String)val.get("ProductName") );
	                    	returnjb.put("UnitPrice", (String)val.get("UnitPrice") );
	                    	returnjb.put("Qty", (String)val.get("Qty") );
	                    	returnjb.put("ProductRemarks", (String)val.get("ProductRemarks") );
	                    	
	                    	
		                 
	                    }
	                }
					hashMap.clear();
	                hashMap = json.GetArrayValue("Distribution");
	                if (hashMap.size() == 0) {
	                	returnjb.put("Distribution", "分账账户信息为空");
	                }
	                else
	                {
	                    //out.println("分账账户信息明细为:<br/>");
	                    Iterator iter = hashMap.entrySet().iterator();
	                    while (iter.hasNext()) {
	                    	Map.Entry entry = (Map.Entry) iter.next();
	                    	//Object key = entry.getKey();
	                    	Hashtable val = (Hashtable)entry.getValue();   
	                    	returnjb.put("DisAccountNo", (String)val.get("DisAccountNo") );
	                    	returnjb.put("DisAccountName", (String)val.get("DisAccountName") );
	                    	returnjb.put("DisAmount", (String)val.get("DisAmount") );
	                    }
	                }
	            }            
	            else if (payTypeID.equals("Refund"))
	            {
	            	returnjb.put("PayTypeID",json.GetKeyValue("PayTypeID"));
	            	returnjb.put("OrderNo",json.GetKeyValue("OrderNo"));
	            	returnjb.put("OrderDate",json.GetKeyValue("OrderDate"));
	            	returnjb.put("OrderTime",json.GetKeyValue("OrderTime"));
	            	returnjb.put("RefundAmount",json.GetKeyValue("RefundAmount"));
	            	returnjb.put("Status",json.GetKeyValue("Status"));
	            	returnjb.put("iRspRef",json.GetKeyValue("iRspRef"));
	            	returnjb.put("MerRefundAccountNo",json.GetKeyValue("MerRefundAccountNo"));
	            	returnjb.put("MerRefundAccountName",json.GetKeyValue("MerRefundAccountName"));
	           
	            	
	                //out.println("HostTime      = [" + json.GetKeyValue("HostTime") + "]<br/>");
	                //out.println("HostDate      = [" + json.GetKeyValue("HostDate") + "]<br/>");
	            }           
	            else if (payTypeID.equals("AgentPay"))
	            {
	            	returnjb.put("PayTypeID",json.GetKeyValue("PayTypeID"));
	            	returnjb.put("OrderNo",json.GetKeyValue("OrderNo"));
	            	returnjb.put("OrderDate",json.GetKeyValue("OrderDate"));
	            	returnjb.put("OrderTime",json.GetKeyValue("OrderTime"));
	            	returnjb.put("OrderAmount",json.GetKeyValue("OrderAmount"));
	            	returnjb.put("Status",json.GetKeyValue("Status"));
	            	returnjb.put("InstallmentCode",json.GetKeyValue("InstallmentCode"));
	            	returnjb.put("InstallmentNum",json.GetKeyValue("InstallmentNum"));
	            	returnjb.put("PaymentLinkType",json.GetKeyValue("PaymentLinkType"));
	            	returnjb.put("AcctNo",json.GetKeyValue("AcctNo"));
	            	returnjb.put("CommodityType",json.GetKeyValue("CommodityType"));
	            	returnjb.put("ReceiverAddress",json.GetKeyValue("ReceiverAddress"));
	            	returnjb.put("BuyIP",json.GetKeyValue("BuyIP"));
	            	returnjb.put("iRspRef",json.GetKeyValue("iRspRef"));
	            	returnjb.put("ReceiveAccount",json.GetKeyValue("ReceiveAccount"));
	            	returnjb.put("ReceiveAccName",json.GetKeyValue("ReceiveAccName"));
	            	returnjb.put("MerchantRemarks",json.GetKeyValue("MerchantRemarks"));
	            	
	                //out.println("HostTime      = [" + json.GetKeyValue("HostTime") + "]<br/>");
	                //out.println("HostDate      = [" + json.GetKeyValue("HostDate") + "]<br/>");
	                
	                hashMap = json.GetArrayValue("OrderItem");
	                if(hashMap.size() == 0){
	                	returnjb.put("OrderItem","商品明细为空");
	                }
	                else
	                {
	                    //out.println("商品明细为:<br/>");
	                    Iterator iter = hashMap.entrySet().iterator();
	                    while (iter.hasNext()) {
	                    	Map.Entry entry = (Map.Entry) iter.next();
	                    	//Object key = entry.getKey();
	                    	Hashtable val = (Hashtable)entry.getValue();  
	                    	returnjb.put("SubMerName", (String)val.get("SubMerName") );
	                    	returnjb.put("SubMerId", (String)val.get("SubMerId") );
	                    	returnjb.put("SubMerMCC", (String)val.get("SubMerMCC") );
	                    	returnjb.put("SubMerchantRemarks", (String)val.get("SubMerchantRemarks") );
	                    	returnjb.put("ProductID", (String)val.get("ProductID") );
	                    	returnjb.put("ProductName", (String)val.get("ProductName") );
	                    	returnjb.put("UnitPrice", (String)val.get("UnitPrice") );
	                    	returnjb.put("Qty", (String)val.get("Qty") );
	                    	returnjb.put("ProductRemarks", (String)val.get("ProductRemarks") );
	                    	
	                    	
	                    }
	                }
	                //4、获取分账账户信息
	                hashMap.clear();
	                hashMap = json.GetArrayValue("Distribution");
	                if (hashMap.size() == 0) {
	                    returnjb.put("Distribution","分账账户信息为空");
                    	
	                }
	                else
	                {
	                    //out.println("分账账户信息明细为:<br/>");
	                    Iterator iter = hashMap.entrySet().iterator();
	                    while (iter.hasNext()) {
	                    	Map.Entry entry = (Map.Entry) iter.next();
	                    	//Object key = entry.getKey();
	                    	Hashtable val = (Hashtable)entry.getValue();   
	                    	returnjb.put("DisAccountNo", (String)val.get("DisAccountNo") );
	                    	returnjb.put("DisAccountName", (String)val.get("DisAccountName") );
	                    	returnjb.put("DisAmount", (String)val.get("DisAmount") );
	                    	
	                    }
	                }                         
	            }           
	            else if (payTypeID.equals("PreAuthed") || payTypeID.equals("PreAuthCancel"))
	            {
	            	returnjb.put("PayTypeID",json.GetKeyValue("PayTypeID") );
	            	returnjb.put("OrderNo",json.GetKeyValue("OrderNo") );
	            	returnjb.put("OrderDate",json.GetKeyValue("OrderDate") );
	            	returnjb.put("OrderTime",json.GetKeyValue("OrderTime") );
	            	returnjb.put("OrderAmount",json.GetKeyValue("OrderAmount") );
	            	returnjb.put("Status",json.GetKeyValue("Status") );
	            	returnjb.put("AcctNo",json.GetKeyValue("AcctNo") );
	            	returnjb.put("iRspRef",json.GetKeyValue("iRspRef") );
	            	returnjb.put("ReceiveAccount",json.GetKeyValue("ReceiveAccount") );
	            	returnjb.put("ReceiveAccName",json.GetKeyValue("ReceiveAccName") );
	                //out.println("HostTime      = [" + json.GetKeyValue("HostTime") + "]<br/>");
	                //out.println("HostDate      = [" + json.GetKeyValue("HostDate") + "]<br/>");
	            }
	        }
	    }
	}
	else
	{
	    //6、商户订单查询失败
		returnjb.put("ReturnCode",ReturnCode );
		returnjb.put("ErrorMessage",ErrorMessage );
	}
} catch (JSONException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}finally {
	return returnjb.toString();
}
}

@Override
public String merchantQueryTrnxRecords(HttpServletRequest request) {
	//1、取得交易流水查询所需要的信息
	String tSettleDate = request.getParameter("SettleDate");
	String tSettleStartHour = request.getParameter("SettleStartHour");
	String tSettleEndHour = request.getParameter("SettleEndHour");
	String tZIP = request.getParameter("ZIP");
	    
	//2、生成交易流水查询请求对象
	QueryTrnxRecords tRequest = new QueryTrnxRecords();
	tRequest.dicRequest.put("SettleDate",tSettleDate);  //查询日期YYYY/MM/DD （必要信息）
	tRequest.dicRequest.put("SettleStartHour",tSettleStartHour);  //查询开始时间段（0-23）
	tRequest.dicRequest.put("SettleEndHour",tSettleEndHour);  //查询截止时间段（0-23）
	tRequest.dicRequest.put("ZIP",tZIP);

	//3、传送交易流水查询请求并取得交易流水
	JSON json = tRequest.postRequest();

	//4、判断交易流水查询结果状态，进行后续操作
	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");
	JSONObject returnjb = new JSONObject();
	try {
	if (ReturnCode.equals("0000"))
	{
	    //5、交易流水查询成功，生成交易流水对象
		
			returnjb.put("ReturnCode", json.GetKeyValue("ReturnCode") );
	
		returnjb.put("ErrorMessage", json.GetKeyValue("ErrorMessage") );
		returnjb.put("TrxType", json.GetKeyValue("TrxType") );
		
		
		
	 	if(tZIP.equals("0")){
	 		returnjb.put("DetailRecords", json.GetKeyValue("DetailRecords") );
	 	}
	    if(tZIP.equals("1")) {
	    	returnjb.put("ZIPDetailRecords",ZipUtil.gunzip(json.GetKeyValue("ZIPDetailRecords"))  );
	    }

	}
	else {
	    //6、交易流水查询失败
		returnjb.put("ReturnCode", ReturnCode );
		returnjb.put("ErrorMessage", ErrorMessage );
	}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}

@Override
public String merchantTrxSettleQuery(HttpServletRequest request) {
	NumberFormat tFormat = NumberFormat.getInstance();
	tFormat.setMaximumFractionDigits(2);
	tFormat.setGroupingUsed(false);
	tFormat.setMinimumFractionDigits(2);

	//1、取得商户对账单下载所需要的信息
	String tSettleDate = request.getParameter("SettleDate");
	String tZIP = request.getParameter("ZIP");
	    
	//2、生成商户对账单下载请求对象
	SettleRequest tRequest = new SettleRequest();
	tRequest.dicRequest.put("SettleDate",tSettleDate);  //对账日期YYYY/MM/DD （必要信息）
	tRequest.dicRequest.put("ZIP",tZIP);

	//3、传送商户对账单下载请求并取得对账单
	JSON json = tRequest.postRequest();

	//4、判断商户对账单下载结果状态，进行后续操作
	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");
	JSONObject returnjb = new JSONObject();
	try {
	if (ReturnCode.equals("0000"))
	{
	    //5、商户对账单下载成功，生成对账单对象
		
			returnjb.put("ReturnCode", json.GetKeyValue("ReturnCode") );
		
		returnjb.put("ErrorMessage", json.GetKeyValue("ErrorMessage") );
		returnjb.put("TrxType", json.GetKeyValue("TrxType") );
		returnjb.put("SettleDate", json.GetKeyValue("SettleDate") );
		
	    
	 
	 	if(tZIP.equals("0")){
	 		returnjb.put("DetailRecords", json.GetKeyValue("DetailRecords") );
	 	}
	    if(tZIP.equals("1")) {
	    	returnjb.put("ZIPDetailRecords", json.GetKeyValue("ZIPDetailRecords") );
	    }
	    
	}
	else {
	    //6、商户账单下载失败
		returnjb.put("ReturnCode", json.GetKeyValue("ReturnCode") );
		returnjb.put("ErrorMessage", json.GetKeyValue("ErrorMessage") );
	}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}

@Override
public String merchantTrxSettlePlatForm(HttpServletRequest request) {
	NumberFormat tFormat = NumberFormat.getInstance();
	tFormat.setMaximumFractionDigits(2);
	tFormat.setGroupingUsed(false);
	tFormat.setMinimumFractionDigits(2);

	//1、取得商户对账单下载所需要的信息
	String tSettleDate = request.getParameter("SettleDate");
	String tZIP = request.getParameter("ZIP");
	    
	//2、生成商户对账单下载请求对象
	SettleRequestPlatForm tRequest = new SettleRequestPlatForm();
	tRequest.dicRequest.put("SettleDate",tSettleDate);  //对账日期YYYY/MM/DD （必要信息）
	tRequest.dicRequest.put("ZIP",tZIP);

	//3、传送商户对账单下载请求并取得对账单
	JSON json = tRequest.postRequest();

	//4、判断商户对账单下载结果状态，进行后续操作
	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");
	JSONObject returnjb = new JSONObject();

	try {
	if (ReturnCode.equals("0000"))
	{
		 //5、商户对账单下载成功，生成对账单对象
		
			returnjb.put("ReturnCode", json.GetKeyValue("ReturnCode") );
		
	
	returnjb.put("ErrorMessage", json.GetKeyValue("ErrorMessage") );
	returnjb.put("TrxType", json.GetKeyValue("TrxType") );
	returnjb.put("SettleDate", json.GetKeyValue("SettleDate") );
	
    
 
 	if(tZIP.equals("0")){
 		returnjb.put("DetailRecords", json.GetKeyValue("DetailRecords") );
 	}
    if(tZIP.equals("1")) {
    	returnjb.put("ZIPDetailRecords", json.GetKeyValue("ZIPDetailRecords") );
    }
	    
	}
	else {
	    //6、商户账单下载失败
		returnjb.put("ReturnCode", ReturnCode );
		
		returnjb.put("ErrorMessage",ReturnCode );
	}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}
/**
 * 需要将返回URL重新请求
 */
@Override
public String identityVerify(HttpServletRequest request) {
	 //1、生成身份验证请求对象
    IdentityVerifyRequest tRequest = new IdentityVerifyRequest();
    tRequest.dicRequest.put("CustomType", request.getParameter("txtCustomType")); //客户类型 （必要信息）
    tRequest.dicRequest.put("BankCardNo", request.getParameter("txtBankCardNo")) ;                    //银行帐号       （必要信息）
    tRequest.dicRequest.put("CertificateNo", request.getParameter("txtCertificateNo")) ;              //证件号码       （必要信息）
    tRequest.dicRequest.put("CertificateType", request.getParameter("ddlCertificateType")); //证件类型       （必要信息）
    tRequest.dicRequest.put("ResultNotifyURL", request.getParameter("txtResultNotifyURL"));          //身份验证回传网址（必要信息）
    tRequest.dicRequest.put("OrderDate" , request.getParameter("txtOrderDate"));              //验证请求日期 （必要信息 - YYYY/MM/DD）
    tRequest.dicRequest.put("OrderTime", request.getParameter("txtOrderTime"));              //验证请求时间 （必要信息 - HH:MM:SS）
    tRequest.dicRequest.put("PaymentLinkType" , request.getParameter("txtPaymentLinkType")); //交易渠道 （必要信息 - HH:MM:SS）

    //3、传送身份验证请求并取得支付网址
    //tRequest.postJSONRequest();
    JSON json = tRequest.postRequest();
    String ReturnCode = json.GetKeyValue("ReturnCode");
    String ErrorMessage = json.GetKeyValue("ErrorMessage");
    JSONObject returnjb = new JSONObject();
    try {
    if (ReturnCode.equals("0000"))
    {                       
        //4、身份验证请求提交成功，将客户端导向身份验证页面
    	
			returnjb.put("ReturnCode", ReturnCode);
		
    	returnjb.put("ErrorMessage", ErrorMessage);
    	returnjb.put("VerifyURL", json.GetKeyValue("VerifyURL"));
        
		//response.sendRedirect(json.GetKeyValue("VerifyURL"));
    }
    else
    {
        //5、身份验证请求提交失败，商户自定后续动作
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
public String staticIdentityVerify(HttpServletRequest request) {

	//1、生成身份验证请求对象
	QuickIdentityVerifyRequest tRequest = new QuickIdentityVerifyRequest();
	tRequest.dicRequest.put("CustomType",request.getParameter("txtCustomType"));    //客户类型 （必要信息）
	tRequest.dicRequest.put("ClientName",request.getParameter("txtClientName")); //客户姓名 （必要信息）
	tRequest.dicRequest.put("AccNo",request.getParameter("txtBankCardNo")) ;                    //银行帐号       （必要信息）
	tRequest.dicRequest.put("CertificateNo",request.getParameter("txtCertificateNo"));              //证件号码       （必要信息）
	tRequest.dicRequest.put("CertificateType",request.getParameter("ddlCertificateType")); //证件类型       （必要信息）
	tRequest.dicRequest.put("MobileNo",request.getParameter("txtPhoneNo"));          //手机号（必要信息）
	tRequest.dicRequest.put("CustomNo",request.getParameter("txtCustomNo"));              //网银客户号
	//2、传送身份验证请求并取得支付网址
	//tRequest.postJSONRequest();
	JSON json = tRequest.postRequest();
	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");
	JSONObject returnjb = new JSONObject();
	try {
	if (ReturnCode.equals("0000"))
	{
	    //3、身份验证请求提交成功，商户自定后续动作 
	
			returnjb.put("ReturnCode", ReturnCode);
		
		returnjb.put("ErrorMessage", ErrorMessage);
		returnjb.put("TrxType",  json.GetKeyValue("TrxType") );
	}
	else
	{
	    //4、身份验证请求提交失败，商户自定后续动作
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
public String submitPreAuthPayment(HttpServletRequest request) {
	//1、取得退货所需要的信息
	String tPayTypeId      = request.getParameter("txtPayTypeID");
	String tOrderDate      = request.getParameter("OrderDate");
	String tOrderTime      = request.getParameter("OrderTime");
	String tOrderNo      = request.getParameter("OrderNo"  );
	String tNewOrderNo      = request.getParameter("OriginalOrderNo"  );
	String tCurrencyCode      = request.getParameter("CurrencyCode"  );
	String tTrxAmountStr = request.getParameter("OrderAmount");
	String tFee = request.getParameter("Fee");
	//double  tTrxAmount    = Double.parseDouble(tTrxAmountStr);
	String tMerchantRemarks      = request.getParameter("MerchantRemarks"  );

	//2、生成退货请求对象
	PreAuthPaymentRequest tRequest = new PreAuthPaymentRequest();
	tRequest.dicOrder.put("OperateType", tPayTypeId);         //交易类型       （必要信息）
	tRequest.dicOrder.put("OrderDate", tOrderDate);           //交易日期       （必要信息）
	tRequest.dicOrder.put("OrderTime", tOrderTime);           //交易时间       （必要信息）
	tRequest.dicOrder.put("OrderNo", tOrderNo);               //交易编号       （必要信息）
	tRequest.dicOrder.put("OriginalOrderNo", tNewOrderNo);    //原交易编号       （必要信息） 
	tRequest.dicOrder.put("CurrencyCode", tCurrencyCode);     //币种       （必要信息）
	tRequest.dicOrder.put("OrderAmount", tTrxAmountStr);      //金额       （必要信息）
	tRequest.dicOrder.put("Fee", tFee);                       //手续费金额     
	tRequest.dicOrder.put("MerchantRemarks", tMerchantRemarks);//附言

	//3、传送退货请求并取得退货结果
	JSON json = tRequest.postRequest();
	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");

	//4、判断退货结果状态，进行后续操作
	JSONObject returnjb = new JSONObject();
	try {
	if (ReturnCode.equals("0000")) {
	       //5、处理成功
			
				returnjb.put("ReturnCode", ReturnCode);
			
			returnjb.put("ErrorMessage", ErrorMessage);
			returnjb.put("OrderNo",  json.GetKeyValue("OrderNo"));
			returnjb.put("OrderAmount",  json.GetKeyValue("OrderAmount"));
			returnjb.put("OriginalOrderNo",  json.GetKeyValue("OriginalOrderNo"));
			returnjb.put("BatchId",  json.GetKeyValue("BatchId"));
			returnjb.put("VouchNo",  json.GetKeyValue("VouchNo"));
			returnjb.put("HostDate",  json.GetKeyValue("HostDate"));
			returnjb.put("HostTime",  json.GetKeyValue("HostTime"));
			returnjb.put("iRspRef",  json.GetKeyValue("iRspRef"));
			
	}
	else {
	       //6、处理失败
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
public String merchantGetReceipt(HttpServletRequest request) {
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

	//1、生成交易查询对象
	String subMerchantNo = request.getParameter("SubMerchantNo");
	String orderNo = request.getParameter("OrderNo");


	GetReceiptRequest tReceiptRequest = new GetReceiptRequest();
	tReceiptRequest.receiptRequest.put("SubMerchantNo", subMerchantNo);    //设定交易类型
	tReceiptRequest.receiptRequest.put("OrderNo", orderNo);    //设定订单编号 （必要信息）

	//如果需要专线地址，调用此方法：
	//tQueryRequest.setConnectionFlag(true);
	JSON json = tReceiptRequest.postRequest();
	//JSON json = tQueryRequest.extendPostRequest(1);

	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");
	JSONObject returnjb = new JSONObject();
	try {
	if (ReturnCode.equals("0000"))
	{
	
			returnjb.put("ReturnCode", ReturnCode);
	
		returnjb.put("ErrorMessage", ErrorMessage);
	    //4、获取结果信息
	    String receiptStr = json.GetKeyValue("ImageCode");
	    //传输图片这种该怎么搞
	    byte[] imageBytes = tReceiptRequest.decompressFromBase64String(receiptStr);
	    returnjb.put("imageBytes", imageBytes);
			/*
			 * response.setHeader("Content-Type","application/octet-stream");
			 * response.setHeader("Content-Disposition","attachment;filename=" + orderNo +
			 * ".bmp"); //ServletOutputStream outReceipt = response.getOutputStream();
			 * response.getOutputStream().write(imageBytes);
			 * response.getOutputStream().flush(); response.getOutputStream().close();
			 */
	    
	    //为防止抛出IllegalStateException,增加下面的两行代码
	    //Servlet中规定response.getWriter()和response.getOutputStream()生成的对象，不能同时调用
			/*
			 * out.clear(); out = pageContext.pushBody();
			 */
	}
	else
	{
	    //5、商户请求下载电子回单失败
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
public String authenMerchantQuery(HttpServletRequest request) {
	//生成鉴权查询对象
	String TransferNo = request.getParameter("TransferNo");

	AuthenMerchantQueryRequest authenQueryRequest = new AuthenMerchantQueryRequest();
	authenQueryRequest.queryRequest.put("TransferNo", request.getParameter("TransferNo"));
	JSON json = authenQueryRequest.postRequest();

	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");
	String Status = json.GetKeyValue("Status");
	JSONObject returnjb = new JSONObject();
	try {
		returnjb.put("ReturnCode", ReturnCode);
	
	returnjb.put("ErrorMessage", ErrorMessage);
	returnjb.put("Status", Status);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}

@Override
public String transferOutQuery(HttpServletRequest request) {
	//生成外转查询对象
	String TransferNo = request.getParameter("TransferNo");

	TransferOutQueryRequest transferQueryRequest = new TransferOutQueryRequest();
	transferQueryRequest.queryRequest.put("TransferNo", request.getParameter("TransferNo"));
	JSON json = transferQueryRequest.postRequest();

	String ReturnCode = json.GetKeyValue("ReturnCode");
	String ErrorMessage = json.GetKeyValue("ErrorMessage");
	String Status = json.GetKeyValue("Status");

	JSONObject returnjb = new JSONObject();
	try {
		returnjb.put("ReturnCode", ReturnCode);
	
	returnjb.put("ErrorMessage", ErrorMessage);
	returnjb.put("Status", Status);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}
}
