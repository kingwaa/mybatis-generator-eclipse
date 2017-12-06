package com.dndc.mybatis.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.dndc.mybatis.CustomInfo;
import com.dndc.mybatis.utils.Utils;

public class TableDialog extends BaseDialog {
	
	private CheckboxTableViewer tableViewer;
	private Combo dbCombo;
	private Text saveDir;
    private Text packageName;
    
    private IPackageFragment packageFragment;
	
	public TableDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public TableDialog(Shell parentShell, IPackageFragment packageFragment) {
        super(parentShell);
        this.packageFragment = packageFragment;
    }
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButton(parent, IDialogConstants.NEXT_ID, "编辑模版", true);
		super.createButton(parent, IDialogConstants.OK_ID, "开始", true);
		super.createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}
	
	@SuppressWarnings("unchecked")
    @Override
	protected void buttonPressed(int buttonId) {
		if(IDialogConstants.BACK_ID == buttonId) {
			getShell().dispose();
			new ConfigDialog(getShell()).open();
		} else if(IDialogConstants.NEXT_ID == buttonId) {
		    new TempleteDialog(getShell()).open();
		} else if (IDialogConstants.OK_ID == buttonId) {
		    Object[] elements = tableViewer.getCheckedElements();
		    if(elements == null || elements.length == 0) {
		        setErrorMessage("请至少选择一个表");
		        return;
		    }
		    List<List<String>> tables = new ArrayList<List<String>>();
		    for(Object obj : elements) {
		        List<String> el = (List<String>)obj;
		        tables.add(el);
		    }
		    
		    CustomInfo info = new CustomInfo();
		    info.setTables(tables);
		    info.setPackageFragment(packageFragment);
		    info.setPackageName(packageName.getText().trim());
		    info.setSaveDir(saveDir.getText().trim());
		    info.setDatasource(dbCombo.getText());
		    new GeneratorProgressMonitorDialog(getShell(), info).open();
//		    String javaCode = "package com.aptech.plugin;public class HelloWorld{public static void main(String[] args){System.out.println(\"中华人民共和国\");}}"; 
//		    try {
//                packageFragment.createCompilationUnit("HelloWord.java", javaCode, true, new NullProgressMonitor());
//            } catch (JavaModelException e) {
//                e.printStackTrace();
//            }
		}
		super.buttonPressed(buttonId);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		getShell().setText(TITLE);
		
		Composite container = (Composite) super.createDialogArea(parent);
		 
		this.setTitle(TITLE);
		this.setMessage("选择要生产代码的表");
		
		Composite composite = new Composite(container, SWT.NONE);
		 
		RowLayout rl_container = new RowLayout();
		rl_container.type = SWT.VERTICAL;
		rl_container.fill = true;
		rl_container.marginTop = 10;
		rl_container.marginRight = 10;
		rl_container.marginLeft = 10;
		rl_container.marginHeight = 10;
		rl_container.marginBottom = 10;
		composite.setLayout(rl_container);
		
		Composite dbComposite = new Composite(composite, SWT.NONE);
		GridLayout gd_dbComposite = new GridLayout(3, false);
		dbComposite.setLayout(gd_dbComposite);
		
		Label dbLabel = new Label(dbComposite, SWT.NONE);
		dbLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		dbLabel.setText("选择数据源");
		dbCombo = new Combo(dbComposite, SWT.BORDER | SWT.FULL_SELECTION);
		dbCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		dbCombo.setItems(Utils.getDatasources());
		
		dbCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                String datasource = dbCombo.getText();
                List<List<String>> tableInfos = Utils.getTableInfos(datasource);
                tableViewer.setInput(tableInfos);
            }
        });
		
		Button btn = new Button(dbComposite, SWT.NONE);
		btn.setText("新增");
		GridData btnGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		btnGridData.widthHint = 80;
		btn.setLayoutData(btnGridData);
		
		btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                super.mouseDown(e);
                ConfigDialog cfgDialog = new ConfigDialog(TableDialog.this.getShell());
                if(IDialogConstants.OK_ID == cfgDialog.open()) {
                    dbCombo.setItems(Utils.getDatasources());
                    if(dbCombo.getItemCount()>0){
                        dbCombo.select(dbCombo.getItemCount()-1);
                    }
                }
            }
        });
		
		Label filterLabel = new Label(dbComposite, SWT.NONE);
        filterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        filterLabel.setText("过滤");
        
        final Text filterText = new Text(dbComposite, SWT.BORDER | SWT.FULL_SELECTION);
        filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        final ValueFilter filter = new ValueFilter();
        
        filterText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String value = filterText.getText();
                filter.setFilterValue(value);
                tableViewer.refresh();
            }
        });
        
        final Button selAllBtn = new Button(dbComposite, SWT.CHECK);
        selAllBtn.setText("全选");
        
        selAllBtn.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                tableViewer.setAllChecked(selAllBtn.getSelection());
            }
        });
		
		Table table = new Table(composite, SWT.CHECK|SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER|SWT.V_SCROLL | SWT.FILL);
		tableViewer = new CheckboxTableViewer(table);
		table.setLayoutData(new RowData(550, 324));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tableViewer.setContentProvider(new TvContentProvider());
		tableViewer.setLabelProvider(new TvLabelProvider());
		tableViewer.addFilter(filter);
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn.setResizable(false);
		tblclmnNewColumn.setWidth(30);
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.LEFT);
		tblclmnNewColumn_2.setWidth(160);
		tblclmnNewColumn_2.setText("表名");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.LEFT);
		tblclmnNewColumn_1.setWidth(360);
		tblclmnNewColumn_1.setText("备注");
		
		Composite fileComposite = new Composite(composite, SWT.NONE);
        GridLayout gd_fileComposite = new GridLayout(3, false);
        fileComposite.setLayout(gd_fileComposite);
        
        Label lblSaveDirectory = new Label(fileComposite, SWT.NONE);
        lblSaveDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblSaveDirectory.setText("保存目录");
        
        saveDir = new Text(fileComposite, SWT.BORDER);
        saveDir.setToolTipText("选择保存生产文件的目录");
        saveDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        Button saveBtn = new Button(fileComposite, SWT.NONE);
        saveBtn.setText("浏览");
        GridData saveBtnGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        saveBtnGridData.widthHint = 80;
        saveBtn.setLayoutData(saveBtnGridData);
        saveBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                DirectoryDialog dirDialog = new DirectoryDialog(TableDialog.this.getShell());
                String dir = dirDialog.open();
                if(dir != null && !"".equals(dir)) {
                    saveDir.setText(dir);
                }
                super.mouseDown(e);
            }
        });
        
        Label lblPackageName = new Label(fileComposite, SWT.NONE);
        lblPackageName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblPackageName.setText("包名");
        
        packageName = new Text(fileComposite, SWT.BORDER);
        packageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        
        if(packageFragment != null) {
            String packagePath = packageFragment.getPath().toOSString();
            String workspacePath = Platform.getLocation().toOSString();
            saveDir.setText(workspacePath + packagePath);
            packageName.setText(packageFragment.getElementName());
            saveDir.setEnabled(false);
            packageName.setEnabled(false);
            saveBtn.setEnabled(false);
        }
        
        if(dbCombo.getItemCount()>0){
            dbCombo.select(0);
        }
		
		return container;
	}
	
	class TvContentProvider implements IStructuredContentProvider{  
	    
        @Override  
        public void dispose() {  
            // TODO Auto-generated method stub  
              
        }  
  
        @Override  
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {  
  
        }  
  
        @Override  
        public Object[] getElements(Object inputElement) {  
            // TODO Auto-generated method stub  
            if(inputElement instanceof List){  
                return ((List) inputElement).toArray();  
            }  
            return new Object[0];  
        }  
    }  
	
	class TvLabelProvider implements ITableLabelProvider {
	    
        public Image getColumnImage(Object element, int columnIndex) {
            // TODO Auto-generated method stub
 
            return null;
        }
 
        public String getColumnText(Object element, int columnIndex) {
            // TODO Auto-generated method stub
            List<String> list = (List<String>) element;
            return list.get(columnIndex);
        }

        @Override
        public void addListener(ILabelProviderListener arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void dispose() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public boolean isLabelProperty(Object arg0, String arg1) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener arg0) {
            // TODO Auto-generated method stub
            
        }
	}
	
	class ValueFilter extends ViewerFilter {
        
        String value;
        
        public void setFilterValue(String value) {
            this.value = value;
        }
        
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            
            if(value == null || "".equals(value)) {
                return true;
            }
            
            // TODO Auto-generated method stub
            List<String> info = (List<String>)element;
            for(String str : info) {
                if(str.contains(value)) {
                    return true;
                }
            }
            return false;
          
        }
	}
}
