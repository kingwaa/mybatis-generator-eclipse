package com.dndc.mybatis.actions;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.dndc.mybatis.ui.TableDialog;

public class TableAction implements IObjectActionDelegate {

	private Shell shell;
	
	private IPackageFragment packageFragment;
	
	/**
	 * Constructor for Action1.
	 */
	public TableAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		new TableDialog(shell, packageFragment).open();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	    if(selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            
            packageFragment = (IPackageFragment) ssel.getFirstElement();
            System.out.println(packageFragment.getElementName());
            System.out.println(packageFragment.getPath().toOSString());
            System.out.println(Platform.getLocation().toOSString());
        }
	}

}
