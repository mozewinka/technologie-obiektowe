package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.PsiSearchScopeUtil;

import java.util.*;

import static com.github.mozewinka.technologieobiektowe.Modifiers.getModifierSymbol;

public class ClassHelper {

    public final HashMap<String, PsiClass> classesMap;
    public static final HashSet<String> relationships = new HashSet<>();
    private final ProjectOnlySearchScope searchScope;

    public ClassHelper(Project project) {
        searchScope = new ProjectOnlySearchScope(project);

        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile[] directories = projectRootManager.getContentSourceRoots();
        List<PsiClass> psiClasses = getClasses(directories, psiManager);
        PsiClass[] classes = psiClasses.toArray(new PsiClass[0]);

        classesMap = classesToStringMap(classes);
    }

    private List<PsiClass> getClasses(VirtualFile[] directories, PsiManager psiManager) {
        List<PsiClass> result = new ArrayList<>();
        for (VirtualFile d : directories) {
            PsiDirectory psiDirectory = psiManager.findDirectory(d);
            if (psiDirectory != null)
                addClasses(psiDirectory, result);
        }
        return result;
    }

    private void addClasses (PsiDirectory psiDirectory, List<PsiClass> classes) {
        PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
        if (psiPackage != null) {
            PsiClass[] packageClasses = psiPackage.getClasses();
            for (PsiClass psiClass : packageClasses) {
                if (PsiSearchScopeUtil.isInScope(searchScope, psiClass))
                    classes.add(psiClass);
            }
            PsiDirectory[] psiSubDirectories = psiDirectory.getSubdirectories();
            for (PsiDirectory sub : psiSubDirectories)
                addClasses(sub, classes);
        }
    }

    public HashMap<String, PsiClass> classesToStringMap(PsiClass[] classesArray) {
        HashMap<String, PsiClass> classMap = new HashMap<>();
        for (PsiClass c : classesArray) {
            classMap.put(c.getQualifiedName(), c);
        }
        return classMap;
    }

    static String[] fieldsToString(String selectedClass, HashMap<String, PsiClass> classesMap) {
        PsiField[] psiFieldsArray = classesMap.get(selectedClass).getFields();
        String[] fieldsArray = new String[psiFieldsArray.length];

        for (int i = 0; i < psiFieldsArray.length; i++) {
            PsiType type = psiFieldsArray[i].getType();
            String name = psiFieldsArray[i].getName();
            fieldsArray[i] = getModifierSymbol(psiFieldsArray[i]) + " " + name + " : " + type.getPresentableText();

            addRelationship(type.getCanonicalText(), selectedClass, classesMap);
        }
        return fieldsArray;
    }

    static String[] methodsToString(String selectedClass, HashMap<String, PsiClass> classesMap) {
        PsiMethod[] psiMethodsArray = classesMap.get(selectedClass).getMethods();
        String[] methodsArray = new String[psiMethodsArray.length];

        for (int i = 0; i < psiMethodsArray.length; i++) {
            String type = "";
            if (!Objects.isNull(psiMethodsArray[i].getReturnType())) { // if null then empty = constructor
                type = " : " + Objects.requireNonNull(psiMethodsArray[i].getReturnType()).getPresentableText(); // return type

                String canonicalType = Objects.requireNonNull(psiMethodsArray[i].getReturnType()).getCanonicalText();
                addRelationship(canonicalType, selectedClass, classesMap);
            }


            StringBuilder parameters = new StringBuilder();
            if (!psiMethodsArray[i].getParameterList().isEmpty()) {
                PsiParameterList psiParameterList = psiMethodsArray[i].getParameterList();
                for (int j = 0; j < psiParameterList.getParametersCount(); j++) {
                    parameters.append(Objects.requireNonNull(psiParameterList.getParameter(j)).getName())
                            .append(" : ")
                            .append(Objects.requireNonNull(psiParameterList.getParameter(j))
                                    .getType().getPresentableText())
                            .append(", ");
                }
                parameters = new StringBuilder(parameters.substring(0, parameters.length() - 2));
            }

            methodsArray[i] = getModifierSymbol(psiMethodsArray[i]) + " "
                    + psiMethodsArray[i].getName()
                    + "(" + parameters + ")"
                    + type;
        }
        return methodsArray;
    }

    static String[] interfacesToString(String selectedClass, HashMap<String, PsiClass> classesMap) {
        PsiClass[] psiClassesArray = classesMap.get(selectedClass).getInterfaces();
        String[] interfacesArray = new String[psiClassesArray.length];

        for (int i = 0; i < psiClassesArray.length; i++) {
            String[] qualifiedName = Objects.requireNonNull(psiClassesArray[i].getQualifiedName()).split("\\.");
            interfacesArray[i] = getModifierSymbol(psiClassesArray[i]) + " "
                    + qualifiedName[qualifiedName.length - 1]; // non-qualified name
        }
        return interfacesArray;
    }

    private static void addRelationship(String canonicalType, String selectedClass, HashMap<String, PsiClass> classesMap) {
        canonicalType = canonicalType.replaceAll("\\[]", "");
        if (classesMap.containsKey(canonicalType) && !Objects.equals(selectedClass, canonicalType)) {
            relationships.add(selectedClass + " o-- " + canonicalType + "\n");
        }
    }
}
