package com.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bean.Clazz;
import com.bean.Grade;
import com.bean.Page;
import com.bean.Student;
import com.dao.impl.BaseDaoImpl;
import com.dao.impl.ClazzDaoImpl;
import com.dao.inter.BaseDaoInter;
import com.dao.inter.ClazzDaoInter;
import com.tools.MysqlTool;
import com.tools.StringTool;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * �꼶�����
 * @author bojiangzhou
 *
 */
public class ClazzService {
	
	ClazzDaoInter dao = new ClazzDaoImpl();
	
	/**
	 * ��ȡָ���꼶�µİ༶
	 * @param gid �꼶ID
	 * @return JSON��ʽ�İ༶
	 */
	public String getClazzList(String gradeid){
		int id = Integer.parseInt(gradeid);
		//��ȡ����
		List<Object> list = dao.getList(Clazz.class, "SELECT * FROM clazz WHERE gradeid=?", new Object[]{id});
		//json��
		JsonConfig config = new JsonConfig();
		config.setExcludes(new String[]{"grade", "studentList"});
        String result = JSONArray.fromObject(list, config).toString();
        
        return result;
	}
	
	/**
	 * ��ȡ�༶��ϸ��Ϣ
	 * @param gradeid
	 * @param page
	 * @return
	 */
	public String getClazzDetailList(String gradeid, Page page) {
		//��ȡ����
		List<Clazz> list = dao.getClazzDetailList(gradeid, page);
		//��ȡ�ܼ�¼��
		long total = 0;
		if(!StringTool.isEmpty(gradeid)){
			int gid = Integer.parseInt(gradeid);
			total = dao.count("SELECT COUNT(*) FROM clazz WHERE gradeid=?", new Object[]{gid});
		} else {
			total = dao.count("SELECT COUNT(*) FROM clazz", new Object[]{});
		}
		//����Map
		Map<String, Object> jsonMap = new HashMap<String, Object>();  
		//total�� ����ܼ�¼���������
        jsonMap.put("total", total);
        //rows�� ���ÿҳ��¼ list 
        jsonMap.put("rows", list); 
        //��ʽ��Map,��json��ʽ��������
        String result = JSONObject.fromObject(jsonMap).toString();
        
        return result;
	}

	/**
	 * ��Ӱ༶
	 * @param name
	 * @param gradeid
	 */
	public void addClazz(String name, String gradeid) {
		int gid = Integer.parseInt(gradeid);
		dao.insert("INSERT INTO clazz(name, gradeid) value(?,?)", new Object[]{name, gid});
	}
	
	/**
	 * ɾ���༶
	 * @param clazzid
	 * @throws Exception 
	 */
	public void deleteClazz(int clazzid) throws Exception {
		//��ȡ����
		Connection conn = MysqlTool.getConnection();
		try {
			//��������
			MysqlTool.startTransaction();
			//ɾ���ɼ���
			dao.deleteTransaction(conn, "DELETE FROM escore WHERE clazzid=?", new Object[]{clazzid});
			//ɾ�����Լ�¼
			dao.deleteTransaction(conn, "DELETE FROM exam WHERE clazzid=?", new Object[]{clazzid});
			//ɾ���û�
			List<Object> list = dao.getList(Student.class, "SELECT number FROM student WHERE clazzid=?",  new Object[]{clazzid});
			if(list.size() > 0){
				Object[] param = new Object[list.size()];
				for(int i = 0;i < list.size();i++){
					Student stu = (Student) list.get(i);
					param[i] = stu.getNumber();
				}
				String sql = "DELETE FROM user WHERE account IN ("+StringTool.getMark(list.size())+")";
				dao.deleteTransaction(conn, sql, param);
				//ɾ��ѧ��
				dao.deleteTransaction(conn, "DELETE FROM student WHERE clazzid=?", new Object[]{clazzid});
			}
			//ɾ���༶�Ŀγ̺���ʦ�Ĺ���
			dao.deleteTransaction(conn, "DELETE FROM clazz_course_teacher WHERE clazzid=?", new Object[]{clazzid});
			//���ɾ���༶
			dao.deleteTransaction(conn, "DELETE FROM clazz WHERE id=?",  new Object[]{clazzid});
			
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
	
}
