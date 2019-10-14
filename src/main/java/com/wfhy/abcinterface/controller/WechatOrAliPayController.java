package com.wfhy.abcinterface.controller;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wfhy.abcinterface.service.WechatOrAliPayService;


@Controller
@RequestMapping("mobile")
@PropertySource("classpath:log4j.properties")
public class WechatOrAliPayController {
	@Autowired
    private WechatOrAliPayService wechatOrAliPayService;
	  Logger logger =Logger.getLogger(WechatOrAliPayController.class); 
	@RequestMapping("wechatpayment")
    public String WechatPayment(HttpServletRequest request, HttpServletResponse response) {
		String prams = "";
		Map pmap = request.getParameterMap();
		Iterator it = pmap.keySet().iterator();
		while(it.hasNext()) {
			String paramName = (String) it.next();
		    String paramValue = request.getParameter(paramName);
		    //处理你得到的参数名与值
		    prams+=paramName+"="+paramValue;
		}
		logger.info("进入方法：WechatPayment| 请求参数："+prams);
        return wechatOrAliPayService.wechatPayment(request);

}  
	@RequestMapping("alipayment")
    public String AliPayment(HttpServletRequest request, HttpServletResponse response) {
		String prams = "";
		Map pmap = request.getParameterMap();
		Iterator it = pmap.keySet().iterator();
		while(it.hasNext()) {
			String paramName = (String) it.next();
		    String paramValue = request.getParameter(paramName);
		    //处理你得到的参数名与值
		    prams+=paramName+"="+paramValue;
		}
		logger.info("进入方法：AliPayment| 请求参数："+prams);
        return wechatOrAliPayService.aliPayment(request);

} 
}
