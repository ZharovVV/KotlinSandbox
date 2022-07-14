package other.directory.utils

fun String.colorize(color: ConsoleColor): String = color.colorString + this + RESET_CONSTANT

enum class ConsoleColor(val colorString: String) {
    BLACK(BLACK_CONSTANT),
    RED(RED_CONSTANT),
    GREEN(GREEN_CONSTANT),
    YELLOW(YELLOW_CONSTANT),
    BLUE(BLUE_CONSTANT),
    PURPLE(PURPLE_CONSTANT),
    CYAN(CYAN_CONSTANT),
    WHITE(WHITE_CONSTANT)
}

// Reset
const val RESET_CONSTANT = "\u001b[0m" // Text Reset


// Regular Colors
const val BLACK_CONSTANT = "\u001b[0;30m" // BLACK

const val RED_CONSTANT = "\u001b[0;31m" // RED

const val GREEN_CONSTANT = "\u001b[0;32m" // GREEN

const val YELLOW_CONSTANT = "\u001b[0;33m" // YELLOW

const val BLUE_CONSTANT = "\u001b[0;34m" // BLUE

const val PURPLE_CONSTANT = "\u001b[0;35m" // PURPLE

const val CYAN_CONSTANT = "\u001b[0;36m" // CYAN

const val WHITE_CONSTANT = "\u001b[0;37m" // WHITE


// Bold
const val BLACK_BOLD_CONSTANT = "\u001b[1;30m" // BLACK

const val RED_BOLD_CONSTANT = "\u001b[1;31m" // RED

const val GREEN_BOLD_CONSTANT = "\u001b[1;32m" // GREEN

const val YELLOW_BOLD_CONSTANT = "\u001b[1;33m" // YELLOW

const val BLUE_BOLD_CONSTANT = "\u001b[1;34m" // BLUE

const val PURPLE_BOLD_CONSTANT = "\u001b[1;35m" // PURPLE

const val CYAN_BOLD_CONSTANT = "\u001b[1;36m" // CYAN

const val WHITE_BOLD_CONSTANT = "\u001b[1;37m" // WHITE


// Underline
const val BLACK_UNDERLINED_CONSTANT = "\u001b[4;30m" // BLACK

const val RED_UNDERLINED_CONSTANT = "\u001b[4;31m" // RED

const val GREEN_UNDERLINED_CONSTANT = "\u001b[4;32m" // GREEN

const val YELLOW_UNDERLINED_CONSTANT = "\u001b[4;33m" // YELLOW

const val BLUE_UNDERLINED_CONSTANT = "\u001b[4;34m" // BLUE

const val PURPLE_UNDERLINED_CONSTANT = "\u001b[4;35m" // PURPLE

const val CYAN_UNDERLINED_CONSTANT = "\u001b[4;36m" // CYAN

const val WHITE_UNDERLINED_CONSTANT = "\u001b[4;37m" // WHITE


// Background
const val BLACK_BACKGROUND_CONSTANT = "\u001b[40m" // BLACK

const val RED_BACKGROUND_CONSTANT = "\u001b[41m" // RED

const val GREEN_BACKGROUND_CONSTANT = "\u001b[42m" // GREEN

const val YELLOW_BACKGROUND_CONSTANT = "\u001b[43m" // YELLOW

const val BLUE_BACKGROUND_CONSTANT = "\u001b[44m" // BLUE

const val PURPLE_BACKGROUND_CONSTANT = "\u001b[45m" // PURPLE

const val CYAN_BACKGROUND_CONSTANT = "\u001b[46m" // CYAN

const val WHITE_BACKGROUND_CONSTANT = "\u001b[47m" // WHITE


// High Intensity
const val BLACK_BRIGHT_CONSTANT = "\u001b[0;90m" // BLACK

const val RED_BRIGHT_CONSTANT = "\u001b[0;91m" // RED

const val GREEN_BRIGHT_CONSTANT = "\u001b[0;92m" // GREEN

const val YELLOW_BRIGHT_CONSTANT = "\u001b[0;93m" // YELLOW

const val BLUE_BRIGHT_CONSTANT = "\u001b[0;94m" // BLUE

const val PURPLE_BRIGHT_CONSTANT = "\u001b[0;95m" // PURPLE

const val CYAN_BRIGHT_CONSTANT = "\u001b[0;96m" // CYAN

const val WHITE_BRIGHT_CONSTANT = "\u001b[0;97m" // WHITE


// Bold High Intensity
const val BLACK_BOLD_BRIGHT_CONSTANT = "\u001b[1;90m" // BLACK

const val RED_BOLD_BRIGHT_CONSTANT = "\u001b[1;91m" // RED

const val GREEN_BOLD_BRIGHT_CONSTANT = "\u001b[1;92m" // GREEN

const val YELLOW_BOLD_BRIGHT_CONSTANT = "\u001b[1;93m" // YELLOW

const val BLUE_BOLD_BRIGHT_CONSTANT = "\u001b[1;94m" // BLUE

const val PURPLE_BOLD_BRIGHT_CONSTANT = "\u001b[1;95m" // PURPLE

const val CYAN_BOLD_BRIGHT_CONSTANT = "\u001b[1;96m" // CYAN

const val WHITE_BOLD_BRIGHT_CONSTANT = "\u001b[1;97m" // WHITE


// High Intensity backgrounds
const val BLACK_BACKGROUND_BRIGHT_CONSTANT = "\u001b[0;100m" // BLACK

const val RED_BACKGROUND_BRIGHT_CONSTANT = "\u001b[0;101m" // RED

const val GREEN_BACKGROUND_BRIGHT_CONSTANT = "\u001b[0;102m" // GREEN

const val YELLOW_BACKGROUND_BRIGHT_CONSTANT = "\u001b[0;103m" // YELLOW

const val BLUE_BACKGROUND_BRIGHT_CONSTANT = "\u001b[0;104m" // BLUE

const val PURPLE_BACKGROUND_BRIGHT_CONSTANT = "\u001b[0;105m" // PURPLE

const val CYAN_BACKGROUND_BRIGHT_CONSTANT = "\u001b[0;106m" // CYAN

const val WHITE_BACKGROUND_BRIGHT_CONSTANT = "\u001b[0;107m" // WHITE
