package com.wfhy.abcinterface.controller;

import com.wfhy.abcinterface.service.PayService;
import com.wfhy.abcinterface.service.RefundService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("refund")
public class RefundController {
    @Autowired
    private RefundService refundService;
    

    @RequestMapping("MerchantRefund")
    public String MerchantRefund(HttpServletRequest request, HttpServletResponse response) {

            return refundService.MerchantRefund(request);
   
    }  
    
    @RequestMapping("MerchantBatchRefund")
    public String MerchantBatchRefund(HttpServletRequest request, HttpServletResponse response) {

            return refundService.MerchantBatchRefund(request);
   
    }  
    @RequestMapping("MerchantBatchRefundQuery")
    public String MerchantBatchRefundQuery(HttpServletRequest request, HttpServletResponse response) {

            return refundService.MerchantBatchRefundQuery(request);
   
    }  

}
