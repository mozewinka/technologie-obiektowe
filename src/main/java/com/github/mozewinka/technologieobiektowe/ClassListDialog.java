package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;

public class ClassListDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonGeneratePng;
    private JButton buttonClose;
    private JBList<String> classes;
    private JBList<String> fields;
    private JBList<String> methods;
    private JBList<String> interfaces;
    private JButton buttonGenerateSvg;
    private final ClassHelper classHelper;
    private final DiagramHelper diagramHelper;

    public ClassListDialog(Project project) {
        setContentPane(contentPane);
        setModal(false);
        getRootPane().setDefaultButton(buttonGeneratePng);
        setTitle("Project class list");

        classHelper = new ClassHelper(project);
        diagramHelper = new DiagramHelper();

        HashMap<String, PsiClass> classesMap = classHelper.classesMap;
        classes.setListData(classesMap.keySet().toArray(new String[0]));
        classes.addListSelectionListener(e -> {
            fields.setListData(ClassHelper.fieldsToString(classes.getSelectedValue(), classesMap));
            methods.setListData(ClassHelper.methodsToString(classes.getSelectedValue(), classesMap));
            interfaces.setListData(ClassHelper.interfacesToString(classes.getSelectedValue(), classesMap));
        });

        buttonGeneratePng.addActionListener(e -> {
            try {
                onGeneratePng();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonGenerateSvg.addActionListener(e -> {
            try {
                onGenerateSvg();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        buttonClose.addActionListener(e -> onClose());
        contentPane.registerKeyboardAction(e -> onClose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onGeneratePng() throws IOException {
        diagramHelper.generatePng(classHelper.classesMap);
    }

    private void onGenerateSvg() throws IOException {
        diagramHelper.generateSvg(classHelper.classesMap);
    }

    private void onClose() {
        dispose();
    }

}
