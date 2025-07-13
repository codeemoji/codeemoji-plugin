package codeemoji.inlay.showingmodifiers;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.collector.base.CEReferenceClassCollector;
import codeemoji.core.collector.base.CEReferenceFieldCollector;
import codeemoji.core.collector.base.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.*;
import static com.intellij.psi.PsiModifier.*;

public class ShowingModifiers extends CEProviderMulti<ShowingModifiersSettings> {

    @Override
    public @NotNull List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        List<SharedBypassCollector> list = new ArrayList<>();
        String key = getKey();
        //class
        addClass(list, editor, PUBLIC, PUBLIC_CLASS, () -> getSettings().getPublic());
        addClass(list, editor, DEFAULT, DEFAULT_CLASS, () -> getSettings().getDefault());
        addClass(list, editor, FINAL, FINAL_CLASS, () -> getSettings().getFinal());
        addClass(list, editor, ABSTRACT, ABSTRACT_CLASS, () -> getSettings().getAbstract());

        //fields
        addField(list, editor, PUBLIC, PUBLIC_FIELD, () -> getSettings().getPublic());
        addField(list, editor, DEFAULT, DEFAULT_FIELD, () -> getSettings().getDefault());
        addField(list, editor, FINAL, FINAL_FIELD, () -> getSettings().getFinal());
        addField(list, editor, PROTECTED, PROTECTED_FIELD, () -> getSettings().getProtected());
        addField(list, editor, PRIVATE, PRIVATE_FIELD, () -> getSettings().getPrivate());
        addField(list, editor, STATIC, STATIC_FIELD, () -> getSettings().getStatic());
        addField(list, editor, VOLATILE, VOLATILE_FIELD, () -> getSettings().getVolatile());
        addField(list, editor, TRANSIENT, TRANSIENT_FIELD, () -> getSettings().getTransient());

        //methods
        addMethod(list, editor, PUBLIC, PUBLIC_METHOD, () -> getSettings().getPublic());
        addMethod(list, editor, DEFAULT, DEFAULT_METHOD, () -> getSettings().getDefault());
        addMethod(list, editor, FINAL, FINAL_METHOD, () -> getSettings().getFinal());
        addMethod(list, editor, PROTECTED, PROTECTED_METHOD, () -> getSettings().getProtected());
        addMethod(list, editor, PRIVATE, PRIVATE_METHOD, () -> getSettings().getPrivate());
        addMethod(list, editor, STATIC, STATIC_METHOD, () -> getSettings().getStatic());
        addMethod(list, editor, ABSTRACT, ABSTRACT_METHOD, () -> getSettings().getAbstract());
        addMethod(list, editor, SYNCHRONIZED, SYNCHRONIZED_METHOD, () -> getSettings().getSynchronized());
        addMethod(list, editor, NATIVE, NATIVE_METHOD, () -> getSettings().getNative());

        if (getSettings().query(DEFAULT_INTERFACE_METHOD)) {
            list.add(new CEModifierInterfaceMethodCollector(editor, key, () -> getSettings().getDefaultInterface(), DEFAULT));
        }

