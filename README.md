<!-- Plugin description -->

# Intellij codƎEmoji Plugin

**codƎEmoji** is an Intellij IDE plugin that inserts inlay hints in the context of code augmentation. The inlay hints inserted with the plugin
are made from emojis for anti-pattern cases, allowing the developer to identify name violations in the code. The plugin works for the Java
programming language.

The cases implemented in the plugin are listed below.

### Short Descriptive Name

Instead of a descriptive name, the variable's name consists of a few letters.

![Short Descriptive Name](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/shortdescriptivename.png)

### Getter More Than Accessor

A getter that doesn't just return the corresponding attribute but also takes other actions. Adapted from Peruma et al.(2021).

![Getter More Than Accessor](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/gettermorethanaccessor.png)

### _Is_ Returns More Than a Boolean

A method's name is a predicate that denotes a true/false value that will be returned. The return type, however, is a more complex type than boolean.
Adapted from Peruma et al.(2021).

![Is Returns More Than a Boolean](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/isreturnsmorethanaboolean.png)

### Setter Method Returns

A setter method that has a return type other than void. Adapted from Peruma et al.(2021).

![Setter Method Returns](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/settermethodreturns.png)

### Expecting But Not Getting a Single Instance

Despite the fact that a method's name suggests it will return a single object, it will actually return a collection. Adapted from Peruma et al.(2021).

![Expecting But Not Getting a Single Instance](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/expectingbutnotgettingasingleinstance.png)

### Validation Method Does Not Confirm

A validation method (such as one with the words <em>validate,</em> <em>check,</em> or <em>ensure</em>) does not confirm the validation; that is, it
neither provides a return value indicating whether the validation was successful. Adapted from Peruma et al.(2021).

![Validation Method Does Not Confirm](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/validationmethoddoesnotconfirm.png)

### Getter Does Not Return

When a method's name begins with <em>get</em> or <em>return</em>, for example, it might be assumed that it returns something, but the return type is
actually void. Adapted from Peruma et al.(2021).

![Getter Does Not Return](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/getterdoesnotreturn.png)

### Not Answered Question

A method's name takes the form of a predicate, but its return type is not boolean. Adapted from Peruma et al.(2021).

![Not Answered Question](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/notansweredquestion.png)

### Transform Method Does Not Return

While there is no return value, a method's name implies that an object has been transformed. Adapted from Peruma et al.(2021).

![Transform Method Does Not Return](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/transformmethoddoesnotreturn.png)

### Expecting But Not Getting a Collection

Even though a method's name suggests a collection should be returned, nothing or just one object is instead given. Adapted from Peruma et al.(2021).

![Expecting But Not Getting a Collection](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/expectingbutnotgettingacollection.png)

### Says One But Contains Many

An attribute's type suggests that it stores a collection of objects, contrary to the name, which suggests a single instance. Adapted from Peruma et
al.(2021).

![Says One But Contains Many](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/saysonebutcontainsmany.png)

### Name Suggests Boolean By Type Does Not

An attribute's name implies that its value is true or false, yet its defining type is not boolean. Adapted from Peruma et al.(2021).

![Name Suggests Boolean By Type Does Not](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/namesuggestsbooleanbytypedoesnot.png)

### Says Many But Contains One

The name of an attribute suggests multiple instances, but its type suggests a single one. Adapted from Peruma et al.(2021).

![Says Many But Contains One](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/saysmanybutcontainsone.png)

### Name Contains Only Special Characters

The identifier's name is made up entirely of non-alphanumeric characters. Adapted from Peruma et al.(2021).

![Name Contains Only Special Character](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/namecontainsonlyspecialcharacters.png)

### Acknowledgements

This work was supported by the **Free University of Bozen-Bolzano - UNIBZ**.

### References

Peruma, Anthony, Venera Arnaoudova, and Christian D. Newman. "Ideal: An open-source identifier name appraisal tool." _2021 IEEE International
Conference on Software Maintenance and Evolution (ICSME)._ IEEE, 2021.

<!-- Plugin description end -->

