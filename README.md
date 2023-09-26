![codEEmoji Plugin](./docs/logotype_title.png)

<!-- DESCRIPTION HEADER BEGIN -->

**codeEEmoji** is a plugin made for Intellij Idea and useful for Java programming. The plugin defines new sets of inlay
hints in the context of code
augmentation. The new inlay hints use emojis in an innovative way to help the developer. Emojis are displayed for
anti-pattern cases such as naming
violations or bad programming practices. The plugin can also show emojis in the editor indicating the presence of
modifiers for classes, fields,
methods, parameters or local variables that are being used. Likewise, the developer can indicate emojis to display for
all these elements, according
to a rule, for example, for a class being instantiated that implements a specific interface, for a method being invoked
that is annotated by a certain
annotation , a local variable of a given type, and so on. For the next version, in addition to emojis, the plugin will
be able to show implicit
annotations for JakartaEE and Spring frameworks.

<!-- DESCRIPTION HEADER END -->

<!-- TOC -->

* [Setup and Use](#setup-and-use)
    * [Prerequisites](#prerequisites)
    * [How to Install](#how-to-install)
        * [Via marketplace](#via-marketplace)
        * [Manual Installation](#manual-installation)
    * [How to Configure](#how-to-configure)
* [Cases of Naming Violation](#cases-of-naming-violation)
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
* [Cases of Showing Modifiers](#cases-of-showing-modifiers)
* [Cases of Showing Specifics of Projects](#cases-of-showing-specifics-of-projects)
* [Cases of Showing Implicit Annotations](#cases-of-showing-implicit-annotations)
* [External Services API](#external-services-api)
* [How to Extend](#how-to-extend)
    * [Providers](#providers)
    * [Collectors](#collectors)
        * [CESimpleCollector](#cesimplecollector)
            * [External Analyzers](#external-analyzers)
        * [CEProjectCollector](#ceprojectcollector)
        * [CEImplictCollector](#ceimplictcollector)
    * [Internationalization](#internationalization)
* [Acknowledgements](#acknowledgements)
* [References](#references)

<!-- TOC -->

# Setup and Use

## Prerequisites

- [**IntelliJ IDEA 2023.***](https://www.jetbrains.com/idea/download/other.html) (Ultimate, Community or Educational)
- JDK 17 [(*Eclipse Temurin - as a suggestion*)](https://github.com/adoptium/temurin17-binaries/releases)

## How to Install

### Via marketplace

[JetBrains Marketplace - **codEEmoji**](https://plugins.jetbrains.com/plugin/22416-cod-emoji)

![How to Download](docs/screenshots/marketplace_install.png)

### Manual Installation

- Download the latest release zip file available
  from [Releases](https://github.com/codeemoji/codeemoji-plugin/releases).

![How to Download](docs/screenshots/howtodownload.png)

- Open Intellij IDEA and navigate to **_"File>Settings>Plugins"_** menu. Click on the gear icon and the "*
  *_Install Plugin From Disk..._**" option.
  Select the downloaded zip file and click "**_OK_**".

![How to Install](docs/screenshots/howtoinstall.png)

- Restart IDE.

## How to Configure

The plugin creates new Inlay hints. All new inlay hints are enabled by default when installing the plugin. To disable
inlay hints or configure
options that are available for each one, go to "**_File>Settings>Editor>Inlay Hints_**". Click "**_Other>Java_**".

![How to Configure](docs/screenshots/howtoconfigure.png)

# Cases of Naming Violation

## Short Descriptive Name

Instead of a descriptive name, the variable's name consists of a few letters.

**Configuration options:** Number of letters

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Short Descriptive Name](docs/screenshots/shortdescriptivename.png)

## Getter More Than Accessor

A getter that doesn't just return the corresponding attribute but also takes other actions. Adapted from Arnaoudova et
al.(2016).

_**Impacted identifiers: Method Names**_

![Getter More Than Accessor](docs/screenshots/gettermorethanaccessor.png)

## _Is_ Returns More Than a Boolean

A method's name is a predicate that denotes a true/false value that will be returned. The return type, however, is a
more complex type than boolean.
Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Is Returns More Than a Boolean](docs/screenshots/isreturnsmorethanaboolean.png)

## Setter Method Returns

A setter method that has a return type other than void. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Setter Method Returns](docs/screenshots/settermethodreturns.png)

## Expecting But Not Getting a Single Instance

Despite the fact that a method's name suggests it will return a single object, it will actually return a collection.
Adapted from Arnaoudova et al.(
2016).

_**Impacted identifiers: Method Names**_

![Expecting But Not Getting a Single Instance](docs/screenshots/expectingbutnotgettingasingleinstance.png)

## Validation Method Does Not Confirm

A validation method (such as one with the words <em>validate,</em> <em>check,</em> or <em>ensure</em>) does not confirm
the validation; that is, it
neither provides a return value indicating whether the validation was successful. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Validation Method Does Not Confirm](docs/screenshots/validationmethoddoesnotconfirm.png)

## Getter Does Not Return

When a method's name begins with <em>get</em> or <em>return</em>, for example, it might be assumed that it returns
something, but the return type is
actually void. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Getter Does Not Return](docs/screenshots/getterdoesnotreturn.png)

## Not Answered Question

A method's name takes the form of a predicate, but its return type is not boolean. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Method Names**_

![Not Answered Question](docs/screenshots/notansweredquestion.png)

## Transform Method Does Not Return

While there is no return value, a method's name implies that an object has been transformed. Adapted from Arnaoudova et
al.(2016).

_**Impacted identifiers: Method Names**_

![Transform Method Does Not Return](docs/screenshots/transformmethoddoesnotreturn.png)

## Expecting But Not Getting a Collection

Even though a method's name suggests a collection should be returned, nothing or just one object is instead given.
Adapted from Arnaoudova et al.(
2016).

_**Impacted identifiers: Method Names**_

![Expecting But Not Getting a Collection](docs/screenshots/expectingbutnotgettingacollection.png)

## Says One But Contains Many

An attribute's type suggests that it stores a collection of objects, contrary to the name, which suggests a single
instance. Adapted from Peruma et
al.(2021).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Says One But Contains Many](docs/screenshots/saysonebutcontainsmany.png)

## Name Suggests Boolean By Type Does Not

An attribute's name implies that its value is true or false, yet its defining type is not boolean. Adapted from
Arnaoudova et al.(2016).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Name Suggests Boolean By Type Does Not](docs/screenshots/namesuggestsbooleanbytypedoesnot.png)

## Says Many But Contains One

The name of an attribute suggests multiple instances, but its type suggests a single one. Adapted from Arnaoudova et
al.(2016).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Says Many But Contains One](docs/screenshots/saysmanybutcontainsone.png)

## Name Contains Only Special Characters

The identifier's name is made up entirely of non-alphanumeric characters. Adapted from Arnaoudova et al.(2016).

_**Impacted identifiers: Fields, Method Parameters and Local Variables**_

![Name Contains Only Special Character](docs/screenshots/namecontainsonlyspecialcharacters.png)

# Cases of Showing Modifiers

This inlay hint allows you to configure the display of emojis for class, field and method modifiers. Emojis are
displayed when an element is used in the code, indicating its modifiers. The figure below shows the configuration screen
with the options enabled during installation. Then a code snippet is displayed, where all options have been enabled.

_**Impacted identifiers: Classes, Fields and Methods**_

![Showing Modifiers](docs/screenshots/showingmodifiers.png)

![Showing Modifiers - Sample](docs/screenshots/showingmodifierssample.png)

# Cases of Showing Specifics of Projects

This inlay hint is displayed according to the specifics of the project. It must be configured by the developer from a
file in the root of the
project named *"codeemoji.json"*. It allows indicating rules for displaying emojis according to specific features for
each element, as follows:

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

For each element, it is possible to optionally indicate an emoji from
the [unicode sequence](https://unicode.org/Public/emoji/15.0/emoji-sequences.txt) that represents it. The *"
codeemoji.json"* file follows
a simple description pattern, as shown in the following a partial example:

![Showing Specifics for the Projects - File Sample](docs/screenshots/showingspecificsoftheprojectfilesample.png)

Complete example [here](docs/samples/codeemoji.json).

In the configuration screen of this inlay hint, the rules currently defined for the open project are displayed. See the
following figure.

![Showing Specifics for the Projects - Configuration](docs/screenshots/showingspecificsoftheprojects.png)

Here's an example of usage from a code snipped:

![Showing Specifics for the Projects - Code Sample](docs/screenshots/showingspecificsoftheprojectcodesample.png)

# Cases of Showing Implicit Annotations

Unlike other cases, this inlay hint is useful for displaying implicit annotations when frameworks are used. It can be
displayed for an entirely implicit annotation, for implicit annotation parameters, or even for implicit values for
annotation parameters.

Implicit information is understood to be information that is optional, but with a different standard value. For example,
a class mapped with _@Entity_ will have _@Column_ implicit for almost all fields (restrictions apply), where the _name_
parameter of the
annotation will have the name of the field as its value.

So far the plugin implements some _JPA_ mapping annotations (_Java Persistence API_ - _javax_ and _jakarta_ packages),
and some useful _Spring_ framework annotations.

In the configuration screen of this inlay hint, the annotations currently implemented by plugin are displayed.
See the following figure.

![Showing Implicit Annotations - Configuration](docs/screenshots/showingimplicits.png)

Here's an example of usage with _JPA_ from a code snipped:

![Showing Implicit Annotations - Example](docs/screenshots/showingimplicitsjpasample.png)

Here's an examples of usage with _Spring_ from a code snipped:

![Showing Implicit Annotations - Example](docs/screenshots/showingimplicitsspringsample1.png)

![Showing Implicit Annotations - Example](docs/screenshots/showingimplicitsspringsample2.png)

# External Services API

The **codeEEmoji** plugin is prepared to work with information provided from external services. It provides extension
points
for creating background services that can obtain information about a source code element for which the insertion of an
inlay hint is being evaluated.

This API is experimental and the plugin currently does not contain any concrete services that use it. However, for
future work it may be useful for cases of inlay hints that involve external services such as code versioners, quality
analyzers, artificial intelligence tools for code prediction, among others.

**codeEEmoji** is already prepared to enable or disable these services, as they can reduce the performance of the IDE,
as
they are transversal to the framework. Therefore, it is up to the user to use these services or not. Services can be
configured using the plugin's global settings. See Figure below.

![External Services](docs/screenshots/externalservices.png)

# How to Extend

The **codEEmoji** plugin is under to the terms and conditions of
the [GNU General Public License version 3.0](https://github.com/codeemoji/codeemoji-plugin/blob/main/LICENSE). The
source code
can be accessed on the [GitHub](https://github.com/codeemoji/codeemoji-plugin/) platform, whereby the repository
contains two distinct branches: _"main"_ for official
releases and _"develop"_ for ongoing development activities. The software is extensively developed using
the [IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html). For individuals without
familiarity with the process of developing plugins for the IntelliJ IDEA
IDE, it is recommended to visit the [official website](https://plugins.jetbrains.com/docs/intellij/welcome.html) in
order to acquire a foundational understanding of the subject. An appropriate first reference for individuals with
further expertise would be the section pertaining
to [Inlay hints](https://plugins.jetbrains.com/docs/intellij/inlay-hints.html). It is noteworthy to acknowledge that the
inclusion of the Inlay hints feature represents a recent addition to the
IntelliJ IDEA IDE. Additionally, it is worth mentioning that the **codEEmoji** plugin was created utilizing numerous
APIs
that have been designated as _@Experimental_. The APIs have the potential to undergo modifications in the future, which
could result in compatibility challenges. Nevertheless, the present condition of the plugin indicates that it is
entirely compatible with versions 2023.1.x and 2023.2.x, provided
that [JDK 17](https://github.com/adoptium/temurin17-binaries/releases/tag/jdk-17.0.8.1%2B1) or a higher version is
utilized.

As mentioned in the reference page for
implementing [Inlay hints in the IDE](https://plugins.jetbrains.com/docs/intellij/inlay-hints.html), _"Inlay hints
render small pieces
of
information directly into the editor and give developers additional code insight without disturbing the workflow. A
well-known example is parameter hints that usually display the name of the function parameters as given in its
declaration"_. Inlay hints can be of the type inline (inlays displayed in the code between code tokens) or block (inlays
displayed above a code block) and must be implemented by a provider class that is registered in
the [plugin configuration
file](https://plugins.jetbrains.com/docs/intellij/plugin-configuration-file.html). All cases implemented in the
**codEEmoji** plugin are inlay hints that extend or implement the
interface [
_InlayHintsProvider_](https://github.com/JetBrains/intellij-community/blob/idea/232.9921.47/platform/lang-api/src/com/intellij/codeInsight/hints/InlayHintsProvider.kt).

## Providers

The **codEEmoji** plugin provides two abstract classes (_CEProvider_ and _CEProviderMulti_) that implement the interface
_InlayHintsProvider_. They are starting points for implementing a case for adding an inlay hint. See figure
that follow.

![Provider Class Diagram - Example](docs/screenshots/howtoextend01.png)

As can be seen in the diagram above, a class of type _InlayHintProvider_ must implement a series of methods
which will allow you to configure the inlay hint case in the IDE, allowing you to configure it in the appropriate menus
for a
programming language. The most important point concerns the _getCollectorFor(PsiFile, Editor, S,
InlayHintsSink)_ method. This method returns a class of type [
_InlayHintsCollector_](https://github.com/JetBrains/intellij-community/blob/idea/232.9921.47/platform/lang-api/src/com/intellij/codeInsight/hints/InlayHintsCollector.kt).
This class that is responsible for scanning
the source code elements and execute the logic to identify the point where the addition of an inlay hint is necessary.

## Collectors

Generally a class of type _InlayHintProvider_ is linked to a class of type _InlayHintCollector_. At this point, the
class _CEProviderMulti_ differentiates itself. It extends the _CEProvider_ class and allows a list of
coupled collectors.

The **codEEmoji** provides a rich API for implementing classes of type _InlayHintsCollector_. The figure below displays
the
class diagram available for this purpose.

![Collector Class Diagram - Example](docs/screenshots/howtoextend02.png)

The _InlayHintsCollector_ framework interface can be implemented in the plugin by the _CECollector_ and
_CECollectorMulti_. The interface defines the _collect(PsiElement, Editor, InlayHintsSink)_ method. _CECollectorMulti_
allows you to implement this method using a list of collectors, useful for use with _CEProviderMulti_.

The _CECollector_ abstract class is the main class for implementing a collector. It extends the abstract class
_CEInlayBuilder_ which contains all the methods for manipulating inlay hints. Child classes must implement
_processCollect(PsiElement, Editor, InlayHintsSink)_.

![CECollector Class Diagram - Example](docs/screenshots/howtoextend03.png)

The _CECollector_ has three main extensions: _CESimpleCollector_, _CEProjectCollector_ and _CEImplicitCollector_.
The first is for general use. The other two extensions are specific cases for working with implicit annotations and
specifications of
projects. The following sections explore each case.

### CESimpleCollector

Child classes of _CESimpleCollector_ must implement the _needsHint(H, Map)_ method. This method is parameterized by
type of element to be analyzed and answers whether it is necessary to add an inlay hint according to the logic of the
concrete child class.

The **codEEmoji** provides six abstract classes that directly extend _CESimpleCollector_ that allow you to abstract the
collect of
elements of type _Class_, _Method_ and _Variable (Field, Parameter and Local Variable)_, in addition to elements that
are
referenced in the source code: _Reference Class_, _Reference Field_ and _Reference Method_.

![CESimpleCollector Class Diagram - Example](docs/screenshots/howtoextend04.png)

Example of use:

````java
public class GetMethodDoesNotReturn extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public void getName() {
                        doSomething();
                    }
                }""";
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), CONFUSED) {
            @Override
            public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return (element.getName().startsWith("get")
                        || element.getName().startsWith("return"))
                        && Objects.equals(element.getReturnType(), PsiTypes.voidType());
            }
        };
    }
}
````

For implementations of reference type elements, the plugin already provides abstract classes to work with
references to modifiers in classes, methods, fields, and interface methods.

![CESimpleCollector Class Diagram - Example](docs/screenshots/howtoextend05.png)

Example of use:

````java
public class ShowingModifiers extends CEProviderMulti<ShowingModifiersSettings> {

    //source code omitted...

    @Override
    public @NotNull List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();
        //fields
        list.addAll(
                Arrays.asList(
                        new CEModifierFieldCollector(editor, getKeyId(), PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_FIELD)),
                        new CEModifierFieldCollector(editor, getKeyId(), DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_FIELD)),
                        new CEModifierFieldCollector(editor, getKeyId(), FINAL_VAR_SYMBOL, FINAL, getSettings().query(FINAL_FIELD)),
                        new CEModifierFieldCollector(editor, getKeyId(), PROTECTED_SYMBOL, PROTECTED, getSettings().query(PROTECTED_FIELD)),
                        new CEModifierFieldCollector(editor, getKeyId(), PRIVATE_SYMBOL, PRIVATE, getSettings().query(PRIVATE_FIELD)),
                        new CEModifierFieldCollector(editor, getKeyId(), STATIC_SYMBOL, STATIC, getSettings().query(STATIC_FIELD)),
                        new CEModifierFieldCollector(editor, getKeyId(), VOLATILE_SYMBOL, VOLATILE, getSettings().query(VOLATILE_FIELD)),
                        new CEModifierFieldCollector(editor, getKeyId(), TRANSIENT_SYMBOL, TRANSIENT, getSettings().query(TRANSIENT_FIELD))
                )
        );

        //source code omitted...

        return list;
    }
}
````

#### External Analyzers

The plugin ensures that before invoking the _needsHint(H, Map)_ method, the element under collect analysis is sent to
everyone
analyzers with external services using _processExternalInfo(H)_ method. To do this, a Map is passed to fill in the
respective information for each
service using a _CEExternalAnalyzer_ singleton and the list of implementations of _CEEExternalService_ interface. This
way, in the concrete child class, the _needsHint(H, Map)_ method receives a Map with information that can
subsidize the addition or not of an inlay hint.

![CEExternalAnalyzer Class Diagram - Example](docs/screenshots/howtoextend06.png)

### CEProjectCollector

_CEProjectCollector_ provides ways to work with a specific case of collector. It allows you to collect elements
configurable according to the _codeemoji.json_ configuration file directly in the user's project. The plugin already
provides classes for collecting elements of type _Class_, _Method_ and _Variable_. The diagram below shows these
collectors and displays the available interfaces.

![CEExternalAnalyzer Class Diagram - Example](docs/screenshots/howtoextend07.png)

Example of use:

````java
public class ShowingSpecifics extends CEProviderMulti<ShowingSpecificsSettings> {

    //source code omitted...

    @Override
    public @NotNull List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();

        list.add(new CEProjectClassCollector(editor, getKeyId()));
        list.add(new CEProjectMethodCollector(editor, getKeyId()));
        list.add(new CEProjectVariableCollector(editor, FIELD, getKeyId()));
        list.add(new CEProjectVariableCollector(editor, PARAMETER, getKeyId()));
        list.add(new CEProjectVariableCollector(editor, LOCALVARIABLE, getKeyId()));

        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingSpecificsSettings settings) {
        return new ShowingSpecificsConfigurable(settings);
    }

}
````

### CEImplictCollector

_CEImplicitCollector_ is also a specific type of collector in the plugin. It allows you to detect the need for implicit
annotations hints to be added via block-type inlay hint. It also allows you to detect implicit annotation attributes as
well as their respective implicit values.

The plugin already provides implementation for several annotations of the _JPA_ and
_Spring_, being fully extensible to other frameworks. In the diagram below, an example of _JPA_ with
_@Entity_ and _@Column_. _CEJPAEntityCollector_ defines a collector to work with the main annotation _@Entity_. The
collector has a list of classes that implement the _CEImplicit_ interface. This way, each item in this list is
invoked to investigate whether to process an inlay hint.

![CEExternalAnalyzer Class Diagram - Example](docs/screenshots/howtoextend08.png)

Example of use:

````java
public class ImplicitAnnotations extends CEProviderMulti<ImplicitAnnotationsSettings> {

    //source code omitted...

    @Override
    public @NotNull List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        final int codePoint = 0x1F4AD;
        String keyId = getKeyId();
        return new ArrayList<>(
                Arrays.asList(
                        new CEJPAEntityCollector(editor, keyId, codePoint, "javax.persistence"),
                        new CEJPAEntityCollector(editor, keyId, codePoint, "jakarta.persistence"),
                        new CEJPAEmbeddableCollector(editor, keyId, codePoint, "javax.persistence"),
                        new CEJPAEmbeddableCollector(editor, keyId, codePoint, "jakarta.persistence"),
                        new CESpringConfigurationCollector(editor, keyId, codePoint),
                        new CESpringControllerCollector(editor, keyId, codePoint),
                        new CESpringRestControllerCollector(editor, keyId, codePoint)
                ));
    }

    //source code omitted...
}
````

## Internationalization

The plugin is fully ready for internationalization. The _CEBundle.properties_ bundle is provided. All strings
for tooltips, settings, warnings, information and exceptions can be internationalized centrally. At the moment, all
these messages are in the English language. To extend it to another language, simply create a file named with
_CEBundle_XX.properties_, where _XX_ stands for the language acronym. The IDE will automatically detect the language
used and
carry out the appropriate configuration.

# Acknowledgements

This work was supported by the **Free University of Bozen-Bolzano - UNIBZ**.

# References

Arnaoudova, Venera, Massimiliano Di Penta, and Giuliano Antoniol. "Linguistic antipatterns: What they are and how
mainers perceive them."
_Empirical Software Engineering_ 21 (2016): 104-158.

