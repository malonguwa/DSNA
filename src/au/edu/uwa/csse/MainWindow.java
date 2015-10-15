package au.edu.uwa.csse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MainWindow {
	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);

		shell.setText("Developer Social Network Demo");
		shell.setLayout(new FillLayout());

		//shell.pack();

		final Label urlLabel = new Label(shell, SWT.CENTER);
		urlLabel.setText("Repository URL:");
		final Text url = new Text(shell, SWT.BORDER);
		//Rectangle clientArea = shell.getClientArea();
		url.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		//text.setBounds(clientArea.x + 100, clientArea.y + 100, 200, 20);
		
		
		final Label nameLabel = new Label(shell, SWT.CENTER);
	    nameLabel.setText("Project Name:");
	    
		final Text project = new Text(shell, SWT.BORDER);
		//text.setBounds(clientArea.x + 100, clientArea.y + 100, 200, 20);
		
		final Label label = new Label(shell, SWT.CENTER);
	    label.setText("c:\\");

		final DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setFilterPath("c:\\"); // Windows specific
		
		Button chooseDirectory = new Button(shell, SWT.NONE);
		//start.setData("newKey", null);
		chooseDirectory.setText("Choose directory");
		
		Button start1 = new Button(shell, SWT.NONE);
		start1.setText("Step 1");
		
		Button start2 = new Button(shell, SWT.NONE);
		start2.setText("Step 2");
		
		Button start3 = new Button(shell, SWT.NONE);
		start3.setText("Step 3");

		start1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final String cvs_root = url.getText();
				final String localPath = label.getText();
				final String projectName = project.getText();

				CVSLogExtractor.getCode(cvs_root, localPath,
				   projectName);
			}
		});
		
		start2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final String cvs_root = url.getText();
				final String localPath = label.getText();
				final String projectName = project.getText();

				CVSLogExtractor.getLog(cvs_root, localPath,
						projectName);
			}
		});
		
		start3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final String cvs_root = url.getText();
				final String localPath = label.getText();
				final String projectName = project.getText();

				CVSLogExtractor.analysisLog(projectName, localPath);
			}
		});
		
		chooseDirectory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.open();
				label.setText(dialog.getFilterPath());
			}
		});

		shell.open();
		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				// If no more entries in the event queue
				display.sleep();
			}
		}
		display.dispose();
	}
}
