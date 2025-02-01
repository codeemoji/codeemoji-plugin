package codeemoji.inlay.vcs;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.nameviolation.ValidationMethodDoesNotConfirm;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GitTest extends CEProvider<GitTest.Settings> {

    public static class Settings extends CEBaseSettings<Settings> {}

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public void blame() {
                        doSomething();
                    }
                }""";
    }

    //TODO: EditorGutterAction seems to be an interfeace to impl to be able to click on the annotations. useful

    // I believe this is called once per FILE in your project, when opened

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {

        return new CESimpleMethodCollector(editor, getKey(),
                mainSymbol()) {

            //so i thin this is an object that returns the author name for each line
            @Nullable
            private final FileAnnotation vcsBlame = CEVcsUtils.getAnnotation(psiFile, editor);


            //key for stuff stored in the editor. Probably used to cache stuff
            //NOTE! the object here is MY object so i should later use my own key
            private static final Key<VcsCodeAuthorInfo> PREVIEW_INFO_KEY = new Key<>("my.preview.author.info");

            //extends InlayHintsCollector
            @Override
            public boolean needsInlay(@NotNull PsiMethod element){
                if(true)return false;
                if (vcsBlame == null) return false;
                // Get the current project and file
                Project project = element.getProject();
                VirtualFile file = element.getContainingFile().getVirtualFile();

                GitRepositoryManager gitRepositoryManager = GitRepositoryManager.getInstance(project);
                // Get the Git repository from the project
                GitRepository repo = gitRepositoryManager.getRepositories()
                        .stream().findFirst().orElse(null);
                //TODO: improve this
                if (repo == null || file == null) {
                    return false; // No repository or file found
                }
                // GitAnnotationProvider annotationProvider = new GitAnnotationProvider(project);
                //   var annotated = annotationProvider.annotate(file);
                //VcsAnnotationProvider vcsAnnotationProvider = VcsAnnotationProvider.getInstance(project);

                //text range of this element without commments
                TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

                VcsCodeAuthorInfo info = editor.getUserData(PREVIEW_INFO_KEY);
                if (info == null) {
                    info = getCodeAuthorInfo(project, textRange, editor);
                }

                return true;
            }

            private VcsCodeAuthorInfo getCodeAuthorInfo(Project project, TextRange range, Editor editor) {
                int startLine = editor.getDocument().getLineNumber(range.getStartOffset());
                int endLine = editor.getDocument().getLineNumber(range.getEndOffset());
                UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(editor.getDocument(), project);

                var aspect = Arrays.stream(vcsBlame.getAspects())
                        .filter(a -> Objects.equals(a.getId(), LineAnnotationAspect.AUTHOR))
                        .findFirst().orElse(null);

                Map<String, Long> authorsFrequency =
                        IntStream.rangeClosed(startLine, endLine)
                                .mapToObj(provider::getLineNumber)
                                .map(aspect::getValue)  //gets the author name for line
                                .filter(Objects::nonNull)
                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                long maxFrequency = authorsFrequency.values().stream()
                        .max(Long::compare)
                        .orElse(0L);
                if (maxFrequency == 0) {
                    return VcsCodeAuthorInfo.NEW_CODE;
                }

                String mainAuthor = authorsFrequency.entrySet().stream()
                        .filter(entry -> entry.getValue() == maxFrequency)
                        .map(Map.Entry::getKey)
                        .min(String::compareTo)
                        .orElse(null);

                return new VcsCodeAuthorInfo(
                        mainAuthor,
                        authorsFrequency.size() - 1,
                        provider.isRangeChanged(startLine, endLine + 1)
                );
            }


        };

    }

    // code shamelessly copied from VcsCodeAuthorInlayHintsCollector


    record VcsCodeAuthorInfo(@Nullable String mainAuthor, int otherAuthorsCount, boolean isModified) {
        static final VcsCodeAuthorInfo NEW_CODE = new VcsCodeAuthorInfo(null, 0, true);
    }

    /*


            @Override
        public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
            var jSpinner = new JSpinner();
            jSpinner.setValue(settings.getMethodCount());
            jSpinner.addChangeListener(event -> {
                settings.setMethodCount((Integer) jSpinner.getValue());
                changeListener.settingsChanged();
            });

            // Create table model for map display
            String[] columnNames = {"Key", "Value"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            for (Map.Entry<String, String> entry : settings.getMap().entrySet()) {
                tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
            }

            // Create table for displaying the map's key-value pairs
            JTable table = new JBTable(tableModel);
            table.setPreferredScrollableViewportSize(new Dimension(300, 150));
            table.setFillsViewportHeight(true);
            table.setAutoCreateRowSorter(true);

            // Add a button to add new rows
            JButton addButton = new JButton("Add Row");
            addButton.addActionListener(e -> {
                // Add a new row with empty key and value
                tableModel.addRow(new Object[]{"", ""});
            });

            // Handle table updates (editing cells)
            table.getModel().addTableModelListener(e -> {
                // Update the map whenever table cells are edited
                for (int row = 0; row < table.getRowCount(); row++) {
                    String key = (String) table.getValueAt(row, 0);
                    String value = (String) table.getValueAt(row, 1);
                    settings.getMap().put(key, value);  // Update map with the edited values
                }
                changeListener.settingsChanged();  // Notify that settings have changed
            });






            // Display the selected emoji
            JBLabel emojiLabel = new JBLabel("Selected Emoji: " + settings.getEmoji());

            // Button to open emoji picker dialog
            JButton pickEmojiButton = new JButton("Pick Emoji");
            pickEmojiButton.addActionListener(e -> {
                // Show emoji picker dialog
                EmojiPickerPanel emojiPickerPanel = new EmojiPickerPanel(
                        EmojiRepository.fetchAndParseEmojis(),
                        pickEmojiButton.getFont(), 60, 60,
                        em ->{
                          settings.setEmoji(em.symbol());
                        });
                JOptionPane.showOptionDialog(
                        null,
                        emojiPickerPanel,
                        "Select an Emoji",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        new Object[] {},
                        null
                );
                emojiLabel.setText("Selected Emoji: " + settings.getEmoji());  // Update emoji label
            });


            // Create a panel for layout
            JPanel panel = FormBuilder.createFormBuilder()
                    .addLabeledComponent("Method Count", jSpinner)
                    .addComponent(addButton)  // Add the button to add rows
                    .addLabeledComponent("Current Emoji", emojiLabel)
                    .addComponent(pickEmojiButton)
                    .addComponent(new JScrollPane(table))  // Add the table
                    .getPanel();

            return panel;
        }
     */
}








