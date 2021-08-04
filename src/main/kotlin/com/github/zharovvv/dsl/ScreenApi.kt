package com.github.zharovvv.dsl

class Header {
    var title: String? = null
    var text: String? = null
    var properties: MutableMap<String, Any>? = null

    override fun toString(): String {
        return "Header(title=$title, text=$text, properties=$properties)"
    }
}

class Widget {
    var title: String? = null
    var text: String? = null
    var properties: MutableMap<String, Any>? = null

    override fun toString(): String {
        return "Widget(title=$title, text=$text, properties=$properties)"
    }
}

class Footer {
    var title: String? = null
    var text: String? = null
    var properties: MutableMap<String, Any>? = null

    override fun toString(): String {
        return "Footer(title=$title, text=$text, properties=$properties)"
    }
}

class Screen {
    var header: Header? = null
    var widgets: MutableList<Widget>? = null
    var footer: Footer? = null

    override fun toString(): String {
        return "Screen(header=$header, widgets=$widgets, footer=$footer)"
    }
}

inline fun Screen(initBlock: Screen.() -> Unit): Screen {
    val screen = Screen()
    initBlock(screen)
    return screen
}

inline fun Header(initBlock: Header.() -> Unit): Header {
    val header = Header()
    initBlock(header)
    return header
}

inline fun Widget(initBlock: Widget.() -> Unit): Widget {
    val widget = Widget()
    initBlock(widget)
    return widget
}

inline fun Footer(initBlock: Footer.() -> Unit): Footer {
    val footer = Footer()
    initBlock(footer)
    return footer
}

inline fun Screen.header(initBlock: Header.() -> Unit) {
    this.header = Header(initBlock)
}

inline fun Screen.widgets(initBlock: MutableList<Widget>.() -> Unit) {
    val widgetList = mutableListOf<Widget>()
    initBlock(widgetList)
    widgets = widgetList
}

inline fun MutableList<Widget>.widget(initBlock: Widget.() -> Unit) {
    add(Widget(initBlock))
}

inline fun MutableList<Widget>.widget(additionCondition: Boolean, initBlock: Widget.() -> Unit) {
    if (additionCondition) {
        add(Widget(initBlock))
    }
}

inline fun Screen.footer(initBlock: Footer.() -> Unit) {
    footer = Footer(initBlock)
}



