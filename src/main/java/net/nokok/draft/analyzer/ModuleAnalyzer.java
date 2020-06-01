package net.nokok.draft.analyzer;

import net.nokok.draft.DraftModule;
import net.nokok.draft.ModuleConfigurationException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

public class ModuleAnalyzer extends AbstractProcessor {

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
                        e.accept(new MethodVisitor(), null);
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

    class MethodVisitor implements ElementVisitor<Object, Object> {

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
            if (parameters.size() >= 2) {
                throw new ModuleConfigurationException("Too many arguments");
            }
            if (parameters.isEmpty()) {

            } else {
                TypeMirror parameterTypeMirror = parameters.get(0).asType();
                Types typeUtils = processingEnv.getTypeUtils();
                if (!typeUtils.isSubtype(parameterTypeMirror, returnType)) {
                    throw new ModuleConfigurationException(String.format("Incompatible types: %s vs %s", returnType, parameterTypeMirror));
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
