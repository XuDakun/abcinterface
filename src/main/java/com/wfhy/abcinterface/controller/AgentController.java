package com.wfhy.abcinterface.controller;

import com.wfhy.abcinterface.service.AgentService;
import com.wfhy.abcinterface.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("agent")
public class AgentController {
    @Autowired
    private AgentService agentService;
   
    @RequestMapping("AgentPayment")
    public String AgentPayment(HttpServletRequest request, HttpServletResponse response) {

            return agentService.agentPayment(request);
   
    }  
    /**
     * 授权扣款扣款批量
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("AgentBatchPayment")
    public String AgentBatchPayment(HttpServletRequest request, HttpServletResponse response) {

            return agentService.agentBatchPayment(request);
   
    }  
    /**
     * 查询批量处理结果
     */
    @RequestMapping("AgentBatchPaymentQuery")
    public String AgentBatchPaymentQuery(HttpServletRequest request, HttpServletResponse response) {

            return agentService.agentBatchPaymentQuery(request);
   
    } 
    @RequestMapping("ErrorPage")
    public String ErrorPage(HttpServletRequest request, HttpServletResponse response) {
    	return "错误页面";
    }
}
