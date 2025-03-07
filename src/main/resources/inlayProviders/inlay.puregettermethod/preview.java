/*<# block fmt:fontSize=ABitSmallerThanInEditor,marginPadding=OnlyPadding #>*/
public class PureGetterMethodExample {

    private int attribute;

    public PureGetterMethodExample(int attribute){
        this.attribute = attribute;
    }

    private int getAttribute(){
        return attribute;
    }
}