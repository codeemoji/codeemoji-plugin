/*<# block fmt:fontSize=ABitSmallerThanInEditor,marginPadding=OnlyPadding #>*/
public class PureSetterMethodExample {

    private int attribute;

    public PureSetterMethodExample(int attribute){
        this.attribute = attribute;
    }

    private void setAttribute(int attribute){
        this.attribute = attribute;
    }
}