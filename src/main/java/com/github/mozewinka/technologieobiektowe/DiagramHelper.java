package com.github.mozewinka.technologieobiektowe;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiModifier;
import net.sourceforge.plantuml.*;
import net.sourceforge.plantuml.core.DiagramDescription;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class DiagramHelper {

    void generatePng(HashMap<String, PsiClass> classesMap) throws IOException {
        saveDiagramStringToFile(classesMap);
        SourceFileReader reader = new SourceFileReader(new File("diagram.plantuml"));
        List<GeneratedImage> list = reader.getGeneratedImages();
        File diagramPng = list.get(0).getPngFile();

        Desktop desktop = Desktop.getDesktop();
        desktop.open(diagramPng);
    }

    void generateSvg(HashMap<String, PsiClass> classesMap) throws IOException {
        SourceStringReader stringReader = new SourceStringReader(getDiagramString(classesMap));
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DiagramDescription description = stringReader.outputImage(outputStream, new FileFormatOption(FileFormat.SVG));
        description.getDescription();
        outputStream.close();
        final String diagramSvg = outputStream.toString(StandardCharsets.UTF_8);

        FileWriter diagramWriter = new FileWriter("diagram.svg");
        diagramWriter.write(diagramSvg);
        diagramWriter.close();

        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File("diagram.svg"));
    }

    String getDiagramString(HashMap<String, PsiClass> classesMap) {
        StringBuilder diagram = new StringBuilder("@startuml\n\n");

        for (var entry : classesMap.entrySet()) {
            if (entry.getValue().isEnum()) {
                diagram.append("enum ").append(entry.getKey()).append(" {\n");
            } else if (entry.getValue().isInterface()) {
                diagram.append("interface ").append(entry.getKey()).append(" {\n");
            } else if (entry.getValue().hasModifierProperty(PsiModifier.ABSTRACT)) {
                diagram.append("abstract class ").append(entry.getKey()).append(" {\n");
            } else {
                diagram.append("class ").append(entry.getKey()).append(" {\n");
            }

            String[] methods = ClassHelper.methodsToString(entry.getKey(), classesMap);
            for (var method : methods) {
                diagram.append("  ").append(method).append("\n");
            }

            String[] interfaces = ClassHelper.interfacesToString(entry.getKey(), classesMap);
            for (var inter : interfaces) {
                diagram.append("  ").append(inter).append("\n");
            }

            String[] fields = ClassHelper.fieldsToString(entry.getKey(), classesMap);
            for (var field : fields) {
                diagram.append("  ").append(field).append("\n");
            }

            diagram.append("}\n");
        }

        for (var relation : ClassHelper.relationships) {
            diagram.append("  ").append(relation);
        }

        diagram.append("@enduml\n");

        return diagram.toString();
    }

    void saveDiagramStringToFile(HashMap<String, PsiClass> classesMap) throws IOException {
        FileWriter diagramWriter = new FileWriter("diagram.plantuml");
//        File temp = new File("diagram.plantuml");
//        System.out.println(temp.getAbsolutePath());

        diagramWriter.write(getDiagramString(classesMap));
        diagramWriter.close();
    }
}
