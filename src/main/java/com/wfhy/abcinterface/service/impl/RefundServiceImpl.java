package com.wfhy.abcinterface.service.impl;

import com.abc.pay.client.Constants;
import com.abc.pay.client.JSON;
import com.abc.pay.client.MerchantConfig;
import com.abc.pay.client.MerchantPara;
import com.abc.pay.client.TrxException;
import com.abc.pay.client.ebus.BatchRefundRequest;
import com.abc.pay.client.ebus.PaymentRequest;
import com.abc.pay.client.ebus.QueryBatchRequest;
import com.abc.pay.client.ebus.QuickPaymentRequest;
import com.abc.pay.client.ebus.RefundRequest;
import com.wfhy.abcinterface.service.PayService;
import com.wfhy.abcinterface.service.RefundService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RefundServiceImpl  implements RefundService {
  

@Override
public String MerchantRefund(HttpServletRequest request) {
	 //1、生成退款请求对象
    RefundRequest tRequest = new RefundRequest();
    tRequest.dicRequest.put("OrderDate", request.getParameter("txtOrderDate"));  //订单日期（必要信息）
    tRequest.dicRequest.put("OrderTime", request.getParameter("txtOrderTime")); //订单时间（必要信息）
    //tRequest.dicRequest.put("MerRefundAccountNo", request.getParameter("txtMerRefundAccountNo"));  //商户退款账号
    //tRequest.dicRequest.put("MerRefundAccountName", request.getParameter("txtMerRefundAccountName")); //商户退款名
    tRequest.dicRequest.put("OrderNo", request.getParameter("txtOrderNo")); //原交易编号（必要信息）
    tRequest.dicRequest.put("NewOrderNo", request.getParameter("txtNewOrderNo")); //交易编号（必要信息）
    tRequest.dicRequest.put("CurrencyCode", request.getParameter("txtCurrencyCode")); //交易币种（必要信息）
    tRequest.dicRequest.put("TrxAmount", request.getParameter("txtTrxAmount")); //退货金额 （必要信息）
    tRequest.dicRequest.put("RefundType", request.getParameter("txtRefundType")); //退货类型 （非必要信息）              
    tRequest.dicRequest.put("MerchantRemarks", request.getParameter("txtMerchantRemarks"));  //附言
	//如果需要专线地址，调用此方法：
	//tRequest.setConnectionFlag(true);
	
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
	         
	         tRequest.dicSplitAccInfo.put(i+1, map);
	     }
	}  

    //3、传送退款请求并取得退货结果
    JSON json = tRequest.postRequest();

    //4、判断退款结果状态，进行后续操作
    StringBuilder strMessage = new StringBuilder("");
    String ReturnCode = json.GetKeyValue("ReturnCode");
    String ErrorMessage = json.GetKeyValue("ErrorMessage");
    JSONObject returnjb = new JSONObject();
    try {
    if (ReturnCode.equals("0000"))
    {
        //5、退款成功/退款受理成功
    	
			returnjb.put("ReturnCode", ReturnCode);
		
    	returnjb.put("ErrorMessage", ErrorMessage);
    	returnjb.put("OrderNo",  json.GetKeyValue("OrderNo"));
    	returnjb.put("NewOrderNo",  json.GetKeyValue("NewOrderNo"));
    	returnjb.put("TrxAmount",  json.GetKeyValue("TrxAmount"));
    	returnjb.put("BatchNo",  json.GetKeyValue("BatchNo"));
    	returnjb.put("VoucherNo",  json.GetKeyValue("VoucherNo"));
    	returnjb.put("HostDate",  json.GetKeyValue("HostDate"));
    	returnjb.put("HostTime",  json.GetKeyValue("HostTime"));
    	returnjb.put("iRspRef",  json.GetKeyValue("iRspRef"));
    }       
    else
    {
        //6、退款失败
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
public String MerchantBatchRefund(HttpServletRequest request) {
	 StringBuilder strMessage = new StringBuilder("");
     //验证输入信息并取得退款所需要的信息
     String totalCount =  request.getParameter("TotalCount");
      
     String[]orderno_arr = null;
     String[]neworderno_arr = null;
     String[]currencycode_arr = null;
     String[]orderamount_arr = null;
     String[]remark_arr = null;
      
     int batchSize = Integer.parseInt(totalCount);
     if(batchSize == 1){
	         String orderno = request.getParameter("txtOrderNo");
	         String neworderno = request.getParameter("txtNewOrderNo");
	         String currencycode = request.getParameter("txtCurrencyCode");
	         String orderamount = request.getParameter("txtRefundAmount");
	         String remark = request.getParameter("txtRemark");
      	 orderno_arr = new String[] { orderno };
      	 neworderno_arr = new String[] { neworderno };
      	 currencycode_arr = new String[] { currencycode };
      	 orderamount_arr = new String[] { orderamount };
      	 remark_arr = new String[] { remark };
     }
     else {
      	orderno_arr = request.getParameterValues("txtOrderNo");
      	neworderno_arr = request.getParameterValues("txtNewOrderNo");
      	currencycode_arr = request.getParameterValues("txtCurrencyCode");
      	orderamount_arr = request.getParameterValues("txtRefundAmount");
      	remark_arr = request.getParameterValues("txtRemark");
     }

     //1、生成批量退款请求对象
     BatchRefundRequest tBatchRefundRequest = new BatchRefundRequest();
     //取得明细项 
     LinkedHashMap map = null;   
     BigDecimal sum = BigDecimal.ZERO;    
     for (int i = 0; i < orderno_arr.length; i++)
     {
         map = new LinkedHashMap();
         map.put("SeqNo", String.valueOf(i + 1));
         map.put("OrderNo",orderno_arr[i]);
         map.put("NewOrderNo",neworderno_arr[i]);
         map.put("CurrencyCode",currencycode_arr[i]);
         map.put("RefundAmount",orderamount_arr[i]);
         map.put("Remark",remark_arr[i]);
         tBatchRefundRequest.dic.put(i+1, map);
         //此处必须使用BigDecimal，否则会丢精度            
         BigDecimal bd = new BigDecimal(orderamount_arr[i].toString());
			sum = sum.add(bd);
     }
     //此处必须设定iSumAmount属性
     tBatchRefundRequest.iSumAmount = sum.doubleValue();

     tBatchRefundRequest.batchRefundRequest.put("BatchNo",request.getParameter("txtBatchNo")); //批量编号  （必要信息）
     tBatchRefundRequest.batchRefundRequest.put("BatchDate",request.getParameter("txtBatchDate"));  //订单日期  （必要信息）
     tBatchRefundRequest.batchRefundRequest.put("BatchTime",request.getParameter("txtBatchTime")); //订单时间  （必要信息）
     tBatchRefundRequest.batchRefundRequest.put("MerRefundAccountNo",request.getParameter("txtMerRefundAccountNo"));  //商户退款账号
     tBatchRefundRequest.batchRefundRequest.put("MerRefundAccountName",request.getParameter("txtMerRefundAccountName")); //商户退款名
     tBatchRefundRequest.batchRefundRequest.put("TotalCount",request.getParameter("TotalCount"));  //总笔数  （必要信息）
     tBatchRefundRequest.batchRefundRequest.put("TotalAmount",request.getParameter("TotalAmount"));  //总金额 （必要信息）

     //2、传送批量退款请求并取得结果
     JSON json = tBatchRefundRequest.postRequest();

     //3、判断批量退款结果状态，进行后续操作
     String ReturnCode = json.GetKeyValue("ReturnCode");
     String ErrorMessage = json.GetKeyValue("ErrorMessage");
     JSONObject returnjb = new JSONObject();
     try {
     if (ReturnCode.equals("0000"))
     {
         //4、批量退款成功
    	 returnjb.put("ReturnCode", ReturnCode);
    	
			returnjb.put("ReturnCode", ErrorMessage);
		
    	 returnjb.put("TrxType", json.GetKeyValue("TrxType") );
    	 returnjb.put("TotalCount", json.GetKeyValue("TotalCount") );
    	 returnjb.put("TotalAmount", json.GetKeyValue("TotalAmount") );
    	 returnjb.put("SerialNumber", json.GetKeyValue("SerialNumber") );
    	 returnjb.put("HostDate", json.GetKeyValue("HostDate") );
    	 returnjb.put("HostTime", json.GetKeyValue("HostTime ") );
     }
     else
     {
         //5、批量退款失败
    	 returnjb.put("ReturnCode", ReturnCode);
    	 returnjb.put("ReturnCode", ErrorMessage);
     }
     } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			return returnjb.toString();
		}
}

@Override
public String MerchantBatchRefundQuery(HttpServletRequest request) {
	 //1、生成退款批量结果查询请求对象
    QueryBatchRequest tQueryBatchRequest = new QueryBatchRequest();
    tQueryBatchRequest.queryBatchRequest.put("BatchDate",request.getParameter("txtBatchDate"));  //订单日期（必要信息）
    tQueryBatchRequest.queryBatchRequest.put("BatchTime",request.getParameter("txtBatchTime")); //订单时间（必要信息）
    tQueryBatchRequest.queryBatchRequest.put("SerialNumber",request.getParameter("txtSerialNumber")); //设定退款批量结果查询请求的流水号（必要信息）

    //2、传送退款批量结果查询请求并取得结果
    JSON json = tQueryBatchRequest.postRequest();

    //3、判断退款批量结果查询状态，进行后续操作
    String ReturnCode = json.GetKeyValue("ReturnCode");
    String ErrorMessage = json.GetKeyValue("ErrorMessage");
    JSONObject returnjb = new JSONObject();
    try {
    if (ReturnCode.equals("0000"))
    {
        //4、生成批量对象
    	
			returnjb.put("ReturnCode", ReturnCode);
		
    	returnjb.put("ErrorMessage", ErrorMessage);
    	returnjb.put("BatchDate", json.GetKeyValue("BatchDate").toString() );
    	returnjb.put("BatchTime", json.GetKeyValue("BatchTime").toString() );
    	returnjb.put("SerialNumber", json.GetKeyValue("SerialNumber").toString() );
    	returnjb.put("BatchStatus", json.GetKeyValue("BatchStatus").toString() );
    	returnjb.put("MerRefundAccountNo", json.GetKeyValue("MerRefundAccountNo").toString() );
    	returnjb.put("MerRefundAccountName", json.GetKeyValue("MerRefundAccountName").toString() );
    	returnjb.put("RefundAmount", json.GetKeyValue("RefundAmount").toString() );
    	returnjb.put("RefundCount", json.GetKeyValue("RefundCount").toString() );
    	returnjb.put("SuccessAmount", json.GetKeyValue("SuccessAmount").toString() );
    	returnjb.put("SuccessCount", json.GetKeyValue("SuccessCount").toString() );
    	returnjb.put("FailedAmount", json.GetKeyValue("FailedAmount").toString() );
    	returnjb.put("FailedCount", json.GetKeyValue("FailedCount").toString() );


        //5、取得订单明细
        LinkedHashMap tOrders = json.GetArrayValue("Order");
        if (tOrders.size() <= 0)
        {
        	returnjb.put("Order", "明细为空！");

        }
        else 
        {
            Iterator iter = tOrders.entrySet().iterator();
	        while (iter.hasNext()) {
	            Map.Entry entry = (Map.Entry) iter.next();
	            Hashtable val = (Hashtable)entry.getValue(); 
	            returnjb.put("OriginalOrderNo",(String)val.get("OriginalOrderNo"));
	            returnjb.put("RefundOrderNo",(String)val.get("RefundOrderNo"));
	            returnjb.put("CurrencyCode",(String)val.get("CurrencyCode"));
	            returnjb.put("RefundAmountCell",(String)val.get("RefundAmountCell"));
	            returnjb.put("OrderStatus",(String)val.get("OrderStatus"));
	            returnjb.put("Remark",(String)val.get("Remark"));
	            
		    }  	            
       }
	}
    else
    {
        //6、退款批量结果查询失败
    	returnjb.put("ReturnCode", ReturnCode);
    	returnjb.put("ErrorMessage", ErrorMessage);
     } 
    } catch (JSONException e) {
		e.printStackTrace();
	}finally {
		return returnjb.toString();
	}
}
}
