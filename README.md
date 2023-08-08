<!-- DESCRIPTION HEADER BEGIN -->

# Intellij codƎEmoji Plugin

**codƎEmoji** is an Intellij IDE plugin for Java, that inserts inlay hints in the context of code augmentation. The inlay hints inserted with the
plugin are made from emojis for anti-pattern cases, allowing the mainer to identify name violations in the code. In future versions, the plugin
will be able to insert inlay hints with emojis for invisible feature cases, in addition to displaying inlay hints without emojis for invisible
annotations.

<!-- DESCRIPTION HEADER END -->

<!-- TOC -->

* [Intellij codƎEmoji Plugin](#intellij-codǝemoji-plugin)
    * [Setup and Use](#setup-and-use)
        * [Prerequisites](#prerequisites)
        * [How to install](#how-to-install)
        * [How to configure](#how-to-configure)
    * [Naming Violation Cases](#naming-violation-cases)
        * [Short Descriptive Name](#short-descriptive-name)
        * [Getter More Than Accessor](#getter-more-than-accessor)
        * [_Is_ Returns More Than a Boolean](#is-returns-more-than-a-boolean)
        * [Setter Method Returns](#setter-method-returns)
        * [Expecting But Not Getting a Single Instance](#expecting-but-not-getting-a-single-instance)
        * [Validation Method Does Not Confirm](#validation-method-does-not-confirm)
        * [Getter Does Not Return](#getter-does-not-return)
        * [Not Answered Question](#not-answered-question)
        * [Transform Method Does Not Return](#transform-method-does-not-return)
        * [Expecting But Not Getting a Collection](#expecting-but-not-getting-a-collection)
        * [Says One But Contains Many](#says-one-but-contains-many)
        * [Name Suggests Boolean By Type Does Not](#name-suggests-boolean-by-type-does-not)
        * [Says Many But Contains One](#says-many-but-contains-one)
        * [Name Contains Only Special Characters](#name-contains-only-special-characters)
    * [Invisible Features Cases](#invisible-features-cases)
    * [Invisible Annotations Cases](#invisible-annotations-cases)
    * [How to Extend](#how-to-extend)
    * [Acknowledgements](#acknowledgements)
    * [References](#references)

<!-- TOC -->

## Setup and Use

### Prerequisites

- **Intellij IDEA 2023.***
- Java JDK 17

### How to install

- Download the latest release zip file (**codEEmojiPlugin-x.x.x.zip**) available
  from [Releases](https://github.com/codeemoji/codeemoji-plugin/releases), where **x.x.x** is the version number.

![How to Download](docs/screenshots/howtodownload.png)

- Open Intellij IDEA and navigate to **_"File>Settings>Plugins"_** menu. Click on the gear icon and the "**_Install Plugin From Disk..._**" option.
  Select the downloaded zip file and click "**_OK_**".

![How to Install](docs/screenshots/howtoinstall.png)

- Restart IDE.

### How to configure

The plugin creates new Inlay Hints. **All are enabled by default when installing the plugin**. To disable hinlay hints or configure options
that are available for each one, go to "**_File>Settings>Editor>Inlay Hints_**". Click "**_Other>Java_**".

![How to Configure](docs/screenshots/howtoconfigure.png)

## Naming Violation Cases

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

## Invisible Features Cases

Coming soon...

## Invisible Annotations Cases

Coming soon...

## How to Extend

Coming soon...

## Acknowledgements

This work was supported by the **Free University of Bozen-Bolzano - UNIBZ**.

## References

Arnaoudova, Venera, Massimiliano Di Penta, and Giuliano Antoniol. "Linguistic antipatterns: What they are and how mainers perceive them."
_Empirical Software Engineering_ 21 (2016): 104-158.

