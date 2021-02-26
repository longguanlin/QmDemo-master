package com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import com.bean.Clazz;
import com.bean.Course;
import com.bean.Exam;
import com.bean.Grade;
import com.bean.Page;
import com.bean.Student;
import com.dao.inter.BaseDaoInter;
import com.dao.inter.ExamDaoInter;
import com.dao.inter.StudentDaoInter;
import com.tools.MysqlTool;

/**
 * 
 * @author bojiangzhou
 *
 */
public class ExamDaoImpl extends BaseDaoImpl implements ExamDaoInter {

	public List<Exam> getExamList(String sql, List<Object> param) {
		//���ݼ���
		List<Exam> list = new LinkedList<>();
		try {
			//��ȡ���ݿ�����
			Connection conn = MysqlTool.getConnection();
			//Ԥ����
			PreparedStatement ps = conn.prepareStatement(sql);
			//���ò���
			if(param != null && param.size() > 0){
				for(int i = 0;i < param.size();i++){
					ps.setObject(i+1, param.get(i));
				}
			}
			//ִ��sql���
			ResultSet rs = ps.executeQuery();
			//��ȡԪ����
			ResultSetMetaData meta = rs.getMetaData();
			//���������
			while(rs.next()){
				//��������
				Exam exam = new Exam();
				//����ÿ���ֶ�
				for(int i=1;i <= meta.getColumnCount();i++){
					String field = meta.getColumnName(i);
					Object value = rs.getObject(field);
					BeanUtils.setProperty(exam, field, rs.getObject(field));
				}
				//��ѯ�༶
				Clazz clazz = (Clazz) getObject(Clazz.class, "SELECT * FROM clazz WHERE id=?", new Object[]{exam.getClazzid()});
				//��ѯ�꼶
				Grade grade = (Grade) getObject(Grade.class, "SELECT * FROM grade WHERE id=?", new Object[]{exam.getGradeid()});
				//��ѯ��Ŀ
				Course course = (Course) getObject(Course.class, "SELECT * FROM course WHERE id=?", new Object[]{exam.getCourseid()});
				//���
				exam.setClazz(clazz);
				exam.setGrade(grade);
				exam.setCourse(course);
				//��ӵ�����
				list.add(exam);
			}
			//�ر�����
			MysqlTool.closeConnection();
			MysqlTool.close(ps);
			MysqlTool.close(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	

}
