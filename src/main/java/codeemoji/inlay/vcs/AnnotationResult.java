package codeemoji.inlay.vcs;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract sealed class AnnotationResult {
    private AnnotationResult() {
    }

    public static final class NoAnnotation extends AnnotationResult {
        @NotNull
        public static final NoAnnotation INSTANCE = new NoAnnotation();

    }

    public static final class NotReady extends AnnotationResult {
        @NotNull
        public static final NotReady INSTANCE = new NotReady();

    }

    @Getter
    public static final class Success extends AnnotationResult {
        private final Object res;

        public Success(Object res) {
            this.res = res;
        }

    }
}