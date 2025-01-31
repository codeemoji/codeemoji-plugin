package codeemoji.core.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

public class TestAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        // Show the settings page
        System.out.println("aaa");
   //     ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), InlayHintsSettingsProvider.class);
    }
}