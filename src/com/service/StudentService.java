package com.service;



import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bean.Page;
import com.bean.Student;
import com.bean.User;
import com.dao.impl.StudentDaoImpl;
import com.dao.inter.StudentDaoInter;
import com.tools.MysqlTool;
import com.tools.StringTool;

import net.sf.json.JSONObject;

/**
 * ѧ����Ϣ�����
 *
 */
public class StudentService {
	
	private StudentDaoInter dao;
	
	public StudentService(){
		dao = new StudentDaoImpl();
	}
	
	/**
	 * �޸�ѧ����Ϣ
	 * @param student
	 */
	public void editStudent(Student student){
		
		String sql = "";
		
		List<Object> params = new LinkedList<>();
		params.add(student.getName());
		params.add(student.getSex());
		params.add(student.getPhone());
		params.add(student.getQq());
		
		if(student.getGrade() == null || student.getClazz() == null){
			sql = "UPDATE student SET name=?, sex=?, phone=?, qq=? WHERE number=?";
		} else{
			sql = "UPDATE student SET name=?, sex=?, phone=?, qq=?, clazzid=?, gradeid=? WHERE number=?";
			params.add(student.getClazzid());
			params.add(student.getGradeid());
		}
		params.add(student.getNumber());
		
		//����ѧ����Ϣ
		dao.update(sql, params);
		
		//�޸�ϵͳ�û���
		dao.update("UPDATE user SET name=? WHERE account=?", 
				new Object[]{student.getName(), student.getNumber()});
	}
	
	/**
	 * ɾ��ѧ��
	 * @param ids ѧ��id����
	 * @param numbers ѧ��ѧ�ż���
	 * @throws Exception 
	 */
	public void deleteStudent(String[] ids, String[] numbers) throws Exception{
		//��ȡռλ��
		String mark = StringTool.getMark(numbers.length);
		Integer sid[] = new Integer[ids.length];
		for(int i =0 ;i < ids.length;i++){
			sid[i] = Integer.parseInt(ids[i]);
		}
		
		//��ȡ����
		Connection conn = MysqlTool.getConnection();
		//��������
		MysqlTool.startTransaction();
		try {
			//ɾ���ɼ���
			dao.deleteTransaction(conn, "DELETE FROM escore WHERE studentid IN("+mark+")", sid);
			//ɾ��ѧ��
			dao.deleteTransaction(conn, "DELETE FROM student WHERE id IN("+mark+")", sid);
			//ɾ��ϵͳ�û�
			dao.deleteTransaction(conn, "DELETE FROM user WHERE account IN("+mark+")",  numbers);
			
			//�ύ����
			MysqlTool.commit();
		} catch (Exception e) {
			//�ع�����
			MysqlTool.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			MysqlTool.closeConnection();
		}
		
	}
	
	/**
	 * ���ѧ��
	 * @param student
	 */
	public void addStudent(Student student){

		//���ѧ����¼
		dao.insert("INSERT INTO student(number, name, sex, phone, qq, clazzid, gradeid) value(?,?,?,?,?,?,?)", 
				new Object[]{
					student.getNumber(), 
					student.getName(), 
					student.getSex(), 
					student.getPhone(),
					student.getQq(),
					student.getClazzid(),
					student.getGradeid()
				});
		//����û���¼
		dao.insert("INSERT INTO user(account, name, type) value(?,?,?)", new Object[]{
				student.getNumber(),
				student.getName(),
				User.USER_STUDENT
		});
	}
	
	/**
	 * ��ҳ��ȡѧ��
	 * @param student ѧ����Ϣ
	 * @param page ��ҳ
	 * @return
	 */
	public String getStudentList(Student student, Page page){
		//sql���
		StringBuffer sb = new StringBuffer("SELECT * FROM student ");
		//����
		List<Object> param = new LinkedList<>();
		//�ж�����
		if(student != null){ 
			if(student.getGrade() != null){//�������꼶
				int gradeid = student.getGrade().getId();
				param.add(gradeid);
				sb.append("AND gradeid=? ");
			}
			if(student.getClazz() != null){
				int clazzid = student.getClazz().getId();
				param.add(clazzid);
				sb.append("AND clazzid=? ");
			}
		}
		//�������
		sb.append("ORDER BY id DESC ");
		//��ҳ
		if(page != null){
			param.add(page.getStart());
			param.add(page.getSize());
			sb.append("limit ?,?");
		}
		String sql = sb.toString().replaceFirst("AND", "WHERE");
		//��ȡ����
		List<Student> list = dao.getStudentList(sql, param);
		//��ȡ�ܼ�¼��
		long total = getCount(student);
		//����Map
		Map<String, Object> jsonMap = new HashMap<String, Object>();  
		//total�� ����ܼ�¼���������
        jsonMap.put("total", total);
        //rows�� ���ÿҳ��¼ list 
        jsonMap.put("rows", list); 
        //��ʽ��Map,��json��ʽ��������
        String result = JSONObject.fromObject(jsonMap).toString();
        //����
		return result;
	}
	
	/**
	 * ��ȡ��¼��
	 * @param student
	 * @param page
	 * @return
	 */
	private long getCount(Student student){
		//sql���
		StringBuffer sb = new StringBuffer("SELECT COUNT(*) FROM student ");
		//����
		List<Object> param = new LinkedList<>();
		//�ж�����
		if(student != null){ 
			if(student.getGrade() != null){//�������꼶
				int gradeid = student.getGrade().getId();
				param.add(gradeid);
				sb.append("AND gradeid=? ");
			}
			if(student.getClazz() != null){
				int clazzid = student.getClazz().getId();
				param.add(clazzid);
				sb.append("AND clazzid=? ");
			}
		}
		String sql = sb.toString().replaceFirst("AND", "WHERE");
		
		long count = dao.count(sql, param).intValue();
		
		return count;
	}

	/**
	 * ��ȡ��ѧ��һ����İ༶ͬѧ
	 * @param account
	 * @param page
	 * @return
	 */
	public String getStudentList(String account, Page page) {
		
		Student student = (Student) dao.getObject(Student.class, "SELECT * FROM student WHERE number=?", new Object[]{account});
		
		return getStudentList(student, page);
	}

	/**
	 * ��ȡѧ����ϸ��Ϣ
	 * @param account
	 * @return
	 */
	public Student getStudent(String account) {
		
		Student student = dao.getStudentList("SELECT * FROM student WHERE number="+account, null).get(0);
		
		return student;
	}
	
	
}
