package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MetricsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonClose;
    private JLabel metricsLabel;

    private int linesCount;
    private int classesCount;
    private int packagesCount;
    private int interfacesCount;
    private int methodsCount;
    private int staticMethodsCount;
    private int fieldsCount;
    private int staticFieldsCount;

    public MetricsDialog(Project project) {
        setContentPane(contentPane);
        setModal(false);
        getRootPane().setDefaultButton(buttonClose);
        setTitle("Metrics");

        ClassHelper classHelper = new ClassHelper(project);
        multiCounter(classHelper);

        metricsLabel.setText("<html>\n" +
                             "Total lines of code: " + linesCount + "<br>\n" +
                             "Number of classes: " + classesCount + "<br>\n" +
                             "Number of packages: " + packagesCount + "<br>\n" +
                             "Number of interfaces: " + interfacesCount + "<br>\n" +
                             "Number of methods: " + methodsCount + "<br>\n" +
                             "Number of static methods: " + staticMethodsCount + "<br>\n" +
                             "Number of fields: " + fieldsCount + "<br>\n" +
                             "Number of static fields: " + staticFieldsCount + "<br>\n" +
                             "</html>");

        buttonClose.addActionListener(e -> onClose());
        contentPane.registerKeyboardAction(e -> onClose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onClose() {
        dispose();
    }

    private void multiCounter(ClassHelper classHelper) {
        classesCount = classHelper.classes.size();
        packagesCount = classHelper.packages.size();

        for (PsiClass cls : classHelper.classes) {
            linesCount += countLines(cls);

            if (cls.isInterface()) {
                interfacesCount++;
            }

            methodsCount += cls.getMethods().length;
            staticMethodsCount += countStatic(cls.getMethods());

            fieldsCount += cls.getFields().length;
            staticFieldsCount += countStatic(cls.getFields());
        }
    }

    private int countLines(PsiElement psiElement) {
        String code = psiElement.getText();
        int lines = 0;

        char[] chars = code.toCharArray();
        for (char c : chars) {
            if (c == '\n' || c == '\r') {
                lines++;
            }
        }

        return lines;
    }

    private int countStatic(PsiMember[] members) {
        int count = 0;
        for (PsiMember member : members) {
            if (member.hasModifierProperty(PsiModifier.STATIC)) {
                count++;
            }
        }
        return count;
    }
}
