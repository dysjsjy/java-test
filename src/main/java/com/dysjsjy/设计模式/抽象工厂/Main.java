package com.dysjsjy.设计模式.抽象工厂;

public class Main {

    public static void main(String[] args) {

    }
}

// 感觉抽象工厂就是工厂的一种组合和扩展

// 抽象产品接口 - 按钮
interface Button {
    void render();
}

// 具体产品 - Windows风格按钮
class WindowsButton implements Button {
    public void render() {
        System.out.println("Rendering a Windows style button");
    }
}

// 具体产品 - Mac风格按钮
class MacButton implements Button {
    public void render() {
        System.out.println("Rendering a Mac style button");
    }
}

// 抽象产品接口 - 文本框
interface TextBox {
    void display();
}

// 具体产品 - Windows风格文本框
class WindowsTextBox implements TextBox {
    public void display() {
        System.out.println("Displaying a Windows style text box");
    }
}

// 具体产品 - Mac风格文本框
class MacTextBox implements TextBox {
    public void display() {
        System.out.println("Displaying a Mac style text box");
    }
}

// 抽象工厂接口
interface UIFactory {
    Button createButton();
    TextBox createTextBox();
}

// 具体工厂 - Windows UI工厂
class WindowsUIFactory implements UIFactory {
    public Button createButton() {
        return new WindowsButton();
    }

    public TextBox createTextBox() {
        return new WindowsTextBox();
    }
}

// 具体工厂 - Mac UI工厂
class MacUIFactory implements UIFactory {
    public Button createButton() {
        return new MacButton();
    }

    public TextBox createTextBox() {
        return new MacTextBox();
    }
}

// 客户端代码
class Application {
    private Button button;
    private TextBox textBox;

    public Application(UIFactory factory) {
        this.button = factory.createButton();
        this.textBox = factory.createTextBox();
    }

    public void renderUI() {
        button.render();
        textBox.display();
    }
}

// 测试类
class AbstractFactoryDemo {
    public static void main(String[] args) {
        // 创建Windows风格UI
        UIFactory windowsFactory = new WindowsUIFactory();
        Application windowsApp = new Application(windowsFactory);
        System.out.println("Windows UI:");
        windowsApp.renderUI();

        System.out.println("----------------");

        // 创建Mac风格UI
        UIFactory macFactory = new MacUIFactory();
        Application macApp = new Application(macFactory);
        System.out.println("Mac UI:");
        macApp.renderUI();
    }
}