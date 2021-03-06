package pl.eiti.bpelcg.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.bpel.model.Variable;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import pl.eiti.bpelcg.ui.controller.AnalyzerWizardController;
import pl.eiti.bpelcg.ui.model.AnalyzerWizardModel;
import pl.eiti.bpelcg.util.Messages;
import pl.eiti.bpelcg.util.Settings;

/**
 * Assign instructions edit wizard page.
 */
public class AssignPage extends WizardPage {
	private Display dev = null;

	private AnalyzerWizardModel model = null;
	private AnalyzerWizardController controller = null;

	private Composite mainContainer = null;
	private Composite assignContainer = null;
	private Composite copyContainer = null;
	private Composite copyListContainer = null;
	private Composite copyFromContainer = null;
	private Composite copyToContainer = null;

	private Composite copyButtonContainer = null;
	private Composite copyFromComboContainer = null;
	private Composite copyToComboContainer = null;

	private List assignList = null;

	private List generatedMarker = null;
	private List copyElemList = null;

	private Button newButton = null;
	private Button delButton = null;
	private Button moveUpButton = null;
	private Button moveDownButton = null;

	private Label copyFromLabel = null;
	private Combo copyFromCombo = null;
	private Tree copyFromList = null;

	private Label copyToLabel = null;
	private Combo copyToCombo = null;
	private Tree copyToList = null;

	private Boolean isExpandEvent = Boolean.FALSE;

	/**
	 * Constructor setting references to model and controller of the wizard.
	 * 
	 * @param pageName
	 *            name of wizard page.
	 * @param newController
	 *            wizard controller.
	 * @param newModel
	 *            wizard model.
	 */
	protected AssignPage(String pageName, AnalyzerWizardController newController, AnalyzerWizardModel newModel) {
		super(pageName);
		dev = Display.getCurrent();
		if (null == dev) {
			dev = Display.getDefault();
		}
		setTitle(Messages.WIZARD_ASSIGN_PAGE_TITLE);
		model = newModel;
		controller = newController;
	}

