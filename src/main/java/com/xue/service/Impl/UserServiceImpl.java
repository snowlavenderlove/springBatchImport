package com.xue.service.Impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.xue.entity.model.User;
import com.xue.repository.dao.UserMapper;
import com.xue.service.UserService;
import com.xue.transcation.MyException;
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public int addUser(MultipartFile file) throws Exception{
		
		
		int result = 0;
		
		SqlSession batchSqlSession = null;
		

//		存放excel表中所有user细腻信息
		List<User> userList = new ArrayList<>();
		/**
		 * 
		 * 判断文件版本
		 */
		String fileName = file.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
		
		InputStream ins = file.getInputStream();
		
		Workbook wb = null;
		
		if(suffix.equals("xlsx")){
			
			wb = new XSSFWorkbook(ins);
			
		}else{
			wb = new HSSFWorkbook(ins);
		}
		/**
		 * 获取excel表单
		 */
		Sheet sheet = wb.getSheetAt(0);
		
		
		/**
		 * line = 2 :从表的第三行开始获取记录
		 * 
		 */
		if(null != sheet){
			
			for(int line = 2; line <= sheet.getLastRowNum();line++){
				
				User user = new User();
				
				Row row = sheet.getRow(line);
				
				if(null == row){
					continue;
				}
				/**
				 * 判断单元格类型是否为文本类型
				 */
				if(1 != row.getCell(0).getCellType()){
					throw new MyException("单元格类型不是文本类型！");
				}
				
				/**
				 * 获取第一个单元格的内容
				 */
				String username = row.getCell(0).getStringCellValue();
				
				row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
				/**
				 * 获取第二个单元格的内容
				 */
				
				String password = row.getCell(1).getStringCellValue();
				
				user.setUsername(username);
				user.setPassword(password);
				userList.add(user);
	
			}
//			逐条插入
//			for(User userInfo:userList){
//			
//				/**
//				 * 判断数据库表中是否存在用户记录，若存在，则更新，不存在，则保存记录
//				 */
//				String name = userInfo.getUsername();
//				
//				int count = userMapper.selectUser(name);
//				
//				if(0 == count){			
//					result = userMapper.addUser(userInfo);
//				}else{
//					result = userMapper.updateUser(userInfo);
//				}
//
//				
//				
//			}
			
			int batchCount = 5;
//			
			int batchLastIndex = batchCount;//每批最后一条数据的下标
			
			batchSqlSession = this.sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH,false);
			
			UserMapper mapper = batchSqlSession.getMapper(UserMapper.class);
				
			int count = userMapper.selectAll();

			if(0 == count){
					
				for(int index = 0; index < userList.size();){
					
						
					if(batchLastIndex >= userList.size()){
							
						batchLastIndex = userList.size();
						result = result + mapper.importBatchInsert(userList.subList(index,batchLastIndex));
						System.out.println(result+"result");
						//清除缓存
						batchSqlSession.clearCache();
						break;	
					}else {
						result = result + mapper.importBatchInsert(userList.subList(index, batchLastIndex));
							
						batchSqlSession.clearCache();
							
						index = batchLastIndex;
							
						batchLastIndex = index + (batchCount -1);
					}
				}
					
				//将数据提交到数据库，否则执行但是未将数据插入到数据库
				batchSqlSession.commit();

			}else{
					
				mapper.importDropTemTable();
				mapper.importCreateTemTabel();
				for(int index = 0; index < userList.size();){
						
					if(batchLastIndex >= userList.size()){
							
						batchLastIndex = userList.size();
						result = result + mapper.importBatchTempInsert(userList.subList(index,batchLastIndex));
							
						batchSqlSession.clearCache();
						break;
							
					}else {
							
						result = result + mapper.importBatchTempInsert(userList.subList(index, batchLastIndex));
							
						batchSqlSession.clearCache();
							
						index = batchLastIndex;
							
						batchLastIndex = index + (batchCount -1);
							
					}
						
				}
					
					/**
					 * 归档
					 */
					
				mapper.importBatchUpdate();
				batchSqlSession.commit();
			}
		}

		return result;
	}
	
	

}
