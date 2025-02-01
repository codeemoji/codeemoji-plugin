package codeemoji.core.actions;

import com.intellij.codeInsight.hints.JavaFqnDeclarativeInlayActionHandler;
import com.intellij.codeInsight.hints.declarative.InlayActionHandler;
import com.intellij.codeInsight.hints.declarative.InlayActionPayload;
import com.intellij.codeInsight.hints.declarative.StringInlayActionPayload;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ActionsKt;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.concurrency.AppExecutorUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class TestAction implements InlayActionHandler {

    @Override
    public void handleClick(@NotNull Editor editor, @NotNull InlayActionPayload payload) {
        Intrinsics.checkNotNullParameter(editor, "editor");
        Intrinsics.checkNotNullParameter(payload, "payload");
        Project var10000 = editor.getProject();
        if (var10000 != null) {
            Project project = var10000;
            StringInlayActionPayload var6 = (StringInlayActionPayload)payload;
            String fqn = ((StringInlayActionPayload)payload).getText();
            JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
            AppExecutorUtil.getAppExecutorService().submit(()->TestAction.handleClick$lambda$0(facade, fqn, project));
        }
    }

    private static void handleClick$lambda$0(final JavaPsiFacade $facade, final String $fqn, final Project $project) {
        Intrinsics.checkNotNullParameter($fqn, "$fqn");
        /*
        Intrinsics.checkNotNullParameter($project, "$project");
        ActionsKt.runReadAction(()->{
                final PsiClass aClass = $facade.findClass($fqn, GlobalSearchScope.allScope($project));
                if (aClass != null) {
                    ActionsKt.invokeLater((ModalityState)null, (()-> {
                        public final void invoke() {
                            aClass.navigate(true);
                        }

                        // $FF: synthetic method
                        // $FF: bridge method
                        public Object invoke() {
                            this.invoke();
                            return Unit.INSTANCE;
                        }
                    }));
                }

            });

            // $FF: synthetic method
            // $FF: bridge method
            public Object invoke() {
                this.invoke();
                return Unit.INSTANCE;
            }
        }));*/
    }
}