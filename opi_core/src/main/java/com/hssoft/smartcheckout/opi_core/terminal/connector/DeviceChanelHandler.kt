package com.hssoft.smartcheckout.opi_core.terminal.connector

import com.hssoft.smartcheckout.opi_core.terminal.entity.OperationResult
import com.hssoft.smartcheckout.opi_core.terminal.entity.request.DeviceRequest
import com.hssoft.smartcheckout.opi_core.terminal.entity.response.DeviceResponse
import com.hssoft.smartcheckout.opi_core.terminal.getLocalIpAddress
import java.net.*

class DeviceChanelHandler(private val port: Int, private val timeout: Long) {

    private var serverSocket: ServerSocket? = null

    fun runServer(onReceiveMessageFromDevice: (request: String) -> Unit,
                  onSendMessageToDevice: (response: String) -> Unit,
                  onCommunicationWithDeviceFinished: () -> Unit) {
        val server = try {
            createSocket()
        } catch (e: Exception) {
            onCommunicationWithDeviceFinished()
            return
        }
        val beginTicks: Long = System.currentTimeMillis()
        var operationTime = 0L
        while (operationTime < timeout && !server.isClosed) {
            operationTime = System.currentTimeMillis() - beginTicks
            val client = server.accept()
            val clientHandler = PosChanelHandler(client)
            val deviceRequest = clientHandler.read().orEmpty()
            onReceiveMessageFromDevice(deviceRequest)
            val callback = deviceMessageResolver(deviceRequest)
            clientHandler.write(callback.serializeToXMLString())
            onSendMessageToDevice(callback.serializeToXMLString())
            clientHandler.shutdown()
            if(callback.output.outDeviceTarget == "PrinterReceipt" && callback.result == OperationResult.Success) shutdown()
        }
        shutdown()
    }

    fun shutdown() {
        if(serverSocket?.isClosed == false){
            serverSocket?.close()
            serverSocket = null
        }
    }

    //return confirmation message
    private fun deviceMessageResolver(messageFromDevice: String): DeviceResponse {
        val deviceRequest = DeviceRequest(messageFromDevice)
        return DeviceResponse(
            requestID = deviceRequest.requestID,
            workstationID = deviceRequest.workstationID,
            requestType = deviceRequest.requestType,
            result = OperationResult.Success,
            output = DeviceResponse.Output(
                outDeviceTarget = deviceRequest.output?.outDeviceTarget.orEmpty(),
                outResult = OperationResult.Success.name
            )
        )
    }

    private fun createSocket(): ServerSocket {
        shutdown()
        serverSocket = ServerSocket(port, 0, InetAddress.getByName(getLocalIpAddress()))
        return serverSocket!!
    }
}