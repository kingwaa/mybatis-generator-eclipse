package com.dndc.mybatis.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.dndc.mybatis.CustomInfo;
import com.dndc.mybatis.utils.Utils;

public class GeneratorProgressMonitorDialog {
	
	private Shell shell;
	private CustomInfo info;
	private int totalTask;
	
	private static final String TITLE = "Mybatis Generator";

	public GeneratorProgressMonitorDialog(Shell parent, CustomInfo info) {
		this.shell = parent;
		this.info = info;
		if(info.getTables() == null) {
			this.totalTask = 10000;
		} else {
			this.totalTask = info.getTables().size();
		}
	}
	
	public void open() {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				
			    monitor.beginTask("start generator", totalTask);
                monitor.worked(0);
                
                Utils.generateEntity(info, monitor);
			    
				monitor.done();
			}
		};
		try {
			new ProgressMonitorDialog(shell).run(false, false, runnable);
		} catch (InvocationTargetException e) {
			MessageDialog.openError(shell,TITLE,e.getMessage());
		} catch (InterruptedException e) {
			MessageDialog.openError(shell,TITLE,e.getMessage());
		}
	}
}
