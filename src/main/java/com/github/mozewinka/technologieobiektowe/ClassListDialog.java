package com.github.mozewinka.technologieobiektowe;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.components.JBList;

import javax.swing.*;

public class ClassListDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JBList<PsiClass> classes;
    private JBList<PsiField> fields;
    private JBList<PsiMethod> methods;

    public ClassListDialog(PsiClass[] data) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Lista Klas Projektu");
        setLocationRelativeTo(null);

        classes.setListData(data);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onOK());
        classes.addListSelectionListener(e -> {
            fields.setListData(classes.getSelectedValue().getAllFields());
            methods.setListData(classes.getSelectedValue().getAllMethods());
        });
    }

    private void onOK() {
        dispose();
    }

    private void createUIComponents() {
    }
}
