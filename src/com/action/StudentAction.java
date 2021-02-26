package com.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts2.ServletActionContext;

import com.bean.Page;
import com.bean.Student;
import com.bean.User;
import com.opensymphony.xwork2.ActionSupport;
import com.service.StudentService;
import com.tools.StringTool;

public class StudentAction extends ActionSupport{
	//������������
		private StudentService service = new StudentService();
		private Student std;
	
	
	//�����ɼ���ѯҳ��
	public String toExamStudentView() {
		return "examStudentList";
	}
	//ת�����ɼ���ѯҳ���в鿴�ɼ�������ѧ���ĳɼ���Ϣ
	public String toStudentNoteListView() {
		return "studentNoteList";
	}
	 public void studentClazzList() throws IOException {
		 HttpServletRequest request=ServletActionContext.getRequest();
		 HttpServletResponse response=ServletActionContext.getResponse();
		 User user = (User) request.getSession().getAttribute("user");
		//��ȡ��ҳ����
		int page = Integer.parseInt(request.getParameter("page"));
		int rows = Integer.parseInt(request.getParameter("rows"));
		
		//��ȡ����
		String result =service.getStudentList(user.getAccount(), new Page(page, rows));
		//��������
		response.setContentType("text/html;charset=UTF-8"); 
        response.getWriter().write(result);
	}
	 
	public String toPersonal() throws ServletException, IOException {
		 HttpServletRequest request=ServletActionContext.getRequest();
		 HttpServletResponse response=ServletActionContext.getResponse();
		 User user = (User) request.getSession().getAttribute("user");
			Student student = service.getStudent(user.getAccount());
			request.getSession().setAttribute("userDetail", student);
			return "toStudentPersonalView";
		}
	
	public void editStudent() throws IOException {
		 HttpServletRequest request=ServletActionContext.getRequest();
		 HttpServletResponse response=ServletActionContext.getResponse();
		//��ȡ������
		/*Enumeration<String> pNames = request.getParameterNames();
		Student student = new Student();
		while(pNames.hasMoreElements()){
			String pName = pNames.nextElement();
			String value = request.getParameter(pName);
			try {
				BeanUtils.setProperty(student, pName, value);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}*/
		service.editStudent(std);
		response.setContentType("text/html;charset=UTF-8"); 
		response.getWriter().write("success");
	}
	
	//����Ա��
	//ѧ����Ϣ����������ѧ���б�
	public String toStudentListView() {
		return "toStudentListView";
	}
	public void studentList() throws IOException {
		 HttpServletRequest request=ServletActionContext.getRequest();
		 HttpServletResponse response=ServletActionContext.getResponse();
		 //�꼶ID
		String gradeid = request.getParameter("gradeid");
		//�༶ID
		String clazzid = request.getParameter("clazzid");
		//��ȡ��ҳ����
		int page = Integer.parseInt(request.getParameter("page"));
		int rows = Integer.parseInt(request.getParameter("rows"));
		//��װ����
		Student student = new Student();
		if(!StringTool.isEmpty(gradeid)){
			student.setGradeid(Integer.parseInt(gradeid));
		}
		if(!StringTool.isEmpty(clazzid)){
			student.setClazzid(Integer.parseInt(clazzid));
		}
		
		//��ȡ����
		String result = service.getStudentList(student, new Page(page, rows));
		//��������
		response.setContentType("text/html;charset=UTF-8"); 
        response.getWriter().write(result);
	}
	
	public void deleteStudent() throws IOException {
		HttpServletRequest request=ServletActionContext.getRequest();
		 HttpServletResponse response=ServletActionContext.getResponse();
		//��ȡҪɾ����ѧ��
		String[] numbers = request.getParameterValues("numbers[]");
		String[] ids = request.getParameterValues("ids[]");
		try {
			service.deleteStudent(ids, numbers);
			response.setContentType("text/html;charset=UTF-8"); 
			response.getWriter().write("success");
		} catch (Exception e) {
			response.getWriter().write("fail");
			e.printStackTrace();
		}
	}
	public void addStudent() throws IOException {
		 HttpServletRequest request=ServletActionContext.getRequest();
		 HttpServletResponse response=ServletActionContext.getResponse();
		
		 //��ȡ������
	/*	Enumeration<String> pNames = request.getParameterNames();
		Student student = new Student();
		while(pNames.hasMoreElements()){
			String pName = pNames.nextElement();
			String value = request.getParameter(pName);
			try {
				BeanUtils.setProperty(student, pName, value);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}*/
		service.addStudent(std);
		response.setContentType("text/html;charset=UTF-8"); 
		response.getWriter().write("success");
	}
	public Student getStd() {
		return std;
	}
	public void setStd(Student std) {
		this.std = std;
	}
	
	
	
	
}
