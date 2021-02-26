package com.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.bean.Course;

import com.bean.CourseItem;
import com.bean.Exam;
import com.bean.Page;
import com.bean.Student;
import com.bean.Teacher;
import com.bean.User;
import com.dao.impl.BaseDaoImpl;
import com.dao.impl.ExamDaoImpl;
import com.dao.impl.ScoreDaoImpl;
import com.dao.impl.StudentDaoImpl;
import com.dao.impl.TeacherDaoImpl;
import com.dao.inter.BaseDaoInter;
import com.dao.inter.ExamDaoInter;
import com.dao.inter.ScoreDaoInter;
import com.dao.inter.StudentDaoInter;
import com.dao.inter.TeacherDaoInter;
import com.tools.ExcelTool;
import com.tools.MysqlTool;
import com.tools.StringTool;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * �ɼ�������
 * @author bojiangzhou
 *
 */
public class ScoreService {
	
	private ScoreDaoInter dao;
	
	public ScoreService(){
		dao = new ScoreDaoImpl();
	}
	
	/**
	 * ��ȡ�ɼ��б�
	 * @param exam ����
	 * @return
	 */
	public String getScoreList(Exam exam) {
		
		List<Map<String, Object>> list = dao.getScoreList(exam);
        //��ʽ��Map,��json��ʽ��������
        String result = JSONArray.fromObject(list).toString();
        //����
		return result;
	}
	
	/**
	 * ��ȡ��¼��
	 * @param exam
	 * @return
	 */
	private long getCount(Exam exam){
		//sql���
		StringBuffer sb = new StringBuffer("SELECT COUNT(*) FROM exam ");
		//����
		List<Object> param = new LinkedList<>();
		//�ж�����
		if(exam != null){ 
			if(exam.getGrade() != null){//�������꼶
				int gradeid = exam.getGradeid();
				param.add(gradeid);
				sb.append("AND gradeid=? ");
			}
			if(exam.getClazz() != null){
				int clazzid = exam.getClazzid();
				param.add(clazzid);
				sb.append("AND clazzid=? ");
			}
		}
		String sql = sb.toString().replaceFirst("AND", "WHERE");
		
		long count = dao.count(sql, param).intValue();
		
		return count;
	}
	
