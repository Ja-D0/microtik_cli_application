package com.microtik.core.base

import com.microtik.core.base.cli.interfaces.Request
import com.microtik.core.base.cli.interfaces.Response

/**
 *
 */
class Application(configFilePath: String? = null) : ApplicationImpl(configFilePath) {

    /**
     * Обрабатывает запрос команды пользователя
     *
     * @return экземпляр [Response]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun handleCommandRequest(request: Request): Response {
        val (command, params) = request.resolve()

        return runCommand(command, params)
    }
}
