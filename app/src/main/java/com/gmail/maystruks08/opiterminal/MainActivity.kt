package com.gmail.maystruks08.opiterminal

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gmail.maystruks08.opi_core.Terminal
import com.gmail.maystruks08.opi_core.connector.OPILogger
import com.gmail.maystruks08.opi_core.entity.Payment
import com.gmail.maystruks08.opi_core.entity.PaymentType
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.util.*


private const val TERMINAL_IP = "192.168.0.125"
private const val PORT_SEND = 20002
private const val PORT_RECEIVE = 20007

//private const val TERMINAL_IP = "192.168.0.200"
//private const val PORT_SEND = 5577
//private const val PORT_RECEIVE = 5578

private const val SOCKET_CONNECT_TIMEOUT = 3000
private const val SOCKET_READ_WRITE_TIMEOUT = 60 * 1000 * 2

private const val WORK_STATION_ID = "Elo C1242435235"
private const val APPLICATION_SENDER = "CashRegister"
private const val CURRENCY = "EUR"

class MainActivity : AppCompatActivity() {

    private var lastTransactionId: Long = -1L


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handler = Handler {
            val messageData = it.data.getString("0") ?: ""
            textView.text = textView.text.toString() + "\n" + "\n" + messageData
            scrollView.fullScroll(View.FOCUS_DOWN)
            return@Handler true
        }

        val terminal = Terminal.Builder()
            .applicationSender("SmartCheckout")
            .connectTimeout(SOCKET_CONNECT_TIMEOUT)
            .readWriteTimeout(SOCKET_READ_WRITE_TIMEOUT)
            .ipAddress(TERMINAL_IP)
            .inputPort(PORT_SEND)
            .outputPort(PORT_RECEIVE)
            .applicationSender(APPLICATION_SENDER)
            .workstationID(WORK_STATION_ID)
            .logger(OPILoggerImpl(handler))
            .build()

        btnLogin.setOnClickListener {
            Thread {
                terminal.login()
                this.runOnUiThread {
                    Toast.makeText(this, "Login finished", Toast.LENGTH_LONG).show()
                }
            }.start()
        }

        btnInit.setOnClickListener {
            Thread {
                terminal.initialisation()
                this.runOnUiThread {
                    Toast.makeText(this, "Initialisation finished", Toast.LENGTH_LONG).show()
                }
            }.start()
        }

        btnStatus.setOnClickListener {
            Thread {
                terminal.status()
                this.runOnUiThread {
                    Toast.makeText(this, "Status finished", Toast.LENGTH_LONG).show()
                }
            }.start()
        }

        btnDiagnostic.setOnClickListener {
            Thread {
                terminal.diagnostic()
                this.runOnUiThread {
                    Toast.makeText(this, "Diagnostic finished", Toast.LENGTH_LONG).show()
                }
            }.start()
        }

        btnTransaction.setOnClickListener {
            lastTransactionId = Random().nextLong()
            Thread {
                val transactionData = Payment.Builder()
                    .total(BigDecimal("5.00"))
                    .currency(CURRENCY)
                    .transactionId(lastTransactionId)
                    .type(PaymentType.SALE)
                    .lastReceiptNumber(5)
                    .build()

                terminal.transaction(transactionData) { status ->
                    Log.d("TERMINAL", "TERMINAL DEVICE MESSAGE: $status")
                }
                this.runOnUiThread {
                    Toast.makeText(this, "Transaction finished", Toast.LENGTH_LONG).show()
                }
            }.start()
        }

        btnCancelTransaction.setOnClickListener {
            Thread {
                terminal.abortRequest("0")
                this.runOnUiThread {
                    Toast.makeText(this, "Cancel transaction finished", Toast.LENGTH_LONG).show()
                }
            }.start()
        }

        btnCloseDay.setOnClickListener {
            Thread {
                terminal.reconciliationWithClosure("0"){ status ->
                    Log.d("TERMINAL", "TERMINAL DEVICE MESSAGE: $status")
                }
                this.runOnUiThread {
                    Toast.makeText(this, "Reconciliation with closure finished", Toast.LENGTH_LONG).show()
                }
            }.start()
        }

        btnDisconnect.setOnClickListener {
            Thread {
                terminal.logout()
                this.runOnUiThread {
                    Toast.makeText(this, "Logout finished", Toast.LENGTH_LONG).show()
                }
            }.start()
        }
    }


    class OPILoggerImpl(private val handler: Handler) : OPILogger {

        override fun log(message: String) {
            Log.d("TERMINAL", message)
            handler.sendMessage(
                Message().apply {
                    what = 0
                    data.putString("0", message)
                })
        }

        override fun logError(exception: Exception, message: String) {
            Log.d("TERMINAL", message)
            handler.sendMessage(
                Message().apply {
                    what = 0
                    data.putString("0", message + "\n" + exception.localizedMessage)
                })
        }

        override fun removeOutdateLogFiles() {
            TODO("Not yet implemented")
        }
    }
}
