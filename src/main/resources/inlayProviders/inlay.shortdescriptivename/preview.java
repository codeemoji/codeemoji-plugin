/*<# block fmt:fontSize=ABitSmallerThanInEditor,marginPadding=OnlyPadding #>*/
public class Customer {

    private String s = ": ";

    public String statement(String p) {
        String result = p + "-> ";
        while (rentals.hasMoreElements()) {
            Rental a = (Rental) rentals.nextElement();
            result += a.getMovie().getTitle() + s
                    + String.valueOf(a.calculateAmount());
        }
        return result;
    }
}