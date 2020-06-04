package net.nokok.draft.analyzer;

import net.nokok.draft.DraftModule;
import net.nokok.draft.ModuleConfigurationException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

public class ModuleAnalyzer extends AbstractProcessor {

    private final BindingValidator validator = new BindingValidator();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        Messager messager = processingEnv.getMessager();
        boolean successful = true;
        for (TypeElement typeElement : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                List<? extends Element> enclosedElements = element.getEnclosedElements();
                if (enclosedElements.isEmpty()) {
                    messager.printMessage(Diagnostic.Kind.WARNING, "No bindings found", element);
                }
                for (Element e : enclosedElements) {
                    ElementKind kind = e.getKind();
                    if (!kind.equals(ElementKind.METHOD)) {
                        continue;
                    }
                    try {
                        e.accept(validator, null);
                    } catch (ModuleConfigurationException ex) {
                        messager.printMessage(Diagnostic.Kind.ERROR, ex.getMessage(), e);
                        successful = false;
                    }
                }
            }
        }
        return successful;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(DraftModule.class.getName());
    }

    class BindingValidator implements ElementVisitor<Object, Object> {

        @Override
        public Object visit(Element e, Object o) {
            return null;
        }

        @Override
        public Object visitPackage(PackageElement e, Object o) {
            return null;
        }

        @Override
        public Object visitType(TypeElement e, Object o) {
            return null;
        }

        @Override
        public Object visitVariable(VariableElement e, Object o) {
            return null;
        }

        @Override
        public Object visitExecutable(ExecutableElement e, Object o) {
            TypeMirror returnType = e.getReturnType();
            List<? extends VariableElement> parameters = e.getParameters();
            if (returnType.getKind().equals(TypeKind.VOID)) {
                throw new ModuleConfigurationException("Void type binding are not supported");
            }
            if (parameters.size() >= 2) {
                throw new ModuleConfigurationException("Too many arguments: " + e);
            }
            if (parameters.isEmpty()) {
                return null;
            } else {
                VariableElement parameter = parameters.get(0);
                TypeMirror parameterTypeMirror = parameter.asType();
                Types typeUtils = processingEnv.getTypeUtils();

                if (!typeUtils.isSubtype(returnType, parameterTypeMirror)) {
                    throw new ModuleConfigurationException(String.format("Incompatible types: %s vs %s", returnType, parameterTypeMirror));
                }
                if (e.getAnnotationMirrors().size() > 0 && parameter.getAnnotationMirrors().size() > 0) {
                    throw new ModuleConfigurationException(String.format("Duplicate qualifiers: %s", e.getAnnotationMirrors()));
                }
            }
            return null;
        }

        @Override
        public Object visitTypeParameter(TypeParameterElement e, Object o) {
            return null;
        }

        @Override
        public Object visitUnknown(Element e, Object o) {
            return null;
        }
    }
}
