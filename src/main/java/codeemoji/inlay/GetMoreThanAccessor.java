package codeemoji.inlay;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;

import java.util.Objects;

import static codeemoji.core.CESymbol.CONFUSED;

public class GetMoreThanAccessor extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public List<Item> getItems() {
                        if (items == null) {
                            List<Item> list = new ArrayList<>();
                            Sale sale = getSale();
                            for (Item i : sale.items()) {
                                if (i.confirmed()){
                                    list.add(i);
                                }
                            }
                            list = Collections.unmodifiableList(list);
                        }
                        return items;
                    }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKey().getId(), CONFUSED) {
            @Override
            public boolean checkAddInlay(PsiMethod method) {
                if (method != null && method.getName().startsWith("get") &&
                        !(Objects.equals(method.getReturnType(), PsiTypes.voidType())) &&
                        method.getBody() != null) {
                    return method.getBody().getStatements().length > 1 && !method.getName().equalsIgnoreCase("getInstance");
                }
                return false;
            }
        };
    }
}








