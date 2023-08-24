<!-- DESCRIPTION HEADER BEGIN -->

# codEEmoji Plugin

**codeEEmoji** is a plug-in made for Intellij Idea and useful for Java programming. The plugin defines new sets of inlay hints in the context of code
augmentation. The new inlay hints use emojis in an innovative way to help the developer. Emojis are displayed for anti-pattern cases such as naming
violations or bad programming practices. The plugin can also show emojis in the editor indicating the presence of modifiers for classes, fields,
methods, parameters or local variables that are being used. Likewise, the developer can indicate emojis to display for all these elements, according
to a rule, for example, for a class being instantiated that implements a specific interface, for a method being invoked that is annotated by a certain
annotation , a local variable of a given type, and so on. For the next version, in addition to emojis, the plugin will be able to show implicit
annotations for JakartaEE and Spring frameworks.

<!-- DESCRIPTION HEADER END -->

## Setup and Use

### Prerequisites

- [**IntelliJ IDEA 2023.***](https://www.jetbrains.com/idea/download/other.html) (Ultimate, Community or Educational)
- JDK 17 [(*Eclipse Temurin - as a suggestion*)](https://github.com/adoptium/temurin17-binaries/releases)

### Installation via marketplace

[JetBrains Marketplace - codEEmoji](https://plugins.jetbrains.com/plugin/22416-cod-emoji)

![How to Download](docs/screenshots/marketplace_install.png)

### Manual installation

- Download the latest release zip file available from [Releases](https://github.com/codeemoji/codeemoji-plugin/releases).

![How to Download](docs/screenshots/howtodownload.png)

- Open Intellij IDEA and navigate to **_"File>Settings>Plugins"_** menu. Click on the gear icon and the "**_Install Plugin From Disk..._**" option.
  Select the downloaded zip file and click "**_OK_**".

![How to Install](docs/screenshots/howtoinstall.png)

- Restart IDE.

### How to configure

The plugin creates new Inlay Hints. All new inlay hints are enabled by default when installing the plugin. To disable inlay hints or configure
options that are available for each one, go to "**_File>Settings>Editor>Inlay Hints_**". Click "**_Other>Java_**".

![How to Configure](docs/screenshots/howtoconfigure.png)

## Cases of Naming Violation

### Short Descriptive Name

Instead of a descriptive name, the variable's name consists of a few letters.

**Configuration options:** Number of letters

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Short Descriptive Name](docs/screenshots/shortdescriptivename.png)

### Getter More Than Accessor

A getter that doesn't just return the corresponding attribute but also takes other actions. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Getter More Than Accessor](docs/screenshots/gettermorethanaccessor.png)

### _Is_ Returns More Than a Boolean

A method's name is a predicate that denotes a true/false value that will be returned. The return type, however, is a more complex type than boolean.
Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Is Returns More Than a Boolean](docs/screenshots/isreturnsmorethanaboolean.png)

### Setter Method Returns

A setter method that has a return type other than void. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Setter Method Returns](docs/screenshots/settermethodreturns.png)

### Expecting But Not Getting a Single Instance

Despite the fact that a method's name suggests it will return a single object, it will actually return a collection. Adapted from Arnaoudova et al.(
2016).

_**Impacted identifiers: Method Names**_

![Expecting But Not Getting a Single Instance](docs/screenshots/expectingbutnotgettingasingleinstance.png)

### Validation Method Does Not Confirm

A validation method (such as one with the words <em>validate,</em> <em>check,</em> or <em>ensure</em>) does not confirm the validation; that is, it
neither provides a return value indicating whether the validation was successful. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Validation Method Does Not Confirm](docs/screenshots/validationmethoddoesnotconfirm.png)

### Getter Does Not Return

When a method's name begins with <em>get</em> or <em>return</em>, for example, it might be assumed that it returns something, but the return type is
actually void. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Getter Does Not Return](docs/screenshots/getterdoesnotreturn.png)

### Not Answered Question

A method's name takes the form of a predicate, but its return type is not boolean. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Not Answered Question](docs/screenshots/notansweredquestion.png)

### Transform Method Does Not Return

While there is no return value, a method's name implies that an object has been transformed. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Transform Method Does Not Return](docs/screenshots/transformmethoddoesnotreturn.png)

### Expecting But Not Getting a Collection

Even though a method's name suggests a collection should be returned, nothing or just one object is instead given. Adapted from Arnaoudova et al.(
2016).

_**Impacted identifiers: Method Names**_

![Expecting But Not Getting a Collection](docs/screenshots/expectingbutnotgettingacollection.png)

### Says One But Contains Many

An attribute's type suggests that it stores a collection of objects, contrary to the name, which suggests a single instance. Adapted from Peruma et
al.(2021).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Says One But Contains Many](docs/screenshots/saysonebutcontainsmany.png)

### Name Suggests Boolean By Type Does Not

An attribute's name implies that its value is true or false, yet its defining type is not boolean. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Name Suggests Boolean By Type Does Not](docs/screenshots/namesuggestsbooleanbytypedoesnot.png)

### Says Many But Contains One

The name of an attribute suggests multiple instances, but its type suggests a single one. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Says Many But Contains One](docs/screenshots/saysmanybutcontainsone.png)

### Name Contains Only Special Characters

The identifier's name is made up entirely of non-alphanumeric characters. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Name Contains Only Special Character](docs/screenshots/namecontainsonlyspecialcharacters.png)

## Cases of Showing Modifiers

This inlay hint allows you to configure the display of emojis for class, field and method modifiers. Emojis are displayed when an element is used in the code, indicating its modifiers. The figure below shows the configuration screen with the options enabled during installation. Then a code snippet is displayed, where all options have been enabled.

_**Impacted identifiers: Classes, Fields and Methods**_

![Showing Modifiers](docs/screenshots/showingmodifiers.png)

![Showing Modifiers - Sample](docs/screenshots/showingmodifierssample.png)

## Cases of Showing Specifics of Projects

This inlay hint is displayed according to the specifics of the project. It must be configured by the developer from a file in the root of the 
project named *"codeemoji.json"*. It allows indicating rules for displaying emojis according to specific features for each element, as follows:

- Element: Class
  - Features: Annotations, Extends and Implements
- Element: Field
  - Features: Annotations, Types
- Element: Method
  - Features: Annotations, Returns
- Element: Parameter
  - Features: Annotations, Types
- Element: Local Variable
  - Features: Annotations, Types

For each element, it is possible to optionally indicate an emoji from the [unicode sequence](https://unicode.org/Public/emoji/15.0/emoji-sequences.txt) that represents it. The *"codeemoji.json"* file follows 
a simple description pattern, as shown in the following a partial example:

![Showing Specifics for the Projects - File Sample](docs/screenshots/showingspecificsoftheprojectfilesample.png)

Complete example [here](docs/codeemoji.json).

In the configuration screen of this inlay hint, the rules currently defined for the open project are displayed. Look the following figure.

![Showing Specifics for the Projects - Configuration](docs/screenshots/showingspecificsoftheproject.png)

Here's an example of usage from a code snipped:

![Showing Specifics for the Projects - Code Sample](docs/screenshots/showingspecificsoftheprojectcodesample.png)


## Cases of Showing Implicit Annotations

Coming soon...

## How to Extend

Coming soon...

## Acknowledgements

This work was supported by the **Free University of Bozen-Bolzano - UNIBZ**.

## References

Arnaoudova, Venera, Massimiliano Di Penta, and Giuliano Antoniol. "Linguistic antipatterns: What they are and how mainers perceive them."
_Empirical Software Engineering_ 21 (2016): 104-158.

