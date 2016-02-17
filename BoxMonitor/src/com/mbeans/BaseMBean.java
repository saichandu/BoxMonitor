package com.mbeans;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.webapp.FacetTag;

import org.apache.commons.lang.StringUtils;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.celleditor.CellEditor;
import org.primefaces.component.column.Column;

import com.exception.MessagesEnum;
import com.util.EmailValidator;

/**
 * @author saavvaru Created May 31st 2015
 */
public class BaseMBean implements Serializable{

	private static final long serialVersionUID = 2501203051160278144L;

	public void addErrorMessage(final String message) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}

	public void addInfoMessage(final String message) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
	}

	public void addWarnMessage(final String message) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
	}
	
	protected MethodExpression createMethodExpression(final String name,
			final Class<?> returnType, final Class<?> argumentType) {
		final FacesContext facesCtx = FacesContext.getCurrentInstance();
		final ELContext elContext = facesCtx.getELContext();
		final ExpressionFactory factory = facesCtx.getApplication()
				.getExpressionFactory();
		return factory.createMethodExpression(elContext, name, returnType,
				new Class[] {});
	}

	protected ValueExpression createValueExpression(final String valueExpression,
			final Class<?> valueType) {
		final FacesContext facesCtx = FacesContext.getCurrentInstance();
		final ELContext elContext = facesCtx.getELContext();
		final ExpressionFactory factory = facesCtx.getApplication()
				.getExpressionFactory();
		return factory.createValueExpression(elContext, valueExpression,
				valueType);
	}

	protected Column getColumn(final String headerTxt, final String columnVal) {
		final Column column = new Column();
		column.setHeaderText(headerTxt);
		column.getChildren().add(getOpText(columnVal));
		return column;
	}
	
	protected Column getColumn(final String headerTxt) {
		final Column column = new Column();
		if (headerTxt != null) {
			column.setHeaderText(headerTxt);
		}
		return column;
	}
	
	protected HtmlOutputText getOpText(final String value) {
		final HtmlOutputText optext = new HtmlOutputText();
		final ValueExpression labelValExpr = createValueExpression(value, String.class);
		optext.setValueExpression("value", labelValExpr);
		return optext;
	}
	
	protected HtmlOutputLabel getOpLbl(final String value, final String style) {
		final HtmlOutputLabel oplabel = new HtmlOutputLabel();
		oplabel.setValue(value);
		oplabel.setStyle(style);
		return oplabel;
	}
	
	protected HtmlInputText getIpText(final String value) {
		final HtmlInputText iptext = new HtmlInputText();
		final ValueExpression labelValExpr = createValueExpression(value, String.class);
		iptext.setValueExpression("value", labelValExpr);
		return iptext;
	}
	
	protected FacetTag getFacet(final String name, final Object value) {
		final FacetTag tag = new FacetTag();
		tag.setValue(name, value);
		return tag;
	}
	
	protected CellEditor getCellEditor(final String value) {
		final CellEditor cellEditor = new CellEditor();
		cellEditor.getFacets().put("output", getOpText(value));
		cellEditor.getFacets().put("input", getIpText(value));
		return cellEditor;
	}
	
	protected AjaxBehavior getAjaxBehavior(final String methodExp, final Class<?> eventType) {
		AjaxBehavior ajaxBehavior = new AjaxBehavior();
		MethodExpression ajaxMethod = createMethodExpression(methodExp, null, eventType);
		AjaxBehaviorListenerImpl ajaxBehaviorImpl = new AjaxBehaviorListenerImpl(ajaxMethod, ajaxMethod);        
		ajaxBehavior.addAjaxBehaviorListener(ajaxBehaviorImpl);
		ajaxBehavior.setUpdate("@form");
		return ajaxBehavior;
	}
	
	protected boolean validateEmail(final String email) {
		if (StringUtils.isNotBlank(email)) {
			EmailValidator validator = new EmailValidator();
			boolean valid = validator.validate(email);
			if (valid) {
				try {
					String domain = StringUtils.substring(email, email.lastIndexOf(".") + 1, email.length());
					String companyName = StringUtils.substring(email, email.lastIndexOf("@") + 1, email.lastIndexOf("."));
					if (!StringUtils.equalsIgnoreCase(companyName, "deloitte") || !StringUtils.equalsIgnoreCase(domain, "com")) {
						addErrorMessage(MessagesEnum.ENTER_DELOITTE_EMAIL.getMessage());
						return false;
					}				
				} catch (Exception e) {
					addErrorMessage(MessagesEnum.ENTER_VALID_EMAIL.getMessage());
					return false;
				}
			} else {
				addErrorMessage(MessagesEnum.ENTER_VALID_EMAIL.getMessage());
				return false;
			}
			return true;
		}
		return false;
	}
}
