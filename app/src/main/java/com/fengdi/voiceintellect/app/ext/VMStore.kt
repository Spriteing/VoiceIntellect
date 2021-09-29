package com.fengdi.voiceintellect.app.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.fengdi.voiceintellect.app.annotation.VMScope
import me.hgj.jetpackmvvm.util.LogUtils

private val vMStores = HashMap<String, VMStore>()
//作用域对应的商店

fun Fragment.injectFragmentVM() {
    this::class.java.declaredFields.forEach { field ->
        field.getAnnotation(VMScope::class.java)?.let { scope ->
            //获取作用域
            val element = scope.scopeName
            val store: VMStore

            LogUtils.debugInfo("element:$element")

            if (vMStores.keys.contains(element)) {
                //如果有，从map中获取
                store = vMStores[element]!!
            } else {
                //如果没有就创建
                store = VMStore()
                vMStores[element] = store
            }

            LogUtils.debugInfo("store:$store")

            store.bindHost(this)
            val clazz = field.type as Class<ViewModel>
            val vm = ViewModelProvider(store, VMFactory()).get(clazz)

            LogUtils.debugInfo("vm:$vm")

            //给view model赋值
            field.set(this, vm)
        }

    }
}


fun AppCompatActivity.injectActivityVM() {
    this::class.java.declaredFields.forEach { field ->
        field.getAnnotation(VMScope::class.java)?.let { scope ->
            //获取作用域
            val element = scope.scopeName
            val store: VMStore

            LogUtils.debugInfo("element:$element")

            if (vMStores.keys.contains(element)) {
                //如果有，从map中获取
                store = vMStores[element]!!
            } else {
                //如果没有就创建
                store = VMStore()
                vMStores[element] = store
            }

            LogUtils.debugInfo("store:$store")

            store.bindHost(this)
            val clazz = field.type as Class<ViewModel>
            val vm = ViewModelProvider(store, VMFactory()).get(clazz)

            LogUtils.debugInfo("vm:$vm")

            //给view model赋值
            field.set(this, vm)
        }

    }
}

class VMFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.newInstance()
    }
}

class VMStore : ViewModelStoreOwner {
    private val bindTargets = ArrayList<LifecycleOwner>()
    private var vmStore: ViewModelStore? = null

    //绑定
    fun bindHost(host: LifecycleOwner) {
        if (!bindTargets.contains(host)) {
            bindTargets.add(host)
        }
        //绑定生命周期
        host.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    bindTargets.remove(host)
                }
                if (bindTargets.isEmpty()) {
                    //如果当前商店没有关联对象，则释放内存
                    vMStores.entries.find { it.value == this@VMStore }?.let {
                        vmStore?.clear()
                        vMStores.remove(it.key)
                        LogUtils.debugInfo("clear vMStores:${vMStores.size}")
                    }

                }
            }

        })

    }

    override fun getViewModelStore(): ViewModelStore {
        if (vmStore == null)
            vmStore = ViewModelStore()
        return vmStore!!
    }

}