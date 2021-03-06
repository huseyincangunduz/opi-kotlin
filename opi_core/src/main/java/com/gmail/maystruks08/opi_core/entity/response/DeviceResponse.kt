package com.gmail.maystruks08.opi_core.entity.response

import com.gmail.maystruks08.opi_core.entity.BaseXMLEntity
import com.gmail.maystruks08.opi_core.entity.OperationResult
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import java.io.Reader
import java.io.StringReader

@Root(name = "DeviceResponse")
data class DeviceResponse(

    @field: Attribute(name = "RequestType")
    var requestType: String = "",

    @field: Attribute(name = "ApplicationSender")
    var applicationSender: String = "",

    @field:Attribute(name = "OverallResult", required = true)
    var result: OperationResult = OperationResult.Failure,

    @field: Attribute(name = "xmlns", required = true)
    var xmlns: String = "http://www.nrf-arts.org/IXRetail/namespace",

    @field: Attribute(name = "xmlns:xsi", required = true)
    var xsi: String = "http://www.w3.org/2001/XMLSchema-instance",

    @field: Element(name = "Output", required = false)
    var output: Output? = null,

    @field: Element(name = "Input", required = false)
    var input: Input? = null,

    @field: Element(name = "EventResult", required = false)
    var eventResult: EventResult? = null,

    @field: Attribute(name = "WorkstationID", required = false)
    var workstationID: String? = null,

    @field: Attribute(name = "TerminalID", required = false)
    var terminalID: String? = null,

    @field: Attribute(name = "RequestID", required = false)
    var requestID: String? = null,

    @field: Attribute(name = "SequenceID", required = false)
    var sequenceID: String? = null,

    @field: Attribute(name = "ReferenceRequestID", required = false)
    var referenceRequestID: String? = null

) : BaseXMLEntity() {

    constructor(xmlString: String) : this() {
        deserializeFromXMLString(xmlString)
    }

    constructor(operationResult: OperationResult) : this() {
        this.result = operationResult
    }

    override fun deserializeFromXMLString(xmlString: String) {
        val reader: Reader = StringReader(xmlString)
        val format = Format("<?xml version=\"1.0\" encoding= \"ISO-8859-1\" ?>")
        val serializer = Persister(format)
        serializer.read(this::class.java, reader, false)?.also {
            this.output = it.output
            this.input = it.input
            this.eventResult = it.eventResult
            this.requestType = it.requestType
            this.applicationSender = it.applicationSender
            this.workstationID = it.workstationID
            this.terminalID = it.terminalID
            this.requestID = it.requestID
            this.sequenceID = it.sequenceID
            this.referenceRequestID = it.referenceRequestID
            this.result = it.result
        }
    }

    @Root(name = "Output")
    data class Output(

        @field: Attribute(name = "OutDeviceTarget")
        var outDeviceTarget: String,

        @field: Attribute(name = "OutResult")
        var outResult: String
    )

    @Root(name = "InputValue")
    data class InputValue(

        @field: Element(name = "InBoolean")
        val inBoolean: Boolean
    )

    @Root(name = "Input")
    data class Input(

        @field: Element(name = "InputValue")
        var inputValue: InputValue? = null,

        @field: Attribute(name = "InDeviceTarget")
        var inDeviceTarget: String,

        @field: Attribute(name = "InResult")
        var inResult: String,
    )


    @Root(name = "EventResult")
    data class EventResult(

        @field: Attribute(name = "dispenser", required = false)
        var dispenser: String? = null,

        @field: Attribute(name = "productCode", required = false)
        var productCode: String? = null,

        @field: Attribute(name = "modifiedRequest", required = false)
        var modifiedRequest: String? = null
    )

}
