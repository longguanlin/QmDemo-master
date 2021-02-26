package com.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bean.Course;
import com.bean.CourseItem;
import com.bean.Exam;
import com.bean.Page;
import com.bean.Student;
import com.bean.Teacher;
import com.bean.User;
import com.dao.impl.BaseDaoImpl;
import com.dao.impl.ExamDaoImpl;
import com.dao.impl.StudentDaoImpl;
import com.dao.impl.TeacherDaoImpl;
import com.dao.inter.BaseDaoInter;
import com.dao.inter.ExamDaoInter;
import com.dao.inter.StudentDaoInter;
import com.dao.inter.TeacherDaoInter;
import com.tools.MysqlTool;
import com.tools.StringTool;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * ��ʦ������
 * @author bojiangzhou
 *
 */
public class ExamService {
	
	private ExamDaoInter dao;
	
	public ExamService(){
		dao = new ExamDaoImpl();
	}
	
	/**
	 * ��ȡ������Ϣ
	 * @param exam ����
	 * @param page ��ҳ
	 * @return
	 */
	public String getExamList(Exam exam, Page page) {
		//sql���
		StringBuffer sb = new StringBuffer("SELECT * FROM exam ");
		//����
		List<Object> param = new LinkedList<>();
		//�ж�����
		if(exam != null){ 
			if(exam.getGradeid() != 0){//�������꼶
				int gradeid = exam.getGradeid();
				param.add(gradeid);
				sb.append("AND gradeid=? ");
			}
			if(exam.getClazzid() != 0){
				int clazzid = exam.getClazzid();
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
			sb.append("LIMIT ?,?");
		}
		String sql = sb.toString().replaceFirst("AND", "WHERE");
		//��ȡ����
		List<Exam> list = dao.getExamList(sql, param);
		//��ȡ�ܼ�¼��
		long total = getCount(exam);
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
					"INSERT INTO exam(name, time, remark, type, gradeid, clazzid, courseid) value(?,?,?,?,?,?,?)", 
					new Object[]{
						exam.getName(), 
						exam.getTime(),
						exam.getRemark(),
						exam.getType(),
						exam.getGradeid(),
						exam.getClazzid(),
						exam.getCourseid()
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
	 * ɾ������
	 * @param ids 
	 * @throws Exception 
	 */
	public void deleteExam(int id) throws Exception{
		//��ȡ����
		Connection conn = MysqlTool.getConnection();
		//��������
		MysqlTool.startTransaction();
		try {
			//ɾ���ɼ���
			dao.deleteTransaction(conn, "DELETE FROM escore WHERE examid=?", new Object[]{id});
			//ɾ������
			dao.deleteTransaction(conn, "DELETE FROM exam WHERE id =?", new Object[]{id});
			
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
	 * ��ȡĳ��ʦ�Ŀ���
	 * @param id
	 * @return
	 */
	public String teacherExamList(String number) {
		//��ȡ��ʦ��Ϣ
		Teacher teacher = new TeacherService().getTeacher(number);
		
		List<CourseItem> itemList = teacher.getCourseList();
		if(itemList.size() == 0){
			return "";
		}
		StringBuffer g = new StringBuffer();
		StringBuffer c = new StringBuffer();
		for(CourseItem item : itemList){
			g.append(","+item.getGradeid());
			c.append(","+item.getCourseid());
		}
		
		StringBuffer sb = new StringBuffer("SELECT * FROM exam WHERE (gradeid IN (");
		sb.append(g.toString().replaceFirst(",", ""));
		sb.append(") AND type=1) OR (courseid IN (");
		sb.append(c.toString().replaceFirst(",", ""));
		sb.append(") AND type=2)");
		//sql���
		String sql = sb.toString();
		//��ȡ����
		List<Exam> list = dao.getExamList(sql, null);
		
        //��ʽ��Map,��json��ʽ��������
        String result = JSONArray.fromObject(list).toString();
        //����
		return result;
	}
	
	/**
	 * ��ȡĳ��ѧ�������б�
	 * @param number
	 * @return
	 */
	public String studentExamList(String number) {
		
		//��ȡѧ����ϸ��Ϣ
		Student student = new StudentDaoImpl().getStudentList("SELECT * FROM student WHERE number="+number, null).get(0);
		
		String sql = "SELECT * FROM exam WHERE (gradeid=? AND type=1) OR (clazzid=? AND type=2)";
		
		List<Object> param = new LinkedList<>();
		param.add(student.getGradeid());
		param.add(student.getClazzid());
		
		//��ȡ����
		List<Exam> list = dao.getExamList(sql, param);
		
		//��ʽ��Map,��json��ʽ��������
        String result = JSONArray.fromObject(list).toString();
		
		return result;
	}
	
	
}
