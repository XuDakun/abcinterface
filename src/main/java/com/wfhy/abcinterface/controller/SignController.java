package com.wfhy.abcinterface.controller;

import com.wfhy.abcinterface.service.PayService;
import com.wfhy.abcinterface.service.SignService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("sign")
public class SignController {
    @Autowired
    private SignService signService;
   
    @RequestMapping("AgentSignContract")
    public String AgentSignContract(HttpServletRequest request, HttpServletResponse response) {
            return signService.agentSignContract(request);
    }  
    @RequestMapping("QuickAgentSignContract")
    public String QuickAgentSignContract(HttpServletRequest request, HttpServletResponse response) {
            return signService.quickAgentSignContract(request);
    }  
    /**
     * 授权支付签约确认
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("QuickAgentSignConfirm")
    public String QuickAgentSignConfirm(HttpServletRequest request, HttpServletResponse response) {
            return signService.quickAgentSignConfirm(request);
    }  
    /**
     * 授权支付短信验证码重发（商户端）
     */
    @RequestMapping("QuickAgentSignResendReq")
    public String QuickAgentSignResendReq(HttpServletRequest request, HttpServletResponse response) {
            return signService.quickAgentSignResendReq(request);
    }  
    /**
     * AgentUnsignContract 授权支付解约（商户端）
     */
    @RequestMapping("AgentUnsignContract")
    public String AgentUnsignContract(HttpServletRequest request, HttpServletResponse response) {
            return signService.agentUnsignContract(request);
    }  
    /**
     * 签约/解约结果查询
     */
    @RequestMapping("AgentSignContractQuery")
    public String AgentSignContractQuery(HttpServletRequest request, HttpServletResponse response) {
            return signService.agentSignContractQuery(request);
    }  
}
