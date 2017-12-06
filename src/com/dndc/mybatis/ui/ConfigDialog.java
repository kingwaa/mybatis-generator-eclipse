package com.dndc.mybatis.ui;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.dndc.mybatis.DatabaseConfig;
import com.dndc.mybatis.utils.Utils;

public class ConfigDialog extends BaseDialog {
	
    private Text datasource;
	private Text host;
	private Text port;
	private Text schema;
	private Text users;
	private Text password;
	private Combo encoding;
	
	public ConfigDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * 移除按钮
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButton(parent, IDialogConstants.NO_ID, "测试", false);
		super.createButton(parent, IDialogConstants.OK_ID, "确定", true);
		super.createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(IDialogConstants.NO_ID == buttonId) {
		    Connection conn = null;
		    try {
                conn = Utils.getConnection(getDatabaseConfig());
            } catch (ClassNotFoundException e) {
                setMessage("连接失败，内部错误", IMessageProvider.ERROR);
            } catch (SQLException e) {
                setMessage(e.getMessage(), IMessageProvider.ERROR);
            }
			if(conn != null) {
				setMessage("连接成功", IMessageProvider.NONE);
			}
		} else if(IDialogConstants.OK_ID == buttonId) {
				Utils.writeDatabaseConfig(getDatabaseConfig());
//				getShell().dispose();
//				new TableDialog(getShell(),configGenerator).open();
		}
		super.buttonPressed(buttonId);
	}
	
	private DatabaseConfig getDatabaseConfig() {
	    DatabaseConfig databaseConfig = new DatabaseConfig();
		String datasourceValue = datasource.getText();
		String hostValue = host.getText();
		String portValue = port.getText();
		String dbValue = schema.getText();
		String userValue = users.getText();
		String pwdValue = password.getText();
		String ecdValue = encoding.getText();
		if(!isCheckPass(datasourceValue, false, false, "数据源名称不能为空")) {
            return null;
        }
		databaseConfig.setName(datasourceValue);
		if(!isCheckPass(hostValue, false, false, "主机不能为空")) {
			return null;
		}
		databaseConfig.setHost(hostValue.trim());
		if(!isCheckPass(portValue, true, false, "端口号不能为空而且必须是数字")) {
			return null;
		}
		databaseConfig.setPort(portValue.trim());
		if(!isCheckPass(dbValue, false, false, "数据库名称不能为空")) {
			return null;
		}
		databaseConfig.setSchema(dbValue.trim());
		if(!isCheckPass(userValue, false, false, "用户名不能为空")) {
			return null;
		}
		databaseConfig.setUsername(userValue.trim());
		databaseConfig.setPassword(pwdValue == null ? "" : pwdValue.trim());
		databaseConfig.setEncoding(ecdValue);
		return databaseConfig;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		parent.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		getShell().setText(TITLE);
		
		 Composite container = (Composite) super.createDialogArea(parent);
		 
		 this.setTitle(TITLE);
		 this.setMessage("配置数据源信息");
		 
		 Composite composite = new Composite(container, SWT.FILL);
		 GridData gd_composite = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		 gd_composite.widthHint = 426;
		 composite.setLayoutData(gd_composite);
		 GridLayout gl_composite = new GridLayout(4, false);
		 gl_composite.verticalSpacing = 15;
		 gl_composite.marginBottom = 20;
		 gl_composite.marginTop = 20;
		 gl_composite.marginRight = 20;
		 gl_composite.marginLeft = 20;
		 composite.setLayout(gl_composite);
		 
		 Label dbsourceLabel = new Label(composite, SWT.NONE);
		 dbsourceLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		 dbsourceLabel.setText("数据源");
         
		 datasource = new Text(composite, SWT.BORDER);
		 GridData dGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		 dGridData.widthHint = 224;
		 datasource.setLayoutData(dGridData);
		 datasource.setText("");
		 
		 Label lblNewLabel = new Label(composite, SWT.NONE);
		 lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		 lblNewLabel.setText("主机");
		 
		 host = new Text(composite, SWT.BORDER);
		 host.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		 host.setText("127.0.0.1");
		 
		 Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		 lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		 lblNewLabel_1.setText(":");
		 
		 port = new Text(composite, SWT.BORDER);
		 GridData portGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		 portGridData.widthHint = 80;
		 port.setLayoutData(portGridData);
		 port.setText("3306");
		 
		 Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		 lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		 lblNewLabel_2.setText("数据库");
		 
		 schema = new Text(composite, SWT.BORDER);
		 GridData sGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		 sGridData.widthHint = 224;
		 schema.setLayoutData(sGridData);
		 schema.setText("test");
		 
		 Label lblUser = new Label(composite, SWT.NONE);
		 lblUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		 lblUser.setText("用户名");
		 
		 users = new Text(composite, SWT.BORDER);
		 GridData uGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
         uGridData.widthHint = 150;
		 users.setLayoutData(uGridData);
		 users.setText("root");
		 
		 Label lblPassword = new Label(composite, SWT.NONE);
		 lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		 lblPassword.setText("密码");
		 
		 password = new Text(composite, SWT.BORDER);
		 GridData pGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
         pGridData.widthHint = 150;
		 password.setLayoutData(pGridData);
		 
		 Label lbEncoding = new Label(composite, SWT.NONE);
		 lbEncoding.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		 lbEncoding.setText("编码");
         
         encoding = new Combo(composite, SWT.BORDER);
         GridData eGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
         eGridData.widthHint = 100;
         encoding.setLayoutData(eGridData);
         encoding.setItems(new String[]{"UTF-8", "GBK", "ISO-8859-1"});
         encoding.select(0);
		
	     return container;
	}
	
}
