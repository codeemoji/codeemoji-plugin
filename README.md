<!-- Plugin description -->

# Intellij codƎEmoji Plugin

**codƎEmoji** is an Intellij IDE plugin that inserts inlay hints in the context of code augmentation. The inlay hints inserted with the plugin
are made from emojis for anti-pattern cases, allowing the developer to identify name violations in the code. The plugin works for the **Java**
programming language.

## Setup and Use

### Prerequisites

- Intellij IDEA 2023
- Java JDK 17

### How to install

Download the latest release zip file (**codEEmojiPlugin-x.x.x.zip**) available
from [Releases](https://github.com/codeemoji/codeemoji-plugin/releases), where **x.x.x** is the version number.

![How to Download](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/howtodownload.png)

Open Intellij IDEA and navigate to **_"File > Settings > Plugins"_** menu. Click on the gear icon and the "**_Install Plugin From Disk..._**" option.
Select the downloaded zip file and click "**_OK_**".

![How to Install](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/howtoinstall.png)

### How to configure

The plugin creates new Inlay Hints. **All are enabled by default when installing the plugin**. To disable hinlay hints or configure options
that are available for each one, go to "**_File>Settings>Editor>Inlay Hints_**". Click "**_Other>Java_**".

![How to Configure](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/develop/docs/images/howtoconfigure.png)

## Naming Violation Cases

### Short Descriptive Name

Instead of a descriptive name, the variable's name consists of a few letters.

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Short Descriptive Name](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/shortdescriptivename.png)

### Getter More Than Accessor

A getter that doesn't just return the corresponding attribute but also takes other actions. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Getter More Than Accessor](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/gettermorethanaccessor.png)

### _Is_ Returns More Than a Boolean

A method's name is a predicate that denotes a true/false value that will be returned. The return type, however, is a more complex type than boolean.
Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Is Returns More Than a Boolean](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/isreturnsmorethanaboolean.png)

### Setter Method Returns

A setter method that has a return type other than void. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Setter Method Returns](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/settermethodreturns.png)

### Expecting But Not Getting a Single Instance

Despite the fact that a method's name suggests it will return a single object, it will actually return a collection. Adapted from Arnaoudova et al.(
2016).

_**Impacted identifiers: Method Names**_

![Expecting But Not Getting a Single Instance](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/expectingbutnotgettingasingleinstance.png)

### Validation Method Does Not Confirm

A validation method (such as one with the words <em>validate,</em> <em>check,</em> or <em>ensure</em>) does not confirm the validation; that is, it
neither provides a return value indicating whether the validation was successful. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Validation Method Does Not Confirm](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/validationmethoddoesnotconfirm.png)

### Getter Does Not Return

When a method's name begins with <em>get</em> or <em>return</em>, for example, it might be assumed that it returns something, but the return type is
actually void. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Getter Does Not Return](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/getterdoesnotreturn.png)

### Not Answered Question

A method's name takes the form of a predicate, but its return type is not boolean. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Not Answered Question](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/notansweredquestion.png)

### Transform Method Does Not Return

While there is no return value, a method's name implies that an object has been transformed. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Transform Method Does Not Return](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/transformmethoddoesnotreturn.png)

### Expecting But Not Getting a Collection

Even though a method's name suggests a collection should be returned, nothing or just one object is instead given. Adapted from Arnaoudova et al.(
2016).

_**Impacted identifiers: Method Names**_

![Expecting But Not Getting a Collection](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/expectingbutnotgettingacollection.png)

### Says One But Contains Many

An attribute's type suggests that it stores a collection of objects, contrary to the name, which suggests a single instance. Adapted from Peruma et
al.(2021).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Says One But Contains Many](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/saysonebutcontainsmany.png)

### Name Suggests Boolean By Type Does Not

An attribute's name implies that its value is true or false, yet its defining type is not boolean. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Name Suggests Boolean By Type Does Not](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/namesuggestsbooleanbytypedoesnot.png)

### Says Many But Contains One

The name of an attribute suggests multiple instances, but its type suggests a single one. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Says Many But Contains One](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/saysmanybutcontainsone.png)

### Name Contains Only Special Characters

The identifier's name is made up entirely of non-alphanumeric characters. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Name Contains Only Special Character](https://raw.githubusercontent.com/codeemoji/codeemoji-plugin/main/docs/images/namecontainsonlyspecialcharacters.png)

## Invisible Features Cases

Coming soon...

## Invisible Annotations Cases

Coming soon...

## How to Extend

Coming soon...

## Acknowledgements

This work was supported by the **Free University of Bozen-Bolzano - UNIBZ**.

## References

Arnaoudova, Venera, Massimiliano Di Penta, and Giuliano Antoniol. "Linguistic antipatterns: What they are and how developers perceive them."
_Empirical
Software Engineering_ 21 (2016): 104-158.

<!-- Plugin description end -->

