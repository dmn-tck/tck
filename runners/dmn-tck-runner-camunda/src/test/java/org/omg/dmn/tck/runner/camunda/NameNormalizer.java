package org.omg.dmn.tck.runner.camunda;

import java.lang.reflect.Method;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.omg.spec.dmn._20151101.dmn.TBusinessKnowledgeModel;
import org.omg.spec.dmn._20151101.dmn.TDRGElement;
import org.omg.spec.dmn._20151101.dmn.TDecision;
import org.omg.spec.dmn._20151101.dmn.TDefinitions;
import org.omg.spec.dmn._20151101.dmn.TExpression;
import org.omg.spec.dmn._20151101.dmn.TFunctionDefinition;
import org.omg.spec.dmn._20151101.dmn.TInformationItem;
import org.omg.spec.dmn._20151101.dmn.TInputData;
import org.omg.spec.dmn._20151101.dmn.TInvocation;
import org.omg.spec.dmn._20151101.dmn.TItemDefinition;
import org.omg.spec.dmn._20151101.dmn.TLiteralExpression;
import org.omg.spec.dmn._20151101.dmn.TNamedElement;

import com.gs.dmn.feel.analysis.scanner.LexicalContext;
import com.gs.dmn.transformation.NameTransformer;
import com.gs.dmn.transformation.ToQuotedNameTransformer;

public class NameNormalizer extends ToQuotedNameTransformer {
  
  // TODO make NameTransformer#renameElement(TNamedElement) protected
  // and override it with an empty implementation to avoid all this
  @Override
  public TDefinitions transform(TDefinitions definitions) {
    definitions = super.transform(definitions);
    replace(definitions);
    rename(definitions);
    return definitions;
  }

  private void replace(TDefinitions definitions) {
    for (JAXBElement<? extends TDRGElement> jaxbElement : definitions.getDrgElement()) {
        TDRGElement element = jaxbElement.getValue();
        if (element instanceof TInputData) {
        } else if (element instanceof TBusinessKnowledgeModel) {
            // Replace old names with new names in body
            LexicalContext lexicalContext = makeLexicalContext();
            TFunctionDefinition encapsulatedLogic = ((TBusinessKnowledgeModel) element).getEncapsulatedLogic();
            replace(encapsulatedLogic.getExpression().getValue(), lexicalContext);
        } else if (element instanceof TDecision) {
            // Replace old names with new names in body
            LexicalContext lexicalContext = makeLexicalContext();
            replace(((TDecision) element).getExpression().getValue(), lexicalContext);
        } else {
        }
    }
  }

  private void replace(TExpression expression, LexicalContext lexicalContext) {
    try {
      Method method = NameTransformer.class.getDeclaredMethod("replace", TExpression.class, LexicalContext.class);
      method.setAccessible(true);
      method.invoke(this, expression, lexicalContext);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // TODO override NameTransformer#makeLexicalContext() to always the property names
  protected LexicalContext makeLexicalContext() {
    return new LexicalContext("grouping separator", "decimal separator", "start position", "time offset");
  }
  
  private void rename(TDefinitions definitions) {
    for(TItemDefinition itemDefinition: definitions.getItemDefinition()) {
        renameItemDefinitionMembers(itemDefinition);
    }
    for (JAXBElement<? extends TDRGElement> jaxbElement : definitions.getDrgElement()) {
        TDRGElement element = jaxbElement.getValue();
        if (element instanceof TInputData) {
            // Rename element and variable
            renameElement(element);
            renameElement(((TInputData) element).getVariable());
        } else if (element instanceof TBusinessKnowledgeModel) {
            // Rename element and variable
            renameElement(element);
            renameElement(((TBusinessKnowledgeModel) element).getVariable());

            // Rename in body
            TFunctionDefinition encapsulatedLogic = ((TBusinessKnowledgeModel) element).getEncapsulatedLogic();
            List<TInformationItem> formalParameterList = encapsulatedLogic.getFormalParameter();
            for(TInformationItem param: formalParameterList) {
                renameElement(param);
            }
            
        } else if (element instanceof TDecision) {
            // Rename element and variable
            renameElement(element);
            renameElement(((TDecision) element).getVariable());
            // Remove quotes from invocation for now
            // TODO evaluate as expression with quoted name support
            TExpression expression = ((TDecision) element).getExpression().getValue();
            if (expression instanceof TInvocation) {
              TLiteralExpression literalExpression = (TLiteralExpression)((TInvocation) expression).getExpression().getValue();
              literalExpression.setText(unquoteName(literalExpression.getText()));
            }
        } else {
        }
    }
  }

  private void renameItemDefinitionMembers(TItemDefinition itemDefinition) {
    List<TItemDefinition> itemComponent = itemDefinition.getItemComponent();
    if (itemComponent != null) {
        for(TItemDefinition member: itemComponent) {
            renameElement(member);
            renameItemDefinitionMembers(member);
        }
    }
  }
  
  private void renameElement(TNamedElement element) {
    String name = element.getName();
    name = unquoteName(name);
    element.setName(name);
  }

  private String unquoteName(String name) {
    if (name.startsWith("'") && name.endsWith("'")) {
      name = name.substring(1, name.length()-1);
    }
    return name;
  }
  
}
