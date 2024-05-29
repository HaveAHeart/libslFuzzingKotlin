//#! pragma: non-synthesizable
libsl "1.1.0";

library any
    version "*"
    language "*"
    url "-";

//доставать тип из сигнатуры
//генерация прокси -> реальные данные -> фаззить указанные поля (со связями данных)
//Predicate + зависимость
//fuzzable
//зависимость от аргументов функции, результату функции (аргументам класса?)
//require/ensure
//property-based testing quicksheck annotations JQwik (junit-quickcheck)?
//вынимать ограничения/связи из LibSL при фаззинге

import java.common;

type URLHolder is com.spbpu.URLHolder for Foo {}
type Person is com.spbpu.Person for Foo {}



annotation AnnotationURLLoader (
    fieldClass: string = "String",
    fieldName: string = "url",
    regexp: string = "(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?\/[a-zA-Z0-9]{2,}|((https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?)|(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}(\.[a-zA-Z0-9]{2,})?",
    minLength: int32 = 10,
    maxLength: int32 = 20
);




annotation AnnotationOuterSizeLimit (
fieldClass: string = "Integer",
fieldName: string = "length",
minValue: int32 = 10,
maxValue: int32 = 150,
dependsOn: string = "URLLoader",
dependsWith: string = "URL.length",
);

automaton Test(): Test {
    @smallInt
    fun annotatedZeroToTen() // без переопределений: [0, 10]

    @smallInt(minValue = 5)
    fun annotatedFiveToTen() // с одним переопределением: [5, 10]

    @smallInt(minValue = 1, maxValue = 5)
    fun annotatedOneToFive() // с двумя переопределениями: [1, 5]

    fun twoPowerOneToThree(inInt: int32): bool {
        requires isOneToThree: inInt >= 1 && inInt <= 3;
        ensures isCorrectPow: res == 2 || res == 4 || res == 8;
    }

}

annotation length (
    class: string = "kotlin.Int",
    name: string = "len",
    minValue: int32 = 10,
    maxValue: int32 = 20
);

annotation height (
    class: string = "kotlin.Float",
    name: string = "height",
    minValue: int32 = 10,
    maxValue: int32 = 20
);

annotation fuzzString(
regexp: string = "[abc]+",
minLength: int32 = 10,
maxLength: int32 = 20
);

annotation fuzzInt(
    minValue: int32 = 10,
    maxValue: int32 = 150
);

annotation smallInt(
    minValue: int32 = 0,
    maxValue: int32 = 10,
);


annotation largerInt(
minValue: int32 = 10,
maxValue: int32 = 20,
);

annotation alwaysPlusOne(
minValue: int32 = 0,
maxValue: int32 = 1000,
dependsOnAutomaton: string = "URLLoaderAutomaton",
dependsOnMethod: string = "compareIntAndPlusOne",
dependsOnParam: string = "int1",
dependsWith: string = "return int1 + 1"
);

//type URLLoader is `com.spbpu.URLLoader` {

//}


annotation returnLen (
class: string = "kotlin.Int",
name: string = "len",
minValue: int32 = 10,
maxValue: int32 = 20
);

annotation returnURL (
class: string = "kotlin.String",
name: string = "url",
regexp: string = "(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?\/[a-zA-Z0-9]{2,}|((https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?)|(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}(\.[a-zA-Z0-9]{2,})?",
minLength: int32 = 10,
maxLength: int32 = 25
);

annotation dependableLen (
class: string = "kotlin.Int",
name: string = "len",
minValue: int32 = 10,
maxValue: int32 = 20,
dependsOn: string = "url",
dependsOnType: string = "kotlin.String",
dependsWith: string = "length"
);



annotation isEven(
    class: string = "kotlin.Boolean",
    name: string = "isEven",
    defaultValue: bool = false
);

annotation nicknameLetter(
    class: string = "kotlin.Char",
    name: string = "nicknameLetter",
    availibleChars: string = "xXx_TuRb0_2007_xXx"
);

annotation height (
    class: string = "kotlin.Float",
    name: string = "height",
    minValue: float32 = 120.1,
    maxValue: float32 = 199.9,
);

annotation dependableBald (
    class: string = "kotlin.Boolean",
    name: string = "isBald",
    dependsOn: string = "height",
    dependsOnType: string = "kotlin.Float",
    dependsWith: string = "toInt() > 150"
);
annotation name (
    class: string = "kotlin.String",
    name: string = "name",
    regexp: string = "[A-Z][a-z]+",
    minLength: int32 = 5,
    maxLength: int32 = 15
);

automaton URLHolder(var initURL: string): URLHolder {
    @returnURL
    @dependableLen
    fun getNewURLHolder(): URLHolder {
        ensures isURLPresent: `genResult.getURL().length` > 0 ;
        ensures isLenPresent: `genResult.getLen()` > 0 ;
    }
}

automaton Person(var name: string, var height: float32, var isBald: bool): Person {
    @name
    @height
    @dependableBald
    fun getPerson(): Person {
        ensures isPersonPresent: `genResult != null`;
    }
}


