package org.slive.quickcmd.actions;

import java.io.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * 
 * @author Slive
 *
 */
@SuppressWarnings("restriction")
public class QuickCmdAction implements IObjectActionDelegate 
{
	private Object selected = null;
	private Class<?> selectedClass = null;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
	}

	public void run(IAction action) 
	{
		if (this.selected == null) 
		{
			MessageDialog.openInformation(new Shell(), "Quick Cmd",
					"Unable to cmd " + this.selectedClass.getName());
			return;
		}
		File directory = null;
		if ((this.selected instanceof IResource)) 
		{
			directory = new File(((IResource) this.selected).getLocation()
					.toOSString());
		} 
		else if ((this.selected instanceof File)) {
			directory = (File) this.selected;
		}

		try 
		{
			String filePath = "";
			String startPath = "c:";
			if (directory != null) 
			{
				if (directory.isDirectory()) 
				{
					filePath = directory.getAbsolutePath();
				} 
				else 
				{
					filePath = directory.getParentFile().getAbsolutePath();
				}
				
				// ��ȡ��ǰ�ļ��Ŀ�ͷ�̷�
				startPath = filePath.substring(0, 2);
			}
			
			//����CMD������л�����ǰĿ¼��
			Runtime.getRuntime().exec("cmd /c start cd " + filePath, null,
					new File(startPath));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		IAdaptable adaptable = null;
		this.selected = null;
		if ((selection instanceof IStructuredSelection)) 
		{
			adaptable = (IAdaptable) ((IStructuredSelection) selection).getFirstElement();
			this.selectedClass = adaptable.getClass();
			if ((adaptable instanceof IResource))
			{
				this.selected = ((IResource) adaptable);
			}
			else if (((adaptable instanceof PackageFragment))
					&& ((((PackageFragment) adaptable).getPackageFragmentRoot() instanceof JarPackageFragmentRoot)))
			{
				this.selected = getJarFile(((PackageFragment) adaptable)
						.getPackageFragmentRoot());
			}
			else if ((adaptable instanceof JarPackageFragmentRoot))
			{
				this.selected = getJarFile(adaptable);
			}
			else
			{
				this.selected = ((IResource) adaptable.getAdapter(IResource.class));
			}
		}
	}

	protected File getJarFile(IAdaptable adaptable) 
	{
		JarPackageFragmentRoot jpfr = (JarPackageFragmentRoot) adaptable;
		File selected = jpfr.getPath().makeAbsolute().toFile();
		if (!selected.exists()) 
		{
			File projectFile = new File(jpfr.getJavaProject().getProject()
					.getLocation().toOSString());
			selected = new File(projectFile.getParent() + selected.toString());
		}
		return selected;
	}
}