	@Override
	public void createControl(Composite parent) {
		mainContainer = new Composite(parent, SWT.NULL);

		setControl(mainContainer);
		mainContainer.setLayout(new RowLayout(SWT.HORIZONTAL));

		assignContainer = new Composite(mainContainer, SWT.NONE);
		RowLayout rl_assignContainer = new RowLayout(SWT.VERTICAL);
		assignContainer.setLayout(rl_assignContainer);
		assignContainer.setLayoutData(new RowData(163, 227));

		Label lblAssignBlocks = new Label(assignContainer, SWT.NONE);
		lblAssignBlocks.setText(Messages.ASSIGN_LABEL_ASSIGNBLOCKS);

		assignList = new List(assignContainer, SWT.BORDER);
		assignList.setLayoutData(new RowData(150, 200));

		copyContainer = new Composite(mainContainer, SWT.NONE);
		RowLayout rl_copyContainer = new RowLayout(SWT.VERTICAL);
		copyContainer.setLayout(rl_copyContainer);
		copyContainer.setLayoutData(new RowData(210, 290));

		Label lblCopyElements = new Label(copyContainer, SWT.NONE);
		lblCopyElements.setText(Messages.ASSIGN_LABEL_COPYELEMENTS);

		copyListContainer = new Composite(copyContainer, SWT.NONE);
		RowLayout rl_copyListContainer = new RowLayout(SWT.HORIZONTAL);
		rl_copyListContainer.spacing = 0;
		copyListContainer.setLayout(rl_copyListContainer);
		copyListContainer.setLayoutData(new RowData(200, 155));

		generatedMarker = new List(copyListContainer, SWT.BORDER);
		generatedMarker.setLayoutData(new RowData(10, 140));
		generatedMarker.setEnabled(Boolean.FALSE);

		copyElemList = new List(copyListContainer, SWT.BORDER | SWT.V_SCROLL);
		copyElemList.setLayoutData(new RowData(150, 140));

		copyButtonContainer = new Composite(copyContainer, SWT.NONE);
		copyButtonContainer.setLayout(new RowLayout(SWT.HORIZONTAL));
		copyButtonContainer.setLayoutData(new RowData(200, 66));

		newButton = new Button(copyButtonContainer, SWT.NONE);
		newButton.setLayoutData(new RowData(95, SWT.DEFAULT));
		newButton.setText(Messages.ASSIGN_BUTTON_NEW);

		delButton = new Button(copyButtonContainer, SWT.NONE);
		delButton.setLayoutData(new RowData(95, SWT.DEFAULT));
		delButton.setText(Messages.ASSIGN_BUTTON_DELETE);

		moveUpButton = new Button(copyButtonContainer, SWT.NONE);
		moveUpButton.setGrayed(true);
		moveUpButton.setLayoutData(new RowData(95, SWT.DEFAULT));
		moveUpButton.setText(Messages.ASSIGN_BUTTON_MOVEUP);

		moveDownButton = new Button(copyButtonContainer, SWT.NONE);
		moveDownButton.setLayoutData(new RowData(95, SWT.DEFAULT));
		moveDownButton.setText(Messages.ASSIGN_BUTTON_MOVEDOWN);

		copyFromContainer = new Composite(mainContainer, SWT.NONE);
		copyFromContainer.setLayout(new RowLayout(SWT.VERTICAL));
		copyFromContainer.setLayoutData(new RowData(330, 333));

		copyFromComboContainer = new Composite(copyFromContainer, SWT.NONE);
		RowLayout rl_copyFromComboContainer = new RowLayout(SWT.HORIZONTAL);
		rl_copyFromComboContainer.justify = true;
		rl_copyFromComboContainer.center = true;
		copyFromComboContainer.setLayout(rl_copyFromComboContainer);
		copyFromComboContainer.setLayoutData(new RowData(201, 30));

		copyFromLabel = new Label(copyFromComboContainer, SWT.NONE);
		copyFromLabel.setText(Messages.ASSIGN_LABEL_FROM);

		copyFromCombo = new Combo(copyFromComboContainer, SWT.READ_ONLY);
		copyFromCombo.setLayoutData(new RowData(100, SWT.DEFAULT));
		copyFromCombo.setEnabled(Boolean.FALSE);

		copyFromList = new Tree(copyFromContainer, SWT.BORDER | SWT.SINGLE);
		copyFromList.setLayoutData(new RowData(300, 240));

		copyToContainer = new Composite(mainContainer, SWT.NONE);
		copyToContainer.setLayout(new RowLayout(SWT.VERTICAL));
		copyToContainer.setLayoutData(new RowData(330, 333));

		copyToComboContainer = new Composite(copyToContainer, SWT.NONE);
		RowLayout rl_copyToComboContainer = new RowLayout(SWT.HORIZONTAL);
		rl_copyToComboContainer.center = true;
		rl_copyToComboContainer.justify = true;
		copyToComboContainer.setLayout(rl_copyToComboContainer);
		copyToComboContainer.setLayoutData(new RowData(195, 30));

		copyToLabel = new Label(copyToComboContainer, SWT.NONE);
		copyToLabel.setText(Messages.ASSIGN_LABEL_TO);

		copyToCombo = new Combo(copyToComboContainer, SWT.READ_ONLY);
		copyToCombo.setLayoutData(new RowData(100, SWT.DEFAULT));
		copyToCombo.setEnabled(Boolean.FALSE);

		copyToList = new Tree(copyToContainer, SWT.BORDER | SWT.SINGLE);
		copyToList.setLayoutData(new RowData(300, 240));

		/** Data load section */

		for (String assignName : model.getAssignNameList()) {
			assignList.add(assignName);
		}

		addDataToCombo(copyFromCombo, model.getFromComboList());
		addDataToCombo(copyToCombo, model.getToComboList());

		/** Add listeners section */
		assignList.addSelectionListener(new AssignSelectionListener());
		copyElemList.addSelectionListener(new CopySelectionListener());
		newButton.addSelectionListener(new NewCopySelectionListener());
		delButton.addSelectionListener(new DeleteCopySelectionListener());
		moveDownButton.addSelectionListener(new MoveDownSelectionListener());
		moveUpButton.addSelectionListener(new MoveUpSelectionListener());
		copyFromCombo.addSelectionListener(new FromTypeSelectionListener());
		copyToCombo.addSelectionListener(new ToTypeSelectionListener());
		copyFromList.addSelectionListener(new FromElementSelectionListener());
		copyToList.addSelectionListener(new ToElementSelectionListener());
	}

	private void addDataToCombo(Combo combobox, java.util.List<String> list) {
		for (String it : list) {
			combobox.add(it);
		}
	}

	/**
	 * Instance of selection listener class that listening for select on Assign
	 * blocks list element.
	 */
	private class AssignSelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {

		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			int selectionIndex = assignList.getSelectionIndex();

