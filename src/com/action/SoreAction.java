package com.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts2.ServletActionContext;

import com.bean.Exam;
import com.opensymphony.xwork2.ActionSupport;
import com.service.ScoreService;

public class SoreAction extends ActionSupport{
		//������������
		private ScoreService service = new ScoreService();
		

		public void columnList() throws IOException {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			Enumeration<String> pNames = request.getParameterNames();
			Exam exam = new Exam();
			
			/* hasMoreElements( )���ڲ��Դ�ö���Ƿ���������Ԫ��*/
			while(pNames.hasMoreElements()){
				/*nextElement()���������ö�ٶ������ٻ���һ�����ṩ��Ԫ�أ��򷵻ش�ö�ٵ���һ��Ԫ�ء�*/
				/*pNameΪö�ٶ���*/
				/*value��pName�����ֵ*/
				String pName = pNames.nextElement();
				String value = request.getParameter(pName);
				try {
					BeanUtils.setProperty(exam, pName, value);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			
			//��ȡ����
			String result = service.columnList(exam);
			//��������
			response.setContentType("text/html;charset=UTF-8");
	        response.getWriter().write(result);
		}
		
		public void scoreList() throws IOException {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			Enumeration<String> pNames = request.getParameterNames();
			Exam exam = new Exam();
			while(pNames.hasMoreElements()){
				String pName = pNames.nextElement();
				String value = request.getParameter(pName);
				try {
					BeanUtils.setProperty(exam, pName, value);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			
			//��ȡ����
			String result = service.getScoreList(exam);
			//��������
			response.setContentType("text/html;charset=UTF-8");
	        response.getWriter().write(result);
		}
		
		public void exportScore() {
			//��ȡ��ҳ����
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			Enumeration<String> pNames = request.getParameterNames();
			Exam exam = new Exam();
			while(pNames.hasMoreElements()){
				String pName = pNames.nextElement();
				String value = request.getParameter(pName);
				try {
					BeanUtils.setProperty(exam, pName, value);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			
			service.exportScore(response, exam);
		}
		
		
		public void setScore() throws IOException {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			String[] score = request.getParameterValues("score[]");
			service.setScore(score);
			//��������
			response.setContentType("text/html;charset=UTF-8");
	        response.getWriter().write("success");
		}
		
}