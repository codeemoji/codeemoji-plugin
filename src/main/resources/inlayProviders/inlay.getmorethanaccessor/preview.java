/*<# block fmt:fontSize=ABitSmallerThanInEditor,marginPadding=OnlyPadding #>*/
public class Customer {
    public List<Item> getItems() {
        if (items == null) {
            List<Item> list = new ArrayList<>();
            Sale sale = getSale();
            for (Item i : sale.items()) {
                if (i.confirmed()){
                    list.add(i);
                }
            }
            list = Collections.unmodifiableList(list);
        }
        return items;
    }
}