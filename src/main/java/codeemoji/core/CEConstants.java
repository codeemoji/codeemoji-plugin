package codeemoji.core;

import javax.swing.*;
import java.util.Objects;

public class CEConstants {

    public static final CESymbol SMALL_NAME = new CESymbol(0x1F90F);
    public static final CESymbol CONFUSED = new CESymbol(0x1F937);
    public static final CESymbol MANY = new CESymbol(0x1F590);
    public static final CESymbol ONE = new CESymbol(0x261D);

    public static final CESymbol SYNCHRONIZED_SYMBOL = new CESymbol(0x1F6A6); //traffic light
    public static final CESymbol PUBLIC_SYMBOL = new CESymbol(0x1F31F); //star
    public static final CESymbol PRIVATE_SYMBOL = new CESymbol(0x1F910); //zipper-mouth//(0x1F6AA); //door
    public static final CESymbol PROTECTED_SYMBOL = new CESymbol(0x1F6E1); //shield
    public static final CESymbol ABSTRACT_SYMBOL = new CESymbol(0x1F3A8); //artist palette
    public static final CESymbol DEFAULT_INTERFACE_SYMBOL = new CESymbol(0x1F503); //arrow curving
    public static final CESymbol DEFAULT_SYMBOL = new CESymbol(0x1F3E0); //house
    public static final CESymbol NATIVE_SYMBOL = new CESymbol(0x2699); //gear
    public static final CESymbol STRICTFP_SYMBOL = new CESymbol(0x1F4D0); //triangle ruler
    public static final CESymbol VOLATILE_SYMBOL = new CESymbol(0x26A1); //high voltage
    public static final CESymbol TRANSIENT_SYMBOL = new CESymbol(0x2601); //cloud
    public static final CESymbol FINAL_SYMBOL = new CESymbol(0x1F512); //locked
    public static final CESymbol STATIC_SYMBOL = new CESymbol(0x1F310); //globe

    public static final CESymbol PRIVATE_ICON = new CESymbol(new ImageIcon(Objects.requireNonNull(CEConstants.class.getResource("/icons/private.png"))));
}
