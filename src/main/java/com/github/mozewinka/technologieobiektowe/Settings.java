package com.github.mozewinka.technologieobiektowe;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "Settings",
    storages = @Storage(value = "Settings.xml")
)
public class Settings implements PersistentStateComponent<Settings> {

    public int mediumDepthThreshold = 5;

    public int highDepthThreshold = 9;

    public int mediumInterfaceThreshold = 6;

    public int highInterfaceThreshold = 12;

    int getMediumDepthThreshold() {
        return mediumDepthThreshold;
    }

    void setMediumDepthThreshold(int mediumDepthThreshold) {
        this.mediumDepthThreshold = mediumDepthThreshold;
    }

    int getHighDepthThreshold() {
        return highDepthThreshold;
    }

    void setHighDepthThreshold(int highDepthThreshold) {
        this.highDepthThreshold = highDepthThreshold;
    }

    int getMediumInterfaceThreshold() {
        return mediumInterfaceThreshold;
    }

    void setMediumInterfaceThreshold(int mediumInterfaceThreshold) {
        this.mediumInterfaceThreshold = mediumInterfaceThreshold;
    }

    int getHighInterfaceThreshold() {
        return highInterfaceThreshold;
    }

    void setHighInterfaceThreshold(int highInterfaceThreshold) {
        this.highInterfaceThreshold = highInterfaceThreshold;
    }

    @Override
    public @Nullable Settings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
