package codeemoji.core.settings;

import codeemoji.core.config.CEPSIType;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Data
public abstract class CEBaseSettings<S extends CEBaseSettings<S>> implements PersistentStateComponent<S> {

    @Transient
    private transient final CEPSIType allowedPsiType;

    protected List<CESymbolHolder> symbols = new ArrayList<>();
    protected CEPSIType targetType;
    protected boolean includeReferences = true;

    public CEBaseSettings(
            CEPSIType allowedPsiType,
            CESymbolHolder... symbols) {
        super();
        this.symbols.addAll(Arrays.asList(symbols));
        this.allowedPsiType = allowedPsiType;
        this.targetType = allowedPsiType;
    }

    //helper that auto makes the string for a single symbol one
    public CEBaseSettings(CEPSIType allowedPsiType,
                          Class<?> providerClass, CESymbol symbol) {
        this(allowedPsiType, new CESymbolHolder(symbol, providerClass.getSimpleName().toLowerCase(Locale.ROOT)));
    }

    public CEBaseSettings(CESymbolHolder... symbols) {
        this(CEPSIType.UNSPECIFIED, symbols);
    }

    // bad
    @Transient
    public CESymbol getMainSymbol() {
        return symbols.get(0).getSymbol();
    }

    @Override
    public final S getState() {
        return (S) this;
    }

    @Override
    public final void loadState(@NotNull S state) {
        XmlSerializerUtil.copyBean(state, this);
        //so dumb as copy beam doesnt calls list setters...
        onUpdated();
    }

    public void onUpdated() {
        //override to do something when the state is loaded
    }

    public void setAllSymbols(List<CESymbolHolder> allSymbols) {
        Map<String, CESymbolHolder> allSymbolsCopy = new HashMap<>();
        allSymbols.forEach(s -> allSymbolsCopy.put(s.getId(), s));
        for (Field field : getClass().getDeclaredFields()) {
            //check if not transient
            if (field.getType().equals(CESymbol.class) && !isTransient(field)) {
                try {
                    CESymbolHolder symbolHolder = allSymbolsCopy.get(field.getName());
                    if (symbolHolder != null) {
                        field.setAccessible(true);
                        allSymbolsCopy.remove(field.getName());
                        field.set(this, symbolHolder.getSymbol());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        symbols.clear();
        symbols.addAll(allSymbolsCopy.values());
        onUpdated();
    }

    public List<CESymbolHolder> gatherAllSymbols() {
        List<CESymbolHolder> allSymbols = new ArrayList<>(symbols);
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getType().equals(CESymbol.class) && !isTransient(field)) {
                try {
                    field.setAccessible(true);
                    allSymbols.add(new CESymbolHolder((CESymbol) field.get(this), field.getName()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return allSymbols;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isTransient(Field field) {
        return Modifier.isTransient(field.getModifiers()) || field.isAnnotationPresent(Transient.class);
    }
}

