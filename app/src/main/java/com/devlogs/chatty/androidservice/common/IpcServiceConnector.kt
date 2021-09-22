package com.devlogs.chatty.androidservice.common

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

/**
 * Base on the post of Vasiliy Zukanov
 * visit: www.techyourchance.com/reliable-connection-aidl-ipc-service-android for more detail
 * @author Devlogs
 * */

class IpcServiceConnector {
    companion object {
        /** Initialize state */
        const val STATE_NONE = 0

        /** IPC service is connecting
         *  When user call {@link android.Context#bindService()}
         *  android.content.ServiceConnection.onServiceConnected(componentName, IBinder)
         *  wasn't called yet
         *  */
        const val STATE_BOUND_WAITING_FOR_CONNECT = 1

        /** IPC service is bound
         * When the {@link ServiceConnection#onServiceConnected(ComponentName, IBinder) } was called
         */
        const val STATE_BOUND_CONNECTED = 2

        /** IPC service is unbound
         *  When user call {@link android.Context.#unBindService()}
         * */
        const val STATE_UNBOUND = 3

        /**
         * IPC service is bound and had already been connected, but
         * {@link ServiceConnection#onServiceDisconnected(ComponentName)} was called
         */
        const val STATE_BOUND_DISCONNECTED = 4

        /**
         * Binding of IPC service failed ({@link Context#bindService(Intent, ServiceConnection, int)}
         * returned false)
         */
        const val STATE_BINDING_FAILED = 5
    }

    private val LOCK = Object()
    private var currentConnectionState: Int = STATE_NONE
    private var serviceConnection: ServiceConnectionDecorator? = null
    private val context: Context
    private val name: String

    constructor(context: Context, name: String) {
        this.context = context
        this.name = name
    }

    private fun setStateAndReleaseBlockedThreads(state: Int) {
        synchronized(LOCK) {
            Log.d(
                name, "setStateAndReleaseBlockedThreads " +
                        "currentThread: ${getStateName(currentConnectionState)} " +
                        "waiting for: ${getStateName(state)}"
            )
            if (currentConnectionState != state) {
                currentConnectionState = state
                Log.d(name, "notifying all waiting (blocked) threads about state change")
                LOCK.notifyAll()
            }
        }
    }


    /**
     * Get connection state of this connector. Will return either of:<br></br>
     * [.STATE_NONE]<br></br>
     * [.STATE_BOUND_WAITING_FOR_CONNECT]<br></br>
     * [.STATE_BOUND_CONNECTED]<br></br>
     * [.STATE_BOUND_DISCONNECTED]<br></br>
     * [.STATE_UNBOUND]<br></br>
     * [.STATE_BINDING_FAILED]<br></br>
     *
     * @return connector's state
     */
    fun getState(): Int {
        synchronized(LOCK) { return currentConnectionState }
    }


    fun getStateName(connectionState: Int): String? {
        return when (connectionState) {
            STATE_NONE -> "STATE_NONE"
            STATE_BOUND_WAITING_FOR_CONNECT -> "STATE_BOUND_WAITING_FOR_CONNECT"
            STATE_BOUND_CONNECTED -> "STATE_BOUND_CONNECTED"
            STATE_BOUND_DISCONNECTED -> "STATE_BOUND_DISCONNECTED"
            STATE_UNBOUND -> "STATE_UNBOUND"
            STATE_BINDING_FAILED -> "STATE_BINDING_FAILED"
            else -> throw IllegalArgumentException("invalid state: $connectionState")
        }
    }

    fun bindService(intent: Intent, connection: ServiceConnection, flag: Int) : Boolean =
        synchronized(LOCK) {
            Log.d(name, "bindService()")
            if (isServiceBound()) {
                Log.d(name, "bindService(): service already been bound")
            }
            val cachedConnection = ServiceConnectionDecorator(connection)
            val isServiceBound = context.bindService(intent, connection, flag)

            if (isServiceBound) {
                currentConnectionState = STATE_BOUND_WAITING_FOR_CONNECT
                serviceConnection = cachedConnection
                Log.d(name, "bindService(): Success (STATE: ${getStateName(currentConnectionState)})")
            } else {
                currentConnectionState = STATE_BINDING_FAILED
                Log.d(name, "bindService(): Failed (STATE: ${getStateName(currentConnectionState)})")
            }

            isServiceBound
        }

