package pl.eiti.bpelag.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpel.model.Assign;
import org.eclipse.bpel.model.Process;
import org.eclipse.bpel.model.Receive;
import org.eclipse.bpel.model.Variable;
import org.eclipse.bpel.model.resource.BPELResource;
import org.eclipse.bpel.model.resource.BPELWriter;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;

/**
 * Reader class using to load BPEL process file to object model based on
 * org.eclipse.bpel.model package classes.
 */
public class BPELDAO {

	private Process process = null;
	private String BPELFileLocation = null;
	private BPELResource resource = null;

	private Factory factory = null;

	/**
	 * Default constructor.
	 */
	public BPELDAO() {
	}

	/**
	 * File location set constructor.
	 * 
	 * @param newBPELFileLocation
	 *            file location
	 */
	public BPELDAO(String newBPELFileLocation) {
		this();
		this.BPELFileLocation = newBPELFileLocation;
	}

	/**
	 * BPEL process load method.
	 */
	public void loadProcess() {
		URI uri = URI.createFileURI(this.BPELFileLocation);
		factory = Resource.Factory.Registry.INSTANCE.getFactory(uri);
		this.resource = (BPELResource) factory.createResource(uri);
		try {
			this.resource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.process = (Process) resource.getContents().get(0);
	}

	public Factory getFactory() {
		return factory;
	}

	/**
	 * BPEL process save method.
	 * 
	 * @return saved process name
	 */
	public String saveProcess() {
		try {
			Map<Object, Object> saveOptions = new HashMap<>();
			saveOptions.put(BPELWriter.SKIP_AUTO_IMPORT, Boolean.FALSE);
			this.resource.save(saveOptions);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return this.process.getName();
	}

	public List<Assign> getAllAssignBlocks() {
		List<Assign> assigns = new ArrayList<>();
		TreeIterator<EObject> it = process.eAllContents();
		while (it.hasNext()) {
			EObject next = it.next();
			if (next instanceof Assign) {
				assigns.add((Assign) next);
			}
		}

		return assigns;
	}

	public List<Variable> getAllVariables() {
		List<Variable> variables = new ArrayList<>();
		for (EObject var : process.getVariables().eContents()) {
			if (var instanceof Variable) {
				variables.add((Variable) var);
			}
		}
		return variables;
	}

	public List<Receive> getAllReceives() {
		List<Receive> result = new ArrayList<>();

		TreeIterator<EObject> it = process.eAllContents();
		while (it.hasNext()) {
			EObject next = it.next();
			if (next instanceof Receive) {
				result.add((Receive) next);
			}
		}

		return result;
	}

	/** Accessors section */
	public Process getBPELProcess() {
		return process;
	}

	public void setBPELFileLocation(String newBPELFileLocation) {
		this.BPELFileLocation = newBPELFileLocation;
	}

}
