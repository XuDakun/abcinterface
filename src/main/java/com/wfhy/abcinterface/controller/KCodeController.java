package com.wfhy.abcinterface.controller;

import com.wfhy.abcinterface.service.KCodePayService;
import com.wfhy.abcinterface.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("kcodepay")
public class KCodeController {
    @Autowired
    private KCodePayService kCodePayService;
    

    @RequestMapping("MerchantQuickPaymentReq")
    public String merchantQuickPaymentReq(HttpServletRequest request, HttpServletResponse response) {
            return kCodePayService.merchantQuickPaymentReq(request);
    }
    
    @RequestMapping("MerchantQuickPaymentSend")
    public String MerchantQuickPaymentSend(HttpServletRequest request, HttpServletResponse response) {
            return kCodePayService.merchantQuickPaymentSend(request);
    }
    
    @RequestMapping("MerchantQuickPaymentResend")
    public String MerchantQuickPaymentResend(HttpServletRequest request, HttpServletResponse response) {
            return kCodePayService.MerchantQuickPaymentResend(request);
    }
}
