package com.bean;

/**
 * ���Գɼ���
 * @author bojiangzhou
 *
 */
public class EScore {
	
	private int id; //ID
	
	private Exam exam; //����
	
	private int examid; //����ID
	
	private Clazz clazz; //���԰༶
	
	private int clazzid; //�༶ID
	
	private Course course; //���Կ�Ŀ
	
	private int courseid; //��ĿID
	
	private Student student; //����ѧ��
	
	private int studentid; //ѧ��ID
	
	private int score; //���Գɼ�

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}

	public int getExamid() {
		return examid;
	}

	public void setExamid(int examid) {
		Exam exam = new Exam();
		exam.setId(examid);
		this.exam = exam;
		this.examid = examid;
	}

	public Clazz getClazz() {
		return clazz;
	}

	public void setClazz(Clazz clazz) {
		this.clazz = clazz;
	}

	public int getClazzid() {
		return clazzid;
	}

	public void setClazzid(int clazzid) {
		Clazz clazz = new Clazz();
		clazz.setId(clazzid);
		this.clazz = clazz;
		this.clazzid = clazzid;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public int getCourseid() {
		return courseid;
	}

	public void setCourseid(int courseid) {
		Course course = new Course();
		course.setId(courseid);
		this.course = course;
		this.courseid = courseid;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public int getStudentid() {
		return studentid;
	}

	public void setStudentid(int studentid) {
		Student student = new Student();
		student.setId(studentid);
		this.student = student;
		this.studentid = studentid;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
