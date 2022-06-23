package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiModifier;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;

public class ClassListDialog extends JDialog {

    private final Settings settings = ApplicationManager.getApplication().getService(Settings.class);

    private JPanel contentPane;
    private JButton buttonGeneratePng;
    private JButton buttonClose;
    private JBList<String> classes;
    private JBList<String> fields;
    private JBList<String> methods;
    private JBList<String> interfaces;
    private JButton buttonGenerateSvg;
    private JLabel classesLabel;
    private JLabel fieldsLabel;
    private JLabel interfacesLabel;
    private JLabel methodsLabel;
    private JLabel depthLabel;
    private JLabel metricsLabel;

    private final ClassHelper classHelper;
    private final DiagramHelper diagramHelper;

    public ClassListDialog(Project project) {
        setContentPane(contentPane);
        setModal(false);
        getRootPane().setDefaultButton(buttonGenerateSvg);
        setTitle("Project class list");

        classHelper = new ClassHelper(project);
        diagramHelper = new DiagramHelper();
        metricsLabel.setText(new MetricsDialog(project).getMetricsLabel().getText());

        HashMap<String, PsiClass> classesMap = classHelper.classesMap;
        classes.setListData(classesMap.keySet().toArray(new String[0]));
        classesLabel.setText("Classes and interfaces: " + classes.getItemsCount());
        classes.addListSelectionListener(e -> {
            fields.setListData(ClassHelper.fieldsToString(classes.getSelectedValue(), classesMap));
            fieldsLabel.setText("Fields: " + fields.getItemsCount() + " (" +
                    countStatic(classesMap.get(classes.getSelectedValue()).getFields()) + " static)");

            methods.setListData(ClassHelper.methodsToString(classes.getSelectedValue(), classesMap));
            methodsLabel.setText("Methods: " + methods.getItemsCount() + " (" +
                    countStatic(classesMap.get(classes.getSelectedValue()).getMethods()) + " static)");

            interfaces.setListData(ClassHelper.interfacesToString(classes.getSelectedValue(), classesMap));
            int interfacesCount = interfaces.getItemsCount();
            interfacesLabel.setText("Interfaces: " + interfacesCount);
            if (interfacesCount < settings.getMediumInterfaceThreshold()) {
                interfacesLabel.setForeground(JBColor.GREEN);
            } else if (interfacesCount < settings.getHighInterfaceThreshold()) {
                interfacesLabel.setForeground(JBColor.ORANGE);
            } else {
                interfacesLabel.setForeground(JBColor.RED);
            }
            depthLabel.setText("Depth of Inheritance Tree: " + getDepth(classesMap.get(classes.getSelectedValue())));
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

    private int countStatic(PsiMember[] members) {
        int count = 0;
        for (PsiMember member : members) {
            if (member.hasModifierProperty(PsiModifier.STATIC)) {
                count++;
            }
        }
        return count;
    }

    private int getDepth(PsiClass cls) {
        int depth = 0;
        while (cls.getSuperClass() != null) {
            cls = cls.getSuperClass();
            depth++;
        }
        if (depth < settings.getMediumDepthThreshold()) {
            depthLabel.setForeground(JBColor.GREEN);
        } else if (depth < settings.getHighDepthThreshold()) {
            depthLabel.setForeground(JBColor.ORANGE);
        } else {
            depthLabel.setForeground(JBColor.RED);
        }
        return depth;
    }
}
