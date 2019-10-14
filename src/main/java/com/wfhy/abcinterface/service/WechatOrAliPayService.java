package com.wfhy.abcinterface.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;


@Service
public interface WechatOrAliPayService {

	String wechatPayment(HttpServletRequest request);

	String aliPayment(HttpServletRequest request);


}
