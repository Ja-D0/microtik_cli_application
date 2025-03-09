package com.microtik.core.base.cli.commandLists

import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.requestModels.AddressPayload
import com.microtik.core.api.responseModels.AddressResponse
import com.microtik.core.base.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandOption
import com.microtik.core.base.cli.annotations.CommandType

/**
 *
 */
@CommandList("address")
class AddressCommandsList : CommandsListImpl() {

    @Command("print", CommandType.COMMAND, "Show the elements")
    fun commandPrint(): String =
        MicrotikApiService.runRequest<ArrayList<AddressResponse>> {
            MicrotikApiService.getInstance().getAddressApi().print().execute()
        }.joinToString("\n") { it.toString() }

    @Command("add", CommandType.COMMAND, "Adds the address")
    fun commandAdd(
        @CommandOption("a", "address", true, "IP address")
        address: String,
        @CommandOption("n", "network", false, "Network")
        network: String? = null,
        @CommandOption("i", "interface", true, "Interface")
        interfaces: String
    ): String =
        MicrotikApiService.runRequest {
            MicrotikApiService.getInstance().getAddressApi().add(AddressPayload(address, interfaces, network)).execute()
        }.toString()

    @Command("edit", CommandType.COMMAND, "Editing the address")
    fun commandEdit(
        @CommandOption("i", "id", true, "ID Rules")
        id: String,
        @CommandOption("a", "address", false, "IP address")
        address: String? = null,
        @CommandOption("n", "network", false, "Network")
        network: String? = null,
        @CommandOption("i", "interface", false, "Interface")
        interfaces: String? = null
    ): String =
        MicrotikApiService.runRequest<AddressResponse> {
            MicrotikApiService.getInstance().getAddressApi().edit(id, AddressPayload(address, interfaces, network))
                .execute()
        }.toString()

    @Command("disable", CommandType.COMMAND, "Turn off the rule")
    fun commandDisable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MicrotikApiService.runRequest<AddressResponse> {
            MicrotikApiService.getInstance().getAddressApi().edit(id, AddressPayload(disabled = true)).execute()
        }.toString()

    @Command("enable", CommandType.COMMAND, "Turn on the rule")
    fun commandEnable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MicrotikApiService.runRequest<AddressResponse> {
            MicrotikApiService.getInstance().getAddressApi().edit(id, AddressPayload(disabled = false)).execute()
        }.toString()

    @Command("remove", CommandType.COMMAND, "Remove the element")
    fun commandRemove(
        @CommandOption("i", "id", true, "Record number")
        id: String
    ): Unit =
        MicrotikApiService.runRequest<Unit> { MicrotikApiService.getInstance().getAddressApi().remove(id).execute() }
}
