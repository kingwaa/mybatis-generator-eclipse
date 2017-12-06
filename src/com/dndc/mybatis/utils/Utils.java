package com.dndc.mybatis.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.dndc.mybatis.CustomInfo;
import com.dndc.mybatis.DatabaseConfig;
import com.dndc.mybatis.TableColumn;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public final class Utils {
	
	private static final String DIR = System.getProperty("user.home") + "/.eclipse/com.dndc.mybatis.plugins/";
	static {
		mkdir();
	}
	
	public static void showMessageBox(Shell shell,String message,int style) {
		MessageBox mb = new MessageBox(shell,style);
		mb.setMessage(message);
		mb.open();
	}
	
	private static void mkdir() {
		try {
			File file = new File(System.getProperty("user.home") + "/.eclipse");
			if(!file.exists()) {
				file.mkdir();
			}
			file = new File(System.getProperty("user.home") + "/.eclipse/com.dndc.mybatis.plugins");
			if(!file.exists()) {
				file.mkdir();
			}
		} catch(Exception e) {
		}
	}

	public static String throwableToString(Throwable throwable) {
		if(throwable == null) {
			return "";
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			throwable.printStackTrace(new PrintStream(out));
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
		return out.toString();
	}
	
	public static void writeDatabaseConfig(DatabaseConfig databaseConfig) {
        FileOutputStream fout = null;
        try {
            Properties properties = readProperties();  
            System.out.println(DIR);
            String value = properties.getProperty("datasources");
            if(value == null || "".equals(value)) {
                value = databaseConfig.getName();
            } else {
                value = value + "," + databaseConfig.getName();
            }
            
            fout = new FileOutputStream(DIR + "databases");
            properties.setProperty("datasources", value);
            properties.setProperty(databaseConfig.getName()+".host", databaseConfig.getHost());
            properties.setProperty(databaseConfig.getName()+".port", databaseConfig.getPort());
            properties.setProperty(databaseConfig.getName()+".schema", databaseConfig.getSchema());
            properties.setProperty(databaseConfig.getName()+".username", databaseConfig.getUsername());
            properties.setProperty(databaseConfig.getName()+".password", databaseConfig.getPassword());
            properties.setProperty(databaseConfig.getName()+".encoding", databaseConfig.getEncoding());
            properties.store(fout, "datasource infomation");  
            fout.close();  
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if(fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                }
            }
        }
    }
	
	public static String[] getDatasources() {
	    Properties properties = readProperties();
	    return getDatasources(properties);
	}
	
	public static String[] getDatasources(Properties properties) {
        String value = properties.getProperty("datasources");
        if(value != null && !"".equals(value)) {
            return value.split(",");
        }
        
        return new String[]{};
    }
	
	public static DatabaseConfig getDatabaseConfig(String datasource) {
        Properties properties = readProperties();
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setName(datasource);
        databaseConfig.setHost(properties.getProperty(datasource+".host"));
        databaseConfig.setPort(properties.getProperty(datasource+".port"));
        databaseConfig.setSchema(properties.getProperty(datasource+".schema"));
        databaseConfig.setUsername(properties.getProperty(datasource+".username"));
        databaseConfig.setPassword(properties.getProperty(datasource+".password"));
        databaseConfig.setEncoding(properties.getProperty(datasource+".encoding"));
        return databaseConfig;
    }
	
	public static List<List<String>> getTableInfos(String datasource) {
	    List<List<String>> tableInfos = new ArrayList<List<String>>();
	    Connection conn = null;
        try {
            conn = getConnection(getDatabaseConfig(datasource));
            PreparedStatement pstate = conn.prepareStatement("show table status");
            ResultSet results = pstate.executeQuery();
            List<String> row = null;
            int i=1;
            while (results.next()) {
                row = new ArrayList<String>();
                row.add(String.valueOf(i++));
                row.add(results.getString("Name"));
                row.add(results.getString("Comment"));
                tableInfos.add(row);
            }
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        
        return tableInfos;
	}
	
	public static Properties readProperties() {
	    Properties properties = new Properties();  
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(DIR + "databases");
	        properties.load(fin);  
	        fin.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if(fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
				}
			}
		}
		return properties;
	}
	
	public static Connection getConnection(DatabaseConfig databaseConfig) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(databaseConfig.getUrl(), databaseConfig.getUsername(), databaseConfig.getPassword());
    }
	
	public static void writeTemplete(String template, String content) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			Element root = doc.createElement("templete");
			doc.appendChild(root);
			
			Text templete = doc.createTextNode(content);
            root.appendChild(templete);
            
			TransformerFactory tFactory =TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new java.io.File(DIR + template));
			transformer.transform(source, result); 
		} catch (ParserConfigurationException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
	}
	
	public static String readTemplete(String template) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String content = null;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(DIR + template)); 
			doc.normalize(); 
			Element root = doc.getDocumentElement();
			content = root.getTextContent();
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		
		if(content == null) {
		    content = readDefaultTemplete(template);
		}
		
		return content;
	}
	
	public static String readDefaultTemplete(String template) {
	    StringBuilder builder = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(Utils.class.getResourceAsStream(template+".ftl"),"UTF-8");
            BufferedReader br = new BufferedReader(in);
            String line = null;
            while((line = br.readLine()) != null) {
                builder.append(line);
                builder.append("\r\n");
            }
            in.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return builder.toString();
    }
	
	public static boolean generateEntity(CustomInfo info, IProgressMonitor monitor) {
	    if(info == null || info.getTables() == null || info.getTables().isEmpty()) {
	        return false;
	    }
	    Connection conn = null;
	    String entityTpl = readTemplete("entity");
	    try {
            conn = getConnection(Utils.getDatabaseConfig(info.getDatasource()));
            for(List<String> table : info.getTables()) {
                String tableName = table.get(1);
                String tableComment = table.get(2);
                List<TableColumn> tableColumns = getTableColumns(tableName, conn);
                String fileName = underline2Camel(tableName, false) + ".java";
                Map<String,Object> data = getDataForFtl(info.getPackageName(), tableName, tableComment, tableColumns);
                freemarkerRender(entityTpl, info.getSaveDir()+"/"+fileName, data);
                monitor.worked(1);
            }
            
            if(info.getPackageFragment() != null) {
                try {
                    info.getPackageFragment().getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
            
            return true;
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        } finally {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
	    
	    return false;
	}
	
	private static Map<String,Object> getDataForFtl(String packageName, String tableName, String tableComment, List<TableColumn> tableColumns) {
	    
	    Map<String,Object> data = new HashMap<String,Object>();
	    
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createDate = format.format(new Date());
        String author = System.getProperty("user.name");
        
        data.put("tableComment", tableComment);
        data.put("author", author);
        data.put("entityName", underline2Camel(tableName, false));
        data.put("tableColumns", tableColumns);
        data.put("createDate", createDate);
        data.put("tableName", tableName);
        data.put("packageName", packageName);
	    
        return data;
	}
	
	public static List<TableColumn> getTableColumns(String tableName, Connection conn) {
	    
	    List<TableColumn> tableColumns = new ArrayList<TableColumn>();
	    
	    try {
            PreparedStatement pstate = conn.prepareStatement("show full fields from "+ tableName);
            ResultSet results = pstate.executeQuery();
            TableColumn col = null;
            while (results.next()) {
                col = new TableColumn();
                col.setName(results.getString("Field"));
                col.setType(mysqlProcessType(results.getString("Type")));
                col.setLowerName(underline2Camel(col.getName(), true));
                col.setUpperName(underline2Camel(col.getName(), false));
                col.setComment(results.getString("Comment"));
                String key = results.getString("Key");
                if(key != null && "PRI".equals(key)) {
                    col.setPrimaryKey(true);
                } else {
                    col.setPrimaryKey(false);
                }
                    
                tableColumns.add(col);
            }
        } catch (SQLException e) {
        }
	    
	    return tableColumns;
	}
	
	/**
     * MYSQL字段类型转换
     *
     * @param type
     *            字段类型
     * @return
     */
    protected static String mysqlProcessType(String type) {
        String t = type.toLowerCase();
        if (t.contains("char")) {
            return "java.lang.String";
        } else if (t.contains("bigint")) {
            return "java.lang.Long";
        } else if (t.contains("int")) {
            return "java.lang.Integer";
        } else if (t.contains("date") || t.contains("timestamp")) {
            return "java.util.Date";
        } else if (t.contains("text")) {
            return "java.lang.String";
        } else if (t.contains("bit")) {
            return "java.lang.Boolean";
        } else if (t.contains("decimal")) {
            return "java.math.BigDecimal";
        } else if (t.contains("blob")) {
            return "byte[]";
        } else if (t.contains("float")) {
            return "java.lang.Float";
        } else if (t.contains("double")) {
            return "java.lang.Double";
        } else if (t.contains("json") || t.contains("enum")) {
            return "java.lang.String";
        }
        return null;
    }
	
	private static void freemarkerRender(String tplFile,String outFile,Map<String,Object> data) {
        FileOutputStream fos = null;
        try {
            fos = new  FileOutputStream(outFile);
            //1.创建配置实例Cofiguration  
            Configuration cfg = new Configuration();  
            //字符串当做模板
            StringTemplateLoader stringLoader = new StringTemplateLoader();  
            stringLoader.putTemplate("mybatisGenerator",tplFile);  
            cfg.setTemplateLoader(stringLoader);  
            /**获取模板（template）  */
            Template template = cfg.getTemplate("mybatisGenerator","UTF-8"); 
            
            //获取输出流（指定到控制台（标准输出））  
            Writer out = new OutputStreamWriter(fos,"UTF-8");  
            //数据与模板合并（数据+模板=输出）  
            template.process(data, out);  
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (TemplateException e) {
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }
	
	/**
     * 下划线转驼峰法
     * @param line 源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line,boolean smallCamel){
        if(line==null||"".equals(line)){
            return "";
        }
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(smallCamel&&matcher.start()==0?Character.toLowerCase(word.charAt(0)):Character.toUpperCase(word.charAt(0)));
            int index=word.lastIndexOf('_');
            if(index>0){
                sb.append(word.substring(1, index).toLowerCase());
            }else{
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
    
    /**
     * 驼峰法转下划线
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line){
        if(line==null||"".equals(line)){
            return "";
        }
        line=String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(word.toLowerCase());
            sb.append(matcher.end()==line.length()?"":"_");
        }
        return sb.toString();
    }
}
