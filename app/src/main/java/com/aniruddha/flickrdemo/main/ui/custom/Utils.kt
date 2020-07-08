package com.aniruddha.flickrdemo.main.ui.custom

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.whileSelect
import java.util.concurrent.TimeUnit

/**
 * Extension functions to help with implementing a debounce control over the flow of text
 * from EditText's text change events.
 * */
fun EditText.onTextChanged(): ReceiveChannel<String> =
        Channel<String>(capacity = Channel.UNLIMITED).also { channel ->
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    editable?.toString().orEmpty().let(channel::offer)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })
        }

fun <T> ReceiveChannel<T>.debounce(coroutineScope: CoroutineScope, time: Long, unit: TimeUnit = TimeUnit.MILLISECONDS): ReceiveChannel<T> =
        Channel<T>(capacity = Channel.CONFLATED).also { channel ->
            coroutineScope.launch {
                var value = receive()
                whileSelect {
                    onTimeout(time) {
                        channel.offer(value)
                        value = receive()
                        true
                    }
                    onReceive {
                        value = it
                        true
                    }
                }
            }
        }