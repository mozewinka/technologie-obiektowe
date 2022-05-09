package com.github.mozewinka.technologieobiektowe;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
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

    public ClassListDialog(PsiClass[] data) {
        setContentPane(contentPane);
        setModal(false);
        getRootPane().setDefaultButton(buttonGenerate);
        setTitle("Lista Klas Projektu");

        classesMap = classesToStringMap(data);
        classes.setListData(classesMap.keySet().toArray(new String[0]));

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
        List <GeneratedImage> list = reader.getGeneratedImages();
        File diagramPng = list.get(0).getPngFile();
        BufferedImage image = ImageIO.read(diagramPng);

        ImageIcon imageIcon = new ImageIcon(image);
        JLabel label = new JLabel();
        label.setIcon(imageIcon);

        JFrame frame = new JFrame("Diagram klas projektu");
        JBScrollPane scrollPane = new JBScrollPane(label);
        frame.add(scrollPane, BorderLayout.CENTER);
//        frame.getContentPane().add(label, BorderLayout.CENTER);
//        JPanel panel = new JPanel() {
//            @Override
//            public void paint(Graphics graphics) {
//                try {
//                    graphics.drawImage(ImageIO.read(diagramPng), 0, 0, this);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        };
//        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void onClose() {
        dispose();
    }

    private void saveDiagramToFile() throws IOException {
        FileWriter diagramWriter = new FileWriter("diagram.plantuml");
        diagramWriter.write("@startuml\n\n");

        for (var entry : classesMap.entrySet()) {
            diagramWriter.write("class " + entry.getKey() + " {\n");

            String[] methods = methodsToString(entry.getKey());
            for (var method : methods) {
                diagramWriter.write("  + " + method + "\n");
            }

            String[] interfaces = interfacesToString(entry.getKey());
            for (var inter : interfaces) {
                diagramWriter.write("  + " + inter + "\n");
            }

            String[] fields = fieldsToString(entry.getKey());
            for (var field : fields) {
                diagramWriter.write("  + " + field + "\n");
            }

            diagramWriter.write("}\n");
        }
        diagramWriter.write("@enduml\n");
        diagramWriter.close();
    }

    private HashMap<String, PsiClass> classesToStringMap(PsiClass[] classesArray) {
        HashMap<String, PsiClass> classMap = new HashMap<>();
        for (PsiClass c : classesArray) {
            classMap.put(c.getName() /* + " (" + PsiUtil.getPackageName(c) + ")" */, c);
        }
        return classMap;
    }

    private String[] fieldsToString(String selectedClass) {
        PsiField[] arr = classesMap.get(selectedClass).getFields();
        String[] fieldsArray = new String[arr.length];
        
        for (int i = 0; i < arr.length; i++) {
            fieldsArray[i] = arr[i].getName()
                    + " : "
                    + arr[i].getType().getPresentableText();
        }
        return fieldsArray;
    }

    private String[] methodsToString(String selectedClass) {
        PsiMethod[] arr = classesMap.get(selectedClass).getMethods();
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

    private String[] interfacesToString(String selectedClass) {
        PsiClass[] arr = classesMap.get(selectedClass).getInterfaces();
        String[] interfacesArray = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
            String[] qualifiedName = Objects.requireNonNull(arr[i].getQualifiedName()).split("\\.");
            interfacesArray[i] = qualifiedName[qualifiedName.length - 1]; // non-qualified name
        }
        return interfacesArray;
    }
}
