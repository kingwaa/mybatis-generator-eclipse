package com.dndc.mybatis.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.dndc.mybatis.utils.Utils;

public class TempleteDialog extends BaseDialog {
	
	private StyledText entityStyledText;

	public TempleteDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MAX);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButton(parent, IDialogConstants.RETRY_ID, "重置", false);
		super.createButton(parent, IDialogConstants.OK_ID, "保存", true);
		super.createButton(parent, IDialogConstants.CANCEL_ID, "取消", true);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(IDialogConstants.RETRY_ID == buttonId) {
		    entityStyledText.setText(Utils.readDefaultTemplete("entity"));
			setMessage("重置成功", IMessageProvider.INFORMATION);
		} else if(IDialogConstants.OK_ID == buttonId) {
			if(getTemplate()) {
				Utils.writeTemplete("entity", entityStyledText.getText());
//				new GeneratorProgressMonitorDialog(getShell()).open();
			}
		}
		super.buttonPressed(buttonId);
	}
	private boolean getTemplate() {
		String entityText = entityStyledText.getText();
		if(!isCheckPass(entityText, false, false, "Entity templete 不能为空")) {
			return false;
		}
		return true;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		getShell().setText(TITLE);
		
		this.setTitle(TITLE);
		this.setMessage("编辑模版");
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new BorderLayout());
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new BorderLayout.BorderData(BorderLayout.CENTER));
		composite.setLayout(new BorderLayout());
		
		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new BorderLayout.BorderData(BorderLayout.CENTER));
		
		TabItem entityItem = new TabItem(tabFolder, SWT.NONE);
		entityItem.setText("Entity");
		entityStyledText = new StyledText(tabFolder, SWT.V_SCROLL);
		entityStyledText.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		entityStyledText.setText(Utils.readTemplete("entity"));
		entityItem.setControl(entityStyledText);
		
//		setStyle();
		
		return container;
	}
	
	
	private void setStyle() {
	    
        Color red = Display.getDefault().getSystemColor(SWT.COLOR_RED);
        Color blue = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
        Color darkBlue = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
        
        Pattern redPattern = Pattern.compile("\\$\\{(.*?)\\}");
        Pattern bluePattern1 = Pattern.compile("\\<\\/*\\#(.*?)\\>");
        
        List<StyleRange> list = new ArrayList<StyleRange>();
        
        String entityStr = entityStyledText.getText();System.out.println(entityStr.length());
        Matcher redM = redPattern.matcher(entityStr);
        while (redM.find()) {
            StyleRange sr = new StyleRange(redM.start(), redM.end()-redM.start(), red, null);System.out.println(redM.start()+"   "+(redM.end()-redM.start()));
            list.add(sr);
        } 
        
        Matcher darkBlueM = bluePattern1.matcher(entityStr);
        while (darkBlueM.find()) {
            StyleRange sr = new StyleRange(darkBlueM.start(), darkBlueM.end()-darkBlueM.start(), darkBlue, null);
            list.add(sr);
        } 
        
        entityStyledText.setStyleRanges((StyleRange[]) list.toArray(new StyleRange[list.size()])); 
                
            
	}
}
