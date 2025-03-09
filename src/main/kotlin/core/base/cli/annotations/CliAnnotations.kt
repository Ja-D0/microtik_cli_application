package com.microtik.core.base.cli.annotations

/**
 * Общий набор перечислений, позволяющий определить для чего предназначена команда
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
enum class CommandType {
    /**
     * Команда предназначена для перехода в каталог
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    PATH,

    /**
     * Команда предназначена для выполнения произвольных действий
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    COMMAND
}
