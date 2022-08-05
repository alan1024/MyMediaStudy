package com.alan.mymediastudy.constant

import android.media.AudioFormat
import android.os.Environment
import java.io.File

object Constants {
    //图片地址
    var PATH = Environment.getExternalStorageDirectory().path + File.separator + "1.jpg"

    //pcm地址
    var PATH_PCM = Environment.getExternalStorageDirectory().path + File.separator + "test.pcm"

    //wav地址
    var PATH_WAV = Environment.getExternalStorageDirectory().path + File.separator + "1.jpg"


    ///////////////////////////////////////////////////////////////////////////
    // AudioRecord
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    const val SAMPLE_RATE_INHZ = 44100

    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    val CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_MONO

    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    val AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT


}