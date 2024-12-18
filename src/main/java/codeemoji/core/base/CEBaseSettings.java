package codeemoji.core.base;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Transient;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public abstract class CEBaseSettings<S extends CEBaseSettings<S>> implements PersistentStateComponent<S> {

    @Tag("symbols")
    private final List<CESymbolHolder> symbols = new ArrayList<>();

    public CEBaseSettings(CESymbolHolder... symbols) {
        this.symbols.addAll(Arrays.stream(symbols).toList());
    }

    // bad
    @Transient
    @Deprecated
    public CESymbol getMainSymbol() {
        return symbols.get(0).getSymbol();
    }

    @Override
    public S getState() {
        return (S) this;
    }

    @Override
    public void loadState(@NotNull S state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void setSymbols(ArrayList<CESymbolHolder> copy) {
        symbols.clear();
        symbols.addAll(copy);
    }
}