    fun unBindService () {
        Log.d(name, "unBindService()")
        synchronized(LOCK) {
            if (isServiceBound() && serviceConnection != null) {
                context.unbindService(serviceConnection!!)
                serviceConnection = null
                Log.d(name, "unBindService() service unBind success")
                setStateAndReleaseBlockedThreads(STATE_BOUND_DISCONNECTED)
            } else {
                Log.d(name, "No IPC service to unBind")
            }
        }
    }

    /**
     * Call to this method will block the calling thread until this connector transitions to the
     * specified state, or until the specified amount of time passes If the connector is already in
     * the requested state then this method returns immediately.<br><br>
     *
     * NOTE: {@link ServiceConnection#onServiceConnected(ComponentName, IBinder)} and
     * {@link ServiceConnection#onServiceDisconnected(ComponentName)} will be invoked BEFORE
     * threads which are waiting due to calls to this method are unblocked. This allows you to
     * use ServiceConnection's callbacks in order perform the required setup before the execution
     * of the blocked threads continues.<br><br>
     *
     * This method MUST NOT be called from UI thread.
     * @param targetState IpcServiceConnector's state in which the calling thread should be
     *                    unblocked. Should be either of:<br>
     *                    {@link #STATE_NONE}<br>
     *                    {@link #STATE_BOUND_WAITING_FOR_CONNECTION}<br>
     *                    {@link #STATE_BOUND_CONNECTED}<br>
     *                    {@link #STATE_BOUND_DISCONNECTED}<br>
     *                    {@link #STATE_UNBOUND}<br>
     *                    {@link #STATE_BINDING_FAILED}<br>
     *
     * @param blockingTimeout the period of time (in milliseconds) after which the calling thread will
     *                        be unblocked (regardless of the state of this IpcServiceConnector)
     * @return true if target state was reached; false otherwise
     */
    fun waitingForState (targetState: Int, timeout: Long): Boolean {
        synchronized(LOCK) {
            if (currentConnectionState == targetState) return true

            Log.d(name, "waitingForState: currentState: ${getStateName(currentConnectionState)}" +
                    "waiting for ${getStateName(targetState)} with timeout: $timeout")

            val blockingOnSetTime = System.currentTimeMillis()

            while (
                currentConnectionState != targetState
                && System.currentTimeMillis() < blockingOnSetTime + timeout
                && !Thread.currentThread().isInterrupted) {
                Log.d(name, "blocking execution of thread: " + Thread.currentThread().name)

                try {
                    LOCK.wait()
                } catch (ex: InterruptedException) {
                    // restore interrupted status (was cleared by wait())
                    Thread.currentThread().interrupt()
                }
            }

            val isReached = currentConnectionState == targetState
            Log.d(
                name, "thread unblocked: " + Thread.currentThread().name +
                        "; current state: " + getStateName(currentConnectionState) + "; target state: " + getStateName(
                    targetState
                )
            )
            return isReached
        }
    }

    fun isServiceBound(): Boolean = when(currentConnectionState) {
            STATE_BOUND_WAITING_FOR_CONNECT,STATE_BOUND_CONNECTED, STATE_BOUND_DISCONNECTED  -> true
            else -> false
    }

    private inner class ServiceConnectionDecorator(private val mDecorated: ServiceConnection) :
        ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            /*
            System will invoke this method after a connection to the already bound service will be
            established.
             */
            mDecorated.onServiceConnected(name, binder)
            setStateAndReleaseBlockedThreads(STATE_BOUND_CONNECTED)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            /*
            System will invoke this method in response to abnormal errors related to connected
            service (IPC service crashed or was killed by OS, etc). After this method is called,
            IPC service is NOT necessarily unbound - the system might restore the service later,
            and invoke onServiceConnected() in order to let us know that the service is connected
            again.
             */
            mDecorated.onServiceDisconnected(name)
            setStateAndReleaseBlockedThreads(STATE_BOUND_DISCONNECTED)
        }
    }
}
