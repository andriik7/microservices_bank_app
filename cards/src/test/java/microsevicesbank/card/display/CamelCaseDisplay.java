package microsevicesbank.card.display;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class CamelCaseDisplay extends DisplayNameGenerator.Standard {

    public CamelCaseDisplay() {
    }

    public String generateDisplayNameForClass(Class<?> testClass) {
        return this.replaceCapitals(super.generateDisplayNameForClass(testClass));
    }

    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        return this.replaceCapitals(super.generateDisplayNameForNestedClass(nestedClass));
    }

    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return this.replaceCapitals(testMethod.getName());
    }

    private String replaceCapitals(String name) {
        name = name.replaceAll("([A-Z])", " $1");
        name = name.replaceAll("([0-9]+)", " $1");
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }
}