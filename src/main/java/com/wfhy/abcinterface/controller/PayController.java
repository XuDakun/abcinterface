package com.wfhy.abcinterface.controller;

import com.abc.pay.client.JSON;
import com.abc.pay.client.ebus.QuickIdentityVerifyRequest;
import com.wfhy.abcinterface.service.PayService;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("pay")
@PropertySource("classpath:log4j.properties")
public class PayController {
    @Autowired
    private PayService payService;
    
 
    Logger logger =Logger.getLogger(PayController.class); 
    @RequestMapping("Test")
    public String test() {
    	logger.info("进入测试方法");
    	logger.info("测试方法返回参数:hi");
    	return "hi!";
    }
    @RequestMapping("MerchantPayment")
    public String merchantPayment(HttpServletRequest request, HttpServletResponse response) {


            return payService.merchantPayment(request);

     
    }

    @RequestMapping("MerchantPaymentIE")
    public String merchantPaymentIE(HttpServletRequest request, HttpServletResponse response) {

        
            return payService.merchantPaymentIE(request);
    
    }

    @RequestMapping("MerchantQuickPaymentReq")
    public String merchantQuickPaymentReq(HttpServletRequest request, HttpServletResponse response) {

            return payService.merchantQuickPaymentReq(request);
   
    }  
    /**
     * MerchantQueryOrder 交易查询
     */
    @RequestMapping("MerchantQueryOrder")
    public String MerchantQueryOrder(HttpServletRequest request, HttpServletResponse response) {

            return payService.merchantQueryOrder(request);
   
    }  
    /**
     * MerchantQueryTrnxRecords 交易流水查询
     */
    @RequestMapping("MerchantQueryTrnxRecords")
    public String MerchantQueryTrnxRecords(HttpServletRequest request, HttpServletResponse response) {
            return payService.merchantQueryTrnxRecords(request);
    }  
    /**
     * MerchantTrxSettleQuery  交易对账单下载
     */
    @RequestMapping("MerchantTrxSettleQuery")
    public String MerchantTrxSettleQuery(HttpServletRequest request, HttpServletResponse response) {
            return payService.merchantTrxSettleQuery(request);
    }  
    /**
     * MerchantTrxSettlePlatForm 交易对账单下载
     */
    @RequestMapping("MerchantTrxSettlePlatForm")
    public String MerchantTrxSettlePlatForm(HttpServletRequest request, HttpServletResponse response) {
            return payService.merchantTrxSettlePlatForm(request);
    }  
    /**
     * IdentityVerify 身份验证
     */
    @RequestMapping("IdentityVerify")
    public String IdentityVerify(HttpServletRequest request, HttpServletResponse response) {
            return payService.identityVerify(request);
    }  
    /**
     * StaticIdentityVerify 身份验证(非页面跳转)
     */
    @RequestMapping("StaticIdentityVerify")
    public String StaticIdentityVerify(HttpServletRequest request, HttpServletResponse response) {
            return payService.staticIdentityVerify(request);
    }  
    
    /**
     * SubmitPreAuthPayment 预授权确认/取消
     */
    @RequestMapping("SubmitPreAuthPayment")
    public String SubmitPreAuthPayment(HttpServletRequest request, HttpServletResponse response) {
            return payService.submitPreAuthPayment(request);
    }  
    /**
     * MerchantGetReceipt 电子回单下载
     */
    @RequestMapping("MerchantGetReceipt")
    public String MerchantGetReceipt(HttpServletRequest request, HttpServletResponse response) {
            return payService.merchantGetReceipt(request);
    }  
    /**
     * AuthenMerchantQuery 鉴权查询
     */
    @RequestMapping("AuthenMerchantQuery")
    public String AuthenMerchantQuery(HttpServletRequest request, HttpServletResponse response) {
            return payService.authenMerchantQuery(request);
    } 
    /**
     * TransferOutQuery 外转查询
     */
    @RequestMapping("TransferOutQuery")
    public String TransferOutQuery(HttpServletRequest request, HttpServletResponse response) {
            return payService.transferOutQuery(request);
    } 
}
