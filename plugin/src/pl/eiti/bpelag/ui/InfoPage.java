package pl.eiti.bpelag.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import pl.eiti.bpelag.ui.controller.AnalyzerWizardController;
import pl.eiti.bpelag.ui.model.AnalyzerWizardModel;
import pl.eiti.bpelag.util.Messages;

public class InfoPage extends WizardPage {
	private AnalyzerWizardModel model = null;
	private AnalyzerWizardController controller = null;

	private Composite container;

	protected InfoPage(String pageName, AnalyzerWizardController newController, AnalyzerWizardModel newModel) {
		super(pageName);
		setTitle(Messages.WIZARD_INFO_PAGE_TITLE);
		model = newModel;
		controller = newController;
		// TODO Auto-generated constructor stub
		setPageComplete(false);
	}

	@Override
	public IWizardPage getNextPage() {
		IWizardPage nextPage = super.getNextPage();

		// TODO magic to run analyzer
		controller.executeAnalyze();
		controller.executeGenerator();
		setPageComplete(true);

		return nextPage;
	}

	@Override
	public boolean canFlipToNextPage() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void createControl(Composite parent) {
		// TODO information page
		container = new Composite(parent, SWT.NONE);
		setControl(container);
	}
}
