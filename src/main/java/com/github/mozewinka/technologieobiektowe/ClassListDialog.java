package com.github.mozewinka.technologieobiektowe;

import com.intellij.psi.*;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ClassListDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonGenerate;
    private JButton buttonClose;
    private JBList<String> classes;
    private JBList<String> fields;
    private JBList<String> methods;
    private JBList<String> interfaces;
    private final HashMap<String, PsiClass> classesMap;
    private final HashSet<String> relationships;

    public ClassListDialog(PsiClass[] data) {
        setContentPane(contentPane);
        setModal(false);
        getRootPane().setDefaultButton(buttonGenerate);
        setTitle("Lista Klas Projektu");

        classesMap = classesToStringMap(data);
        classes.setListData(classesMap.keySet().toArray(new String[0]));
        relationships = new HashSet<>();

        buttonGenerate.addActionListener(e -> {
            try {
                onGenerate();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonClose.addActionListener(e -> onClose());
        classes.addListSelectionListener(e -> {
            fields.setListData(fieldsToString(classes.getSelectedValue()));
            methods.setListData(methodsToString(classes.getSelectedValue()));
            interfaces.setListData(interfacesToString(classes.getSelectedValue()));
        });
    }

    private void onGenerate() throws IOException {
        saveDiagramToFile();
        SourceFileReader reader = new SourceFileReader(new File("diagram.plantuml"));
        List<GeneratedImage> list = reader.getGeneratedImages();
        File diagramPng = list.get(0).getPngFile();
        BufferedImage image = ImageIO.read(diagramPng);

        ImageIcon imageIcon = new ImageIcon(image);
        JLabel label = new JLabel();
        label.setIcon(imageIcon);

        JFrame frame = new JFrame("Diagram klas projektu");
        JBScrollPane scrollPane = new JBScrollPane(label);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    private void onClose() {
        dispose();
    }

    private void saveDiagramToFile() throws IOException {
        FileWriter diagramWriter = new FileWriter("diagram.plantuml");
        File temp = new File("diagram.plantuml");
        System.out.println(temp.getAbsolutePath());

        diagramWriter.write("@startuml\n\n");

        for (var entry : classesMap.entrySet()) {
            if (entry.getValue().isEnum()) {
                diagramWriter.write("enum " + entry.getKey() + " {\n");
            } else if (entry.getValue().isInterface()) {
                diagramWriter.write("interface " + entry.getKey() + " {\n");
            } else if (entry.getValue().hasModifierProperty(PsiModifier.ABSTRACT)) {
                diagramWriter.write("abstract class " + entry.getKey() + " {\n");
            } else {
                diagramWriter.write("class " + entry.getKey() + " {\n");
            }

            String[] methods = methodsToString(entry.getKey());
            for (var method : methods) {
                diagramWriter.write("  " + method + "\n");
            }

            String[] interfaces = interfacesToString(entry.getKey());
            for (var inter : interfaces) {
                diagramWriter.write("  " + inter + "\n");
            }

            String[] fields = fieldsToString(entry.getKey());
            for (var field : fields) {
                diagramWriter.write("  " + field + "\n");
            }

            diagramWriter.write("}\n");
        }

        for (var relation : relationships) {
            diagramWriter.write("  " + relation);
        }

        diagramWriter.write("@enduml\n");
        diagramWriter.close();
    }

    private HashMap<String, PsiClass> classesToStringMap(PsiClass[] classesArray) {
        HashMap<String, PsiClass> classMap = new HashMap<>();
        for (PsiClass c : classesArray) {
            classMap.put(c.getQualifiedName(), c);
        }
        return classMap;
    }

    private String[] fieldsToString(String selectedClass) {
        PsiField[] psiFieldsArray = classesMap.get(selectedClass).getFields();
        String[] fieldsArray = new String[psiFieldsArray.length];

        for (int i = 0; i < psiFieldsArray.length; i++) {
            PsiType type = psiFieldsArray[i].getType();
            String name = psiFieldsArray[i].getName();
            fieldsArray[i] = getModifierSymbol(psiFieldsArray[i]) + " " + name + " : " + type.getPresentableText();

            addRelationship(type.getCanonicalText(), selectedClass);
        }
        return fieldsArray;
    }

    private String[] methodsToString(String selectedClass) {
        PsiMethod[] psiMethodsArray = classesMap.get(selectedClass).getMethods();
        String[] methodsArray = new String[psiMethodsArray.length];

        for (int i = 0; i < psiMethodsArray.length; i++) {
            String type = "";
            if (!Objects.isNull(psiMethodsArray[i].getReturnType())) { // if null then empty = constructor
                type = " : " + Objects.requireNonNull(psiMethodsArray[i].getReturnType()).getPresentableText(); // return type

                String canonicalType = Objects.requireNonNull(psiMethodsArray[i].getReturnType()).getCanonicalText();
                addRelationship(canonicalType, selectedClass);
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

    private String[] interfacesToString(String selectedClass) {
        PsiClass[] psiClassesArray = classesMap.get(selectedClass).getInterfaces();
        String[] interfacesArray = new String[psiClassesArray.length];

        for (int i = 0; i < psiClassesArray.length; i++) {
            String[] qualifiedName = Objects.requireNonNull(psiClassesArray[i].getQualifiedName()).split("\\.");
            interfacesArray[i] = getModifierSymbol(psiClassesArray[i]) + " "
                    + qualifiedName[qualifiedName.length - 1]; // non-qualified name
        }
        return interfacesArray;
    }

    private String getModifierSymbol(PsiModifierListOwner element) {
        String symbol = "";
        if (element.hasModifierProperty(PsiModifier.PRIVATE)) {
            symbol = "-";
        } else if (element.hasModifierProperty(PsiModifier.PROTECTED)) {
            symbol = "#";
        } else if (element.hasModifierProperty(PsiModifier.PACKAGE_LOCAL)) {
            symbol = "~";
        } else if (element.hasModifierProperty(PsiModifier.PUBLIC)) {
            symbol = "+";
        }
        return symbol;
    }

    private void addRelationship(String canonicalType, String selectedClass) {
        canonicalType = canonicalType.replaceAll("\\[]", "");
        if (classesMap.containsKey(canonicalType) && !Objects.equals(selectedClass, canonicalType)) {
            relationships.add(selectedClass + " o-- " + canonicalType + "\n");
        }
    }
}
