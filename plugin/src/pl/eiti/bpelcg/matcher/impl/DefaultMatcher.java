package pl.eiti.bpelcg.matcher.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpel.model.BPELFactory;
import org.eclipse.bpel.model.Copy;
import org.eclipse.bpel.model.From;
import org.eclipse.bpel.model.Invoke;
import org.eclipse.bpel.model.Query;
import org.eclipse.bpel.model.To;
import org.eclipse.bpel.model.Variable;
import org.eclipse.bpel.model.impl.FromImpl;
import org.eclipse.bpel.model.impl.ToImpl;
import org.eclipse.bpel.model.proxy.MessageProxy;
import org.eclipse.wst.wsdl.Message;

import pl.eiti.bpelcg.dao.WSDLDAO;
import pl.eiti.bpelcg.matcher.IMatcher;
import pl.eiti.bpelcg.resolver.WSDLResolver;
import pl.eiti.bpelcg.util.Settings;

/**
 * Default plugin matcher using simple rule for searching matches between
 * variables which checks equality of names and types.
 */
public class DefaultMatcher extends Settings implements IMatcher {
	WSDLDAO wsdlLoader = null;
	WSDLResolver wsdlResolver = null;

	/**
	 * Default constructor used for get instances of singleton WSDL DAO and
	 * resolver elements.
	 */
	public DefaultMatcher() {
		wsdlLoader = WSDLDAO.getInstance();
		wsdlResolver = WSDLResolver.getInstance();
	}