        return list;
    }

    public void addMethod(Collection<SharedBypassCollector> list, Editor editor,
                          String psiModifier, ScopeModifier modifier, Supplier<CESymbol> symbol) {
        if (getSettings().query(modifier)) {
            list.add(new CEModifierMethodCollector(editor, getKey(), symbol, psiModifier));
        }
    }

    public void addField(Collection<SharedBypassCollector> list, Editor editor,
                         String psiModifier, ScopeModifier modifier, Supplier<CESymbol> symbol) {
        if (getSettings().query(modifier)) {
            list.add(new CEModifierFieldCollector(editor, getKey(), symbol, psiModifier));
        }
    }

    public void addClass(Collection<SharedBypassCollector> list, Editor editor,
                         String psiModifier, ScopeModifier modifier, Supplier<CESymbol> symbol) {
        if (getSettings().query(modifier)) {
            list.add(new CEModifierClassCollector(editor, getKey(), symbol, psiModifier));
        }
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<ShowingModifiersSettings> createConfigurable() {
        return new ShowingModifiersConfigurable();
    }

    public enum ScopeModifier {
        PUBLIC_CLASS, DEFAULT_CLASS, FINAL_CLASS, ABSTRACT_CLASS,
        PUBLIC_FIELD, DEFAULT_FIELD, FINAL_FIELD, PROTECTED_FIELD, PRIVATE_FIELD, STATIC_FIELD, VOLATILE_FIELD, TRANSIENT_FIELD,
        PUBLIC_METHOD, DEFAULT_METHOD, FINAL_METHOD, PROTECTED_METHOD, PRIVATE_METHOD, STATIC_METHOD, ABSTRACT_METHOD, SYNCHRONIZED_METHOD, NATIVE_METHOD, DEFAULT_INTERFACE_METHOD
    }


    //TODO: improve with settings
    public final class CEModifierFieldCollector extends CEReferenceFieldCollector {

        private final String modifier;
        private final Supplier<CESymbol> symbol;

        public CEModifierFieldCollector(@NotNull Editor editor, String key, Supplier<CESymbol> settings,
                                        String modifier) {
            super(editor, key);
            this.modifier = modifier;
            this.symbol = settings;
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiField element) {
            if (needsInlay(element, modifier)) {
                String tooltip = getKey() + ".field." + modifier;
                return InlayVisuals.translated(symbol.get(), tooltip, null);
            }
            return null;
        }
    }


    private final class CEModifierClassCollector extends CEReferenceClassCollector {

        private final String modifier;
        private final Supplier<CESymbol> symbol;

        public CEModifierClassCollector(@NotNull Editor editor, String key, Supplier<CESymbol> settings,
                                        String modifier) {
            super(editor, key);
            this.modifier = modifier;
            this.symbol = settings;
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiClass element) {
            if (needsInlay(element, modifier)) {
                String tooltip = getKey() + ".class." + modifier;
                return InlayVisuals.translated(symbol.get(), tooltip, null);
            }
            return null;
        }
    }

    public static final class CEModifierInterfaceMethodCollector extends CEReferenceMethodCollector {

        private final String modifier;
        private final Supplier<CESymbol> symbol;

        public CEModifierInterfaceMethodCollector(@NotNull Editor editor, String key, Supplier<CESymbol> settings,
                                                  String modifier) {
            super(editor, key);
            this.modifier = modifier;
            this.symbol = settings;
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (needsInlay(element)) {
                String tooltip = getKey() + ".method." + modifier + "interface";
                return InlayVisuals.translated(symbol.get(), tooltip, null);
            }
            return null;
        }

        public boolean needsInlay(@NotNull PsiMethod element) {
            var parent = element.getParent();
            if (parent instanceof PsiClass clazz && (clazz.isInterface())) {
                var psiModifierList = element.getModifierList();
                return psiModifierList.hasModifierProperty(modifier);

            }
            return false;
        }

    }


    public final class CEModifierMethodCollector extends CEReferenceMethodCollector {

        private final String modifier;
        private final Supplier<CESymbol> symbol;

        public CEModifierMethodCollector(@NotNull Editor editor, String key, Supplier<CESymbol> symbol,
                                         String modifier) {
            super(editor, key);
            this.modifier = modifier;
            this.symbol = symbol;
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (needsInlay(element, modifier)) {
                String tooltip = getKey() + ".method." + modifier;
                return InlayVisuals.translated(symbol.get(), tooltip);
            }
            return null;
        }

    }


    private boolean needsInlay(@NotNull PsiModifierListOwner element, String modifier) {
        var psiModifierList = element.getModifierList();
        if (null != psiModifierList) {
            if (modifier.equalsIgnoreCase(DEFAULT)) {
                return CEUtils.checkDefaultModifier(psiModifierList);
            } else {
                return psiModifierList.hasModifierProperty(modifier);
            }
        }
        return false;
    }
}








