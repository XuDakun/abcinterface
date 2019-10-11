package com.wfhy.abcinterface.service.impl;

import com.abc.pay.client.Constants;
import com.abc.pay.client.JSON;
import com.abc.pay.client.MerchantConfig;
import com.abc.pay.client.MerchantPara;
import com.abc.pay.client.TrxException;
import com.abc.pay.client.ebus.AgentBatchPaymentQueryRequest;
import com.abc.pay.client.ebus.AgentBatchPaymentRequest;
import com.abc.pay.client.ebus.AgentPaymentRequest;
import com.abc.pay.client.ebus.PaymentRequest;
import com.abc.pay.client.ebus.QuickPaymentRequest;
import com.wfhy.abcinterface.service.AgentService;
import com.wfhy.abcinterface.service.PayService;
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
public class AgentServiceImpl  implements AgentService {

	@Override
	public String agentPayment(HttpServletRequest request) {
		//1、生成单笔授权扣款请求对象
        AgentPaymentRequest tRequest = new AgentPaymentRequest();
        //2、生成定单订单对象，并将订单明细加入定单中
        tRequest.dicRequest.put("OrderDate", request.getParameter("OrderDate"));                   //设定订单日期 （必要信息 - YYYY/MM/DD）
        tRequest.dicRequest.put("OrderTime", request.getParameter("OrderTime"));                   //设定订单时间 （必要信息 - HH:MM:SS）
        tRequest.dicRequest.put("OrderNo", request.getParameter("OrderNo"));                       //设定订单编号 （必要信息）
        tRequest.dicRequest.put("AgentSignNo", request.getParameter("AgentSignNo"));                       //设定授权支付协议号 （必要信息）
        tRequest.dicRequest.put("CardNo", request.getParameter("CardNo"));                       //设定账号
        tRequest.dicRequest.put("CurrencyCode", request.getParameter("CurrencyCode"));    //设定交易币种 （必要信息）
        tRequest.dicRequest.put("Amount", request.getParameter("Amount"));    //设定交易金额 （必要信息）
        tRequest.dicRequest.put("ReceiverAddress", request.getParameter("ReceiverAddress")); //设定手续费金额
        tRequest.dicRequest.put("Fee", request.getParameter("Fee")); //设定手续费金额
        tRequest.dicRequest.put("CertificateNo", request.getParameter("CertificateNo"));             //证件号码
        tRequest.dicRequest.put("InstallmentMark", request.getParameter("InstallmentMark"));  //分期标识（必要信息）
        if (request.getParameter("InstallmentMark").toString().equals("1"))
        {
            tRequest.dicRequest.put("InstallmentCode", request.getParameter("InstallmentCode"));    //设定分期代码
            tRequest.dicRequest.put("InstallmentNum", request.getParameter("InstallmentNum"));    //设定分期期数
        }
        tRequest.dicRequest.put("CommodityType", request.getParameter("CommodityType"));   //设置商品种类 （必要信息）
        tRequest.dicRequest.put("PaymentLinkType", request.getParameter("PaymentLinkType"));      //设定支付接入方式 （必要信息）
        tRequest.dicRequest.put("BuyIP", request.getParameter("BuyIP")); 
        tRequest.dicRequest.put("ExpiredDate", request.getParameter("ExpiredDate"));//设定订单保存时间
        tRequest.dicRequest.put("ReceiveAccount", request.getParameter("ReceiveAccount"));    //设定收款方账号
        tRequest.dicRequest.put("ReceiveAccName", request.getParameter("ReceiveAccName"));    //设定收款方户名
        tRequest.dicRequest.put("MerchantRemarks", request.getParameter("MerchantRemarks"));    //设定附言
        tRequest.dicRequest.put("IsBreakAccount", request.getParameter("IsBreakAccount"));    //设定交易是否分账
        tRequest.dicRequest.put("SplitAccTemplate", request.getParameter("SplitAccTemplate"));      //分账模版编号

      //2、订单明细
        JSONObject returnjb = new JSONObject();
        try {
        String items =  request.getParameter("Items");
        JSONObject json  = null;
        LinkedHashMap orderitem = null;
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
                tRequest.orderitems.put(i+1, orderitem);
            }
		
		//添加分账信息
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

        //3、传送单笔授权扣款请求
        JSON json1 = tRequest.postRequest();

		String ReturnCode = json1.GetKeyValue("ReturnCode");
		String ErrorMessage = json1.GetKeyValue("ErrorMessage");
		
		
        if (ReturnCode.equals("0000"))
        {
            //4、单笔授权扣款请求提交成功
        	returnjb.put("ReturnCode", ReturnCode);
        	returnjb.put("ErrorMessage", ErrorMessage);
        	returnjb.put("OrderNo",  json1.GetKeyValue("OrderNo"));
        	returnjb.put("TrxType", json1.GetKeyValue("TrxType"));
            
        }
        else
        {
            //5、单笔授权扣款请求提交失败，商户自定后续动作
        	
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
	public String agentBatchPayment(HttpServletRequest request) {
		//1、取得委托扣款批量需要的信息
		String batchNo = request.getParameter("BatchNo");
		//String batchDate = request.getParameter("BatchDate");
		String agentCount = request.getParameter("AgentCount");
		String agentAmount = request.getParameter("AgentAmount");
		
		//String orderno_arr[] = null;
		//String orderamount_arr[] = null;
		//String expireddate_arr[] = null;
		//String certificateno_arr[] = null;
		//String contractid_arr[] = null;
		//String productid_arr[] = null;
		//String productname_arr[] = null;
		//String productnum_arr[] = null;
		String seqno_arr[] = null;
	    String orderno_arr[] = null;
	    String agentsignno_arr[] = null;
	    String cardno_arr[] = null;
	    String orderamount_arr[] = null;
	    String receiveraddress_arr[] = null;
	    String fee_arr[] = null;
	    String certificateno_arr[] = null;
	    String installmentmark_arr[] = null;
	    String installmentcode_arr[] = null;
	    String installmentnum_arr[] = null;
	    String commoditytype_arr[] = null;
	    String submername_arr[] = null;
	    String submerid_arr[] = null;
	    String submermcc_arr[] = null;
	    String submerchantremarks_arr[] = null;
	    String productid_arr[] = null;
	    String productname_arr[] = null;
	    String unitprice_arr[] = null;
	    String qty_arr[] = null;
	    String productremarks_arr[] = null;
	    String producttype_arr[] = null;
	    String productdiscount_arr[] = null;
	    String productexpireddate_arr[] = null;
	    String buyip_arr[] = null;
	    String remark_arr[] = null;
	    String IsBreakAccount_arr[] = null;
	    String SplitAccTemplate_arr[] = null;

		int iBatchSize = Integer.parseInt(agentCount);
		
		if (iBatchSize == 1) {
			String seqno = request.getParameter("SeqNo");
	        String orderno = request.getParameter("OrderNo");
	        String agentsignno = request.getParameter("AgentSignNo");
	        String cardno = request.getParameter("CardNo");
	        String orderamount = request.getParameter("OrderAmount");
	        String receiveraddress = request.getParameter("ReceiverAddress");
	        String fee = request.getParameter("Fee");
	        String certificateno = request.getParameter("CertificateNo");
	        String installmentmark = request.getParameter("InstallmentMark");
	        String installmentcode = request.getParameter("InstallmentCode");
	        String installmentnum = request.getParameter("InstallmentNum");
	        String commoditytype = request.getParameter("CommodityType");
	        String submername = request.getParameter("SubMerName");
	        String submerid = request.getParameter("SubMerId");
	        String submermcc = request.getParameter("SubMerMCC");
	        String submerchantremarks = request.getParameter("SubMerchantRemarks");
	        String productid = request.getParameter("ProductID");
	        String productname = request.getParameter("ProductName");
	        String unitprice = request.getParameter("UnitPrice");
	        String qty = request.getParameter("Qty");
	        String productremarks = request.getParameter("ProductRemarks");
	        String producttype = request.getParameter("ProductType");
	        String productdiscount = request.getParameter("ProductDiscount");
	        String productexpireddate = request.getParameter("ProductExpiredDate");
	        String buyip = request.getParameter("BuyIP");
	        String remark = request.getParameter("Remark");
			String IsBreakAccount = request.getParameter("IsBreakAccount");
			String SplitAccTemplate = request.getParameter("SplitAccTemplate"); 
			
			seqno_arr = new String[] { seqno };
			orderno_arr = new String[] { orderno };
			agentsignno_arr = new String[] { agentsignno };
			cardno_arr = new String[] { cardno };
			orderamount_arr = new String[] { orderamount };
			receiveraddress_arr = new String[] { receiveraddress };
			fee_arr = new String[] { fee };
			certificateno_arr = new String[] { certificateno };
			installmentmark_arr = new String[] { installmentmark };
			installmentcode_arr = new String[] { installmentcode };
			installmentnum_arr = new String[] { installmentnum };
			commoditytype_arr = new String[] { commoditytype };
			submername_arr = new String[] { submername };
			submerid_arr = new String[] { submerid };
			submermcc_arr = new String[] { submermcc };
			submerchantremarks_arr = new String[] { submerchantremarks };
			productid_arr = new String[] { productid };
			productname_arr = new String[] { productname };
			unitprice_arr = new String[] { unitprice };
			qty_arr = new String[] { qty };
			productremarks_arr = new String[] { productremarks };
			producttype_arr = new String[] { producttype };
			productdiscount_arr = new String[] { productdiscount };
			productexpireddate_arr = new String[] { productexpireddate };
			buyip_arr = new String[] { buyip };
			remark_arr = new String[] { remark };		
			IsBreakAccount_arr = new String[] { IsBreakAccount };
	    	SplitAccTemplate_arr = new String[] { SplitAccTemplate };;

		} else {
	        
			seqno_arr = request.getParameterValues("SeqNo");
			orderno_arr = request.getParameterValues("OrderNo");
			agentsignno_arr = request.getParameterValues("AgentSignNo");
			cardno_arr = request.getParameterValues("CardNo");
			orderamount_arr = request.getParameterValues("OrderAmount");
			receiveraddress_arr = request.getParameterValues("ReceiverAddress");
			fee_arr = request.getParameterValues("Fee");
			certificateno_arr = request.getParameterValues("CertificateNo");
			installmentmark_arr = request.getParameterValues("InstallmentMark");
			installmentcode_arr = request.getParameterValues("InstallmentCode");
			installmentnum_arr = request.getParameterValues("InstallmentNum");
			commoditytype_arr = request.getParameterValues("CommodityType");
			submername_arr = request.getParameterValues("SubMerName");
			submerid_arr = request.getParameterValues("SubMerId");
			submermcc_arr = request.getParameterValues("SubMerMCC");
			submerchantremarks_arr = request.getParameterValues("SubMerchantRemarks");
			productid_arr = request.getParameterValues("ProductID");
			productname_arr = request.getParameterValues("ProductName");
			unitprice_arr = request.getParameterValues("UnitPrice");
			qty_arr = request.getParameterValues("Qty");
			productremarks_arr = request.getParameterValues("ProductRemarks");
			producttype_arr = request.getParameterValues("ProductType");
			productdiscount_arr = request.getParameterValues("ProductDiscount");
			productexpireddate_arr = request.getParameterValues("ProductExpiredDate");
			buyip_arr = request.getParameterValues("BuyIP");
			remark_arr = request.getParameterValues("Remark");	
			IsBreakAccount_arr = request.getParameterValues("IsBreakAccount");	
			SplitAccTemplate_arr = request.getParameterValues("SplitAccTemplate");		
			
		}
		//2、生成委托扣款批量请求对象
		AgentBatchPaymentRequest tRequest = new AgentBatchPaymentRequest();
		
		 tRequest.agentBatch.put("BatchNo", batchNo);
		 tRequest.agentBatch.put("BatchDate", request.getParameter("BatchDate"));
		 tRequest.agentBatch.put("BatchTime", request.getParameter("BatchTime"));
		 tRequest.agentBatch.put("AgentCount", agentCount);
		 tRequest.agentBatch.put("AgentAmount", agentAmount);

		 tRequest.dicRequest.put("ReceiveAccount", request.getParameter("ReceiveAccount"));
		 tRequest.dicRequest.put("ReceiveAccName", request.getParameter("ReceiveAccName"));
		 tRequest.dicRequest.put("CurrencyCode", request.getParameter("CurrencyCode"));
		 //tRequest.dicRequest.put("IsBreakAccount", request.getParameter("IsBreakAccount"));
		 //tRequest.dicRequest.put("SplitAccTemplate", request.getParameter("SplitAccTemplate"));
		 
		 LinkedHashMap item = null;
		 BigDecimal sum = BigDecimal.ZERO;    
		 for (int i = 0; i < orderno_arr.length; i++)
		 {
		     item = new LinkedHashMap();
		     item.put("SeqNo", seqno_arr[i]);
		     item.put("OrderNo", orderno_arr[i]);
		     item.put("AgentSignNo", agentsignno_arr[i]);
		     item.put("CardNo", cardno_arr[i]);
		     item.put("OrderAmount", orderamount_arr[i]);
		     item.put("ReceiverAddress", receiveraddress_arr[i]);
		     item.put("Fee", fee_arr[i]);
		     item.put("CertificateNo", certificateno_arr[i]);
		     item.put("InstallmentMark", installmentmark_arr[i]);
		     if (installmentmark_arr[i].toString().equals("1"))
		     {
			 	 item.put("InstallmentCode", installmentcode_arr[i]);
			 	 item.put("InstallmentNum", installmentnum_arr[i]);
		     }
		     item.put("CommodityType", commoditytype_arr[i]);
		     item.put("SubMerName", submername_arr[i]);
		     item.put("SubMerId", submerid_arr[i]);
		     item.put("SubMerMCC", submermcc_arr[i]);
		     item.put("SubMerchantRemarks", submerchantremarks_arr[i]);
		     item.put("ProductID", productid_arr[i]);
		     item.put("ProductName", productname_arr[i]);
		     item.put("UnitPrice", unitprice_arr[i]);
		     item.put("Qty", qty_arr[i]);
		     item.put("ProductRemarks", productremarks_arr[i]);
		     item.put("ProductType", producttype_arr[i]);
		     item.put("ProductDiscount", productdiscount_arr[i]);
		     item.put("ProductExpiredDate", productexpireddate_arr[i]);
		     item.put("BuyIP", buyip_arr[i]);
		     item.put("Remark", remark_arr[i]);
		     item.put("IsBreakAccount", IsBreakAccount_arr[i]);
		     item.put("SplitAccTemplate", SplitAccTemplate_arr[i]);
		     tRequest.items.put(i, item);

	         BigDecimal bd = new BigDecimal(orderamount_arr[i].toString());
			 sum = sum.add(bd);  
		 }
		 //此处必须设定iSumAmount属性
	     tRequest.iSumAmount = sum.doubleValue();

		 //3、传送批量授权扣款请求
		 JSON json = tRequest.postRequest();
		 String ReturnCode = json.GetKeyValue("ReturnCode");
		 String ErrorMessage = json.GetKeyValue("ErrorMessage");
		 JSONObject returnjb = new JSONObject();
		  try {
		 if (ReturnCode.equals("0000"))
		 {
		     //4、批量授权扣款请求提交成功
		   
				returnjb.put("ReturnCode", ReturnCode);
			
		     returnjb.put("ErrorMessage", ErrorMessage);
		     returnjb.put("TrxType",  json.GetKeyValue("TrxType") );
		     returnjb.put("MerchantNo",  json.GetKeyValue("MerchantNo") );
		     returnjb.put("SendTime",  json.GetKeyValue("SendTime") );
		 }
		 else
		 {
		     //5、批量授权扣款请求提交失败，商户自定后续动作
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
	public String agentBatchPaymentQuery(HttpServletRequest request) {
		//1、生成批量授权扣款查询请求对象
		AgentBatchPaymentQueryRequest tRequest=new AgentBatchPaymentQueryRequest();
		tRequest.agentBatch.put("BatchNo",request.getParameter("BatchNo")); //请求批次号       （必要信息）
		tRequest.agentBatch.put("BatchDate",request.getParameter("BatchDate")); //请求日期      YYYY/MM/DD       （必要信息）

		//2传送交易请求
		JSON json = tRequest.postRequest();

		//3、判断结果状态，进行后续操作
		String ReturnCode = json.GetKeyValue("ReturnCode");
		String ErrorMessage = json.GetKeyValue("ErrorMessage");
		JSONObject returnjb = new JSONObject();
		try {
		if (ReturnCode.equals("0000"))
		{
		   //4、查询成功
			
				returnjb.put("ReturnCode", json.GetKeyValue("ReturnCode") );
			
			returnjb.put("ErrorMessage", json.GetKeyValue("ErrorMessage") );
			returnjb.put("BatchNo", json.GetKeyValue("BatchNo") );
			returnjb.put("BatchDate", json.GetKeyValue("BatchDate") );
			returnjb.put("BatchTime", json.GetKeyValue("BatchTime") );
			returnjb.put("AgentAmount", json.GetKeyValue("AgentAmount") );
			returnjb.put("AgentCount", json.GetKeyValue("AgentCount") );
			returnjb.put("BatchStatus", json.GetKeyValue("BatchStatus") );
			returnjb.put("BatchStatusZH", json.GetKeyValue("BatchStatusZH") );
			returnjb.put("CurrencyCode", json.GetKeyValue("CurrencyCode") );
			returnjb.put("SuccessAmount", json.GetKeyValue("SuccessAmount") );
			returnjb.put("SuccessCount", json.GetKeyValue("SuccessCount") );
			returnjb.put("FailedAmount", json.GetKeyValue("FailedAmount") );
			returnjb.put("FailedCount", json.GetKeyValue("FailedCount") );
			
		    
		   	LinkedHashMap hashMap = new LinkedHashMap();
		    hashMap = json.GetArrayValue("AgentBatchDetail");
		    //5、取得批量授权扣款明细
		    if(hashMap.size() == 0)
		    {
		    	returnjb.put("AgentBatchDetail", "批量授权扣款明细为空" );
		    }
		    else
		    {               
		        Iterator iter = hashMap.entrySet().iterator();
		        while (iter.hasNext()) {
		            Map.Entry entry = (Map.Entry) iter.next();
		            Hashtable val = (Hashtable)entry.getValue();      
		            returnjb.put("SeqNo",(String)val.get("SeqNo"));
		            returnjb.put("OrderNo",(String)val.get("OrderNo") );
		            returnjb.put("OrderAmount",(String)val.get("OrderAmount") );
		            returnjb.put("AgentSignNo", (String)val.get("AgentSignNo"));
		            returnjb.put("OrderStatus", (String)val.get("OrderStatus") );
		            returnjb.put("OrderStatusZH", (String)val.get("OrderStatusZH") );
			        }    
		       }
		       
		}
		else {
		   //6、批量结果查询失败
			returnjb.put("ReturnCode", ReturnCode );
			returnjb.put("ErrorMessage",ErrorMessage );
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			return returnjb.toString();
		}
	}
  
}
