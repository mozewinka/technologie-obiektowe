package com.github.mozewinka.technologieobiektowe;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.util.Objects;

public class ClassListDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JBList<PsiClass> classes;
    private JBList<String> fields;
    private JBList<String> methods;
    private JBList<String> interfaces;

    public ClassListDialog(PsiClass[] data) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Lista Klas Projektu");

        classes.setListData(data);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onOK());
        classes.addListSelectionListener(e -> {
            fields.setListData(fieldsToString());
            methods.setListData(methodsToString());
            interfaces.setListData(interfacesToString());
        });
    }

    private void onOK() {
        dispose();
    }

    private String[] fieldsToString() {
        PsiField[] arr = classes.getSelectedValue().getFields();
        String[] fieldsArray = new String[arr.length];
        
        for (int i = 0; i < arr.length; i++) {
            fieldsArray[i] = arr[i].getName()
                    + " : "
                    + arr[i].getType().getPresentableText();
        }

        return fieldsArray;
    }

    private String[] methodsToString() {
        PsiMethod[] arr = classes.getSelectedValue().getMethods();
        String[] methodsArray = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
            String type = "";
            if (!Objects.isNull(arr[i].getReturnType())) // if null then empty = constructor
                type = " : " + Objects.requireNonNull(arr[i].getReturnType()).getPresentableText(); // return type

            StringBuilder parameters = new StringBuilder();
            if (!arr[i].getParameterList().isEmpty()) {
                PsiParameterList psiParameterList = arr[i].getParameterList();
                for (int j = 0; j < psiParameterList.getParametersCount(); j++) {
                    parameters.append(Objects.requireNonNull(psiParameterList
                                    .getParameter(j))
                                    .getName())
                            .append(" : ")
                            .append(Objects.requireNonNull(psiParameterList
                                    .getParameter(j))
                                    .getType()
                                    .getPresentableText())
                            .append(", ");
                }
                parameters = new StringBuilder(parameters.substring(0, parameters.length() - 2));
            }

            methodsArray[i] = arr[i].getName()
                    + "(" + parameters + ")"
                    + type;
        }

        return methodsArray;
    }

    private String[] interfacesToString() {
        PsiClass[] arr = classes.getSelectedValue().getInterfaces();
        String[] interfacesArray = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
            String[] qualifiedName = Objects.requireNonNull(arr[i].getQualifiedName()).split("\\.");
            interfacesArray[i] = qualifiedName[qualifiedName.length - 1]; // non-qualified name
        }

        return interfacesArray;
    }
}
