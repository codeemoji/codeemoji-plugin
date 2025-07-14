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
    @Transient
    private transient final boolean canHaveReferences;

    protected List<CESymbolHolder> symbols = new ArrayList<>();
    protected CEPSIType targetType;
    protected boolean includeReferences;

    public CEBaseSettings(Builder builder, CESymbolHolder... symbols) {
        super();
        this.symbols.addAll(Arrays.asList(symbols));
        this.allowedPsiType = builder.getAllowedTargets();
        this.targetType = builder.getDefaultTargets();
        this.canHaveReferences = builder.canHaveReferences;
        this.includeReferences = builder.referencesDefault;
    }

    //helper that auto makes the string for a single symbol one
    public CEBaseSettings(Builder builder,
                          Class<?> providerClass, CESymbol symbol) {
        this(builder, new CESymbolHolder(symbol, providerClass.getSimpleName().toLowerCase(Locale.ROOT)));
    }

    public CEBaseSettings(CESymbolHolder... symbols) {
        this(builder(), symbols);
    }

    // bad
    @Transient
    public CESymbol getMainSymbol() {
        return symbols.get(0).getSymbol();
    }


    public boolean appliesToMethods() {
        return targetType.isMethods();
    }

    public boolean appliesToClasses() {
        return targetType.isClasses();
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        boolean canHaveReferences = false;
        boolean referencesDefault = false;
        boolean canTargetMethods = false;
        boolean targetMethodDefault = false;
        boolean canTargetClases = false;
        boolean targetsClassesDefault = false;

        public Builder targetReferences(boolean defaultOn) {
            this.canHaveReferences = true;
            this.referencesDefault = defaultOn;
            return this;
        }

        public Builder targetReferences() {
            return targetReferences(true);
        }

        public Builder targetMethods(boolean defaultOn) {
            this.canTargetMethods = true;
            this.targetMethodDefault = defaultOn;
            return this;
        }

        public Builder targetMethods() {
            return targetMethods(true);
        }

        public Builder targetClasses(boolean defaultOn) {
            this.canTargetClases = true;
            this.targetsClassesDefault = defaultOn;
            return this;
        }

        public Builder targetClasses() {
            return targetClasses(true);
        }

        CEPSIType getDefaultTargets() {
            if (targetsClassesDefault && targetMethodDefault) return CEPSIType.METHODS_AND_CLASSES;
            else if (targetMethodDefault) return CEPSIType.METHODS;
            else if (targetsClassesDefault) return CEPSIType.CLASSES;
            else return CEPSIType.UNSPECIFIED;
        }

        CEPSIType getAllowedTargets() {
            if (canTargetClases && canTargetMethods) return CEPSIType.METHODS_AND_CLASSES;
            else if (canTargetMethods) return CEPSIType.METHODS;
            else if (canTargetClases) return CEPSIType.CLASSES;
            else return CEPSIType.UNSPECIFIED;
        }

    }
}

