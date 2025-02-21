/*<# block fmt:fontSize=ABitSmallerThanInEditor,marginPadding=OnlyPadding #>*/
import java.util.*;

public class Customer {
    private String[] name;

    public String getItem(byte[] buffer, List device) {
        return doSomething(buffer, device);
    }

    public List<Object> transformValue(int value) {
        List<Object> item = new ArrayList<>();
        item.addAll(doSomething(name, value));
        return item;
    }
}