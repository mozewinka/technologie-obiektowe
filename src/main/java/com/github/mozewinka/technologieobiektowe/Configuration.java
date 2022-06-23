package com.github.mozewinka.technologieobiektowe;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Configuration extends BaseConfigurable implements SearchableConfigurable, PersistentStateComponent<Configuration> {

    JPanel myConfigPanel;
    private JSpinner myMediumDepth;
    private JSpinner myHighDepth;
    private JSpinner myMediumInterface;
    private JSpinner myHighInterface;

    private final Settings settings = ApplicationManager.getApplication().getService(Settings.class);

    @Override
    public @NotNull @NonNls String getId() {
        return "TechnologieObiektoweConfiguration";
    }

    @Override
    public String getDisplayName() {
        return "TechnologieObiektowe";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myConfigPanel;
    }

    @Override
    public void apply() {
        settings.setMediumDepthThreshold(Integer.parseInt(myMediumDepth.getValue().toString()));
        settings.setHighDepthThreshold(Integer.parseInt(myHighDepth.getValue().toString()));
        settings.setMediumInterfaceThreshold(Integer.parseInt(myMediumInterface.getValue().toString()));
        settings.setHighInterfaceThreshold(Integer.parseInt(myHighInterface.getValue().toString()));
    }

    public boolean isModified() {
        if (!myMediumDepth.getValue().equals(settings.getMediumDepthThreshold())) return true;
        if (!myHighDepth.getValue().equals(settings.getHighDepthThreshold())) return true;
        if (!myMediumInterface.getValue().equals(settings.getMediumInterfaceThreshold())) return true;
        return !myHighInterface.getValue().equals(settings.getHighInterfaceThreshold());
    }

    public void reset() {
        myMediumDepth.setValue(settings.getMediumDepthThreshold());
        myHighDepth.setValue(settings.getHighDepthThreshold());
        myMediumInterface.setValue(settings.getMediumInterfaceThreshold());
        myHighInterface.setValue(settings.getHighInterfaceThreshold());
    }

    @Override
    public @Nullable Configuration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Configuration state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
