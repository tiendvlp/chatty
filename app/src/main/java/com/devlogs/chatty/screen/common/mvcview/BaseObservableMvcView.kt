package com.devlogs.chatty.screen.common.mvcview

import com.devlogs.chatty.common.base.BaseObservable

abstract class BaseObservableMvcView <LISTENER> : BaseObservable<LISTENER>(), ObservableMvcView<LISTENER> {

}
