package codeemoji.core;

import javax.swing.*;
import java.util.Objects;

public class CEConstants {

    public static final CESymbol SMALL_NAME = new CESymbol(0x1F90F);
    public static final CESymbol CONFUSED = new CESymbol(0x1F937);
    public static final CESymbol MANY = new CESymbol(0x1F590);
    public static final CESymbol ONE = new CESymbol(0x261D);
    public static final CESymbol SEMAPHORE = new CESymbol(0x1F6A6);
    public static final CESymbol GLOBE = new CESymbol(0x1F30D);
    public static final CESymbol KEY = new CESymbol(0x1F5DD);
    public static final CESymbol SHIELD = new CESymbol(0x1F6E1);
    public static final CESymbol WHITE_CIRCLE = new CESymbol(0x26AA);
    public static final CESymbol ORANGE_CIRCLE = new CESymbol(0x1F7E0);
    public static final CESymbol WHITE_FLAG = new CESymbol(0x1F3F3);
    public static final CESymbol GEAR = new CESymbol(0x2699);
    public static final CESymbol HASHTAG = new CESymbol(0x0023);
    public static final CESymbol SPARKLE = new CESymbol(0x2728);
    public static final CESymbol BALLOON = new CESymbol(0x1F4AD);
    public static final CESymbol MEDAL = new CESymbol(0x1F947);
    public static final CESymbol RAISED_HAND = new CESymbol(0x270A);

    public static final CESymbol FINAL_ICON = new CESymbol(new ImageIcon(Objects.requireNonNull(CEConstants.class.getResource("/icons/final.png"))));
}