			controller.generateMarkers(selectionIndex);

			copyElemList.removeAll();
			copyFromCombo.deselectAll();
			copyToCombo.deselectAll();
			copyFromList.removeAll();
			copyToList.removeAll();
			generatedMarker.removeAll();

			for (String elem : model.getCopyListNames(selectionIndex)) {
				copyElemList.add(elem);
			}

			for (String it : model.getMarkers()) {
				generatedMarker.add(it);
			}

		}
	}

	/**
	 * Instance of selection listener that listening for select on Copy elements
	 * list.
	 */
	private class CopySelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// nothing to do here.
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			model.setCurrentlyProcessingCopy(copyElemList.getSelectionIndex());

			Integer fromIndex = model.getFromTypeCopyIndex(copyElemList.getSelectionIndex());
			Integer toIndex = model.getToTypeCopyIndex(copyElemList.getSelectionIndex());

			if (null != fromIndex && !(fromIndex < 0)) {
				copyFromCombo.select(fromIndex);
				Event comboSelect = new Event();
				comboSelect.item = copyFromCombo;
				copyFromCombo.notifyListeners(SWT.Selection, comboSelect);
			} else {
				copyFromCombo.select(model.getFromComboList().indexOf(Messages.ASSIGN_CATEGORY_VARPART));
			}

			if (null != toIndex && !(toIndex < 0)) {
				copyToCombo.select(toIndex);
				Event comboSelect = new Event();
				comboSelect.item = copyToCombo;
				copyToCombo.notifyListeners(SWT.Selection, comboSelect);
			} else {
				copyToCombo.select(model.getToComboList().indexOf(Messages.ASSIGN_CATEGORY_VARPART));
			}

			if (copyFromList.getItems().length <= 0 && copyToList.getItems().length <= 0) {
				createVarTree(copyFromList);
				createVarTree(copyToList);
			}

			unexpandTree(copyFromList);
			unexpandTree(copyToList);

			copyFromList.deselectAll();
			copyToList.deselectAll();

			if (null != model.getFromVarIndex()) {
				copyFromList.getItem(model.getFromVarIndex()).setExpanded(Boolean.TRUE);
				TreeItem messageFrom = getItemWithName(copyFromList.getItem(model.getFromVarIndex()), model
						.getFromPartName());
				messageFrom.setExpanded(Boolean.TRUE);
				TreeItem elementFrom = getItemWithName(messageFrom, model.getElementNameFrom().split(":")[1]);
				copyFromList.setSelection(elementFrom);
			}

			if (null != model.getToVarIndex()) {
				copyToList.getItem(model.getToVarIndex()).setExpanded(Boolean.TRUE);
				TreeItem messageTo = getItemWithName(copyToList.getItem(model.getToVarIndex()), model.getToPartName());
				messageTo.setExpanded(Boolean.TRUE);
				TreeItem elementTo = getItemWithName(messageTo, model.getElementNameTo().split(":")[1]);
				copyToList.setSelection(elementTo);
			}
		}

		/**
		 * Find element with given name in tree.
		 * 
		 * @param parent
		 *            parent tree element to searching.
		 * @param name
		 *            name of element to find in tree.
		 * @return item with given name.
		 */
		private TreeItem getItemWithName(TreeItem parent, String name) {
			TreeItem result = null;
			if (parent.getItems().length > 0) {
				if (parent.getItems().length > 1) {
					for (TreeItem item : parent.getItems()) {
						String[] elements = item.getText().split(":");
						if (name.equals(elements[0].trim())) {
							result = item;
							break;
						}
					}
				} else {
					result = parent.getItem(0);
				}
			}

			return result;
		}

		/**
		 * Creates tree element for variable of complex type.
		 * 
		 * @param parent
		 *            parent tree element.
		 */
		private void createVarTree(Tree parent) {
			for (Variable var : model.getProcessVariables()) {
				String itemLabel = var.getName();

				TreeItem newTreeItem = new TreeItem(parent, SWT.NONE);

				URL varImgURL = Platform.getBundle(Settings.PROJECT_ID).getEntry(Settings.VAR_IMG_PATH);
				URL mesImgURL = Platform.getBundle(Settings.PROJECT_ID).getEntry(Settings.MES_IMG_PATH);
				URL eleImgURL = Platform.getBundle(Settings.PROJECT_ID).getEntry(Settings.ELE_IMG_PATH);

				Image varIconImg = null;
				Image messIconImg = null;
				Image elemIconImg = null;

				try {
					varIconImg = new Image(dev, new ImageData(FileLocator.resolve(varImgURL).toString().substring(6)));
					messIconImg = new Image(dev, new ImageData(FileLocator.resolve(mesImgURL).toString().substring(6)));
					elemIconImg = new Image(dev, new ImageData(FileLocator.resolve(eleImgURL).toString().substring(6)));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				newTreeItem.setImage(varIconImg);

				if (null != var.getType()) {
					itemLabel += " : " + var.getType().getName();
				} else if (null != var.getMessageType()) {
					itemLabel += " : " + var.getMessageType().getQName().getLocalPart();

					Map<String, java.util.List<String>> resolvedMessages = controller.resolveMessageType(var
							.getMessageType());

					for (String parIter : resolvedMessages.keySet()) {
						TreeItem newPartTreeItem = new TreeItem(newTreeItem, SWT.NONE);
						newPartTreeItem.setText(parIter);
						newPartTreeItem.setImage(messIconImg);
						for (String elemIter : resolvedMessages.get(parIter)) {
							TreeItem newElementTreeItem = new TreeItem(newPartTreeItem, SWT.NONE);
							newElementTreeItem.setText(elemIter);
							newElementTreeItem.setImage(elemIconImg);
						}
					}
				}

				newTreeItem.setText(itemLabel);
			}

		}
	}

	/**
	 * Expands tree element.
	 * 
	 * @param treeElem
	 *            tree to expand.
	 */
	private void unexpandTree(Tree treeElem) {
		isExpandEvent = Boolean.TRUE;
		for (TreeItem item : treeElem.getItems()) {
			if (item.getExpanded()) {
				item.setExpanded(Boolean.FALSE);
				for (TreeItem mes : item.getItems()) {
					mes.setExpanded(Boolean.FALSE);
				}
			}
		}
		isExpandEvent = Boolean.FALSE;
	}

	/**
	 * Instance of selection listener that listening for select on copy from
	 * type combo.
	 */
	private class FromTypeSelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// nothing to do here.
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			String copyType = null;
			int selectionIndex = copyFromCombo.getSelectionIndex();

			copyType = model.getFromComboList().get(selectionIndex);

			copyFromList.setVisible(Boolean.FALSE);

			switch (copyType) {
			case Messages.ASSIGN_CATEGORY_VARPART:
				copyFromList.setVisible(Boolean.TRUE);
				break;
			case Messages.ASSIGN_CATEGORY_EXPRESSION:
				break;
			case Messages.ASSIGN_CATEGORY_LITERAL:
				break;
			case Messages.ASSIGN_CATEGORY_VARPROPERTY:
				break;
			case Messages.ASSIGN_CATEGORY_PARTNERROLE:
				break;
			case Messages.ASSIGN_CATEGORY_ENDPOINTREF:
				break;
			case Messages.ASSIGN_CATEGORY_OPAQUE:
				break;
			}
		}
	}

	/**
	 * Instance of selection listener that listening for select on copy to type
	 * combo.
	 */
	private class ToTypeSelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// nothing to do here.
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			String copyType = null;
			int selectionIndex = copyToCombo.getSelectionIndex();

			copyType = model.getToComboList().get(selectionIndex);

			switch (copyType) {
			case Messages.ASSIGN_CATEGORY_VARPART:
				copyToList.setVisible(Boolean.TRUE);
				break;
			case Messages.ASSIGN_CATEGORY_EXPRESSION:
				copyToList.setVisible(Boolean.FALSE);
				break;
			case Messages.ASSIGN_CATEGORY_VARPROPERTY:
				copyToList.setVisible(Boolean.FALSE);
				break;
			case Messages.ASSIGN_CATEGORY_PARTNERROLE:
				copyToList.setVisible(Boolean.FALSE);
				break;
			}
		}
	}

	/**
	 * Instance of selection listener that listening for select on copy from
	 * element.
	 */
	private class FromElementSelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// nothing to do here.
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			if (!isExpandEvent) {
				TreeItem selectedItem = copyFromList.getSelection()[0];
				java.util.List<String> elements = new ArrayList<>();

				while (null != selectedItem) {
					elements.add(selectedItem.getText());
					selectedItem = selectedItem.getParentItem();
				}

				model.getCurrentlyProcessingCopy().setFrom(controller.createFromVarPart(elements));

				copyElemList.removeAll();

				for (String elem : model.getCopyListNames(model.getAssignList().indexOf(
						model.getCurrentlyProcessingAssign()))) {
					copyElemList.add(elem);
				}
			}
		}

	}

	/**
	 * Instance of selection listener that listening for select on copy to
	 * element.
	 */
	private class ToElementSelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// nothing to do here.
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			if (!isExpandEvent) {
				TreeItem selectedItem = copyToList.getSelection()[0];
				java.util.List<String> elements = new ArrayList<>();

				while (null != selectedItem) {
					elements.add(selectedItem.getText());
					selectedItem = selectedItem.getParentItem();
				}

				model.getCurrentlyProcessingCopy().setTo(controller.createToVarPart(elements));
				copyElemList.removeAll();

				for (String elem : model.getCopyListNames(model.getAssignList().indexOf(
						model.getCurrentlyProcessingAssign()))) {
					copyElemList.add(elem);
				}
			}
		}

	}

	/**
	 * Add copy instruction command listener.
	 */
	private class NewCopySelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// nothing to do here.
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			controller.addNewCopy();
			copyElemList.removeAll();
			copyFromCombo.deselectAll();
			copyToCombo.deselectAll();
			copyFromList.removeAll();
			copyToList.removeAll();

			for (String elem : model.getCopyListNames(assignList.getSelectionIndex())) {
				copyElemList.add(elem);
			}
		}
	}

	/**
	 * Delete copy instruction command listener.
	 */
	private class DeleteCopySelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// nothing to do here.
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			Integer copySelectionIndex = copyElemList.getSelectionIndex();
			if (copySelectionIndex >= 0) {
				controller.deleteCopy(copySelectionIndex);
				copyElemList.removeAll();
				copyFromCombo.deselectAll();
				copyToCombo.deselectAll();
				copyFromList.removeAll();
				copyToList.removeAll();
				generatedMarker.removeAll();

				for (String elem : model.getCopyListNames(assignList.getSelectionIndex())) {
					copyElemList.add(elem);
				}

				controller.generateMarkers(assignList.getSelectionIndex());

				for (String it : model.getMarkers()) {
					generatedMarker.add(it);
				}
			}
		}

	}

	/**
	 * Move down command listener.
	 */
	private class MoveDownSelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// Nothing to implement here.
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			int copySelectionIndex = copyElemList.getSelectionIndex();

			if (copySelectionIndex >= 0 && copySelectionIndex < copyElemList.getItemCount() - 1) {

				controller.moveDownCopy(copySelectionIndex);

				controller.generateMarkers(assignList.getSelectionIndex());

				copyElemList.removeAll();
				copyFromCombo.deselectAll();
				copyToCombo.deselectAll();
				copyFromList.removeAll();
				copyToList.removeAll();
				generatedMarker.removeAll();

				for (String elem : model.getCopyListNames(assignList.getSelectionIndex())) {
					copyElemList.add(elem);
				}

				for (String it : model.getMarkers()) {
					generatedMarker.add(it);
				}

				copyElemList.select(copySelectionIndex + 1);
				Event copyElemListEvent = new Event();
				copyElemListEvent.item = copyFromCombo;
				copyElemList.notifyListeners(SWT.Selection, copyElemListEvent);
			}
		}

	}

	/**
	 * Move up command listener.
	 */
	private class MoveUpSelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// Nothing to implement here.
		}

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			int copySelectionIndex = copyElemList.getSelectionIndex();

			if (copySelectionIndex > 0 && copySelectionIndex < copyElemList.getItemCount()) {

				controller.moveUpCopy(copySelectionIndex);

				controller.generateMarkers(assignList.getSelectionIndex());

				copyElemList.removeAll();
				copyFromCombo.deselectAll();
				copyToCombo.deselectAll();
				copyFromList.removeAll();
				copyToList.removeAll();
				generatedMarker.removeAll();

				for (String elem : model.getCopyListNames(assignList.getSelectionIndex())) {
					copyElemList.add(elem);
				}

				for (String it : model.getMarkers()) {
					generatedMarker.add(it);
				}

				copyElemList.select(copySelectionIndex - 1);
				Event copyElemListEvent = new Event();
				copyElemListEvent.item = copyFromCombo;
				copyElemList.notifyListeners(SWT.Selection, copyElemListEvent);
			}
		}

	}

}
