package com.github.mozewinka.technologieobiektowe;

import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierListOwner;

public class Modifiers {

    public static String getModifierSymbol(PsiModifierListOwner element) {
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
}
