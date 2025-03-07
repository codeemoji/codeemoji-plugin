/*<# block fmt:fontSize=ABitSmallerThanInEditor,marginPadding=OnlyPadding #>*/
public class StateChangingMethodExample {
    private int attribute;

    public void stateChangingMethod() {
        this.attribute = this.attribute * 2;
    }
}