package codeemoji.inlay;

import codeemoji.core.CEMethodCallCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.CEConstants.SEMAPHORE;
import static com.intellij.psi.PsiModifier.SYNCHRONIZED;

public class ShowingSynchronizedModifier extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Example {
                                
                  public class Storage {
                      public static  Storage instance = new Storage();
                      //...
                      public synchronized void addElement(String str) {
                        //...
                      }
                  }

                  public class InsertElements implements Runnable {
                      private int threadNumber;
                      //...
                      public void run() {
                          Storage.instance.addElement("Thread " + threadNumber + " - " + i);
                      }
                  }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCallCollector(editor, getKeyId(), SEMAPHORE) {
            @Override
            public boolean isHintable(@NotNull PsiMethod element) {
                return element.getModifierList().hasExplicitModifier(SYNCHRONIZED);
            }

        };
    }
}