	@Override
	public List<Copy> createCopyForMatchedVariables(List<Variable> settedVariables, List<Invoke> followingInvokes) {
		List<Copy> result = new ArrayList<>();

		for (Invoke it : followingInvokes) {
			Variable invokeInput = it.getInputVariable();

			for (Variable var : settedVariables) {

				if (null != var.getMessageType() && null != invokeInput.getMessageType()) {
					// both types complex

					if (var.getMessageType().equals(invokeInput.getMessageType())) {
						// create and add copy instruction here
						From fromElement = BPELFactory.eINSTANCE.createFrom();
						To toElement = BPELFactory.eINSTANCE.createTo();
						fromElement.setVariable(var);
						toElement.setVariable(invokeInput);

						Copy copyElement = BPELFactory.eINSTANCE.createCopy();
						copyElement.setFrom(fromElement);
						copyElement.setTo(toElement);

						result.add(copyElement);
					} else {
						result.addAll(retrieveComplexTypeMatching(var, invokeInput));
					}
				} else if (null != var.getMessageType() && null == invokeInput.getMessageType()) {
					result.addAll(retrieveSimpleToComplexTypeMatching(invokeInput, var, Boolean.FALSE));
				} else if (null == var.getMessageType() && null != invokeInput.getMessageType()) {
					result.addAll(retrieveSimpleToComplexTypeMatching(var, invokeInput, Boolean.TRUE));
				} else {
					// both types simple
					if (isMatching(invokeInput, var)) {
						// create and add copy instruction here
						From fromElement = BPELFactory.eINSTANCE.createFrom();
						To toElement = BPELFactory.eINSTANCE.createTo();
						fromElement.setVariable(var);
						toElement.setVariable(invokeInput);

						Copy copyElement = BPELFactory.eINSTANCE.createCopy();
						copyElement.setFrom(fromElement);
						copyElement.setTo(toElement);

						result.add(copyElement);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Retrieves all matchings between simple types that variables given as
	 * parameters includes.
	 * 
	 * @param varToCopyFrom
	 *            complex type variable to copy from
	 * @param varToCopyTo
	 *            complex type variable to copy to
	 * @return list of copy elements
	 */
	private List<Copy> retrieveComplexTypeMatching(Variable varToCopyFrom, Variable varToCopyTo) {
		List<Copy> result = new ArrayList<>();

		Message varComplexType = wsdlLoader.getMessage(varToCopyFrom.getMessageType().getQName());
		Message invokeComplexType = wsdlLoader.getMessage(varToCopyTo.getMessageType().getQName());

		List<String> varMessageElements = wsdlResolver.retrieveElements(varComplexType);
		List<String> invokeMessageElements = wsdlResolver.retrieveElements(invokeComplexType);

		List<SimpleMessageElement> simpleElements = new ArrayList<>();

		for (String varMessageElem : varMessageElements) {
			String[] varMessageInfo = varMessageElem.split(M_DELIMITER);

			for (String invokeMessageElem : invokeMessageElements) {
				String[] invokeMessageInfo = invokeMessageElem.split(M_DELIMITER);

				if (invokeMessageInfo[ELEM_TYPE_INDEX].equals(varMessageInfo[ELEM_TYPE_INDEX])
						&& invokeMessageInfo[ELEM_NAME_INDEX].equals(varMessageInfo[ELEM_NAME_INDEX])) {
					SimpleMessageElement simpleElem = new SimpleMessageElement();
					simpleElem.fromSimpleMessage = varMessageElem;
					simpleElem.toSimpleMessage = invokeMessageElem;

					simpleElements.add(simpleElem);
				}
			}
		}

		for (SimpleMessageElement match : simpleElements) {

			if (null != match.fromSimpleMessage && null != match.toSimpleMessage) {
				String[] fromElementSplitted = match.fromSimpleMessage.split(M_DELIMITER);
				String[] toElementSplitted = match.toSimpleMessage.split(M_DELIMITER);

				Query fromQuery = BPELFactory.eINSTANCE.createQuery();
				fromQuery.setQueryLanguage(QUERY_LANGUAGE);
				fromQuery.setValue(((MessageProxy) varToCopyFrom.getMessageType()).getQName().getPrefix() + ":"
						+ fromElementSplitted[ELEM_NAME_INDEX]);

				Query toQuery = BPELFactory.eINSTANCE.createQuery();
				toQuery.setQueryLanguage(QUERY_LANGUAGE);
				toQuery.setValue(((MessageProxy) varToCopyTo.getMessageType()).getQName().getPrefix() + ":"
						+ toElementSplitted[ELEM_NAME_INDEX]);

				From fromElement = BPELFactory.eINSTANCE.createFrom();
				To toElement = BPELFactory.eINSTANCE.createTo();

				fromElement.setVariable(varToCopyFrom);
				if (fromElement instanceof FromImpl) {
					((FromImpl) fromElement).setPartName(fromElementSplitted[PART_NAME_INDEX]);
				}
				fromElement.setQuery(fromQuery);

				toElement.setVariable(varToCopyTo);
				if (toElement instanceof ToImpl) {
					((ToImpl) toElement).setPartName(toElementSplitted[PART_NAME_INDEX]);
				}
				toElement.setQuery(toQuery);

				Copy newCopy = BPELFactory.eINSTANCE.createCopy();
				newCopy.setFrom(fromElement);
				newCopy.setTo(toElement);
				result.add(newCopy);
			}
		}

		return result;
	}

	/**
	 * Retrieves all matchings between simple type variable given as parameter
	 * and complex type elements variable with additional switch that inform if
	 * it will be copy from simple to complex type or from complex to simple
	 * type.
	 * 
	 * @param simpleTypeVar
	 *            simple type variable
	 * @param complexTypeVar
	 *            complex type variable
	 * @param fromSimpleToComplex
	 *            switch that determines copy instruction direction
	 * @return list of copy elements
	 */
	private List<Copy> retrieveSimpleToComplexTypeMatching(Variable simpleTypeVar, Variable complexTypeVar,
			Boolean fromSimpleToComplex) {
		List<Copy> result = new ArrayList<>();

		Message complexType = wsdlLoader.getMessage(complexTypeVar.getMessageType().getQName());

		List<String> messageElements = wsdlResolver.retrieveElements(complexType);

		String simpleElement = null;

		for (String messageElem : messageElements) {
			String[] messageInfo = messageElem.split(M_DELIMITER);

			if (simpleTypeVar.getName().equals(messageInfo[ELEM_NAME_INDEX])
					&& simpleTypeVar.getType().getName().equals(messageInfo[ELEM_TYPE_INDEX])) {
				simpleElement = messageElem;
				break;
			}
		}

		if (null != simpleElement) {
			String[] elementSplitted = simpleElement.split(M_DELIMITER);

			Query query = BPELFactory.eINSTANCE.createQuery();
			query.setQueryLanguage(QUERY_LANGUAGE);
			query.setValue(((MessageProxy) complexTypeVar.getMessageType()).getQName().getPrefix() + ":"
					+ elementSplitted[ELEM_NAME_INDEX]);

			From fromElement = BPELFactory.eINSTANCE.createFrom();
			To toElement = BPELFactory.eINSTANCE.createTo();

			if (fromSimpleToComplex) {
				fromElement.setVariable(simpleTypeVar);
				toElement.setVariable(complexTypeVar);
				if (toElement instanceof ToImpl) {
					((ToImpl) toElement).setPartName(elementSplitted[PART_NAME_INDEX]);
				}
				toElement.setQuery(query);
			} else {
				toElement.setVariable(simpleTypeVar);
				fromElement.setVariable(complexTypeVar);
				if (fromElement instanceof FromImpl) {
					((FromImpl) fromElement).setPartName(elementSplitted[PART_NAME_INDEX]);
				}
				fromElement.setQuery(query);
			}

			Copy newCopy = BPELFactory.eINSTANCE.createCopy();
			newCopy.setFrom(fromElement);
			newCopy.setTo(toElement);
			result.add(newCopy);
		}

		return result;
	}

	/**
	 * Checks if simple type variables given as parameters are matching.
	 * 
	 * @param varTo
	 *            simple type variable to copy to
	 * @param varFrom
	 *            simple type variable to copy from
	 * @return boolean type information if parameters are matching
	 */
	private Boolean isMatching(Variable varTo, Variable varFrom) {
		Boolean result = Boolean.FALSE;

		if (varFrom.getName().equals(varTo.getName())
				&& ((null != varFrom.getMessageType() && varFrom.getMessageType().equals(varTo.getMessageType())) || (null != varFrom
						.getType() && varFrom.getType().equals(varTo.getType())))) {
			result = Boolean.TRUE;
		}
		return result;
	}

	/**
	 * Simple structure - keeps pairs of messages that match.
	 */
	private class SimpleMessageElement {
		public String fromSimpleMessage;
		public String toSimpleMessage;
	}

}