	/**
	 * ��ӿ���
	 * @param exam
	 * @throws Exception
	 */
	public void addExam(Exam exam) throws Exception {
		Connection conn = MysqlTool.getConnection();
		try {
			//��������
			MysqlTool.startTransaction();
			
			//��ӿ�����Ϣ
			int examid = dao.insertReturnKeysTransaction(conn, 
					"INSERT INTO exam(name, time, remark, type, gradeid, clazzid) value(?,?,?,?,?,?)", 
					new Object[]{
						exam.getName(), 
						exam.getTime(),
						exam.getRemark(),
						exam.getType(),
						exam.getGradeid(),
						exam.getClazzid()
					});
			
			//���ѧ���ɼ���
			String sql = "INSERT INTO escore(examid,clazzid,studentid,gradeid,courseid) value(?,?,?,?,?)";
			
			if(exam.getType() == Exam.EXAM_GRADE_TYPE){ //�꼶ͳ��
				
				//��ѯ���꼶�Ŀγ�
				List<Object> couObjList = dao.getList(Course.class, 
						"SELECT courseid id FROM grade_course WHERE gradeid=?", 
						new Object[]{exam.getGradeid()});
				
				//��ѯ���꼶�µ�ѧ��
				List<Object> stuList = dao.getList(Student.class, 
						"SELECT id, clazzid FROM student WHERE gradeid=?",
						new Object[]{exam.getGradeid()});
				
				//ת������
				List<Course> couList = new LinkedList<>();
				for(Object obj : couObjList){
					Course course = (Course) obj;
					couList.add(course);
				}
				//��������
				Object[][] param = new Object[stuList.size()*couList.size()][5];
				int index = 0;
				for(int i = 0;i < stuList.size();i++){
					Student student = (Student) stuList.get(i);
					for(int j = 0;j < couList.size();j++){
						param[index][0] = examid;
						param[index][1] = student.getClazzid();
						param[index][2] = student.getId();
						param[index][3] = exam.getGradeid();
						param[index][4] = couList.get(j).getId();
						
						index++;
					}
				}
				//�������ѧ�����Ա�
				dao.insertBatchTransaction(conn, sql, param);
				
			} else{  //ƽʱ����
				
				//��ѯ�ð༶�µ�ѧ��
				List<Object> stuList = dao.getList(Student.class, 
						"SELECT id FROM student WHERE clazzid=?",
						new Object[]{exam.getClazzid()});
				
				//��������
				Object[][] param = new Object[stuList.size()][5];
				for(int i = 0;i < stuList.size();i++){
					Student student = (Student) stuList.get(i);
					param[i][0] = examid;
					param[i][1] = exam.getClazzid();
					param[i][2] = student.getId();
					param[i][3] = exam.getGradeid();
					param[i][4] = exam.getCourseid();
				}
				//�������ѧ�����Ա�
				dao.insertBatchTransaction(conn, sql, param);
			}
			
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
	 * ��ȡ������������
	 * @param exam
	 * @return
	 */
	public String columnList(Exam exam) {
		List<Object> list = getColumn(exam);
		
		return JSONArray.fromObject(list).toString();
	}
	
	private List<Object> getColumn(Exam exam){
		List<Object> list = null;
		if(exam.getType() == Exam.EXAM_GRADE_TYPE){ //�꼶����
			//��ȡ���ԵĿ�Ŀ
			list = dao.getList(Course.class, 
					"SELECT c.id id, c.name name FROM course c, grade_course gc WHERE c.id=gc.courseid AND gc.gradeid=?", 
					new Object[]{exam.getGradeid()});
		} else{
			//��ȡĳ��
			list =  dao.getList(Course.class, 
					"SELECT * FROM course WHERE id=?", new Object[]{exam.getCourseid()});
			
		}
		return list;
	}
	

	/**
	 * �����ɼ��б�
	 * @param response
	 * @param exam
	 */
	public void exportScore(HttpServletResponse response, Exam exam) {
		//��ȡ��Ҫ����������
		List<Map<String, Object>> list = dao.getScoreList(exam);
		//��ȡ������Ϣ
		Exam em = (Exam) dao.getObject(Exam.class, "SELECT name, time FROM exam WHERE id=?", new Object[]{exam.getId()});
		//�����ļ���

		System.out.println("exam++++"+exam.getName());

		String fileName = em.getName()+".xls";
		//�����������
		response.setContentType("application/msexcel;charset=utf-8");
		//�趨����ļ�ͷ
		try {
			response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		//��ȡ�����Ŀγ�
		List<Object> courseList = getColumn(exam);
		
		//��ͷ����
		int len = 2 + courseList.size();
		if(exam.getType() == Exam.EXAM_GRADE_TYPE){
			len += 1;
		}
		//����excel������
		String[] headers = new String[len];
		headers[0] = "����";
		headers[1] = "ѧ��";
		
		int index = 2;
		for(Object obj : courseList){
			Course course = (Course) obj;
			headers[index++] = course.getName();
		}
		
		if(exam.getType() == Exam.EXAM_GRADE_TYPE){
			headers[len-1] = "�ܷ�";
		}
		
		ExcelTool et = new ExcelTool<>();
		//����
		try {
			et.exportMapExcel(headers, list, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���óɼ�
	 * @param score id_score ��ʽ
	 */
	public void setScore(String[] score) {
		Object[][] param = new Object[score.length][2];
		
		for(int i = 0;i < score.length;i++){
			String[] id_score = score[i].split("_");
			int id = Integer.parseInt(id_score[0]);
			param[i][1] = id;
			if(id_score.length == 1){
				param[i][0] = 0;
			} else {
				int sco = Integer.parseInt(id_score[1]);
				param[i][0] = sco;
			}
		}
		
		dao.updateBatch("UPDATE escore SET score=? WHERE id=?", param);
		
	}
	
}
